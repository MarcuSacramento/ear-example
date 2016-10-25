package br.gov.atlas.dto;

import java.util.List;

import br.gov.atlas.entidade.AtlasEntidade;
import br.gov.atlas.entidade.Municipio;
import br.gov.atlas.entidade.Ramo;
import br.gov.atlas.entidade.Servico;
import br.gov.atlas.entidade.Tema;
import br.gov.atlas.entidade.TipoOrgao;
import br.gov.atlas.entidade.UF;
import br.gov.atlas.enums.AmbitoAtuacaoOrgao;
import br.gov.atlas.enums.TipoInformacaoCoordenada;
import br.gov.atlas.enums.TipoInformacaoErro;
import br.gov.atlas.enums.TipoSituacaoRegistroOrgao;

@SuppressWarnings("serial")
public class OrgaoDTO extends AtlasEntidade {

	private Ramo ramo;

	private TipoOrgao tipoOrgao;

	private AmbitoAtuacaoOrgao ambitoAtuacaoOrgao;

	private Tema tema;

	private Servico servico;

	private Municipio municipio;

	private String nome;

	private UF uf;

	private Boolean orgaosComErro;

	private Boolean orgaosComLatitudeLongitude;

	private String  orgaosSituacaoRegistro;

	private int qtdTipoOrgao;

	private List<OrgaoDTO> orgaos;

	private Integer id;

	private Integer qtdRegistros;

	private TipoInformacaoCoordenada tipoInfCoordenada;

	private TipoSituacaoRegistroOrgao tipoSituacaoRegistro;

	private TipoInformacaoErro tipoInfErro;

	public OrgaoDTO()
	{
		setPrimeiroRegistro(0);
	}

	@Override
	public Integer getId() {
		return id;
	}

	@Override
	public void setId(Integer id) {
		this.id = id;
	}

	@Override
	public String getLabel() {
		return null;
	}

	public Ramo getRamo() {
		return ramo;
	}

	public void setRamo(Ramo ramo) {
		this.ramo = ramo;
	}

	public TipoOrgao getTipoOrgao() {
		return tipoOrgao;
	}

	public void setTipoOrgao(TipoOrgao tipoOrgao) {
		this.tipoOrgao = tipoOrgao;
	}

	public Municipio getMunicipio() {
		return municipio;
	}

	public void setMunicipio(Municipio municipio) {
		this.municipio = municipio;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public UF getUf() {
		return uf;
	}

	public void setUf(UF uf) {
		this.uf = uf;
	}

	public int getQtdTipoOrgao() {
		return qtdTipoOrgao;
	}



	public void setQtdTipoOrgao(int qtdTipoOrgao) {
		this.qtdTipoOrgao = qtdTipoOrgao;
	}

	public String getOrgaosComErroDescricao() {
		if(orgaosComErro != null){
			if(orgaosComErro.booleanValue()){
				return TipoInformacaoErro.COM_ERRO.getDescricao();
			}else{
				return TipoInformacaoErro.SEM_ERRO.getDescricao();
			}
		}
		return TipoInformacaoErro.AMBOS.getDescricao();
	}

	public Boolean getOrgaosComErro() {
		return orgaosComErro;
	}

	public void setOrgaosComErro(Boolean orgaosComErro) {
		this.orgaosComErro = orgaosComErro;
	}

	public String getOrgaosComLatitudeLongitudeDescricao() {
		if(orgaosComLatitudeLongitude != null){
			if(orgaosComLatitudeLongitude.booleanValue()){
				return TipoInformacaoCoordenada.COM_INFORMACAO.getDescricao();
			}else{
				return TipoInformacaoCoordenada.SEM_INFORMACAO.getDescricao();
			}
		}
		return TipoInformacaoCoordenada.AMBOS.getDescricao();
	}

	public Boolean getOrgaosComLatitudeLongitude() {
		return orgaosComLatitudeLongitude;
	}

	public void setOrgaosComLatitudeLongitude(Boolean orgaosComLatitudeLongitude) {
		this.orgaosComLatitudeLongitude = orgaosComLatitudeLongitude;
	}

	public String getOrgaosSituacaoRegistroDescricao() {
		if(orgaosSituacaoRegistro != null){
			if(orgaosSituacaoRegistro.equals(TipoSituacaoRegistroOrgao.DISPONIVEL.indiceSituacao())){
				return TipoSituacaoRegistroOrgao.DISPONIVEL.descricao();
			}
			if(orgaosSituacaoRegistro.equals(TipoSituacaoRegistroOrgao.EM_ANALISE.indiceSituacao())){
				return TipoSituacaoRegistroOrgao.EM_ANALISE.descricao();
			}
			if(orgaosSituacaoRegistro.equals(TipoSituacaoRegistroOrgao.PROPOSTO.indiceSituacao())){
				return TipoSituacaoRegistroOrgao.PROPOSTO.descricao();
			}
			if(orgaosSituacaoRegistro.equals(TipoSituacaoRegistroOrgao.NAO_EH_PORTA_DA_JUSTICA.indiceSituacao())){
				return TipoSituacaoRegistroOrgao.NAO_EH_PORTA_DA_JUSTICA.descricao();
			}
		}
		return orgaosSituacaoRegistro;
	}

	public String getOrgaosSituacaoRegistro() {
		return orgaosSituacaoRegistro;
	}

	public void setOrgaosSituacaoRegistro(String orgaosSituacaoRegistro) {
		this.orgaosSituacaoRegistro = orgaosSituacaoRegistro;
	}

	public Tema getTema() {
		return tema;
	}

	public void setTema(Tema tema) {
		this.tema = tema;
	}

	public Servico getServico() {
		return servico;
	}

	public void setServico(Servico servico) {
		this.servico = servico;
	}

	public List<OrgaoDTO> getOrgaos() {
		return orgaos;
	}

	public void setOrgaos(List<OrgaoDTO> orgaos) {
		this.orgaos = orgaos;
	}



	public Integer getQtdRegistros() {
		return qtdRegistros;
	}



	public void setQtdRegistros(Integer qtdRegistros) {
		this.qtdRegistros = qtdRegistros;
	}



	public TipoInformacaoCoordenada getTipoInfCoordenada() {
		return tipoInfCoordenada;
	}



	public void setTipoInfCoordenada(TipoInformacaoCoordenada tipoInfCoordenada) {
		this.tipoInfCoordenada = tipoInfCoordenada;
	}



	public TipoSituacaoRegistroOrgao getTipoSituacaoRegistro() {
		return tipoSituacaoRegistro;
	}



	public void setTipoSituacaoRegistro(TipoSituacaoRegistroOrgao tipoSituacaoRegistro) {
		this.tipoSituacaoRegistro = tipoSituacaoRegistro;
	}



	public TipoInformacaoErro getTipoInfErro() {
		return tipoInfErro;
	}



	public void setTipoInfErro(TipoInformacaoErro tipoInfErro) {
		this.tipoInfErro = tipoInfErro;
	}

	public AmbitoAtuacaoOrgao getAmbitoAtuacaoOrgao() {
		return ambitoAtuacaoOrgao;
	}

	public void setAmbitoAtuacaoOrgao(AmbitoAtuacaoOrgao ambitoAtuacaoOrgao) {
		this.ambitoAtuacaoOrgao = ambitoAtuacaoOrgao;
	}

}