package com.imst.event.map.web.services;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.imst.event.map.hibernate.entity.BlackList;
import com.imst.event.map.hibernate.entity.Event;
import com.imst.event.map.hibernate.entity.EventBlackList;
import com.imst.event.map.web.constant.StateE;
import com.imst.event.map.web.db.repositories.EventBlackListRepository;
import com.imst.event.map.web.db.repositories.EventRepository;
import com.imst.event.map.web.vo.EventItem;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class TransactionalEventBlackListService {
	
	@Autowired private EventRepository eventRepository;
	@Autowired private EventBlackListRepository eventBlackListRepository;
	

	@Transactional(transactionManager = "masterTransactionManager")
	public void blackListIsStateFalse(List<EventItem> eventList, BlackList blackList) {
		//önce delete çağırılacak sonra ara tabloda kalan event list olarak dönülüp true olarak güncellenecek.
		
		List<Integer> updateListForBlackListStateSetToTrue = eventList.stream().filter(f -> f.getStateId().equals(StateE.BLACKLISTED.getValue())).map(EventItem::getId).collect(Collectors.toList());
		List<Integer> eventIdListSetToTrue = this.deletedEventBlackList(updateListForBlackListStateSetToTrue, blackList.getId());
		eventRepository.batchOperationsUpdateEventBlackList(StateE.TRUE.getValue(),eventIdListSetToTrue);
	}
	
	
	public List<Integer> deletedEventBlackList(List<Integer> eventIds, Integer blackListId) {
		
		List<Integer> eventIdList = new ArrayList<>();
		try {
			eventBlackListRepository.eventBlackListDeleted(eventIds, blackListId);
			eventIdList = eventBlackListRepository.findAllByEventIdInAndBlackListIdIsNot(eventIds, blackListId).stream().map(EventBlackList::getEvent).map(Event::getId).collect(Collectors.toList());
			
		}catch (Exception e) {
			log.debug(e);
			return null;
		}
		
		
		return eventIdList;
	}

	
	
}
