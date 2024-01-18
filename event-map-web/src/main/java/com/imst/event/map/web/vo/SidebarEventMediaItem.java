package com.imst.event.map.web.vo;

import java.util.Base64;

import javax.persistence.Column;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SidebarEventMediaItem {
	
	private Integer id;
	private String path;
	private String coverImagePath;
	@Column(name = "event.id")
	private Integer eventId;
	private Boolean isVideo;
	
	
	public String getPath() {
		
		if(path == null) {
			return "";
		}
		
//		if (path.startsWith("http")) {
//			
//			return path;
//		}
		
		return new String(Base64.getEncoder().encode(path.getBytes()));
	}
	
	public String getCoverImagePath() {
		
		if(coverImagePath == null) {
			return "";
		}
		
//		if (coverImagePath.startsWith("http")) {
//			
//			return coverImagePath;
//		}
		
		return new String(Base64.getEncoder().encode(coverImagePath.getBytes()));
	}
	
}
