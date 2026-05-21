package com.example.flightapp.batch.config;

import com.example.flightapp.booking.domain.BookingStatus;
import com.example.flightapp.booking.persistence.BookingEntity;
import com.example.flightapp.booking.persistence.BookingRepository;
import com.example.flightapp.common.domain.BusinessRuleException;
import com.example.flightapp.flight.application.FlightApplicationService;
import com.example.flightapp.flight.domain.Flight;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

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
    public Step flightImportStep(StepBuilderFactory steps,
                                 FlatFileItemReader<FlightCsvRow> flightCsvReader,
                                 ItemProcessor<FlightCsvRow, Flight> flightCsvProcessor,
                                 ItemWriter<Flight> flightWriter) {
        return steps.get("flightImportStep")
            .<FlightCsvRow, Flight>chunk(25)
            .reader(flightCsvReader)
            .processor(flightCsvProcessor)
            .writer(flightWriter)
            .faultTolerant()
            .skip(BusinessRuleException.class)
            .skip(IllegalArgumentException.class)
            .skipLimit(10)
            .build();
    }

    @Bean
    public Job bookingExportJob(JobBuilderFactory jobs, Step bookingExportStep) {
        return jobs.get("bookingExportJob")
            .incrementer(new RunIdIncrementer())
            .start(bookingExportStep)
            .build();
    }

    @Bean
    public Step bookingExportStep(StepBuilderFactory steps, BookingRepository bookingRepository) {
        return steps.get("bookingExportStep")
            .tasklet((contribution, chunkContext) -> {
                String outputFile = (String) chunkContext.getStepContext()
                    .getJobParameters()
                    .get("outputFile");
                writeConfirmedBookings(bookingRepository, outputFile);
                return RepeatStatus.FINISHED;
            })
            .build();
    }

    @Bean
    @StepScope
    public FlatFileItemReader<FlightCsvRow> flightCsvReader(@Value("#{jobParameters['fileName']}") String fileName) {
        FlatFileItemReader<FlightCsvRow> reader = new FlatFileItemReader<FlightCsvRow>();
        reader.setResource(resolveInput(fileName));
        reader.setLinesToSkip(1);
        DefaultLineMapper<FlightCsvRow> lineMapper = new DefaultLineMapper<FlightCsvRow>();
        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        tokenizer.setNames(new String[] {
            "flightNumber",
            "originAirportCode",
            "destinationAirportCode",
            "departureTime",
            "arrivalTime",
            "capacity"
        });
        BeanWrapperFieldSetMapper<FlightCsvRow> fieldSetMapper = new BeanWrapperFieldSetMapper<FlightCsvRow>();
        fieldSetMapper.setTargetType(FlightCsvRow.class);
        lineMapper.setLineTokenizer(tokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);
        reader.setLineMapper(lineMapper);
        return reader;
    }

    @Bean
    public ItemProcessor<FlightCsvRow, Flight> flightCsvProcessor() {
        return row -> new Flight(
            null,
            row.getFlightNumber(),
            row.getOriginAirportCode(),
            row.getDestinationAirportCode(),
            LocalDateTime.parse(row.getDepartureTime()),
            LocalDateTime.parse(row.getArrivalTime()),
            row.getCapacity()
        );
    }

    @Bean
    public ItemWriter<Flight> flightWriter(FlightApplicationService flightService) {
        return flights -> {
            for (Flight flight : flights) {
                flightService.createOrUpdateByFlightNumber(flight);
            }
        };
    }

    private Resource resolveInput(String fileName) {
        if (fileName == null || fileName.trim().isEmpty()) {
            return new ClassPathResource("batch/input/flights-sample.csv");
        }
        File file = new File(fileName);
        if (file.exists()) {
            return new FileSystemResource(file);
        }
        return new ClassPathResource(fileName);
    }

    private void writeConfirmedBookings(BookingRepository bookingRepository, String outputFile) throws IOException {
        String path = outputFile == null || outputFile.trim().isEmpty()
            ? "target/booking-export.csv"
            : outputFile;
        File file = new File(path);
        File parent = file.getParentFile();
        if (parent != null) {
            parent.mkdirs();
        }
        List<BookingEntity> bookings = bookingRepository.findByStatus(BookingStatus.CONFIRMED);
        FileWriter writer = new FileWriter(file);
        try {
            writer.write("bookingReference,flightId,passengerName,passengerEmail,createdAt\n");
            for (BookingEntity booking : bookings) {
                writer.write(booking.getBookingReference() + ","
                    + booking.getFlightId() + ","
                    + booking.getPassengerName() + ","
                    + booking.getPassengerEmail() + ","
                    + booking.getCreatedAt() + "\n");
            }
        } finally {
            writer.close();
        }
    }
}
