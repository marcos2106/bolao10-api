package br.com.segmedic.clubflex.model;

import java.io.Serializable;
import java.util.List;

import br.com.segmedic.clubflex.domain.ClubCard;
import br.com.segmedic.clubflex.domain.Holder;

public class LifeGroup implements Serializable {
	
	private static final long serialVersionUID = 8647529355737206844L;

	private Holder holder;
	private List<ClubCard> cards;
	
	public LifeGroup() {
		super();
	}
	
	public LifeGroup(Holder holder, List<ClubCard> cards) {
		super();
		this.holder = holder;
		this.cards = cards;
	}
	
	public Holder getHolder() {
		return holder;
	}
	public void setHolder(Holder holder) {
		this.holder = holder;
	}
	public List<ClubCard> getCards() {
		return cards;
	}
	public void setCards(List<ClubCard> cards) {
		this.cards = cards;
	}
}
