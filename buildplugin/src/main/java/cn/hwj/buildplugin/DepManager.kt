/**
 * 具体依赖内容
 *
 */
object DepManager {

    //true=各模块单独运行，false=app集成所有模块
    //手动调整集成模式，需要修改isDebug的值，还要Invalidate Caches全√才能变换
    val isDebug=false
    /**
     * 依赖版本
     */
    private const val core_ktx_version = "1.7.0"
    private const val appcompat_version = "1.5.1"
    private const val material_version = "1.6.1"
    private const val constraintlayout_version = "2.1.4"
    private const val router_version="2.4.3"
    private const val glide_version="4.12.2"

    const val core = "androidx.core:core-ktx:$core_ktx_version"

    const val mutilx="androidx.multidex:multidex:2.0.1"
    const val appcompat = "androidx.appcompat:appcompat:$appcompat_version"
    const val material = "com.google.android.material:material:$material_version"
    const val constraintlayout =
        "androidx.constraintlayout:constraintlayout:$constraintlayout_version"


    const val mmkv = "com.tencent:mmkv:1.2.13" //com.tencent:mmkv:1.0.22

    const val eventbusCore = "org.greenrobot:eventbus:3.2.0"
    const  val eventbusNote = "org.greenrobot:eventbus-annotation-processor:3.2.0"

    const  val runtime = "androidx.work:work-runtime-ktx:2.7.1"

    const  val routerCore =
        "io.github.didi:drouter-api:${router_version}" //https://github.com/didi/DRouter/wiki

    const  val permissionCore= "com.guolindev.permissionx:permissionx:1.7.1"  //已适配Android13

    const val buglySdk="com.tencent.bugly:crashreport:4.1.9"

    //图片处理
    const val glide = "com.github.bumptech.glide:glide:$glide_version"
    const val glideCompiler = "com.github.bumptech.glide:compiler:$glide_version"
    const val glideTransformations = "jp.wasabeef:glide-transformations:4.3.0"
    const val annotation = "androidx.annotation:annotation:1.5.0" //androidx注解迁移

    //界面view  列表适配器 https://github.com/CymChad/BaseRecyclerViewAdapterHelper
    const val rvHelper = "io.github.cymchad:BaseRecyclerViewAdapterHelper:4.0.0-beta02"
    const val recyclerView="androidx.recyclerview:recyclerview:1.3.0-rc01"
    const val swipeRefresh= "androidx.swiperefreshlayout:swiperefreshlayout:1.1.0"
}

//单元测试
object TestManager {
    const val junit = "junit:junit:4.13.2"
    const val junitX = "androidx.test.ext:junit:1.1.4"
    const val espresso = "androidx.test.espresso:espresso-core:3.5.0"
}

