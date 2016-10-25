package br.gov.atlas.bean;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.event.ActionEvent;

import br.gov.atlas.component.model.AtlasLazyDataModel;
import br.gov.atlas.entidade.Servico;
import br.gov.atlas.entidade.TipoOrgao;
import br.gov.atlas.exception.AtlasAcessoBancoDadosException;
import br.gov.atlas.exception.AtlasNegocioException;
import br.gov.atlas.servico.AtlasServicoImpl;
import br.gov.atlas.servico.ManterRamoServico;
import br.gov.atlas.servico.ManterServicoServico;
import br.gov.atlas.servico.ManterTematresServico;
import br.gov.atlas.servico.ManterTipoOrgaoServico;
import br.gov.atlas.servico.OrgaoServico;

@ManagedBean(name = "manterTipoOrgaoBean")
@SessionScoped
public class ManterTipoOrgaoBean extends AtlasCrudBean<TipoOrgao> {

	private static final long serialVersionUID = 5288423084607996395L;

	@EJB(name = "ManterTipoOrgaoServico")
	private ManterTipoOrgaoServico tipoOrgaoServico;

	@EJB(name = "ManterRamoServico")
	private ManterRamoServico ramoServico;

	@EJB(name = "OrgaoServico")
	private OrgaoServico orgaoServico;

	@EJB(name = "ManterServicoServico")
	private ManterServicoServico servicoServico;

	@EJB(name = "ManterTematresServico")
	private ManterTematresServico tematresServico;

	private AtlasLazyDataModel<TipoOrgao> tiposOrgao;

	private TipoOrgao tipoOrgaoFiltro;

	private String termoPesquisa;

	private List<Servico> servicosParaSelecao;

	private List<Servico> servicosSelecionados;

	private Servico servicoEmFoco;

	public void init() throws AtlasAcessoBancoDadosException {
		limpar();
		this.setTipoOrgaoFiltro(new TipoOrgao());
		carregarTermos(null);
	}

	public void carregarTermos(String textoParcial)
			throws AtlasAcessoBancoDadosException {
		this.tiposOrgao = new AtlasLazyDataModel<TipoOrgao>();
		this.tiposOrgao.setServico(this.tipoOrgaoServico);
		this.tiposOrgao.setMetodo("recuperarTiposOrgao");
		this.tipoOrgaoFiltro = new TipoOrgao();
		this.tipoOrgaoFiltro.setNome(textoParcial);
		this.tiposOrgao.setParametros(this.tipoOrgaoFiltro);
		pesquisar();
	}

	public String novoRegistroManterTipoOrgao() {
		super.novoRegistro();
		return getTelaCadastro();
	}

	@Override
	public String salvar() {
		super.salvar();
		return null;
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
		setServicoEmFoco(new Servico());
		setServicosParaSelecao(new ArrayList<Servico>());
		setServicosSelecionados(new ArrayList<Servico>());
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
		StringBuffer campoObrigatorio = new StringBuffer();
		boolean valido = true;

		if ((getEntidade().getNome() == null)
				|| getEntidade().getNome().toString().trim().equals("")) {
			campoObrigatorio.append(campoObrigatorio.toString().trim()
					.isEmpty() ? "Nome" : ", Nome");
			valido = false;
		}

		if (!valido) {
			error(null, "MSG20", campoObrigatorio.toString());
		}

		return valido;
	}

	public String editarManterTipoOrgao(Integer pk) {
		super.editar(pk);
		return getTelaCadastro();
	}

	public void excluirTipoOrgao(TipoOrgao tipoOrgao)
			throws AtlasAcessoBancoDadosException, AtlasNegocioException {
		if (!existeTipoOrgaoRelacionado(tipoOrgao)) {
			super.excluir(tipoOrgao);
		} else {
			error(null, "MSG26", tipoOrgao.getNome());
		}
	}

	private boolean existeTipoOrgaoRelacionado(TipoOrgao tipoOrgao)
			throws AtlasAcessoBancoDadosException {
		return this.orgaoServico.possuiOrgaos(tipoOrgao);
	}

	@Override
	protected void efetuarPesquisa() throws AtlasAcessoBancoDadosException {
		this.tiposOrgao.setListaPaginada(this.tipoOrgaoServico
				.recuperarTiposOrgao(this.tipoOrgaoFiltro));
	}

	public void pesquisarServicos() throws AtlasAcessoBancoDadosException {
		setServicosParaSelecao(tipoOrgaoServico
				.recuperarServicosDisponiveisSelecao(getEntidade(),
						getServicoEmFoco().getNome()));
	}

	public void adicionarServicoAction() throws AtlasAcessoBancoDadosException,
	AtlasNegocioException {
		if (getServicosSelecionados() != null
				&& !getServicosSelecionados().isEmpty()) {
			if(getEntidade().getServicos() == null){
				getEntidade().setServicos(new ArrayList<Servico>());
			}
			getEntidade().getServicos().addAll(getServicosSelecionados());
		}
	}

	public void limparFiltroDlgServicos(ActionEvent event){
		setServicosParaSelecao(new ArrayList<Servico>());
		setServicosSelecionados(new ArrayList<Servico>());
		setServicoEmFoco(new Servico());
	}

	public void excluirServico(Servico servico){
		getEntidade().getServicos().remove(servico);
	}

	@Override
	protected AtlasServicoImpl getServico() {
		return this.tipoOrgaoServico;
	}

	public String getTermoPesquisa() {
		return termoPesquisa;
	}

	public void setTermoPesquisa(String termoPesquisa) {
		this.termoPesquisa = termoPesquisa;
	}

	@Override
	protected String getTelaPesquisa() {
		return "tipoOrgaoConsulta";
	}

	public String voltarTelaPesquisa() {
		return "tipoOrgaoConsulta";
	}

	@Override
	protected String getTelaCadastro() {
		return "tipoOrgaoCadastro";
	}

	public TipoOrgao getTipoOrgaoFiltro() {
		return this.tipoOrgaoFiltro;
	}

	public void setTipoOrgaoFiltro(TipoOrgao tipoOrgaoFiltro) {
		this.tipoOrgaoFiltro = tipoOrgaoFiltro;
	}

	public AtlasLazyDataModel<TipoOrgao> getTiposOrgao() {
		return this.tiposOrgao;
	}

	public void setTiposOrgao(AtlasLazyDataModel<TipoOrgao> tiposOrgao) {
		this.tiposOrgao = tiposOrgao;
	}

	public List<Servico> getServicosParaSelecao() {
		return servicosParaSelecao;
	}

	public void setServicosParaSelecao(List<Servico> servicosParaSelecao) {
		this.servicosParaSelecao = servicosParaSelecao;
	}

	public List<Servico> getServicosSelecionados() {
		return servicosSelecionados;
	}

	public void setServicosSelecionados(List<Servico> servicosSelecionados) {
		this.servicosSelecionados = servicosSelecionados;
	}

	public Servico getServicoEmFoco() {
		return servicoEmFoco;
	}

	public void setServicoEmFoco(Servico servicoEmFoco) {
		this.servicoEmFoco = servicoEmFoco;
	}

}