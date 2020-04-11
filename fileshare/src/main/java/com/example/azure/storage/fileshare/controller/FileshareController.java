package com.example.azure.storage.fileshare.controller;

import java.net.URI;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.azure.storage.fileshare.service.FileshareService;
import com.microsoft.azure.storage.file.CloudFileDirectory;
import com.microsoft.azure.storage.file.CloudFileShare;

/**
 * @author harik
 *
 */
@RestController
public class FileshareController {

	private Logger logger = LoggerFactory.getLogger(FileshareController.class);
	
	@Autowired
	private FileshareService fileshareService;
		
	@PostMapping("/createfileshare")
	public ResponseEntity createFileShare(@RequestParam String fileShareName) {
		CloudFileShare fileShare =fileshareService.createCloudFileShare(fileShareName);
		logger.info("File directory"+fileShare.getUri());
		return ResponseEntity.ok(fileShare.getUri());
	}

	@PostMapping("/createfiledirectory")
	public ResponseEntity createFileDirectory(@RequestParam String fileShareName,@RequestParam String directoryName) {
		CloudFileDirectory fileDirectory = fileshareService.createCloudFileDirectory(fileShareName, directoryName);
		logger.info("File directory"+fileDirectory.getUri());
		return ResponseEntity.ok(fileDirectory.getUri());
	}
	
	@PostMapping("/uploadfile")
	public ResponseEntity uploadFile(@RequestParam String fileShareName,@RequestParam(required=false) String directoryName,@RequestParam MultipartFile multiPartFile) {
		URI uri= fileshareService.uploadFile(fileShareName, directoryName, multiPartFile);
		logger.info("File location"+uri);
		return ResponseEntity.ok(uri);
		
	}
	@GetMapping("/listfileshare")
	public ResponseEntity listFiles(@RequestParam String fileShareName) {
		List<URI> uris= fileshareService.listDirectoriesAndFiles(fileShareName);
		return ResponseEntity.ok(uris);
		
	}
}
