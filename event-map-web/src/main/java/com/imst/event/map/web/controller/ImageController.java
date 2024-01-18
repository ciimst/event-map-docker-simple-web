package com.imst.event.map.web.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Base64;
import java.util.Optional;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.imst.event.map.hibernate.entity.EventType;
import com.imst.event.map.web.constant.SettingsE;
import com.imst.event.map.web.constant.Statics;
import com.imst.event.map.web.services.S3Service;
import com.imst.event.map.web.utils.MyStringUtils;
import com.imst.event.map.web.utils.SettingsUtil;

@RequestMapping("/image")
@Controller
public class ImageController {
	
	@Autowired private S3Service s3Service;
	@Value("${s3BucketName}")
	private String s3BucketName;
	
	@Value("${s3Region}")
	private String s3Region;
	
//	@GetMapping(value="/get/{path}", headers="accept=image/tiff", produces="image/tiff")
//	public ResponseEntity<byte[]> getTiffImage() throws IOException {
//		
//		String pathBase64 = "MjAyMi8wOC8yMS9maWxlXzM0ODkxMDc3MDc3NjMxODMwNzJfMjAyMi0wOC0yMV8wMC0xNi0zMi50aWY=";
//		byte[] outputData = getImageWithPath(SettingsUtil.getString(SettingsE.MEDIA_PATH), pathBase64);
//
//		return ResponseEntity
//				.ok()
//				.contentLength(outputData.length)
//				.contentType(MediaType.valueOf("image/tiff"))
//				.body(outputData);
//	}
	

	@RequestMapping(value = "/get/{path}", method = RequestMethod.GET, produces = MediaType.IMAGE_JPEG_VALUE)
	@ResponseBody
	public ResponseEntity<byte[]> getImageWithMediaType(@PathVariable(name = "path") String base64Path) throws IOException {

		 HttpHeaders headers = new HttpHeaders();

			ResponseEntity<byte[]> responseEntity = ResponseEntity.ok()
					.headers(headers)
					.cacheControl(CacheControl.maxAge(Duration.ofDays(1)))
					.body(getImageWithPath(SettingsUtil.getString(SettingsE.MEDIA_PATH), base64Path));
			return responseEntity;
	}
	
	@RequestMapping(value = "/static/{path}", method = RequestMethod.GET, produces = MediaType.IMAGE_JPEG_VALUE)
	@ResponseBody
	public byte[] getImageWithStaticType(@PathVariable(name = "path") String base64Path) throws IOException {

		return getImageWithPath(SettingsUtil.getString(SettingsE.STATIC_IMAGE_ROOT_PATH), base64Path);
	}
	
	private byte[] getImageWithPath(String rootPath, String relativeBase64Path) throws IOException {


		
		String imageRelativePath = new String(Base64.getDecoder().decode(relativeBase64Path.getBytes()));
		imageRelativePath = imageRelativePath.replace("\\", "/");
		imageRelativePath = imageRelativePath.replace("..", "");
		
		byte[] imageByte = null;
		//static medialar için
		if(rootPath.equals(SettingsUtil.getString(SettingsE.STATIC_IMAGE_ROOT_PATH)) || (!imageRelativePath.contains(s3BucketName) && !imageRelativePath.contains(s3Region))) {
			
			
			String imageFullPath = Paths.get(rootPath, imageRelativePath).toString();
			
			File initialFile = new File(imageFullPath);
			InputStream in = new FileInputStream(initialFile);
	
			imageByte = IOUtils.toByteArray(in);
		}
		//s3 için.
		else if(rootPath.equals(SettingsUtil.getString(SettingsE.MEDIA_PATH))) {

			int index = imageRelativePath.indexOf(SettingsUtil.getString(SettingsE.MEDIA_PATH));
			String path = imageRelativePath.substring(index, imageRelativePath.length());			
			path = MyStringUtils.getStartAndEndWithSubstring(path);
			imageByte = s3Service.download(path);
		}
		
		
		return imageByte;
	}

    @RequestMapping(value = "/markerImg", method = RequestMethod.GET)
    public ResponseEntity<byte[]> getImage(@RequestParam(name = "eventTypeId") Integer eventTypeId ,@RequestParam(name = "color") String color, @RequestParam(name="selectedIconControl") boolean selectedIconControl) throws UnsupportedEncodingException {
		
    	String selectionColor = "none";
		String iconColorEnd = "black";

		Optional<EventType> eventType = Statics.eventTypeList.stream().filter(item -> item.getId().equals(eventTypeId)).findFirst();
		String content = eventType.get().getImage();

		if(selectedIconControl == true) {		
			selectionColor = "#fdc510";
		}
    	String SVG = "<svg version=\"1.1\" style= \" z-index:1000 \" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" x=\"0px\" y=\"0px\" width=\"100px\" height=\"100px\" viewBox=\"0 0 100 100\" enable-background=\"new 0 0 100 100\" xml:space=\"preserve\">"
				 + "	<circle id=\"lp\" fill=\""+selectionColor+"\" cx=\"49.978\" cy=\"50.008\" r=\"49.995\"/>"
				 + "	<linearGradient id=\"SVGID_1_\" gradientUnits=\"userSpaceOnUse\" x1=\"49.9995\" y1=\"8.5\" x2=\"0\" y2=\"91.5005\">"
				 + "	<stop  offset=\"0\" style=\"stop-color:"+color.toString()+"\"/><stop  offset=\"1\" style=\"stop-color:"+iconColorEnd+"\"/></linearGradient>"
				 + "	<circle fill=\"url(#SVGID_1_)\" cx=\"50\" cy=\"50\" r=\"41.5\"/><g>"
				 + "	"+content+" </g></svg>";
		
    	byte[] decodedBytes = SVG.getBytes("UTF-8");
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(new MediaType("image", "svg+xml"));

		ResponseEntity<byte[]> svgEntity = ResponseEntity.ok()
				.headers(headers)
				.cacheControl(CacheControl.maxAge(Duration.ofMinutes(60)))
				.body(decodedBytes);
		return svgEntity;
    }
    
    


    
	
}
