package com.oussama.FacultyPlanning.Configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {
    @Bean(name = "emailTaskExecutor")
    public Executor emailTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);     // Always ready threads
        executor.setMaxPoolSize(20);     // Max threads during load
        executor.setQueueCapacity(100);  // Pending tasks queue
        executor.setThreadNamePrefix("EmailThread-");
        executor.initialize();
        return executor;
    }
}
