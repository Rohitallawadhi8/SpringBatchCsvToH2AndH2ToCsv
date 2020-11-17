package com.SpringBatch_CsvToH2AndH2ToCsv.JobListener;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.Scanner;

import javax.batch.api.listener.JobListener;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.beans.factory.annotation.Value;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.OperationContext;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.BlobContainerPublicAccessType;
import com.microsoft.azure.storage.blob.BlobRequestOptions;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;
import com.microsoft.azure.storage.blob.ListBlobItem;

public class MyJobListener implements JobExecutionListener {

	@Value("${password}")
	private String password;
	
	@Value("${uploadFilePath}") 
	private String uploadFilePath;

	public static final String storageConnectionString = "DefaultEndpointsProtocolhttps;AccountName=azureblobaccountrohit;AccountKey=pOrEawPp91GcvdMYrc49uvwCPZNPrBO2KZPHn3xhaKvU/DNK3/osROywo+FJSJIIfMAqc6pERIf24Yuk/2QQvA==;EndpointSuffix=core.windows.net";

	
	@Override
	public void beforeJob(JobExecution jobExecution) {
		System.out.println("Password:" + password);
		System.out.println("uploadFilePath:" + uploadFilePath);

		File sourceFile = null, downloadedFile = null;
//System.out.println("Azure Blob storage quick start sample");

		CloudStorageAccount storageAccount;
		CloudBlobClient blobClient = null;
		CloudBlobContainer container = null;

		try {
			// Parse the connection string and create a blob client to interact with Blob
			// storage
			storageAccount = CloudStorageAccount.parse(storageConnectionString);
			blobClient = storageAccount.createCloudBlobClient();
			container = blobClient.getContainerReference("quickstartcontainer");

			// Create the container if it does not exist with public access.
			container.createIfNotExists(BlobContainerPublicAccessType.CONTAINER, new BlobRequestOptions(),
					new OperationContext());


//Getting a blob reference
			CloudBlockBlob blob = container.getBlockBlobReference("ABS Blob");

			// Creating blob and uploading file to it
			System.out.println("Uploading the sample file ");
			blob.uploadFromFile(uploadFilePath);

//	blob.uploadFromFile("C:\\Users\\Rohit\\Desktop\\New folder (2)\\users.csv");

			// Listing contents of container
			for (ListBlobItem blobItem : container.listBlobs()) {
				System.out.println("URI of blob is: " + blobItem.getUri());
			}

//// Download blob. In most cases, you would have to retrieve the reference
//// to cloudBlockBlob here. However, we created that reference earlier, and 
//// haven't changed the blob we're interested in, so we can reuse it. 
//// Here we are creating a new file to download to. Alternatively you can also pass in the path as a string into downloadToFile method: blob.downloadToFile("/path/to/new/file").
			downloadedFile = new File("C:\\Users\\Rohit\\Desktop\\New folder (2)", "downloadedFile.csv");
			blob.downloadToFile(downloadedFile.getAbsolutePath());
		} catch (StorageException ex) {
			System.out.println(String.format("Error returned from the service. Http code: %d and error code: %s",
					ex.getHttpStatusCode(), ex.getErrorCode()));
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		} finally {
			System.out.println("The program has completed successfully.");
			System.out.println(
					"Press the 'Enter' key while in the console to delete the sample files, example container, and exit the application.");

			// Pausing for input
			Scanner sc = new Scanner(System.in);
			sc.nextLine();

			System.out.println("Deleting the container");
			try {
				if (container != null)
					container.deleteIfExists();
				
			} catch (StorageException ex) {
				System.out.println(String.format("Service error. Http code: %d and error code: %s",
						ex.getHttpStatusCode(), ex.getErrorCode()));
			}

			System.out.println("Deleting the source, and downloaded files");

			if (downloadedFile != null)
				downloadedFile.deleteOnExit();

			if (sourceFile != null)
				sourceFile.deleteOnExit();

			// Closing scanner
			sc.close();
		}

	}

	@Override
	public void afterJob(JobExecution jobExecution) {
		// TODO Auto-generated method stub

	}

}
