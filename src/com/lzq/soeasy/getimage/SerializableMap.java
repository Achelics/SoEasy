package com.lzq.soeasy.getimage;

import java.io.Serializable;
import java.util.Map;

public class SerializableMap implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Map<Integer, Integer> map;
	public Map<Integer, Integer> getMap() {
		return map;
	}
	public void setMap(Map<Integer, Integer> imageIndex){
		this.map=imageIndex;
	}
}
