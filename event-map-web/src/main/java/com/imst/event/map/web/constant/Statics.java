package com.imst.event.map.web.constant;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.context.i18n.LocaleContextHolder;

import com.imst.event.map.hibernate.entity.EventType;
import com.imst.event.map.web.utils.DateUtils;
import com.imst.event.map.web.vo.DataSourceInfo;

public class Statics {
	
	public static Date timeLineStartDate = DateUtils.convertToDate("01.01.2000", DateUtils.TURKISH_DATE);
	public static final String DEFAULT_DB_NAME = "default";
	public static final Integer webUserLoginPermissionId = 38;

	
	public static Map<String, DataSourceInfo> tenantDataSourceInfoMap = new HashMap<>();
	public static Integer eventMediaListSize = 1000;
	
	public static String databaseEncryptPrivateKey = "qwerty";
	
	public static List<EventType> eventTypeList = new ArrayList<>();
	
	public static Collator sortedCollator() {
		
		String language = LocaleContextHolder.getLocale().getLanguage();
		Locale locale = new Locale(language);
		Collator collator = Collator.getInstance(locale);
		collator.setStrength(Collator.PRIMARY);
		
		return collator;
	}
	
}
