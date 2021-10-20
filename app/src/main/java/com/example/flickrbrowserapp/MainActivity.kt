package com.example.flickrbrowserapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL

class MainActivity : AppCompatActivity() {

    lateinit var images: ArrayList<Images>
    lateinit var recyclerView: RecyclerView
    lateinit var rvAdapter: RVadapter

    lateinit var searchFiled: EditText
    lateinit var searchButton: Button

    lateinit var imageView: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        images = arrayListOf()

        imageView = findViewById(R.id.imageView)

        recyclerView = findViewById(R.id.recyclerView)
        rvAdapter= RVadapter(this, images)

        recyclerView.adapter = rvAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        searchFiled = findViewById(R.id.searchFiled)
        searchButton = findViewById(R.id.searchButton)

        searchButton.setOnClickListener {
            if(searchFiled.text.isNotEmpty()){
                requestAPI()
            }else{
                Toast.makeText(this, "Search filed is Empty, Please enter something", Toast.LENGTH_LONG).show()
            }
        }

        imageView.setOnClickListener {
            closeImg()
        }
    }

    fun getPhotos(): String{
        var response = " "
        try{
            response = URL("https://api.flickr.com/services/rest/?method=flickr" +
                    ".photos.search&per_page=10&api_key=cb0cbca5c50568f7e3189b08d8e6a89b&tags=${searchFiled.text}&format=json&nojsoncallback=1")
                .readText(Charsets.UTF_8)
        }catch (e: Exception){
            println("ISSUE: $e")
        }
        return response
    }
    fun requestAPI(){
        CoroutineScope(IO).launch {
            val data = async { getPhotos() }.await()
            if(data.isNotEmpty()){
                println(data)
                showPhotos(data)
            }else{
                Toast.makeText(this@MainActivity, "No Images Found", Toast.LENGTH_LONG).show()
            }
        }
    }
    suspend fun showPhotos(data: String){
        withContext(Main){
            val jsonObj = JSONObject(data)
            val photos = jsonObj.getJSONObject("photos").getJSONArray("photo")
            println("photos")
            println(photos.getJSONObject(0))
            println(photos.getJSONObject(0).getString("farm"))
            for(i in 0 until photos.length()){
                val title = photos.getJSONObject(i).getString("title")
                val farmID = photos.getJSONObject(i).getString("farm")
                val serverID = photos.getJSONObject(i).getString("server")
                val id = photos.getJSONObject(i).getString("id")
                val secret = photos.getJSONObject(i).getString("secret")
                val photoLink = "https://farm$farmID.staticflickr.com/$serverID/${id}_$secret.jpg"
                images.add(Images(title, photoLink))
            }
            rvAdapter.notifyDataSetChanged()
        }
    }
    fun openImg(link: String){
        Glide.with(this).load(link).into(imageView)
        imageView.isVisible = true
        recyclerView.isVisible = false
    }

    private fun closeImg(){
        imageView.isVisible = false
        recyclerView.isVisible = true
    }
}