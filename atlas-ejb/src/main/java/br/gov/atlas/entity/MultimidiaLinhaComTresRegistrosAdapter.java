package br.gov.atlas.entity;

import br.gov.atlas.entidade.Multimidia;

public class MultimidiaLinhaComTresRegistrosAdapter {

	private Multimidia registroAEsquerda;
	private Multimidia registroADireita;
	private Multimidia registroCentro;

	public MultimidiaLinhaComTresRegistrosAdapter() {
		this.registroAEsquerda = new Multimidia();
		this.registroADireita = new Multimidia();
		this.registroCentro = new Multimidia();
	}
	
	public boolean getIsRegistroADireitaNotEmpty() {
		return !isRegistroADireitaEmpty();
	}

	private boolean isRegistroADireitaEmpty() {
		return this.registroADireita.equals(new Multimidia());
	}

	public Multimidia getRegistroAEsquerda() {
		return registroAEsquerda;
	}

	public void setRegistroAEsquerda(Multimidia registroAEsquerda) {
		this.registroAEsquerda = registroAEsquerda;
	}

	public Multimidia getRegistroADireita() {
		return registroADireita;
	}

	public void setRegistroADireita(Multimidia registroADireita) {
		this.registroADireita = registroADireita;
	}

	public Multimidia getRegistroCentro() {
		return registroCentro;
	}

	public void setRegistroCentro(Multimidia registroCentro) {
		this.registroCentro = registroCentro;
	}
}
