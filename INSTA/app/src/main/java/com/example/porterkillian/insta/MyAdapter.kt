package com.example.porterkillian.insta


import android.content.Intent
import android.support.v4.content.ContextCompat.startActivity
import android.support.v7.widget.RecyclerView
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso


class MyAdapter(private val pictures: ArrayList<String>,private val dates: ArrayList<String>,private val captions: ArrayList<String>,private val uuid: ArrayList<String>) :
    RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    // Each data item is just a string in this case that is shown in a TextView.
    class MyViewHolder(val view: View) : RecyclerView.ViewHolder(view){
        val imgView: ImageView = view.findViewById(R.id.image_view)
        val textView: TextView = view.findViewById(R.id.text_view)
        val dateView: TextView = view.findViewById(R.id.date_view)
        val deleteButton: Button = view.findViewById(R.id.delete_button)



    }
    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): MyAdapter.MyViewHolder {

        val finalView:View = LayoutInflater.from(parent.context)
            .inflate(R.layout.recylerview_text, parent, false)
        return MyViewHolder(finalView)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.dateView.setText(convertDate(dates[pictures.size-position-1],"dd/MM/yyyy hh:mm:ss"))
        holder.textView.setText(captions[pictures.size-position-1])
        holder.deleteButton.setText("X")
        holder.deleteButton.setOnClickListener(){

            var image = FirebaseDatabase.getInstance().getReference()
                .child("posts") .child(uuid[pictures.size-position-1])
            image.removeValue()

            //Remove from Cloud Storage
            val storageReference = FirebaseStorage.getInstance().reference.child("images")
            Log.i("tag",dates[pictures.size-position-1])
            val fileRef = storageReference!!.child(dates[pictures.size-position-1] + ".jpg")
            fileRef.delete()
        }
        val key = pictures[pictures.size-position-1]
        Picasso.get()
            .load(key) // load the image
            .into(holder.imgView)
    }

    fun convertDate(dateInMilliseconds: String, dateFormat: String): String {
        return DateFormat.format(dateFormat, java.lang.Long.parseLong(dateInMilliseconds)).toString()
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = pictures.size
}