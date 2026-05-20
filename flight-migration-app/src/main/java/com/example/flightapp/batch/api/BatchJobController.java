package com.example.flightapp.batch.api;

import java.util.Collections;
import java.util.List;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/batch")
public class BatchJobController {

    private final JobLauncher jobLauncher;
    private final Job flightImportJob;

    public BatchJobController(JobLauncher jobLauncher, Job flightImportJob) {
        this.jobLauncher = jobLauncher;
        this.flightImportJob = flightImportJob;
    }

    @GetMapping("/jobs")
    public List<String> jobs() {
        return Collections.singletonList(flightImportJob.getName());
    }

    @PostMapping("/jobs/{jobName}/launch")
    public BatchJobLaunchResponse launch(@PathVariable String jobName) throws Exception {
        if (!flightImportJob.getName().equals(jobName)) {
            throw new IllegalArgumentException("Unknown job: " + jobName);
        }
        JobParameters parameters = new JobParametersBuilder()
            .addLong("runAt", System.currentTimeMillis())
            .toJobParameters();
        JobExecution execution = jobLauncher.run(flightImportJob, parameters);
        return new BatchJobLaunchResponse(
            flightImportJob.getName(),
            execution.getId(),
            execution.getStatus().toString()
        );
    }
}
