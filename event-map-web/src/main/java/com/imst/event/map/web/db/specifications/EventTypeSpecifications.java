package com.imst.event.map.web.db.specifications;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;

import com.imst.event.map.hibernate.entity.EventType;
import com.imst.event.map.web.db.support.CustomSpecification;
import com.imst.event.map.web.db.support.CustomSpecificationAbs;
import com.imst.event.map.web.vo.EventTypeItem;

public class EventTypeSpecifications  {
		
	@SuppressWarnings("serial")
	public static CustomSpecification<EventType, EventTypeItem> EventTypeSpecification(List<Integer> eventTypeIdList){
		
		return new CustomSpecificationAbs<EventType, EventTypeItem>() {

			@Override
			public Selection<?>[] getConstructorParams(Root<EventType> root) {
				return null;
			}
			
			@Override
			public Predicate toPredicate(Root<EventType> root, CriteriaQuery<EventTypeItem> criteriaQuery,
					CriteriaBuilder criteriaBuilder) {
				
				List<Predicate> predicates = new ArrayList<>();
				
				
				if (eventTypeIdList != null && !eventTypeIdList.isEmpty()) {					
					predicates.add(root.get("id").in(eventTypeIdList));
				}

				return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
			}
		};
		
	}
}
