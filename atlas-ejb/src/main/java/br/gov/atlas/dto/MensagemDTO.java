package br.gov.atlas.dto;

import br.gov.atlas.entidade.Municipio;
import br.gov.atlas.entidade.TipoOrgao;
import br.gov.atlas.entidade.UF;

public class MensagemDTO {
	private String nomeOrgaoPesquisa;
	private Municipio municipio;
	private TipoOrgao tipoOrgao;
	private UF uf;
	
	public String getNomeOrgaoPesquisa() {
		return nomeOrgaoPesquisa;
	}
	public void setNomeOrgaoPesquisa(String nomeOrgaoPesquisa) {
		this.nomeOrgaoPesquisa = nomeOrgaoPesquisa;
	}
	public Municipio getMunicipio() {
		return municipio;
	}
	public void setMunicipio(Municipio municipioCadastro) {
		this.municipio = municipioCadastro;
	}
	public TipoOrgao getTipoOrgao() {
		return tipoOrgao;
	}
	public void setTipoOrgao(TipoOrgao tipoOrgao) {
		this.tipoOrgao = tipoOrgao;
	}
	public UF getUf() {
		return uf;
	}
	public void setUf(UF ufCadastro) {
		this.uf = ufCadastro;
	}
	
}