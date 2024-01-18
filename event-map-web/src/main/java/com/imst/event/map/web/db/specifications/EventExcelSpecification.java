package com.imst.event.map.web.db.specifications;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;

import org.apache.commons.lang3.StringUtils;

import com.imst.event.map.hibernate.entity.AlertEvent;
import com.imst.event.map.hibernate.entity.Event;
import com.imst.event.map.hibernate.entity.State;
import com.imst.event.map.web.constant.StateE;
import com.imst.event.map.web.constant.Statics;
import com.imst.event.map.web.db.support.CustomSpecification;
import com.imst.event.map.web.db.support.CustomSpecificationAbs;
import com.imst.event.map.web.security.UserItemDetails;
import com.imst.event.map.web.utils.ApplicationContextUtils;
import com.imst.event.map.web.vo.EventExcelItem;

public class EventExcelSpecification  {
	
	@SuppressWarnings("serial")
	public static CustomSpecification<Event, EventExcelItem> sidebarSpecification(Integer lastId, List<Integer> groupIdList, List<Integer> userIdList, 
			Integer currentLayerId,Integer alertLastId, String eventSearch, List<Integer> eventTypeIdSearch, List<Integer> eventGroupIdList, String eventSearchCity, String eventSearchCountry, Date startDate, Date endDate, Boolean isAlertEvent){
		
		return new CustomSpecificationAbs<Event, EventExcelItem>() {

			@Override
			public Selection<?>[] getConstructorParams(Root<Event> root) {
				return null;
			}
			
			@Override
			public Predicate toPredicate(Root<Event> root, CriteriaQuery<EventExcelItem> criteriaQuery,
					CriteriaBuilder criteriaBuilder) {
				
				List<Predicate> predicates = new ArrayList<>();
				List<Predicate> userOrGroupIdList = new ArrayList<>();
				
				
				
				if (lastId != null && lastId != 0) {					
					predicates.add(criteriaBuilder.gt(root.get("id"), lastId));
				}
				
				if (alertLastId != null && alertLastId != 0) {					
					predicates.add(criteriaBuilder.le(root.get("id"), alertLastId));
				}
				
				
				if(groupIdList != null && !groupIdList.isEmpty()){	
					userOrGroupIdList.add( root.get("groupId").in(groupIdList));
				}
				
				if(userIdList != null && !userIdList.isEmpty()){
					userOrGroupIdList.add( root.get("userId").in(userIdList));
				}	
				
				if(eventGroupIdList != null && ! eventGroupIdList.isEmpty() && eventGroupIdList.get(0) != 0 ) {
					
					predicates.add(root.get("eventGroup").get("id").in(eventGroupIdList));
				}		  
				
				if (startDate != null) {
					predicates.add(criteriaBuilder.greaterThan(root.get("eventDate"), startDate));
				}
				
				if (endDate != null) {
					predicates.add(criteriaBuilder.lessThan(root.get("eventDate"), endDate));
				}
				
				if(!StringUtils.isBlank(eventSearch)){
					
					Predicate spotPredicate = ilike(criteriaBuilder, root.get("spot"), eventSearch);
					Predicate descriptionPredicate = ilike(criteriaBuilder, root.get("description"), eventSearch);
					Predicate titlePredicate = ilike(criteriaBuilder, root.get("title"), eventSearch);
					
					Predicate spotOrTitleOrDescriptionSearch = criteriaBuilder.or(spotPredicate, descriptionPredicate, titlePredicate);
					
					predicates.add(criteriaBuilder.and(spotOrTitleOrDescriptionSearch));				
				
				}
				
				
				if(!StringUtils.isBlank(eventSearchCity)){
					predicates.add(ilike(criteriaBuilder, root.get("city"), eventSearchCity));
				}
				
				if(!StringUtils.isBlank(eventSearchCountry)){
					predicates.add(ilike(criteriaBuilder, root.get("country"), eventSearchCountry));
				}
				
				if(eventTypeIdSearch != null && !eventTypeIdSearch.isEmpty()) {
//					predicates.add(criteriaBuilder.equal(root.get("eventType").get("id"), eventTypeIdSearch));
					predicates.add(root.get("eventType").get("id").in(eventTypeIdSearch));
				}
				
				if(isAlertEvent != null && isAlertEvent == true) {
										
					Join<AlertEvent, Event> alertEvent = root.join("alertEvents");
					UserItemDetails sessionUser = ApplicationContextUtils.getUser();
					
					predicates.add(criteriaBuilder.equal(alertEvent.get("user").get("id"), sessionUser.getUserId()));
					predicates.add(criteriaBuilder.equal(alertEvent.get("dbName"), Statics.DEFAULT_DB_NAME));
					
					criteriaQuery.distinct(true);
					
				}
				
				predicates.add(criteriaBuilder.or(userOrGroupIdList.toArray(new Predicate[0])));
				
				predicates.add(criteriaBuilder.equal(root.get("eventGroup").get("layer").get("id"), currentLayerId));
				predicates.add(criteriaBuilder.equal(root.get("state").get("id"), StateE.TRUE.getValue()));
				
								
				
				return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
			}
		};
	}
	
	
	@SuppressWarnings("serial")
	public static CustomSpecification<Event, EventExcelItem> eventTableViewExcelSpecification(Date startDate, Date endDate,  List<Integer> groupIdList, List<Integer> userIdList, 
			Integer currentLayerId, String title, String spot, String description, List<Integer> eventTypeIdSearch, 
			List<Integer> eventGroupIdList, String eventSearchCity, String eventSearchCountry, String eventSearchBlackListTag, Boolean state, Boolean hasEventStateViewRole, 
			String reserved1, String reserved2, String reserved3, String reserved4, String reserved5, Boolean isAlertEvent){
		
		return new CustomSpecificationAbs<Event, EventExcelItem>() {

			@Override
			public Selection<?>[] getConstructorParams(Root<Event> root) {
				return null;
			}
			
			@Override
			public Predicate toPredicate(Root<Event> root, CriteriaQuery<EventExcelItem> criteriaQuery,
					CriteriaBuilder criteriaBuilder) {
				
				List<Predicate> predicates = new ArrayList<>();
				List<Predicate> userOrGroupIdList = new ArrayList<>();
				
				
				if (startDate != null) {
					predicates.add(criteriaBuilder.greaterThan(root.get("eventDate"), startDate));
				}
				
				if (endDate != null) {
					predicates.add(criteriaBuilder.lessThan(root.get("eventDate"), endDate));
				}

				
				if(groupIdList != null && !groupIdList.isEmpty()){	
					userOrGroupIdList.add( root.get("groupId").in(groupIdList));
				}
				
				if(userIdList != null && !userIdList.isEmpty()){
					userOrGroupIdList.add( root.get("userId").in(userIdList));
				}	
				
				if(eventGroupIdList != null && ! eventGroupIdList.isEmpty()) {
					
					predicates.add(root.get("eventGroup").get("id").in(eventGroupIdList));
				}		  

				
				if(!StringUtils.isBlank(title)){
					predicates.add(ilike(criteriaBuilder, root.get("title"), title));
				}
				
				if(!StringUtils.isBlank(spot)){
					predicates.add(ilike(criteriaBuilder, root.get("spot"), spot));
				}
				
				if(!StringUtils.isBlank(description)){
					predicates.add(ilike(criteriaBuilder, root.get("description"), description));
				}
				
				
				if(!StringUtils.isBlank(eventSearchCity)){
					predicates.add(ilike(criteriaBuilder, root.get("city"), eventSearchCity));
				}
				
				if(!StringUtils.isBlank(eventSearchCountry)){
					predicates.add(ilike(criteriaBuilder, root.get("country"), eventSearchCountry));
				}
				
				if(!StringUtils.isBlank(eventSearchBlackListTag)){
					predicates.add(ilike(criteriaBuilder, root.get("blackListTag"), eventSearchBlackListTag));
				}
				
				if(!StringUtils.isBlank(reserved1)){
					predicates.add(ilike(criteriaBuilder, root.get("reserved1"), reserved1));
				}
				
				if(!StringUtils.isBlank(reserved2)){
					predicates.add(ilike(criteriaBuilder, root.get("reserved2"), reserved2));
				}
				
				if(!StringUtils.isBlank(reserved3)){
					predicates.add(ilike(criteriaBuilder, root.get("reserved3"), reserved3));
				}
				
				if(!StringUtils.isBlank(reserved4)){
					predicates.add(ilike(criteriaBuilder, root.get("reserved4"), reserved4));
				}
				
				if(!StringUtils.isBlank(reserved5)){
					predicates.add(ilike(criteriaBuilder, root.get("reserved5"), reserved5));
				}
				
				if(eventTypeIdSearch != null && !eventTypeIdSearch.isEmpty()) {
//					predicates.add(criteriaBuilder.equal(root.get("eventType").get("id"), eventTypeIdSearch));
					predicates.add(root.get("eventType").get("id").in(eventTypeIdSearch));
				}
				
				predicates.add(criteriaBuilder.or(userOrGroupIdList.toArray(new Predicate[0])));
				
				predicates.add(criteriaBuilder.equal(root.get("eventGroup").get("layer").get("id"), currentLayerId));
				
				
				if (hasEventStateViewRole) {
					if(state != null) {//aramadan 
						State objState = StateE.getBooleanState(state);
						predicates.add(criteriaBuilder.equal(root.get("state").get("id"), objState.getId()));
					}else{
						predicates.add( criteriaBuilder.or(criteriaBuilder.equal(root.get("state").get("id"), StateE.FALSE.getValue()), criteriaBuilder.equal(root.get("state").get("id"), StateE.TRUE.getValue())));
					}
				} else {
					predicates.add(criteriaBuilder.equal(root.get("state").get("id"), StateE.TRUE.getValue()));
				}
				
				if(isAlertEvent == true) {
					
					Join<AlertEvent, Event> alertEvent = root.join("alertEvents");
					UserItemDetails sessionUser = ApplicationContextUtils.getUser();
					
					predicates.add(criteriaBuilder.equal(alertEvent.get("user").get("id"), sessionUser.getUserId()));
					predicates.add(criteriaBuilder.equal(alertEvent.get("dbName"), Statics.DEFAULT_DB_NAME));
					
					criteriaQuery.distinct(true);
				}
				
				return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
			}
		};
	}
	
}
