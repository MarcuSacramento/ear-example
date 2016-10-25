package br.gov.atlas.entity;

import br.gov.atlas.entidade.Parceiro;

public class ParceiroLinhaComTresRegistrosAdapter {
	
	private Parceiro registroEsquerda;
	private Parceiro registroCentro;
	private Parceiro registroDireita;
		
	public ParceiroLinhaComTresRegistrosAdapter() {
		this.registroEsquerda = new Parceiro();
		this.registroCentro   = new Parceiro();
		this.registroDireita  = new Parceiro();
	}
	
	public boolean getIsRegistroCentroNotEmpty() {
		return !isRegistroCentroEmpty();
	}

	private boolean isRegistroCentroEmpty() {
		return this.registroCentro.equals(new Parceiro());
	}
	
	public boolean getIsRegistroDireitaNotEmpty() {
		return !isRegistroDireitaEmpty();
	}

	private boolean isRegistroDireitaEmpty() {
		return this.registroDireita.equals(new Parceiro());
	}
	
	public Parceiro getRegistroEsquerda() {
		return registroEsquerda;
	}
	public void setRegistroEsquerda(Parceiro registroEsquerda) {
		this.registroEsquerda = registroEsquerda;
	}
	
	public Parceiro getRegistroCentro() {
		return registroCentro;
	}
	public void setRegistroCentro(Parceiro registroCentro) {
		this.registroCentro = registroCentro;
	}
	
	public Parceiro getRegistroDireita() {
		return registroDireita;
	}
	public void setRegistroDireita(Parceiro registroDireita) {
		this.registroDireita = registroDireita;
	}

}
