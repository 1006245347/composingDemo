package cn.hwj.search.menu

/**
 * @author by jason-何伟杰，2023/1/12
 * des:左右联动列表实体，每个左项具有一个集合数据(右边)
 */
class MenuSortBean : Comparable<MenuSortBean> {

    var menuName: String? = null
    var menuId: Int = 0
    var menuDes: String? = null
    var date: Long = 0
    var type: Int = 0       //被标记的项可以置顶
    var index: Int = 0     //当前指针，根据type的大小重新排序，注意后面要反序
    var list: MutableList<SubBean>

    constructor(
        name: String?,
        id: Int,
        des: String?,
        date: Long,
        type: Int,
        list: MutableList<SubBean>,
        index: Int
    ) {
        this.menuName = name
        this.menuId = id
        this.menuDes = des
        this.date = date
        this.list = list
        this.type = type
        this.index = index
    }

    //排序对比
    override fun compareTo(other: MenuSortBean): Int {
        return this.type.compareTo(other.type)
    }

    data class SubBean(
        var menuId: Int,//每个子项要关联分类id
        var menuName: String?,
        val title: String?,
        val imgUrl: String,
        var isLike: Boolean,
    )
}

