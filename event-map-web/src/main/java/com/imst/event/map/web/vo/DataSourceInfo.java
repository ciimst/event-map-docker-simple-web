package com.imst.event.map.web.vo;

import javax.sql.DataSource;

import com.imst.event.map.web.constant.MultitenantDatabaseE;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class DataSourceInfo {
	
	private String name;
	private MultitenantDatabaseE multitenantDatabaseE;
	private DataSource datasource;

}
