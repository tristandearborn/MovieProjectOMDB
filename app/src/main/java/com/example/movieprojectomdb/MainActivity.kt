package com.example.movieprojectomdb

import MovieAdapter
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.movieprojectomdb.MovieSearchResponse
import com.example.movieprojectomdb.R
import com.example.movieprojectomdb.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

class MainActivity : AppCompatActivity() { //when app launches, access binding and movieadapter class
    private lateinit var binding: ActivityMainBinding
    private lateinit var movieAdapter: MovieAdapter

    override fun onCreate(savedInstanceState: Bundle?) { //creates toolbar and initiates binding
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "IMDb Movie Lookup"
        movieAdapter = MovieAdapter(this)
        binding.movieRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.movieRecyclerView.adapter = movieAdapter
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean { //takes menu xml file for toolbar
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean { //feedback option in toolbar
        when (item.itemId) {
            R.id.feedback -> {
                sendFeedbackEmail()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun sendFeedbackEmail() { //when feedback option is clicked, composes an email to my email with subject Feedback
        val emailIntent = Intent(Intent.ACTION_SENDTO)
        emailIntent.data = Uri.parse("mailto:tristandearborn@gmail.com?subject=Feedback")

        try {
            startActivity(Intent.createChooser(emailIntent, "Send feedback"))
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, "No email app found", Toast.LENGTH_SHORT).show()
        }
    }



    fun search(view: View) { //when search button is clicked, initiates search using retrofit into omdb api
        val searchQuery = binding.movieInput.text.toString()
        val retrofit = Retrofit.Builder()
            .baseUrl("https://www.omdbapi.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val omdbApiService = retrofit.create(OMDbApiService::class.java)
        val apiKey = "b5a0a286"

        movieAdapter.clearMovies()

        val call = omdbApiService.searchMovies(searchQuery, apiKey) //calls omdb api to search for movie from title given
        call.enqueue(object : Callback<MovieSearchResponse> {
            override fun onResponse(call: Call<MovieSearchResponse>, response: Response<MovieSearchResponse>) {
                if (response.isSuccessful) {
                    val movieSearchResponse = response.body()

                    val movies = movieSearchResponse?.Search

                    if (movies != null) {
                        movieAdapter.setMovies(movies)
                    } else {
                        Toast.makeText(this@MainActivity, "No movies found.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@MainActivity, "API error. Please try again later.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<MovieSearchResponse>, t: Throwable) { //if search failed, may be due to internet connection, as that's an issue i encountered
                Toast.makeText(this@MainActivity, "Network error. Please check your internet connection.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    interface OMDbApiService { //creates omdb api interface with search and my api key
        @GET("/")
        fun searchMovies(
            @Query("s") searchQuery: String,
            @Query("apiKey") apiKey: String
        ): Call<MovieSearchResponse>
    }
}
