//package com.imst.event.map.web.services;
//
//import java.util.Arrays;
//import java.util.List;
//import java.util.stream.Collectors;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Sort;
//import org.springframework.data.domain.Sort.Order;
//import org.springframework.stereotype.Service;
//
//import com.imst.event.map.hibernate.entity.BlackList;
//import com.imst.event.map.hibernate.entity.Event;
//import com.imst.event.map.hibernate.entity.EventBlackList;
//import com.imst.event.map.web.constant.StateE;
//import com.imst.event.map.web.db.dao.MasterDao;
//import com.imst.event.map.web.db.projections.EventGroupProjection;
//import com.imst.event.map.web.db.repositories.EventBlackListRepository;
//import com.imst.event.map.web.db.repositories.EventGroupRepository;
//import com.imst.event.map.web.db.repositories.EventRepository;
//import com.imst.event.map.web.db.specifications.EventSpecificationForBlackList;
//import com.imst.event.map.web.utils.EventGroupTree;
//import com.imst.event.map.web.vo.EventItem;
//import com.imst.event.map.web.vo.EventItemForBlackList;
//
//import lombok.extern.log4j.Log4j2;
//
//@Service
//@Log4j2
//public class BlackListService {
//
//	@Autowired private EventRepository eventRepository;
//	@Autowired private EventGroupRepository eventGroupRepository;
//	@Autowired private MasterDao masterDao;
//	@Autowired private EventBlackListRepository eventBlackListRepository;
//	@Autowired private TransactionalEventBlackListService transactionalEventBlackListService;
//	
//	
//	//blacklist silindiğinde olayların durumu true yapılıyor.
//	public boolean updatingEventsAfterBlackListDeleted(BlackList blackList) {
//		
//		List<EventGroupProjection> allEventGroupList = eventGroupRepository.findAllProjectedByLayerOrderByName(blackList.getLayer());
//		EventGroupTree eventGroupTree = new EventGroupTree(allEventGroupList);
//		EventItemForBlackList eventItem = new EventItemForBlackList();
//		eventItem.setLayerId(blackList.getLayer().getId());
//		eventItem.setBlackListTag(blackList.getTag());
//		eventItem.setBlackListId(blackList.getId());
//		if(blackList.getEventGroup() != null) {
//			
//			List<Integer> permissionEventGroupIdList = eventGroupTree.getPermissionEventGroup(Arrays.asList(blackList.getEventGroup().getId()));
//			permissionEventGroupIdList.add(blackList.getEventGroup().getId());
//			eventItem.setEventGroupIdList(permissionEventGroupIdList);
//		}
//		
//		if (blackList.getEventType() != null) {
//			eventItem.setEventTypeId(blackList.getEventType().getId());
//		}
//		EventSpecificationForBlackList eventSpecificationForBlackList = new EventSpecificationForBlackList(eventItem);
//		
//		int index = 0;
//		while (true) {
//
//			PageRequest pageRequest = PageRequest.of(index, 10000, Sort.by(Order.asc("id")));
//
//			Page<EventItem> eventListPage = masterDao.findAll(eventSpecificationForBlackList, pageRequest);
//			List<EventItem> eventList = eventListPage.getContent();
//
//			if(eventList.size() == 0) {
//				break;
//			}
//			
//			
//			try {
//				transactionalEventBlackListService.blackListIsStateFalse(eventList, blackList);	
//				
//			} catch (Exception e) {
//				log.debug(e);
//				return false;
//			}
//			
//			index++;
//		}
//		
//		log.info("BlackList silindiği için, bu blackliste ait olayların durumu aktif yapılmıştır.");
//		return true;
//		
//	}
//	
//	public boolean deletedEventBlackList(List<Integer> eventIds, Integer blackListId) {
//		
//		try {
//			eventBlackListRepository.eventBlackListDeleted(eventIds, blackListId);
//			List<Integer> event = eventBlackListRepository.findAllByEventIdInAndBlackListIdIsNot(eventIds, blackListId).stream().map(EventBlackList::getEvent).map(Event::getId).collect(Collectors.toList());
//			eventRepository.batchOperationsUpdateEventBlackList(StateE.BLACKLISTED.getValue(), event);
//			
//			
//		}catch (Exception e) {
//			log.debug(e);
//			return false;
//		}
//		
//		
//		return true;
//	}
//	
//
//	
//}
