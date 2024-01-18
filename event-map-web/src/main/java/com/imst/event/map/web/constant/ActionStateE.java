package com.imst.event.map.web.constant;


import java.util.HashMap;

import com.imst.event.map.hibernate.entity.ActionState;

public enum ActionStateE {
	
	PENDING(1),//column id 
	RUNNING(2), 
	FINISHED(3),
	;
	
	private Integer value; 
	
	private ActionStateE(Integer value) {
		this.value = value;
	}
	

	private static HashMap<Integer, ActionStateE> map = new HashMap<>();
	
	public Integer getValue() {
		return value;
	}
	
	public static ActionStateE getSettings(Integer key) {
		return map.get(key) == null ? null : map.get(key);
	}
	
	static {
		
		for (ActionStateE settingsE : ActionStateE.values()) {
			map.put(settingsE.getValue(), settingsE);
		}
	}
	
	public ActionState getActionState() {
		
		ActionState state = new ActionState();
		state.setId(getValue());
		return state;
	}

	
}
