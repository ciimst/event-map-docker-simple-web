package com.imst.event.map.web.db.specifications;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.imst.event.map.hibernate.entity.EventTag;
import com.imst.event.map.web.db.support.CustomSpecification;
import com.imst.event.map.web.db.support.CustomSpecificationImpl;
import com.imst.event.map.web.vo.SidebarTagItem;

public class EventTagSpecifications  {
	
	@SuppressWarnings("serial")
	public static CustomSpecification<EventTag, SidebarTagItem> sidebarSpecification(List<Integer> eventIdList){
		
		return new CustomSpecificationImpl<EventTag, SidebarTagItem>() {
			@Override
			public Predicate toPredicate(Root<EventTag> root, CriteriaQuery<SidebarTagItem> criteriaQuery,
					CriteriaBuilder criteriaBuilder) {
				
				List<Predicate> predicates = new ArrayList<>();

				if(eventIdList != null && !eventIdList.isEmpty()){
					predicates.add( root.get("event").get("id").in(eventIdList));
				}
				
				return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
			}
		};
		
	}
}
