# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\Nicholas\android-sdks/tools/proguard/proguard-android.txt
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
-keepclassmembers class fqcn.of.javascript.interface.for.webview {
   public *;
}

-keepclassmembers class **.R$* {
    public static <fields>;
}

-keep class messagecenter.*
-keep class messagecenter.**
-keep class messagecenter.** {*;}

-keep class com.template.template.*
-keep class com.template.template.**
-keep class com.template.template.** {*;}

-dontwarn net.fortuna.ical4j.**
-dontwarn org.apache.commons.**
-dontwarn edu.emory.mathcs.backport.**
-dontwarn com.squareup.picasso.**
-dontwarn org.apache.http.**

-dontwarn okio.**