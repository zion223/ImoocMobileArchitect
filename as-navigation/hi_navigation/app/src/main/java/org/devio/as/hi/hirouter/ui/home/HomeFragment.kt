package org.devio.`as`.hi.hirouter.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import org.devio.`as`.hi.hirouter.R
import org.devio.`as`.hi.nav_annotation.Destination

@Destination(pageUrl = "main/tabs/home", asStarter = true)
class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProviders.of(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        val textView: TextView = root.findViewById(R.id.text_home)
        homeViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })


        Log.e("fragment","HomeFragment,onCreateView")
        return root
    }

    override fun onResume() {
        super.onResume()
        Log.e("fragment","HomeFragment,onResume")
    }
}