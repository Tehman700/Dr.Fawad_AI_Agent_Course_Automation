package com.azure;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import io.github.cdimascio.dotenv.Dotenv;

class VectorStoreFileDeleter {

    static Dotenv dotenv = Dotenv.load();

    private static final String API_KEY = dotenv.get("API_KEY");
    public static void main(String[] args) {

        VectorStoreFileDeleter delete=new VectorStoreFileDeleter();

        String vectorStoreId = "vs_p0tRLzmvpGhtMCim0PuAxgDM"; // Replace with your vector store ID
//        String fileId = "file-7Y3hU99wbiEPdB1rgyiqTT";       // Replace with your file ID


        com.azure.OpenAIFileUpload oop=new OpenAIFileUpload();
        String fileId="file-YBLE2sUsD79X3Kpyed7yrj";




        try {
            delete.deleteVectorStoreFile(vectorStoreId, fileId);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteVectorStoreFile(String vectorStoreId, String fileId) throws Exception {
        String endpoint = String.format("https://api.openai.com/v1/vector_stores/%s/files/%s", vectorStoreId, fileId);

        URL url = new URL(endpoint);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("DELETE");
        connection.setRequestProperty("Authorization", "Bearer " + API_KEY);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("OpenAI-Beta", "assistants=v2");

        int responseCode = connection.getResponseCode();

        BufferedReader reader;
        if (responseCode == HttpURLConnection.HTTP_OK) {
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        } else {
            reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
        }

        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = reader.readLine()) != null) {
            response.append(inputLine);
        }
        reader.close();

        System.out.println("Response Code: " + responseCode);
        System.out.println("Response Body: " + response.toString());


        deleteFile(fileId);

        connection.disconnect();

    }





         //to delete file from storage files///

    public void deleteFile(String fileId) throws Exception {
        // Construct the endpoint URL using the file ID
        String endpoint = String.format("https://api.openai.com/v1/files/%s", fileId);

        URL url = new URL(endpoint);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        // Set request method to DELETE
        connection.setRequestMethod("DELETE");

        // Add the required headers
        connection.setRequestProperty("Authorization", "Bearer " + API_KEY);

        // Get the response code
        int responseCode = connection.getResponseCode();

        // Read the response
        BufferedReader reader;
        if (responseCode == HttpURLConnection.HTTP_OK) {
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        } else {
            reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
        }

        StringBuilder response = new StringBuilder();
        String inputLine;
        while ((inputLine = reader.readLine()) != null) {
            response.append(inputLine);
        }
        reader.close();

        // Print the response
        System.out.println("Response Code: " + responseCode);
        System.out.println("Response Body: " + response.toString());

        connection.disconnect();
    }







}
