package com.imst.event.map.web.db.specifications;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.imst.event.map.hibernate.entity.AlertEvent;
import com.imst.event.map.web.db.support.CustomSpecification;
import com.imst.event.map.web.db.support.CustomSpecificationImpl;
import com.imst.event.map.web.vo.SidebarAlertEventItem;

public class AlertEventSpecifications {

	@SuppressWarnings("serial")
	public static CustomSpecification<AlertEvent, SidebarAlertEventItem> alertEventSpecification(List<String> eventIdDbName, Integer userId){		
		
		return new CustomSpecificationImpl<AlertEvent, SidebarAlertEventItem>() {

			@Override
			public Predicate toPredicate(Root<AlertEvent> root, CriteriaQuery<SidebarAlertEventItem> criteriaQuery,
					CriteriaBuilder criteriaBuilder) {
				
				List<Predicate> predicates = new ArrayList<>();

				if(eventIdDbName != null && !eventIdDbName.isEmpty()){
					predicates.add( root.get("eventIdDbName").in(eventIdDbName));
				}
				
				if(userId != null) {
					predicates.add(criteriaBuilder.equal(root.get("user").get("id"),userId));
				}
				
				return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
			}
		};
		
	}
	
	@SuppressWarnings("serial")
	public static CustomSpecification<AlertEvent, Long> countAlertEventSpecificationUnreadByUserAndLayer(Integer userId, Integer layerId, List<Integer> permEventGroupIds){		
		
		return new CustomSpecificationImpl<AlertEvent, Long>() {

			@Override
			public Predicate toPredicateCount(Root<AlertEvent> root, CriteriaQuery<Long> criteriaQuery, CriteriaBuilder criteriaBuilder) {

				
				List<Predicate> predicates = new ArrayList<>();

				if(userId != null) {
					predicates.add(criteriaBuilder.equal(root.get("user").get("id"),userId));
				}
				
				if(layerId != null) {
					predicates.add(criteriaBuilder.equal(root.get("alert").get("layer").get("id"), layerId));
				}
				
				predicates.add(root.get("event").get("eventGroup").get("id").in(permEventGroupIds));
				
				predicates.add(criteriaBuilder.equal(root.get("readState"), false));// True olmayanları geritir. null ve false dahil
				
				return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
			}
		};
		
	}
}
