package com.example.myproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ExpandableListView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myproject.RetrofitDataClasses.RetrofitClient
import com.example.myproject.RetrofitDataClasses.movieReviewDataClass
import com.example.myproject.RetrofitDataClasses.particularMovieReview
import com.example.myproject.expandableListViewAdapter.customExpandableAdapter
import com.example.myproject.recylerViewAdapter.myAdapter
import kotlinx.android.synthetic.main.activity_movie_review.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class movieReviewActivity : AppCompatActivity() {

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.home->{
                finish()
                return true
            }
        }
        return false
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_review)

        setSupportActionBar(movieReviewToolBar)
        val x = supportActionBar
        x?.setTitle("Reviews")
        x?.setDisplayHomeAsUpEnabled(true)

        if(intent.getIntExtra("type",0) == 0)
        {
            val call = RetrofitClient.retrofitService?.getMovieReviewByID(intent.getIntExtra("movieID",0))
            call?.enqueue(object  : Callback<movieReviewDataClass> {
                override fun onFailure(call: Call<movieReviewDataClass>, t: Throwable) {
                    Toast.makeText(this@movieReviewActivity,t.message, Toast.LENGTH_LONG).show()
                }

                override fun onResponse(call: Call<movieReviewDataClass>, response: Response<movieReviewDataClass>) {
                    if(response.isSuccessful)
                    {
                        val data = response.body()
                        movieReviewsRecyclerView.layoutManager = LinearLayoutManager(this@movieReviewActivity)
                        movieReviewsRecyclerView.adapter = myAdapter<particularMovieReview>(data!!.results,this@movieReviewActivity,6)
                        if(data!!.results.size==0){
                            noReviewTextView.text = "NO REVIEWS AVAILABLE"
                        }
                    }
                }
            })
        }
        else
        {
            val call = RetrofitClient.retrofitService?.getTVShowReviewsByID(intent.getIntExtra("TVShowID",0))
            call?.enqueue(object  : Callback<movieReviewDataClass> {
                override fun onFailure(call: Call<movieReviewDataClass>, t: Throwable) {
                    Toast.makeText(this@movieReviewActivity,t.message, Toast.LENGTH_LONG).show()
                }

                override fun onResponse(call: Call<movieReviewDataClass>, response: Response<movieReviewDataClass>) {
                    if(response.isSuccessful)
                    {
                        val data = response.body()
                        movieReviewsRecyclerView.layoutManager = LinearLayoutManager(this@movieReviewActivity)
                        movieReviewsRecyclerView.adapter = myAdapter<particularMovieReview>(data!!.results,this@movieReviewActivity,6)
                        if(data!!.results.size==0){
                            noReviewTextView.text = "NO REVIEWS AVAILABLE"
                        }
                    }
                }
            })
        }
    }
}
