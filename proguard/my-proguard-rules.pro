# 保留整个项目的所有类和接口
-keep class io.github.lazyimmortal.sesame.** { *; }
# 保留 TypeReference 及其子类
#noinspection ShrinkerUnresolvedReference
-keep class com.fasterxml.jackson.core.type.TypeReference { *; }
-keep class * extends com.fasterxml.jackson.core.type.TypeReference { *; }
# 保留异常堆栈的源文件名与行号，方便排错
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# 保留 JSON 反序列化所需的字段
-keepclassmembers class * {
    #noinspection ShrinkerUnresolvedReference
    @com.fasterxml.jackson.annotation.JsonProperty <fields>;
}

#-------------- okhttp3 -------------
# OkHttp3
# https://github.com/square/okhttp
# okhttp
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.squareup.okhttp.* { *; }
-keep interface com.squareup.okhttp.** { *; }
-dontwarn com.squareup.okhttp.**

# okhttp 3
-keepattributes Signature
-keepattributes *Annotation*
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.**

# Okio
-dontwarn com.squareup.**
-dontwarn okio.**
-keep public class org.codehaus.* { *; }
-keep public class java.nio.* { *; }

#------------------

-obfuscationdictionary proguard-sxbk.txt
-classobfuscationdictionary proguard-sxbk.txt
-packageobfuscationdictionary proguard-sxbk.txt