package com.SpringBatch_CsvToH2AndH2ToCsv.config;

import javax.batch.runtime.StepExecution;
import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.SpringBatch_CsvToH2AndH2ToCsv.JobListener.MyJobListener;
import com.SpringBatch_CsvToH2AndH2ToCsv.Policy.JobSkipPolicy;
import com.SpringBatch_CsvToH2AndH2ToCsv.batch.Processor1;
import com.SpringBatch_CsvToH2AndH2ToCsv.batch.Processor2;
import com.SpringBatch_CsvToH2AndH2ToCsv.mapper.UserDBRowMapper.UserDBRowMapper;
import com.SpringBatch_CsvToH2AndH2ToCsv.model.User;

@Configuration
@EnableBatchProcessing

public class SpringBatchConfig {

	@Autowired
	private JobBuilderFactory jobBuilderFactory;

	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	@Autowired
	private DataSource dataSource;

//	@Autowired
//	private DBWriter writer1;

	@Autowired
	private Processor1 processor1;

	@Autowired
	private Processor2 processor2;

//	@Autowired
//	UserRepository userRepository;
	
	@Value("${inputFile}") 
	private String inputResource;
	
	@Value("${outputFile}") 
	private String outputResource;
	//private Resource outputResource = new FileSystemResource("output/users_output.csv");
	
	@Bean
	public MyJobListener myJobListener()
	{
		return new MyJobListener();
	}


	@Bean
	public Job job() throws Exception {

//		return this.jobBuilderFactory.get("BATCH JOB1").incrementer(new RunIdIncrementer()).start(step1()).on("COMPLETED").stopAndRestart(step2())
//				.end()
//				.build();
		
		
		return this.jobBuilderFactory.get("BATCH JOB1").incrementer(new RunIdIncrementer()).start(step1()).next(step2()).listener(myJobListener())
						.build();
	}

	@Bean
	public Step step1() throws Exception { // Step 1 - Read CSV and Write to DB
		return stepBuilderFactory.get("step1").<User, User>chunk(1).reader(reader1()).processor(processor1)
				.writer(writer1())
				.faultTolerant().skipPolicy(skipPolicy()).build();
	}

	@Bean
	public Step step2() throws Exception { // STEP - Read DB and Write CSV
		return stepBuilderFactory.get("step2").<User, User>chunk(1).reader(reader2()).processor(processor2)
				.writer(writer2()).build();
	}
	
	public void beforeStep(StepExecution stepExecution) {

	    System.out.println("Before Step");
	}
	
	
	@Bean
	public com.SpringBatch_CsvToH2AndH2ToCsv.Policy.JobSkipPolicy skipPolicy()
	{
		return new JobSkipPolicy();
	}
	
	
	@Bean
	public ItemReader<User> reader1() {

		System.out.println("inside reader 1");

		FlatFileItemReader<User> flatFileItemReader = new FlatFileItemReader<>();
//		flatFileItemReader.setResource(new FileSystemResource("src\\main\\resources\\users.csv"));
		
		flatFileItemReader.setResource(new FileSystemResource(inputResource));
		// flatFileItemReader.setResource(new ClassPathResource("users.csv"));
		flatFileItemReader.setName("CSV-Reader");
		flatFileItemReader.setLinesToSkip(1);//because the first line would be always the header
		flatFileItemReader.setLineMapper(lineMapper());//that knows how to read one line at a time
		
		//flatFileItemReader.setBufferedReaderFactory(bufferedReaderFactory);
		return flatFileItemReader;
	}

	@Bean
	public LineMapper<User> lineMapper() {

		DefaultLineMapper<User> defaultLineMapper = new DefaultLineMapper<>();
		DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        //knows how to pass tokens 
		lineTokenizer.setDelimiter(",");
		lineTokenizer.setStrict(false);
		lineTokenizer.setNames(new String[] { "id", "name", "dept", "salary" });
		//the name of the columns which we added 

		BeanWrapperFieldSetMapper<User> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
		fieldSetMapper.setTargetType(User.class);

		defaultLineMapper.setLineTokenizer(lineTokenizer);
		defaultLineMapper.setFieldSetMapper(fieldSetMapper);

		return defaultLineMapper;
	}

	@Bean
	public ItemReader<User> reader2() {
		System.out.println("inside reader 2");

		JdbcCursorItemReader<User> cursorItemReader = new JdbcCursorItemReader<>();
		cursorItemReader.setDataSource(dataSource);
		cursorItemReader.setSql("select * from user");
		cursorItemReader.setRowMapper(new UserDBRowMapper());
		System.out.println(cursorItemReader);
		return cursorItemReader;
	}


	@Bean
	public JdbcBatchItemWriter<User> writer1() {

		JdbcBatchItemWriter<User> writer = new JdbcBatchItemWriter<>();
		writer.setDataSource(dataSource);
		writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
		writer.setSql("Insert into user ( id, dept, name, salary, time) values ( :id, :dept, :name, :salary, :time)");
		return writer;

	}

	@Bean
	public FlatFileItemWriter<User> writer2() {
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
		return writer;
	}
	
	
	@Bean
	public Processor2 processor2() { 
		
		return new Processor2();
	}

}

//
//@Bean
//public ItemReader<User> reader2() {
//	System.out.println("inside reader 2");
//    RepositoryItemReader<User> repItemReader = new RepositoryItemReader<User>();
//    repItemReader.setRepository(userRepository);
//    repItemReader.setMethodName("findTempByStatus");
//    HashMap<String, Direction> sortMap = new HashMap<>();
//    sortMap.put("id", Direction.ASC);
//    repItemReader.setPageSize(10);
//    repItemReader.setSort(sortMap);
//    return repItemReader;
//}
//
