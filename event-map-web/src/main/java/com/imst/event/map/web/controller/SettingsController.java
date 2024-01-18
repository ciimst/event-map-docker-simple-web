package com.imst.event.map.web.controller;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.imst.event.map.hibernate.entity.Layer;
import com.imst.event.map.hibernate.entity.Settings;
import com.imst.event.map.hibernate.entity.User;
import com.imst.event.map.hibernate.entity.UserSettings;
import com.imst.event.map.hibernate.entity.UserSettingsType;
import com.imst.event.map.web.constant.LogTypeE;
import com.imst.event.map.web.constant.Statics;
import com.imst.event.map.web.constant.UserSettingsTypeE;
import com.imst.event.map.web.db.repositories.SettingsRepository;
import com.imst.event.map.web.db.repositories.UserSettingsRepository;
import com.imst.event.map.web.db.repositories.UserSettingsTypeRepository;
import com.imst.event.map.web.security.UserItemDetails;
import com.imst.event.map.web.services.DBLogger;
import com.imst.event.map.web.services.EventService;
import com.imst.event.map.web.services.UserLayerPermissionService;
import com.imst.event.map.web.utils.ApplicationContextUtils;
import com.imst.event.map.web.utils.DateUtils;
import com.imst.event.map.web.vo.EventTypeItem;
import com.imst.event.map.web.vo.LayerSimpleItem;
import com.imst.event.map.web.vo.UserLayerPermissionItem;
import com.imst.event.map.web.vo.UserSettingsEventGroupItem;
import com.imst.event.map.web.vo.UserSettingsItem;
import com.imst.event.map.web.vo.UserSettingsTypeItem;
import com.imst.event.map.web.vo.UsertSettingsTypeColumn;

import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
@RequestMapping("/settings")
public class SettingsController {
	
	@Autowired
	private UserSettingsRepository userSettingsRepository;
	@Autowired
	private UserSettingsTypeRepository userSettingsTypeRepository;
	@Autowired
	private SettingsRepository settingsRepository;
	@Autowired
	private DBLogger dbLogger;
	
	@Autowired private UserLayerPermissionService userLayerPermissionService;
	@Autowired private EventService eventService;

