package br.gov.atlas.bean;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import br.gov.atlas.component.model.AtlasLazyDataModel;
import br.gov.atlas.dto.FuncionalidadeDTO;
import br.gov.atlas.entidade.Funcionalidade;
import br.gov.atlas.entidade.NivelAcesso;
import br.gov.atlas.entidade.Perfil;
import br.gov.atlas.enums.TipoNivelAcesso;
import br.gov.atlas.exception.AtlasAcessoBancoDadosException;
import br.gov.atlas.exception.AtlasNegocioException;
import br.gov.atlas.servico.AtlasServicoImpl;
import br.gov.atlas.servico.ManterFuncionalidadeServico;
import br.gov.atlas.servico.ManterPerfilServico;

@ManagedBean(name = "manterPerfilBean")
@SessionScoped
public class ManterPerfilBean extends AtlasCrudBean<Perfil> {

	private static final long serialVersionUID = 5288423084607996355L;

	@EJB(name = "ManterPerfilServico")
	ManterPerfilServico manterPerfilServico;
	
	@EJB(name = "ManterFuncionalidadeServico")
	ManterFuncionalidadeServico manterFuncionalidadeServico;

	private String termoPesquisa;

	private AtlasLazyDataModel<Perfil> perfis;

	private Perfil perfilFiltro;
	
	private String descricao;

	private FuncionalidadeDTO funcionalidadeDTO;

	private List<Funcionalidade> funcionalidades;

	private List<String> niveisAcesso;

	public void init() throws AtlasAcessoBancoDadosException {
		this.perfilFiltro = new Perfil();
		funcionalidadeDTO = new FuncionalidadeDTO();
		carregarTermos(null);
	}
	
	public List<Funcionalidade> getKeySetAsList() throws AtlasAcessoBancoDadosException {
		carregarNiveisAcesso();
		return new ArrayList<Funcionalidade>(funcionalidadeDTO.getMapFuncionalidade().keySet());
	}

	private void carregarNiveisAcesso() throws AtlasAcessoBancoDadosException {
		if (funcionalidadeDTO == null
				|| funcionalidadeDTO.getMapFuncionalidade().isEmpty()) {
			if (getEntidade().getId() == null) {
				carregarNiveisAcessoInsercao();
			} else {
				if (getEntidade().getNiveisAcesso().size() != manterFuncionalidadeServico
						.contarRegistros().intValue()) {
					inserirNovasFuncionalidadesParaNiveisAcesso();
				}
				Map<Funcionalidade, String> mapNiveisAcesso = new TreeMap<Funcionalidade, String>();
				if (getEntidade().getNiveisAcesso() != null
						&& !getEntidade().getNiveisAcesso().isEmpty()) {
					for (NivelAcesso nivelAcesso : getEntidade()
							.getNiveisAcesso()) {
						Funcionalidade funcionalidade = nivelAcesso
								.getFuncionalidade();
						TipoNivelAcesso tipoNivelAcesso = TipoNivelAcesso
								.getTipo(nivelAcesso.getIndNivelAcesso());
						mapNiveisAcesso.put(funcionalidade,
								tipoNivelAcesso.getValor());
					}
					funcionalidadeDTO.setMapFuncionalidade(mapNiveisAcesso);

				}

			}
		}
	}

	/**
	 * Método utilizado para criar níveis de acesso com funcionalidades criadas após a inserção do perfil
	 * 
	 * @throws AtlasAcessoBancoDadosException
	 */
	private void inserirNovasFuncionalidadesParaNiveisAcesso()
			throws AtlasAcessoBancoDadosException {
		List<Funcionalidade> novasFuncionalidades = manterFuncionalidadeServico
				.recuperarFuncionalidadesNaoEstaoVinculadasPerfil(getEntidade());
		for (Funcionalidade funcionalidade : novasFuncionalidades) {
			NivelAcesso nivelAcesso = new NivelAcesso();
			nivelAcesso.setFuncionalidade(funcionalidade);
			nivelAcesso.setPerfil(getEntidade());
			nivelAcesso
			.setIndNivelAcesso(TipoNivelAcesso.SEM_ACESSO.getValor());
			getEntidade().getNiveisAcesso().add(nivelAcesso);
		}
	}

	@SuppressWarnings("unchecked")
	private void carregarNiveisAcessoInsercao() throws AtlasAcessoBancoDadosException {
		Map<Funcionalidade, String> mapNiveisAcesso = new TreeMap<Funcionalidade, String>();
		funcionalidades = manterFuncionalidadeServico.buscarTodos(Funcionalidade.class, "codigo");
		for (Funcionalidade funcionalidade : funcionalidades) {
			mapNiveisAcesso.put(funcionalidade, TipoNivelAcesso.SEM_ACESSO.getValor());
		}

		funcionalidadeDTO.setMapFuncionalidade(mapNiveisAcesso);
	}

