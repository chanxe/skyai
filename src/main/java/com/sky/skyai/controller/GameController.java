package com.sky.skyai.controller;

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
public class GameController {
    private final ChatClient gameChatClient;
    private final ChatMemory chatMemory;
    @RequestMapping(value = "/game", produces = "text/html;charset=utf-8")
    public Flux<String> chat(String prompt, String chatId) {

        // 请求模型
        return gameChatClient.prompt()
                .user(prompt)
                .advisors(MessageChatMemoryAdvisor.builder(chatMemory).conversationId(chatId).build())
                .stream()
                .content();
    }
}
