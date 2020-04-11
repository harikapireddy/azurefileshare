package com.example.azure.storage.fileshare.service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.microsoft.azure.storage.OperationContext;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.file.CloudFile;
import com.microsoft.azure.storage.file.CloudFileClient;
import com.microsoft.azure.storage.file.CloudFileDirectory;
import com.microsoft.azure.storage.file.CloudFileShare;
import com.microsoft.azure.storage.file.FileRequestOptions;
import com.microsoft.azure.storage.file.ListFileItem;


/**
 * @author harik
 *
 */
@Component
public class FileshareService {

	private static final Logger logger = LoggerFactory.getLogger(FileshareService.class);
	
	@Autowired
	private CloudFileClient cloudFileClient;
	
	private List<URI> uris = new ArrayList<URI>();
	
	/**
	 * Creates CloudFileShare
	 * @param fileshareName
	 * @return CloudFileShare
	 */
	public CloudFileShare createCloudFileShare(String fileShareName) {
		CloudFileShare cloudFileShare = null;
		try {
			 cloudFileShare = cloudFileClient.getShareReference(fileShareName);
			 boolean hasFileshareCreated = cloudFileShare.createIfNotExists();
			 if(hasFileshareCreated) {
				 logger.info("file share created");
			 }else {
				 logger.info("file share exists");
			 }
		} catch (URISyntaxException | StorageException e) {
			logger.error(e.getMessage(),e);
		}
		return cloudFileShare;
	}
	
	/**
	 * Creates CloudFileDirectory
	 * @param fileShareName
	 * @param directoryName
	 * @return CloudFileDirectory
	 */
	public CloudFileDirectory createCloudFileDirectory(String fileShareName,String directoryName) {
		CloudFileDirectory cloudFileDirectory = null;
		try {
			cloudFileDirectory = createCloudFileShare(fileShareName).getRootDirectoryReference().getDirectoryReference(directoryName);
			boolean fileDirectoryCreated = cloudFileDirectory.createIfNotExists();
			if(fileDirectoryCreated) {
				logger.info("directory created");
			}else {
				logger.info("directory exists");
			}
		} catch (StorageException | URISyntaxException e) {
			logger.error(e.getMessage(),e);
		}
		return cloudFileDirectory;
	}
	
	/**
	 * Upload the file to file share
	 * @param fileShareName
	 * @param directoryName
	 * @param multipartFile
	 */
	public URI uploadFile(String fileShareName,String directoryName,MultipartFile multipartFile) {
		CloudFile cloudFile =null;
		try {
			if(directoryName!=null) {
				cloudFile = createCloudFileDirectory(fileShareName,directoryName).getFileReference(multipartFile.getOriginalFilename());
			}else {
				cloudFile = createCloudFileShare(fileShareName).getRootDirectoryReference().getFileReference((multipartFile.getOriginalFilename()));
			}
			cloudFile.upload(multipartFile.getInputStream(), multipartFile.getSize());
			logger.info("file uploaded");
		} catch (URISyntaxException | StorageException | IOException e) {
			logger.error(e.getMessage(),e);
		}
		return cloudFile.getUri();
	}


	/**
	 * Returns list of files from files share and it's directories
	 * @param fileShareName
	 * @return list of uri's
	 */
	public List<URI> listDirectoriesAndFiles(String fileShareName) {
		List<URI> uris = null;
		try {
			CloudFileDirectory cloudFileDirectory = createCloudFileShare(fileShareName).getRootDirectoryReference();
			uris = enumarateDirectoryContents(cloudFileDirectory);
		} catch (URISyntaxException | StorageException e) {
			logger.error(e.getMessage(),e);
		}
		return uris;
	}
	
	/**
	 * Iterate the directory and list the files
	 * @param cloudFileDirectory
	 * @return list of uri's
	 */
	private List<URI> enumarateDirectoryContents(CloudFileDirectory cloudFileDirectory) {
		Iterable<ListFileItem> fileItems = cloudFileDirectory.listFilesAndDirectories();
		for(Iterator<ListFileItem> iterator = fileItems.iterator(); iterator.hasNext();) {
			ListFileItem listFileIteam = iterator.next();
			if(listFileIteam.getClass() == CloudFileDirectory.class) {
				enumarateDirectoryContents((CloudFileDirectory)listFileIteam);
			}
			logger.info("File URI"+listFileIteam.getUri());
			uris.add(listFileIteam.getUri());
		}
		return uris;
	}

	
}
