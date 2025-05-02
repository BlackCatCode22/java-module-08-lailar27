package com.example.Restful.API;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(Application.class, args);

		testChatbot(context);
	}

	private static void testChatbot(ApplicationContext context) {
		ChatbotService chatbotService = context.getBean(ChatbotService.class);

		String sessionID = "test-session-1";

		String testMessage = "Hey chatbot, what is your name?";
		String response = chatbotService.getChatbotResponse(sessionID, testMessage);

		System.out.println("User: " + testMessage);
		System.out.println("Chatbot: " + response);
	}
}
