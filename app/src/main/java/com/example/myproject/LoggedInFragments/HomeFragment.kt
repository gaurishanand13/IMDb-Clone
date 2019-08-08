package com.example.myproject.LoggedInFragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter

import com.example.myproject.R
import com.felipecsl.asymmetricgridview.library.Utils
import kotlinx.android.synthetic.main.fragment_home.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class HomeFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        listView.setRequestedColumnWidth(Utils.dpToPx(this, 120));
//    final List<AsymmetricItem> items = new ArrayList<>();
//
//    // initialize your items array
//    adapter = new ListAdapter(this, listView, items);
//    AsymmetricGridViewAdapter asymmetricAdapter =
//        new AsymmetricGridViewAdapter<>(this, listView, adapter);
//    listView.setAdapter(asymmetricAdapter);
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listView.setRequestedColumnWidth(Utils.dpToPx(context,120f))
       /// val adapter = ListAdapter(this,listView,ArrayList<String>())
    }
}
