package com.imst.event.map.web.constant;


import java.util.HashMap;

import com.imst.event.map.hibernate.entity.State;

public enum StateE {
	
	FALSE(1),//column id 
	TRUE(2), 
	BLACKLISTED(3),
	DELETED(4),
	;
	
	private Integer value; 
	
	private StateE(Integer value) {
		this.value = value;
	}
	

	private static HashMap<Integer, StateE> map = new HashMap<>();
	
	public Integer getValue() {
		return value;
	}
	
	public static StateE getSettings(Integer key) {
		return map.get(key) == null ? null : map.get(key);
	}
	
	static {
		
		for (StateE settingsE : StateE.values()) {
			map.put(settingsE.getValue(), settingsE);
		}
	}
	
	public State getState() {
		
		State state = new State();
		state.setId(getValue());
		return state;
	}
	
	public static Boolean getIntegerStateToBoolean(Integer inState) {
		
		Boolean state = inState != null && inState.equals(StateE.TRUE.getValue()) ?  true : false;
		return state;
	}

	public static State getBooleanState(Boolean boolState) {
		
		Integer id = boolState != null && boolState ? StateE.TRUE.getValue() : StateE.FALSE.getValue();
		State state = new State();
		state.setId(id);
		
		return state;
	}
	
	public static State getToggleStateChange(Integer intState) {//gelenin tersi
		
		Integer resultState = intState != null &&  StateE.FALSE.getValue().equals(intState) ?  StateE.TRUE.getValue() : StateE.FALSE.getValue();	
		State state = new State();
		state.setId(resultState);	
		return state;
		
	}
	
	
	public static State getBatchOperationStateChange(Boolean intState) {
		
		Integer resultState = intState != null && intState ?  StateE.TRUE.getValue() : StateE.FALSE.getValue();		
		
		State state = new State();
		state.setId(resultState);	
		return state;
		
	}
	
	public static State getIntegerToBoolean(Integer intState) {
		
		Integer resultState = intState.equals(StateE.FALSE.getValue())	? StateE.FALSE.getValue() : StateE.TRUE.getValue();
		
		State state = new State();
		state.setId(resultState);	
		return state;
	}
	
	
}
