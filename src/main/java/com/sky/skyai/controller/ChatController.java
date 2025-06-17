package com.sky.skyai.controller;

import com.sky.skyai.repository.ChatHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.content.Media;
import org.springframework.util.MimeType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@RestController
@RequestMapping("/ai")
public class ChatController {
    private final ChatClient chatClient;
    private final ChatMemory chatMemory;
    private final ChatHistoryRepository chatHistoryRepository;
    @RequestMapping(value = "/chat", produces = "text/html;charset=utf-8")
    public Flux<String> chat(@RequestParam("prompt") String prompt,
                             @RequestParam("chatId") String chatId,
                             @RequestParam(value = "files", required = false) List<MultipartFile> files) {
        // 保存会话ID
        chatHistoryRepository.save("chat", chatId);
        if (files != null && !files.isEmpty()) {
            return multiModalChat(prompt, chatId, files);
        } else {
            return textChat(prompt, chatId);
        }

    }

    private Flux<String> multiModalChat(String prompt, String chatId, List<MultipartFile> files) {
        // 解析多媒体
        List<Media> medias = files.stream()
                .map(file -> new Media(MimeType.valueOf(Objects.requireNonNull(file.getContentType())), file.getResource()))
                .toList();
        return chatClient.prompt()
                .user(p -> p.text(prompt).media(medias.toArray(Media[]::new)))
                .advisors(MessageChatMemoryAdvisor.builder(chatMemory).conversationId(chatId).build())
                .stream()
                .content();
    }

    private Flux<String> textChat(String prompt, String chatId) {
        return chatClient.prompt()
                .user(prompt)
                .advisors(MessageChatMemoryAdvisor.builder(chatMemory).conversationId(chatId).build())
                .stream()
                .content();
    }
}
