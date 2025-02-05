package com.azure;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

import org.apache.pdfbox.multipdf.PDFMergerUtility;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.jar.JarFile;

class GUI {
    private JFrame frame;
    private JTextArea textArea;
    private JScrollPane scrollPane;
    private JButton chooseFileButton, chooseSaveLocationButton, runMainButton, exitButton;
    private JLabel filePathLabel, savePathLabel;
    private String selectedFilePath = ""; // Stores the selected file path
    private String selectedSavePath = ""; // Stores the selected save path for PDF


    public GUI() {
        // Create Frame
        frame = new JFrame("Course Automated Analyzer");
        frame.setSize(900, 550);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Ensure it closes properly
        frame.setLayout(new BorderLayout());

        // Create Text Area with Scroll Pane
        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setFont(new Font("Arial", Font.PLAIN, 14));
        scrollPane = new JScrollPane(textArea);
        frame.add(scrollPane, BorderLayout.CENTER);

        // Create Bottom Panel
        JPanel panel = new JPanel();
        frame.add(panel, BorderLayout.SOUTH);

        // Create File Path Label
        filePathLabel = new JLabel("No folder selected");
        filePathLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        frame.add(filePathLabel, BorderLayout.NORTH);
        savePathLabel = new JLabel("No save location selected");
        savePathLabel.setFont(new Font("Arial", Font.PLAIN,     20));
        frame.add(savePathLabel, BorderLayout.CENTER);

        // Create FileChooser Button
        chooseFileButton = new JButton("Select Directory");
        chooseFileButton.setFont(new Font("Arial", Font.BOLD, 14));
        // Create Save Location Button
        chooseSaveLocationButton = new JButton("Select Save Location");
        chooseSaveLocationButton.setFont(new Font("Arial", Font.BOLD, 14));

        // Create Run Button
        runMainButton = new JButton("Analyze");
        runMainButton.setFont(new Font("Arial", Font.BOLD, 14));
        runMainButton.setBackground(new Color(100, 149, 237));
        runMainButton.setForeground(Color.WHITE);
        runMainButton.setEnabled(false); // Initially disabled

        // Create Exit Button
        exitButton = new JButton("Click for Stopping after Text File Uploaded Prompt");
        exitButton.setFont(new Font("Arial", Font.BOLD, 12));
        exitButton.setBackground(new Color(220, 20, 60)); // Red color
        exitButton.setForeground(Color.WHITE);
        // Add Action Listener for FileChooser
        chooseFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY); // Allow only folder selection
                fileChooser.setAcceptAllFileFilterUsed(false);

                int returnValue = fileChooser.showOpenDialog(frame);

                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    if (selectedFile != null) {
                        selectedFilePath = selectedFile.getAbsolutePath();
                        filePathLabel.setText("Selected: " + selectedFilePath);
                        runMainButton.setEnabled(true); // Enable run button
                    }
                }
            }
        });
        chooseSaveLocationButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY); // Allow only folder selection
                fileChooser.setAcceptAllFileFilterUsed(false);

                int returnValue = fileChooser.showOpenDialog(frame);

                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    if (selectedFile != null) {
                        selectedSavePath = selectedFile.getAbsolutePath();
                        savePathLabel.setText("Save Location: " + selectedSavePath);
                    }
                }
            }
        });

        // Add Action Listener for Run Button
        runMainButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!selectedFilePath.isEmpty() && !selectedSavePath.isEmpty()) {
                    try {
                        Main.main(new String[]{selectedFilePath, selectedSavePath});
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(frame, "Please select a directory and a save location first.", "Warning", JOptionPane.WARNING_MESSAGE);
                }
            }
        });
        // Add Action Listener for Exit Button
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0); // Terminate the program
            }
        });
        // Add Components to Panel
        panel.add(chooseFileButton);
        panel.add(chooseSaveLocationButton);
        panel.add(runMainButton);
        panel.add(exitButton);

        // Make Frame Visible
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        // Start GUI
        SwingUtilities.invokeLater(() -> new GUI());
    }
}

public class Main {

    String vectorStoreId = "vs_p0tRLzmvpGhtMCim0PuAxgDM"; // Replace with your vector store ID

