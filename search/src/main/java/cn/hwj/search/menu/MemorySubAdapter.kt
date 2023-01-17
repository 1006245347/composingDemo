package cn.hwj.search.menu

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import cn.hwj.search.R
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.fullspan.FullSpanAdapterType

class MemorySubAdapter(private val sizeSpan: Int) :
    BaseQuickAdapter<MenuSortBean.SubBean, MemorySubAdapter.Vh>(), FullSpanAdapterType {

    override fun onBindViewHolder(holder: Vh, position: Int, item: MenuSortBean.SubBean?) {
//        item?.imgUrl?.let {
//            GlideUtil.loadImg(context, it, holder.ivPic)
//        }
        holder.tvDes.text = item?.title
//        if (position == 0) {    //单列数据
//            holder.flHeader.visibility = View.VISIBLE //悬停标题
//            holder.tvHeader.text = item?.menuName
//        } else {
//            if (item?.menuId == items[position - 1].menuId) {
//                holder.flHeader.visibility = View.GONE
//            } else {
//                holder.flHeader.visibility = View.VISIBLE
//                holder.tvHeader.text = item?.menuName
//            }
//        }

        if (position < sizeSpan) {     //n列数据,这种用网格
            setShowHeader(holder, item)
            if (position == 0) {
                setShowHeader(holder, item)
            } else {
                setHideHeader(holder)
            }
        } else {
            if (items[position - 1].menuId != item?.menuId) {
                setShowHeader(holder, item)
            } else {
                setHideHeader(holder)
            }
        }
    }

    //position是当前组的指针，不是整个adapter的
    private fun setShowHeader(holder: Vh, item: MenuSortBean.SubBean?) {
        holder.flHeader.visibility = View.VISIBLE
        holder.tvHeader.visibility = View.VISIBLE
        holder.tvHeader.text = item?.menuName
    }

    private fun setHideHeader(holder: Vh) {
        holder.flHeader.visibility = View.VISIBLE
        holder.tvHeader.visibility = View.INVISIBLE
    }

    override fun onCreateViewHolder(context: Context, parent: ViewGroup, viewType: Int): Vh {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.memory_menu_sub, parent, false)
        return Vh(v)
    }

    inner class Vh(v: View) : RecyclerView.ViewHolder(v) {
        val tvHeader: TextView = v.findViewById(R.id.tvHeader)
        val ivPic: ImageView = v.findViewById(R.id.ivPic)
        val flHeader: FrameLayout = v.findViewById(R.id.fl_header)
        val tvDes: TextView = v.findViewById(R.id.tvDes)
    }
}