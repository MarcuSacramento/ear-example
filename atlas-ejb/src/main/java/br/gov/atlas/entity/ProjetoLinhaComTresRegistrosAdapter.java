package br.gov.atlas.entity;

import br.gov.atlas.entidade.Projeto;

public class ProjetoLinhaComTresRegistrosAdapter {
	
	private Projeto registroEsquerda;
	private Projeto registroCentro;
	private Projeto registroDireita;
		
	public ProjetoLinhaComTresRegistrosAdapter() {
		this.registroEsquerda = new Projeto();
		this.registroCentro   = new Projeto();
		this.registroDireita  = new Projeto();
	}
	
	public boolean getIsRegistroCentroNotEmpty() {
		return !isRegistroCentroEmpty();
	}

	private boolean isRegistroCentroEmpty() {
		return this.registroCentro.equals(new Projeto());
	}
	
	public boolean getIsRegistroDireitaNotEmpty() {
		return !isRegistroDireitaEmpty();
	}

	private boolean isRegistroDireitaEmpty() {
		return this.registroDireita.equals(new Projeto());
	}
	
	public Projeto getRegistroEsquerda() {
		return registroEsquerda;
	}
	public void setRegistroEsquerda(Projeto registroEsquerda) {
		this.registroEsquerda = registroEsquerda;
	}
	
	public Projeto getRegistroCentro() {
		return registroCentro;
	}
	public void setRegistroCentro(Projeto registroCentro) {
		this.registroCentro = registroCentro;
	}
	
	public Projeto getRegistroDireita() {
		return registroDireita;
	}
	public void setRegistroDireita(Projeto registroDireita) {
		this.registroDireita = registroDireita;
	}

}
