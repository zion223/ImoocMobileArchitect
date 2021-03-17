package org.devio.hi.config.app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import org.devio.hi.config.HiConfig

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tv_fetch.setOnClickListener {
            testFetch()
        }
        tv_get_config.setOnClickListener {
            tv_show.text = HiConfig.instance.getStringConfig("tips")
        }
    }

    private fun testFetch() {
        HiConfig.instance.feed(json)
    }

    companion object {
        const val json = "{\n" +
                "    \"code\":0,\n" +
                "    \"message\":\"SUCCESS.\",\n" +
                "    \"data\":{\n" +
                "        \"total\":30,\n" +
                "        \"list\":[\n" +
                "            {\n" +
                "                \"categoryId\":1,\n" +
                "                \"categoryName\":\"热门\",\n" +
                "                \"createTime\":\"2022-09-10 15:53:48\"\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    \"extra\":{\n" +
                "        \"hiConfig\":{\n" +
                "            \"id\":29,\n" +
                "            \"namespace\":\"haowu\",\n" +
                "            \"version\":\"20200920182242\",\n" +
                "            \"createTime\":\"2020-09-20 18:22:42\",\n" +
                "            \"originalUrl\":\"http://192.168.50.125:8080/as/file/as/haowu_20200920182242\",\n" +
                "            \"jsonUrl\":\"http://192.168.50.125:8080/as/file/as/haowu_20200920182242.json\",\n" +
                "            \"content\":null\n" +
                "        }\n" +
                "    }\n" +
                "}"
    }
}