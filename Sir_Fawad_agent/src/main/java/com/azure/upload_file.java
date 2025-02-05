package com.azure;

import okhttp3.*;
import com.google.gson.*;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.io.*;
import io.github.cdimascio.dotenv.Dotenv;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import io.github.cdimascio.dotenv.Dotenv;

import java.util.List;


class OpenAIFileUpload {
    AutoShowingDialog objjj = new AutoShowingDialog();

    private static final String FILE_PATH = "file_ids.txt";
    static Dotenv dotenv = Dotenv.load();

    private static final String API_KEY = dotenv.get("API_KEY");  // Replace with your actual OpenAI API key
    private static final String BASE_URL = "https://api.openai.com/v1/";

    public  String fileId="";
    // Method to upload a file to OpenAI and get the file_id
    public  String uploadFile(File file) throws IOException {
        OkHttpClient client = new OkHttpClient();

        // Build the request body for file upload
        RequestBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", file.getName(),
                        RequestBody.create(MediaType.get("application/pdf"), file))
                .addFormDataPart("purpose", "assistants")  // Add the purpose parameter
                .build();

        // Build the file upload request
        Request request = new Request.Builder()
                .url(BASE_URL + "files")
                .post(body)
                .header("Authorization", "Bearer " + API_KEY)
                .build();

        // Send the file upload request and get the response
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                String responseBody = response.body().string();
                JsonObject jsonResponse = JsonParser.parseString(responseBody).getAsJsonObject();
                 fileId = jsonResponse.get("id").getAsString();
                objjj.showAutoCloseDialog("Important Task!! File Uploaded Successfully with ID: " +fileId, "File Uploaded Message", JOptionPane.INFORMATION_MESSAGE);
                return fileId;
            } else {
                objjj.showAutoCloseDialog("Failed to upload file: " + response.body().string(), "File Uploaded Failed", JOptionPane.INFORMATION_MESSAGE);
                throw new IOException("Failed to upload file: " + response.body().string());
            }
        }
    }


    public String associate_with_assistant(File file) throws IOException {


        OpenAIFileUpload oop =new OpenAIFileUpload();


//        File file = new File("D:\\Documents\\3rd Semmester\\Object Oriented Programming\\Assignements\\Assignment 01\\Sample Papers.pdf");

        String apiKey = API_KEY ;
        String vectorStoreId = "vs_p0tRLzmvpGhtMCim0PuAxgDM"; // Replace with your vector store ID
         fileId =oop.uploadFile(file) ;      // Replace with the file ID to attach

        if(fileId ==""){

            return "File does not uploaded";

        }


        String urlString = "https://api.openai.com/v1/vector_stores/" + vectorStoreId + "/files";
        String requestBody = "{ \"file_id\": \"" + fileId + "\" }";

        try {
            // Create a URL object
            URL url = new URL(urlString);

            // Open the connection
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Set the request method to POST
            connection.setRequestMethod("POST");

            // Set headers
            connection.setRequestProperty("Authorization", "Bearer " + apiKey);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("OpenAI-Beta", "assistants=v2");

            // Enable output for the request body
            connection.setDoOutput(true);

            // Write the request body
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = requestBody.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // Get the response code
            int responseCode = connection.getResponseCode();
            System.out.println("Response Code: " + responseCode);

            // Read the response
            if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED) {
                // Read the response (same as in the previous example)
                try (var in = connection.getInputStream();
                     var reader = new java.io.InputStreamReader(in);
                     var bufferedReader = new java.io.BufferedReader(reader)) {

                    String line;
                    StringBuilder response = new StringBuilder();
                    while ((line = bufferedReader.readLine()) != null) {
                        response.append(line);
                    }
                    objjj.showAutoCloseDialog("Response: " + response.toString(), "File Response Generated", JOptionPane.INFORMATION_MESSAGE);

                    System.out.println("Response: " + response.toString());
                }
            } else {
                objjj.showAutoCloseDialog("Error: !!!!" + connection.getResponseMessage(), "File Response Error", JOptionPane.INFORMATION_MESSAGE);
                System.out.println("Error: " + connection.getResponseMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        // THIS PORTION IS DONE BY TEHMAN ON 28-01-2023 3:01AM, THIS IS DONE FOR HANDLING BREAKING ISSUES IF INTERNET IS SLOW AND FILES WERE NOT DELETED PREVIOUSLY

        try {
            addFileIDtoFile(fileId);
        } catch (IOException e) {
            System.out.println("An error occurred: " + e.getMessage());
        }
        return fileId;
    }


    private static void addFileIDtoFile(String str) throws IOException {
        try (FileWriter writer = new FileWriter(FILE_PATH, true);
             BufferedWriter bufferedWriter = new BufferedWriter(writer)) {
            bufferedWriter.write(str);
            bufferedWriter.newLine(); // Add a new line after the string a fileid
        }
    }




    public static void main(String[] args) throws IOException {




    }
}

