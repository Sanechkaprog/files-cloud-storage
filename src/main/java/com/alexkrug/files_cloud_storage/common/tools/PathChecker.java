package com.alexkrug.files_cloud_storage.common.tools;

import java.util.Arrays;

public class PathChecker {
    public static boolean checkPath(String path) {
        return (path == null ||
                path.isBlank() ||
                path.startsWith("/") ||
                path.contains("\\") ||
                Arrays.stream(path.split("/"))
                        .anyMatch(part -> part.equals(".") || part.equals("..")));
    }
}