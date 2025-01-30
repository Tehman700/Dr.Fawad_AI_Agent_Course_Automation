package com.azure;

import java.io.File;

class FolderSearch {
    public static void main(String[] args) {
        String rootDirectory = "D:\\Documents\\Sample Course Folder\\Object Oriented Programming\\Object Oriented Programming (Theory)";
        String targetName = "Assignment 1"; // Could be a file or folder

        String result = searchTarget(rootDirectory, targetName);
        if (result != null) {
            System.out.println(result);
        } else {
            System.out.println("Target not found.");
        }
    }

    public static String searchTarget(String rootDirectory, String targetName) {
        File rootDir = new File(rootDirectory);
        if (!rootDir.exists() || !rootDir.isDirectory()) {
            System.out.println("Invalid root directory.");
            return null;
        }

        return findTarget(rootDir, targetName);
    }

    public static String findTarget(File directory, String targetName) {
        File[] files = directory.listFiles();

        if (files == null || files.length == 0) {
            // Return null if the folder is empty or no files are listed
            return null;
        }

        for (File file : files) {
            if (file.isDirectory()) {
                if (file.getName().equalsIgnoreCase(targetName)) {
                    // Found target folder, return all files inside as CSV
                    return getFilesInFolder(file);
                } else {
                    // Recursively search in subdirectories
                    String found = findTarget(file, targetName);
                    if (found != null) {
                        return found;
                    }
                }
            } else if (file.isFile() && file.getName().equalsIgnoreCase(targetName)) {
                // Found target file
                return file.getAbsolutePath();
            }
        }

        return null; // Target not found
    }

    public static String getFilesInFolder(File folder) {
        File[] files = folder.listFiles();

        if (files == null || files.length == 0) {
            return folder.getAbsolutePath();
        }

        StringBuilder csvBuilder = new StringBuilder();
        for (File file : files) {
            if (file.isFile()) {
                csvBuilder.append(file.getName()).append(",");
            }
        }

        // Remove trailing comma
        if (csvBuilder.length() > 0) {
            csvBuilder.setLength(csvBuilder.length() - 1);
        }

        return csvBuilder.toString();
    }
}
