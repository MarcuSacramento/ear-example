package br.gov.atlas.bean;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.primefaces.model.DualListModel;

import br.gov.atlas.component.model.AtlasLazyDataModel;
import br.gov.atlas.dto.TipoOrgaoProfissaoDTO;
import br.gov.atlas.entidade.Servico;
import br.gov.atlas.exception.AtlasAcessoBancoDadosException;
import br.gov.atlas.exception.AtlasNegocioException;
import br.gov.atlas.servico.AtlasServicoImpl;
import br.gov.atlas.servico.ManterServicoServico;
import br.gov.atlas.servico.ManterTematresServico;
import br.gov.atlas.servico.OrgaoServico;

@ManagedBean(name = "manterServicoBean")
@SessionScoped
public class ManterServicoBean extends AtlasCrudBean<Servico> {

	private static final long serialVersionUID = 5288423084607996395L;

	@EJB(name = "ManterServicoServico")
	ManterServicoServico servicoServico;

	@EJB(name = "OrgaoServico")
	private OrgaoServico orgaoServico;

	@EJB(name = "ManterTematresServico")
	private ManterTematresServico tematresServico;

	private AtlasLazyDataModel<Servico> servicos;

	private Servico servicoFiltro;

	private String termoPesquisa;

	private DualListModel<TipoOrgaoProfissaoDTO> enteList;

	public void init() throws AtlasAcessoBancoDadosException {
		this.setServicoFiltro(new Servico());
		carregarTermos(null);
	}

	public void carregarTermos(String textoParcial) throws AtlasAcessoBancoDadosException {
		servicos = new AtlasLazyDataModel<Servico>();
		servicos.setServico(servicoServico);
		servicos.setMetodo("recuperarServicos");
		servicoFiltro = new Servico();
		servicoFiltro.setNome(textoParcial);
		servicos.setParametros(servicoFiltro);
		pesquisar();
	}

	public String novoRegistroManterServico(){
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
			error(null, "MSG20", "Nome do serviço");
			return false;
		}
		return true;
	}

	public String editarManterServico(Integer pk) {
		super.editar(pk);
		return getTelaCadastro();
	}

	public void excluirServico(Servico servico) throws AtlasAcessoBancoDadosException, AtlasNegocioException {
		/*if(!existeOrgaoRelacionado(servico)){
			super.excluir(servico);
		}else{
			error(null, "MSG23", servico.getNome());
		}*/
	}

	@Override
	protected void efetuarPesquisa() throws AtlasAcessoBancoDadosException {
		servicos.setListaPaginada(servicoServico.recuperarServicos(servicoFiltro));
	}

	@Override
	protected AtlasServicoImpl getServico() {
		return servicoServico;
	}

	@Override
	protected String getTelaPesquisa() {
		return "exibirServicos";
	}

	public String voltarTelaPesquisa() {
		return "exibirServicos";
	}

	public String getTermoPesquisa() {
		return termoPesquisa;
	}

	public void setTermoPesquisa(String termoPesquisa) {
		this.termoPesquisa = termoPesquisa;
	}

	@Override
	protected String getTelaCadastro() {
		return "informarServicos";
	}

	public OrgaoServico getOrgaoServico() {
		return orgaoServico;
	}

	public void setOrgaoServico(OrgaoServico orgaoServico) {
		this.orgaoServico = orgaoServico;
	}

	public AtlasLazyDataModel<Servico> getServicos() {
		return servicos;
	}

	public void setServicos(AtlasLazyDataModel<Servico> servicos) {
		this.servicos = servicos;
	}

	public Servico getServicoFiltro() {
		return servicoFiltro;
	}

	public void setServicoFiltro(Servico servicoFiltro) {
		this.servicoFiltro = servicoFiltro;
	}

	public DualListModel<TipoOrgaoProfissaoDTO> getEnteList() {
		return enteList;
	}

	public void setEnteList(DualListModel<TipoOrgaoProfissaoDTO> enteList) {
		this.enteList = enteList;
	}


}