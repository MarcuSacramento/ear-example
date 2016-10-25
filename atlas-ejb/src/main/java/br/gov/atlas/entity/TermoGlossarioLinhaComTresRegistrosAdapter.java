package br.gov.atlas.entity;

import br.gov.atlas.entidade.TermoGlossario;

public class TermoGlossarioLinhaComTresRegistrosAdapter {
	
	private TermoGlossario registroEsquerda;
	private TermoGlossario registroCentro;
	private TermoGlossario registroDireita;
		
	public TermoGlossarioLinhaComTresRegistrosAdapter() {
		this.registroEsquerda = new TermoGlossario();
		this.registroCentro   = new TermoGlossario();
		this.registroDireita  = new TermoGlossario();
	}
	
	public boolean getIsRegistroDireitaNotEmpty() {
		return !isRegistroDireitaEmpty();
	}

	private boolean isRegistroDireitaEmpty() {
		return this.registroDireita.equals(new TermoGlossario());
	}
	
	public TermoGlossario getRegistroEsquerda() {
		return registroEsquerda;
	}
	public void setRegistroEsquerda(TermoGlossario registroEsquerda) {
		this.registroEsquerda = registroEsquerda;
	}
	
	public TermoGlossario getRegistroCentro() {
		return registroCentro;
	}
	public void setRegistroCentro(TermoGlossario registroCentro) {
		this.registroCentro = registroCentro;
	}
	
	public TermoGlossario getRegistroDireita() {
		return registroDireita;
	}
	public void setRegistroDireita(TermoGlossario registroDireita) {
		this.registroDireita = registroDireita;
	}
}
