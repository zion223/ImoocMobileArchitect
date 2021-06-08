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
import org.devio.`as`.hi.hirouter.ui.notifications.NotificationsViewModel
import org.devio.`as`.hi.nav_annotation.Destination

@Destination(pageUrl = "main/tabs/notification")
class NotificationsFragment : Fragment() {

    private lateinit var notificationsViewModel: NotificationsViewModel

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

        Log.e("fragment", "NotificationsFragment,onCreateView")
        return root
    }

    override fun onResume() {
        super.onResume()
        Log.e("fragment", "NotificationsFragment,onResume")
    }
}