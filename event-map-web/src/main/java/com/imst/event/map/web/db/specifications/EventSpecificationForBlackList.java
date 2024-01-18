package com.imst.event.map.web.db.specifications;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;

import com.imst.event.map.hibernate.entity.Event;
import com.imst.event.map.hibernate.entity.EventBlackList;
import com.imst.event.map.web.db.support.CustomSpecificationAbs;
import com.imst.event.map.web.vo.EventItem;
import com.imst.event.map.web.vo.EventItemForBlackList;

public class EventSpecificationForBlackList extends CustomSpecificationAbs<Event, EventItem> {
	

	private static final long serialVersionUID = 7151046389517645223L;
	
	private EventItemForBlackList eventItem;
	
	public EventSpecificationForBlackList(EventItemForBlackList eventItem) {
		
		this.eventItem = eventItem;
	}
	
	@Override
	public Predicate toPredicate(Root<Event> root, CriteriaQuery<EventItem> criteriaQuery, CriteriaBuilder criteriaBuilder) {
		
		List<Predicate> predicates = new ArrayList<>();

		
		if (eventItem.getEventGroupIdList() != null) {
			
			predicates.add( root.get("eventGroup").get("id").in(eventItem.getEventGroupIdList()));
		}		
		
		if (eventItem.getEventTypeId() != null) {
			
			predicates.add(criteriaBuilder.equal(root.get("eventType").get("id"), eventItem.getEventTypeId()));
		}
		

		if(eventItem.getBlackListId() != null) {
			Join<EventBlackList, Event> alertEvent = root.join("eventBlackLists");
			
			predicates.add(criteriaBuilder.equal(alertEvent.get("blackList").get("id"), eventItem.getBlackListId()));
		}
		
		predicates.add(criteriaBuilder.equal(root.get("eventGroup").get("layer").get("id"), eventItem.getLayerId()));
		predicates.add(criteriaBuilder.equal(root.get("blackListTag"), eventItem.getBlackListTag().trim() ));

		return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
	}

	
	@Override
	public Selection<?>[] getConstructorParams(Root<Event> root) {
		
		return new Selection[] {root.get("id"),
				root.get("state").get("id")
				
		};
	}
	
}
