package com.imst.event.map.web.vo;

import java.util.Date;

import javax.persistence.Column;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SidebarAlertEventItem {

	private Integer id;
	private String dbName;	
	
	@Column(name ="event.id")
	private Integer eventId;
	
	@Column(name = "user.id")
	private Integer userId;
	
	@Column(name = "alert.id")
	private Integer alertId;
	
	@Column(name = "alert.name")
	private String alertName;	
	
	private Date createDate;
	private String eventIdDbName;
	
	private Boolean readState;
	
}
