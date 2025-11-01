# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile


-keep class com.epay.sbi.android.interfaces.TransactionResultListener { *; }

#-keep class com.epay.sbi.android.model.PaymentResponseModel { *; }
#-keep class com.epay.sbi.android.model.ThemeModel { *; }
#-keep class com.epay.sbi.android.model.LanguageStrings { *; }
#-keep class com.epay.sbi.android.model.Translations { *; }
#-keep class com.epay.sbi.android.model.AlertData { *; }
#-keep class com.epay.sbi.android.model.UpiData { *; }

-keep class com.epay.sbi.android.model.** { *; }

-keepclassmembers class com.epay.sbi.android.model.**{
<fields>;
<methods>;
}



#-keepclasseswithmembers class * {
#  public void initiateTransaction(...);
#}

# Prevent obfuscation of TransactionActivity class
-keep class com.epay.sbi.android.ui.TransactionActivity { *; }

# Prevent obfuscation of the companion object of TransactionActivity
-keepclassmembers class com.epay.sbi.android.ui.TransactionActivity$Companion { *; }

# Ensure that the static companion object field is kept intact
-keep class com.epay.sbi.android.ui.TransactionActivity$Companion { public static *; }
