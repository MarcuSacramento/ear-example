package br.gov.atlas.entity;

public class LinksPaginacao {

	private String paginaAlvo;
	private String nomeClasseCss;
		
	public LinksPaginacao(String paginaAlvo, String nomeClasseCss) {
		this.paginaAlvo = paginaAlvo;
		this.nomeClasseCss = nomeClasseCss;
	}
		
	public String getPaginaAlvo() {
		return paginaAlvo;
	}
	
	public void setPaginaAlvo(String paginaAlvo) {
		this.paginaAlvo = paginaAlvo;
	}

	public String getNomeClasseCss() {
		return nomeClasseCss;
	}
	
	public void setNomeClasseCss(String nomeClasseCss) {
		this.nomeClasseCss = nomeClasseCss;
	}
	
}
