package com.example.myproject.recylerViewAdapter

import android.view.View
import androidx.constraintlayout.widget.ConstraintSet
import androidx.recyclerview.widget.RecyclerView
import com.example.myproject.R
import com.example.myproject.RetrofitDataClasses.*
import com.example.myproject.particularMovieActivity
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.cast_view.view.*
import kotlinx.android.synthetic.main.expandable_list_view_child.view.*
import kotlinx.android.synthetic.main.famous_personality_staggerview_item.view.*
import kotlinx.android.synthetic.main.item_view_one.view.*
import kotlinx.android.synthetic.main.movies_see_all_item_view.view.*

class CourseViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView)
{
    fun setThingsForMovies(myList : Results, adapterClassType: Int)
    {
        if(adapterClassType == 1)
        {
            with(itemView){
                titleTextView.text = myList.title
                ratingTextView.text = myList.vote_average.toString()
                Picasso.get().load("https://image.tmdb.org/t/p/original"+myList.poster_path).fit().into(posterImageView)
            }
        }
        if (adapterClassType == 2)
        {
            with(itemView){
                moviesSeeAllTitleTextView.text = myList.title
                moviesSeeAllRatingTextView.text = "${myList.vote_average}/10"
                Picasso.get().load("https://image.tmdb.org/t/p/original"+myList.poster_path).fit().into(moviesSeeAllImageView)
            }
        }
    }

    fun setThingsForTVShows(myList: TVResult, adapterClassType: Int)
    {
        if(adapterClassType ==3 ){
            with(itemView){
                titleTextView.text = myList.name
                ratingTextView.text = myList.vote_average.toString()
                Picasso.get().load("https://image.tmdb.org/t/p/original"+myList.poster_path).fit().into(posterImageView)
            }
        }
        if(adapterClassType == 4)
        {
            with(itemView){
                moviesSeeAllTitleTextView.text = myList.name
                moviesSeeAllRatingTextView.text = myList.vote_average.toString()
                Picasso.get().load("https://image.tmdb.org/t/p/original"+myList.poster_path).fit().into(moviesSeeAllImageView)
            }
        }
    }

    fun setThingsForCelebsFragment(list: celebsResult, adapterClassType: Int, position: Int) {

        if(adapterClassType == 5)
        {

            val set = ConstraintSet()
            with(itemView){
                nameTextView.text = list.name


                if(position%3==1)
                {
                    Picasso.get().load("https://image.tmdb.org/t/p/original"+list.profile_path).resize(400,650).into(famousPersonalitiesImageViewItem)
                }
                else
                {
                    Picasso.get().load("https://image.tmdb.org/t/p/original"+list.profile_path).resize(400,325).into(famousPersonalitiesImageViewItem)
                }
            }
        }

    }

    fun setThingsInMovieReviews(review: particularMovieReview, adapterClassType: Int) {

        if(adapterClassType == 6)
        {
            with(itemView){
                reviewOwner.text = review.author
                reviewDetails.text = review.content
            }
        }

    }


    fun setmovieCast(cast : Cast,adapterClassType : Int)
    {
        with(itemView){
            movieStarNameTextView.text = cast.name
            movieCharacterNameTextView.text = cast.character
            if(cast.profile_path == null){
                movieCastImageView.setImageResource(R.drawable.blankprofile)
            }else{

                Picasso.get().load("https://image.tmdb.org/t/p/original"+cast.profile_path).fit().into(movieCastImageView)
            }
        }
    }

    fun setTVShowCast(cast: tvShowParticularCastDetails, adapterClassType: Int) {

        with(itemView){
            movieStarNameTextView.text = cast.name
            movieCharacterNameTextView.text = cast.character
            if(cast.profile_path == null){
                movieCastImageView.setImageResource(R.drawable.blankprofile)
            }else{

                Picasso.get().load("https://image.tmdb.org/t/p/original"+cast.profile_path).fit().into(movieCastImageView)
            }
        }
    }
}