    // Method to list all files and directories in a given directory
    public static void listAllFilesAndDirectories(String directoryPath) {
        try {
            Files.walkFileTree(Paths.get(directoryPath), new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    System.out.println("File: " + file.toString());
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                    System.out.println("Directory: " + dir.toString());
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to check for specific files in the directory
    public static boolean checkForFiles(String directoryPath, String... fileNames) throws IOException {
        boolean allFilesPresent = true;
        for (String fileName : fileNames) {
            Path filePath = Paths.get(directoryPath, fileName);
            if (!Files.exists(filePath)) {
                System.out.println("File missing: " + fileName);
                allFilesPresent = false;
            }
        }
        return allFilesPresent;
    }

    /// file creation for report/////// // THis portion is used to append to the file that is appended when response is generated
    ///  30-01-2025 at 1:34 PM

    private static void appendToFile(String filePath, String content, String fileType) {
        // Append content to the specified file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(new File(filePath), true))) {
            writer.write(content);
            writer.newLine(); // Ensure new content is on a new line
            System.out.println(fileType + " file updated successfully at: " + new File(filePath).getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Error appending content to the " + fileType + " file: " + e.getMessage());
        }
    }

    private static final String FILE_PATHSS = "file_ids.txt";
    // Method to show auto-closing dialog
    private static void showAutoCloseDialog(String message, String title, int messageType) {
        JOptionPane optionPane = new JOptionPane(message, messageType);
        JDialog dialog = optionPane.createDialog(title);
        Timer timer = new Timer(2000, e -> dialog.dispose());
        timer.setRepeats(false);
        timer.start();
        dialog.setVisible(true);
    }
    // Main method
    public static void main(String[] args) throws Exception {

        String qwer = "Analysis Report.md";
        for (int i = 0; i < 2; i++) {
            try (FileWriter writer = new FileWriter(qwer)) {
                writer.write("");
                showAutoCloseDialog("Analysis Report File Deleted for New Changes.!!! Task 01 Completed", "Deletion Complete", JOptionPane.INFORMATION_MESSAGE);
                System.out.println("Cleared all contents of: " + qwer);

            } catch (IOException e) {
                showAutoCloseDialog("Some Error Occurred while deleting Analysis Report File.!!! Task 01 Exception", "Deletion Error", JOptionPane.ERROR_MESSAGE);
                System.err.println("An error occurred while clearing the file " + qwer);
                e.printStackTrace();
            }

            qwer = "Analysis Report.txt";

        }


        String directoryPath = null;
        String savePath = null; // Add a variable to store the save path
        if (args.length > 0) {
            directoryPath = args[0];
            if (args.length > 1) {
                savePath = args[1]; // Get the save path from the arguments
            }
        } else {
            JOptionPane.showMessageDialog(null, "Please Provide Path for Files. It must be a Folder!!!", "Incomplete Path", JOptionPane.INFORMATION_MESSAGE);
            System.out.println("No File Path Provided");
        }


        Main main = new Main();
        OpenAISetup run_bot = new OpenAISetup();
        OpenAIFileUpload upload = new OpenAIFileUpload();
        VectorStoreFileDeleter delete_file = new VectorStoreFileDeleter();

        // BELOW IS THE IMPORTANT PORTION FOR APPENDING FILE'S IN A FILE AND THEN DELETING AGAIN, HANDLING
        // IF PROGRAM STOPS IN BETWEEN LIKE INTERNET OR BREAKING EXCEPTIONS  **** Changes made on 28-01-2025 at 6:06AM *****
        try {
            // Read all lines from the file
            List<String> lines = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATHSS))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    lines.add(line);
                }
            }

            // Process lines: Remove each line after reading
            while (!lines.isEmpty()) {
                String lineToProcess = lines.get(0);
                System.out.println("YES THERE WAS PREVIOUS FILES IN VECTOR DATABASE!!!!");
                // Show pop-up before deletion
                showAutoCloseDialog("Starting deletion of file: " + lineToProcess, "Deletion Started", JOptionPane.INFORMATION_MESSAGE);
                delete_file.deleteVectorStoreFile(main.vectorStoreId, lineToProcess);

                // Show pop-up after deletion
                showAutoCloseDialog("Successfully deleted file: " + lineToProcess, "Deletion Complete", JOptionPane.INFORMATION_MESSAGE);
                // Remove the line from the list
                lines.remove(0);

                // Write the remaining lines back to the file
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATHSS))) {
                    for (String remainingLine : lines) {
                        writer.write(remainingLine);
                        writer.newLine();
                    }
                }
            }
            System.out.println("Everything is Free in Vector Database");
            // Show pop-up when all files are deleted
            showAutoCloseDialog("All files have been deleted from the vector database.", "Deletion Complete", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            System.err.println("An error occurred: " + e.getMessage());
            // Show pop-up if an error occurs
            JOptionPane.showMessageDialog(null, "An error occurred during deletion: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        com.azure.DirectoryParser directoryParser = new DirectoryParser();

        ArrayList<String> filtered_Array = directoryParser.process_filteration(directoryPath);

        System.out.println("files\n" + filtered_Array);

        String file_1 = "";
        String file_2 = "";

        for (String S : filtered_Array) {

            String pair_files = S;

            String[] Pair_file_array = pair_files.split(",");

            file_1 = directoryParser.getFilesInFolder(directoryPath, Pair_file_array[0].trim());

            file_2 = directoryParser.getFilesInFolder(directoryPath, Pair_file_array[1].trim());

            System.out.println("\nFile_one_path: " + file_1);
            System.out.println("\nfile_two_path" + file_2);

            String fileId_1 = upload.associate_with_assistant(new File(file_1));
            String fileId_2 = upload.associate_with_assistant(new File(file_2));

            run_bot.process_bot();
            String response = "# " + Pair_file_array[0].trim() + "\n\n" + run_bot.contentValue + "\n\n# ---------------------------------------------------------------------\n";
            run_bot.contentValue = "";

            delete_file.deleteVectorStoreFile(main.vectorStoreId, fileId_1);
            delete_file.deleteVectorStoreFile(main.vectorStoreId, fileId_2);

            Thread.sleep(10000);

            // Append content to the specified file
            String markdownFilePath = "Analysis Report.md"; // Markdown file
            String textFilePath = "Analysis Report.txt"; // Text file
            String newContent = response + "\n\n\n";

            // Append the content to the Markdown file and Text file
            appendToFile(markdownFilePath, newContent, "Markdown");
            appendToFile(textFilePath, newContent, "Text");

            /// We also have to empty the text file which contains ID's   changes made on 28-01-2025      6:01 AM by Tehman

            try {
                FileWriter obj = new FileWriter(FILE_PATHSS);
                obj.write("");
                obj.close();
                showAutoCloseDialog("******************  Everything is Cleared in teh ID's File!!! *****************", "Deletion Complete", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                System.out.println("An Error Occurred while Clearing the ID's File, Well System won't Stop!!!" + e.getMessage());
            }

            // In this portion we will transform the MD file to PDF File and then delete all the content of MD File, Implemented by Tehman on 29-01-2025 2:26AM

            MarkdownToPdfConverter objss = new MarkdownToPdfConverter();
            objss.changerss(savePath);

        }

    }
}
