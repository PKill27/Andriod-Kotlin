package com.example.porterkillian.insta

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Camera
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.database.FirebaseDatabase

import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask

import kotlinx.android.synthetic.main.activity_capture.*
import kotlinx.android.synthetic.main.recylerview_text.*
import java.io.ByteArrayOutputStream

import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

class CaptureActivity : Activity() {
    val REQUEST_IMAGE_CAPTURE = 1
    private val CAMERA = 2

    lateinit var storage : StorageReference
    lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_capture)

        database = FirebaseDatabase.getInstance().reference
        storage = FirebaseStorage.getInstance().reference

        capture_button.setOnClickListener(){
            dispatchTakePictureIntent()
        }
        save_button.setOnClickListener(){
            saveToFireBase()
        }
        cancel_button.setOnClickListener(){
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

    }
    private fun dispatchTakePictureIntent() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        // CAMERA is a result code that will tell us we came from the camera activity
        startActivityForResult(cameraIntent, CAMERA)

    }
    lateinit var imgBitmap:Bitmap
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (requestCode == CAMERA && resultCode == RESULT_OK) {
            val imageBitmap = data.data
            val thumbnail = data.extras.get("data") as Bitmap
            your_image.setImageBitmap(thumbnail)
            your_image.tag = saveImage(thumbnail)
        }
    }

    private fun saveToFireBase() {
        // Step 1: Save image to Storage
        var file = Uri.fromFile(File(your_image.tag.toString()))
        val imageRef = storage.child("images/${file.lastPathSegment}")
        val uploadTask = imageRef.putFile(file)

        // Step 2: Get imageURL and save entire Profile to Database
        val urlTask = uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            return@Continuation imageRef.downloadUrl
        }).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    Log.d("download url", downloadUri.toString())
                    date.text = (Calendar.getInstance()).timeInMillis.toString()
                    // Create Profile with this Url and save to Database
                    val profile = Post(caption.text.toString(), date.text.toString(), downloadUri.toString())
                    var key = database.child("posts").push().key!!
                    profile.uuid = key
                    database.child("posts").child(key).setValue(profile)

                    // clear all fields
                    caption.text.clear()
                    //date.text.clear()
                    your_image.setImageDrawable(getDrawable(R.mipmap.ic_launcher))
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                } else {
                    // Handle failures
                }
            }
        }
    }
    private fun saveImage(myBitmap: Bitmap):String {
        val bytes = ByteArrayOutputStream()
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes)
        val wallpaperDirectory = File(
            (Environment.getExternalStorageDirectory()).toString())
        // have the object build the directory structure, if needed.
        if (!wallpaperDirectory.exists())
        {

            wallpaperDirectory.mkdirs()
        }

        try
        {
            val f = File(wallpaperDirectory, ((Calendar.getInstance()
                .timeInMillis).toString() + ".jpg"))
            f.createNewFile()
            val fo = FileOutputStream(f)
            fo.write(bytes.toByteArray())
            MediaScannerConnection.scanFile(this,
                arrayOf(f.path),
                arrayOf("image/jpeg"), null)
            fo.close()
            Log.d("TAG", "File Saved::--->" + f.absolutePath)

            return f.absolutePath
        }
        catch (e1: IOException) {
            e1.printStackTrace()
        }

        return ""
    }

}
