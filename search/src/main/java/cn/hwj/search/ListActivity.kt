package cn.hwj.search

import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import cn.hwj.core.global.printV
import cn.hwj.route.RoutePath
import com.chad.library.adapter.base.QuickAdapterHelper
import com.chad.library.adapter.base.loadState.LoadState
import com.chad.library.adapter.base.loadState.trailing.TrailingLoadStateAdapter
import com.didi.drouter.annotation.Router

@Router(path = RoutePath.SEARCH_ACTIVITY_LIST)
class ListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.setLayerType(View.LAYER_TYPE_HARDWARE, setThemePaint(1))
        setContentView(R.layout.activity_list)
        val iv = findViewById<ImageView>(R.id.iv)
        iv.setBackgroundResource(R.mipmap.ic_launcher_round)
        val tvInfo = findViewById<TextView>(R.id.tvInfo)
        tvInfo.text = "ssssssssssssssssssssssssssssss"
        initRv()
    }

    //0-正常、1-黑白
    private fun setThemePaint(type: Int): Paint {
        val paint = Paint()
        val cm = ColorMatrix()
        when (type) {
            0 -> cm.setSaturation(1.0f)
            1 -> cm.setSaturation(0.0f)
        }
        paint.colorFilter = ColorMatrixColorFilter(cm)
        return paint
    }

    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var rvList: RecyclerView
    private lateinit var rvHelper: QuickAdapterHelper
    private var curPage = 0
    private val PAGE_SIZE = 10
    private val rvListAdapter = RvListAdapter()
    private fun initRv(num: Int = 1) {
        //默认样式
        rvHelper = QuickAdapterHelper.Builder(rvListAdapter)
            .setTrailingLoadStateAdapter(object : TrailingLoadStateAdapter.OnTrailingListener {
                override fun onFailRetry() {
                    loadData()
                }

                override fun onLoad() {
                    loadData()
                }

                override fun isAllowLoading(): Boolean {
                    return !swipeRefreshLayout.isRefreshing
                }
            }).build()
        swipeRefreshLayout = findViewById(R.id.refreshLayout)
        initRefreshLayout()
        rvList = findViewById(R.id.rvList)
        rvList.layoutManager = GridLayoutManager(this, num)
        rvList.adapter = rvHelper.adapter
        refresh()
    }

    private fun loadData(size: Int = PAGE_SIZE) {
        val list = mutableListOf<Status>()
        for (i in 0..size) {
            list.add(i, Status())
        }
        swipeRefreshLayout.isRefreshing = false
        if (curPage == 0) {
            rvListAdapter.submitList(list)
        } else {
            rvListAdapter.addAll(list)
        }
        // 如果在数据不满足一屏时，暂停加载更多，请调用下面方法
        // helper.trailingLoadStateAdapter?.checkDisableLoadMoreIfNotFullPage()
        if (curPage == 3) { //模拟没有第4页
            rvHelper.trailingLoadState = LoadState.NotLoading(true)
            printV("no more data>>>>")
        } else {
            rvHelper.trailingLoadState = LoadState.NotLoading(false)
        }
        curPage++

        //没有network
//     swipeRefreshLayout.isRefreshing=false
//     rvHelper.trailingLoadState=LoadState.Error(err)
    }

    private fun addHeadView() {
//rvHelper.addBeforeAdapter(0,adatper)
    }

    //下拉刷新
    private fun refresh() {
        curPage = 0
        rvHelper.trailingLoadState = LoadState.None
        loadData()
    }

    private fun initRefreshLayout() {
        swipeRefreshLayout.setColorSchemeColors(Color.rgb(47, 223, 199))
        swipeRefreshLayout.setOnRefreshListener {
            refresh()
        }
    }
}