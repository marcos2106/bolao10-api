package br.com.segmedic.clubflex.model;

import java.io.Serializable;
import java.util.List;

public class UserByPlanReportItem implements Serializable{

	private static final long serialVersionUID = 6604159759695577902L;
	
	private Long userId;
	private String userName;
	private List<UserByPlanReportHorizontal> horizontalData;
	
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public List<UserByPlanReportHorizontal> getHorizontalData() {
		return horizontalData;
	}
	public void setHorizontalData(List<UserByPlanReportHorizontal> horizontalData) {
		this.horizontalData = horizontalData;
	}
	public Integer getHorizontalDataTotal() {
		if(this.horizontalData != null) {
			Integer total = 0;
			for (UserByPlanReportHorizontal horizontal : horizontalData) {
				total+=horizontal.getQuantity();
			}
			return total;
		}
		return 0;
	}
}
