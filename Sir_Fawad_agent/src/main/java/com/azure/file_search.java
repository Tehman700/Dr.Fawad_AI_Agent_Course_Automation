package com.azure;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import io.github.cdimascio.dotenv.Dotenv;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.io.BufferedWriter;
import java.util.concurrent.CompletableFuture;


class OpenAISetup {
    private static void showAutoCloseDialog(String message, String title, int messageType) {
        JOptionPane optionPane = new JOptionPane(message, messageType);
        JDialog dialog = optionPane.createDialog(title);
        Timer timer = new Timer(2000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });
        timer.setRepeats(false);
        timer.start();
        dialog.setVisible(true);
    }


    static Dotenv dotenv = Dotenv.load();

    private static final String OPENAI_API_KEY = dotenv.get("API_KEY");
    private static final String BASE_URL = "https://api.openai.com/v1/";



    public final OkHttpClient client = new OkHttpClient();
    public String contentValue = "" ;

    public String threadId="";
    String STT="";
    String assistant_ID="asst_Ctk8uGybzhwFUllzaoKIuSkP";

    String instructions="You are an AI Agent for the Computer Engineering Department designed to assist teachers in analyzing and generating results based on the following requirements:\\n\" +\n" +
            "                \"\\n\" +\n" +
            "                \"The file consists of two sections: one contains questions with their corresponding CLOs (Course Learning Outcomes) and Bloom's Taxonomy levels (either as level numbers or level names), while the other contains their solutions.\\n\" +\n" +
            "                \"\\n\" +\n" +
            "                \"1. *Question Analysis*:\\n\" +\n" +
            "                \"   - Analyze each question to determine its taxonomy level.\\n\" +\n" +
            "                \"   - Compare the identified level with the given level.\\n\" +\n" +
            "                \"   - If the question is of a lower level than specified, suggest improvements to align it with the desired level. Provide clear guidance on how and where to improve the question, along with two example questions that meet the required level.\\n\" +\n" +
            "                \"   - If the taxonomy levels match, proceed to the next question.\\n\" +\n" +
            "                \"\\n\" +\n" +
            "                \"2. *Solution Analysis*:\\n\" +\n" +
            "                \"   - Examine each solution thoroughly to verify whether it meets the specified taxonomy level of the question.\\n\" +\n" +
            "                \"   - Generate a detailed report for each solution, including:\\n\" +\n" +
            "                \"     - A rating out of 10.\\n\" +\n" +
            "                \"     - Key elements that accurately meet the requirements.\\n\" +\n" +
            "                \"     - Suggestions for improvement if the solution falls below the required taxonomy level, specifying the areas to enhance and how to do so.\\n\" +\n" +
            "                \"\\n\" +\n" +
            "                \"Your goal is to ensure both questions and solutions align with the desired Bloom's Taxonomy levels, providing actionable insights and constructive feedback.";

    // Wrap ServletOutputStream with OutputStreamWriter for UTF-8 encoding




    public String createThread() {
        JsonObject threadRequestBody = new JsonObject();
        Request request = new Request.Builder()
                .url(BASE_URL + "threads")
                .post(RequestBody.create(threadRequestBody.toString(), MediaType.parse("application/json")))
                .addHeader("Authorization", "Bearer " + OPENAI_API_KEY)
                .addHeader("OpenAI-Beta", "assistants=v2")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                JsonObject responseObject = JsonParser.parseString(response.body().string()).getAsJsonObject();
                showAutoCloseDialog("This is an Important Part, Thread Created!!!","Thread Created Successfully", JOptionPane.INFORMATION_MESSAGE);
                System.out.println("thread Created...: " );

                return responseObject.get("id").getAsString();
            } else {
                showAutoCloseDialog("Failed to create thread: " + response.code() + " " + response.message(),"Thread Failed Creation", JOptionPane.INFORMATION_MESSAGE);

                System.out.println("Failed to create thread: " + response.code() + " " + response.message());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /// fetching thread_ID




    public boolean addMessage(String threadId, String role, String content) {
        System.out.println("Thread ID: "+ threadId);


        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("role", role);
        requestBody.addProperty("content", content);


        Request request = new Request.Builder()
                .url(BASE_URL + "threads/" + threadId + "/messages")
                .post(RequestBody.create(requestBody.toString(), MediaType.parse("application/json")))
                .addHeader("Authorization", "Bearer " + OPENAI_API_KEY)
                .addHeader("OpenAI-Beta", "assistants=v2")
                .build();

        try (Response response = client.newCall(request).execute()) {
            return response.isSuccessful();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }




    public void streamAssistantResponse(String threadId, String instructions) throws Exception {

         BufferedWriter writer = null;
//        System.out.println("entered in stream");


        String url = "https://api.openai.com/v1/threads/" + threadId + "/runs";




        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("instructions", instructions);
        requestBody.addProperty("stream", true);
        requestBody.addProperty("assistant_id", assistant_ID);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(url))
                .header("Authorization", "Bearer " + dotenv.get("API_KEY"))
                .header("OpenAI-Beta", "assistants=v2")
                .header("Accept", "text/event-stream")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                .build();



        HttpClient client = HttpClient.newHttpClient();

        CompletableFuture<Void> future = client.sendAsync(request, HttpResponse.BodyHandlers.ofInputStream())


                .thenAccept(response -> {
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(response.body(), StandardCharsets.UTF_8))) {


                        String line;

                        while ((line = reader.readLine()) != null) {
                            handleEvent(line, writer);
//                            System.out.println(line);
                        }
                    } catch (IOException e) {
                        System.err.println("Error reading response: " + e.getMessage());

                    }
                });

        future.join();
        System.out.println(contentValue);


    }





    void handleEvent(String line, BufferedWriter writer) {

        try {
            if (line.startsWith("data:")) {
                String json = line.substring(5).trim();
                if (!json.isEmpty()) {
                    // First check if the response is a JSON array
                    if (json.startsWith("[") && json.endsWith("]")) {
                        // Handle the ["DONE"] case (stream finished)
                        JsonArray jsonArray = JsonParser.parseString(json).getAsJsonArray();
                        if (jsonArray.size() == 1 && jsonArray.get(0).getAsString().equals("DONE")) {
                            System.out.println("Stream finished.");
                            return; // Exit or handle the end of the stream
                        }
                    } else {
                        // If not an array, proceed with parsing as JSON object
                        JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();

                        // Check if the "delta" field exists
                        if (jsonObject.has("delta")) {
                            JsonObject delta = jsonObject.getAsJsonObject("delta");

                            // Check if "content" is an array and contains items
                            if (delta.has("content") && delta.get("content").isJsonArray()) {
                                var contentArray = delta.getAsJsonArray("content");

                                // Process each element in the "content" array
                                for (var item : contentArray) {
                                    if (item.isJsonObject()) {
                                        JsonObject contentItem = item.getAsJsonObject();

                                        // Check if the "text" field exists in the content item
                                        if (contentItem.has("text")) {
                                            JsonObject textObject = contentItem.getAsJsonObject("text");

                                            // Check if "value" exists and is not null
                                            if (textObject.has("value") && textObject.get("value") != null) {
                                                String content = textObject.get("value").getAsString();
                                                contentValue+=content;

                                            } else {
                                                System.out.println("Text value is missing or null.");
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error processing event: " + e.getMessage());
        }}

    public void process_bot() throws Exception {
        String query="evaluate";
        boolean message_added=false;
      threadId=createThread();
        if(threadId!=null){
            showAutoCloseDialog("This is an Important Portion!! Thread Created Successfully, Usually Takes Time","Thread Created Successfully", JOptionPane.INFORMATION_MESSAGE);

            System.out.println("thread created");
          message_added=addMessage(threadId,"user",query);
      }
      else{
            showAutoCloseDialog("Thread does not exist or not created","Thread Failed Creation", JOptionPane.INFORMATION_MESSAGE);

            System.out.println("thread does not exist or not created");
     }
      if(message_added){
          showAutoCloseDialog("The Message is Added to Thread Successfully","Message Creation", JOptionPane.INFORMATION_MESSAGE);

          System.out.println("Message Added to thread successfully");
          streamAssistantResponse(threadId,instructions);

      }
    }
    public static  void main(String[] args) throws Exception {
        OpenAISetup op=new OpenAISetup();
        op.process_bot();
    }
}



