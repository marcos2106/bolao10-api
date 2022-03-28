package br.com.segmedic.clubflex.model;

import java.util.List;

import com.beust.jcommander.internal.Lists;

public class DashGraphSubs {
	
	private List<String> months;
	private List<String> subs;
	
	public DashGraphSubs() {
		super();
		this.months = Lists.newArrayList();
		this.subs = Lists.newArrayList();
	}
	public List<String> getMonths() {
		return months;
	}
	public void setMonths(List<String> months) {
		this.months = months;
	}
	public List<String> getSubs() {
		return subs;
	}
	public void setSubs(List<String> subs) {
		this.subs = subs;
	}
}
