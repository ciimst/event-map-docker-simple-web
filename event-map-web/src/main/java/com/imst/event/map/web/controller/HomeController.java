package com.imst.event.map.web.controller;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.github.jscookie.javacookie.Cookies;
import com.imst.event.map.hibernate.entity.Layer;
import com.imst.event.map.hibernate.entity.State;
import com.imst.event.map.web.constant.SettingsE;
import com.imst.event.map.web.constant.Statics;
import com.imst.event.map.web.constant.UserSettingsTypeE;
import com.imst.event.map.web.db.dao.MasterDao;
import com.imst.event.map.web.db.projections.EventGroupProjection;
import com.imst.event.map.web.db.projections.EventTypeProjection;
import com.imst.event.map.web.db.repositories.EventGroupRepository;
import com.imst.event.map.web.db.repositories.EventTypeRepository;
import com.imst.event.map.web.db.repositories.LayerRepository;
import com.imst.event.map.web.db.repositories.StateRepository;
import com.imst.event.map.web.security.UserItemDetails;
import com.imst.event.map.web.services.EventGroupService;
import com.imst.event.map.web.services.EventMediaService;
import com.imst.event.map.web.services.EventService;
import com.imst.event.map.web.services.EventTypeService;
import com.imst.event.map.web.services.GeoLayerService;
import com.imst.event.map.web.services.MapAreaGroupService;
import com.imst.event.map.web.services.MapAreaService;
import com.imst.event.map.web.services.TagService;
import com.imst.event.map.web.services.TileServerService;
import com.imst.event.map.web.services.UserGroupIdService;
import com.imst.event.map.web.services.UserLayerPermissionService;
import com.imst.event.map.web.services.UserSettingsService;
import com.imst.event.map.web.services.UserUserIdService;
import com.imst.event.map.web.utils.ApplicationContextUtils;
import com.imst.event.map.web.utils.DateUtils;
import com.imst.event.map.web.utils.EventGroupTree;
import com.imst.event.map.web.utils.SettingsUtil;
import com.imst.event.map.web.utils.UserSettingsUtil;
import com.imst.event.map.web.vo.EventGroupItem;
import com.imst.event.map.web.vo.EventGroupParentItem;
import com.imst.event.map.web.vo.EventTypeItem;
import com.imst.event.map.web.vo.LayerSimpleItem;
import com.imst.event.map.web.vo.UserEventGroupPermissionItem;
import com.imst.event.map.web.vo.UserLayerPermissionItem;

@Controller
public class HomeController {
	
	@Autowired EventService eventService;
	@Autowired EventMediaService eventMediaService;
	@Autowired TagService tagService;
	@Autowired GeoLayerService geoLayerService;
	@Autowired MapAreaService mapAreaService;
	@Autowired TileServerService tileServerService;
	@Autowired EventGroupService eventGroupService;
	@Autowired EventTypeService eventTypeService;
	@Autowired MapAreaGroupService mapAreaGroupService;
	@Autowired UserGroupIdService userGroupIdService;
	@Autowired UserUserIdService userUserIdService;
	@Autowired UserSettingsService userSettingsService;
	@Autowired UserLayerPermissionService userLayerPermissionService;
	@Autowired EventTypeRepository eventTypeRepository;
	@Autowired EventGroupRepository eventGroupRepository;
	@Autowired LayerRepository layerRepository;
	@Autowired MasterDao masterDao;
	@Autowired StateRepository stateRepository;

	@Autowired private  HttpServletRequest request;
	@Autowired private HttpServletResponse response;
	
	
	
