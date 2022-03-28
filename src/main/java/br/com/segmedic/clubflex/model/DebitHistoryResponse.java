package br.com.segmedic.clubflex.model;

import java.io.Serializable;
import java.util.List;

import com.google.common.collect.Lists;

public class DebitHistoryResponse implements Serializable{
	
	private static final long serialVersionUID = -9181702034124193321L;

	private List<String> months;
	private List<Integer> values;
	
	public DebitHistoryResponse() {
		super();
		this.months = Lists.newArrayList();
		this.values = Lists.newArrayList();
	}
	
	public List<String> getMonths() {
		return months;
	}
	public List<Integer> getValues() {
		return values;
	}
}