	@Override
	public Boolean validar() throws AtlasNegocioException {
		StringBuffer campoObrigatorio = new StringBuffer();
		boolean valido = true;

		if (getEntidade().getNome()== null	|| getEntidade().getNome().trim().isEmpty()) {
			valido = false;
			campoObrigatorio.append(campoObrigatorio.toString().trim().isEmpty() ? "Nome" : ", Nome");
		}
		if (getEntidade().getTipo() == null	|| getEntidade().getTipo().trim().isEmpty()) {
			valido = false;
			campoObrigatorio.append(campoObrigatorio.toString().trim().isEmpty() ? "Tipo" : ", Tipo");
		}
		if (getEntidade().getSituacao() == null	|| getEntidade().getSituacao().trim().isEmpty()) {
			valido = false;
			campoObrigatorio.append(campoObrigatorio.toString().trim().isEmpty() ? "Situação" : ", Situação");
		}
		Set<Funcionalidade> funcionalidadeSet = funcionalidadeDTO.getMapFuncionalidade().keySet();
		for (Funcionalidade funcionalidade : funcionalidadeSet) {
			String nivelAcesso = funcionalidadeDTO.getMapFuncionalidade().get(funcionalidade);
			if(nivelAcesso == null){
				campoObrigatorio.append("Situação da funcionalidade "+funcionalidade.getNome()+" obrigatória! ");
				valido = false;
			}
		}
		
		if (!valido){
			error(null, "MSG20", campoObrigatorio.toString());
		}
		
		if(verificarIsPerfilExiste(getEntidade())){
			valido = false;
			error(null, "MSG_PEFIL_CADASTRADO");
		}

		return valido;
	}