	@RequestMapping(value = {"/","home"}, method = RequestMethod.GET)
	public ModelAndView  home(Model model, @RequestParam(name = "layerId", required = false) String currentLayerGuid ) {
		
		
		UserItemDetails sessionUser = ApplicationContextUtils.getUser();
		model.addAttribute("pageRefreshTime", SettingsUtil.getInteger(SettingsE.PAGE_REFRESH_TIME_IN_SECOND));
		
		String nowFormatted = DateUtils.formatWithLocale(DateUtils.now(), DateUtils.NAV_TIME, ApplicationContextUtils.getMessage("lang.iso"));
		model.addAttribute("navTime", nowFormatted);
		
		model.addAttribute("timeLineStartDate", DateUtils.format(Statics.timeLineStartDate, DateUtils.TURKISH_DATE));	
		
		if(currentLayerGuid == null) {
			
			Cookies cookies = Cookies.initFromServlet( request, response );
			currentLayerGuid = cookies.get( "currentLayerGuid" ); 
			
			if(currentLayerGuid == null) {
				currentLayerGuid = sessionUser.getCurrentLayerGuid();
			}
		}
		
		if (sessionUser.getProviderUserId() != null) {
			model.addAttribute("providerUserId", sessionUser.getProviderUserId().toString());
		}
		model.addAttribute("layerId", currentLayerGuid);
		
		String url = "/region/"+currentLayerGuid;
		
		UserSettingsUtil userSettingsUtil = userSettingsService.updateUserSettingsCacheAndGet();
		
		if(userSettingsUtil.getString(UserSettingsTypeE.FIRST_PAGE_OPENED)!=null) {

			url = "/" + userSettingsUtil.getString(UserSettingsTypeE.FIRST_PAGE_OPENED) + "/"+currentLayerGuid;
		}
			
		
		if(currentLayerGuid == null) {
			return new ModelAndView("page/error");
		}
		return new ModelAndView("redirect:" + url);
	}
	
	@GetMapping(value = {"/live"})
	public ResponseEntity<?> livenessProbe() {
		try {
			List<State> cont = null;
			cont = stateRepository.findAll();
			if ((cont != null && cont.isEmpty()) || cont == null) {
				return ResponseEntity.badRequest().build();
			}
		}
		catch(Exception e) {
			return ResponseEntity.badRequest().build();
		}
		return ResponseEntity.ok().build();
	}

	@GetMapping(value = {"/ready"})
	public ResponseEntity<?> readinessProbe() {
		return ResponseEntity.ok().build();
	}
	
	@RequestMapping(value = "/region/{layerId}", method = RequestMethod.GET)
	public String layer(Model model, @PathVariable("layerId") String currentLayerGuid) {
		
		Cookies cookies = Cookies.initFromServlet( request, response );
		
		LayerSimpleItem layerItem = userLayerPermissionService.checkLayerPermissionAndGet(currentLayerGuid);
		if(layerItem == null) {
			// there is no permission to see events under this region
			cookies.remove("currentLayerGuid");
			return "redirect:/home";
		}
		
		UserItemDetails sessionUser = ApplicationContextUtils.getUser();		
		sessionUser.setCurrentLayerGuid(currentLayerGuid);
		
		
		cookies.set( "currentLayerGuid", currentLayerGuid);
		
		
		return homeModel(model, currentLayerGuid);
	}
	
	
	@RequestMapping(value = "/time/{layerId}", method = RequestMethod.GET)
	public String time(Model model, @PathVariable("layerId") String currentLayerGuid, @RequestParam("time") String time ) {
		
		UserItemDetails sessionUser = ApplicationContextUtils.getUser();
		String homePagePath = homeModel(model, currentLayerGuid);
		
		Date timeInDate = DateUtils.convertToDate(time, DateUtils.TURKISH_DATE);
		String nowFormatted = DateUtils.formatWithLocale(timeInDate, DateUtils.NAV_TIME, ApplicationContextUtils.getMessage("lang.iso"));
		model.addAttribute("navTime", nowFormatted);
		
		model.addAttribute("time", time);
		
		if (sessionUser.getProviderUserId() != null) {
		model.addAttribute("providerUserId", sessionUser.getProviderUserId().toString());
		}		
		return homePagePath;
	}
	
	@RequestMapping(value = "/timeDimension/{layerId}", method = RequestMethod.GET)
	public String timeDimension(Model model, @PathVariable("layerId") String currentLayerGuid) {
		
		String homePagePath = homeModel(model, currentLayerGuid);
		
		model.addAttribute("timeDimensionMode", true);
		
		return homePagePath;
	}
	
