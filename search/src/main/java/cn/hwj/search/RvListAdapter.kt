package cn.hwj.search

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter

class RvListAdapter : BaseQuickAdapter<Status, RvListAdapter.VH>() {

    //创建viewHolder
    inner class VH(v: View) : RecyclerView.ViewHolder(v) {
        val tv1: TextView
        val btn1: Button

        init {
            tv1 = v.findViewById(R.id.tv1)
            btn1 = v.findViewById(R.id.btn1)
        }
    }



    override fun onCreateViewHolder(context: Context, parent: ViewGroup, viewType: Int): VH {
        val v=LayoutInflater.from(parent.context).inflate(R.layout.item_rv,parent,false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int, item: Status?) {
        when(holder.layoutPosition%2){
            0->holder.tv1.text="0000  $position"
            1->holder.tv1.text="1111  $position"
        }
    }
}