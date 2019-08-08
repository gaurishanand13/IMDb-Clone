package com.example.myproject

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.RecognizerIntent
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.example.myproject.RetrofitDataClasses.RetrofitClient
import com.example.myproject.RetrofitDataClasses.searchdataClasses.searchData
import com.example.myproject.RetrofitDataClasses.searchdataClasses.searchDataResult
import com.miguelcatalan.materialsearchview.MaterialSearchView
import kotlinx.android.synthetic.main.activity_search.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchActivity : AppCompatActivity() {

    var list  = ArrayList<searchDataResult>()


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search,menu)
        val searchItem = menu?.findItem(R.id.search)
        search_view.setMenuItem(searchItem)
        return true
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if(requestCode == MaterialSearchView.REQUEST_VOICE && resultCode== Activity.RESULT_OK)
        {
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId)
        {
            android.R.id.home ->{
                onBackPressed()
            }
            else -> return false
        }
        return false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        setSupportActionBar(searchtoolBar)
        val x = supportActionBar
        x?.setTitle("")
        x?.setDisplayHomeAsUpEnabled(true)


        search_view.clearFocus()

        search_view.setOnQueryTextListener(object : MaterialSearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                //do something here
                var indexClicked = 0
                for(x in list){
                    if(x.title!=null && x.title == query){
                        break
                    }
                    if(x.name!= null && x.name==query){
                        break
                    }
                    indexClicked++
                }

                //Therefore first now get the result clicked
                val resultClicked = list.get(indexClicked)
                if(resultClicked.media_type.equals("movie")){
                    //Now vahi same operation
                }
                if(resultClicked.media_type.equals("tv")){

                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                //Here we will alter the suggesstion according to the data which user types
                if(newText!=null){
                    search(newText)
                }
                return true
            }

        })


        //adding the search thing through voice
        search_view.setVoiceSearch(true)  //Handle its reponse in the onActivity result
    }



    fun search(stringToBeSearched : String){

        var stringArray = stringToBeSearched.split("\\s".toRegex())
        var string = ""
        for(x in stringArray){
            string = string+ x
            if(x != stringArray.get(stringArray.size-1))
            {
                string = string + "%20"
            }
        }
        val call = RetrofitClient.retrofitService?.searchQuery(string)
        call?.enqueue(object : Callback<searchData>{
            override fun onFailure(call: Call<searchData>, t: Throwable) {
                Toast.makeText(this@SearchActivity,t.message,Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<searchData>, response: Response<searchData>) {
                if(response.isSuccessful){
                    val data = response.body()
                    val results = data!!.results
                    if(results.size == 0)
                    {
                        //no result found for the following data
                        list.clear()
                        //search_view.setSuggestions(null)
                    }
                    else
                    {
                        //update the list view adapter then
                        list.clear()
                        list.addAll(results)
                        var smallList = Array(results.size){
                            if(results.get(it).name==null){
                                results.get(it).title
                            }
                            else{
                                results.get(it).name
                            }
                        }
                        smallList.forEach {
                            println(it)
                        }
                        search_view.setSuggestions(smallList)
                    }
                }
            }

        })

    }
}



//        val searchView = searchItem?.actionView as androidx.appcompat.widget.SearchView
//        searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener{
//            override fun onQueryTextSubmit(p0: String?): Boolean {
//                if(p0!=null && !p0.equals("")){
//
//                }
//                return false
//            }
//
//            override fun onQueryTextChange(p0: String?): Boolean {
//                if(p0!=null && !p0.equals("")){
//
//                }
//                return false
//            }
//
//        })