	@RequestMapping(value = "/event-table/{layerId}", method = RequestMethod.GET)
	public String eventTable(Model model, @PathVariable("layerId") String currentLayerGuid) {

		homeModel(model, currentLayerGuid);
		int pageLoadLimit = SettingsUtil.getInteger(SettingsE.PAGE_EVENT_COUNT_PER_LOAD);
		model.addAttribute("dataTableRowCount", pageLoadLimit);
		model.addAttribute("pageRefreshDate", DateUtils.now().getTime());
		model.addAttribute("pageRefreshTimeInterval", SettingsUtil.getInteger(SettingsE.WEB_TABLE_REFRESH_TIME_IN_SECOND));
		
		UserSettingsUtil userSettingsUtil = userSettingsService.updateUserSettingsCacheAndGet();
		
		int maxCountEventsExcel=0;
		
		if(userSettingsUtil.hasSettings(UserSettingsTypeE.MAX_COUNT_EVENTS_EXCEL) && userSettingsUtil.getInteger(UserSettingsTypeE.MAX_COUNT_EVENTS_EXCEL) <= SettingsUtil.getInteger(SettingsE.MAX_COUNT_EVENTS_EXCEL)) {
			
			maxCountEventsExcel = userSettingsUtil.getInteger(UserSettingsTypeE.MAX_COUNT_EVENTS_EXCEL);
		}
		else {
			
			maxCountEventsExcel = SettingsUtil.getInteger(SettingsE.MAX_COUNT_EVENTS_EXCEL);
		}
		
		model.addAttribute("maxCountEventsExcel", maxCountEventsExcel);
		
		return "page/event-table";
	}
	
	@RequestMapping(value = "/event-table-view/{layerId}", method = RequestMethod.GET)
	public String eventTableView(Model model, @PathVariable("layerId") String currentLayerGuid) {

		//combobox lar vs dolu olarak gönderilecek.
		homeModel(model, currentLayerGuid);
		int pageLoadLimit = SettingsUtil.getInteger(SettingsE.PAGE_EVENT_COUNT_PER_LOAD);
		model.addAttribute("dataTableRowCount", pageLoadLimit);
		model.addAttribute("pageRefreshDate", DateUtils.now().getTime());
		model.addAttribute("pageRefreshTimeInterval", SettingsUtil.getInteger(SettingsE.WEB_TABLE_REFRESH_TIME_IN_SECOND));
		
		UserSettingsUtil userSettingsUtil = userSettingsService.updateUserSettingsCacheAndGet();
		
		int maxCountEventsExcel=0;
		
		if(userSettingsUtil.hasSettings(UserSettingsTypeE.MAX_COUNT_EVENTS_EXCEL) && userSettingsUtil.getInteger(UserSettingsTypeE.MAX_COUNT_EVENTS_EXCEL) <= SettingsUtil.getInteger(SettingsE.MAX_COUNT_EVENTS_EXCEL)) {
			
			maxCountEventsExcel = userSettingsUtil.getInteger(UserSettingsTypeE.MAX_COUNT_EVENTS_EXCEL);
		}
		else {
			
			maxCountEventsExcel = SettingsUtil.getInteger(SettingsE.MAX_COUNT_EVENTS_EXCEL);
		}

		model.addAttribute("maxCountEventsExcel", maxCountEventsExcel);
		
		
		
		List<EventTypeProjection> eventTypeProjectionList = eventTypeRepository.findAllProjectedBy();
		
		String language = LocaleContextHolder.getLocale().getLanguage();
		Locale locale = new Locale(language);
		
		List<EventTypeItem> eventTypes = eventTypeProjectionList.stream().map(item -> new EventTypeItem(item)).collect(Collectors.toList());
		eventTypes.forEach(item->{
			String name = ApplicationContextUtils.getMessage("icons." + item.getCode(), locale);
			name = name.equals("icons." + item.getCode()) ? item.getName() : name;
			item.setName(name);

		});

		List<EventTypeItem> sortedEventTypeItemList = eventTypes.stream().sorted(Comparator.comparing(EventTypeItem::getName, Statics.sortedCollator())).collect(Collectors.toList());	
		
		
//		LayerSimpleItem layerItem = userLayerPermissionService.checkLayerPermissionAndGet(currentLayerGuid);
		
//		List<EventGroupItem> eventGroupPermissionList = getUserEventGroupPermissionList(layerItem.getId());
//		List<Integer> eventGroupPermissionIdList = eventGroupPermissionList.stream().map(EventGroupItem::getId).collect(Collectors.toList());
		
//		Layer layer = new Layer();
//		layer.setId(layerItem.getId());
//		List<EventGroupProjection> allEventGroupList = eventGroupRepository.findAllProjectedByLayerOrderByName(layer);
//		    
//		EventGroupTree eventGroupParentTree = new EventGroupTree(allEventGroupList);
//		List<EventGroupParentItem >allEventGroupItemList = eventGroupParentTree.eventGroupListWithParentString(eventGroupPermissionIdList);
//			
//		model.addAttribute("eventGroups", allEventGroupItemList);
		model.addAttribute("eventTypes", sortedEventTypeItemList);
		
		UserItemDetails sessionUser = ApplicationContextUtils.getUser();
		boolean hasEventBatchOperationRole = sessionUser.getAuthorities().stream().anyMatch(item -> item.getAuthority().equals("ROLE_EVENT_BATCH_OPERATIONS"));
		model.addAttribute("hasEventBatchOperationRole", hasEventBatchOperationRole);
		
		return "page/event-table-view";
	}
	
