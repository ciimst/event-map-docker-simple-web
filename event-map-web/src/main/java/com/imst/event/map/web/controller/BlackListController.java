package com.imst.event.map.web.controller;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.imst.event.map.hibernate.entity.BlackList;
import com.imst.event.map.hibernate.entity.EventGroup;
import com.imst.event.map.hibernate.entity.EventType;
import com.imst.event.map.hibernate.entity.Layer;
import com.imst.event.map.hibernate.entity.User;
import com.imst.event.map.web.constant.ActionStateE;
import com.imst.event.map.web.constant.LogTypeE;
import com.imst.event.map.web.constant.SettingsE;
import com.imst.event.map.web.constant.StateE;
import com.imst.event.map.web.datatables.ajax.ColumnDef;
import com.imst.event.map.web.datatables.ajax.DataSet;
import com.imst.event.map.web.datatables.ajax.DatatablesCriterias;
import com.imst.event.map.web.datatables.ajax.DatatablesResponse;
import com.imst.event.map.web.datatables.util.StringUtils;
import com.imst.event.map.web.db.dao.MasterDao;
import com.imst.event.map.web.db.projections.EventGroupProjection;
import com.imst.event.map.web.db.repositories.BlackListRepository;
import com.imst.event.map.web.db.repositories.EventGroupRepository;
import com.imst.event.map.web.db.repositories.EventTypeRepository;
import com.imst.event.map.web.db.specifications.BlackListSpecifications;
import com.imst.event.map.web.security.UserItemDetails;
import com.imst.event.map.web.services.DBLogger;
import com.imst.event.map.web.utils.ApplicationContextUtils;
import com.imst.event.map.web.utils.DateUtils;
import com.imst.event.map.web.utils.EventGroupTree;
import com.imst.event.map.web.utils.SettingsUtil;
import com.imst.event.map.web.vo.BlackListItem;
import com.imst.event.map.web.vo.EventGroupParentItem;
import com.imst.event.map.web.vo.EventTypeItem;
import com.imst.event.map.web.vo.GenericResponseItem;
import com.imst.event.map.web.vo.UserEventGroupPermissionItem;
import com.imst.event.map.web.vo.UserLayerPermissionItem;

import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
public class BlackListController {
    
	@Autowired
	private MasterDao masterDao;
	@Autowired
	private EventGroupRepository eventGroupRepository;
	@Autowired 
	private EventTypeRepository eventTypeRepository;
	@Autowired 
	private BlackListRepository blackListRepository;
	@Autowired
	private DBLogger dbLogger;
	

