package com.imst.event.map.web.constant;

import com.imst.event.map.web.db.dao.MasterDao;
import com.imst.event.map.web.db.dao.MasterDaoGeneric;
import com.imst.event.map.web.db.dao.MasterDaoMssql;
//import com.imst.event.map.web.db.dao.MasterDaoOracle;
import com.imst.event.map.web.utils.ApplicationContextUtils;

public enum MultitenantDatabaseE {

	ORACLE("oracle", false), MSSQL("mssql", false), GENERIC("generic", true), MASTER(Statics.DEFAULT_DB_NAME, false);
	
	private String name;
	private boolean isGeneric;
	
	private MultitenantDatabaseE(String name, boolean isGeneric) {

		this.name = name;
		this.isGeneric = isGeneric;
	}
	
	public String getName() {
		return name;
	}
	
	public boolean isGeneric() {
		return isGeneric;
	}
	
	public static boolean isGeneric(String name) {
		
		boolean generic = true;
		for (MultitenantDatabaseE multitenantDatabaseE : values()) {
			if(!multitenantDatabaseE.isGeneric() && name.equals(multitenantDatabaseE.getName())) {
					
				generic = false;
				break;
				
			}
		}
		
		return generic;
	}
	
	public static MultitenantDatabaseE getMultitenantDatabaseE(String database) {
		
		for (MultitenantDatabaseE multitenantDatabaseE : values()) {
		
			if( database.equals(multitenantDatabaseE.getName()) ) {
				return multitenantDatabaseE;
			}
		}
		
		return GENERIC;
	}
	
		
	public MasterDao getMasterDAOBean() {
		
		switch (this) {
//			case ORACLE:
//				return ApplicationContextUtils.getBean(MasterDaoOracle.class);
	
			case MSSQL:
				return ApplicationContextUtils.getBean(MasterDaoMssql.class);
				
			case MASTER:
				return ApplicationContextUtils.getBean(MasterDao.class);
				
			default:
				return ApplicationContextUtils.getBean(MasterDaoGeneric.class);
		}
		
	}
	
}
