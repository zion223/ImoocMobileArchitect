package org.devio.`as`.hi.hinavigation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        //寻找出路由控制器对象,它是我们路由跳转的唯一入口
        val navController: NavController = findNavController(R.id.nav_host_fragment)
        val hostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment);

        NavUtil.builderNavGraph(
            this,
            hostFragment!!.childFragmentManager,
            navController,
            R.id.nav_host_fragment
        )

        NavUtil.builderBottomBar(navView)

        navView.setOnNavigationItemSelectedListener { item ->
            navController.navigate(item.itemId)
            true
        }
        //将navController和BottomNavigationView绑定，形成联动效果
        //navView.setupWithNavController(navController)


//        navController!!.navigate(R.id.navigation_notifications)
//        navController!!.navigate(R.id.navigation_notifications, Bundle.EMPTY)
//        navController!!.navigate(Uri.parse("www.imooc.com"))
//
//        navController!!.navigateUp() // 回退到上一个页面
//        navController!!.popBackStack(R.id.navigation_dashboard,false) // false 指定目标也不需要被弹出
    }
}
