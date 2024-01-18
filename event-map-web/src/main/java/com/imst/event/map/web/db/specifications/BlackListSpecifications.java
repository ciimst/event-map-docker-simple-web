package com.imst.event.map.web.db.specifications;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;

import org.apache.commons.lang3.StringUtils;

import com.imst.event.map.hibernate.entity.BlackList;
import com.imst.event.map.web.constant.StateE;
import com.imst.event.map.web.db.support.CustomSpecification;
import com.imst.event.map.web.db.support.CustomSpecificationAbs;
import com.imst.event.map.web.db.support.CustomSpecificationImpl;
import com.imst.event.map.web.vo.BlackListItem;


public class BlackListSpecifications  {
	
	@SuppressWarnings("serial")
	public static CustomSpecification<BlackList, BlackListItem> blackListSpecification(String blackListSearch, String blackListTagSearch, Integer layerIdSearch, Integer eventGroupIdSearch, Integer eventTypeIdSearch, Boolean blackListStateSearch, List<Integer> userLayerPermissionIdList, List<Integer> userEventGroupPermissionIdList){


		return new CustomSpecificationAbs<BlackList, BlackListItem>() {

			@Override
			public Selection<?>[] getConstructorParams(Root<BlackList> root) {
				
				return new Selection[] {root.get("id"),
						root.get("name"),
						root.get("tag"),
						root.get("createUser"),
						root.get("createDate"),
						root.get("updateDate"),
						root.get("state").get("id"),
						root.get("layer").get("id"),
						root.get("layer").get("name"),
						root.get("eventGroup").get("id"),
						root.get("eventType").get("id"),
						root.get("actionState").get("id"),
						root.get("actionState").get("stateType")
				};
				
				//return null;
			}
			
			@Override
			public Predicate toPredicate(Root<BlackList> root, CriteriaQuery<BlackListItem> criteriaQuery, CriteriaBuilder criteriaBuilder) {
				
				List<Predicate> predicates = new ArrayList<>();
				List<Predicate> userLayerPermissionOrUserEventGroupPermissionIdList = new ArrayList<>();
	
				if(!StringUtils.isBlank(blackListSearch)){
					predicates.add(ilike(criteriaBuilder, root.get("name"), blackListSearch));
				}
				
				if(!StringUtils.isBlank(blackListTagSearch)){
					predicates.add(ilike(criteriaBuilder, root.get("tag"), blackListTagSearch));
				}
				   	
		        if (Optional.ofNullable(layerIdSearch).orElse(0) > 0) {
					
					predicates.add(criteriaBuilder.equal(root.get("layer").get("id"), layerIdSearch));
				}
		        
			    if (Optional.ofNullable(eventGroupIdSearch).orElse(0) > 0) {
					
					predicates.add(criteriaBuilder.equal(root.get("eventGroup").get("id"), eventGroupIdSearch));
				}
		        
		        if (Optional.ofNullable(eventTypeIdSearch).orElse(0) > 0) {
					
					predicates.add(criteriaBuilder.equal(root.get("eventType").get("id"), eventTypeIdSearch));
				}
		        
		        if (blackListStateSearch != null) {
					predicates.add(criteriaBuilder.equal(root.get("state").get("id"), StateE.getBooleanState(blackListStateSearch).getId()));
				}else {
					predicates.add( criteriaBuilder.or(criteriaBuilder.equal(root.get("state").get("id"), StateE.FALSE.getValue()), criteriaBuilder.equal(root.get("state").get("id"), StateE.TRUE.getValue())));
				}
		        
		        userLayerPermissionOrUserEventGroupPermissionIdList.add( root.get("layer").get("id").in(userLayerPermissionIdList));
		
				userLayerPermissionOrUserEventGroupPermissionIdList.add( root.get("eventGroup").get("id").in(userEventGroupPermissionIdList));
					
				predicates.add(criteriaBuilder.or(userLayerPermissionOrUserEventGroupPermissionIdList.toArray(new Predicate[0])));

				return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
			}
		};
	}
	
	@SuppressWarnings("serial")
	public static CustomSpecification<BlackList, Long> totalCountBlackLists(String blackListSearch, String blackListTagSearch, Integer layerIdSearch, Integer eventGroupIdSearch, Integer eventTypeIdSearch, Boolean blackListStateSearch, List<Integer> userLayerPermissionIdList, List<Integer> userEventGroupPermissionIdList){
		
		return new CustomSpecificationImpl<BlackList, Long>() {

			
			@Override
			public Predicate toPredicateCount(Root<BlackList> root, CriteriaQuery<Long> criteriaQuery, CriteriaBuilder criteriaBuilder) {
				
                List<Predicate> predicates = new ArrayList<>();
            	List<Predicate> userLayerPermissionOrUserEventGroupPermissionIdList = new ArrayList<>();
				
            	if(!StringUtils.isBlank(blackListSearch)){
					predicates.add(ilike(criteriaBuilder, root.get("name"), blackListSearch));
				}
				
				if(!StringUtils.isBlank(blackListTagSearch)){
					predicates.add(ilike(criteriaBuilder, root.get("tag"), blackListTagSearch));
				}
				   	
		        if (Optional.ofNullable(layerIdSearch).orElse(0) > 0) {
					
					predicates.add(criteriaBuilder.equal(root.get("layer").get("id"), layerIdSearch));
				}
		        
			    if (Optional.ofNullable(eventGroupIdSearch).orElse(0) > 0) {
					
					predicates.add(criteriaBuilder.equal(root.get("eventGroup").get("id"), eventGroupIdSearch));
				}
		        
		        if (Optional.ofNullable(eventTypeIdSearch).orElse(0) > 0) {
					
					predicates.add(criteriaBuilder.equal(root.get("eventType").get("id"), eventTypeIdSearch));
				}
		        
		        if (blackListStateSearch != null) {
					predicates.add(criteriaBuilder.equal(root.get("state").get("id"), StateE.getBooleanState(blackListStateSearch).getId()));
				}else {
					predicates.add( criteriaBuilder.or(criteriaBuilder.equal(root.get("state").get("id"), StateE.FALSE.getValue()), criteriaBuilder.equal(root.get("state").get("id"), StateE.TRUE.getValue())));
				}
		        
		        userLayerPermissionOrUserEventGroupPermissionIdList.add( root.get("layer").get("id").in(userLayerPermissionIdList));
				
				userLayerPermissionOrUserEventGroupPermissionIdList.add( root.get("eventGroup").get("id").in(userEventGroupPermissionIdList));
					
				predicates.add(criteriaBuilder.or(userLayerPermissionOrUserEventGroupPermissionIdList.toArray(new Predicate[0])));

			
				return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
			}
		};
	}
	
}