	@Override
	public String salvar() {
		try {
			if(validar()){
				if(getEntidade().getId() == null){
					super.salvar();
					processarNiveisAcessoPerfil();
					super.salvarSemExibirMensagem();
				}else{
					processarNiveisAcessoPerfil();
					super.salvar();
				}
				return "pesquisarPerfil";
			}
		} catch (AtlasNegocioException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		return null;
	}

	private void processarNiveisAcessoPerfil() {
		Map<Funcionalidade, String> mapFuncionalidade = funcionalidadeDTO.getMapFuncionalidade();
		Set<Funcionalidade> funcionalidadeSet = mapFuncionalidade.keySet();
		if (getEntidade().getNiveisAcesso() != null
				&& !getEntidade().getNiveisAcesso().isEmpty()) {
			alteraNiveisAcessoEntidade(mapFuncionalidade, funcionalidadeSet);
		}else{
			insereNiveisAcessoEntidade(funcionalidadeSet);
		}
	}

	private void insereNiveisAcessoEntidade(Set<Funcionalidade> funcionalidadeSet) {
		for (Funcionalidade funcionalidade : funcionalidadeSet) {
			String indNivelAcesso = funcionalidadeDTO.getMapFuncionalidade().get(funcionalidade);
			NivelAcesso nivelAcesso = new NivelAcesso();
			nivelAcesso.setFuncionalidade(funcionalidade);
			nivelAcesso.setPerfil(getEntidade());
			nivelAcesso.setIndNivelAcesso(indNivelAcesso);
			getEntidade().getNiveisAcesso().add(nivelAcesso);
		}
	}

	private void alteraNiveisAcessoEntidade(
			Map<Funcionalidade, String> mapFuncionalidade,
			Set<Funcionalidade> funcionalidadeSet) {
		externo:
			for (Funcionalidade funcionalidade : funcionalidadeSet) {
				List<NivelAcesso> niveisAcessoEntidade = getEntidade().getNiveisAcesso();
				for (NivelAcesso nivelAcessoEntidade : niveisAcessoEntidade) {
					if(nivelAcessoEntidade.getFuncionalidade().getCodigo().equals(funcionalidade.getCodigo())){
						String strTipoNivelAcesso = mapFuncionalidade.get(funcionalidade);
						TipoNivelAcesso tipoNivelAcesso = TipoNivelAcesso.getTipo(strTipoNivelAcesso);
						nivelAcessoEntidade.setIndNivelAcesso(tipoNivelAcesso.getValor());
						continue externo;
					}
				}
			}
	}

	public void excluirPerfil(Perfil perfil) throws AtlasAcessoBancoDadosException, AtlasNegocioException{
		if( verificarIsPerfilJaAssociadoAAlgumUsuario(perfil) ) {
			error(null, "MSG22");
		} else {
			super.excluir(perfil);
		}
	}

	private Boolean verificarIsPerfilJaAssociadoAAlgumUsuario(Perfil perfil) throws AtlasAcessoBancoDadosException {
		return manterPerfilServico.isPerfilAssociadoAAlgumUsuario(perfil);
	}

	private Boolean verificarIsPerfilExiste(Perfil perfil) {
		try {
			return manterPerfilServico.isPerfilExiste(perfil);
		} catch (AtlasAcessoBancoDadosException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	protected AtlasServicoImpl getServico() {
		return manterPerfilServico;
	}

	@Override
	public void inicializarObjetos() throws AtlasAcessoBancoDadosException {
		super.inicializarObjetos();
	}

	@Override
	protected void efetuarPesquisa() throws AtlasAcessoBancoDadosException {
		perfis.setListaPaginada(manterPerfilServico.recuperarPerfis(perfilFiltro, "nme_perfil"));
	}

	public void carregarTermos(String textoParcial) throws AtlasAcessoBancoDadosException {
		perfis = new AtlasLazyDataModel<Perfil>();
		perfis.setServico(manterPerfilServico);
		perfis.setMetodo("recuperarPerfis");
		perfilFiltro = new Perfil();
		perfilFiltro.setNome(textoParcial);
		perfilFiltro.setDescricao(descricao);
		perfis.setParametros(perfilFiltro, "nme_perfil");
		pesquisar();
	}

	public String novoRegistroManterPerfil()
	{
		super.novoRegistro();
		getEntidade().setDataExpiracao( calcularDataDaquiAUmAno() );
		return getTelaCadastro();
	}

	private Date calcularDataDaquiAUmAno()
	{
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.add( Calendar.YEAR, 1);
		Date dataDaquiAUmAno = calendar.getTime();
		return dataDaquiAUmAno;
	}
	
	public void limpar() {
		this.termoPesquisa = null;
		this.descricao = null;
		try {
			init();
		} catch (AtlasAcessoBancoDadosException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void limparPesquisar() throws AtlasAcessoBancoDadosException {
		limpar();
		carregarTermos(null);
	}
	
	public void pesquisarTermo() throws AtlasAcessoBancoDadosException {
		if (this.termoPesquisa != null && !this.termoPesquisa.trim().isEmpty()) {
			carregarTermos("%" + this.termoPesquisa + "%");
		} else {
			carregarTermos(null);
		}
	}
	
	public String editarManterPerfil(Integer pk) {
		super.editar(pk);
		return getTelaCadastro();
	}

	public String voltarTelaPesquisa() {
		return getTelaPesquisa();
	}

	@Override
	public String cancelar() {
		limpar();
		return "/admin/gerenciamento/perfil/exibirPerfil.faces";
	}
	
	@Override
	protected String getTelaPesquisa() {
		return "perfilConsulta";
	}

	@Override
	protected String getTelaCadastro() {
		return "informarPerfil";
	}

	public String getTermoPesquisa() {
		return termoPesquisa;
	}

	public void setTermoPesquisa(String termoPesquisa) {
		this.termoPesquisa = termoPesquisa;
	}

	public AtlasLazyDataModel<Perfil> getPerfis() {
		return perfis;
	}

	public void setPerfis(AtlasLazyDataModel<Perfil> perfis) {
		this.perfis = perfis;
	}

	public Perfil getPerfilFiltro() {
		return perfilFiltro;
	}

	public void setPerfilFiltro(Perfil perfilFiltro) {
		this.perfilFiltro = perfilFiltro;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao){
		this.descricao = descricao;
	}
	
	public boolean isEstahIncluindo() {
		Perfil perfil = getEntidade();
		if (perfil == null || perfil.getId() == null)
			return true;
		return false;
	}

	public boolean isEstahAlterando() {
		return isEstahIncluindo()==false;
	}

	public FuncionalidadeDTO getFuncionalidadeDTO() {
		return funcionalidadeDTO;
	}

	public void setFuncionalidadeDTO(FuncionalidadeDTO funcionalidadeDTO) {
		this.funcionalidadeDTO = funcionalidadeDTO;
	}

	public List<Funcionalidade> getFuncionalidades() {
		return funcionalidades;
	}

	public void setFuncionalidades(List<Funcionalidade> funcionalidades) {
		this.funcionalidades = funcionalidades;
	}

	public List<String> getNiveisAcesso() {
		return niveisAcesso;
	}

	public void setNiveisAcesso(List<String> niveisAcesso) {
		this.niveisAcesso = niveisAcesso;
	}
}
