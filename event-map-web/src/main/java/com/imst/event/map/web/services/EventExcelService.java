package com.imst.event.map.web.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import com.imst.event.map.web.constant.SettingsE;
import com.imst.event.map.web.constant.StateE;
import com.imst.event.map.web.constant.Statics;
import com.imst.event.map.web.constant.UserSettingsTypeE;
import com.imst.event.map.web.utils.ApplicationContextUtils;
import com.imst.event.map.web.utils.MyStringUtils;
import com.imst.event.map.web.utils.SettingsUtil;
import com.imst.event.map.web.utils.UserSettingsUtil;
import com.imst.event.map.web.vo.DataSourceInfo;
import com.imst.event.map.web.vo.EventExcelItem;

@Service
public class EventExcelService {

	@Autowired private UserSettingsService userSettingsService;
	
	@Value("${event.reserved1}")
    private String eventReserved1; 
	
	@Value("${event.reserved2}")
    private String eventReserved2;
	
	@Value("${event.reserved3}")
    private String eventReserved3;
	
	@Value("${event.reserved4}")
    private String eventReserved4;
	
	@Value("${event.reserved5}")
    private String eventReserved5;
	
	@Value("${using.helm.config}")
    private Boolean usingHelmConfig;
	
	public Workbook createExcel(List<EventExcelItem> totalList) {
		
		String eventReserved1Utf8 = eventReserved1;
		String eventReserved2Utf8 = eventReserved2;
		String eventReserved3Utf8 = eventReserved3;
		String eventReserved4Utf8 = eventReserved4;
		String eventReserved5Utf8 = eventReserved5;
		
		if (!usingHelmConfig) {
			eventReserved1Utf8 = MyStringUtils.toUTF8(eventReserved1);
			eventReserved2Utf8 = MyStringUtils.toUTF8(eventReserved2);
			eventReserved3Utf8 = MyStringUtils.toUTF8(eventReserved3);
			eventReserved4Utf8 = MyStringUtils.toUTF8(eventReserved4);
			eventReserved5Utf8 = MyStringUtils.toUTF8(eventReserved5);
		}
		
		List<String> commonColumnsList = new ArrayList<String>();
	    String[] columns = {SettingsE.EXCEL_EVENT_TITLE.getExcelTitle(), SettingsE.EXCEL_EVENT_SPOT.getExcelTitle(), SettingsE.EXCEL_EVENT_DESCRIPTION.getExcelTitle(), SettingsE.EXCEL_EVENT_DATE.getExcelTitle(), SettingsE.EXCEL_EVENT_TYPE.getExcelTitle(), SettingsE.EXCEL_EVENT_LAYER_NAME.getExcelTitle(), SettingsE.EXCEL_EVENT_GROUP_NAME.getExcelTitle(), SettingsE.EXCEL_EVENT_COUNTRY.getExcelTitle(), SettingsE.EXCEL_EVENT_CITY.getExcelTitle(), SettingsE.EXCEL_EVENT_RESERVED_LINK.getExcelTitle(), SettingsE.EXCEL_EVENT_RESERVED_TYPE.getExcelTitle(), SettingsE.EXCEL_EVENT_RESERVED_KEY.getExcelTitle(), SettingsE.EXCEL_EXCEL_EVENT_RESEERVED_ID.getExcelTitle(), SettingsE.EXCEL_EVENT_LATITUDE.getExcelTitle(), SettingsE.EXCEL_EVENT_LONGITUDE.getExcelTitle(), SettingsE.EXCEL_EVENT_BLACK_LIST_TAG.getExcelTitle(), SettingsE.EXCEL_EVENT_CREATE_USER.getExcelTitle(), SettingsE.EXCEL_EVENT_CREATE_DATE.getExcelTitle(), SettingsE.EXCEL_EVENT_UPDATE_DATE.getExcelTitle(), 
	    		SettingsE.EXCEL_EVENT_GROUP_COLOR.getExcelTitle(), SettingsE.EXCEL_EVENT_USER_ID.getExcelTitle(), SettingsE.EXCEL_EVENT_GROUP_ID.getExcelTitle(), SettingsE.EXCEL_EVENT_STATE.getExcelTitle(), eventReserved1Utf8, eventReserved2Utf8, eventReserved3Utf8, eventReserved4Utf8, eventReserved5Utf8};
	    List<String> columnsList = new ArrayList<String>();
	    boolean[] settings = {
	    		SettingsUtil.getBoolean(SettingsE.EXCEL_EVENT_TITLE),
	    		SettingsUtil.getBoolean(SettingsE.EXCEL_EVENT_SPOT),
	    		SettingsUtil.getBoolean(SettingsE.EXCEL_EVENT_DESCRIPTION),
	    		SettingsUtil.getBoolean(SettingsE.EXCEL_EVENT_DATE),
	    		SettingsUtil.getBoolean(SettingsE.EXCEL_EVENT_TYPE),
	    		SettingsUtil.getBoolean(SettingsE.EXCEL_EVENT_LAYER_NAME),
	    		SettingsUtil.getBoolean(SettingsE.EXCEL_EVENT_GROUP_NAME),
	    		SettingsUtil.getBoolean(SettingsE.EXCEL_EVENT_COUNTRY),
	    		SettingsUtil.getBoolean(SettingsE.EXCEL_EVENT_CITY),
	    		SettingsUtil.getBoolean(SettingsE.EXCEL_EVENT_RESERVED_LINK),
	    		SettingsUtil.getBoolean(SettingsE.EXCEL_EVENT_RESERVED_TYPE),
	    		SettingsUtil.getBoolean(SettingsE.EXCEL_EVENT_RESERVED_KEY),
	    		SettingsUtil.getBoolean(SettingsE.EXCEL_EXCEL_EVENT_RESEERVED_ID),
	    		SettingsUtil.getBoolean(SettingsE.EXCEL_EVENT_LATITUDE),
	    		SettingsUtil.getBoolean(SettingsE.EXCEL_EVENT_LONGITUDE),
	    		SettingsUtil.getBoolean(SettingsE.EXCEL_EVENT_BLACK_LIST_TAG),
	    		SettingsUtil.getBoolean(SettingsE.EXCEL_EVENT_CREATE_USER),
	    		SettingsUtil.getBoolean(SettingsE.EXCEL_EVENT_CREATE_DATE),
	    		SettingsUtil.getBoolean(SettingsE.EXCEL_EVENT_UPDATE_DATE),
	    		SettingsUtil.getBoolean(SettingsE.EXCEL_EVENT_GROUP_COLOR),
	    		SettingsUtil.getBoolean(SettingsE.EXCEL_EVENT_USER_ID),
	    		SettingsUtil.getBoolean(SettingsE.EXCEL_EVENT_GROUP_ID),
	    		SettingsUtil.getBoolean(SettingsE.EXCEL_EVENT_STATE),
	    		SettingsUtil.getBoolean(SettingsE.EXCEL_EVENT_RESERVED_1),
	    		SettingsUtil.getBoolean(SettingsE.EXCEL_EVENT_RESERVED_2),
	    		SettingsUtil.getBoolean(SettingsE.EXCEL_EVENT_RESERVED_3),
	    		SettingsUtil.getBoolean(SettingsE.EXCEL_EVENT_RESERVED_4),
	    		SettingsUtil.getBoolean(SettingsE.EXCEL_EVENT_RESERVED_5),
	    		
	    		};
	    
	    for (int i = 0; i < settings.length; i++) {
	    	if (settings[i]) {
	    		columnsList.add(columns[i]);
	    	}
	    }
	    
	    
	    UserSettingsUtil userSettingsUtil = userSettingsService.updateUserSettingsCacheAndGet();
		
		Map<String, Integer> userSettingsLastIdMap = new HashMap<>();
		userSettingsLastIdMap.put(Statics.DEFAULT_DB_NAME, null);

    	for(DataSourceInfo dataSourceInfo : Statics.tenantDataSourceInfoMap.values()) {
    		userSettingsLastIdMap.put(dataSourceInfo.getName(), null);
    	}
		
					
		
		
	    String[] userSettingsColumns = {UserSettingsTypeE.EXCEL_EVENT_TITLE.getExcelTitle(), UserSettingsTypeE.EXCEL_EVENT_SPOT.getExcelTitle(), UserSettingsTypeE.EXCEL_EVENT_DESCRIPTION.getExcelTitle(), UserSettingsTypeE.EXCEL_EVENT_DATE.getExcelTitle(), UserSettingsTypeE.EXCEL_EVENT_TYPE.getExcelTitle(), UserSettingsTypeE.EXCEL_EVENT_LAYER_NAME.getExcelTitle(), UserSettingsTypeE.EXCEL_EVENT_GROUP_NAME.getExcelTitle(), UserSettingsTypeE.EXCEL_EVENT_COUNTRY.getExcelTitle(), UserSettingsTypeE.EXCEL_EVENT_CITY.getExcelTitle(), UserSettingsTypeE.EXCEL_EVENT_RESERVED_LINK.getExcelTitle(), UserSettingsTypeE.EXCEL_EVENT_RESERVED_TYPE.getExcelTitle(), UserSettingsTypeE.EXCEL_EVENT_RESERVED_KEY.getExcelTitle(), UserSettingsTypeE.EXCEL_EXCEL_EVENT_RESEERVED_ID.getExcelTitle(), UserSettingsTypeE.EXCEL_EVENT_LATITUDE.getExcelTitle(), UserSettingsTypeE.EXCEL_EVENT_LONGITUDE.getExcelTitle(), UserSettingsTypeE.EXCEL_EVENT_BLACK_LIST_TAG.getExcelTitle(), UserSettingsTypeE.EXCEL_EVENT_CREATE_USER.getExcelTitle(), UserSettingsTypeE.EXCEL_EVENT_CREATE_DATE.getExcelTitle(), UserSettingsTypeE.EXCEL_EVENT_UPDATE_DATE.getExcelTitle(), 
	    		UserSettingsTypeE.EXCEL_EVENT_GROUP_COLOR.getExcelTitle(), UserSettingsTypeE.EXCEL_EVENT_USER_ID.getExcelTitle(), UserSettingsTypeE.EXCEL_EVENT_GROUP_ID.getExcelTitle(), UserSettingsTypeE.EXCEL_EVENT_STATE.getExcelTitle(), eventReserved1Utf8, eventReserved2Utf8, eventReserved3Utf8, eventReserved4Utf8, eventReserved5Utf8};
	    List<String> userSettingsColumnsList = new ArrayList<String>();

	    boolean[] userSettings = {
	    		userSettingsUtil.getBoolean(UserSettingsTypeE.EXCEL_EVENT_TITLE),
	    		userSettingsUtil.getBoolean(UserSettingsTypeE.EXCEL_EVENT_SPOT),
	    		userSettingsUtil.getBoolean(UserSettingsTypeE.EXCEL_EVENT_DESCRIPTION),
	    		userSettingsUtil.getBoolean(UserSettingsTypeE.EXCEL_EVENT_DATE),
	    		userSettingsUtil.getBoolean(UserSettingsTypeE.EXCEL_EVENT_TYPE),
	    		userSettingsUtil.getBoolean(UserSettingsTypeE.EXCEL_EVENT_LAYER_NAME),
	    		userSettingsUtil.getBoolean(UserSettingsTypeE.EXCEL_EVENT_GROUP_NAME),
	    		userSettingsUtil.getBoolean(UserSettingsTypeE.EXCEL_EVENT_COUNTRY),
	    		userSettingsUtil.getBoolean(UserSettingsTypeE.EXCEL_EVENT_CITY),
	    		userSettingsUtil.getBoolean(UserSettingsTypeE.EXCEL_EVENT_RESERVED_LINK),
	    		userSettingsUtil.getBoolean(UserSettingsTypeE.EXCEL_EVENT_RESERVED_TYPE),
	    		userSettingsUtil.getBoolean(UserSettingsTypeE.EXCEL_EVENT_RESERVED_KEY),
	    		userSettingsUtil.getBoolean(UserSettingsTypeE.EXCEL_EXCEL_EVENT_RESEERVED_ID),
	    		userSettingsUtil.getBoolean(UserSettingsTypeE.EXCEL_EVENT_LATITUDE),
	    		userSettingsUtil.getBoolean(UserSettingsTypeE.EXCEL_EVENT_LONGITUDE),
	    		userSettingsUtil.getBoolean(UserSettingsTypeE.EXCEL_EVENT_BLACK_LIST_TAG),
	    		userSettingsUtil.getBoolean(UserSettingsTypeE.EXCEL_EVENT_CREATE_USER),
	    		userSettingsUtil.getBoolean(UserSettingsTypeE.EXCEL_EVENT_CREATE_DATE),
	    		userSettingsUtil.getBoolean(UserSettingsTypeE.EXCEL_EVENT_UPDATE_DATE),
	    		userSettingsUtil.getBoolean(UserSettingsTypeE.EXCEL_EVENT_GROUP_COLOR),
	    		userSettingsUtil.getBoolean(UserSettingsTypeE.EXCEL_EVENT_USER_ID),
	    		userSettingsUtil.getBoolean(UserSettingsTypeE.EXCEL_EVENT_GROUP_ID),
	    		userSettingsUtil.getBoolean(UserSettingsTypeE.EXCEL_EVENT_STATE),
	    		userSettingsUtil.getBoolean(UserSettingsTypeE.EXCEL_EVENT_RESERVED_1),
	    		userSettingsUtil.getBoolean(UserSettingsTypeE.EXCEL_EVENT_RESERVED_2),
	    		userSettingsUtil.getBoolean(UserSettingsTypeE.EXCEL_EVENT_RESERVED_3),
	    		userSettingsUtil.getBoolean(UserSettingsTypeE.EXCEL_EVENT_RESERVED_4),
	    		userSettingsUtil.getBoolean(UserSettingsTypeE.EXCEL_EVENT_RESERVED_5),
		
		};

	    // excel için bakıyoruz
	    boolean anyTrue = false;
	    for (boolean userSettingsBoolean : userSettings) {
	    	anyTrue |= userSettingsBoolean;
		}
	    
	    for (int i = 0; i < userSettings.length; i++) {
	    	if (userSettings[i] || !anyTrue) {
	    		userSettingsColumnsList.add(userSettingsColumns[i]);
	    	}
	    }
	    
	    
	    for (String column : columnsList) {
	    	for (String userSettingsColumn : userSettingsColumnsList) {
		    	if (column.equals(userSettingsColumn)) {
		    		commonColumnsList.add(column);
		    		break;
		    	}
	    	}
	    }
	    
	    
	    
	    Workbook workbook = new XSSFWorkbook();

	    Sheet sheet = workbook.createSheet("events");

	    Font headerFont = workbook.createFont();

	    headerFont.setBold(true);

	    headerFont.setFontHeightInPoints((short) 14);

	    headerFont.setColor(IndexedColors.BLACK.getIndex());

	    CellStyle headerCellStyle = workbook.createCellStyle();

	    headerCellStyle.setFont(headerFont);

	    Row headerRow = sheet.createRow(0);

	    for (int i = 0; i < commonColumnsList.size(); i++) {
	
		   Cell cell = headerRow.createCell(i);
		
		   cell.setCellValue(commonColumnsList.get(i));
		
		   cell.setCellStyle(headerCellStyle);
	    }

	    int rowNum = 1;

	    for (EventExcelItem event : totalList) {
	    	
	    	
	    	String language = LocaleContextHolder.getLocale().getLanguage();
			Locale locale = new Locale(language);
			
			String eventTypeName = ApplicationContextUtils.getMessage("icons." + event.getEventTypeCode(), locale);
			eventTypeName = eventTypeName.equals("icons." + event.getEventTypeCode()) ? event.getEventTypeName() : eventTypeName;
			
		    Row row = sheet.createRow(rowNum++);
		    int counter = 0;
		    
		    if(!anyTrue || (userSettingsUtil.getBoolean(UserSettingsTypeE.EXCEL_EVENT_TITLE) && commonColumnsList.contains(UserSettingsTypeE.EXCEL_EVENT_TITLE.getExcelTitle()))) {
		    	row.createCell(counter).setCellValue(event.getTitle()); 
		    	counter++;
		    }
		    if(!anyTrue || (userSettingsUtil.getBoolean(UserSettingsTypeE.EXCEL_EVENT_SPOT) && commonColumnsList.contains(UserSettingsTypeE.EXCEL_EVENT_SPOT.getExcelTitle()))) {
			    row.createCell(counter).setCellValue(event.getSpot());
			    counter++;
		    }
		    if(!anyTrue || (userSettingsUtil.getBoolean(UserSettingsTypeE.EXCEL_EVENT_DESCRIPTION) && commonColumnsList.contains(UserSettingsTypeE.EXCEL_EVENT_DESCRIPTION.getExcelTitle()))) {
			    row.createCell(counter).setCellValue(event.getDescription());
			    counter++;
		    }
		    if(!anyTrue || (userSettingsUtil.getBoolean(UserSettingsTypeE.EXCEL_EVENT_DATE) && commonColumnsList.contains(UserSettingsTypeE.EXCEL_EVENT_DATE.getExcelTitle()))) {
			    row.createCell(counter).setCellValue(event.getEventDateStr());
			    counter++;
		    }
		    if(!anyTrue || (userSettingsUtil.getBoolean(UserSettingsTypeE.EXCEL_EVENT_TYPE) && commonColumnsList.contains(UserSettingsTypeE.EXCEL_EVENT_TYPE.getExcelTitle()))) {
			    row.createCell(counter).setCellValue(eventTypeName);
			    counter++;
		    }
		    if(!anyTrue || (userSettingsUtil.getBoolean(UserSettingsTypeE.EXCEL_EVENT_LAYER_NAME) && commonColumnsList.contains(UserSettingsTypeE.EXCEL_EVENT_LAYER_NAME.getExcelTitle()))) {
			    row.createCell(counter).setCellValue(event.getLayerName());
			    counter++;
		    }
		    if(!anyTrue || (userSettingsUtil.getBoolean(UserSettingsTypeE.EXCEL_EVENT_GROUP_NAME) && commonColumnsList.contains(UserSettingsTypeE.EXCEL_EVENT_GROUP_NAME.getExcelTitle()))) {
			    row.createCell(counter).setCellValue(event.getEventGroupName());
			    counter++;
		    }
		    if(!anyTrue || (userSettingsUtil.getBoolean(UserSettingsTypeE.EXCEL_EVENT_COUNTRY) && commonColumnsList.contains(UserSettingsTypeE.EXCEL_EVENT_COUNTRY.getExcelTitle()))) {
			    row.createCell(counter).setCellValue(event.getCountry());
			    counter++;
		    }
		    if(!anyTrue || (userSettingsUtil.getBoolean(UserSettingsTypeE.EXCEL_EVENT_CITY) && commonColumnsList.contains(UserSettingsTypeE.EXCEL_EVENT_CITY.getExcelTitle()))) {
			    row.createCell(counter).setCellValue(event.getCity());
			    counter++;
		    }
		    if(!anyTrue || (userSettingsUtil.getBoolean(UserSettingsTypeE.EXCEL_EVENT_RESERVED_LINK) && commonColumnsList.contains(UserSettingsTypeE.EXCEL_EVENT_RESERVED_LINK.getExcelTitle()))) {
			    row.createCell(counter).setCellValue(event.getReservedLink());
			    counter++;
		    }
		    if(!anyTrue || (userSettingsUtil.getBoolean(UserSettingsTypeE.EXCEL_EVENT_RESERVED_TYPE) && commonColumnsList.contains(UserSettingsTypeE.EXCEL_EVENT_RESERVED_TYPE.getExcelTitle()))) {
			    row.createCell(counter).setCellValue(event.getReservedType());
			    counter++;
		    }
		    if(!anyTrue || (userSettingsUtil.getBoolean(UserSettingsTypeE.EXCEL_EVENT_RESERVED_KEY) && commonColumnsList.contains(UserSettingsTypeE.EXCEL_EVENT_RESERVED_KEY.getExcelTitle()))) {
			    row.createCell(counter).setCellValue(event.getReservedKey());
			    counter++;
		    }
		    if(!anyTrue || (userSettingsUtil.getBoolean(UserSettingsTypeE.EXCEL_EXCEL_EVENT_RESEERVED_ID) && commonColumnsList.contains(UserSettingsTypeE.EXCEL_EXCEL_EVENT_RESEERVED_ID.getExcelTitle()))) {
			    row.createCell(counter).setCellValue(event.getReservedId());
			    counter++;
		    }
		    if(!anyTrue || (userSettingsUtil.getBoolean(UserSettingsTypeE.EXCEL_EVENT_LATITUDE) && commonColumnsList.contains(UserSettingsTypeE.EXCEL_EVENT_LATITUDE.getExcelTitle()))) {
			    row.createCell(counter).setCellValue(event.getLatitude());
			    counter++;
		    }
		    if(!anyTrue || (userSettingsUtil.getBoolean(UserSettingsTypeE.EXCEL_EVENT_LONGITUDE) && commonColumnsList.contains(UserSettingsTypeE.EXCEL_EVENT_LONGITUDE.getExcelTitle()))) {
			    row.createCell(counter).setCellValue(event.getLongitude());
			    counter++;
		    }
		    if(!anyTrue || (userSettingsUtil.getBoolean(UserSettingsTypeE.EXCEL_EVENT_BLACK_LIST_TAG) && commonColumnsList.contains(UserSettingsTypeE.EXCEL_EVENT_BLACK_LIST_TAG.getExcelTitle()))) {
			    row.createCell(counter).setCellValue(event.getBlackListTag());
			    counter++;
		    }
		    if(!anyTrue || (userSettingsUtil.getBoolean(UserSettingsTypeE.EXCEL_EVENT_CREATE_USER) && commonColumnsList.contains(UserSettingsTypeE.EXCEL_EVENT_CREATE_USER.getExcelTitle()))) {
			    row.createCell(counter).setCellValue(event.getCreateUser());
			    counter++;
		    }
		    if(!anyTrue || (userSettingsUtil.getBoolean(UserSettingsTypeE.EXCEL_EVENT_CREATE_DATE) && commonColumnsList.contains(UserSettingsTypeE.EXCEL_EVENT_CREATE_DATE.getExcelTitle()))) {
			    row.createCell(counter).setCellValue(event.getCreateDateStr());
			    counter++;
		    }
		    if(!anyTrue || (userSettingsUtil.getBoolean(UserSettingsTypeE.EXCEL_EVENT_UPDATE_DATE) && commonColumnsList.contains(UserSettingsTypeE.EXCEL_EVENT_UPDATE_DATE.getExcelTitle()))) {
			    row.createCell(counter).setCellValue(event.getUpdateDateStr());  
			    counter++;
		    }
		    if(!anyTrue || (userSettingsUtil.getBoolean(UserSettingsTypeE.EXCEL_EVENT_GROUP_COLOR) && commonColumnsList.contains(UserSettingsTypeE.EXCEL_EVENT_GROUP_COLOR.getExcelTitle()))) {
			    row.createCell(counter).setCellValue(event.getEventGroupColor());			    
			    counter++;
		    }
		    if(!anyTrue || (userSettingsUtil.getBoolean(UserSettingsTypeE.EXCEL_EVENT_USER_ID) && commonColumnsList.contains(UserSettingsTypeE.EXCEL_EVENT_USER_ID.getExcelTitle()))) {
			    row.createCell(counter).setCellValue(event.getUserId());
			    counter++;
		    }
		    if(!anyTrue || (userSettingsUtil.getBoolean(UserSettingsTypeE.EXCEL_EVENT_GROUP_ID) && commonColumnsList.contains(UserSettingsTypeE.EXCEL_EVENT_GROUP_ID.getExcelTitle()))) {
			    row.createCell(counter).setCellValue(event.getGroupId());
			    counter++;
		    }
		    if(!anyTrue || (userSettingsUtil.getBoolean(UserSettingsTypeE.EXCEL_EVENT_STATE) && commonColumnsList.contains(UserSettingsTypeE.EXCEL_EVENT_STATE.getExcelTitle()))) {
			    row.createCell(counter).setCellValue(event.getState() != null && event.getStateId().equals(StateE.TRUE.getValue()) ? "Aktif" : "Pasif");	
			    counter++;
		    }
		    		
		    if(!anyTrue || (userSettingsUtil.getBoolean(UserSettingsTypeE.EXCEL_EVENT_RESERVED_1) && commonColumnsList.contains(eventReserved1Utf8))) {
			    row.createCell(counter).setCellValue(event.getReserved1());
			    counter++;
		    }
		    if(!anyTrue || (userSettingsUtil.getBoolean(UserSettingsTypeE.EXCEL_EVENT_RESERVED_2) && commonColumnsList.contains(eventReserved2Utf8))) {
			    row.createCell(counter).setCellValue(event.getReserved2());
			    counter++;
		    }
		    if(!anyTrue || (userSettingsUtil.getBoolean(UserSettingsTypeE.EXCEL_EVENT_RESERVED_3) && commonColumnsList.contains(eventReserved3Utf8))) {
			    row.createCell(counter).setCellValue(event.getReserved3());
			    counter++;
		    }
		    if(!anyTrue || (userSettingsUtil.getBoolean(UserSettingsTypeE.EXCEL_EVENT_RESERVED_4) && commonColumnsList.contains(eventReserved4Utf8))) {
			    row.createCell(counter).setCellValue(event.getReserved4());
			    counter++;
		    }
		    if(!anyTrue || (userSettingsUtil.getBoolean(UserSettingsTypeE.EXCEL_EVENT_RESERVED_5) && commonColumnsList.contains(eventReserved5Utf8))) {
			    row.createCell(counter).setCellValue(event.getReserved5());
			    counter++;
		    }
	    }
	    
	  
	    
	    Integer i=0;
	    for (String commonColumn : commonColumnsList) {

	    	if(commonColumn.equals(UserSettingsTypeE.EXCEL_EVENT_TITLE.getExcelTitle()) || commonColumn.equals(UserSettingsTypeE.EXCEL_EVENT_DESCRIPTION.getExcelTitle()) || commonColumn.equals(UserSettingsTypeE.EXCEL_EVENT_SPOT.getExcelTitle())) {
	    		sheet.setColumnWidth(i, 40 * 256);
	    	}else { 
	    		sheet.autoSizeColumn(i);
	    	}
	    	
	    	i++;
	    }
	    
	    return workbook;
	}
}
