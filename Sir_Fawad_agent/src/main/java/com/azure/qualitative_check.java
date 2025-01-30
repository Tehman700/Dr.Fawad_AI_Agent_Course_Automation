package com.azure;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import io.github.cdimascio.dotenv.Dotenv;

import java.util.List;

class DirectoryNode {
    String name;
    List<String> files;
    List<DirectoryNode> subdirectories;

    public DirectoryNode(String name) {
        this.name = name;
        this.files = new ArrayList<>();
        this.subdirectories = new ArrayList<>();
    }
}






class DirectoryParser {

    public static DirectoryNode parseDirectory(File dir) {
        DirectoryNode node = new DirectoryNode(dir.getName());
        File[] contents = dir.listFiles();

        if (contents != null) {
            for (File file : contents) {
                if (file.isFile()) {
                    node.files.add(file.getName());
                } else if (file.isDirectory()) {
                    node.subdirectories.add(parseDirectory(file));
                }
            }
        }

        return node;
    }

    public static List<String> getFoldersWithFilesOnly(DirectoryNode node) {
        List<String> result = new ArrayList<>();
        if (!node.files.isEmpty() && node.subdirectories.isEmpty()) {
            // Only include folders that have files and no subdirectories
            result.add(node.name);
        }


        // RECURSIVE FUNTION TO BAAR BAAR CALL RHA HAO
        for (DirectoryNode subDir : node.subdirectories) {
            result.addAll(getFoldersWithFilesOnly(subDir));
        }

        // ARRAY THAT CONTAINS ALLL FOLDEERS  WHICH HAVE ILES IN THEM
        return result;
    }

    public static void displayFileFullPath(File dir) {
        File[] contents = dir.listFiles();

        if (contents != null) {
            for (File file : contents) {
                if (file.isFile()) {
                    System.out.println("File: " + file.getAbsolutePath());
                } else if (file.isDirectory()) {
                    displayFileFullPath(file);
                }
            }
        }
    }


    /// for folder check/////////////////

    public static String sendChatCompletionRequest(String folderNames) throws IOException {

        Dotenv dotenv = Dotenv.load();

        String apiKey = dotenv.get("API_KEY");
        // CONTENTS THAT WE HAVE TO BUILD OR REQUEST OR METADATA WHICH IS GIVEN TO OPENAI
        // Replace with your OpenAI API key

        // Prepare the API endpoint
        URL url = new URL("https://api.openai.com/v1/chat/completions");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        // Configure the request
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Authorization", "Bearer " + apiKey);
        connection.setDoOutput(true);

        // Validate the input parameter
        if (folderNames == null || folderNames.isEmpty()) {
            throw new IllegalArgumentException("Folder names cannot be null or empty.");
        }

        String payload = String.format("""
        {
            "model": "gpt-4o-mini",
            "messages": [
                {
                    "role": "system",
                    "content": "Example: If the input list is mids,finals,quizzes,extra materials, and the task is to filter for folders related to exams or assignments, the output format should be mids,finals,quizzes"
                },
                {
                    "role": "user",
                    "content": "From the following list of folder names: %s, return only those names exactly as they are that relate to quizzes, assignments, labs, mid exams, or final exams. The response should be a comma-separated list of exact folder names, with no extra text."
                }
            ]
        }
        """, folderNames);


        // Write the payload to the connectioN
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = payload.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        // Read the response
        StringBuilder response = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
        }

        // Parse the JSON response
        JSONObject jsonResponse = new JSONObject(response.toString());
        JSONArray choices = jsonResponse.getJSONArray("choices");
        String assistantResponse = choices.getJSONObject(0).getJSONObject("message").getString("content");

