------------

|作者|何伟杰|
|---|---
|邮箱| zhheweijie@foxmail.com
|备注|安卓 小程序

## 分支管理

- master 线上发布环境代码版本，不要随意合并该处代码
- dev 业务功能迭代开发，请不要push bug的代码

## kotlin+mmvm+协程+组件化

|ip环境|自动打包渠道|ip地址|
|---|---|---
|生成环境|greePadpro|https://venus |
|测试环境|greePaddev|http://134. |
|质控部测试|greePadque|http: |

## 路由组件 滴滴[](https://github.com/didi/DRouter/wiki)

架构说明：...

## 配置
JDK 11 
android 版本 12 File->Settings->Android API 32 SDK 32.0.0 
compileSdkVersion = 33
buildToolsVersion = "33.0.0"
targetSdkVersion = 33

Kotlin版本 kotlin = "1.6.10"

## build.gradle 文件
{ 
   ... 
   dependencies { 
        classpath 'com.android.tools.build:gradle:7.1.2' 
   ...
    }
   ... 
}

## gradle-wrapper.properties文件
distributionUrl=https\://services.gradle.org/distributions/gradle-7.2-bin.zip