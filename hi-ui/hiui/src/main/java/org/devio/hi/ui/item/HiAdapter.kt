package org.devio.hi.ui.item

import android.content.Context
import android.util.SparseArray
import android.util.SparseIntArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.*
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import java.lang.ref.WeakReference
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * 通用数据适配器
 *
 *
 * bugfix:HiDataItem<*, out RecyclerView.ViewHolder>  都被改成了这样。否则会有类型转换问题
 */
class HiAdapter(context: Context) : Adapter<ViewHolder>() {
    private var recyclerViewRef: WeakReference<RecyclerView>? = null
    private var mContext: Context = context
    private var mInflater = LayoutInflater.from(context)
    private var dataSets = java.util.ArrayList<HiDataItem<*, out ViewHolder>>()

    //private var typeArrays = SparseArray<HiDataItem<*, out ViewHolder>>()
    private val typePositions = SparseIntArray();

    private var headers = SparseArray<View>()
    private var footers = SparseArray<View>()
    private var BASE_ITEM_TYPE_HEADER = 1000000
    private var BASE_ITEM_TYPE_FOOTER = 2000000

    fun addHeaderView(view: View) {
        //没有添加过
        if (headers.indexOfValue(view) < 0) {
            //2
            headers.put(BASE_ITEM_TYPE_HEADER++, view)
            notifyItemInserted(headers.size() - 1)
        }
    }

    fun removeHeaderView(view: View) {
        val indexOfValue = headers.indexOfValue(view)
        if (indexOfValue < 0) return
        headers.removeAt(indexOfValue)
        notifyItemRemoved(indexOfValue)
    }

    fun addFooterView(view: View) {
        //说明这个fgooterview 没有添加过
        if (footers.indexOfValue(view) < 0) {
            footers.put(BASE_ITEM_TYPE_FOOTER++, view)
            notifyItemInserted(itemCount)
        }
    }

    fun removeFooterView(view: View) {
        //0 1  2
        val indexOfValue = footers.indexOfValue(view)
        if (indexOfValue < 0) return
        footers.removeAt(indexOfValue)
        //position代表的是在列表中分位置
        notifyItemRemoved(indexOfValue + getHeaderSize() + getOriginalItemSize())
    }

    fun getHeaderSize(): Int {
        return headers.size()
    }

    fun getFooterSize(): Int {
        return footers.size()
    }

    fun getOriginalItemSize(): Int {
        return dataSets.size
    }

    /**
     *在指定为上添加HiDataItem
     */
    fun addItemAt(
        index: Int,
        dataItem: HiDataItem<*, out ViewHolder>,
        notify: Boolean
    ) {
        if (index >= 0) {
            dataSets.add(index, dataItem)
        } else {
            dataSets.add(dataItem)
        }

        val notifyPos = if (index >= 0) index else dataSets.size - 1
        if (notify) {
            notifyItemInserted(notifyPos)
        }

        dataItem.setAdapter(this)
    }

    /**
     * 往现有集合的尾部逐年items集合
     */
    fun addItems(items: List<HiDataItem<*, out ViewHolder>>, notify: Boolean) {
        val start = dataSets.size
        items.forEach { dataItem ->
            dataSets.add(dataItem)
            dataItem.setAdapter(this)
        }
        if (notify) {
            notifyItemRangeInserted(start, items.size)
        }
    }

    /**
     * 从指定位置上移除item
     */
    fun removeItemAt(index: Int): HiDataItem<*, out ViewHolder>? {
        if (index >= 0 && index < dataSets.size) {
            val remove: HiDataItem<*, out ViewHolder> = dataSets.removeAt(index)
            notifyItemRemoved(index)
            return remove
        } else {
            return null
        }
    }

    /**
     * 移除指定item
     */
    fun removeItem(dataItem: HiDataItem<*, out ViewHolder>) {
        val index: Int = dataSets.indexOf(dataItem)
        removeItemAt(index)
    }

    /**
     * 指定刷新 某个item的数据
     */
    fun refreshItem(dataItem: HiDataItem<*, out ViewHolder>) {
        val indexOf = dataSets.indexOf(dataItem)
        notifyItemChanged(indexOf)
    }

    /**
     * 以每种item类型的class.hashcode为 该item的viewType
     *
     * 这里把type存储起来，是为了onCreateViewHolder方法能够为不同类型的item创建不同的viewholder
     */
    override fun getItemViewType(position: Int): Int {
        if (isHeaderPosition(position)) {
            return headers.keyAt(position)
        }
        if (isFooterPosition(position)) {
            //footer的位置 应该计算一下  position =6 , headercount =1  itemcoun=5 =,footersize=1
            val footerPosition = position - getHeaderSize() - getOriginalItemSize()
            return footers.keyAt(footerPosition)
        }

        val itemPosition = position - getHeaderSize()
        val dataItem = dataSets[itemPosition]
        val type = dataItem.javaClass.hashCode()

        //按照原来的写法 相同的viewType仅仅只在第一次，会把viewType和dataItem关联
//        if (typeArrays.indexOfKey(type) < 0) {
//            typeArrays.put(type, dataItem)
//        }
        typePositions.put(type, position)
        return type
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        if (headers.indexOfKey(viewType) >= 0) {
            val view = headers[viewType]
            return object : RecyclerView.ViewHolder(view) {}
        }
        if (footers.indexOfKey(viewType) >= 0) {
            val view = footers[viewType]
            return object : RecyclerView.ViewHolder(view) {}
        }

        //这会导致不同position，但viewType相同，获取到的dataItem始终是第一次关联的dataItem对象。
        //这就会导致通过getItemView创建的成员变量，只在第一个dataItem中，其它实例中无法生效

        //为了解决dataItem成员变量binding, 刷新之后无法被复用的问题
        val position = typePositions.get(viewType)
        val dataItem = dataSets[position]
        val vh = dataItem.onCreateViewHolder(parent)
        if (vh != null) return vh

        var view: View? = dataItem.getItemView(parent)
        if (view == null) {
            val layoutRes = dataItem.getItemLayoutRes()
            if (layoutRes < 0) {
                throw RuntimeException("dataItem:" + dataItem.javaClass.name + " must override getItemView or getItemLayoutRes")
            }
            view = mInflater.inflate(layoutRes, parent, false)
        }
        return createViewHolderInternal(dataItem.javaClass, view!!)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (isHeaderPosition(position) || isFooterPosition(position)) return

        val itemPosition = position - getHeaderSize()
        val dataItem = getItem(itemPosition)
        dataItem?.onBindData(holder, itemPosition)
    }

