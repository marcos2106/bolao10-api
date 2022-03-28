package br.com.segmedic.clubflex.model;

import java.io.Serializable;
import java.util.List;

public class UserByPlanReport implements Serializable {
	
	private static final long serialVersionUID = -8978906246818002847L;
	
	private List<UserByPlanReportPlan> header;
	private List<UserByPlanReportItem> itens;
	private List<UserByPlanReportPlan> footer;
	
	public List<UserByPlanReportPlan> getHeader() {
		return header;
	}
	public void setHeader(List<UserByPlanReportPlan> header) {
		this.header = header;
	}
	public List<UserByPlanReportItem> getItens() {
		return itens;
	}
	public void setItens(List<UserByPlanReportItem> itens) {
		this.itens = itens;
	}
	public List<UserByPlanReportPlan> getFooter() {
		return footer;
	}
	public void setFooter(List<UserByPlanReportPlan> footer) {
		this.footer = footer;
	}
}

