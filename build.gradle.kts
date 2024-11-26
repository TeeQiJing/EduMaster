buildscript {
    dependencies {
        classpath("com.google.gms:google-services:4.4.2")
    }
}
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
//    Default 8.7.1
    id("com.android.application") version "8.2.2" apply false
    id("com.google.gms.google-services") version "4.4.2" apply false
}