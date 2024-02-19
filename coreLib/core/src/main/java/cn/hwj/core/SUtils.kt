package cn.hwj.core

class SUtils {

    //非静态
    fun do1(){}
    companion object{

        //静态函数
        @JvmStatic
        fun doA(){}

        //非静态，伴生类
        fun doB(){}
    }
}


object U2{

    //单例，非静态函数
    fun do1(){}
}