	@RequestMapping(value = {"/{layerId}/{groupName}"}, method = RequestMethod.GET)
	public ModelAndView getUserPage(@PathVariable(value = "layerId") String currentLayerGuid, @PathVariable(value = "groupName") String groupName) {
		
		ModelAndView modelAndView = new ModelAndView("page/settings");
		
		UserItemDetails sessionUser = ApplicationContextUtils.getUser();
		User user = new User();
		user.setId(sessionUser.getUserId());
		
		modelAndView.addObject("timeLineStartDate", DateUtils.format(Statics.timeLineStartDate, DateUtils.TURKISH_DATE));		
		
		LayerSimpleItem layerItem = userLayerPermissionService.checkLayerPermissionAndGet(currentLayerGuid);
		if(layerItem == null) {
			// there is no permission to see events under this region
			return null;
		}
		
		modelAndView.addObject("userDisplayName", sessionUser.getDisplayName());
		
		modelAndView.addObject("groupName", groupName);
		
		modelAndView.addObject("layerId", layerItem.getGuid());
		
		modelAndView.addObject("userSettingsId", sessionUser.getCurrentUserSettingsGroupName());
		

		List<UserSettingsTypeItem> userSettingsDbList = userSettingsRepository.findAllProjectedByUser(user);  
		modelAndView.addObject("userSettings", userSettingsDbList);
		
		List<UserSettingsType> settingsTypeList = userSettingsTypeRepository.findAll();
		modelAndView.addObject("userSettingsTypes", settingsTypeList);
		
		List<Settings> settingsAdminExcelDbList = settingsRepository.findAllProjectedBySettingsValueAndGroupName("true","ExcelYönetim");  
		modelAndView.addObject("settings", userSettingsDbList);
		
		List<UserSettingsTypeItem> userSettingsItemList = new ArrayList<>();
		List<UserSettingsTypeItem> userSettingsItemLayerList = new ArrayList<>();
		List<UserSettingsTypeItem> menuLayerList = new ArrayList<>();
		
		ObjectMapper objectMapper = new ObjectMapper();

		for(UserSettingsType userSettingsType : settingsTypeList) {
			

			if(userSettingsType.getIsLayer()) {
				continue;
			}
			
			// isLayer olmayanlar için işlem yapılır
			
			if(userSettingsType.getGroupName().equals(groupName)) {
				
			   UserSettingsTypeItem userSettingsTypeItem= new UserSettingsTypeItem();

			   if(groupName.equals("ExcelYönetim")) {
				   
				   for(Settings settings : settingsAdminExcelDbList) {
					   					   
					   if(userSettingsType.getSettingsKey().equals(settings.getSettingsKey())) {
							  
						    userSettingsTypeItem.setId(userSettingsType.getId());
							userSettingsTypeItem.setSettingsKey(userSettingsType.getSettingsKey());
							userSettingsTypeItem.setDescription(userSettingsType.getDescription());
							userSettingsTypeItem.setGroupName(userSettingsType.getGroupName());
							userSettingsTypeItem.setType(userSettingsType.getType());
							userSettingsTypeItem.setIsLayer(userSettingsType.getIsLayer());
							userSettingsTypeItem.setOrder(userSettingsType.getOrder());
							
							for(UserSettingsTypeItem userSettings : userSettingsDbList) {
								
							   if(userSettings.getUserSettingsTypeId().equals(userSettingsType.getId())) {
					
								  userSettingsTypeItem.setSettingsValue(userSettings.getSettingsValue());

							    }
						    }
							
							  userSettingsItemList.add(userSettingsTypeItem);
					   }
					   
				   }

			   }
			   else {
				   
				    userSettingsTypeItem.setId(userSettingsType.getId());
					userSettingsTypeItem.setSettingsKey(userSettingsType.getSettingsKey());
					userSettingsTypeItem.setDescription(userSettingsType.getDescription());
					userSettingsTypeItem.setGroupName(userSettingsType.getGroupName());
					userSettingsTypeItem.setType(userSettingsType.getType());
					userSettingsTypeItem.setIsLayer(userSettingsType.getIsLayer());
					userSettingsTypeItem.setOrder(userSettingsType.getOrder());
					
					for(UserSettingsTypeItem userSettings : userSettingsDbList) {
						
					   if(userSettings.getUserSettingsTypeId().equals(userSettingsType.getId())) {
			
						  userSettingsTypeItem.setSettingsValue(userSettings.getSettingsValue());

					    }
				    }
					
					userSettingsItemList.add(userSettingsTypeItem);
			   }

			 }	

		}

		for(UserLayerPermissionItem userLayerPermission : sessionUser.getUserLayerPermissionList()) {
									
			for(UserSettingsType userSettingsType : settingsTypeList) {

				
				if(!userSettingsType.getIsLayer()) {
					continue;
				}

				// isLayer olanlar için işlem yapılır
				
				
				UserSettingsTypeItem userSettingsTypeItemLayer= new UserSettingsTypeItem();	
				userSettingsTypeItemLayer.setGroupName(userLayerPermission.getLayerName());
				menuLayerList.add(userSettingsTypeItemLayer);
			
				
				if(userLayerPermission.getLayerName().equals(groupName)) {
					
					UserSettingsTypeItem userSettingsTypeItem= new UserSettingsTypeItem();
					
					userSettingsTypeItem.setId(userSettingsType.getId());
					userSettingsTypeItem.setSettingsKey(userSettingsType.getSettingsKey());
					userSettingsTypeItem.setSettingsValue(null);
					userSettingsTypeItem.setDescription(userSettingsType.getDescription());
					userSettingsTypeItem.setLayerId(userLayerPermission.getLayerId());
					userSettingsTypeItem.setGroupName(userLayerPermission.getLayerName());
					userSettingsTypeItem.setType(userSettingsType.getType());
					userSettingsTypeItem.setIsLayer(userSettingsType.getIsLayer());
					userSettingsTypeItem.setOrder(userSettingsType.getOrder());
	
	                for(UserSettingsTypeItem userSettings : userSettingsDbList) {
	                	
	                	if(!userSettings.getIsLayer()) {
	    					continue;
	    				}

						if(userSettings.getLayerId() != null && userSettings.getLayerId().equals(userLayerPermission.getLayerId()) && userSettings.getUserSettingsTypeId().equals(userSettingsType.getId())) {
			
						   userSettingsTypeItem.setSettingsValue(userSettings.getSettingsValue());
							
						}
				    }

	                //Olay Grupları
	                
	                if(userSettingsType.getSettingsKey().equals(UserSettingsTypeE.EVENT_GROUPS.getName()) 
	                		&& !StringUtils.isBlank(userSettingsTypeItem.getSettingsValue())
	                		) {
	                	//System.out.println(userSettingsType.getType());
	                	
	                	
	                	try {
							List<UserSettingsEventGroupItem> aa = objectMapper.readValue(userSettingsTypeItem.getSettingsValue(), new TypeReference<List<UserSettingsEventGroupItem>>(){});
							
							
							List<JSONObject> eventGroupJsonObjectList = new ArrayList<>();
							aa.forEach(eventGroupItem -> {
		            			
		            			JSONObject eventTypeJsonObject = new JSONObject();
		            			 try {
		            				 eventTypeJsonObject.put("eventGroupName", eventGroupItem.getName());								 
		            				 eventGroupJsonObjectList.add(eventTypeJsonObject);
									 
									 
								} catch (JSONException e) {
									log.error(e);
								}
		            			
		            		});
							
		            		userSettingsTypeItem.setSettingsListValue(eventGroupJsonObjectList);	
		            		
		            		//userLayerPermission.getLayerId()
		            		userSettingsTypeItem.setLayerGuid(userLayerPermission.getLayerGuid());
	                	
	                	
	                	} catch (JsonMappingException e) {
	                		log.error(e);
						} catch (JsonProcessingException e) {
							log.error(e);
						}
	                }
                
				 
                           
	                //Olay türü - olay grubu listeleri tutmak için json object kullanıldı.
	                if(userSettingsType.getSettingsKey().equals(UserSettingsTypeE.EVENT_TYPE.getName())) {
							
	            		List<EventTypeItem> eventTypeItemList = eventService.prepareEventTypeWrapperList(userLayerPermission.getLayerId());//LayerItem.getId()

	            		String language = LocaleContextHolder.getLocale().getLanguage();
	            		Locale locale = new Locale(language);

	            		eventTypeItemList.forEach(item->{

	            			String name = ApplicationContextUtils.getMessage("icons." + item.getCode(), locale);
	            			name = name.equals("icons." + item.getCode()) ? item.getName() : name;
	            			item.setName(name);

	            		});
	            		
	            		Collator collator = Collator.getInstance(locale);
	            		collator.setStrength(Collator.PRIMARY);
	            		List<EventTypeItem> sortedList = eventTypeItemList.stream()
	                            .sorted(Comparator.comparing(EventTypeItem::getName, collator))
	                            .collect(Collectors.toList());
	            		
	            		List<JSONObject> jsonObjectList = new ArrayList<>();
	            		sortedList.forEach(eventTypeItem -> {
	            			
	            			JSONObject eventTypeJsonObject = new JSONObject();
	            			 try {
	            				 eventTypeJsonObject.put("name", eventTypeItem.getName());
								 eventTypeJsonObject.put("id", eventTypeItem.getId());
								 
								 jsonObjectList.add(eventTypeJsonObject);
								 
								 
							} catch (JSONException e) {
								log.error(e);
								
							}
	            			
	            		});
						
	            		userSettingsTypeItem.setSettingsListValue(jsonObjectList);	
	            		
	            		//userLayerPermission.getLayerId()
	            		userSettingsTypeItem.setLayerGuid(userLayerPermission.getLayerGuid());
	                }

                userSettingsItemLayerList.add(userSettingsTypeItem);

			  }
			}
		 }
		
		// Menü
		Map<String, List<UserSettingsType>> contentGeneralMap = settingsTypeList
				.stream()
				.collect(Collectors.groupingBy(userSetting -> userSetting.getGroupName()));  
		
		LinkedHashMap<String, List<UserSettingsType>> sortedGeneralMap = contentGeneralMap.entrySet()
				.stream()
				.sorted(Map.Entry.comparingByKey())
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue, LinkedHashMap::new));
		
		// Menü Layer
		Map<String, List<UserSettingsTypeItem>> contentGeneralLayerMap = menuLayerList
				.stream()
				.collect(Collectors.groupingBy(userSetting -> userSetting.getGroupName()));  
		
		LinkedHashMap<String, List<UserSettingsTypeItem>> sortedGeneralLayerMap = contentGeneralLayerMap.entrySet()
				.stream()
				.sorted(Map.Entry.comparingByKey())
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue, LinkedHashMap::new));
	 	
		// Ayarlar
		Map<String, List<UserSettingsTypeItem>> contentMap = userSettingsItemList
				.stream()
				.collect(Collectors.groupingBy(userSetting -> userSetting.getGroupName()));  
		
		LinkedHashMap<String, List<UserSettingsTypeItem>> sortedMap = contentMap.entrySet()
				.stream()
				.sorted(Map.Entry.comparingByKey())				
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue, LinkedHashMap::new));

		// Ayarlar Layer
		Map<String, List<UserSettingsTypeItem>> contentLayerMap = userSettingsItemLayerList
				.stream()
				.collect(Collectors.groupingBy(userSetting -> userSetting.getGroupName()));  
		
		LinkedHashMap<String, List<UserSettingsTypeItem>> sortedLayerMap = contentLayerMap.entrySet()
				.stream()
				.sorted(Map.Entry.comparingByKey())
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue, LinkedHashMap::new));
		
		
		//sortedMap.putAll(sortedLayerMap);
		
		for (Entry<String, List<UserSettingsTypeItem>> entrySet : sortedLayerMap.entrySet()) {
				
			List<UserSettingsTypeItem> settingsListTemp = entrySet.getValue();
			List<UserSettingsTypeItem> sortedSettingsListTemp = settingsListTemp.stream().sorted((o1,o2) -> (o1.getOrder() != null ? o1.getOrder() : o1.getId()) .compareTo(o2.getOrder() != null ? o2.getOrder() : o2.getId())).collect(Collectors.toList());
			entrySet.setValue(sortedSettingsListTemp);
		}
		
		for (Entry<String, List<UserSettingsTypeItem>> entrySet : sortedMap.entrySet()) {
			
			List<UserSettingsTypeItem> settingsListTemp = entrySet.getValue();
			List<UserSettingsTypeItem> sortedSettingsListTemp = settingsListTemp.stream().sorted((o1,o2) -> o1.getId().compareTo(o2.getId())).collect(Collectors.toList());
			entrySet.setValue(sortedSettingsListTemp);
		}
		
		modelAndView.addObject("settingsGeneralMap", sortedGeneralMap);
		modelAndView.addObject("settingsGeneralLayerMap", sortedGeneralLayerMap);
		modelAndView.addObject("settingsMap", sortedMap);
		modelAndView.addObject("settingsLayerMap", sortedLayerMap);
		
		return modelAndView;
	}
	
	
	@RequestMapping(value="/layer/{layerId}", method = RequestMethod.POST)
	public Map<String, UsertSettingsTypeColumn> getData(@PathVariable(name = "layerId") String currentLayerGuid) {
		
		LayerSimpleItem layerItem = userLayerPermissionService.checkLayerPermissionAndGet(currentLayerGuid);
		if(layerItem == null) {
			// there is no permission to see events under this region
			return null;
		}
		
		
		UserItemDetails sessionUser = ApplicationContextUtils.getUser();
		User user = new User();
		user.setId(sessionUser.getUserId());	
		
		Layer layer = new Layer();
		layer.setId(layerItem.getId());
		
		List<UserSettingsTypeItem> userSettingsAllList = userSettingsRepository.findAllByUserAndLayer(user, layer);  //.findAllByUserAndLayerNotNull(user);
		Map<Integer,List<UserSettingsTypeItem>> mapList = userSettingsAllList.stream().collect(Collectors.groupingBy(UserSettingsTypeItem::getLayerId));
		
		List<UserLayerPermissionItem> userLayerPermissionItemList = sessionUser.getUserLayerPermissionList();
		
		Map<String, UsertSettingsTypeColumn> map = new HashMap<>();
		

	      for (Map.Entry<Integer, List<UserSettingsTypeItem>> entry : mapList.entrySet()) {
//	          System.out.println("Key : " + entry.getKey() + ", Value : " + entry.getValue());
	    	  
	    	  
	    	  	List<UserSettingsTypeItem> list = entry.getValue();
	    	  	Optional<UserLayerPermissionItem> optionalLayerGUid = userLayerPermissionItemList.stream().filter(f -> f.getLayerId().equals(entry.getKey())).findAny();
	    	  	
	    	  	
	    	  	if(optionalLayerGUid.isPresent()) {
	    	  		
	    	  		UsertSettingsTypeColumn userSettingsTypeColumn = new UsertSettingsTypeColumn();
	    	  		userSettingsTypeColumn.setAnyExistField(false);
	    	  		
	    	  		String city = "";
	    	  		String country = "";
	    	  		String titleAndSpotAndDescription = "";
	    	  		
	    	  		Optional<UserSettingsTypeItem> optionalCity = list.stream().filter(f -> f.getSettingsKey().equals(UserSettingsTypeE.CITY.getName())).findAny();
	    	  		if(optionalCity.isPresent()) {
	    	  			city = optionalCity.get().getSettingsValue();
	    	  			userSettingsTypeColumn.setAnyExistField(true);
	    	  		}
	    	  		
	    	  		Optional<UserSettingsTypeItem> optionalCountry = list.stream().filter(f -> f.getSettingsKey().equals(UserSettingsTypeE.COUNTRY.getName())).findAny();
	    	  		if(optionalCountry.isPresent()) {
	    	  			country = optionalCountry.get().getSettingsValue();
	    	  			userSettingsTypeColumn.setAnyExistField(true);
	    	  		}
	    	  		
	    	  		Optional<UserSettingsTypeItem> optionalTitleAndSpotAndDescription = list.stream().filter(f -> f.getSettingsKey().equals(UserSettingsTypeE.TITLE_SPOT_DESCRIPTION.getName())).findAny();
	    	  		if(optionalTitleAndSpotAndDescription.isPresent()) {
	    	  			titleAndSpotAndDescription = optionalTitleAndSpotAndDescription.get().getSettingsValue();
	    	  			userSettingsTypeColumn.setAnyExistField(true);
	    	  		}
	    	  		
			  		Optional<UserSettingsTypeItem> optioanalEventTypeId = list.stream().filter(f -> f.getSettingsKey().equals(UserSettingsTypeE.EVENT_TYPE.getName())).findAny();
			  		
			  		String eventTypeId = "0";
			  		if(optioanalEventTypeId.isPresent()) {
			  			eventTypeId = optioanalEventTypeId.get().getSettingsValue();
			  			userSettingsTypeColumn.setAnyExistField(true);
			  		}
			  		
			  		String alertEvent = "false";
			  		Optional<UserSettingsTypeItem> optionalAlertEvent = list.stream().filter(f -> f.getSettingsKey().equals(UserSettingsTypeE.ALERT_EVENT.getName())).findAny();
			  		if(optionalAlertEvent.isPresent()) {
			  			alertEvent = optionalAlertEvent.get().getSettingsValue();
			  			userSettingsTypeColumn.setAnyExistField(true);
			  		}
			  		
			  		String startDate = "";
			  		Optional<UserSettingsTypeItem> optionalStartDate = list.stream().filter(f -> f.getSettingsKey().equals(UserSettingsTypeE.START_DATE.getName())).findAny();
			  		if(optionalStartDate.isPresent()) {
			  			startDate = optionalStartDate.get().getSettingsValue();
			  			userSettingsTypeColumn.setAnyExistField(true);
			  		}
			  		
			  		String endDate = "";
			  		Optional<UserSettingsTypeItem> optionalEndDate = list.stream().filter(f -> f.getSettingsKey().equals(UserSettingsTypeE.END_DATE.getName())).findAny();
			  		if(optionalEndDate.isPresent()) {
			  			endDate =  optionalEndDate.get().getSettingsValue();
			  			userSettingsTypeColumn.setAnyExistField(true);
			  		}
			  		
			  		
			  		Optional<UserSettingsTypeItem> optioanalEventGroupIdAndDbName = list.stream().filter(f -> f.getSettingsKey().equals(UserSettingsTypeE.EVENT_GROUPS.getName())).findAny();
			  		
			  		String eventGroupIdAndDbName ="";
			  		if(optioanalEventGroupIdAndDbName.isPresent()) {
			  			eventGroupIdAndDbName = optioanalEventGroupIdAndDbName.get().getSettingsValue();
			  			
			  			ObjectMapper objectMapper = new ObjectMapper();
			  			try {
			  				
			  				if(!StringUtils.isBlank(eventGroupIdAndDbName)) {
			  					List<UserSettingsEventGroupItem> aa = Arrays.asList(objectMapper.readValue(eventGroupIdAndDbName, UserSettingsEventGroupItem[].class));
				  				userSettingsTypeColumn.setEventGroupList(aa);
				  				userSettingsTypeColumn.setExistEventGroups(true);
			  				}
			  				
			  				
			  			} catch (JsonMappingException e) {
			  				log.error(e);
			  				
			  			} catch (JsonProcessingException e) {
			  				log.error(e);
			  				
			  			}
			  			
			  		}else {
			  			
			  			userSettingsTypeColumn.setExistEventGroups(false);
			  		}
			  		
			  		
			  		String mapCoordinates = "";
			  		Optional<UserSettingsTypeItem> optionalMapCoordinates = list.stream().filter(f -> f.getSettingsKey().equals(UserSettingsTypeE.MAPCOORDINATES.getName())).findAny();
			  		if(optionalMapCoordinates.isPresent()) {
			  			mapCoordinates = optionalMapCoordinates.get().getSettingsValue();
			  			userSettingsTypeColumn.setAnyExistField(true);
			  		}
			  		
			  		String mapZoom = "";
			  		Optional<UserSettingsTypeItem> optionalMapZoom = list.stream().filter(f -> f.getSettingsKey().equals(UserSettingsTypeE.MAPZOOM.getName())).findAny();
			  		if(optionalMapZoom.isPresent()) {
			  			mapZoom = optionalMapZoom.get().getSettingsValue();
			  			userSettingsTypeColumn.setAnyExistField(true);
			  		}
			  		
			  		userSettingsTypeColumn.setCity(city);
			  		userSettingsTypeColumn.setCountry(country);
			  		userSettingsTypeColumn.setEventTypeId(eventTypeId);
			  		userSettingsTypeColumn.setTitleAndSpotAndDescription(titleAndSpotAndDescription);
			  		userSettingsTypeColumn.setAlertEvent(alertEvent);
			  		userSettingsTypeColumn.setStartDate(startDate);
			  		userSettingsTypeColumn.setEndDate(endDate);
			  		userSettingsTypeColumn.setMapCoordinate(mapCoordinates);
			  		userSettingsTypeColumn.setMapZoom(mapZoom);

			  		map.put(optionalLayerGUid.get().getLayerGuid(), userSettingsTypeColumn);
			  		
			  		
	    	  	}
	    	  
	    	  	
	      }				
		
		return map;
	}
	
	
	@RequestMapping(value = "/detailedSearchCookieDeleteInformation", method = RequestMethod.GET)
	@ResponseBody
	public boolean detailedSearchCookieDeleteInformation(@RequestParam(name = "writeUserSettingsToCookieAfterLogin") boolean writeUserSettingsToCookieAfterLogin) {
		
		UserItemDetails sessionUser = ApplicationContextUtils.getUser();
		sessionUser.setWriteUserSettingsToCookieAfterLogin(writeUserSettingsToCookieAfterLogin);
		return sessionUser.getWriteUserSettingsToCookieAfterLogin();
	}
	
	
	@RequestMapping(value = "/userSettingsValueSave/{layerId}", method = RequestMethod.POST)
	@ResponseBody
	public Boolean setEventGroupsUserSettingsData(
			  @PathVariable(name = "layerId") String currentLayerGuid,
			  @RequestParam(name = "eventGroupSave") boolean eventGroupSave,
			  @RequestParam(name = "outSideEventGroup") boolean outSideEventGroup,
			  @RequestParam(name = "mapInfo", required = false) boolean mapInfo,
			  @RequestBody UsertSettingsTypeColumn userSettingsTypeColumn) throws JSONException {


		LayerSimpleItem layerItem = userLayerPermissionService.checkLayerPermissionAndGet(currentLayerGuid);
		if(layerItem == null) {
			// there is no permission to see events under this region
			return false;
		}
		
		List<UserSettingsEventGroupItem> eventGroupList = userSettingsTypeColumn.getEventGroupList();
		if(eventGroupSave) {
			List<String> eventGroupdbNameIdList  = new ArrayList<>();
			eventGroupList.forEach(item -> {
				eventGroupdbNameIdList.add(currentLayerGuid);
			});
			
			
			if(!userLayerPermissionService.checkEventGroupPermission(layerItem, eventGroupdbNameIdList)) {
				// there is no permission to see events under this event group

				return false;
			}
		}
		
		UserItemDetails sessionUser = ApplicationContextUtils.getUser();
		User user = new User();
		user.setId(sessionUser.getUserId());
		
		Layer layer = new Layer();
		layer.setId(layerItem.getId());
		
		ObjectMapper objectMapper = new ObjectMapper();
		

		String eventGroupIdAndDbNameJson = null;
		try {
			eventGroupIdAndDbNameJson = objectMapper.writeValueAsString(eventGroupList);
		} catch (JsonProcessingException e) {
			log.error(e);
			
		}

		List<UserSettings> userSettingsDbList = userSettingsRepository.findAllByLayerAndUser(layer, user);
		List<UserSettingsType> userSettingsTypeList = userSettingsTypeRepository.findAllByIsLayer(true);
		
		List<UserSettings> saveList = new ArrayList<>();
		
		//Tüm olaylar tablosunda kaydet kısmından geldiyse eğer olay grubu dışında diğerlerininde kaydedilmesi gerekiyor.
		if(outSideEventGroup) {//olay grubu dışındaki diğer alanlar kaydedilecekse.
			
			for(UserSettingsType item : userSettingsTypeList) {
				
				UserSettings userSettings = new UserSettings();
				
				
				switch(item.getSettingsKey()) {
				case "City":
					
					Optional<UserSettings> optioanalUserSettingsCity = userSettingsDbList.stream().filter(a -> a.getUserSettingsType().getSettingsKey().equals(UserSettingsTypeE.CITY.getName())).findAny();
					if(UserSettingsTypeE.CITY.getName().equals(item.getSettingsKey()) && optioanalUserSettingsCity.isPresent()) {		
						
						userSettings = optioanalUserSettingsCity.get();
						userSettings.setSettingsValue(userSettingsTypeColumn.getCity());
					}else {
						
						UserSettingsType userSettingsType = userSettingsTypeList.stream().filter(f -> f.getSettingsKey().equals(UserSettingsTypeE.CITY.getName())).findAny().get();
						userSettings.setLayer(layer);
						userSettings.setUser(user);
						userSettings.setSettingsValue(userSettingsTypeColumn.getCity());					
						userSettings.setUserSettingsType(userSettingsType);
						
					}
					
					saveList.add(userSettings);
					break;
					
					
				case "Country":
					
					Optional<UserSettings> optioanalUserSettingsCountry = userSettingsDbList.stream().filter(a -> a.getUserSettingsType().getSettingsKey().equals(UserSettingsTypeE.COUNTRY.getName())).findAny();
					if(UserSettingsTypeE.COUNTRY.getName().equals(item.getSettingsKey()) && optioanalUserSettingsCountry.isPresent()) {		
						
						userSettings = optioanalUserSettingsCountry.get();
						userSettings.setSettingsValue(userSettingsTypeColumn.getCountry());
					}else {
						
						UserSettingsType userSettingsType = userSettingsTypeList.stream().filter(f -> f.getSettingsKey().equals(UserSettingsTypeE.COUNTRY.getName())).findAny().get();
						userSettings.setLayer(layer);
						userSettings.setUser(user);
						userSettings.setSettingsValue(userSettingsTypeColumn.getCountry());					
						userSettings.setUserSettingsType(userSettingsType);
						
					}
					
					saveList.add(userSettings);
					break;
					
				case "TitleAndDescription":
					
					Optional<UserSettings> optioanalUserSettingsTitleAndSpotAndDescription = userSettingsDbList.stream().filter(a -> a.getUserSettingsType().getSettingsKey().equals(UserSettingsTypeE.TITLE_SPOT_DESCRIPTION.getName())).findAny();
					if(UserSettingsTypeE.TITLE_SPOT_DESCRIPTION.getName().equals(item.getSettingsKey()) && optioanalUserSettingsTitleAndSpotAndDescription.isPresent()) {		
						
						userSettings = optioanalUserSettingsTitleAndSpotAndDescription.get();
						userSettings.setSettingsValue(userSettingsTypeColumn.getTitleAndSpotAndDescription());
					}else {
						
						UserSettingsType userSettingsType = userSettingsTypeList.stream().filter(f -> f.getSettingsKey().equals(UserSettingsTypeE.TITLE_SPOT_DESCRIPTION.getName())).findAny().get();
						userSettings.setLayer(layer);
						userSettings.setUser(user);
						userSettings.setSettingsValue(userSettingsTypeColumn.getTitleAndSpotAndDescription());					
						userSettings.setUserSettingsType(userSettingsType);
						
					}
					
					saveList.add(userSettings);
					break;
					
				case "EventsWithAlarm":
					
					Optional<UserSettings> optioanalUserSettingsAlertEvent = userSettingsDbList.stream().filter(a -> a.getUserSettingsType().getSettingsKey().equals(UserSettingsTypeE.ALERT_EVENT.getName())).findAny();
					if(UserSettingsTypeE.ALERT_EVENT.getName().equals(item.getSettingsKey()) && optioanalUserSettingsAlertEvent.isPresent()) {		
						
						userSettings = optioanalUserSettingsAlertEvent.get();
						userSettings.setSettingsValue(userSettingsTypeColumn.getAlertEvent());
					}else {
						
						UserSettingsType userSettingsType = userSettingsTypeList.stream().filter(f -> f.getSettingsKey().equals(UserSettingsTypeE.ALERT_EVENT.getName())).findAny().get();
						userSettings.setLayer(layer);
						userSettings.setUser(user);
						userSettings.setSettingsValue(userSettingsTypeColumn.getAlertEvent());					
						userSettings.setUserSettingsType(userSettingsType);
						
					}
					
					saveList.add(userSettings);
					break;
					
				case "EventTypes":
					
					Optional<UserSettings> optioanalUserSettingsEventType = userSettingsDbList.stream().filter(a -> a.getUserSettingsType().getSettingsKey().equals(UserSettingsTypeE.EVENT_TYPE.getName())).findAny();
					if(UserSettingsTypeE.EVENT_TYPE.getName().equals(item.getSettingsKey()) && optioanalUserSettingsEventType.isPresent()) {		
						
						userSettings = optioanalUserSettingsEventType.get();
						userSettings.setSettingsValue(userSettingsTypeColumn.getEventTypeId());
					}else {
						
						UserSettingsType userSettingsType = userSettingsTypeList.stream().filter(f -> f.getSettingsKey().equals(UserSettingsTypeE.EVENT_TYPE.getName())).findAny().get();
						userSettings.setLayer(layer);
						userSettings.setUser(user);
						userSettings.setSettingsValue(userSettingsTypeColumn.getEventTypeId());					
						userSettings.setUserSettingsType(userSettingsType);	
					}
					
					saveList.add(userSettings);
					break;
					
				case "StartingDate":
					
					Optional<UserSettings> optioanalStartDate = userSettingsDbList.stream().filter(a -> a.getUserSettingsType().getSettingsKey().equals(UserSettingsTypeE.START_DATE.getName())).findAny();
					if(UserSettingsTypeE.START_DATE.getName().equals(item.getSettingsKey()) && optioanalStartDate.isPresent()) {		
						
						userSettings = optioanalStartDate.get();
						userSettings.setSettingsValue(userSettingsTypeColumn.getStartDate());
					}else {
						
						UserSettingsType userSettingsType = userSettingsTypeList.stream().filter(f -> f.getSettingsKey().equals(UserSettingsTypeE.START_DATE.getName())).findAny().get();
						userSettings.setLayer(layer);
						userSettings.setUser(user);
						userSettings.setSettingsValue(userSettingsTypeColumn.getStartDate());					
						userSettings.setUserSettingsType(userSettingsType);
						
					}
					
					saveList.add(userSettings);
					break;
					
				case "EndDate":
					
					Optional<UserSettings> optioanalEndDate = userSettingsDbList.stream().filter(a -> a.getUserSettingsType().getSettingsKey().equals(UserSettingsTypeE.END_DATE.getName())).findAny();
					if(UserSettingsTypeE.END_DATE.getName().equals(item.getSettingsKey()) && optioanalEndDate.isPresent()) {		
						
						userSettings = optioanalEndDate.get();
						userSettings.setSettingsValue(userSettingsTypeColumn.getEndDate());
					}else {
						
						UserSettingsType userSettingsType = userSettingsTypeList.stream().filter(f -> f.getSettingsKey().equals(UserSettingsTypeE.END_DATE.getName())).findAny().get();
						userSettings.setLayer(layer);
						userSettings.setUser(user);
						userSettings.setSettingsValue(userSettingsTypeColumn.getEndDate());					
						userSettings.setUserSettingsType(userSettingsType);
						
					}
					
					saveList.add(userSettings);
					break;
					
				default:
					
					break;
					
					
				}
				
				
				
				
			}
		}
		
		if(eventGroupSave) {//olay grubu kaydedilecekse
			
			Optional<UserSettings> optioanalUserSettingsEventGroup = userSettingsDbList.stream().filter(f -> f.getUserSettingsType().getSettingsKey().equals(UserSettingsTypeE.EVENT_GROUPS.getName())).findAny();				
			UserSettings userSettings = new UserSettings();
			
			if(userSettingsDbList.size() > 0 && optioanalUserSettingsEventGroup.isPresent()) {
				
				userSettings = optioanalUserSettingsEventGroup.get();
				userSettings.setSettingsValue(eventGroupIdAndDbNameJson);				
					
			}else {
				
				UserSettingsType userSettingsTypeItem = userSettingsTypeRepository.findOneBySettingsKey(UserSettingsTypeE.EVENT_GROUPS.getName());
				
				UserSettingsType userSettingsType = new UserSettingsType();
				userSettingsType.setId(userSettingsTypeItem.getId());
				userSettings.setLayer(layer);
				userSettings.setUser(user);
				userSettings.setSettingsValue(eventGroupIdAndDbNameJson);					
				userSettings.setUserSettingsType(userSettingsType);
				
			}
			
			saveList.add(userSettings);
		}
		
		if(mapInfo) {
			for(UserSettingsType item : userSettingsTypeList) {
				
				UserSettings userSettings = new UserSettings();
				
				switch(item.getSettingsKey()) {
					case "MapCoordinates":
						
						Optional<UserSettings> optioanalUserSettingsMapCoordinates = userSettingsDbList.stream().filter(f -> f.getUserSettingsType().getSettingsKey().equals(UserSettingsTypeE.MAPCOORDINATES.getName())).findAny();
						if(userSettingsDbList.size() > 0 && optioanalUserSettingsMapCoordinates.isPresent()) {
							
							userSettings = optioanalUserSettingsMapCoordinates.get();
							userSettings.setSettingsValue(userSettingsTypeColumn.getMapCoordinate());	
							
						}else {
							
							UserSettingsType userSettingsTypeItem = userSettingsTypeRepository.findOneBySettingsKey(UserSettingsTypeE.MAPCOORDINATES.getName());
							UserSettingsType userSettingsType = new UserSettingsType();
							userSettingsType.setId(userSettingsTypeItem.getId());
							userSettings.setLayer(layer);
							userSettings.setUser(user);
							userSettings.setSettingsValue(userSettingsTypeColumn.getMapCoordinate());					
							userSettings.setUserSettingsType(userSettingsType);
						}
						
						saveList.add(userSettings);
						break;
					
					case "MapZoom":
						
						Optional<UserSettings> optioanalUserSettingsMapZoom = userSettingsDbList.stream().filter(f -> f.getUserSettingsType().getSettingsKey().equals(UserSettingsTypeE.MAPZOOM.getName())).findAny();
						if(userSettingsDbList.size() > 0 && optioanalUserSettingsMapZoom.isPresent()) {
							
							userSettings = optioanalUserSettingsMapZoom.get();
							userSettings.setSettingsValue(userSettingsTypeColumn.getMapZoom());	
						}else {
							
							UserSettingsType userSettingsTypeItem = userSettingsTypeRepository.findOneBySettingsKey(UserSettingsTypeE.MAPZOOM.getName());
							UserSettingsType userSettingsType = new UserSettingsType();
							userSettingsType.setId(userSettingsTypeItem.getId());
							userSettings.setLayer(layer);
							userSettings.setUser(user);
							userSettings.setSettingsValue(userSettingsTypeColumn.getMapZoom());					
							userSettings.setUserSettingsType(userSettingsType);
						}
						
						saveList.add(userSettings);
						break;
						
					default:
						
						break;
					}
				
			}
			
			
			
		}
		
		
	
		userSettingsRepository.saveAll(saveList);
		

    	return true;
	}
	
	
	@RequestMapping(value = "/userSettingsDelete/{layerId}", method = RequestMethod.POST)

	public Boolean userSettingsDelete( 
			@PathVariable(name = "layerId") String currentLayerGuid,
			@RequestParam(name = "eventGroupDelete") boolean eventGroupDelete,
			@RequestParam(name = "outSideEventGroupDelete") boolean outSideEventGroupDelete) {
		
		LayerSimpleItem layerItem = userLayerPermissionService.checkLayerPermissionAndGet(currentLayerGuid);
		if(layerItem == null) {
			// there is no permission to see events under this region
			return false;
		}
		
		Layer layer = new Layer();
		layer.setId(layerItem.getId());
		
		UserItemDetails sessionUser = ApplicationContextUtils.getUser();
		User user = new User();
		user.setId(sessionUser.getUserId());
		
		List<UserSettings> userSettingsList = userSettingsRepository.findAllByLayerAndUser(layer, user);	
		
		List<UserSettings> deleteList = new ArrayList<>();
		if(outSideEventGroupDelete) {
			deleteList = userSettingsList.stream().filter(f -> f.getUserSettingsType().getSettingsKey().equals(UserSettingsTypeE.CITY.getName()) 				
					|| f.getUserSettingsType().getSettingsKey().equals(UserSettingsTypeE.COUNTRY.getName()) 
					|| f.getUserSettingsType().getSettingsKey().equals(UserSettingsTypeE.EVENT_TYPE.getName()) 
					|| f.getUserSettingsType().getSettingsKey().equals(UserSettingsTypeE.ALERT_EVENT.getName())
					|| f.getUserSettingsType().getSettingsKey().equals(UserSettingsTypeE.TITLE_SPOT_DESCRIPTION.getName())
					|| f.getUserSettingsType().getSettingsKey().equals(UserSettingsTypeE.START_DATE.getName())
					|| f.getUserSettingsType().getSettingsKey().equals(UserSettingsTypeE.END_DATE.getName())).collect(Collectors.toList());
			userSettingsRepository.deleteAll(deleteList);
		}
		
		if(eventGroupDelete) {
			deleteList = userSettingsList.stream().filter(f -> f.getUserSettingsType().getSettingsKey().equals(UserSettingsTypeE.EVENT_GROUPS.getName())).collect(Collectors.toList());
			userSettingsRepository.deleteAll(deleteList);
		}
		
		

		return true;
	}
	
