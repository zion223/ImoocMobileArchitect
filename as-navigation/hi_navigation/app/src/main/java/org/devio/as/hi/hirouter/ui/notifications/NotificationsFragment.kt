package org.devio.`as`.hi.hirouter.ui.notifications

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

@Destination(pageUrl = "main/tabs/notification")
class NotificationsFragment : Fragment() {

    private val TAG: String = NotificationsViewModel::class.java.simpleName

    private lateinit var notificationsViewModel: NotificationsViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.e(TAG, "NotificationsFragment,onViewCreated")
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        notificationsViewModel =
                ViewModelProviders.of(this).get(NotificationsViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_notifications, container, false)
        val textView: TextView = root.findViewById(R.id.text_notifications)
        notificationsViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })

        Log.e(TAG, "NotificationsFragment,onCreateView")
        return root
    }

    override fun onResume() {
        super.onResume()
        Log.e(TAG, "NotificationsFragment,onResume")
    }

    override fun onDestroy() {
        Log.e(TAG, "NotificationsFragment,onDestroy")
        super.onDestroy()
    }

    override fun onStart() {
        Log.e(TAG, "NotificationsFragment,onStart")
        super.onStart()
    }

    override fun onPause() {
        Log.e(TAG, "NotificationsFragment,onPause")
        super.onPause()
    }

    override fun onStop() {
        Log.e(TAG, "NotificationsFragment,onStop")
        super.onStop()
    }

    override fun onDestroyView() {
        Log.e(TAG, "NotificationsFragment,onDestroyView")
        super.onDestroyView()
    }

    override fun onDetach() {
        Log.e(TAG, "NotificationsFragment,onDetach")
        super.onDetach()
    }
}