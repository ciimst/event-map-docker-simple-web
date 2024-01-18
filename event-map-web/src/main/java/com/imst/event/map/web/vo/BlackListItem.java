package com.imst.event.map.web.vo;

import java.util.Date;

import javax.persistence.Column;

import com.imst.event.map.hibernate.entity.ActionState;
import com.imst.event.map.hibernate.entity.BlackList;
import com.imst.event.map.web.constant.StateE;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class BlackListItem {

	private Integer id;
	private String name;
	private String tag;
	private String createUser;
	@Column(name = "eventType.id")
	private Integer eventTypeId;
	private Date createDate;
	private Date updateDate;
	
	@Column(name = "eventGroup.name")
	private String eventGroupName;
	@Column(name = "eventGroup.id")
	private Integer eventGroupId;
	@Column(name = "layer.name")
	private String layerName;
	@Column(name = "layer.id")
	private Integer layerId;
	
	@Column(name = "eventType.name")
	private String eventTypeName;
	
	@Column(name = "state.id")
	private Integer stateId;
	
	@Column(name="layer.isTemp")
	private Boolean state;
	
	@Column(name = "actionState.state_type")
	private String actionStateType;
	@Column(name="actionState.id")
	private Integer actionStateId;
	
    public BlackListItem() {
		
	}
	
	public BlackListItem(Integer id,String name,String tag,String createUser,Date createDate,Date updateDate,Integer stateId,Integer layerId,String layerName,Integer eventGroupId, Integer eventTypeId, Integer actionStateId, String actionStateType) {
		this.id = id;
		this.name = name;
		this.tag = tag;
		this.createUser = createUser;
		this.createDate = createDate;
		this.updateDate = updateDate;
		this.stateId = stateId;
		this.state = StateE.getIntegerStateToBoolean(stateId);
		this.layerId = layerId;
		this.layerName = layerName;
		this.eventGroupId = eventGroupId != null ? eventGroupId : null;
		this.eventTypeId = eventTypeId!= null ? eventTypeId : null;
		this.actionStateId = actionStateId;
		this.actionStateType = actionStateType;
     }
	
	public BlackListItem(BlackList blackList) {
		this.id = blackList.getId();
		this.name = blackList.getName();
		this.tag =  blackList.getTag();
		this.createUser = blackList.getCreateUser();
		this.createDate = blackList.getCreateDate();
		this.updateDate = blackList.getUpdateDate();
		this.stateId = blackList.getState().getId();
		this.state = StateE.getIntegerStateToBoolean(blackList.getState().getId());
		this.layerId = blackList.getLayer().getId();
		this.layerName = blackList.getLayer().getName();
		this.eventGroupId = blackList.getEventGroup() != null ? blackList.getEventGroup().getId() : null;
		this.eventGroupName = blackList.getEventGroup() != null ? blackList.getEventGroup().getName() : null;
		this.eventTypeId = blackList.getEventType() != null ? blackList.getEventType().getId() : null;
		this.eventTypeName = blackList.getEventType() != null ? blackList.getEventType().getName() : null;
		this.actionStateId = blackList.getActionState().getId();
		this.actionStateType = blackList.getActionState().getStateType();
	}
	
	public static BlackListItem newInstanceForLog(BlackList blackList) {
		BlackListItem blackListItem = new BlackListItem(blackList);
		return blackListItem;
	}
	
}