//	@Operation(summary = "Kaydet")
	@RequestMapping(value = "/save/{groupName}", method = RequestMethod.POST)
	public Boolean save(@PathVariable(name = "groupName") String groupName, HttpServletRequest request) {
			
   		UserItemDetails sessionUser = ApplicationContextUtils.getUser();
		User user = new User();
		user.setId(sessionUser.getUserId());	
		
		Map<String, Object> userSettingsForLog = new TreeMap<>();
		List<Object> oldSettingsForLog = new ArrayList<>();
		List<Object> newSettingsForLog = new ArrayList<>();
		List<UserSettings> changeList = new ArrayList<>();
		Integer groupLayerId=null;
		
		LogTypeE logTypeE = LogTypeE.USER_SETTINGS_ADD;
		
		for(UserLayerPermissionItem userLayerPermission : sessionUser.getUserLayerPermissionList()) {
			
			if(userLayerPermission.getLayerName().equals(groupName)) {
				
				groupName="Katman";
				groupLayerId=userLayerPermission.getLayerId();
			}
		}
		
		List<UserSettings> userSettingsDbList = userSettingsRepository.findAllProjectedByUserAndUserSettingsTypeGroupName(user, groupName);
		
		List<UserSettingsType> settingsTypeList = userSettingsTypeRepository.findAllProjectedByGroupName(groupName);
		
		List<String> userSettingsKeyList = new ArrayList<>();
		
		String[] valueMultiple;
		
		for (UserSettingsType userSettingsType : settingsTypeList) {
			
			if(userSettingsType.getIsLayer()) {

				String userSettingsKey = userSettingsType.getSettingsKey().toString() + "_" + groupLayerId.toString();
				userSettingsKeyList.add(userSettingsKey);
			
			}
			else {
				
				String userSettingsKey = userSettingsType.getSettingsKey().toString() + "_null";
				userSettingsKeyList.add(userSettingsKey);
			}
					
		}
		
		try {

			for (String userSettingsKey : userSettingsKeyList) {

				String value = request.getParameter(userSettingsKey);
			    	
			    UserSettings userSettings= new UserSettings();
	
	            String[] settingsKeyAndLayerIdArray = userSettingsKey.split("_");
	            
	           
				for (UserSettingsType userSettingsTypeItem : settingsTypeList) {
					
					if(userSettingsTypeItem.getSettingsKey().equals(settingsKeyAndLayerIdArray[0])) {
						
						userSettings.setUserSettingsType(userSettingsTypeItem);
						break;
				    }	
			    }
				
				
				switch (userSettings.getUserSettingsType().getType()) {
				case "checkbox":
					
					int userSettingsChkTypeId = userSettings.getUserSettingsType().getId();
					
					 if(userSettings.getUserSettingsType().getIsLayer()) {
						 
						 Layer layer = new Layer();
		 	        	 layer.setId(Integer.parseInt(settingsKeyAndLayerIdArray[1]));
					
		 	        	Optional<UserSettings> optionalUserSettingsChkDb = userSettingsDbList.stream().filter(item -> item.getLayer() != null && item.getLayer().getId().equals(layer.getId())  && item.getUserSettingsType().getId().equals(userSettingsChkTypeId)).findFirst();
						 
						 if(optionalUserSettingsChkDb.isPresent()) {
							 
							  userSettings = optionalUserSettingsChkDb.get();
			        	 }
						 
						 if (value != null && value.equals("")) {
								
							  userSettings.setSettingsValue("true"); 
							  userSettings.setLayer(layer);	
			 	        		
						  }
						 else {
							   
							  value = !StringUtils.isBlank(value) + "";
							  userSettings.setSettingsValue(value);
							  userSettings.setLayer(layer);	
						  }
		
						 userSettings.setUser(user);
					
					 }
					 else {
						 
						 Optional<UserSettings> optionalUserSettingsChkDb = userSettingsDbList.stream().filter(item -> item.getUserSettingsType().getSettingsKey().equals(settingsKeyAndLayerIdArray[0])).findFirst();
						 
						 if(optionalUserSettingsChkDb.isPresent()) {
							 
							  userSettings = optionalUserSettingsChkDb.get();
			        	 }
						 
						 if (value != null && value.equals("")) {
								
							  userSettings.setSettingsValue("true"); 
			 	        		
						  }
	//					 if (value == null) {
	//	
	//						  userSettings.setSettingsValue("false"); 
	//		 	        		
	//					  }
						  else {
							   
							  value = !StringUtils.isBlank(value) + "";
							  userSettings.setSettingsValue(value);
						  }
		
						 userSettings.setUser(user);
					 }
						 
					 break;
					
				case "pass":
//					//boşsa continue.(en alta ulaşınca changeList e ekleneceği için)
//					if (StringUtils.isBlank(value)) {
//						continue;
//					}
//					settings.setSettingsValue(passwordEncoder.encode(value));
					break;
					
				default:

	//				 if (value == null) {
	//					 continue;
	//				 }
					
					logTypeE = LogTypeE.USER_SETTINGS_EDIT;
	
					int userSettingsTypeId = userSettings.getUserSettingsType().getId();
					
	                if(userSettings.getUserSettingsType().getIsLayer()) {
	                	
	                	Layer layer = new Layer();
	 	        		layer.setId(Integer.parseInt(settingsKeyAndLayerIdArray[1]));
	
	 	        		Optional<UserSettings> optionalUserSettingsLayerDb = userSettingsDbList.stream().filter(item -> item.getLayer() != null && item.getLayer().getId().equals(layer.getId())  && item.getUserSettingsType().getId().equals(userSettingsTypeId)).findFirst();
	 	        		
	 	        		if(optionalUserSettingsLayerDb.isPresent()) {	 	        			
	 	        			
	 	        			if (optionalUserSettingsLayerDb.get().getUserSettingsType().getSettingsKey().equals("EventTypes")) {
	 	        				valueMultiple = request.getParameterValues(userSettingsKey);
		 	        			userSettings = optionalUserSettingsLayerDb.get();
		 	        			String valueMultipleToString = valueMultiple != null ? String.join(",", valueMultiple) : "";
		 	        			userSettings.setSettingsValue(valueMultipleToString);
	 	        			}
	 	        			else {
		 	        			userSettings = optionalUserSettingsLayerDb.get();
		 	        			userSettings.setSettingsValue(value);
	 	        			} 
	 	        			
	 	        		}
	 	        		else {
	 	        			
	 	        			if (userSettings.getUserSettingsType().getSettingsKey().equals("EventTypes")) {
		 	        			userSettings.setUser(user);
								userSettings.setLayer(layer);
								valueMultiple = request.getParameterValues(userSettingsKey);
								String valueMultipleToString = valueMultiple != null ? String.join(",", valueMultiple) : "";
		 	        			userSettings.setSettingsValue(valueMultipleToString);
	 	        			}
	 	        			else {
		 	        			userSettings.setUser(user);
								userSettings.setLayer(layer);	
						        userSettings.setSettingsValue(value); 
	 	        			}

	 	        		}
	 	 
	                }
	                else {
	
	                    Optional<UserSettings> optionalUserSettingsGeneralDb = userSettingsDbList.stream().filter(item -> item.getUserSettingsType().getSettingsKey().equals(settingsKeyAndLayerIdArray[0]) && item.getUserSettingsType().getId().equals(userSettingsTypeId)).findFirst();
	 	        		
	 	        		if(optionalUserSettingsGeneralDb.isPresent()) {
	 	        			
	 	        			userSettings = optionalUserSettingsGeneralDb.get();
	 	        		    userSettings.setSettingsValue(value); 
	 	        		}
	                	else {
	                		
	                		userSettings.setUser(user);
					        userSettings.setSettingsValue(value); 
	                	}
	
	                }
								 
					break;
			     }
			 	
				 UserSettingsItem oldSettings = UserSettingsItem.newInstanceForLog(userSettings);
	
			     changeList.add(userSettings); 
				 oldSettingsForLog.add(oldSettings);
				 newSettingsForLog.add(UserSettingsItem.newInstanceForLog(userSettings));

		      }
			
	
			
		} catch (Exception e) {
			
			log.error(e);			
			
			return false;
		}
		

		if (!changeList.isEmpty()) {
			
			userSettingsRepository.saveAll(changeList);
	
			userSettingsForLog.put("old", oldSettingsForLog);
			userSettingsForLog.put("new", newSettingsForLog);
			dbLogger.log(new Gson().toJson(userSettingsForLog), logTypeE);
		}
		 
		return true;
	}
	
