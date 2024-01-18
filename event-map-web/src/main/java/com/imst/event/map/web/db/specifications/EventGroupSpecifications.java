package com.imst.event.map.web.db.specifications;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;

import com.imst.event.map.hibernate.entity.EventGroup;
import com.imst.event.map.hibernate.entity.User;
import com.imst.event.map.hibernate.entity.UserEventGroupPermission;
import com.imst.event.map.web.db.support.CustomSpecification;
import com.imst.event.map.web.db.support.CustomSpecificationAbs;
import com.imst.event.map.web.vo.EventGroupItem;
import com.imst.event.map.web.vo.UserEventGroupPermissionItem;

public class EventGroupSpecifications  {
		
	@SuppressWarnings("serial")
	public static CustomSpecification<EventGroup, EventGroupItem> eventGroupSpecification(Integer currentLayerId, List<Integer> eventGroupIdList){
		
		return new CustomSpecificationAbs<EventGroup, EventGroupItem>() {

			@Override
			public Selection<?>[] getConstructorParams(Root<EventGroup> root) {
				return null;
			}
			
			@Override
			public Predicate toPredicate(Root<EventGroup> root, CriteriaQuery<EventGroupItem> criteriaQuery,
					CriteriaBuilder criteriaBuilder) {
				
				List<Predicate> predicates = new ArrayList<>();
				
				if(eventGroupIdList != null) {
					predicates.add(root.get("id").in(eventGroupIdList));
				}
				
				predicates.add(criteriaBuilder.equal(root.get("layer").get("id"), currentLayerId));

				return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
			}
		};
		
	}
	
	@SuppressWarnings("serial")
	public static CustomSpecification<UserEventGroupPermission, UserEventGroupPermissionItem> userEventGroupPermissionSpecification(User user){
		
		return new CustomSpecificationAbs<UserEventGroupPermission, UserEventGroupPermissionItem>() {

			@Override
			public Selection<?>[] getConstructorParams(Root<UserEventGroupPermission> root) {
				return null;
			}
			
			@Override
			public Predicate toPredicate(Root<UserEventGroupPermission> root, CriteriaQuery<UserEventGroupPermissionItem> criteriaQuery,
					CriteriaBuilder criteriaBuilder) {
				
				List<Predicate> predicates = new ArrayList<>();
				
				
				predicates.add(criteriaBuilder.equal(root.get("user").get("id"), user.getId()));

				return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
			}
		};
		
	}
	
	@SuppressWarnings("serial")
	public static CustomSpecification<UserEventGroupPermission, UserEventGroupPermissionItem> userEventGroupPermissionByLayerIdListSpecification(List<Integer> layerIdList){
		
		return new CustomSpecificationAbs<UserEventGroupPermission, UserEventGroupPermissionItem>() {

			@Override
			public Selection<?>[] getConstructorParams(Root<UserEventGroupPermission> root) {
				return null;
			}
			
			@Override
			public Predicate toPredicate(Root<UserEventGroupPermission> root, CriteriaQuery<UserEventGroupPermissionItem> criteriaQuery,
					CriteriaBuilder criteriaBuilder) {
				
				List<Predicate> predicates = new ArrayList<>();
				
				
				predicates.add(root.get("eventGroup").get("layer").get("id").in(layerIdList));

				return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
			}
		};
		
	}
	

}
