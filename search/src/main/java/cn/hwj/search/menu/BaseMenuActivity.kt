package cn.hwj.search.menu

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.SparseArray
import android.widget.Toast
import androidx.core.util.forEach
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.hwj.core.global.printD
import cn.hwj.search.R
/**
 * @author by jason-何伟杰，2023/1/17
 * des:左右联动列表的中间层，有太多界面逻辑管理
 */
open class BaseMenuActivity : AppCompatActivity() {

    private val sizeSpan = 3  //右边列数
    private lateinit var rvMenu: RecyclerView   //左菜单
    private lateinit var rvSub: RecyclerView    //右列表
    private lateinit var menuAdapter: MemoryMenuAdapter
    private var subList = mutableListOf<MenuSortBean.SubBean>()

    //第一种方式  普通适配器  0
    lateinit var subAdapter: MemorySubAdapter

    //第二种 拼接Adapter   1
    private val concatAdapter: ConcatAdapter = ConcatAdapter() //包裹多个adapter

    var curIndex = 0
    var move2Top = false

    var techType = 0  //实现分类
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base_menu)
        initAty()
    }

    private fun initAty() {
        techType = intent.getIntExtra("type", 0)
        initLeft()
        initRight()

        val mockList = loadData()
        mockList.let {
            menuAdapter.submitList(it)          //设置左边菜单数据

            subList.clear()         //设置右边数据
            menuAdapter.items.forEach {
                subList.addAll(it.list)
            } //整个网格每行都会被铺满
            //第一种方式填充数据
            if (techType == 0)
                subAdapter.submitList(subList)

            //第二种
            if (techType == 1) {
                it.forEach { m ->
                    val adapter = MemorySubAdapter(sizeSpan)
                    adapter.setOnItemClickListener { adapter1, _, index ->
                        val item = adapter1.getItem(index) as MenuSortBean.SubBean
                        printD("sub=${item.menuId} $index") //在当前adapter的位置
//                    ToastUtil.showToast("sub=${item.menuId} $index")
                    }
                    adapter.submitList(m.list)
                    concatAdapter.addAdapter(adapter)
                }
            }
        }
    }

    private fun initLeft() {
        rvMenu = findViewById(R.id.rvMenu)
        rvMenu.layoutManager = LinearLayoutManager(this)
        menuAdapter = MemoryMenuAdapter()
        rvMenu.adapter = menuAdapter
        menuAdapter.setOnItemClickListener { adapter, view, position ->
            val item = adapter.getItem(position) as MenuSortBean
            if (rvSub.scrollState != RecyclerView.SCROLL_STATE_IDLE) return@setOnItemClickListener
            menuAdapter.isClick = true
            menuAdapter.setChecked(position)
            val menuId = item.menuId

            //第二种方式 自动追溯索引
            if (techType == 1) {
                var count = 0
                menuAdapter.items.forEach { m ->
                    count += m.list.size
                    if (m.list[0].menuId == menuId) {
                        curIndex = count - m.list.size
                        move2Position(curIndex)
                        return@setOnItemClickListener
                    }
                }
            }

            //第一种方式使用
            if (techType == 0) {
                for (i in subAdapter.items.indices) {
                    //找到点击项的menuId,匹配两边的索引
                    if (menuId == subAdapter.items[i].menuId) {
                        curIndex = i
                        move2Position(curIndex)
                        return@setOnItemClickListener
                    }
                }
            }
        }
    }

    private fun initRight() {
        rvSub = findViewById(R.id.rvSub)
        val gridLayoutManager = GridLayoutManager(this, sizeSpan)
        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                if (position > sizeSpan && subList.size > 0) { //第一行不处理
//                    val item = subAdapter.getItem(position)
                    val item = subList[position]
                    var sum = 0
                    for (m in menuAdapter.items) {
                        val count = m.list.size
                        sum += count
                        if (item.menuId == m.menuId) { //只处理分组的最后一行最后一个铺满
                            if (position == sum - 1) {  //直接找到每组最后一个index
                                val at = (position - (sum - m.list.size)) % sizeSpan
                                if (at == 0) {
                                    return sizeSpan
                                } else if (at > 0 && at < sizeSpan) {
                                    return sizeSpan - at
                                }
                            }
                        }
                    }
                }
                return 1
            }
        }
        rvSub.layoutManager = gridLayoutManager
        //第一种方式 可以快速找到全局真正的指针
        if (techType == 0) {
            subAdapter = MemorySubAdapter(sizeSpan)
            rvSub.adapter = subAdapter
            subAdapter.apply {
                addOnItemChildClickListener(R.id.ivPic) { adapter, view, position ->
                    val item = adapter.getItem(position) as MenuSortBean.SubBean
                    Toast.makeText(
                        context,
                        "msg:${item.title} index=$position ",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            }
        }

        //第二种方式
        if (techType == 1) {
            rvSub.adapter = concatAdapter
        }
        rvSub.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val manager = rvSub.layoutManager as GridLayoutManager
                if (move2Top) { //向下滑动时，只会把改条目显示出来；我们还需要让该条目滑动到顶部；
                    move2Top = false
                    val m = curIndex - manager.findFirstVisibleItemPosition()
                    if (m >= 0 && m <= manager.childCount) {
                        val top = manager.getChildAt(m)?.top
                        top?.let {
                            rvSub.smoothScrollBy(0, top)
                        }
                    }
                } else {
                    val index = manager.findFirstVisibleItemPosition()
                    //无奈，要全局维护一个list,拼接adapter没有暴露简单的位置回调api
                    menuAdapter.set2Index(subList[index].menuId)
                }
            }
        })

        rvSub.setOnTouchListener { view, event ->
            menuAdapter.isClick = false
            false
        }
    }

    private fun move2Position(index: Int) {
        val manager = rvSub.layoutManager as GridLayoutManager
        val f = manager.findFirstVisibleItemPosition()
        val l = manager.findLastVisibleItemPosition()
        if (index <= f) { //向上移动
            manager.scrollToPosition(index)
        } else if (index <= l) { //已经再屏幕上面显示时
            val m = index - f
            if (0 <= m && m <= manager.childCount) {
                val top = manager.getChildAt(m)?.top
                top?.let {
                    rvSub.smoothScrollBy(0, it)
                }
            }
        } else {    //向下滑动
            move2Top = true
            manager.scrollToPosition(index)
        }
    }

    /***************************************以下是模拟数据测试********************************/
    private val sortList = mutableListOf<MenuSortBean>()

    private val sparseArray = SparseArray<MenuSortBean>()

    private fun loadData(): MutableList<MenuSortBean> {
        val subList = mutableListOf<MenuSortBean.SubBean>()
        for (sub in 1..10) { //每个子项的分类id/名必须一致
            subList.add(MenuSortBean.SubBean(sub, "menu", "title$sub", "img", false))
        }
        for (c in 1..10) {  //构造10个分类
            val newList = mutableListOf<MenuSortBean.SubBean>()
            subList.forEach {
                val n = MenuSortBean.SubBean(
                    c, "menu$c", it.title,
                    it.imgUrl, it.isLike
                )
                newList.add(n)//要构造新对象才能正常赋值
            }
            if (c == 2 || c == 9) {
                newList.add(newList[3]) //第2、9组有11个数据
            }
            insertData(
                "menu$c", c, "des$c", System.currentTimeMillis() - 1000 * 60 * 60,
                c * 10, newList, c - 1
            )
        }
        sortType()
        return sortList
    }

    private fun insertData(
        menuName: String,
        menuId: Int,
        menuDes: String?,
        date: Long,
        type: Int,
        subList: MutableList<MenuSortBean.SubBean>, index: Int
    ): MenuSortBean {
//        subList.forEach {
//            printD("n-${subList.size}==${it.menuId}")
        //// Logcat把重复性日志过滤掉！！！
//        }
        val bean = MenuSortBean(menuName, menuId, menuDes, date, type, subList, index)
        sortList.add(bean)
        return bean
    }

    private fun sortType() {
        sortList.forEach {
            sparseArray.append(it.type, it)
        }
        sortList.clear()
        sparseArray.forEach { key, value ->
            sortList.add(sparseArray.get(key)) //根据type升序
            printD("type大小-$key $sortList")
        }
//    //    sortList.reverse()//反序
    }
}