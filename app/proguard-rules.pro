# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\D\androidsdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
#指定代码的压缩级别
-optimizationpasses 5
#包名不混合大小写
-dontusemixedcaseclassnames
#忽略警告
#-ignorewarning
-dontpreverify
-verbose
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*


#所有public protected的方法名不进行混淆
-keep public abstract interface com.funnco.funnco.*{
    public protected <methods>;
    }
#对实现了Parcelable接口的所有类的类名不进行混淆，
#对其成员变量为Parcelable$Creator类型的成员变量的变量名不进行混淆
 -keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}
-keep class * extends java.lang.annotation.Annotation { *; }
#不进行混淆类名的类，保持其原类名和包名
# 对系统四大组件不混淆，应为需要在清单文件中配置
#注解保护
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgent
-keep public class * extends android.preference.Preference
-keep public class * extends android.support.v4.app.Fragment
-keep public class * extends android.app.Fragment
-keep public class com.android.vending.licensing.ILicensingService
-keep public class * extends android.view.View{
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public void set*(...);
    public void get*(...);
}
#fasterjson
-keep public class * implements java.io.Serializable {
        public *;
}
-dontwarn android.support.**
-dontwarn com.alibaba.fastjson.**
-dontskipnonpubliclibraryclassmembers
-dontskipnonpubliclibraryclasses
-keep class com.alibaba.fastjson.** { *; }
-keepclassmembers class * {
public <methods>;
}
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
    public <fields>;
}
-keepattributes Signture


#xUtils
-keep class com.lidroid.xutils.** {*;}

#避免第三方jar的混淆

#如果引用了v4或者v7包
-dontwarn android.support.**
#保持 native 方法不被混淆
-keepclasseswithmembernames class * {
    native <methods>;
}
#不混淆资源类
-keepclassmembers class **.R$* {
    public static <fields>;
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context);
}
#保持自定义控件类不被混淆
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}
#保持自定义控件类不被混淆
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
#保持自定义控件类不被混淆
-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
   public void *(android.widget.TextView);
   public void *(android.widget.RelativeLayout);
   public void *(android.widget.LinearLayout);
   public void *(android.widget.FrameLayout);
   public void *(android.graphics.drawable.Drawable);
   public void *(android.widget.ImageView);
   public void *(android.widget.EditText);
   public void *(android.graphics.drawable.AnimationDrawable);
   public void *(android.widget.ListView);
   public void *(android.support.v4.view.ViewPager);
   public void *(android.widget.ScrollView);
   public void *(android.app.Dialog);
   public void *(com.funnco.funnco.view.SwipeLayout);
   public void *(android.widget.GridView);
}

-dontwarn com.android.volley.**
-dontwarn com.lidroid.xutils.**
-dontwarn com.nostra13.universalimageloader.**
-dontwarn org.apache.http.entity.mime.**

-dontwarn android.support.v4.**
-dontwarn org.apache.commons.net.**
-dontwarn com.tencent.**

#-libraryjars libs/httpmime-4.1.3.jar

-keepattributes Exceptions,InnerClasses,Signature
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable

-keep class com.parse.*{ *; }
-dontwarn com.parse.**
-dontwarn com.squareup.picasso.**

#umeng 统计
-keepclassmembers class * {
   public <init>(org.json.JSONObject);
}
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
#umeng 分享
-dontshrink
-dontoptimize
-dontwarn com.google.android.maps.**
-dontwarn android.webkit.WebView
-dontwarn com.umeng.**
-dontwarn com.tencent.weibo.sdk.**
-keepattributes Exceptions,InnerClasses,Signature
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable
-keep public interface com.tencent.**
-keep public interface com.umeng.socialize.**
-keep public interface com.umeng.socialize.sensor.**
-keep public interface com.umeng.scrshot.**
-keep public class com.umeng.socialize.* {*;}
-keep public class javax.**
-keep public class android.webkit.**
-keep class com.umeng.scrshot.**
-keep public class com.tencent.** {*;}
-keep class com.umeng.socialize.sensor.**
-keep class com.tencent.mm.sdk.modelmsg.WXMediaMessage {*;}
-keep class com.tencent.mm.sdk.modelmsg.** implements com.tencent.mm.sdk.modelmsg.WXMediaMessage$IMediaObject {*;}
-keep class im.yixin.sdk.api.YXMessage {*;}
-keep class im.yixin.sdk.api.** implements im.yixin.sdk.api.YXMessage$YXMessageData{*;}
-keep public class com.funnco.funnco.R$*{
    public static final int *;
}