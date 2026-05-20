package com.example.flightapp.batch.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableBatchProcessing
public class BatchConfig {

    @Bean
    public Job flightImportJob(JobBuilderFactory jobs, Step flightImportStep) {
        return jobs.get("flightImportJob")
            .incrementer(new RunIdIncrementer())
            .start(flightImportStep)
            .build();
    }

    @Bean
    public Step flightImportStep(StepBuilderFactory steps) {
        return steps.get("flightImportStep")
            .tasklet((contribution, chunkContext) -> RepeatStatus.FINISHED)
            .build();
    }
}
