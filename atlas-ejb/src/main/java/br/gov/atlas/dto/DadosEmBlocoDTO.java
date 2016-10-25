package br.gov.atlas.dto;

import java.util.Date;
import java.util.List;

import br.gov.atlas.entidade.Orgao;
import br.gov.atlas.entidade.Representante;
import br.gov.atlas.entidade.Servico;
import br.gov.atlas.entidade.Tema;
import br.gov.atlas.entidade.TipoOrgao;

public class DadosEmBlocoDTO {

	private List<Orgao> orgaosSelecionados;
	private List<Orgao> orgaosSelecionadosResumo;
	private TipoOrgao tipoOrgao;
	private String email;
	private List<Representante> representantes;
	private List<Tema> temas;
	private List<Servico> servicos;
	private String sigla;
	private String descricao;
	private String homePage;
	private Date dataAtualizacao;
	private OrgaoDTO orgaoFiltro;
	private Orgao orgaoSuperior;
	private String desinencia;

	public List<Orgao> getOrgaosSelecionados() {
		return orgaosSelecionados;
	}
	public void setOrgaosSelecionados(List<Orgao> orgaosSelecionados) {
		this.orgaosSelecionados = orgaosSelecionados;
	}
	public List<Orgao> getOrgaosSelecionadosResumo() {
		return orgaosSelecionadosResumo;
	}
	public void setOrgaosSelecionadosResumo(List<Orgao> orgaosSelecionadosResumo) {
		this.orgaosSelecionadosResumo = orgaosSelecionadosResumo;
	}
	public TipoOrgao getTipoOrgao() {
		return tipoOrgao;
	}
	public void setTipoOrgao(TipoOrgao tipoOrgao) {
		this.tipoOrgao = tipoOrgao;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public List<Representante> getRepresentantes() {
		return representantes;
	}
	public void setRepresentantes(List<Representante> representantes) {
		this.representantes = representantes;
	}
	public List<Tema> getTemas() {
		return temas;
	}
	public void setTemas(List<Tema> temas) {
		this.temas = temas;
	}
	public List<Servico> getServicos() {
		return servicos;
	}
	public void setServicos(List<Servico> servicos) {
		this.servicos = servicos;
	}
	public String getSigla() {
		return sigla;
	}
	public void setSigla(String sigla) {
		this.sigla = sigla;
	}
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	public String getHomePage() {
		return homePage;
	}
	public void setHomePage(String homePage) {
		this.homePage = homePage;
	}
	public Date getDataAtualizacao() {
		return dataAtualizacao;
	}
	public void setDataAtualizacao(Date dataAtualizacao) {
		this.dataAtualizacao = dataAtualizacao;
	}
	public OrgaoDTO getOrgaoFiltro() {
		return orgaoFiltro;
	}
	public void setOrgaoFiltro(OrgaoDTO orgaoFiltro) {
		this.orgaoFiltro = orgaoFiltro;
	}
	public Orgao getOrgaoSuperior() {
		return orgaoSuperior;
	}
	public void setOrgaoSuperior(Orgao orgaoSuperior) {
		this.orgaoSuperior = orgaoSuperior;
	}
	public boolean isAlteracaoPorSelecao(){
		return orgaosSelecionados != null && !orgaosSelecionados.isEmpty();
	}
	public boolean isAlteracaoPorFiltro(){
		return orgaoFiltro != null;
	}
	public String getDesinencia() {
		return desinencia;
	}
	public void setDesinencia(String desinencia) {
		this.desinencia = desinencia;
	}
}