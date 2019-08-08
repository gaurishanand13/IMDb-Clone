package com.example.myproject.expandableListViewAdapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.TextView
import com.example.myproject.R
import com.example.myproject.RetrofitDataClasses.expandableData
import com.example.myproject.RetrofitDataClasses.particularMovieReview


//Reviews
class customExpandableAdapter (val data : HashMap<String,ArrayList<particularMovieReview>>,val context: Context): BaseExpandableListAdapter(){

    override fun getGroupCount() = 1

    override fun getChildrenCount(p0: Int) = data["Reviews"]!!.size


    override fun getGroup(p0: Int): Any {

        //Here we will just return the data of that particular group which is selected. Here since it is only 1 group. Therefore we will return that group data
        return data["Reviews"]!!
    }

    override fun getChild(p0: Int, p1: Int): Any {

        //Here p0 is the group position and p1 is the child position of that particular group
        return data["Reviews"]!!.get(p1)

    }

    override fun getGroupId(p0: Int): Long {
        return p0.toLong()   //Let group id be unique to corresponding to its position in the list
    }

    override fun getChildId(p0: Int, p1: Int): Long {
        return (p1*p0).toLong()   // Here too let child id of the particular group be unique
    }

    override fun isChildSelectable(p0: Int, p1: Int): Boolean {
        return false  //because we don't have anything in the child view to be selected
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    override fun getGroupView(p0: Int, p1: Boolean, p2: View?, p3: ViewGroup?): View {

        //here p0 is the position of the group in the list
        //p1 is the boolean which represents if the list is expanded or not. If it it expanded p1 is true otherwise p2 is false

        //here p2 is just like normal view, for initial group members it will be null but as we scroll and items are repeated parent view will be the same and repeated ones
        var view: View?=null
        if(p2==null)
        {
            val layoutInflater = p3!!.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = layoutInflater.inflate(R.layout.expandable_list_view_header,p3,false)
        }
        else
        {
            view = p2
        }
        val textView = view!!.findViewById<TextView>(R.id.listTitle)
        textView.text = "Reviews"
        return view
    }


    override fun getChildView(p0: Int, p1: Int, p2: Boolean, p3: View?, p4: ViewGroup?): View {

        //p0 is the groupView position
        //p1 is the position in the expanded list
        //p2 is the boolean if it is the last child or not
        //p3 is the child view of the group just like above
        //p4 is the parent view

        Log.i("TAG",p1.toString())
        var view: View?=null
        if(p3==null)
        {
            val layoutInflater = p4!!.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = layoutInflater.inflate(R.layout.expandable_list_view_child,p4,false)
        }
        else
        {
            view = p3
        }
        var textView = view!!.findViewById<TextView>(R.id.reviewOwner)
        textView.text = data["Reviews"]!!.get(p1).author
        textView = view!!.findViewById<TextView>(R.id.reviewDetails)
        textView.text = data["Reviews"]!!.get(p1).content
        return view

    }


}