package com.sky.skyai;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class SkyAiApplicationTests {

    @Test
    void contextLoads() {
        // Test that application context loads successfully
        // Note: We don't test actual AI functionality here as it requires external services
        System.out.println("Application context loaded successfully!");
    }

}
