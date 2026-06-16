package com.example.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "menus")
public class Menu {
	@Id
	private String id;
	private int mid;
	private String menu;
	private String micon;

	public String getId() { return id; }
	public void setId(String id) { this.id = id; }
	public int getMid() { return mid; }
	public void setMid(int mid) { this.mid = mid; }
	public String getMenu() { return menu; }
	public void setMenu(String menu) { this.menu = menu; }
	public String getMicon() { return micon; }
	public void setMicon(String micon) { this.micon = micon; }
}
