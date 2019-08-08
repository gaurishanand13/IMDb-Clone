package com.example.myproject

import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.net.Uri
import android.speech.RecognizerIntent
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import com.example.myproject.RetrofitDataClasses.*
import com.example.myproject.RetrofitDataClasses.searchdataClasses.searchData
import com.example.myproject.RetrofitDataClasses.searchdataClasses.searchDataResult
import com.example.myproject.loginDetails.sessionIDDelete
import com.example.myproject.navigationDrawerFiles.userFavouritesActivity
import com.example.myproject.navigationDrawerFiles.userWatchListActivity
import com.example.myproject.statePagerAdapter.fragmentStatePagerAdapter
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import com.miguelcatalan.materialsearchview.MaterialSearchView
import kotlinx.android.synthetic.main.activity_logged_in.*
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.header_for_navigation_drawer.*
import kotlinx.android.synthetic.main.mainfile.*
import mehdi.sakout.dynamicbox.DynamicBox
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception


class LoggedInActivity : AppCompatActivity(),NavigationView.OnNavigationItemSelectedListener {


    val datalist_of_searchView = ArrayList<searchDataResult>()

    override fun onNavigationItemSelected(p0: MenuItem): Boolean {
        when(p0.itemId){
            R.id.favourite->{
                startActivity(Intent(this@LoggedInActivity,userFavouritesActivity::class.java))
            }
            R.id.watchList->{
                startActivity(Intent(this@LoggedInActivity,userWatchListActivity::class.java))
            }
            R.id.helpDesk->{

            }
            R.id.logout->{
                val box = DynamicBox(this,viewPager)  //-> the layout in which you want to show this symbol
                box.showLoadingLayout()
                box.setLoadingMessage("Logging Out")
                val preferences = getSharedPreferences("myPref",Context.MODE_PRIVATE)
                val sessionID = preferences.getString("sessionID","")
                preferences.edit().remove("sessionID").commit()

                //We should not only delete the session id from the local database but also make sure to delete the session from the imdb API as it is a good practice
                val call = RetrofitClient.retrofitService?.deleteSession(sessionID!!)
                call?.enqueue(object : Callback<sessionIDDelete>{
                    override fun onFailure(call: Call<sessionIDDelete>, t: Throwable) {
                        Toast.makeText(this@LoggedInActivity,t.message,Toast.LENGTH_SHORT).show()
                    }

                    override fun onResponse(call: Call<sessionIDDelete>, response: Response<sessionIDDelete>) {
                        if(response.isSuccessful){
                            Toast.makeText(this@LoggedInActivity,"Logout Successful",Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this@LoggedInActivity,MainActivity::class.java)) //-> it will take it to the first activity only
                        }
                    }

                })
            }
        }
        drawer_layout.closeDrawer(GravityCompat.START);
        return true;
    }

    //Since on pressing back we can't go to the back Page as we are already logged in. Therefore nothing will happen if we won't write this code.
    //originally if the user was logged in then he should go to the home Page of his phone if he/she is logged in.
    override fun onBackPressed() {
        if(drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        }
        else{
            finishAffinity()
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search,menu)
        val searchItem = menu?.findItem(R.id.search)
        loggedIn_search_view.setMenuItem(searchItem)
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == MaterialSearchView.REQUEST_VOICE && resultCode== Activity.RESULT_OK) {
            val matches = data!!.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            if(matches!=null && matches.size >0){
                val searchWord = matches.get(0)
                if(!TextUtils.isEmpty(searchWord)){
                    search_view.setQuery(searchWord,false)
                }
            }
            return
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_logged_in)

        val preferences = getSharedPreferences("myPref", Context.MODE_PRIVATE)
        SESSION_ID_DETAILS.sessionID = preferences.getString("sessionID","")

        setSupportActionBar(toolBar)
        val x = supportActionBar
        x?.setIcon(R.drawable.imdblogo)
        // Just adding the above line won't set up the icon in the action Bar. You need to add these 2 lines too.
        x?.setDisplayUseLogoEnabled(true)
        x?.setDisplayShowHomeEnabled(true)
        x?.title = ""


        //setting of the navigation layout
        val toggle = ActionBarDrawerToggle(this, drawer_layout, toolBar,R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        nav_view.setNavigationItemSelectedListener(this)


        val header = nav_view.getHeaderView(0)
        val profileUsernameTextView = header.findViewById<TextView>(R.id.profileUsernameTextView)
        profileUsernameTextView.text = intent.getStringExtra("userName")


        //Now we will set up the tabLayout and viewPager
        val adapter = fragmentStatePagerAdapter(supportFragmentManager)
        viewPager.adapter = adapter
        viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tablayout))

        //Now we will do what we want to do if an item on the tabLayout is selected
        tablayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(p0: TabLayout.Tab?) {
                viewPager.currentItem = p0!!.position
            }

            override fun onTabReselected(p0: TabLayout.Tab?) {
            }

            override fun onTabUnselected(p0: TabLayout.Tab?) {
            }
        })


        //setting up the searchView
        loggedIn_search_view.clearFocus()
        setUpOnQueryTextListenerOn_onMaterialSearchView()
        loggedIn_search_view.setVoiceSearch(true)
    }


    fun setUpOnQueryTextListenerOn_onMaterialSearchView(){
        loggedIn_search_view.setOnQueryTextListener(object : MaterialSearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                //Now first search for this text
                if(query!=null){
                    performFinalSearch_For_Final_Query(query)
                }
                return true //we will return false in both conditions if we don't want to perform any task
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                //Here we will alter the suggesstion according to the data which user types
                if(newText!=null){
                    search(newText)
                }
                return true
            }

        })
    }

    private fun performFinalSearch_For_Final_Query(query: String) {

        //var stringArray = query!!.split("\\s".toRegex())
        var string = ""
        for(x in query){
            if(x==' '){
                string = string + "%20"
            }else if(x=='!'){
                string = string + "%21"
            }else if (x=='&'){
                string = string + "%26"
            }
            else if (x=='$'){
                string = string + "%24"
            }else if (x==':'){
                string = string + "%3A"
            }else{
                string = string + x
            }
        }
        Log.i("string",string)
        val call = RetrofitClient.retrofitService?.searchQuery(string)
        call?.enqueue(object : Callback<searchData>{
            override fun onFailure(call: Call<searchData>, t: Throwable) {
                Toast.makeText(this@LoggedInActivity,t.message,Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<searchData>, response: Response<searchData>) {
                if(response.isSuccessful){
                    val data = response.body()
                    Log.i("RESULT",data.toString())
                    val results = data!!.results
                    Log.i("SIZE",results.size.toString())
                    if(results.size == 0)
                    {
                        Toast.makeText(this@LoggedInActivity,"NO DATA FOUND",Toast.LENGTH_SHORT).show()
                    }
                    else
                    {
                        //Here perform the search operation which type of data is this
                        val resultClicked = results.get(0)

                        if(resultClicked.media_type.equals("movie")){
                            //Now we will perform those all operations like checking if data exist or not and it data exits , we will take them back to that movie activity
                            val movieID = resultClicked.id
                            checkVideosForMovie(movieID,resultClicked.title)
                        }
                        if(resultClicked.media_type.equals("tv")){
                            val tvID = resultClicked.id
                            checkVideosForTVShows(tvID,resultClicked.name)
                        }
                        if(resultClicked.media_type.equals("person")){
                            //Then open that intent where you are directing the person
                            val personID = resultClicked.id
                            getTheIMDB_ID(personID)
                        }
                    }
                }
            }

        })

    }

    private fun getTheIMDB_ID(personId : Int) {

        // First get the IMDB ID of the user

        val smallcall = RetrofitClient.retrofitService?.getPopularPersonDetailsByID(personId)
        smallcall?.enqueue(object : Callback<celeb_Details_by_ID>{
            override fun onFailure(call: Call<celeb_Details_by_ID>, t: Throwable) {
                Toast.makeText(this@LoggedInActivity,t.message, Toast.LENGTH_LONG).show()
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
                    }catch (e: Exception) {
                        Log.i("TAG",e.toString())
                        Toast.makeText(this@LoggedInActivity,"No Data Found",Toast.LENGTH_SHORT).show()
                    }
                }
            }

        })

    }

    private fun checkVideosForTVShows(TVid: Int, TVShowName: String) {

        val call = RetrofitClient.retrofitService?.getTVShowsVideosByItsKey(TVid)
        call?.enqueue(object  : Callback<movieVideoDetails> {
            override fun onFailure(call: retrofit2.Call<movieVideoDetails>, t: Throwable) {
                Toast.makeText(this@LoggedInActivity,t.message,Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: retrofit2.Call<movieVideoDetails>, response: Response<movieVideoDetails>) {
                if(response.isSuccessful)
                {
                    val data = response.body()
                    if(data!!.results.size>0)
                    {
                        var intent = Intent(this@LoggedInActivity, particularTVShowActivity::class.java)
                        intent.putExtra("movieId",TVid)
                        intent.putExtra("movieName",TVShowName)
                        intent.putExtra("movieYoutubeId",data!!.results.get(0).key)
                        startActivity(intent)
                    }
                    else
                    {
                        Toast.makeText(this@LoggedInActivity,"No Data Exits For This TV Show",Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })

    }

    private fun checkVideosForMovie(movieId : Int , movieName : String) {

        Log.i("movieId",movieId.toString())
        val call = RetrofitClient.retrofitService?.getVideoOfMovieByItsID(movieId)
        call?.enqueue(object  : Callback<movieVideoDetails> {
            override fun onFailure(call: Call<movieVideoDetails>, t: Throwable) {
                Toast.makeText(this@LoggedInActivity,t.message,Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call<movieVideoDetails>, response: Response<movieVideoDetails>) {
                if(response.isSuccessful)
                {
                    val data = response.body()
                    if(data!!.results.size!=0)
                    {

                        var intent = Intent(this@LoggedInActivity,particularMovieActivity::class.java)
                        intent.putExtra("movieId",movieId)
                        intent.putExtra("movieName",movieName)
                        intent.putExtra("movieYoutubeId",data!!.results.get(0).key)
                        startActivity(intent)
                    }
                    else
                    {
                        Toast.makeText(this@LoggedInActivity,"No Data Exits For This Movie",Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }


    fun search(stringToBeSearched : String){

        //var stringArray = stringToBeSearched.split("\\s".toRegex())
        var string = ""
        for(x in stringToBeSearched){
            if(x==' '){
                string = string + "%20"
            }else if(x=='!'){
                string = string + "%21"
            }else if (x=='&'){
                string = string + "%26"
            }
            else if (x=='$'){
                string = string + "%24"
            }else if (x==':'){
                string = string + "%3A"
            }else{
                string = string + x
            }
        }
        val call = RetrofitClient.retrofitService?.searchQuery(string)
        call?.enqueue(object : Callback<searchData>{
            override fun onFailure(call: Call<searchData>, t: Throwable) {
                Toast.makeText(this@LoggedInActivity,t.message,Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<searchData>, response: Response<searchData>) {
                if(response.isSuccessful){
                    val data = response.body()
                    val results = data!!.results
                    if(results.size == 0)
                    {
                        //no result found for the following data
                        datalist_of_searchView.clear()
                        //search_view.setSuggestions(null)
                    }
                    else
                    {
                        //update the list view adapter then
                        datalist_of_searchView.clear()
                        datalist_of_searchView.addAll(results)
                        var smallList = Array(results.size){
                            if(results.get(it).name==null){
                                results.get(it).title
                            }
                            else{
                                results.get(it).name
                            }
                        }
                        loggedIn_search_view.setSuggestions(smallList)
                    }
                }
            }

        })

    }
}





//  fun getAlltheLanguages()
//    {
//        var call = RetrofitClient.retrofitService?.languages()
//
//        call?.enqueue(object  : Callback<ArrayList<languageDetails>> {
//            override fun onFailure(call: Call<ArrayList<languageDetails>>, t: Throwable) {
//                Toast.makeText(this@LoggedInActivity,t.message,Toast.LENGTH_LONG).show()
//            }
//
//            override fun onResponse(call: Call<ArrayList<languageDetails>>, response: Response<ArrayList<languageDetails>>) {
//                if(response.isSuccessful)
//                {
//                    val list = response.body()
//                    for(details in list!!)
//                    {
//                        Log.i("TAG","""
//                            ${details.iso_639_1},
//                            ${details.english_name},
//                            ${details.name}
//                            """.trimIndent())
//                    }
//                }
//                else
//                {
//                    Toast.makeText(this@LoggedInActivity,"NOT ABLE TO FETCH DATA",Toast.LENGTH_LONG).show()
//                }
//            }
//        })
//    }




//fun getMovieAndTvCertificates()
//    {
//        //Similary we can do for tv certificates too
//        var call = RetrofitClient.retrofitService?.getMovieCertificates()
//
//        call?.enqueue(object : Callback<ArrayList<certificationData>>{
//            override fun onFailure(call: Call<ArrayList<certificationData>>, t: Throwable) {
//                Toast.makeText(this@LoggedInActivity,t.message,Toast.LENGTH_LONG).show()
//            }
//
//            override fun onResponse(call: Call<ArrayList<certificationData>>, response: Response<ArrayList<certificationData>>) {
//
//                if(response.isSuccessful)
//                {
//                    val list = response.body()
//                    Log.i("TAG",list!!.size.toString())
//                }
//                else
//                {
//                    Toast.makeText(this@LoggedInActivity,"NOT ABLE TO FETCH DATA",Toast.LENGTH_LONG).show()
//                }
//
//            }
//        })
//    }