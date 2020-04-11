package com.example.azure.storage.fileshare;

import java.net.URISyntaxException;
import java.security.InvalidKeyException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.file.CloudFileClient;

@SpringBootApplication
public class FileshareApplication {

	@Autowired
	private Environment environment;
	
	public static void main(String[] args) {
		SpringApplication.run(FileshareApplication.class, args);
	}
	
	/**
	 * Creates CloudFileClient from CloudStorageAccount
	 * @return A CloudFileClient
	 * @throws InvalidKeyException
	 * @throws URISyntaxException
	 */
	@Bean
	public CloudFileClient  createFileClient() throws InvalidKeyException, URISyntaxException {
		CloudStorageAccount cloudStorageAccount = CloudStorageAccount.parse(environment.getProperty("azure.storage.connectionstring"));
		return cloudStorageAccount.createCloudFileClient();
	}

}
