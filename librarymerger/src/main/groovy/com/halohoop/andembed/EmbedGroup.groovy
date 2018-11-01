package com.halohoop.andembed

import org.gradle.api.Project
import org.gradle.api.provider.Property

class EmbedGroup {
    Property<String> packageName
    Property<String> versionName
    Property<String> outputDirPath

    EmbedGroup(Project project) {
        packageName = project.getObjects().property(String.class)
        versionName = project.getObjects().property(String.class)
        outputDirPath = project.getObjects().property(String.class)
        outputDirPath.set(project.buildDir.getAbsolutePath() + File.separator + "outputs" + File.separator + "jar")
    }

    Property<String> getPackageName() {
        return packageName
    }

    Property<String> getVersionName() {
        return versionName
    }

    Property<String> getOutputDirPath() {
        return outputDirPath
    }
}