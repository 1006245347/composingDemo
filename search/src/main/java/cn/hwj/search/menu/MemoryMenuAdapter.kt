package cn.hwj.search.menu

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.hwj.core.global.UiHelper
import cn.hwj.search.R
import com.chad.library.adapter.base.BaseQuickAdapter

/**
 * @author by jason-何伟杰，2023/1/12
 * des:左列表
 */
class MemoryMenuAdapter : BaseQuickAdapter<MenuSortBean, MemoryMenuAdapter.Vh>() {

    private var checkIndex: Int = 0
    var isClick = false

    fun setChecked(index: Int) {
        this.checkIndex = index
        this.notifyDataSetChanged()
    }

    /* 让左边选中 */
    fun set2Index(menuId: Int) {
        if (isClick) return
        for (i in items.indices) {
            if (menuId == items[i].menuId) {
                setChecked(i)
                move2Index(i)
                return
            }
        }
    }

    /* 如果选中的条目不在显示范围内，要滑动条目让该条目显示出来*/
    fun move2Index(index: Int) {
        val manager = recyclerView.layoutManager as LinearLayoutManager
        val f = manager.findFirstVisibleItemPosition()
        val l = manager.findLastVisibleItemPosition()
        if (index <= f || index >= l) {
            manager.scrollToPosition(index)
        }
    }

    override fun onBindViewHolder(holder: Vh, position: Int, item: MenuSortBean?) {
        holder.tvName.text = item?.menuName
        if (checkIndex == position) {
            holder.tvName.setTextColor(UiHelper.getColor(R.color.cGreen))
            holder.tvName.setBackgroundResource(R.color.cYellow)
        }else{
            holder.tvName.setTextColor(UiHelper.getColor(R.color.cBlack))
            holder.tvName.setBackgroundResource(R.color.cWhite)
        }
    }

    override fun onCreateViewHolder(context: Context, parent: ViewGroup, viewType: Int): Vh {
        val v =
            LayoutInflater.from(parent.context).inflate(R.layout.memory_menu_item, parent, false)
        return Vh(v)
    }

    inner class Vh(v: View) : RecyclerView.ViewHolder(v) {
        val tvName: TextView = v.findViewById(R.id.tvName)
    }
}