package com.SpringBatch_CsvToH2AndH2ToCsv.batch;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.SpringBatch_CsvToH2AndH2ToCsv.model.User;
import com.SpringBatch_CsvToH2AndH2ToCsv.repository.UserRepository;


@Component
public class Writer1 implements ItemWriter<User> {

	@Autowired
	private DataSource dataSource;

	@Autowired
	UserRepository repo;
	
	private StepExecution stepExecution;

	@Override
	public void write(List<? extends User> items) throws Exception {

		repo.save(items);
		System.out.println("Inside Writer 1");
		ExecutionContext stepContext = this.stepExecution.getExecutionContext();
        int count = stepContext.containsKey("count") ? stepContext.getInt("count") : 0;
        stepContext.put("count", items);
	}
	
	 @BeforeStep
     public void saveStepExecution(StepExecution stepExecution) {
         this.stepExecution = stepExecution;
     }

}
