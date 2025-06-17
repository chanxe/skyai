package com.sky.skyai;

import org.junit.jupiter.api.Test;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureGraphQlTester;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;

@SpringBootTest
class SkyAiApplicationTests {
    @Autowired
    private OpenAiEmbeddingModel embeddingModel;

    @Test
    void contextLoads() {
        float[] sss = embeddingModel.embed("学Java就是吊");
        System.out.println(Arrays.toString(sss));
    }

}
