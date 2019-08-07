package com.example.porterkillian.localmedia2


import android.content.Intent
import android.net.Uri
import android.os.Build.VERSION_CODES.M
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.widget.*
import com.example.porterkillian.localmedia.MapsActivity
import com.example.porterkillian.localmedia.Radius
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_capture.*
import kotlinx.android.synthetic.main.activity_maps.*
import kotlinx.android.synthetic.main.activity_photo_view.*
import java.io.File
import java.util.*

class PhotoViewActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private var radius = 1
    val posts = ArrayList<String>()
    val pictures = ArrayList<String>()
    val dates = ArrayList<String>()
    val uuids = ArrayList<String>()
    val captions = ArrayList<String>()
    val latitudes = ArrayList<String>()
    val longitudes = ArrayList<String>()
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_view)
        radius = intent.getStringExtra("radius").toInt()
        returnButton.setOnClickListener(){
            val intent = Intent(this, MapsActivity::class.java)
            startActivity(intent)
        }
        viewAdapter = MyAdapter(arrayListOf(), arrayListOf(), arrayListOf(), arrayListOf(),arrayListOf(),arrayListOf(),0.0,0.0,1)
        val database = FirebaseDatabase.getInstance().reference
        val storage = FirebaseStorage.getInstance().reference

        //val latitude = intent.getStringExtra("latitude")
        //val longitude = intent.getStringExtra("longitude")
        val btnShow = findViewById<Button>(R.id.btnShow)
        btnShow?.setOnClickListener { showText() }

        val postListener = object : ValueEventListener {
            // when data changes, add the new Profile to the adapter

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                dataSnapshot.children.forEach {
                    // for each new data piece, extract the Post
                    val post = it.getValue<Post>(Post::class.java)
                    if (post!!.latitude.equals("")) {

                    } else {
                        // check if post is already in the adapter
                        if (!posts.contains(post!!.uuid) && isInRadius(
                                post.latitude.toDouble(),
                                post.longitude.toDouble()
                            )
                        ) {
                            Log.d("name", post.toString())
                            pictures.add(post!!.imageUrl)
                            dates.add(post.date)
                            captions.add(post.caption)
                            uuids.add(post.uuid)
                            latitudes.add(post.latitude)
                            longitudes.add(post.longitude)
                            viewAdapter = MyAdapter(
                                pictures,
                                dates,
                                captions,
                                uuids,
                                latitudes,
                                longitudes,
                                getIntent().getStringExtra("latitude").toDouble(),
                                getIntent().getStringExtra("longitude").toDouble(),
                                1
                            )
                            posts.add(post!!.uuid)
                            recyclerView = findViewById<RecyclerView>(R.id.my_recicler_view).apply {
                                // use this setting to improve performance if you know that changes
                                // in content do not change the layout size of the RecyclerView
                                setHasFixedSize(true)
                                // use a linear layout manager
                                layoutManager = viewManager

                                // specify an viewAdapter (see also next example)
                                adapter = viewAdapter

                            }
                        }
                    }
                }

            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w("TAG", "loadPost:onCancelled", databaseError.toException())
            }
        }
        database.child("posts").addValueEventListener(postListener)
        viewManager = LinearLayoutManager(this)
        recyclerView = findViewById<RecyclerView>(R.id.my_recicler_view).apply {
            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            setHasFixedSize(true)
            // use a linear layout manager
            layoutManager = viewManager

            // specify an viewAdapter (see also next example)
            adapter = viewAdapter

        }

    }

    private fun isInRadius(photoLat: Double, photoLong:Double): Boolean {
        var myLat = getIntent().getStringExtra("latitude").toDouble()
        var myLong = getIntent().getStringExtra("longitude").toDouble()
        var R = 3961
        var dLat = deg2rad(myLat-photoLat);  // deg2rad below
        var dLon = deg2rad(myLong-photoLong);
        var a =
            Math.sin(dLat/2) * Math.sin(dLat/2) +
                    Math.cos(deg2rad(photoLat)) * Math.cos(deg2rad(myLat)) *
                    Math.sin(dLon/2) * Math.sin(dLon/2)
        ;
        var c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        var d = R * c
        Log.i("test",d.toString())
        if(d > radius){

            return false
        }else{
            return true
        }
    }
    fun deg2rad(deg:Double):Double {
        return deg * (Math.PI/180)
    }

    private fun showText() {
        val editText = findViewById<EditText>(R.id.editText)
        try {
            val text = editText.text.toString()
            val rad = text.toInt()
            if(rad < 0){
                Toast.makeText(this@PhotoViewActivity,"not a valid radius for search",Toast.LENGTH_SHORT).show()
            }else{
                val textView = findViewById<TextView>(R.id.radView)
                textView.setText("current radius of "+radius.toString())
                radius = rad
                saveToFireBase()
               }

        }
        catch (e: Exception) {
            Toast.makeText(this@PhotoViewActivity,"enter only numbers",Toast.LENGTH_SHORT).show()
        }
    }
    lateinit var storage : StorageReference
    lateinit var database: DatabaseReference
    private fun saveToFireBase() {
        // Step 1: Save image to Storage
        database = FirebaseDatabase.getInstance().reference
        storage = FirebaseStorage.getInstance().reference
        val availableSalads: List<Post> = mutableListOf(
            Post("","","","","","")
        )
        availableSalads.forEach {
            val key = database.child("posts").push().key
            it.uuid = key.toString()
            database.child("posts").child(key.toString()).setValue(it)
        }

    }
}
