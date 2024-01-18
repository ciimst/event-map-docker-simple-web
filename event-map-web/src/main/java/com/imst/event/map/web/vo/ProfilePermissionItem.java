package com.imst.event.map.web.vo;

import javax.persistence.Column;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ProfilePermissionItem {

	private Integer id;
	@Column(name = "profile.id")
	private Integer profileId;
	@Column(name = "permission.id")
	private Integer permissionId;
	@Column(name = "permission.name")
	private String permissionName;

}