        // Return the extracted response
        return assistantResponse;
    }


    /// ////////for file filteration////////////////////


    public static String fileFilteration(String filenames) throws IOException {
        // Replace with your OpenAI API key
        Dotenv dotenv = Dotenv.load();

        String apiKey = dotenv.get("API_KEY");
        // Validate the input parameter
        if (filenames == null || filenames.isEmpty()) {
            throw new IllegalArgumentException("File names cannot be null or empty.");
        }

        // Prepare the API endpoint
        URL url = new URL("https://api.openai.com/v1/chat/completions");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        // Configure the request
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Authorization", "Bearer " + apiKey);
        connection.setDoOutput(true);

        // Prepare the JSON payload
        String prompt = String.format("""
                You are given a list of file names in CSV format: %s. 
                Identify and return exactly two files, separated by a comma, with their full names as provided in the list. 
                The first file must be the question file, and the second must be the solution file. 
                Return only these two file names, with no additional text, punctuation, or symbols.
                donot include file with keywords  "best,worse,average,sample".

                """, filenames);


        String payload = new JSONObject()
                .put("model", "gpt-4o-mini")
                .put("messages", new JSONArray()
                        .put(new JSONObject().put("role", "system").put("content", "You are a helpful assistant."))
                        .put(new JSONObject().put("role", "user").put("content", prompt))
                ).toString();

        // Write the payload to the connection
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = payload.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        // Check for errors in the response
        int responseCode = connection.getResponseCode();
        if (responseCode != 200) {
            throw new IOException("Server returned HTTP response code: " + responseCode + " for URL: " + url);
        }

        // Read the response
        StringBuilder response = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
        }

        // Parse the JSON response
        JSONObject jsonResponse = new JSONObject(response.toString());
        JSONArray choices = jsonResponse.getJSONArray("choices");
        String assistantResponse = choices.getJSONObject(0).getJSONObject("message").getString("content");

        // Return the extracted response
        return assistantResponse;
    }



    // VALIDATION FUNCTION TO CHECK EITHER THE SPECIFIC FILDER IS EMPTY OR NOT IF EMTRY THEN DON'T GO FURTHER OTHERWISE GO ON TO FIND TARGET FUNTION THAT IS RETURNING FILES FROM THE FOLDER
     public static String getFilesInFolder(String rootDirectory, String targetName) {
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

                        // THIS STRING IS BASCIALLY ALL THE FILES IN THE FOLDER
                        System.out.println("/n/n/n/n/n/n/n ***************************************************" +found);
                        return found;
                    }
                }


                // THIS IS THE PORTION WHICH WILL RETURN ABSOLUTE PATH OF THE FILES IF THE FODLER IS NOT GIVEN
            } else if (file.isFile() && file.getName().equalsIgnoreCase(targetName)) {
                // Found target file
                return file.getAbsolutePath();
            }
        }

        return null; // Target not found
    }

    // Lab Assignment 1.docx


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



    public static ArrayList<String> process_filteration(String rootpath) throws IOException {

        String rootPath = rootpath; // Replace with the root directory path
        File rootDir = new File(rootPath);
        ArrayList<String> arraylist_filtered_Files = new ArrayList<>();

        if (rootDir.exists() && rootDir.isDirectory()) {
            DirectoryNode root = parseDirectory(rootDir);

//            System.out.println("Directory Structure:");
//            printDirectory(root, "");

//            System.out.println("\nFolders containing only files:");
            List<String> foldersWithFilesOnly = getFoldersWithFilesOnly(root);

//            String folderWithFilesOnly = String.join(", ", foldersWithFilesOnly);
//            System.out.println(folderWithFilesOnly);


            String folder_WITH_FILES = "";

            for (String f : foldersWithFilesOnly) {

                //
                folder_WITH_FILES += f+",";


            }

            // 1. FILTERATION LAYER 01
            // ONLY RETURN FOLDERS IN THE WHOLEN DIRECTORY THAT HAAS JUST FILES
//            System.out.println(folder_WITH_FILES);


//            System.out.println("\nFull directory of each file:");
//            displayFileFullPath(rootDir);

            System.out.println("\n");


            String Valid_folders = sendChatCompletionRequest(folder_WITH_FILES);


            String[] words = Valid_folders.split(",");

            System.out.println("\n" +"Required Folders in this directory : "+ Valid_folders);


            //System.out.println(test);

//            String test= "Mid";
//
//
//
//            String[] words=test.split(",");


            //Array_List////



            String filtered_files;
            for (String S : words) {

                // S is the folder that we are requiring by filtering like mids or finals
                System.out.println("\n" + S);





                String rootDirectory = rootPath;
                String targetFolder = S.trim();
                // THIS IS THE STRING THAT HAS ALL THE FILES IN THE FOLDER WHICH IS GENUINE TO CHECK
                String csvResult = getFilesInFolder(rootDirectory, targetFolder);
                if (csvResult != null) {
                    System.out.println("Displaying all files in this Folder : " + csvResult);


                    // THIS IS THE STRING THAT CONTAINS ONLY FILES THAT ARE REQUIRED
                    filtered_files = fileFilteration(csvResult);

                    arraylist_filtered_Files.add(filtered_files);



                    System.out.println("Required/Filtered files : " + filtered_files);

                } else {
                    System.out.println("Folder not found or no files in the folder.");
                }
            }


        }

        else {
            System.out.println("Invalid directory path: " + rootPath);
        }


        for(int i =0; i< arraylist_filtered_Files.size(); i++){
            System.out.println(arraylist_filtered_Files.get(i) + "            xonasifaf        ");
        }



        return arraylist_filtered_Files;

    }









    public static void main(String[] args) throws IOException {
        String rootPath = "D:\\Dr.Fawad Sample\\first\\Object Oriented Programming\\Object Oriented Programming (Theory)"; // Replace with the root directory path

       ArrayList<String>filtered_Array= process_filteration(rootPath);

//        System.out.println("files\n"+filtered_Array);



        for(String S: filtered_Array){


            String pair_files=S;

            String[] Pair_file_array=pair_files.split(",");


            String first_file_name = Pair_file_array[0].trim();
            String second_file_name = Pair_file_array[1].trim();




            System.out.println("File 1 :"+ getFilesInFolder(rootPath,first_file_name));
            System.out.println("File 2 :"+ getFilesInFolder(rootPath,second_file_name));

        }






    }

}