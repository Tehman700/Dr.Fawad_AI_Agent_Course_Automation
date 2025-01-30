package com.azure;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

 class AppendToFiles {
    public static void main(String[] args) {
        // Specify the paths and names of the files
        String markdownFilePath = "test.md"; // Markdown file
        String textFilePath = "test.txt"; // Text file
        String newContent = "## Appended Content\nThis content is appended to the file.\n";

        // Append the content to the Markdown file and Text file
        appendToFile(markdownFilePath, newContent, "Markdown");
        appendToFile(textFilePath, newContent, "Text");
    }

    private static void appendToFile(String filePath, String content, String fileType) {
        // Append content to the specified file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(new File(filePath), true))) {
            writer.write(content);
            writer.newLine(); // Ensure new content is on a new line
            Thread.sleep(1000);

            System.out.println(fileType + " file updated successfully at: " + new File(filePath).getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Error appending content to the " + fileType + " file: " + e.getMessage());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


    }
}