	private List<EventGroupItem> getUserEventGroupPermissionList(Integer layerId){
		

		UserItemDetails sessionUser = ApplicationContextUtils.getUser();
		
		List<UserEventGroupPermissionItem> permList = sessionUser.getUserEventGroupPermissionList();
		List<Integer> permIdlist = permList.stream().map(UserEventGroupPermissionItem::getEventGroupId).collect(Collectors.toList());
		
		List<EventGroupItem> currentLayerAllEventGroupList = eventGroupService.findAllAndSetDbName(masterDao, Sort.by(Direction.ASC, "id"), layerId ,Statics.DEFAULT_DB_NAME, permIdlist);	
		

		return currentLayerAllEventGroupList;
	}
	
	@RequestMapping(value = "/black-list/{layerId}", method = RequestMethod.GET)
	public String blackListTable(Model model, @PathVariable("layerId") String currentLayerGuid) {

		homeModel(model, currentLayerGuid);
		int pageLoadLimit = SettingsUtil.getInteger(SettingsE.PAGE_BLACK_LIST_COUNT_PER_LOAD);
		model.addAttribute("dataTableRowCount", pageLoadLimit);
		model.addAttribute("pageRefreshDate", DateUtils.now().getTime());
		model.addAttribute("pageRefreshTimeInterval", SettingsUtil.getInteger(SettingsE.WEB_TABLE_REFRESH_TIME_IN_SECOND));
		
		List<EventTypeProjection> eventTypeProjectionList = eventTypeRepository.findAllProjectedBy();
		List<EventTypeItem> eventTypes = eventTypeProjectionList.stream().map(item -> new EventTypeItem(item)).collect(Collectors.toList());
		
		String language = LocaleContextHolder.getLocale().getLanguage();
		Locale locale = new Locale(language);
		eventTypes.forEach(item->{

		
			String name = ApplicationContextUtils.getMessage("icons." + item.getCode(), locale);
			name = name.equals("icons." + item.getCode()) ? item.getName() : name;
			item.setName(name);

		});
		
		UserItemDetails sessionUser = ApplicationContextUtils.getUser();

		List<EventTypeItem> sortedEventTypeItemList = eventTypes.stream().sorted(Comparator.comparing(EventTypeItem::getName, Statics.sortedCollator())).collect(Collectors.toList());	
		
		List<Layer> allowedLayers = new ArrayList<>();
		for(UserLayerPermissionItem userLayerPermissionItem : sessionUser.getUserLayerPermissionList()) {
			
			Layer layer=new Layer();
			layer.setId(userLayerPermissionItem.getLayerId());
			layer.setName(userLayerPermissionItem.getLayerName());
			allowedLayers.add(layer);
		}
		
		List<Layer> sortedAllowedLayers = allowedLayers.stream().sorted(Comparator.comparing(Layer::getName, Statics.sortedCollator())).collect(Collectors.toList());	
//		List<Integer> layerIdList = allowedLayers.stream().map(Layer::getId).collect(Collectors.toList());	
		
		
		
//		List<Integer> userEventGroupPermissionEventGroupIdList = sessionUser.getUserEventGroupPermissionList().stream().map(UserEventGroupPermissionItem::getEventGroupId).collect(Collectors.toList());
//		List<EventGroupProjection> allEventGroupsParentItemList = eventGroupRepository.findAllProjectedByLayerIdInOrderByName(layerIdList);
		
//		EventGroupTree eventGroupParentTree = new EventGroupTree(allEventGroupsParentItemList);
//		List<EventGroupParentItem> eventGroupParentItemList = eventGroupParentTree.eventGroupListWithParentString(userEventGroupPermissionEventGroupIdList);
		

//		List<EventGroupParentItem> sortedAllowedEventGroups = eventGroupParentItemList.stream().sorted(Comparator.comparing(EventGroupParentItem::getName, Statics.sortedCollator())).collect(Collectors.toList());	

//		model.addAttribute("eventGroups",sortedAllowedEventGroups);
		model.addAttribute("eventTypes",sortedEventTypeItemList);
		model.addAttribute("layers",sortedAllowedLayers);
			
		return "page/black-list";
	}
	
