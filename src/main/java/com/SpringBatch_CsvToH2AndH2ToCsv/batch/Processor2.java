 package com.SpringBatch_CsvToH2AndH2ToCsv.batch;

import org.springframework.batch.item.ItemProcessor;

import com.SpringBatch_CsvToH2AndH2ToCsv.model.User;





public class Processor2 implements ItemProcessor<User,User>{

	@Override
	public User process(User user) throws Exception {
    	System.out.println("inside processor 2");

		user.setSalary(user.getSalary()+2000);
		return user;
	}

	
}
