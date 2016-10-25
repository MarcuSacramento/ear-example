package br.gov.atlas.entity;

import br.gov.atlas.entidade.Cartilha;

public class CartilhaLinhaComTresRegistrosAdapter {

	private Cartilha registroAEsquerda;
	private Cartilha registroADireita;
	private Cartilha registroCentro;
	
	public CartilhaLinhaComTresRegistrosAdapter() {
		this.registroAEsquerda = new Cartilha();
		this.registroADireita  = new Cartilha();
		this.registroCentro  = new Cartilha();
	}
	
	public Cartilha getRegistroCentro() {
		return registroCentro;
	}

	public void setRegistroCentro(Cartilha registroCentro) {
		this.registroCentro = registroCentro;
	}

	public Cartilha getRegistroAEsquerda() {
		return registroAEsquerda;
	}
	
	public void setRegistroAEsquerda(Cartilha registroAEsquerda) {
		this.registroAEsquerda = registroAEsquerda;
	}
	
	public Cartilha getRegistroADireita() {
		return registroADireita;
	}
	
	public void setRegistroADireita(Cartilha registroADireita) {
		this.registroADireita = registroADireita;
	}
	
	public boolean getIsRegistroADireitaNotEmpty() {
		return !isRegistroADireitaEmpty();
	}
	
	private boolean isRegistroADireitaEmpty() {
		return this.registroADireita.equals( new Cartilha() );
	}
	
	
}
// 