    private fun createViewHolderInternal(
        javaClass: Class<HiDataItem<*, out ViewHolder>>,
        view: View
    ): ViewHolder {
        //得到该Item的父类类型,即为HiDataItem.class。  class 也是type的一个子类。
        //type的子类常见的有 class，类泛型,ParameterizedType参数泛型 ，TypeVariable字段泛型
        //所以进一步判断它是不是参数泛型
        val superclass = javaClass.genericSuperclass
        if (superclass is ParameterizedType) {
            //得到它携带的泛型参数的数组
            val arguments = superclass.actualTypeArguments
            //挨个遍历判断 是不是咱们想要的 RecyclerView.ViewHolder 子类 类型的。
            for (argument in arguments) if (argument is Class<*> && ViewHolder::class.java.isAssignableFrom(
                    argument
                )
            ) {
                try {
                    //如果是，则使用反射 实例化类上标记的实际的泛型对象
                    //这里需要  try-catch 一把，如果咱们直接在HiDataItem子类上标记 RecyclerView.ViewHolder，抽象类是不允许反射的
                    return argument.getConstructor(View::class.java).newInstance(view) as ViewHolder
                } catch (e: Throwable) {
                    e.printStackTrace()

                }
            }
        }
        return object : HiViewHolder(view) {}
    }

    private fun isFooterPosition(position: Int): Boolean {
        // 10->  4+ 4.
        return position >= getHeaderSize() + getOriginalItemSize()
    }

    private fun isHeaderPosition(position: Int): Boolean {
        // 5 --> 4 3 2 1
        return position < headers.size()
    }

    override fun getItemCount(): Int {
        return dataSets.size + getHeaderSize() + getFooterSize()
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        recyclerViewRef = WeakReference(recyclerView)
        /**
         * 为列表上的item 适配网格布局
         */
        val layoutManager = recyclerView.layoutManager
        if (layoutManager is GridLayoutManager) {
            val spanCount = layoutManager.spanCount
            layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    if (isHeaderPosition(position) || isFooterPosition(position)) {
                        return spanCount
                    }
                    val itemPosition = position - getHeaderSize()
                    if (itemPosition < dataSets.size) {
                        val dataItem = getItem(itemPosition)
                        if (dataItem != null) {
                            val spanSize = dataItem.getSpanSize()
                            return if (spanSize <= 0) spanCount else spanSize
                        }
                    }
                    return spanCount
                }
            }
        }
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        recyclerViewRef?.clear()
    }

    open fun getAttachRecyclerView(): RecyclerView? {
        return recyclerViewRef?.get()
    }

    fun getItem(position: Int): HiDataItem<*, ViewHolder>? {
        if (position < 0 || position >= dataSets.size)
            return null
        return dataSets[position] as HiDataItem<*, ViewHolder>
    }

    override fun onViewAttachedToWindow(holder: ViewHolder) {
        val recyclerView = getAttachRecyclerView()
        if (recyclerView != null) {
            //瀑布流的item占比适配
            val position = recyclerView.getChildAdapterPosition(holder.itemView)
            val isHeaderFooter = isHeaderPosition(position) || isFooterPosition(position)
            val itemPosition = position - getHeaderSize()
            val dataItem = getItem(itemPosition) ?: return
            val lp = holder.itemView.layoutParams
            if (lp != null && lp is StaggeredGridLayoutManager.LayoutParams) {
                val manager = recyclerView.layoutManager as StaggeredGridLayoutManager?
                if (isHeaderFooter) {
                    lp.isFullSpan = true
                    return
                }
                val spanSize = dataItem.getSpanSize()
                if (spanSize == manager!!.spanCount) {
                    lp.isFullSpan = true
                }
            }

            dataItem.onViewAttachedToWindow(holder)
        }
    }

    override fun onViewDetachedFromWindow(holder: ViewHolder) {
        val position = holder.adapterPosition
        if (isHeaderPosition(position) || isFooterPosition(position))
            return
        val itemPosition = position - getHeaderSize()
        val dataItem = getItem(itemPosition) ?: return
        dataItem.onViewDetachedFromWindow(holder)
    }

    fun clearItems() {
        dataSets.clear()
        notifyDataSetChanged()
    }
}