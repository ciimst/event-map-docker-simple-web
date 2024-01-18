package com.imst.event.map.hibernate.entity.redis;

import org.springframework.data.redis.core.RedisHash;

@RedisHash("event-map:UserLastPage")
public class UserLastPage{
  
	private Integer id; //userId
    private String url;
    private String updateDate;


    public UserLastPage(Integer id, String url, String updateDate) {
		this.id = id;
		this.url = url;
		this.updateDate = updateDate;	
	}
	

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}


	public String getUpdateDate() {
		return updateDate;
	}


	public void setUpdateDate(String updateDate) {
		this.updateDate = updateDate;
	}
	
	

}