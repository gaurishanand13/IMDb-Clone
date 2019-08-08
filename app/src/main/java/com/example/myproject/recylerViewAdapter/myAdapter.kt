package com.example.myproject.recylerViewAdapter

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintSet
import androidx.recyclerview.widget.RecyclerView
import com.example.myproject.R
import com.example.myproject.RetrofitDataClasses.*
import com.example.myproject.particularMovieActivity
import com.example.myproject.tvShowsSeeAllActivity
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.famous_personality_staggerview_item.view.*
import kotlinx.android.synthetic.main.item_view_one.view.*
import kotlinx.android.synthetic.main.movies_see_all_item_view.view.*
import java.lang.Exception


interface myAdapterInterface
{

    abstract fun openParticularMoviesActivity(movieId: Int,movieName : String)

    abstract fun openParticularTVShowActivity(TVid : Int,TVShowName : String)

}

interface ScrollEventListenerForStaggeredGrid
{

    abstract fun takeMoreData(pageNo : Int)

    abstract fun onClickItems(personId: Int)

}

interface SeeAllClick
{
    abstract fun SeeAllMoviesActivityClick(movieId: Int,movieName : String)
    abstract fun SeeAllTVShowsActivityClick(TVid : Int,TVShowName : String)
}


class myAdapter<T>(val list: ArrayList<T>, val context: Context, val adapterClassType:Int): RecyclerView.Adapter<CourseViewHolder>()
{
    var myInterface : myAdapterInterface? = null
    var scrollEventListenerForStaggeredGrid : ScrollEventListenerForStaggeredGrid? = null
    var seeAllClickInterface : SeeAllClick? = null




    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {


        val layoutInflater = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        var view : View?= null

        if(adapterClassType == 1 || adapterClassType == 3)
        {
            view = layoutInflater.inflate(R.layout.item_view_one,parent,false)
        }

        if(adapterClassType == 2 || adapterClassType == 4)
        {
            view = layoutInflater.inflate(R.layout.movies_see_all_item_view,parent,false)
        }

        if(adapterClassType == 5)
        {
            view = layoutInflater.inflate(R.layout.famous_personality_staggerview_item,parent,false)
        }
        if(adapterClassType == 6)
        {
            view = layoutInflater.inflate(R.layout.expandable_list_view_child,parent, false)
        }
        if(adapterClassType == 7 || adapterClassType == 8){
            view = layoutInflater.inflate(R.layout.cast_view,parent, false)
        }
        var courseViewHolder = CourseViewHolder(view!!) // -> we can see its syntax. It accepts a type of view in its constructor
        return courseViewHolder

    }



    override fun getItemCount() = list.size



    override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {


        if(adapterClassType == 1 || adapterClassType == 2)
        {
            val myList = list as ArrayList<Results>
            holder.setThingsForMovies(myList.get(position),adapterClassType)

            if(adapterClassType == 1)
            {
                holder.itemView.setOnClickListener {
                    myInterface!!.openParticularMoviesActivity(myList.get(position).id,myList.get(position).title)
                }
            }
            if(adapterClassType == 2)
            {
                holder.itemView.setOnClickListener {
                    seeAllClickInterface!!.SeeAllMoviesActivityClick(myList.get(position).id,myList.get(position).title)
                }
            }
        }


        if(adapterClassType == 3 || adapterClassType == 4)
        {
            val myList = list as ArrayList<TVResult>
            holder.setThingsForTVShows(myList.get(position),adapterClassType)

            if(adapterClassType == 3){
                holder.itemView.setOnClickListener {
                    myInterface!!.openParticularTVShowActivity(myList.get(position).id,myList.get(position).name)
                }
            }
            if(adapterClassType == 4){
                holder.itemView.setOnClickListener {
                    seeAllClickInterface!!.SeeAllTVShowsActivityClick(myList.get(position).id,myList.get(position).name)
                }
            }
        }


        if(adapterClassType == 5 )
        {
            if(list.size>=20 && position == list.size-1)
            {
                scrollEventListenerForStaggeredGrid!!.takeMoreData((list.size)/20)
            }
            val myList = list as ArrayList<celebsResult>
            holder.setThingsForCelebsFragment(myList.get(position),adapterClassType,position)

            holder.itemView.setOnClickListener {
                scrollEventListenerForStaggeredGrid!!.onClickItems(list.get(position).id)
            }
        }

        if(adapterClassType == 6)
        {
            val myList = list as ArrayList<particularMovieReview>
            holder.setThingsInMovieReviews(myList.get(position),adapterClassType)
        }
        if(adapterClassType == 7)
        {
            val myList = list as ArrayList<Cast>
            holder.setmovieCast(myList.get(position),adapterClassType)
        }
        if(adapterClassType == 8)
        {
            val myList = list as ArrayList<tvShowParticularCastDetails>
            holder.setTVShowCast(myList.get(position),adapterClassType)
        }

    }


}