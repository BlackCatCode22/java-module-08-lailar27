package com.example.Restful.API;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
public class ChatbotController {

    @Autowired
    private ChatbotService chatbotService;

    /**
     * Endpoint to handle chat requests.
     *
     * @param sessionId     The session ID for the user.
     * @param userMessage   The user's message.
     * @return  The chatbot's response.
     */
    @PostMapping("/{sessionId}")
    public String getChatResponse(@PathVariable String sessionId, @RequestBody String userMessage) {
        return chatbotService.getChatbotResponse(sessionId,userMessage);
    }
}


