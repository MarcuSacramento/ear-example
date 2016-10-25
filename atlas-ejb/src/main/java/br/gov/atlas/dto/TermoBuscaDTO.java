/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gov.atlas.dto;

/**
 *
 * @author edle
 */
public class TermoBuscaDTO {

	private String codUf;
	private String nmeMunicipio;
	private String nmeTermoBusca;
	private boolean sitLocalizado;
	private long qtdTotal;

	public TermoBuscaDTO() {
		super();
	}

	public TermoBuscaDTO(String nmeTermoBusca,	long qtdTotal) {
		this();
		this.nmeTermoBusca = nmeTermoBusca;
		this.qtdTotal = qtdTotal;
	}

	public String getCodUf() {
		return codUf;
	}

	public void setCodUf(String codUf) {
		this.codUf = codUf;
	}

	public String getNmeMunicipio() {
		return nmeMunicipio;
	}

	public void setNmeMunicipio(String nmeMunicipio) {
		this.nmeMunicipio = nmeMunicipio;
	}

	public String getNmeTermoBusca() {
		return nmeTermoBusca;
	}

	public void setNmeTermoBusca(String nmeTermoBusca) {
		this.nmeTermoBusca = nmeTermoBusca;
	}

	public boolean isSitLocalizado() {
		return sitLocalizado;
	}

	public void setSitLocalizado(boolean sitLocalizado) {
		this.sitLocalizado = sitLocalizado;
	}

	public long getQtdTotal() {
		return qtdTotal;
	}

	public void setQtdTotal(long qtdTotal) {
		this.qtdTotal = qtdTotal;
	}

}
