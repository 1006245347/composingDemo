package cn.hwj.core.global

/**
 * @author by jason-何伟杰，2023/1/16
 * des: 组件间数据通信方式
 * 1.全局共享数据下沉到基础库中
 * 2.用 EventBus 时间响应
 */
data class UserBean (var name:String,var phone:String)