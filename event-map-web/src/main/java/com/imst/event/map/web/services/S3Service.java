package com.imst.event.map.web.services;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class S3Service {


	@Value("${s3BucketName}")
	private String s3BucketName;

	@Autowired 
	private  AmazonS3 amazonS3;
	 
	 public byte[] download(String fileName) {
        try {
            S3Object object = amazonS3.getObject(s3BucketName, fileName);//event-map/images/media/2023/06/22/10.jpg
            S3ObjectInputStream objectContent = object.getObjectContent();
            return IOUtils.toByteArray(objectContent);
        } catch (AmazonServiceException | IOException e) {
        	log.error("Failed to download the file", e);
            throw new IllegalStateException("Failed to download the file", e);
        }
    }
}
