package br.com.segmedic.clubflex.model;

import java.io.Serializable;
import java.util.List;

public class ProductivityReport implements Serializable {

	private static final long serialVersionUID = 4950969378073747297L;
	
	private List<ProductivityReportObject> companies;
	private List<ProductivityReportObject> users;
	private List<ProductivityReportObject> brokers;
	private List<ProductivityReportObject> plans;
	
	public List<ProductivityReportObject> getCompanies() {
		return companies;
	}
	public void setCompanies(List<ProductivityReportObject> companies) {
		this.companies = companies;
	}
	public List<ProductivityReportObject> getUsers() {
		return users;
	}
	public void setUsers(List<ProductivityReportObject> users) {
		this.users = users;
	}
	public List<ProductivityReportObject> getBrokers() {
		return brokers;
	}
	public void setBrokers(List<ProductivityReportObject> brokers) {
		this.brokers = brokers;
	}
	public List<ProductivityReportObject> getPlans() {
		return plans;
	}
	public void setPlans(List<ProductivityReportObject> plans) {
		this.plans = plans;
	}
}
