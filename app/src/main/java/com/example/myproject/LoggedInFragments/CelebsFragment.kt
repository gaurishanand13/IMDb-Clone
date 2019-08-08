package com.example.myproject.LoggedInFragments


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

import com.example.myproject.R
import com.example.myproject.RetrofitDataClasses.*
import com.example.myproject.recylerViewAdapter.ScrollEventListenerForStaggeredGrid
import com.example.myproject.recylerViewAdapter.myAdapter
import kotlinx.android.synthetic.main.activity_movies_see_all.*
import kotlinx.android.synthetic.main.activity_tv_shows_see_all.*
import kotlinx.android.synthetic.main.fragment_celebs.*
import kotlinx.android.synthetic.main.fragment_celebs.progressBar
import kotlinx.android.synthetic.main.fragment_celebs.view.*
import kotlinx.android.synthetic.main.fragment_celebs.view.progressBar
import kotlinx.android.synthetic.main.fragment_movies.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import java.lang.StringBuilder

class CelebsFragment : Fragment() {

    var count = 1
    var totalPages = 4
    var pages  = ArrayList<Int>()
    var list  = ArrayList<celebsResult>()

    var adapter :  myAdapter<celebsResult>? = null

    var layoutManager = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
    //var layoutManager = GridLayoutManager(context,2, RecyclerView.VERTICAL,false)

    var call : Call<fomousPersonalitiesDataClass>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_celebs, container, false)
    }

    fun setCall()
    {
        call = RetrofitClient.retrofitService?.getPopularPersonalities(pages)
    }

    fun update_data_in_the_recycler_view(){

        call?.enqueue(object : Callback<fomousPersonalitiesDataClass>{
            override fun onFailure(call: Call<fomousPersonalitiesDataClass>, t: Throwable) {
                Toast.makeText(context,t.message, Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call<fomousPersonalitiesDataClass>, response: Response<fomousPersonalitiesDataClass>) {
                if(response.isSuccessful)
                {
                    val smallList = response.body()
                    totalPages = smallList!!.total_pages
                    list.addAll(smallList.results)


                    if(count == 1)
                    {
                        adapter = myAdapter<celebsResult>(list,context!!,5)
                        view!!.celebsFragmentRecyclerView.adapter = adapter
                    }
                    else
                    {
                        adapter!!.notifyDataSetChanged()
                    }


                    adapter?.scrollEventListenerForStaggeredGrid = object  : ScrollEventListenerForStaggeredGrid{


                        override fun onClickItems(personId: Int) {

                            val smallcall = RetrofitClient.retrofitService?.getPopularPersonDetailsByID(personId)
                            smallcall?.enqueue(object : Callback<celeb_Details_by_ID>{
                                override fun onFailure(call: Call<celeb_Details_by_ID>, t: Throwable) {

                                    Toast.makeText(context,t.message, Toast.LENGTH_LONG).show()

                                }

                                override fun onResponse(call: Call<celeb_Details_by_ID>, response: Response<celeb_Details_by_ID>) {
                                    if(response.isSuccessful)
                                    {
                                        val imdbID = response.body()!!.imdb_id
                                        val intent:Intent = Intent()
                                        intent.action = Intent.ACTION_VIEW
                                        Log.i("TAG",imdbID)
                                        val uri  = Uri.parse("https://www.imdb.com/name/" + imdbID + "/")
                                        intent.data = uri

                                        try {
                                            startActivity(intent)
                                        }catch (e:Exception) {
                                            Log.i("TAG",e.toString())
                                            Toast.makeText(context,"No Data Found",Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }

                            })
                        }



                        override fun takeMoreData(pageNo: Int) {
                            pages.clear()
                            pages.add(pageNo+1)
                            setCall()
                            progressBar.visibility = View.VISIBLE
                            update_data_in_the_recycler_view()
                        }



                    }

                    count++
                    progressBar.visibility = View.GONE
                }
            }

        })



    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.celebsFragmentRecyclerView.layoutManager = layoutManager

        pages.add(1)

        setCall()
        update_data_in_the_recycler_view()


    }
}
