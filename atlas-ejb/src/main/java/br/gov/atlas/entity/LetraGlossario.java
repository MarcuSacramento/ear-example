package br.gov.atlas.entity;

public class LetraGlossario {

	private String letraAtual;
	private String nomeClasseCss;
		
	public LetraGlossario(String letraAtual, String nomeClasseCss) {
		this.letraAtual    = letraAtual;
		this.nomeClasseCss = nomeClasseCss;
	}
		
	public String getLetraAtual() {
		return letraAtual;
	}
	public void setLetraAtual(String letraAtual) {
		this.letraAtual = letraAtual;
	}

	public String getNomeClasseCss() {
		return nomeClasseCss;
	}
	public void setNomeClasseCss(String nomeClasseCss) {
		this.nomeClasseCss = nomeClasseCss;
	}
	
}
