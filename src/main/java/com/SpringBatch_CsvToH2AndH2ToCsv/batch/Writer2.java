package com.SpringBatch_CsvToH2AndH2ToCsv.batch;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import com.SpringBatch_CsvToH2AndH2ToCsv.model.User;

@Configuration
public class Writer2 {
	
	
	@Value("${outputFile}") 
	private String outputResource;
	
	private int someObject;
	
	@Bean
	public FlatFileItemWriter<User> writer() {
		System.out.println("inside writer 2");

		FlatFileItemWriter<User> writer = new FlatFileItemWriter<User>();
		writer.setResource(new FileSystemResource(outputResource));

		DelimitedLineAggregator<User> lineAggregator = new DelimitedLineAggregator<User>();
		lineAggregator.setDelimiter(",");

		BeanWrapperFieldExtractor<User> fieldExtractor = new BeanWrapperFieldExtractor<User>();
		fieldExtractor.setNames(new String[] { "id", "dept", "name", "salary", "time" });

		lineAggregator.setFieldExtractor(fieldExtractor);

		writer.setLineAggregator(lineAggregator);
		writer.setShouldDeleteIfExists(true);
		System.out.println("inside writer 2"+someObject);

		
		return writer;
		
		
		
	}
	
	 @BeforeStep
	    public void retrieveInterstepData(StepExecution stepExecution) {
	        JobExecution jobExecution = stepExecution.getJobExecution();
	        ExecutionContext jobContext = jobExecution.getExecutionContext();
	        this.someObject =(int) jobContext.get("someKey");
	        System.out.println("SomeObject"+someObject);
	    }

}
