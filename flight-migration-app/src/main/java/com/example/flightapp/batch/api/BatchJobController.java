package com.example.flightapp.batch.api;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/batch")
public class BatchJobController {

    private final JobLauncher jobLauncher;
    private final Job flightImportJob;
    private final Job bookingExportJob;
    private final JobExplorer jobExplorer;

    public BatchJobController(JobLauncher jobLauncher,
                              Job flightImportJob,
                              Job bookingExportJob,
                              JobExplorer jobExplorer) {
        this.jobLauncher = jobLauncher;
        this.flightImportJob = flightImportJob;
        this.bookingExportJob = bookingExportJob;
        this.jobExplorer = jobExplorer;
    }

    @GetMapping("/jobs")
    public List<String> jobs() {
        List<String> jobs = new ArrayList<String>();
        jobs.add(flightImportJob.getName());
        jobs.add(bookingExportJob.getName());
        return jobs;
    }

    @GetMapping("/jobs/{jobName}/executions")
    public List<Map<String, Object>> executions(@PathVariable String jobName) {
        Set<JobExecution> executions = jobExplorer.findRunningJobExecutions(jobName);
        List<Map<String, Object>> response = new ArrayList<Map<String, Object>>();
        for (JobExecution execution : executions) {
            response.add(toMap(execution));
        }
        List<org.springframework.batch.core.JobInstance> instances = jobExplorer.getJobInstances(jobName, 0, 20);
        for (org.springframework.batch.core.JobInstance instance : instances) {
            for (JobExecution execution : jobExplorer.getJobExecutions(instance)) {
                if (!executions.contains(execution)) {
                    response.add(toMap(execution));
                }
            }
        }
        return response;
    }

    @PostMapping("/jobs/{jobName}/launch")
    public BatchJobLaunchResponse launch(@PathVariable String jobName,
                                         @RequestParam(required = false) String fileName,
                                         @RequestParam(required = false) String outputFile) throws Exception {
        Job job = resolveJob(jobName);
        if (job == null) {
            throw new IllegalArgumentException("Unknown job: " + jobName);
        }
        JobParametersBuilder builder = new JobParametersBuilder()
            .addLong("runAt", System.currentTimeMillis())
            .addString("fileName", fileName == null ? "" : fileName)
            .addString("outputFile", outputFile == null ? "" : outputFile);
        JobParameters parameters = builder.toJobParameters();
        JobExecution execution = jobLauncher.run(job, parameters);
        return new BatchJobLaunchResponse(
            job.getName(),
            execution.getId(),
            execution.getStatus().toString()
        );
    }

    private Job resolveJob(String jobName) {
        if (flightImportJob.getName().equals(jobName)) {
            return flightImportJob;
        }
        if (bookingExportJob.getName().equals(jobName)) {
            return bookingExportJob;
        }
        return null;
    }

    private Map<String, Object> toMap(JobExecution execution) {
        Map<String, Object> map = new LinkedHashMap<String, Object>();
        map.put("id", execution.getId());
        map.put("jobName", execution.getJobInstance().getJobName());
        map.put("status", execution.getStatus().toString());
        map.put("startTime", execution.getStartTime());
        map.put("endTime", execution.getEndTime());
        map.put("skipCount", execution.getStepExecutions().isEmpty()
            ? 0
            : execution.getStepExecutions().iterator().next().getSkipCount());
        return map;
    }
}
