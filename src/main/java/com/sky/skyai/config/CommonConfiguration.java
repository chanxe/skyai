package com.sky.skyai.config;

import com.sky.skyai.constants.SystemConstants;
import com.sky.skyai.tools.CourseTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class CommonConfiguration {
    @Bean
    public ChatMemory chatMemory() {
        return MessageWindowChatMemory.builder().build();
    }

    @Bean
    public VectorStore vectorStore(OpenAiEmbeddingModel embeddingModel) {
        return SimpleVectorStore.builder(embeddingModel).build();
    }

    @Bean
    public ChatClient chatClient(OpenAiChatModel model) {
        return ChatClient.builder(model)
                .defaultOptions(ChatOptions.builder()
                        .model("qwen-omni-turbo")
                        .build())
                .defaultSystem("你叫小团团，是一个可爱的人工智能助手，请用小团团的语气和我对话。")
                .defaultAdvisors(new SimpleLoggerAdvisor())
                .build();
    }
    @Bean
    public ChatClient gameChatClient(OpenAiChatModel model) {
        return ChatClient.builder(model)
                .defaultSystem(SystemConstants.GAME_SYSTEM_PROMPT)
                .defaultAdvisors(new SimpleLoggerAdvisor())
                .build();
    }

    @Bean
    public ChatClient serviceChatClient(OpenAiChatModel model, CourseTools courseTools) {
        return ChatClient.builder(model)
                .defaultSystem(SystemConstants.SERVICE_SYSTEM_PROMPT)
                .defaultAdvisors(new SimpleLoggerAdvisor())
                .defaultTools(courseTools)
                .build();
    }
    @Bean
    public ChatClient pdfChatClient(OpenAiChatModel model, VectorStore vectorStore) {
        return ChatClient.builder(model)
                .defaultSystem("请根据上下文回答问题，遇到上下文没有的问题，不要随意编造。")
                .defaultAdvisors(
                        new SimpleLoggerAdvisor(),
                        QuestionAnswerAdvisor.builder(vectorStore)
                                .searchRequest(SearchRequest.builder()
                                        .similarityThreshold(0.6)
                                        .topK(2)
                                        .build()).build()
                )
                .build();
    }
}
