package com.SpringBatch_CsvToH2AndH2ToCsv;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

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
		SpringApplication.run(SpringBatchExample1Application.class, args);
		System.out.println("running");

		File sourceFile = null, downloadedFile = null;
		//System.out.println("Azure Blob storage quick start sample");

		CloudStorageAccount storageAccount;
		CloudBlobClient blobClient = null;
		CloudBlobContainer container=null;

		try {    
			// Parse the connection string and create a blob client to interact with Blob storage
			storageAccount = CloudStorageAccount.parse(storageConnectionString);
			blobClient = storageAccount.createCloudBlobClient();
			container = blobClient.getContainerReference("quickstartcontainer");

			// Create the container if it does not exist with public access.
			container.createIfNotExists(BlobContainerPublicAccessType.CONTAINER, new BlobRequestOptions(), new OperationContext());		    

			//Creating a sample file
			sourceFile = File.createTempFile("sampleFile", ".txt");
			System.out.println("Creating a sample file at: " + sourceFile.toString());
			Writer output = new BufferedWriter(new FileWriter(sourceFile));
			output.write("id,name,dept,salary\n"
					+ "1,Rohit,001,12000\n"
					+ "2,Kiran,002,13000\n"
					+ "3,Shivani,003,10000\n"
					+ "4,Abhi,002,15000\n"
					+ "5,Aditi,001,15500\n"
					+ "6,Charan,003,13000\n"
					+ "7,Agya,001,10000\n"
					+ "8,Jayant,002,30000\n"
					+ "9,Aman,003,12600\n"
					+ "10,Sandeep,001,14000\n"
					+ "11,Amrit,002,16000\n"
					+ "12,P,001,11000\n"
					+ "13,Prince,001,12050\n"
					+ "14,Ankur,003,6000\n"
					+ "15,Kiran,002,20000");
			output.close();

		//Getting a blob reference
			CloudBlockBlob blob = container.getBlockBlobReference(sourceFile.getName());

			//Creating blob and uploading file to it
			System.out.println("Uploading the sample file ");
			blob.uploadFromFile(sourceFile.getAbsolutePath());

		//	blob.uploadFromFile("C:\\Users\\Rohit\\Desktop\\New folder (2)\\users.csv");

			//Listing contents of container
		for (ListBlobItem blobItem : container.listBlobs()) {
			System.out.println("URI of blob is: " + blobItem.getUri());
		}

//		// Download blob. In most cases, you would have to retrieve the reference
//		// to cloudBlockBlob here. However, we created that reference earlier, and 
//		// haven't changed the blob we're interested in, so we can reuse it. 
//		// Here we are creating a new file to download to. Alternatively you can also pass in the path as a string into downloadToFile method: blob.downloadToFile("/path/to/new/file").
		downloadedFile = new File("C:\\Users\\Rohit\\Desktop\\New folder (2)", "downloadedFile.csv");
		blob.downloadToFile(downloadedFile.getAbsolutePath());
		} 
		catch (StorageException ex)
		{
			System.out.println(String.format("Error returned from the service. Http code: %d and error code: %s", ex.getHttpStatusCode(), ex.getErrorCode()));
		}
		catch (Exception ex) 
		{
			System.out.println(ex.getMessage());
		}
		finally 
		{
			System.out.println("The program has completed successfully.");
			System.out.println("Press the 'Enter' key while in the console to delete the sample files, example container, and exit the application.");

			//Pausing for input
			Scanner sc = new Scanner(System.in);
			sc.nextLine();

			System.out.println("Deleting the container");
			try {
				if(container != null)
					container.deleteIfExists();
			} 
			catch (StorageException ex) {
				System.out.println(String.format("Service error. Http code: %d and error code: %s", ex.getHttpStatusCode(), ex.getErrorCode()));
			}

			System.out.println("Deleting the source, and downloaded files");

			if(downloadedFile != null)
				downloadedFile.deleteOnExit();

			if(sourceFile != null)
				sourceFile.deleteOnExit();

			//Closing scanner
			sc.close();
		}
	
		
	}
	
}