    @RequestMapping(value = "/black-list", method = RequestMethod.POST)
    @ResponseBody
    public DatatablesResponse<BlackListItem> blacklistData(@RequestBody DatatablesCriterias criteria) {

		String blackListSearch = null;
		String blackListTagSearch = null;
		Integer layerIdSearch = null;
		Integer eventGroupIdSearch = null;
		Integer eventTypeIdSearch = null;

		Boolean blackListStateSearch = null;

		List<ColumnDef> columns = criteria.getColumns();
		for (ColumnDef columnDef : columns) {
			
			if (columnDef.getData() == null) {
				continue;
			}
			
			if (StringUtils.isBlank(columnDef.getSearch().getValue())) {
				continue;
			}
			
			switch (columnDef.getName()) {
			case "name":
				if (StringUtils.isNotBlank(columnDef.getSearch().getValue())) {
					blackListSearch = columnDef.getSearch().getValue(); 
				}
				break;
			case "tag":
				if (StringUtils.isNotBlank(columnDef.getSearch().getValue())) {
					blackListTagSearch = columnDef.getSearch().getValue(); 
				}
				break;
			case "layerId":
				if (StringUtils.isNotBlank(columnDef.getSearch().getValue())) {
					try {
						layerIdSearch = Integer.parseInt(columnDef.getSearch().getValue()); 
					} catch (Exception e) {
					}
				}
				break;
			case "eventGroupId":
				if (StringUtils.isNotBlank(columnDef.getSearch().getValue())) {
					try {
						eventGroupIdSearch = Integer.parseInt(columnDef.getSearch().getValue()); 
					} catch (Exception e) {
					}
				}
				break;
			case "eventTypeId":
				if (StringUtils.isNotBlank(columnDef.getSearch().getValue())) {
					try {
						eventTypeIdSearch = Integer.parseInt(columnDef.getSearch().getValue()); 
					} catch (Exception e) {
					}
				}
				break;	
			case "state":
				if (StringUtils.isNotBlank(columnDef.getSearch().getValue())) {
					try {
						blackListStateSearch = Boolean.parseBoolean(columnDef.getSearch().getValue()); 
					} catch (Exception e) {
					}
				}
				break;	
			default:
				break;
			}
		}
 
		int pageLoadLimit = SettingsUtil.getInteger(SettingsE.PAGE_BLACK_LIST_COUNT_PER_LOAD);
//		int page = criteria.getStart() / pageLoadLimit;
		
//    	PageRequest pageRequest = PageRequest.of(page, pageLoadLimit, Sort.by(Order.desc("createDate"), Order.desc("id"))); 
    	PageRequest pageRequest = criteria.getPageRequest(BlackListItem.class);	
    	
    	UserItemDetails sessionUser = ApplicationContextUtils.getUser();
		User user = new User();
		user.setId(sessionUser.getUserId());
				
		List<UserLayerPermissionItem> userLayerPermissionItemList =sessionUser.getUserLayerPermissionList();
	
		List<Integer> userLayerPermissionIdList = userLayerPermissionItemList.stream()
				.filter(o -> o.getHasFullPermission() == true)
				.map(UserLayerPermissionItem::getLayerId)
				.collect(Collectors.toList());
		
		List<Integer> userEventGroupPermissionIdList = sessionUser.getUserEventGroupPermissionList().stream().map(UserEventGroupPermissionItem::getEventGroupId).collect(Collectors.toList());	
		
//		List <Integer> allGroups = eventGroupRepository.findAll().stream().map(EventGroup::getId).collect(Collectors.toList()); //for test		
		
//		List <Integer> allLayers = layerRepository.findAll().stream().map(Layer::getId).collect(Collectors.toList()); //for test	
		
		
    	Page<BlackListItem> blackListItems = masterDao.findAll(BlackListSpecifications.blackListSpecification(blackListSearch, blackListTagSearch, layerIdSearch, eventGroupIdSearch, eventTypeIdSearch, blackListStateSearch, userLayerPermissionIdList, userEventGroupPermissionIdList), pageRequest);
	
		long totalBlackListCount = masterDao.executeCountQueryAndGetResultBlackList(BlackListSpecifications.totalCountBlackLists(blackListSearch, blackListTagSearch, layerIdSearch, eventGroupIdSearch, eventTypeIdSearch, blackListStateSearch, userLayerPermissionIdList, userEventGroupPermissionIdList));

		
//		Long totalEventCount = masterDao.findAll(BlackListSpecifications.totalCountBlackLists(blackListSearch, blackListTagSearch, layerIdSearch, eventGroupIdSearch, eventTypeIdSearch, blackListStateSearch, allLayers, allLayers), Sort.by("id"));

		List<Integer> eventGroupIdList = blackListItems.stream().map(BlackListItem::getEventGroupId).collect(Collectors.toList());
        List<EventGroup> eventGroupList = eventGroupRepository.findAllProjectedByIdIn(eventGroupIdList);
        
        List<Integer> eventTypeIdList = blackListItems.stream().map(BlackListItem::getEventTypeId).collect(Collectors.toList());
        List<EventTypeItem> eventTypeList = eventTypeRepository.findAllProjectedByIdIn(eventTypeIdList);
		      
        String language = LocaleContextHolder.getLocale().getLanguage();
		Locale locale = new Locale(language);
		
        for (BlackListItem blackListItemTemp : blackListItems) {
			
        	//Foreign key alanı olarak null gelebildiği için  name alanaı sonradan atanamaktadır
        	Optional<EventGroup> eventGroupOptional = eventGroupList.stream().filter(item -> item.getId().equals(blackListItemTemp.getEventGroupId())).findFirst();
			
			if(eventGroupOptional.isPresent()) {
				
				blackListItemTemp.setEventGroupName(eventGroupOptional.get().getName());
			}
			
			//Foreign key alanı olarak null gelebildiği için bu şekilde bir atama yapıldı. name alanaı sonradan atanamaktadır
			Optional<EventTypeItem> eventTypeOptional = eventTypeList.stream().filter(item -> item.getId().equals(blackListItemTemp.getEventTypeId())).findFirst();
			
			if(eventTypeOptional.isPresent()) {
				
				String name = ApplicationContextUtils.getMessage("icons." + eventTypeOptional.get().getCode(), locale);
				name = name.equals("icons." + eventTypeOptional.get().getCode()) ? blackListItemTemp.getEventTypeName() : name;
				blackListItemTemp.setEventTypeName(name);
			}
				
		}
        
//        blackListItems.getContent().forEach(item->  {
//        	System.out.println(item.getEventGroupId());
//        });
 
		DataSet<BlackListItem> dataSet = new DataSet<>(blackListItems.getContent(), (long) pageLoadLimit, totalBlackListCount);
		return DatatablesResponse.build(dataSet, criteria);
    }
    
//	@Operation(summary = "Güncelleme")
	@RequestMapping(value = "/edit", method = RequestMethod.POST)
	public GenericResponseItem edit(Integer blackListId) {

		GenericResponseItem genericResponseItem = new GenericResponseItem(true, "");
		
		
		BlackListItem blackListItem = blackListRepository.findOneByIdAndStateIdIn(blackListId, Arrays.asList(StateE.TRUE.getValue(), StateE.FALSE.getValue()));
		
		UserItemDetails sessionUser = ApplicationContextUtils.getUser();
		
		
		if (blackListItem.getEventGroupId() != null) {
			
			Optional<UserEventGroupPermissionItem> eventGroupPermissionOptional = sessionUser.getUserEventGroupPermissionList().stream().filter(item -> item.getEventGroupId().equals(blackListItem.getEventGroupId())).findFirst();
			
			if(!eventGroupPermissionOptional.isPresent()) {
				
				genericResponseItem.setState(false);
				genericResponseItem.setData(null);
				return genericResponseItem;
			}
		}
		else {
			
	        Optional<UserLayerPermissionItem> layerPermissionOptional = sessionUser.getUserLayerPermissionList().stream().filter(item -> item.getHasFullPermission() == true).filter(item -> item.getLayerId().equals(blackListItem.getLayerId())).findFirst();
			
			if(!layerPermissionOptional.isPresent()) {
				
				genericResponseItem.setState(false);
				genericResponseItem.setData(null);
				return genericResponseItem;
			}
		}
		
		if(blackListItem.getActionStateId().equals(ActionStateE.PENDING.getValue()) || blackListItem.getActionStateId().equals(ActionStateE.RUNNING.getValue())) {
			genericResponseItem.setState(false);
			genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.no.action.can.be.taken.on.the.blacklist"));
			return genericResponseItem;
		}
		
		genericResponseItem.setData(blackListItem);
		return genericResponseItem;
	}
	
	
//	@Operation(summary = "Kaydet")
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	@ResponseBody
	public GenericResponseItem save(BlackListItem blackListItem) {
					
		GenericResponseItem genericResponseItem = new GenericResponseItem(true, ApplicationContextUtils.getMessage("label.success.save"));
		
		Timestamp nowT = DateUtils.nowT();
		Map<String, Object> blackListsForLog = new TreeMap<>();
		List<Object> newBlackListForLog = new ArrayList<>();
		LogTypeE logTypeE = LogTypeE.BLACK_LIST_ADD;;
		BlackList blackList;
		
		UserItemDetails sessionUser = ApplicationContextUtils.getUser();	
		
		if (StringUtils.isBlank(blackListItem.getTag()) || blackListItem.getTag() == null) {
			genericResponseItem.setState(false);
			genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.black.list.tag.correctly"));//TODO:lang  
			return genericResponseItem;
		}
		
		 String[] arraytags = blackListItem.getTag().split("\n");
		 List<BlackList> blackListList = new ArrayList<>();
		 
		 for(String tag : arraytags) {
			 
			if (tag.trim().equals("")) {
				continue;
			}
			 
			if (Optional.ofNullable(blackListItem.getId()).orElse(0) > 0) {//edit
				
				logTypeE = LogTypeE.BLACK_LIST_EDIT;   
				
				blackList = blackListRepository.findByIdAndStateIdIn(blackListItem.getId(), Arrays.asList(StateE.TRUE.getValue(), StateE.FALSE.getValue()));
				
				if (blackList == null) {
					genericResponseItem.setState(false);
					genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.black.list.not.found"));//TODO:lang  
					return genericResponseItem;
				}
				
				if (blackList.getEventGroup() != null) {
					
					Integer blackListEventGroupId = blackList.getEventGroup().getId();
					Optional<UserEventGroupPermissionItem> eventGroupPermissionOptional = sessionUser.getUserEventGroupPermissionList().stream().filter(item -> item.getEventGroupId().equals(blackListEventGroupId)).findFirst();
					
					if(!eventGroupPermissionOptional.isPresent()) {
						
						genericResponseItem.setState(false);
						genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.you.not.permission.event.group.you.want.add"));//TODO:lang  
						return genericResponseItem;
					}
				}
				else {
					
					Integer blackListLayerId = blackList.getLayer().getId();
			        Optional<UserLayerPermissionItem> layerPermissionOptional = sessionUser.getUserLayerPermissionList().stream().filter(item -> item.getHasFullPermission() == true).filter(item -> item.getLayerId().equals(blackListLayerId)).findFirst();
					
					if(!layerPermissionOptional.isPresent()) {
						
						genericResponseItem.setState(false);
						genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.you.not.permission.event.group.you.want.add"));//TODO:lang  
						return genericResponseItem;
					}
				}
				
				if (blackListItem.getEventGroupId() != null) {
					
					Optional<UserEventGroupPermissionItem> eventGroupPermissionOptional = sessionUser.getUserEventGroupPermissionList().stream().filter(item -> item.getEventGroupId().equals(blackListItem.getEventGroupId())).findFirst();
					
					if(!eventGroupPermissionOptional.isPresent()) {
						
						genericResponseItem.setState(false);
						genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.you.not.permission.event.group.you.want.add"));//TODO:lang  
						return genericResponseItem;
					}
				}
				else {
					
			        Optional<UserLayerPermissionItem> layerPermissionOptional = sessionUser.getUserLayerPermissionList().stream().filter(item -> item.getHasFullPermission() == true).filter(item -> item.getLayerId().equals(blackListItem.getLayerId())).findFirst();
					
					if(!layerPermissionOptional.isPresent()) {
						
						genericResponseItem.setState(false);
						genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.you.not.permission.event.group.you.want.add"));//TODO:lang  
						return genericResponseItem;
					}
				}
				
				if(blackList.getActionState() != null && (blackList.getActionState().getId().equals(ActionStateE.PENDING.getValue()) || blackList.getActionState().getId().equals(ActionStateE.RUNNING.getValue()))) {
					genericResponseItem.setState(false);
					genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.no.action.can.be.taken.on.the.blacklist"));
					return genericResponseItem;
				}
				
				
				Boolean stateStatus = blackListItem.getState() != null && blackListItem.getState() == true ? true : false;
				if(!stateStatus.equals(StateE.getIntegerStateToBoolean(blackList.getState().getId()))) {
					blackList.setActionState(ActionStateE.PENDING.getActionState());
					blackList.setActionDate(nowT);
				}
				
				
				blackListsForLog.put("old", BlackListItem.newInstanceForLog(blackList));
				
				
			} else {//add
				
				if (blackListItem.getEventGroupId() != null) {
					
					Optional<UserEventGroupPermissionItem> eventGroupPermissionOptional = sessionUser.getUserEventGroupPermissionList().stream().filter(item -> item.getEventGroupId().equals(blackListItem.getEventGroupId())).findFirst();
					
					if(!eventGroupPermissionOptional.isPresent()) {
						
						genericResponseItem.setState(false);
						genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.you.not.permission.event.group.you.want.add"));//TODO:lang  
						return genericResponseItem;
					}
				}
				else {
					
			        Optional<UserLayerPermissionItem> layerPermissionOptional = sessionUser.getUserLayerPermissionList().stream().filter(item -> item.getHasFullPermission() == true).filter(item -> item.getLayerId().equals(blackListItem.getLayerId())).findFirst();
					
					if(!layerPermissionOptional.isPresent()) {
						
						genericResponseItem.setState(false);
						genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.you.not.permission.event.group.you.want.add"));//TODO:lang  
						return genericResponseItem;
					}
				}
					
				blackList = new BlackList();
				blackList.setCreateDate(nowT);
				blackList.setTag(tag.trim());
				

				Layer layer = new Layer();
				layer.setId(blackListItem.getLayerId());
				blackList.setLayer(layer);
				
				EventGroup eventGroup = new EventGroup();
				eventGroup.setId(blackListItem.getEventGroupId());
				blackList.setEventGroup(eventGroup);
				
				EventType eventType = new EventType();
				if (blackListItem.getEventTypeId() != null) {
					eventType.setId(blackListItem.getEventTypeId());
					blackList.setEventType(eventType);
				}
				
				blackList.setActionState(ActionStateE.PENDING.getActionState());
				blackList.setActionDate(nowT);
			}
			
			
			
			
			blackList.setName(blackListItem.getName());
			
			
			blackList.setCreateUser(ApplicationContextUtils.getUser().getUsername());
			blackList.setState(StateE.getBooleanState(blackListItem.getState()));
			
			
			
			blackListList.add(blackList);
			newBlackListForLog.add(BlackListItem.newInstanceForLog(blackList));
	    }
		 
		blackListRepository.saveAll(blackListList);
		
		
		blackListsForLog.put("new", newBlackListForLog);
		dbLogger.log(new Gson().toJson(blackListsForLog), logTypeE);
			
	
		return genericResponseItem;
	}
	 
//	@Operation(summary = "Sil")
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public GenericResponseItem delete(Integer blackListId) {

		GenericResponseItem genericResponseItem = new GenericResponseItem(true, ApplicationContextUtils.getMessage("label.success.delete"));
		
		try {
	
			BlackList blackList = blackListRepository.findByIdAndStateIdIn(blackListId, Arrays.asList(StateE.TRUE.getValue(), StateE.FALSE.getValue()));
			
			if(!blackList.getState().getId().equals(StateE.TRUE.getValue()) && !blackList.getState().getId().equals(StateE.FALSE.getValue())) {
				genericResponseItem.setState(false);
				return genericResponseItem;
			}
			
			UserItemDetails sessionUser = ApplicationContextUtils.getUser();
			
			List<Integer> userEventGroupPermissionIdList = sessionUser.getUserEventGroupPermissionList().stream().map(UserEventGroupPermissionItem::getEventGroupId).collect(Collectors.toList());
			
			List<Integer> userLayerFullPermissionIdList = sessionUser.getUserLayerPermissionList().stream().filter(n -> n.getHasFullPermission() == true).map(UserLayerPermissionItem::getLayerId).collect(Collectors.toList());
			
			if(blackList.getActionState().getId().equals(ActionStateE.PENDING.getValue()) || blackList.getActionState().getId().equals(ActionStateE.RUNNING.getValue())) {
				genericResponseItem.setState(false);
				genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.no.action.can.be.taken.on.the.blacklist"));
				return genericResponseItem;
			}
			
			if (blackList.getEventGroup() != null) {
				
				if (!userEventGroupPermissionIdList.stream().anyMatch(n -> n.equals(blackList.getEventGroup().getId()))) {
					genericResponseItem.setState(false);
					return genericResponseItem;
				}
				
			}	
			else {
				
				if (!userLayerFullPermissionIdList.stream().anyMatch(n -> n.equals(blackList.getLayer().getId()))) {
					genericResponseItem.setState(false);
					return genericResponseItem;
				}
				
			}
			
			
			
			blackList.setActionState(ActionStateE.PENDING.getActionState());
			blackList.setState(StateE.DELETED.getState());
			blackListRepository.save(blackList);
			
			Map<String, Object> blackListsForLog = new TreeMap<>();
			blackListsForLog.put("deleted", BlackListItem.newInstanceForLog(blackList));
			
			dbLogger.log(new Gson().toJson(blackListsForLog), LogTypeE.BLACK_LIST_DELETE);  
			
		} catch (Exception e) {
			
			log.error(e);
	
		}
		
		return genericResponseItem;
	}
	
	@RequestMapping(value="/eventGroupFilter", method = RequestMethod.POST)
	public List<EventGroupParentItem> eventGroupFilter(Integer layerId){
		
		if(layerId != null) {

			Layer layer = new Layer();
			layer.setId(layerId);
			
			UserItemDetails sessionUser = ApplicationContextUtils.getUser();
			List<Integer> eventGroupPermissionIdList = sessionUser.getUserEventGroupPermissionList().stream().filter(item -> item.getEventGroupLayerId().equals(layerId)).map(UserEventGroupPermissionItem::getEventGroupId).collect(Collectors.toList());
			
		    List<EventGroupProjection> allEventGroupListWithParent = eventGroupRepository.findAllProjectedByLayerOrderByName(layer);
		    
		    EventGroupTree eventGroupParentTree = new EventGroupTree(allEventGroupListWithParent);
			List<EventGroupParentItem> allEventGroupListWithParentItem= eventGroupParentTree.eventGroupListWithParentString(eventGroupPermissionIdList);
		    
		    return allEventGroupListWithParentItem;
		}
		return null;
	}
}
