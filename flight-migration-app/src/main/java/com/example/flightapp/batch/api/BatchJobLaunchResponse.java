package com.example.flightapp.batch.api;

public class BatchJobLaunchResponse {

    private String jobName;
    private Long executionId;
    private String status;

    public BatchJobLaunchResponse() {
    }

    public BatchJobLaunchResponse(String jobName, Long executionId, String status) {
        this.jobName = jobName;
        this.executionId = executionId;
        this.status = status;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public Long getExecutionId() {
        return executionId;
    }

    public void setExecutionId(Long executionId) {
        this.executionId = executionId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
