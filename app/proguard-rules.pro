# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/mike/Library/Android/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

-dontwarn java.awt.geom.Rectangle2D

# Crashlytics requires these lines to provide readable crash reports:
# https://firebase.google.com/docs/crashlytics/get-deobfuscated-reports?hl=en&platform=android#config-r8-proguard-dexguard
-keepattributes SourceFile,LineNumberTable        # Keep file names and line numbers.
-keep public class * extends java.lang.Exception  # Optional: Keep custom exceptions.
