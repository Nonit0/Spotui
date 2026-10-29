# R8 for the release build (IT-54, S4). Shrink and optimize, but keep class and member names:
# lots of reflection (NewPipeExtractor/Rhino, serialization, WebView bridges) and readable crash logs.
-dontobfuscate
-keepattributes SourceFile,LineNumberTable

# NewPipeExtractor runs YouTube's player JS with Rhino, which loads its own classes by name.
-keep class org.mozilla.javascript.** { *; }
-keep class org.mozilla.classfile.ClassFileWriter
-dontwarn org.mozilla.javascript.tools.**
# Time-ago patterns are looked up by class name per language.
-keep class org.schabi.newpipe.extractor.timeago.patterns.** { *; }
# Desktop-JVM APIs referenced by Rhino's script engine and invokedynamic optimizer, absent on Android.
-dontwarn java.beans.**
-dontwarn javax.script.**
-dontwarn jdk.dynalink.**
