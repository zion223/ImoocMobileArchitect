package org.devio.`as`.hi.hirouter.ui.dashboard

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

@Destination(pageUrl = "main/tabs/dashboard", asStarter = false)
class DashboardFragment : Fragment() {

    private val TAG: String = DashboardFragment::class.java.simpleName

    private lateinit var dashboardViewModel: DashboardViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        dashboardViewModel =
                ViewModelProviders.of(this).get(DashboardViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_dashboard, container, false)
        val textView: TextView = root.findViewById(R.id.text_dashboard)
        dashboardViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        Log.e("fragment", "DashboardFragment,onCreateView")
        return root
    }


    override fun onResume() {
        super.onResume()
        Log.e(TAG, "onResume")
    }

    override fun onDestroy() {
        Log.e(TAG, "onDestroy")
        super.onDestroy()
    }

    override fun onStart() {
        Log.e(TAG, "onStart")
        super.onStart()
    }

    override fun onPause() {
        Log.e(TAG, "onPause")
        super.onPause()
    }

    override fun onStop() {
        Log.e(TAG, "onStop")
        super.onStop()
    }

    override fun onDestroyView() {
        Log.e(TAG, "onDestroyView")
        super.onDestroyView()
    }

    override fun onDetach() {
        Log.e(TAG, "onDetach")
        super.onDetach()
    }
}