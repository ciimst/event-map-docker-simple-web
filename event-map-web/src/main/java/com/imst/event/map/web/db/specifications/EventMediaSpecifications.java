package com.imst.event.map.web.db.specifications;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.imst.event.map.hibernate.entity.EventMedia;
import com.imst.event.map.web.db.support.CustomSpecification;
import com.imst.event.map.web.db.support.CustomSpecificationImpl;
import com.imst.event.map.web.vo.SidebarEventMediaItem;

public class EventMediaSpecifications  {
	
	@SuppressWarnings("serial")
	public static CustomSpecification<EventMedia, SidebarEventMediaItem> sidebarSpecification(List<Integer> eventIdList){		
		
		return new CustomSpecificationImpl<EventMedia, SidebarEventMediaItem>() {

			@Override
			public Predicate toPredicate(Root<EventMedia> root, CriteriaQuery<SidebarEventMediaItem> criteriaQuery,
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