//	@Operation(summary = "Temizle")
	@RequestMapping(value = "/clean/{groupName}", method = RequestMethod.POST)
	public Boolean clean(@PathVariable(name = "groupName") String groupName, HttpServletRequest request) {
		
		UserItemDetails sessionUser = ApplicationContextUtils.getUser();
		User user = new User();
		user.setId(sessionUser.getUserId());	
		
		Integer groupLayerId=null;
		
        for(UserLayerPermissionItem userLayerPermission : sessionUser.getUserLayerPermissionList()) {
			
			if(userLayerPermission.getLayerName().equals(groupName)) {
				
				groupName="Katman";
				groupLayerId=userLayerPermission.getLayerId();
			}
		}

		List<UserSettings> userSettingsDbList = userSettingsRepository.findAllProjectedByUserAndUserSettingsTypeGroupName(user, groupName);
		
		List<UserSettingsType> settingsTypeList = userSettingsTypeRepository.findAllProjectedByGroupName(groupName);
		
		List<String> userSettingsKeyList = new ArrayList<>();
		
		Map<String, Object> userSettingsForLog = new TreeMap<>();
		
		for (UserSettingsType userSettingsType : settingsTypeList) {
			
			if(userSettingsType.getIsLayer()) {

				String userSettingsKey = userSettingsType.getSettingsKey().toString() + "_" + groupLayerId.toString();
				userSettingsKeyList.add(userSettingsKey);
				
			}
			else {
				
				String userSettingsKey = userSettingsType.getSettingsKey().toString() + "_null";
				userSettingsKeyList.add(userSettingsKey);
			}
					
		}
			
		try {
			
			for (String userSettingsKey : userSettingsKeyList) {

				String value = request.getParameter(userSettingsKey);
			    	
			    UserSettings userSettings= new UserSettings();
	
	            String[] settingsKeyAndLayerIdArray = userSettingsKey.split("_");
	            
	           
				for (UserSettingsType userSettingsTypeItem : settingsTypeList) {
					
					if(userSettingsTypeItem.getSettingsKey().equals(settingsKeyAndLayerIdArray[0])) {
						
						userSettings.setUserSettingsType(userSettingsTypeItem);
						break;
				    }	
			    }
					
	                
				
				switch (userSettings.getUserSettingsType().getType()) {
				
				case "checkbox":

					int userSettingsChkTypeId = userSettings.getUserSettingsType().getId();
					
					 if(userSettings.getUserSettingsType().getIsLayer()) {
						 
						 Layer layer = new Layer();
		 	        	 layer.setId(Integer.parseInt(settingsKeyAndLayerIdArray[1]));
					
		 	        	Optional<UserSettings> optionalUserSettingsChkDb = userSettingsDbList.stream().filter(item -> item.getLayer() != null && item.getLayer().getId().equals(layer.getId())  && item.getUserSettingsType().getId().equals(userSettingsChkTypeId)).findFirst();
						 
						 if(optionalUserSettingsChkDb.isPresent()) {
							 
							  userSettings = optionalUserSettingsChkDb.get();
							  userSettingsRepository.delete(userSettings);
			        	 }
						 
						 if (value != null && value.equals("")) {
								
							  userSettings.setSettingsValue("false"); 
							  userSettings.setLayer(layer);	
			 	        		
						  }
						 else {
							   
							  value = !StringUtils.isBlank(value) + "";
							  userSettings.setSettingsValue(value);
							  userSettings.setLayer(layer);	
						  }
		
						 userSettings.setUser(user);
					
					 }
					 else {
						 
						 Optional<UserSettings> optionalUserSettingsChkDb = userSettingsDbList.stream().filter(item -> item.getUserSettingsType().getSettingsKey().equals(settingsKeyAndLayerIdArray[0])).findFirst();
						 
						 if(optionalUserSettingsChkDb.isPresent()) {
							 
							  userSettings = optionalUserSettingsChkDb.get();
							  userSettingsRepository.delete(userSettings);
			        	 }
						 
						 if (value != null && value.equals("")) {
								
							  userSettings.setSettingsValue("false"); 
			 	        		
						  }
	//					 if (value == null) {
	//	
	//						  userSettings.setSettingsValue("false"); 
	//		 	        		
	//					  }
						  else {
							   
							  value = !StringUtils.isBlank(value) + "";
							  userSettings.setSettingsValue(value);
						  }
		
						 userSettings.setUser(user);
					 }

					 break;
					
				case "pass":
//					//boşsa continue.(en alta ulaşınca changeList e ekleneceği için)
//					if (StringUtils.isBlank(value)) {
//						continue;
//					}
//					settings.setSettingsValue(passwordEncoder.encode(value));
					break;
					
				default:
 
//					if (value == null) {
//				    	 continue;
//					}
	
					int userSettingsTypeId = userSettings.getUserSettingsType().getId();
					
	                if(userSettings.getUserSettingsType().getIsLayer()) {
	                	
	                	Layer layer = new Layer();
	 	        		layer.setId(Integer.parseInt(settingsKeyAndLayerIdArray[1]));
	
	 	        		Optional<UserSettings> optionalUserSettingsLayerDb = userSettingsDbList.stream().filter(item -> item.getLayer() != null && item.getLayer().getId().equals(layer.getId()) && item.getUserSettingsType().getId().equals(userSettingsTypeId)).findFirst();
	 	        		
	 	        		if(optionalUserSettingsLayerDb.isPresent()) {
	 	        			
	 	        			userSettings = optionalUserSettingsLayerDb.get();
	 	        			userSettingsRepository.delete(userSettings);
			
	 	        		}
	 	 
	                }
	                else {
	
	                    Optional<UserSettings> optionalUserSettingsGeneralDb = userSettingsDbList.stream().filter(item -> item.getUserSettingsType().getSettingsKey().equals(settingsKeyAndLayerIdArray[0]) && item.getUserSettingsType().getId().equals(userSettingsTypeId)).findFirst();
	 	        		
	 	        		if(optionalUserSettingsGeneralDb.isPresent()) {
	 	        			
	 	        			userSettings = optionalUserSettingsGeneralDb.get();		
	                        userSettingsRepository.delete(userSettings);
	 	        		 }
	
	                }
	                userSettings.setUser(user);

					break;
			    }
			
                userSettingsForLog.put("deleted", UserSettingsItem.newInstanceForLog(userSettings));
                dbLogger.log(new Gson().toJson(userSettingsForLog), LogTypeE.USER_SETTINGS_DELETE);
			  } 


			  } catch (Exception e) {
				
				 log.error(e);
			  }
	
		 
			return true;

	}
			
}

