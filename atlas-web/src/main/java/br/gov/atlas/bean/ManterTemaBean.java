package br.gov.atlas.bean;

import java.util.List;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import br.gov.atlas.component.model.AtlasLazyDataModel;
import br.gov.atlas.dto.OrgaoDTO;
import br.gov.atlas.entidade.Orgao;
import br.gov.atlas.entidade.Tema;
import br.gov.atlas.exception.AtlasAcessoBancoDadosException;
import br.gov.atlas.exception.AtlasNegocioException;
import br.gov.atlas.servico.AtlasServicoImpl;
import br.gov.atlas.servico.ManterTemaServico;
import br.gov.atlas.servico.OrgaoServico;

@ManagedBean(name = "manterTemaBean")
@SessionScoped
public class ManterTemaBean extends AtlasCrudBean<Tema> {
	
	private static final long serialVersionUID = 5288423084607996395L;

	@EJB(name = "ManterTemaServico")
	ManterTemaServico temaServico;
	
	@EJB(name = "OrgaoServico")
	private OrgaoServico orgaoServico;

	private AtlasLazyDataModel<Tema> temas;

	private Tema temaFiltro;
	
	private String termoPesquisa;
	
	public void init() throws AtlasAcessoBancoDadosException {
		this.setTemaFiltro(new Tema());
		carregarTermos(null);
	}
	
	public void carregarTermos(String textoParcial) throws AtlasAcessoBancoDadosException {
		temas = new AtlasLazyDataModel<Tema>();
		temas.setServico(temaServico);
		temas.setMetodo("recuperarTemas");
		temaFiltro = new Tema();
		temaFiltro.setNome(textoParcial);
		temas.setParametros(temaFiltro);
		pesquisar();
	}
	
	public String novoRegistroManterTema(){
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
			error(null, "MSG20", "Nome do tema");
			return false;
		}
		return true;
	}

	public String editarManterTema(Integer pk) {
		super.editar(pk);
		return getTelaCadastro();
	}
	
	public void excluirTema(Tema tema) throws AtlasAcessoBancoDadosException, AtlasNegocioException {
		if(!existeOrgaoRelacionado(tema)){
			super.excluir(tema); 
		}else{
			error(null, "MSG23", tema.getNome());
		}
	}
	
	private boolean existeOrgaoRelacionado(Tema tema) throws AtlasAcessoBancoDadosException {
		OrgaoDTO orgaoFiltro = new OrgaoDTO();
		orgaoFiltro.setTema(tema);
		List<Orgao> orgaos = orgaoServico.recuperarOrgaos(orgaoFiltro);
		return (orgaos != null && !orgaos.isEmpty());
	}

	@Override
	protected void efetuarPesquisa() throws AtlasAcessoBancoDadosException {
		temas.setListaPaginada(temaServico.recuperarTemas(temaFiltro));
	}

	@Override
	protected AtlasServicoImpl getServico() {
		return temaServico;
	}

	@Override
	protected String getTelaPesquisa() {
		return "exibirTemas";
	}
	
	public String voltarTelaPesquisa() {
		return "exibirTemas";
	}

	public String getTermoPesquisa() {
		return termoPesquisa;
	}

	public void setTermoPesquisa(String termoPesquisa) {
		this.termoPesquisa = termoPesquisa;
	}
	
	@Override
	protected String getTelaCadastro() {
		return "informarTemas";
	}
	
	public Tema getTemaFiltro() {
		return temaFiltro;
	}

	public void setTemaFiltro(Tema temaFiltro) {
		this.temaFiltro = temaFiltro;
	}

	public AtlasLazyDataModel<Tema> getTemas() {
		return temas;
	}

	public void setTemas(AtlasLazyDataModel<Tema> temas) {
		this.temas = temas;
	}
}