package com.alexkrug.files_cloud_storage.utils;

public class PathResolver {
    public static String getParentPath(String path) {
        int index = path.lastIndexOf("/") + 1;
        return path.substring(0, index);
    }

    public static String getFileName(String path) {
        int index = path.lastIndexOf("/") + 1;
        return path.substring(index);
    }

    public static String getPathWithoutPrefix(String path) {
        int index = path.indexOf("/") + 1;
        return path.substring(index);
    }

    public static String getPathWithoutSuffix(String path) {
        if (!path.endsWith("/")) {
            return path;
        }
        int index = path.lastIndexOf("/");
        return path.substring(0, index);
    }

    public static int getPrefixLength(String path) {
        int index = path.indexOf("/");
        return path.substring(0, index + 1).length();
    }
}
