package com.halohoop.andembed
import org.gradle.api.Project;
import org.gradle.api.provider.Property;

class EmbedGroup {
    Property<String> packageName;
    Property<String> versionName;

    EmbedGroup(Project project) {
        packageName = project.getObjects().property(String.class);
        versionName = project.getObjects().property(String.class);
    }

    Property<String> getPackageName() {
        return packageName
    }

    Property<String> getVersionName() {
        return versionName
    }
}