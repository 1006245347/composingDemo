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

    const val core = "androidx.core:core-ktx:$core_ktx_version"

    const val mutilx="androidx.multidex:multidex:2.0.1"
    const val appcompat = "androidx.appcompat:appcompat:$appcompat_version"
    const val material = "com.google.android.material:material:$material_version"
    const val constraintlayout =
        "androidx.constraintlayout:constraintlayout:$constraintlayout_version"


    val mmkv = "com.tencent:mmkv:1.2.13" //com.tencent:mmkv:1.0.22

    val eventbusCore = "org.greenrobot:eventbus:3.2.0"
    val eventbusNote = "org.greenrobot:eventbus-annotation-processor:3.2.0"

    val runtime = "androidx.work:work-runtime-ktx:2.7.1"

    val routerCore =
        "io.github.didi:drouter-api:${router_version}" //https://github.com/didi/DRouter/wiki

    val permissionCore= "com.guolindev.permissionx:permissionx:1.7.1"  //已适配Android13

    val buglySdk="com.tencent.bugly:crashreport:4.1.9"
}

object TestManager {
    const val junit = "junit:junit:4.13.2"
    const val junitX = "androidx.test.ext:junit:1.1.4"
    const val espresso = "androidx.test.espresso:espresso-core:3.5.0"

}

