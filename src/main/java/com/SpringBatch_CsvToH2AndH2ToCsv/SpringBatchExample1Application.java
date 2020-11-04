package com.SpringBatch_CsvToH2AndH2ToCsv;


import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.microsoft.azure.storage.*;
import com.microsoft.azure.storage.blob.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.Scanner;

@SpringBootApplication
@EnableBatchProcessing
public class SpringBatchExample1Application {
	
	public static final String storageConnectionString = "DefaultEndpointsProtocol=https;AccountName=azureblobaccountrohit;AccountKey=pOrEawPp91GcvdMYrc49uvwCPZNPrBO2KZPHn3xhaKvU/DNK3/osROywo+FJSJIIfMAqc6pERIf24Yuk/2QQvA==;EndpointSuffix=core.windows.net";


	public static void main(String[] args) {
		ApplicationContext context=	SpringApplication.run(SpringBatchExample1Application.class, args);
		System.out.println("running");
		
		System.out.println("running");
		Test test = context.getBean(Test.class);

        String password = test.getPassword();

        System.out.println(password);

		
		
	}
	
} 

	
        
        
	 @Component
	    class Test {

	        @Value("${password}")
	        private String password;

	        public String getPassword() {
	            return password;
	        }
	    }
	

