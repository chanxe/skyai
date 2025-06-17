package com.sky.skyai.controller;

import com.sky.skyai.repository.ChatHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RequiredArgsConstructor
@RestController
@RequestMapping("/ai")
public class CustomerServiceController {
    private final ChatClient serviceChatClient;
    private final ChatHistoryRepository chatHistoryRepository;
    private final ChatMemory chatMemory;

    @RequestMapping(value = "/service", produces = "text/html;charset=utf-8")
    public Flux<String> service(String prompt, String chatId) {
        chatHistoryRepository.save("service", chatId);
        return serviceChatClient.prompt()
                .user(prompt)
                .advisors(MessageChatMemoryAdvisor.builder(chatMemory).conversationId(chatId).build())
                .stream()
                .content();
    }
}
