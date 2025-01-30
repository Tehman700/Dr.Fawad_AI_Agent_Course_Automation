package com.azure;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import org.apache.pdfbox.multipdf.PDFMergerUtility;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;



class MainGUI{

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

    /// file creation for report///////



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

    // Main method
    public static void main(String[] args) throws Exception {


        String qwer = "Analysis Report.md";
        for(int i=0;i<2;i++){
            try (FileWriter writer = new FileWriter(qwer)){
                writer.write("");
                System.out.println("Cleared all contents of: "+ qwer);

            }catch(IOException e){
                System.err.println("An error occurred while clearing the file "+ qwer);
                e.printStackTrace();
            }

            qwer = "Analysis Report.txt";

        }
        String directoryPath = "D:\\Dr.Fawad Sample\\first\\Object Oriented Programming\\Object Oriented Programming (Theory)\\6. Exams\\Mid";
//                                                  /// ///////////////// (File Search LLM)\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
//
        Main main=new Main();
        OpenAISetup run_bot=new OpenAISetup();
        OpenAIFileUpload upload=new OpenAIFileUpload();
        VectorStoreFileDeleter delete_file=new VectorStoreFileDeleter();


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
                delete_file.deleteVectorStoreFile(main.vectorStoreId,lineToProcess);

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
        } catch (IOException e) {
            System.err.println("An error occurred: " + e.getMessage());
        }




        com.azure.DirectoryParser directoryParser=new DirectoryParser();


        ArrayList<String> filtered_Array= directoryParser.process_filteration(directoryPath);



        System.out.println("files\n"+filtered_Array);


         String file_1 = "";
         String file_2="";


        for(String S: filtered_Array) {


//            String test_file_path="C:\\Users\\Syed Ali\\Downloads\\Sir_Fawad_agent\\test.txt";
//            String file_ID_test_file= upload.associate_with_assistant(new File(test_file_path));


            String pair_files = S;

            String[] Pair_file_array = pair_files.split(",");


             file_1 = directoryParser.getFilesInFolder(directoryPath,Pair_file_array[0].trim());


            file_2 = directoryParser.getFilesInFolder(directoryPath,Pair_file_array[1].trim());





            System.out.println("\nFile_one_path: "+ file_1);
            System.out.println("\nfile_two_path"+ file_2);






            String fileId_1= upload.associate_with_assistant(new File(file_1));
            String fileId_2= upload.associate_with_assistant(new File(file_2));





            run_bot.process_bot();
            String response="# "+Pair_file_array[0].trim()+"\n\n"+run_bot.contentValue+"\n\n# ---------------------------------------------------------------------\n";
            run_bot.contentValue="";



            delete_file.deleteVectorStoreFile(main.vectorStoreId, fileId_1);
            delete_file.deleteVectorStoreFile(main.vectorStoreId, fileId_2);


            Thread.sleep(10000);



            // Append content to the specified file
            String markdownFilePath = "Analysis Report.md"; // Markdown file
            String textFilePath = "Analysis Report.txt"; // Text file
            String newContent = response+"\n\n\n";

            // Append the content to the Markdown file and Text file
            appendToFile(markdownFilePath, newContent, "Markdown");
            appendToFile(textFilePath, newContent, "Text");


            /// We also have to empty the text file which contains ID's   changes made on 28-01-2025      6:01 AM by Tehman


            try{
                FileWriter obj = new FileWriter(FILE_PATHSS);
                obj.write("");
                obj.close();

                System.out.println("******************  Everything is Cleared in teh ID's File!!! *****************");
            } catch (IOException e){
                System.out.println("An Error Occurred while Clearing the ID's File, Well System won't Stop!!!" + e.getMessage());
            }




            // In this portion we will transform the MD file to PDF File and then delete all the content of MD File, Implemented by Tehman on 29-01-2025 2:26AM


            MarkdownToPdfConverter objss = new MarkdownToPdfConverter();
            objss.changerss();






        }

    }
}