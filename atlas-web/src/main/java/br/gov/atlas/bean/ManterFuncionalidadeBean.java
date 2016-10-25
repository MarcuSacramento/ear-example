package br.gov.atlas.bean;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import br.gov.atlas.component.model.AtlasLazyDataModel;
import br.gov.atlas.entidade.Funcionalidade;
import br.gov.atlas.entity.ListaPaginada;
import br.gov.atlas.exception.AtlasAcessoBancoDadosException;
import br.gov.atlas.exception.AtlasNegocioException;
import br.gov.atlas.servico.AtlasServicoImpl;
import br.gov.atlas.servico.ManterFuncionalidadeServico;

@ManagedBean(name = "manterFuncionalidadeBean")
@SessionScoped
public class ManterFuncionalidadeBean extends AtlasCrudBean<Funcionalidade> {
	
	private static final long serialVersionUID = 400739953369421952L;

	@EJB(name = "ManterFuncionalidadeServico")
	ManterFuncionalidadeServico funcionalidadeServico;
	
	private AtlasLazyDataModel<Funcionalidade> funcionalidades;
	
	private String termoPesquisa;
	
	private String descricao;
	
	private Funcionalidade funcionalidadeFiltro;
	
	public void init() throws AtlasAcessoBancoDadosException {
		this.setFuncionalidadeFiltro(new Funcionalidade());
		carregarTermos(null);
	}
	
	public void carregarTermos(String textoParcial) throws AtlasAcessoBancoDadosException {
		funcionalidades = new AtlasLazyDataModel<Funcionalidade>();
		funcionalidades.setServico(funcionalidadeServico);
		funcionalidades.setMetodo("recuperarFuncionalidades");
		funcionalidadeFiltro = new Funcionalidade();
		funcionalidadeFiltro.setNome(textoParcial);
		funcionalidades.setParametros(funcionalidadeFiltro);
		pesquisar();
	}
	
	public String novoRegistroManterFuncionalidade(){
		super.novoRegistro();
		return getTelaCadastro();
	}
	
	@Override
	public void inicializarObjetos() throws AtlasAcessoBancoDadosException {
		super.inicializarObjetos();
		getEntidade().setCodigo(funcionalidadeServico.recuperarProximoCodigo());
	}
	
	
	public final String editar(Funcionalidade funcionalidade) {
		try {
			setEntidade(funcionalidade);
			initCarregarEntidade();

		} catch (AtlasAcessoBancoDadosException e) {
			error(e);
		}
		return getTelaCadastro();
	}
	
	public void excluirFuncionalidade(Funcionalidade funcionalidade) throws AtlasAcessoBancoDadosException, AtlasNegocioException {
		boolean existePerfilRelacionado = existePerfilRelacionado(funcionalidade);
		boolean isExclusaoPermitida = isExclusaoPermitida(funcionalidade);
		if(!existePerfilRelacionado && isExclusaoPermitida){
			super.excluir(funcionalidade); 
		}else{
			if(existePerfilRelacionado ){
				error(null, "MSG23", funcionalidade.getNome());
				
			}else if (isFuncionalidadeAssociadaAAlgumNivelAcesso(funcionalidade)){
				error(null, "MSG36", funcionalidade.getNome());
				
			} else {
				error(null, "MSG25", funcionalidade.getNome());
			}
		}
	}
	
	private Boolean isFuncionalidadeAssociadaAAlgumNivelAcesso(Funcionalidade funcionalidade) throws AtlasAcessoBancoDadosException
	{
		Boolean isCargoJaAssociado = funcionalidadeServico.isFuncionalidadeAssociadaAAlgumNivelAcesso(funcionalidade);
		return isCargoJaAssociado;
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
		this.descricao = null;
	}
	
	public void limparPesquisar() throws AtlasAcessoBancoDadosException {
		limpar();
		carregarTermos(null);
	}
	
	private boolean isExclusaoPermitida(Funcionalidade funcionalidade) {
		return funcionalidadeServico.isExclusaoPermitida(funcionalidade);
	}

	public boolean isAlteracaoPermitida(){
		return !funcionalidadeServico.isExclusaoPermitida(getEntidade());
	}
	
	private boolean existePerfilRelacionado(Funcionalidade funcionalidade) {
		return false;
	}

	@Override
	public Boolean validar() throws AtlasNegocioException {
		try {
			if(getEntidade().getCodigo() == null || getEntidade().getCodigo().trim().equals("")){
				error(null, "MSG20", "Código da funcionalidade");
				return false;
			}else{
				if(validarCodigoExistente() && getEntidade().getId() == null){
					error(null, "MSG24");
					return false;
				}
			}
			if(getEntidade().getNome() == null || getEntidade().getNome().trim().equals("")){
				error(null, "MSG20", "Nome da funcionalidade");
				return false;
			}
		} catch (AtlasAcessoBancoDadosException e) {
			e.printStackTrace();
		}
		return true;
	}

	private boolean validarCodigoExistente() throws AtlasAcessoBancoDadosException {
		Funcionalidade funcionalidadeFiltro = new Funcionalidade();
		funcionalidadeFiltro.setCodigo(getEntidade().getCodigo());
		ListaPaginada<Funcionalidade> funcList = funcionalidadeServico.recuperarFuncionalidades(funcionalidadeFiltro);
		return (funcList != null && funcList.getQtdRegistros().intValue() > 0);
	}

	public boolean isAlteracao(){
		return (getEntidade().getId() != null); 
	}
	
	@Override
	protected void efetuarPesquisa() throws AtlasAcessoBancoDadosException {
		funcionalidades.setListaPaginada(funcionalidadeServico.recuperarFuncionalidades(funcionalidadeFiltro));
	}

	@Override
	protected AtlasServicoImpl getServico() {
		return funcionalidadeServico;
	}

	@Override
	protected String getTelaPesquisa() {
		return "exibirFuncionalidades";
	}

	@Override
	protected String getTelaCadastro() {
		return "informarFuncionalidades";
	}
	
	public String voltarTelaPesquisa(){
		return getTelaPesquisa();
	}

	public String getTermoPesquisa() {
		return termoPesquisa;
	}

	public void setTermoPesquisa(String termoPesquisa) {
		this.termoPesquisa = termoPesquisa;
	}
	
	public AtlasLazyDataModel<Funcionalidade> getFuncionalidades() {
		return funcionalidades;
	}

	public void setFuncionalidades(AtlasLazyDataModel<Funcionalidade> funcionalidades) {
		this.funcionalidades = funcionalidades;
	}

	public Funcionalidade getFuncionalidadeFiltro() {
		return funcionalidadeFiltro;
	}

	public void setFuncionalidadeFiltro(Funcionalidade funcionalidadeFiltro) {
		this.funcionalidadeFiltro = funcionalidadeFiltro;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

}