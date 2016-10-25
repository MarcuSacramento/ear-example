package br.gov.atlas.dto;

import java.math.BigInteger;

import br.gov.atlas.entidade.TipoOrgao;
import br.gov.atlas.entidade.UF;


public class TipoOrgaoProfissaoDTO {
	
	private int ano;
	private String tipo;
	private ProfissaoDTO profissao;
	private UF uf;
	private BigInteger quantidade;	
	
	public TipoOrgaoProfissaoDTO(int ano, String tipo, ProfissaoDTO profissao, UF uf, BigInteger quantidade) {
		this.ano =  ano;
		this.tipo = tipo;
		this.profissao = profissao;
		this.uf = uf;
		this.quantidade = quantidade;
	}
	
	 public int getAno() {
		return ano;
	}



	public void setAno(int ano) {
		this.ano = ano;
	}



	public String getTipo() {
		return tipo;
	}



	public void setTipo(String tipo) {
		this.tipo = tipo;
	}



	public ProfissaoDTO getProfissao() {
		return profissao;
	}



	public void setProfissao(ProfissaoDTO profissao) {
		this.profissao = profissao;
	}



	public UF getUf() {
		return uf;
	}



	public void setUf(UF uf) {
		this.uf = uf;
	}



	public BigInteger getQuantidade() {
		return quantidade;
	}



	public void setQuantidade(BigInteger quantidade) {
		this.quantidade = quantidade;
	}
	
}
