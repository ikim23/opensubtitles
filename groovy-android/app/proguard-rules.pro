  -dontobfuscate
  -keep class org.codehaus.groovy.vmplugin.**
  -keep class org.codehaus.groovy.runtime.dgm*
  -keepclassmembers class org.codehaus.groovy.runtime.dgm* {
      *;
  }

  -keepclassmembers class ** implements org.codehaus.groovy.runtime.GeneratedClosure {
      *;
  }

  -dontwarn org.codehaus.groovy.**
  -dontwarn groovy**
