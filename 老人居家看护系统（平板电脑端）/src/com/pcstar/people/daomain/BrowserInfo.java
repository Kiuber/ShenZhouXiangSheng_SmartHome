package com.pcstar.people.daomain;

/**
 * 浏览器信息实体类
 * @author ming
 *
 */
public class BrowserInfo {

	public String type;
	public String name;
	public String order;
	
	
	public BrowserInfo() {
		super();
		// TODO Auto-generated constructor stub
	}
	public BrowserInfo(String type, String name, String order) {
		super();
		this.type = type;
		this.name = name;
		this.order = order;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getOrder() {
		return order;
	}
	public void setOrder(String order) {
		this.order = order;
	}
	@Override
	public String toString() {
		return "BrowserInfo [type=" + type + ", name=" + name + ", order="
				+ order + "]";
	}
	
}
