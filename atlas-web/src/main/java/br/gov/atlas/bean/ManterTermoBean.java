package br.gov.atlas.bean;


import java.util.Date;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import br.gov.atlas.component.model.AtlasLazyDataModel;
import br.gov.atlas.dto.TermoBuscaDTO;
import br.gov.atlas.entidade.Termo;
import br.gov.atlas.exception.AtlasAcessoBancoDadosException;
import br.gov.atlas.exception.AtlasNegocioException;
import br.gov.atlas.servico.AtlasServicoImpl;
import br.gov.atlas.servico.ManterTermoServico;
import br.gov.atlas.servico.OrgaoServico;

@ManagedBean(name = "manterTermoBean")
@SessionScoped
public class ManterTermoBean extends AtlasCrudBean<Termo> {

	/**
	 *
	 */
	private static final long serialVersionUID = -5802803768467289881L;

	@EJB(name = "ManterTermoServico")
	private ManterTermoServico termoServico;

	@EJB(name = "OrgaoServico")
	private OrgaoServico orgaoServico;

	private AtlasLazyDataModel<TermoBuscaDTO> termos;

	private Termo termoFiltro;

	private String termoPesquisa;

	public void init() throws AtlasAcessoBancoDadosException {
		this.setTermoFiltro(new Termo());
		carregarTermos(null);
	}

	public void carregarTermos(String textoParcial) throws AtlasAcessoBancoDadosException {
		termos = new AtlasLazyDataModel<TermoBuscaDTO>();
		termos.setServico(termoServico);
		termos.setMetodo("recuperarTermos");
		termoFiltro = new Termo();
		termoFiltro.setNome(textoParcial);
		termos.setParametros(termoFiltro);
		pesquisar();
	}

	public String novoRegistroManterTermo(){
		super.novoRegistro();
		return getTelaCadastro();
	}

	public void pesquisarTermo() throws AtlasAcessoBancoDadosException {
		if (this.termoPesquisa != null && !this.termoPesquisa.trim().isEmpty()) {
			carregarTermos("%" + this.termoPesquisa + "%");
		} else {
			carregarTermos(null);
		}
	}

	public void limpar() {
		this.termoPesquisa = null;
	}

	public void limparPesquisar() throws AtlasAcessoBancoDadosException {
		limpar();
		carregarTermos(null);
	}

	@Override
	public void inicializarObjetos() throws AtlasAcessoBancoDadosException {
		super.inicializarObjetos();
	}

	@Override
	public Boolean validar() throws AtlasNegocioException {
		if(getEntidade().getNome() == null || getEntidade().getNome().toString().trim().equals("")){
			error(null, "MSG20", "Nome do termo");
			return false;
		}
		return true;
	}

	public String editarManterTermo(Integer pk) {
		super.editar(pk);
		return getTelaCadastro();
	}

	public void excluirTermo(Termo termo) throws AtlasAcessoBancoDadosException, AtlasNegocioException {
		super.excluir(termo);
	}

	public void ativarDesativarTermo(TermoBuscaDTO termo) throws AtlasAcessoBancoDadosException, AtlasNegocioException {
		Termo termoAtual = termoServico.recuperarUltimoRegistroAtualizadoPorNome(termo.getNmeTermoBusca());
		Date data = termoAtual.getDataBloqueio() == null ? new Date() : null;
		termoServico.alterarDataBloqueioPorNomeTermo(termo.getNmeTermoBusca(), data);
	}

	public String getStatus(TermoBuscaDTO termo) throws AtlasAcessoBancoDadosException {
		Termo termoAtual = termoServico.recuperarUltimoRegistroAtualizadoPorNome(termo.getNmeTermoBusca());
		if (termoAtual.getDataBloqueio() == null)
			return "BLOQUEAR";
		else
			return "ATIVAR";
	}

	@Override
	protected void efetuarPesquisa() throws AtlasAcessoBancoDadosException {
		termos.setListaPaginada(termoServico.recuperarTermos(termoFiltro));
	}

	@Override
	protected AtlasServicoImpl getServico() {
		return termoServico;
	}

	@Override
	protected String getTelaPesquisa() {
		return "exibirTermos";
	}

	public String voltarTelaPesquisa() {
		return "exibirTermos";
	}

	public String getTermoPesquisa() {
		return termoPesquisa;
	}

	public void setTermoPesquisa(String termoPesquisa) {
		this.termoPesquisa = termoPesquisa;
	}

	@Override
	protected String getTelaCadastro() {
		return "informarTermos";
	}

	public Termo getTermoFiltro() {
		return termoFiltro;
	}

	public void setTermoFiltro(Termo termoFiltro) {
		this.termoFiltro = termoFiltro;
	}

	public AtlasLazyDataModel<TermoBuscaDTO> getTermos() {
		return termos;
	}

	public void setTermos(AtlasLazyDataModel<TermoBuscaDTO> termos) {
		this.termos = termos;
	}
}