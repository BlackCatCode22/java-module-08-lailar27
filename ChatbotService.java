package com.example.Restful.API;

import OkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * ChatbotService handles communication with OpenAi API and maintains session-based chat history.
 */
@Service
public class ChatbotService {

    @Value("${openai.api.key}")
    private String apiKey;

    @Value("${open.api.url:https://api.openai.com/v1/chat/completions}")
    private String apiUrl;

    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private final Map<String, List<Map<String, String >>> sessionConversations = new HashMap<>();

    /**
     * Retrieves a response from the chatbot
     *
     * @param sessionID     The unique session ID for the user.
     * @param userMessage   The user's message to the chatbot.
     * @return  The chatbot's response.
     */
    public String getChatbotResponse(String sessionID, String userMessage) {
        OkHttpClient client = new OkHttpClient();

        List<Map<String, String>> conversationHistory = sessionConversations.computeIfAbsent(sessionID, k -> new ArrayList<>());

        Map<String, String> userEntry = new HashMap<>();
        userEntry.put("role", "user");
        userEntry.put("content", userMessage);
        conversationHistory.add(userEntry);

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> payload = new HashMap<>();
            payload.put("model", "gpt-3.5-turbo");
            payload.put("messages", conversationHistory);

            String requestBody = objectMapper.writeValueAsString(payload);

            Request request = new Request.Builder()
                    .url(apiUrl)
                    .post(RequestBody.create(requestBody, JSON))
                    .addHeader("Authorization", "Bearer " + apiKey)
                    .build();

            try (Response response = client.newCall(request).execute()){
                if (response.isSuccessful() && response.body() != null) {
                    String responseBody = response.body().string();
                    String botReply = extractBotMessage(responseBody);

                    Map<String, String> botEntry = new HashMap<>();
                    botEntry.put("role", "assistant");
                    botEntry.put("content", botReply);
                    conversationHistory.add(botEntry);

                    return botReply;
                } else {
                    System.out.println("Request Body: " + requestBody);
                    System.out.println("Response Code: " + response.code());
                    System.out.println("Response Message: " + response.message());
                    System.out.println("Response Body: " + (response.body() != null ? response.body().string() : "null"));
                    return "Error: " + response.code() + " - " + response.message();
                }
            }
        } catch (Exception e) {
            return "Error: Unable to construct request or communicate with API - " + e.getMessage();
        }
    }
    /**
     * Extracts the chatbot's message from API's response.
     *
     * @param responseBody  The JSON response from OpenAI API.
     * @return  The chatbot's message content.
     */
    private String extractBotMessage(String responseBody) {
        try {

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(responseBody);

            JsonNode choicesNode = rootNode.path("choices");
            if (choicesNode.isArray() && choicesNode.size() > 0) {
                JsonNode messageNode = choicesNode.get(0).path("message");
                return messageNode.path("content").asText();
            } else {
                return "Error: No choices found in the response. ";
            }
        }   catch (Exception e) {
            return "Error: Unable to parse response - " + e.getMessage();
        }
    }

    /**
     * Clears the conversation history for the given session ID.
     *
     * @param sessionId The session ID whose history should be cleared.
     */
    public void clearConversation(String sessionId) { sessionConversations.remove(sessionId); }
}
