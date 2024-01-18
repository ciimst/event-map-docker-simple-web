package com.imst.event.map.web.cron;


import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.imst.event.map.hibernate.entity.EventsTimeCount;
import com.imst.event.map.hibernate.entity.Event;
import com.imst.event.map.hibernate.entity.EventGroup;
import com.imst.event.map.hibernate.entity.EventType;
import com.imst.event.map.hibernate.entity.Layer;
import com.imst.event.map.hibernate.entity.Settings;
import com.imst.event.map.web.constant.StateE;
import com.imst.event.map.web.constant.Statics;
import com.imst.event.map.web.db.projections.EventsTimeCountProjection;
import com.imst.event.map.web.db.repositories.EventsTimeCountRepository;
import com.imst.event.map.web.db.repositories.EventTypeRepository;
import com.imst.event.map.web.db.repositories.LayerRepository;
import com.imst.event.map.web.db.repositories.SettingsRepository;
import com.imst.event.map.web.services.EventService;
import com.imst.event.map.web.utils.SettingsUtil;

import lombok.extern.log4j.Log4j2;

@Component
@Log4j2
public class ScheduledQueries {
	
	@Autowired
	private SettingsRepository settingsRepository;
	
	@Autowired
	private EventService eventService;
	
	@Autowired EventTypeRepository eventTypeRepository;
	
	@Autowired 
	private EventsTimeCountRepository eventsTimeCountRepository;
	
	@Autowired private LayerRepository layerRepository;
	
	@PostConstruct
	public void initialize() {
		generalSettings();
		eventTypeListUpdater();
	}
	
	@Scheduled(initialDelayString="${settings.update.initial.delay}", fixedDelayString ="${settings.update.interval}")
	public void generalSettings() {
		
		try {
			
			List<Settings> all = settingsRepository.findAll();
			for (Settings settings : all) {
				
				SettingsUtil.settings.put(settings.getSettingsKey(), settings.getSettingsValue());
			}
		}
		catch (Exception e) {
			
			log.debug(e);
		}
		
	}
	
	
	
	@Scheduled(initialDelayString="${count.events.time.update.initial.delay}", fixedDelayString ="${count.events.time.update.interval}")
	public void setEventCount() {
		
		try {
			
			List<Layer> layerList = layerRepository.findAll();
			
			log.info(String.format("EventsTimeCount scheduled job started"));
			
			for(Layer layer : layerList) {
				
				
				
				List<EventsTimeCount> saveList = new ArrayList<>();
				Integer layerId = layer.getId();
				
				List<EventsTimeCountProjection> layerUnderEventCountlist = eventsTimeCountRepository.findAllByLayerId(layerId);
				
				List<Object[]> list = eventsTimeCountRepository.eventCountsInDateTimes(StateE.TRUE.getValue(), layerId);
				
				
				for (Object[] ob : list){
					
					
					Date date = (Date)ob[0];			  
				    Integer eventGroupId = (Integer)ob[1];
				    Long count = (Long)ob[3];				    
				    
				    Calendar calendar = Calendar.getInstance();
				    calendar.setTime(date);
				    Integer day = calendar.get(Calendar.DAY_OF_MONTH);
				    Integer month = calendar.get(Calendar.MONTH) +1;
				    Integer year = calendar.get(Calendar.YEAR);

				    EventGroup eventGroup = new EventGroup();
				    eventGroup.setId(eventGroupId);
				    
				    
				    EventsTimeCount countEventsTime = new EventsTimeCount();
				    countEventsTime.setEventCount(count);		   
				    countEventsTime.setEventGroup(eventGroup);
				    countEventsTime.setLayer(layer);
				    countEventsTime.setEventDay(day);
				    countEventsTime.setEventMonth(month);
				    countEventsTime.setEventYear(year);
				    
				    Optional<EventsTimeCountProjection> opt = layerUnderEventCountlist.stream().filter(f -> f.getEventDay().equals(day) && f.getEventMonth().equals(month) && f.getEventYear().equals(year) && f.getEventGroupId().equals(eventGroupId)).findAny();
				    if(opt.isPresent()) {//add
				    	
				    	countEventsTime.setId(opt.get().getId());
				    }
				    
				
				    saveList.add(countEventsTime);				    
				  
				}
				
				//Olayların state bilgisi BlackList veya silinmişse eğer count değerinin sıfır olarak güncellenmesi gerekmektedir.
				for(EventsTimeCountProjection item : layerUnderEventCountlist) {
					
					 Boolean isThere = saveList.stream().anyMatch(f -> f.getEventDay().equals(item.getEventDay()) && f.getEventGroup().getId().equals(item.getEventGroupId())
					    		&& f.getEventMonth().equals(item.getEventMonth()) && f.getEventYear().equals(item.getEventYear()));
					    
					    if(!isThere) {
					    	EventGroup eventGroup = new EventGroup();
					    	eventGroup.setId(item.getEventGroupId());
					    	EventsTimeCount countEventsTime = new EventsTimeCount();
					    	countEventsTime.setId(item.getId());
						    countEventsTime.setEventCount(0L);		   
						    countEventsTime.setEventGroup(eventGroup);
						    countEventsTime.setLayer(layer);
						    countEventsTime.setEventDay(item.getEventDay());
						    countEventsTime.setEventMonth(item.getEventMonth());
						    countEventsTime.setEventYear(item.getEventYear());
						    saveList.add(countEventsTime);				
					    	
					    }
					
				}
			    
			   
			    
				
				eventsTimeCountRepository.saveAll(saveList);
			}
			
			log.info(String.format("EventsTimeCount scheduled job finished"));
		}
		catch (Exception e) {			
			log.debug(e);
		}
		
	}
	
	@Scheduled(initialDelay = 1000, fixedDelay = 1000*60*60) // 1 saat
	public void timeLineStartDateUpdater() {
		
		try {
			
			Event oldestEvent = eventService.getOldestEvent();
			Timestamp oldestDate = oldestEvent.getEventDate();
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(oldestDate.getTime());
			calendar.set(Calendar.MONTH, 0);
			calendar.set(Calendar.DAY_OF_MONTH, 1);
					
			Statics.timeLineStartDate = calendar.getTime();
		}
		catch (Exception e) {
			
			log.debug(e);
		}
		
	}
	
	@Scheduled(initialDelayString="${count.event.type.update.initial.delay}", fixedDelayString ="${count.event.type.update.interval}")
//	@Scheduled(initialDelay = 1000, fixedDelay = 1000*60*60) // 1 saat
	public void eventTypeListUpdater() {
		
		try {
			
			List<EventType> eventTypeAll = eventTypeRepository.findAll();
			Statics.eventTypeList = eventTypeAll;
		}
		catch (Exception e) {
			
			log.debug(e);
		}
		
	}
	
	
}
