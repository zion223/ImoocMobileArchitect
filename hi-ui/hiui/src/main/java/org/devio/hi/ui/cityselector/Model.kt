package org.devio.hi.ui.cityselector

import android.os.Parcelable
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

const val TYPE_COUNTRY = "0"
const val TYPE_PROVINCE = "1"
const val TYPE_CITY = "2"
const val TYPE_DISTRICT = "3"

@Parcelize
open class Province : District(), Parcelable, Serializable {
    //该省份下面所有的市
    val cities = ArrayList<City>()

    //选择的市
    var selectCity: City? = null

    //选择的区
    var selectDistrict: District? = null
}

@Parcelize
open class City : District(), Parcelable, Serializable {
    var districts = ArrayList<District>()
}

//城市的类型，type .0是国，1是省，2是市，3是区
//districtName 地区名
//pid 父级id
@Parcelize
open class District : Parcelable, Serializable {
    var districtName: String? = null
    var id: String? = null
    var pid: String? = null
    var type: String? = null


    companion object {
        fun copyDistrict(src: District, dest: District) {
            dest.id = src.id
            dest.type = src.type
            dest.districtName = src.districtName
            dest.pid = src.pid
        }
    }
}