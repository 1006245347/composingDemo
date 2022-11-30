package cn.hwj.buildplugin

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * @author by jason-何伟杰，2022/11/29
 * des:现在我的新版gradle 写法 不需要将插件库放到项目根目录外，
 * 并且某些脚本都是按照新的格式完成，都是试验成功了!
 */
class VersionPlugin : Plugin<Project> {
    override fun apply(p0: Project) {

    }
}