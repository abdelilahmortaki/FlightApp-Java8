# Batch

Spring Batch is enabled with `@EnableBatchProcessing`.

Current job:

```text
flightImportJob
```

The app sets:

```yaml
spring.batch.job.enabled: false
spring.batch.initialize-schema: always
```

That prevents jobs from auto-running at startup and initializes Spring Batch metadata tables in H2.
