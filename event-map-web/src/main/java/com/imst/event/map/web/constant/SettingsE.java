package com.imst.event.map.web.constant;


import java.util.HashMap;

public enum SettingsE {
	
	MEDIA_PATH("mediaPath"),
	PAGE_REFRESH_TIME_IN_SECOND("WebPageRefreshTimeInSec"), 
	PAGE_EVENT_COUNT_PER_LOAD("WebPageEventCountPerLoad"),
	STATIC_IMAGE_ROOT_PATH("StaticImageRootPath"),
	ADMIN_TITLE_FOR_LOGIN("AdminTitleForLogin"),
	ADMIN_TEXT_FOR_LOGIN("AdminTextForLogin"),
	ADMIN_LOGO_IMAGE("AdminLogoImage"),
	ADMIN_LOGIN_IMAGE("AdminLoginImage"),
	WEB_TITLE_FOR_LOGIN("WebTitleForLogin"),
	WEB_TEXT_FOR_LOGIN("WebTextForLogin"),
	WEB_LOGO_IMAGE("WebLogoImage"),
	WEB_LOGIN_IMAGE("WebLoginImage"),
	FAVICON_IMAGE("FaviconImage"),
	WEB_LOAD_ALL_EVENTS("WebLoadAllEvents"),
	WEB_TABLE_REFRESH_TIME_IN_SECOND("WebTableRefreshTimeInSec"),
	MAX_COUNT_EVENTS_EXCEL("MaxCountEventsExcel"),
	OPTIONAL_BUTTON_VALUE("OptionalButtonValue"),
	EXCEL_EVENT_TITLE("ExcelEventTitle", "Başlık"),
	EXCEL_EVENT_SPOT("ExcelEventSpot", "Kısa Açıklama"),
	EXCEL_EVENT_DESCRIPTION("ExcelEventDescription", "Açıklama"),
	EXCEL_EVENT_DATE("ExcelEventDate", "Olay Tarihi"),
	EXCEL_EVENT_TYPE("ExcelEventType", "Olay Türü"),
	EXCEL_EVENT_LAYER_NAME("ExcelEventLayerName", "Katman"),
	EXCEL_EVENT_GROUP_NAME("ExcelEventGroupName", "Olay Grubu"),
	EXCEL_EVENT_COUNTRY("ExcelEventCountry", "Ülke"),
	EXCEL_EVENT_CITY("ExcelEventCity", "Şehir"),
	EXCEL_EVENT_RESERVED_LINK("ExcelEventReservedLink", "Ayrılmış Bağlantı"),
	EXCEL_EVENT_RESERVED_TYPE("ExcelEventReservedType", "Ayrılmış Tür"),
	EXCEL_EVENT_RESERVED_KEY("ExcelEventReservedKey", "Ayrılmış Anahtar"),
	EXCEL_EXCEL_EVENT_RESEERVED_ID("ExcelEventReservedId", "Ayrılmış Kimlik(ID)"),
	EXCEL_EVENT_LATITUDE("ExcelEventLatitude", "Enlem"),
	EXCEL_EVENT_LONGITUDE("ExcelEventLongitude", "Boylam"),
	EXCEL_EVENT_BLACK_LIST_TAG("ExcelEventBlackListTag", "Black List Tag"),
	EXCEL_EVENT_CREATE_USER("ExcelEventCreateUser", "Oluşturan Kullanıcı"),
	EXCEL_EVENT_CREATE_DATE("ExcelEventCreateDate", "Oluşturulma Tarihi"),
	EXCEL_EVENT_UPDATE_DATE("ExcelEventUpdateDate", "Güncellenme Tarihi"),
	EXCEL_EVENT_GROUP_COLOR("ExcelEventGroupColor", "Olay Grup Rengi"),
	EXCEL_EVENT_USER_ID("ExcelEventUserId", "Kullanıcı Id"),
	EXCEL_EVENT_GROUP_ID("ExcelEventGroupId", "Grup Id"),
	EXCEL_EVENT_STATE("ExcelEventState", "Durum"),
	EXCEL_EVENT_RESERVED_1("ExcelEventReserved1"),
	EXCEL_EVENT_RESERVED_2("ExcelEventReserved2"),
	EXCEL_EVENT_RESERVED_3("ExcelEventReserved3"),
	EXCEL_EVENT_RESERVED_4("ExcelEventReserved4"),
	EXCEL_EVENT_RESERVED_5("ExcelEventReserved5"),
	PAGE_BLACK_LIST_COUNT_PER_LOAD("WebPageBlackListCountPerLoad"),
	DATABASE_BACKUP_COUNT("DatabaseBackupCount"),
	DATABASE_BACKUP_INTERVAL("DatabaseBackupInterval")
	;
	
	private String name;
	private String excelTitle;
	
	private SettingsE(String name) {
		this.name = name;
	}
	
	private SettingsE(String name, String excelTitle) {
		this.name = name;
		this.excelTitle = excelTitle;
	}
	
	private static HashMap<String, SettingsE> map = new HashMap<>();
	
	public String getName() {
		return name;
	}
	
	public String getExcelTitle() {
		return excelTitle;
	}
	
	public static SettingsE getSettings(String key) {
		return map.get(key) == null ? null : map.get(key);
	}
	
	static {
		
		for (SettingsE settingsE : SettingsE.values()) {
			map.put(settingsE.name(), settingsE);
		}
	}
	
}
