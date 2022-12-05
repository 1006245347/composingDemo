 -dontoptimize
    -dontpreverify

    -dontwarn cn.jpush.**
    -keep class cn.jpush.** { *; }
    -keep class * extends cn.jpush.android.service.JPushMessageReceiver { *; }

    -dontwarn cn.jiguang.**
    -keep class cn.jiguang.** { *; }