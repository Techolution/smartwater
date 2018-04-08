package com.techolution.mauritius.smartwater.reports.service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;
import com.techolution.mauritius.smartwater.reports.CustomProperties;

@Component
public class AzureFileService {

	
	private Log log = LogFactory.getLog(AzureFileService.class);
	@Autowired
	FileClientProvider azureFileClientProvider;
	
	@Autowired
	CustomProperties customProperties;
	
	public String uploadFile(String localFilePath,String fileName){
		
		  String path=null;
		/*CloudFileClient fileClient = null;
        CloudFileShare fileShare1 = null;
        CloudFileShare fileShare2 = null;
		
      
        try {
			fileClient = azureFileClientProvider.getFileClientReference();
			fileShare1= fileClient.getShareReference(customProperties.getFilesharename());
			CloudFileDirectory rootDir1 = fileShare1.getRootDirectoryReference();
			CloudFile file1 = rootDir1.getFileReference(fileName);
			log.debug("Created file in azure:"+fileName);
			//file1.
			file1.uploadFromFile(localFilePath);
			log.debug("Uploaded local file to azure:"+localFilePath);
			URI uri=file1.getUri();
			log.debug("File URL is:"+uri.toString());
			path= uri.toString();
			
		} catch (InvalidKeyException | RuntimeException | IOException | URISyntaxException | StorageException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
		try {
			CloudBlobClient blobClient= azureFileClientProvider.getFileClientReference();
			CloudBlobContainer container=blobClient.getContainerReference(customProperties.getContainername());
			CloudBlockBlob blockBlob = container.getBlockBlobReference(fileName);
			blockBlob.uploadFromFile(localFilePath);
			URI uri=blockBlob.getUri();
			path=uri.toString();
		} catch (InvalidKeyException | RuntimeException | IOException | URISyntaxException | StorageException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        return path;
	}
}
