# Frequently Asked Questions

## Can't see the crop button. So, can't crop the image.
Does your app theme has no action bar? If yes, you should add this to your manifest
```kt
<activity android:name="com.canhub.cropper.CropImageActivity"
  android:theme="@style/Base.Theme.AppCompat"/>
```

## Can I use this library in Java project?
Yes! Check [Using the library in Java](
.documentation/java_usage.md)

## Bad class file error : class file has wrong version xx.0, should be yy.0
It means your Java runtime version is different from your compiler version (javac).
To simply solve it, just advance your JVM version to 11

- Select "File" -> "Project Structure".
- Under "Project Settings" select "Project"
- From there you can select the "Project SDK".
- But if you don't want to change the Java runtime version, then do the following steps:
`JAVA_HOME= "your jdk v11 folder path", to make sure jdk is also v11 and use java -version and javac -version again to ensure it`

[more information](https://stackoverflow.com/a/4692743/3117650)

## Why Java 11 is needed since version 3.3.4?
We update to Java 11 so we could update Gradle. 
And is always good to keep the most updated versions available. 
Those who do not update, keep using old versions of the library, but those who update can get the benefits of the latest versions.

