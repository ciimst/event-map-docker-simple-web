package com.imst.event.map.web.controller;

import java.io.IOException;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.imst.event.map.web.utils.MyStringUtils;
import com.imst.event.map.web.constant.SettingsE;
import com.imst.event.map.web.services.VideoStreamService;
import com.imst.event.map.web.utils.SettingsUtil;


@RequestMapping("/video")
@Controller
public class VideoController {
	

    private final VideoStreamService videoStreamService;
    
    @Autowired 
	private AmazonS3 amazonS3;

    public VideoController(VideoStreamService videoStreamService) {
    	
        this.videoStreamService = videoStreamService;
    }

	@GetMapping(value = "/get/{path}")
	public ResponseEntity<StreamingResponseBody> getWithMediaType(@PathVariable(name = "path") String base64Path, 
			 @RequestHeader(value = "Range", required = false) String httpRangeList) throws IOException {
		
		String imageRelativePath = new String(Base64.getDecoder().decode(base64Path.getBytes()));
		imageRelativePath = imageRelativePath.replace("\\", "/");
		imageRelativePath = imageRelativePath.replace("..", "");
		
//		String imageFullPath = Paths.get(SettingsUtil.getString(SettingsE.MEDIA_PATH), imageRelativePath).toString();
		
		int index = imageRelativePath.indexOf(SettingsUtil.getString(SettingsE.MEDIA_PATH));
		String imageFullPath = imageRelativePath.substring(index, imageRelativePath.length());
		
		imageFullPath = MyStringUtils.getStartAndEndWithSubstring(imageFullPath);
		
		S3Object object = amazonS3.getObject("imstbucket", imageFullPath);
	    S3ObjectInputStream finalObject = object.getObjectContent();

        final StreamingResponseBody body = outputStream -> {
            int numberOfBytesToWrite = 0;
            byte[] data = new byte[1024];
            while ((numberOfBytesToWrite = finalObject.read(data, 0, data.length)) != -1) {
                System.out.println("Writing some bytes..");
                outputStream.write(data, 0, numberOfBytesToWrite);
            }
            finalObject.close();
        };
		
		return new ResponseEntity<>(body, HttpStatus.OK);//videoStreamService.prepareContent(imageFullPath, "mp4", httpRangeList);
	}
    
}