	@RequestMapping(value = "/heatmap/{layerId}", method = RequestMethod.GET)
	public String heatmap(Model model, @PathVariable("layerId") String currentLayerGuid) {
		
		String homePagePath = homeModel(model, currentLayerGuid);
		
		model.addAttribute("isHeatmap", true);
		
		return homePagePath;
	}
	
	//Timedimension infinite-scroll için eklendi. Başka bir yerde kullanılmıyor.
	@RequestMapping(value = "/timeDimensionScroll", method = RequestMethod.GET)
	public String timeDimensionScroll() {

		return "page/home";
	}
	
	public String homeModel(Model model, String currentLayerGuid) {
		
		UserItemDetails sessionUser = ApplicationContextUtils.getUser();
		model.addAttribute("pageRefreshTime", SettingsUtil.getInteger(SettingsE.PAGE_REFRESH_TIME_IN_SECOND));
		
		String nowFormatted = DateUtils.formatWithLocale(DateUtils.now(), DateUtils.NAV_TIME, ApplicationContextUtils.getMessage("lang.iso"));
		model.addAttribute("navTime", nowFormatted);
		
		model.addAttribute("timeLineStartDate", DateUtils.format(Statics.timeLineStartDate, DateUtils.TURKISH_DATE));		
		
		if(currentLayerGuid == null) {
			Cookies cookies = Cookies.initFromServlet( request, response );
			currentLayerGuid = cookies.get( "currentLayerGuid" );
			if(currentLayerGuid == null) {
				
				currentLayerGuid = sessionUser.getCurrentLayerGuid();
			}
		}
		
		if (sessionUser.getProviderUserId() != null) {
			model.addAttribute("providerUserId", sessionUser.getProviderUserId().toString());
		}
		model.addAttribute("layerId", currentLayerGuid);
		
		model.addAttribute("userSettingsId", sessionUser.getCurrentUserSettingsGroupName());
		
		model.addAttribute("writeUserSettingsToCookieAfterLogin", sessionUser.getWriteUserSettingsToCookieAfterLogin());
			
		List<Layer> allowedLayers = new ArrayList<>();
		for(UserLayerPermissionItem userLayerPermissionItem : sessionUser.getUserLayerPermissionList()) {
			
			Layer layer=new Layer();
			layer.setId(userLayerPermissionItem.getLayerId());
			layer.setName(userLayerPermissionItem.getLayerName());
			allowedLayers.add(layer);
		}
		
		List<Integer> layerIdList = allowedLayers.stream().map(Layer::getId).collect(Collectors.toList());	
		
		List<Integer> userEventGroupPermissionEventGroupIdList = sessionUser.getUserEventGroupPermissionList().stream().map(UserEventGroupPermissionItem::getEventGroupId).collect(Collectors.toList());
		List<EventGroupProjection> allEventGroupsParentItemList = eventGroupRepository.findAllProjectedByLayerIdInOrderByName(layerIdList);
		
		EventGroupTree eventGroupParentTree = new EventGroupTree(allEventGroupsParentItemList);
		List<EventGroupParentItem> eventGroupParentItemList = eventGroupParentTree.eventGroupListWithParentString(userEventGroupPermissionEventGroupIdList);
		

		List<EventGroupParentItem> sortedAllowedEventGroups = eventGroupParentItemList.stream().sorted(Comparator.comparing(EventGroupParentItem::getName, Statics.sortedCollator())).collect(Collectors.toList());	


		model.addAttribute("eventGroups",sortedAllowedEventGroups);
		

		
		return "page/home";
	}
	

}
