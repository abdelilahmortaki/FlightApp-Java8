package com.example.flightapp.batch;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.flightapp.flight.application.FlightApplicationService;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class FlightImportBatchTest {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job flightImportJob;

    @Autowired
    private FlightApplicationService flightService;

    @Test
    void flightImportSkipsInvalidRows() throws Exception {
        JobExecution execution = jobLauncher.run(
            flightImportJob,
            new JobParametersBuilder().addLong("runAt", System.currentTimeMillis()).toJobParameters()
        );

        assertEquals(BatchStatus.COMPLETED, execution.getStatus());
        assertEquals(2L, flightService.count());
    }
}
