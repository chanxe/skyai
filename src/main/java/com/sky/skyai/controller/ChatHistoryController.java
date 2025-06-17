package com.sky.skyai.controller;

import com.sky.skyai.entity.vo.MessageVO;
import com.sky.skyai.repository.ChatHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping("/ai/history")
public class ChatHistoryController {
    private final ChatHistoryRepository chatHistoryRepository;
    private final ChatMemory chatMemory;
    /**
     * 根据类型查询会话历史
     * @param type
     * @return
     */
    @GetMapping("{type}")
    public List<String> getChatIds(@PathVariable String type) {
        return chatHistoryRepository.getChatIds(type);
    }

    @GetMapping("{type}/{chatId}")
    public List<MessageVO> getChatHistory(@PathVariable String type, @PathVariable String chatId) {
        return chatMemory.get(chatId).stream().map(MessageVO::new).collect(Collectors.toList());
    }


}
