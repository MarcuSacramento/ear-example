package br.gov.atlas.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.primefaces.context.RequestContext;
import org.primefaces.event.CellEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.ToggleSelectEvent;
import org.primefaces.event.UnselectEvent;
import org.primefaces.event.data.PageEvent;
import org.primefaces.model.DualListModel;

import br.gov.atlas.component.model.AtlasLazyDataModel;
import br.gov.atlas.dto.DadosEmBlocoDTO;
import br.gov.atlas.dto.OrgaoDTO;
import br.gov.atlas.entidade.Cargo;
import br.gov.atlas.entidade.EmailUsuario;
import br.gov.atlas.entidade.Municipio;
import br.gov.atlas.entidade.Orgao;
import br.gov.atlas.entidade.Representante;
import br.gov.atlas.entidade.Servico;
import br.gov.atlas.entidade.Tema;
import br.gov.atlas.entidade.TipoOrgao;
import br.gov.atlas.entidade.UF;
import br.gov.atlas.entity.ListaPaginada;
import br.gov.atlas.enums.TipoSituacaoRegistroOrgao;
import br.gov.atlas.exception.AtlasAcessoBancoDadosException;
import br.gov.atlas.servico.EmailServico;
import br.gov.atlas.servico.ManterCargoServico;
import br.gov.atlas.servico.ManterServicoServico;
import br.gov.atlas.servico.ManterTemaServico;
import br.gov.atlas.servico.ManterTematresServico;
import br.gov.atlas.servico.ManterTipoOrgaoServico;
import br.gov.atlas.servico.MunicipioServico;
import br.gov.atlas.servico.OrgaoServico;
import br.gov.atlas.servico.RepresentanteServico;
import br.gov.atlas.servico.TelefoneServico;
import br.gov.atlas.util.Valida;

@ManagedBean(name = "manterDadosEmBlocoBean")
@SessionScoped
public class ManterDadosEmBlocoBean {

	@EJB(name = "OrgaoServico")
	OrgaoServico orgaoServico;

	@EJB(name = "MunicipioServico")
	private MunicipioServico municipioServico;

	@EJB(name = "ManterTipoOrgaoServico")
	private ManterTipoOrgaoServico tipoOrgaoServico;

	@EJB(name = "RepresentanteServico")
	private RepresentanteServico representanteServico;

	@EJB(name = "TelefoneServico")
	private TelefoneServico telefoneServico;

	@EJB(name = "EmailServico")
	private EmailServico emailServico;

	@EJB(name = "ManterTemaServico")
	private ManterTemaServico temaServico;

	@EJB(name = "ManterServicoServico")
	private ManterServicoServico servicoServico;

	@EJB(name = "ManterTematresServico")
	private ManterTematresServico tematresServico;

	@EJB(name = "ManterCargoServico")
	ManterCargoServico cargoServico;

	private AtlasLazyDataModel<Orgao> orgaosPesquisa;
	private List<Orgao> orgaosSelecionados;

	private Map<Integer, List<Orgao>> orgaosSelecaoPagina = new HashMap<>();
	private Integer numPagina = 0;
	private Integer numPaginaAnterior = 0;
	private DadosEmBlocoDTO dadosEmBlocoDTO;
	private DualListModel<Tema> temasList;
	private DualListModel<Servico> servicosList;
	private String termoPesquisa;
	private String carregarOrgaosInformacaoLatitudeLongitude;
	private String carregarOrgaosSituacaoRegistro;
	private String carregarOrgaosErro;
	private UF uf;
	private Municipio municipio;
	private TipoOrgao tipoOrgao;
	private Tema tema;
	private Servico servico;
	private List<Representante> representantes;
	private Representante representante;
	private OrgaoDTO orgaoFiltro;

	boolean representanteValido; 
	
	public boolean isRepresentanteValido() {
		return representanteValido;
	}

	public void setRepresentanteValido(boolean representanteValido) {
		this.representanteValido = representanteValido;
	}

	private String filtroInformacao;

	public String getFiltroInformacao() {
		return filtroInformacao;
	}

	public void setFiltroInformacao(String filtroInformacao) {
		this.filtroInformacao = filtroInformacao;
	}

	// Filtro do dialog para recuperar o órgão superior
	private List<Orgao> filtroDlgOrgaosPesquisa = new ArrayList<>();
	private String filtroDlgNomeOrgaoPesquisa;
	private UF filtroDlgUf;
	private Municipio filtroDlgMunicipio;
	private TipoOrgao filtroDlgTipoOrgao;
	private Tema filtroDlgTema;
	private String filtroDlgCarregarOrgaosInformacaoLatitudeLongitude;
	private String filtroDlgCarregarOrgaosSituacaoRegistro;
	private String filtroDlgCarregarOrgaosErro;
	private Orgao orgaoSuperiorSelecionado;

	public List<Orgao> getOrgaosSelecionados() {
		return orgaosSelecionados;
	}

	public void setOrgaosSelecionados(List<Orgao> orgaosSelecionados) {
		this.orgaosSelecionados = orgaosSelecionados;
	}

	public DadosEmBlocoDTO getDadosEmBlocoDTO() {
		if(dadosEmBlocoDTO == null){
			dadosEmBlocoDTO = new DadosEmBlocoDTO();
		}
		return dadosEmBlocoDTO;
	}

	public void setDadosEmBlocoDTO(DadosEmBlocoDTO dadosEmBlocoDTO) {
		this.dadosEmBlocoDTO = dadosEmBlocoDTO;
	}

	public DualListModel<Tema> getTemasList() {
		return temasList;
	}

	public DualListModel<Servico> getServicosList() {
		return servicosList;
	}

	public List<Orgao> getFiltroDlgOrgaosPesquisa() {
		return filtroDlgOrgaosPesquisa;
	}

	public void setFiltroDlgOrgaosPesquisa(List<Orgao> filtroDlgOrgaosPesquisa) {
		this.filtroDlgOrgaosPesquisa = filtroDlgOrgaosPesquisa;
	}

	public String getFiltroDlgNomeOrgaoPesquisa() {
		return filtroDlgNomeOrgaoPesquisa;
	}

	public void setFiltroDlgNomeOrgaoPesquisa(String filtroDlgNomeOrgaoPesquisa) {
		this.filtroDlgNomeOrgaoPesquisa = filtroDlgNomeOrgaoPesquisa;
	}

	public UF getFiltroDlgUf() {
		return filtroDlgUf;
	}

	public void setFiltroDlgUf(UF filtroDlgUf) {
		this.filtroDlgUf = filtroDlgUf;
	}

	public Municipio getFiltroDlgMunicipio() {
		return filtroDlgMunicipio;
	}

	public void setFiltroDlgMunicipio(Municipio filtroDlgMunicipio) {
		this.filtroDlgMunicipio = filtroDlgMunicipio;
	}

	public TipoOrgao getFiltroDlgTipoOrgao() {
		return filtroDlgTipoOrgao;
	}

	public void setFiltroDlgTipoOrgao(TipoOrgao filtroDlgTipoOrgao) {
		this.filtroDlgTipoOrgao = filtroDlgTipoOrgao;
	}

	public Tema getFiltroDlgTema() {
		return filtroDlgTema;
	}

	public void setFiltroDlgTema(Tema filtroDlgTema) {
		this.filtroDlgTema = filtroDlgTema;
	}

	public String getFiltroDlgCarregarOrgaosInformacaoLatitudeLongitude() {
		return filtroDlgCarregarOrgaosInformacaoLatitudeLongitude;
	}

	public void setFiltroDlgCarregarOrgaosInformacaoLatitudeLongitude(
			String filtroDlgCarregarOrgaosInformacaoLatitudeLongitude) {
		this.filtroDlgCarregarOrgaosInformacaoLatitudeLongitude = filtroDlgCarregarOrgaosInformacaoLatitudeLongitude;
	}

	public String getFiltroDlgCarregarOrgaosSituacaoRegistro() {
		return filtroDlgCarregarOrgaosSituacaoRegistro;
	}

	public void setFiltroDlgCarregarOrgaosSituacaoRegistro(
			String filtroDlgCarregarOrgaosSituacaoRegistro) {
		this.filtroDlgCarregarOrgaosSituacaoRegistro = filtroDlgCarregarOrgaosSituacaoRegistro;
	}

	public String getFiltroDlgCarregarOrgaosErro() {
		return filtroDlgCarregarOrgaosErro;
	}

	public void setFiltroDlgCarregarOrgaosErro(String filtroDlgCarregarOrgaosErro) {
		this.filtroDlgCarregarOrgaosErro = filtroDlgCarregarOrgaosErro;
	}

	public Orgao getOrgaoSuperiorSelecionado() {
		return orgaoSuperiorSelecionado;
	}

	public void setOrgaoSuperiorSelecionado(Orgao orgaoSuperiorSelecionado) {
		this.orgaoSuperiorSelecionado = orgaoSuperiorSelecionado;
	}

	public void limpar() {
		this.termoPesquisa = null;
		this.carregarOrgaosInformacaoLatitudeLongitude = null;
		this.carregarOrgaosSituacaoRegistro = null;
		this.carregarOrgaosErro = null;
		this.uf = null;
		this.municipio = null;
		this.tipoOrgao = null;
		this.tema = null;
		this.servico = null;

	}

	public void limparPesquisar() throws AtlasAcessoBancoDadosException {
		limpar();
		carregarTermos(null);
	}

	public void removerOrgaoSuperior(ActionEvent event){
		if(getDadosEmBlocoDTO().getOrgaoSuperior() != null){
			getDadosEmBlocoDTO().setOrgaoSuperior(null);
		}
	}

	public List<Municipio> getFiltroDlgMunicipios()
			throws AtlasAcessoBancoDadosException {
		if (getFiltroDlgUf() != null) {
			return municipioServico.recuperarMunicipios(getFiltroDlgUf());
		} else {
			return null;
		}
	}

	public String getNomeTipoOrgaoDoOrgaoSuperior() throws AtlasAcessoBancoDadosException{
		if (getDadosEmBlocoDTO().getOrgaoSuperior() != null
				&& getDadosEmBlocoDTO().getOrgaoSuperior().getTipoOrgao() != null) {
			Integer idTipoOrgao = getDadosEmBlocoDTO().getOrgaoSuperior().getTipoOrgao().getId();
			TipoOrgao tipoDoOrgaoSuperior = (TipoOrgao) tipoOrgaoServico.pesquisarPorId(idTipoOrgao, TipoOrgao.class);
			return tipoDoOrgaoSuperior.getNome();
		}
		return null;
	}

	public void informaOrgaoSuperior(ActionEvent event){
		if(orgaoSuperiorSelecionado != null){
			getDadosEmBlocoDTO().setOrgaoSuperior(orgaoSuperiorSelecionado);
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, null, "Órgão adicionado. Clique em SALVAR para confirmar a operação!"));
		}else{
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_WARN, null, "Selecione um órgão para realizar a operação!"));
		}
	}

	public void limparFiltroDlgOrgao(ActionEvent event){
		filtroDlgOrgaosPesquisa = new ArrayList<>();
		filtroDlgNomeOrgaoPesquisa = null;
		filtroDlgMunicipio = null;
		filtroDlgTipoOrgao = null;
		filtroDlgUf = null;
		filtroDlgTema = null;
		filtroDlgCarregarOrgaosInformacaoLatitudeLongitude = null;
		filtroDlgCarregarOrgaosSituacaoRegistro = null;
		filtroDlgCarregarOrgaosErro = null;
	}

	public void pesquisarOrgaos(ActionEvent event) throws AtlasAcessoBancoDadosException {
		if (this.filtroDlgNomeOrgaoPesquisa != null && !this.filtroDlgNomeOrgaoPesquisa.trim().isEmpty()) {
			pesquisarOrgaosPorFiltro("%" + this.filtroDlgNomeOrgaoPesquisa + "%");
		} else {
			pesquisarOrgaosPorFiltro(null);
		}
	}

	/**
	 * Metodo responsavel por carregar os orgão de acordo com os filtros.
	 * @param textoParcial
	 * @throws AtlasAcessoBancoDadosException
	 */
	public void pesquisarOrgaosPorFiltro(String textoParcial) throws AtlasAcessoBancoDadosException {

		// FILTRO, DTO
		OrgaoDTO orgaoFiltro = new OrgaoDTO();

		// FILTROS MAIS COMUNS
		orgaoFiltro.setNome(textoParcial);
		orgaoFiltro.setMunicipio(filtroDlgMunicipio);
		orgaoFiltro.setTipoOrgao(filtroDlgTipoOrgao);
		orgaoFiltro.setUf(filtroDlgUf);
		orgaoFiltro.setTema(filtroDlgTema);

		//		if (!isAdministrador()){
		//			List<OrgaoDTO> orgaoDTOs = recuperarOrgaosDTO();
		//			orgaoFiltro.setOrgaos(orgaoDTOs);
		//		}

		// COORDENADAS GEOGRAFICAS
		String valorVindoDaApresentacaoCoordenadas = getFiltroDlgCarregarOrgaosInformacaoLatitudeLongitude();
		Boolean booleanValueOrgaosComCoordenadas = getBooleanValueOrgaosInformacaoLatitudeLongitude(valorVindoDaApresentacaoCoordenadas);
		orgaoFiltro.setOrgaosComLatitudeLongitude(booleanValueOrgaosComCoordenadas);

		// ERRO INFORMACAO
		String valorVindoDaApresentacaoErro = getFiltroDlgCarregarOrgaosErro();
		Boolean booleanValueOrgaosComErro = getBooleanValueOrgaosComErro(valorVindoDaApresentacaoErro);
		orgaoFiltro.setOrgaosComErro(booleanValueOrgaosComErro);

		// SITUACAO DO REGISTRO
		String valorVindoDaApresentacaoSituacaoRegistro = getFiltroDlgCarregarOrgaosSituacaoRegistro();
		String stringValueOrgaosSituacaoRegistro = getStringValueOrgaosSituacaoRegistro(valorVindoDaApresentacaoSituacaoRegistro);
		orgaoFiltro.setOrgaosSituacaoRegistro(stringValueOrgaosSituacaoRegistro);

		if(isFiltroInformado(orgaoFiltro)){
			setFiltroDlgOrgaosPesquisa(orgaoServico.recuperarOrgaos(orgaoFiltro));
		}

	}

	private boolean isFiltroInformado(OrgaoDTO orgaoFiltro) {
		boolean isFiltroPreenchido = true;
		if(orgaoFiltro == null){
			isFiltroPreenchido = false;
		}else{
			if((orgaoFiltro.getNome() == null || orgaoFiltro.getNome().trim().equals("")) &&
					orgaoFiltro.getMunicipio() == null &&
					orgaoFiltro.getTipoOrgao() == null &&
					orgaoFiltro.getUf() == null &&
					orgaoFiltro.getTema() == null &&
					orgaoFiltro.getOrgaosComLatitudeLongitude() == null &&
					orgaoFiltro.getOrgaosComErro() == null &&
					orgaoFiltro.getOrgaosSituacaoRegistro() == null){
				isFiltroPreenchido = false;
			}
		}

		return isFiltroPreenchido;
	}

	public void setTemasList(DualListModel<Tema> temasList) {
		this.temasList = temasList;
	}

	public void setServicosList(DualListModel<Servico> servicosList) {
		this.servicosList = servicosList;
	}

	public String getTermoPesquisa() {
		return termoPesquisa;
	}

	public void setTermoPesquisa(String termoPesquisa) {
		this.termoPesquisa = termoPesquisa;
	}

	public String getCarregarOrgaosInformacaoLatitudeLongitude() {
		return carregarOrgaosInformacaoLatitudeLongitude;
	}

	public void setCarregarOrgaosInformacaoLatitudeLongitude(
			String carregarOrgaosInformacaoLatitudeLongitude) {
		this.carregarOrgaosInformacaoLatitudeLongitude = carregarOrgaosInformacaoLatitudeLongitude;
	}

	public String getCarregarOrgaosSituacaoRegistro() {
		return carregarOrgaosSituacaoRegistro;
	}

	public void setCarregarOrgaosSituacaoRegistro(
			String carregarOrgaosSituacaoRegistro) {
		this.carregarOrgaosSituacaoRegistro = carregarOrgaosSituacaoRegistro;
	}

	public String getCarregarOrgaosErro() {
		return carregarOrgaosErro;
	}

	public void setCarregarOrgaosErro(String carregarOrgaosErro) {
		this.carregarOrgaosErro = carregarOrgaosErro;
	}

	public UF getUf() {
		return uf;
	}

	public void setUf(UF uf) {
		this.uf = uf;
	}

	public Municipio getMunicipio() {
		return municipio;
	}

	public void setMunicipio(Municipio municipio) {
		this.municipio = municipio;
	}

	public TipoOrgao getTipoOrgao() {
		return tipoOrgao;
	}

	public void setTipoOrgao(TipoOrgao tipoOrgao) {
		this.tipoOrgao = tipoOrgao;
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

	public List<Representante> getRepresentantes() {
		return representantes;
	}

	public void setRepresentantes(List<Representante> representantes) {
		this.representantes = representantes;
	}

	public Representante getRepresentante() {
		return representante;
	}

	public void setRepresentante(Representante representante) {
		this.representante = representante;
	}

	public AtlasLazyDataModel<Orgao> getOrgaosPesquisa() throws AtlasAcessoBancoDadosException{
		if (orgaosPesquisa == null) {
			carregarTermos(null);
		}
		return orgaosPesquisa;
	}

	public List<TipoSituacaoRegistroOrgao> getSituacoes(){
		return TipoSituacaoRegistroOrgao.getValuesAsList();
	}

	@SuppressWarnings("unchecked")
	public List<TipoOrgao> getTiposOrgaos()
			throws AtlasAcessoBancoDadosException {
		return orgaoServico.buscarTodos(TipoOrgao.class, "nome");
	}
	@SuppressWarnings("unchecked")
	public List<Tema> getTemas()
			throws AtlasAcessoBancoDadosException {
		return temaServico.buscarTodos(Tema.class, "nome");
	}
	@SuppressWarnings("unchecked")
	public List<Servico> getServicos()
			throws AtlasAcessoBancoDadosException {
		return servicoServico.buscarTodos(Servico.class, "nome");
	}
	@SuppressWarnings("unchecked")
	public List<UF> getUfs() throws AtlasAcessoBancoDadosException {
		return orgaoServico.buscarTodos(UF.class, "nome");
	}

	@SuppressWarnings("unchecked")
	public List<Cargo> getCargos() throws AtlasAcessoBancoDadosException {
		return cargoServico.buscarTodos(Cargo.class, "nome");
	}

	public List<Municipio> getMunicipios()
			throws AtlasAcessoBancoDadosException {
		if (getUf() != null) {
			return municipioServico.recuperarMunicipios(getUf());
		} else {
			return null;
		}
	}

	public void pesquisarTermo() throws AtlasAcessoBancoDadosException {
		if (this.termoPesquisa != null && !this.termoPesquisa.trim().isEmpty()) {
			carregarTermos("%" + this.termoPesquisa + "%");
		} else {
			carregarTermos(null);
		}
	}

	private Boolean getBooleanValueOrgaosInformacaoLatitudeLongitude(
			String valorVindoDaApresentacaoCoordenadas) {
		valorVindoDaApresentacaoCoordenadas = getFiltroInformacao();
		if (valorVindoDaApresentacaoCoordenadas == null)
			return null;

		if (valorVindoDaApresentacaoCoordenadas.equals("S"))
			return false;

		if (valorVindoDaApresentacaoCoordenadas.equals("C"))
			return true;

		return null;
	}

	private Boolean getBooleanValueOrgaosComErro(
			String valorVindoDaApresentacaoErro) {
		if (valorVindoDaApresentacaoErro == null)
			return null;

		if (valorVindoDaApresentacaoErro.equals("S"))
			return false;

		if (valorVindoDaApresentacaoErro.equals("C"))
			return true;

		return null;
	}

	private String getStringValueOrgaosSituacaoRegistro(
			String valorVindoDaApresentacaoSituacaoRegistro) {
		if (valorVindoDaApresentacaoSituacaoRegistro == null)
			return null;

		if (valorVindoDaApresentacaoSituacaoRegistro.equals("")) // QUALQUER
			return null;

		return valorVindoDaApresentacaoSituacaoRegistro;
	}

	public void carregarTermos(String textoParcial) throws AtlasAcessoBancoDadosException {
		orgaosPesquisa = new AtlasLazyDataModel<Orgao>();
		orgaosPesquisa.setServico(orgaoServico);
		orgaosPesquisa.setMetodo("recuperarOrgaosPaginados");

		// FILTRO, DTO
		orgaoFiltro = new OrgaoDTO();

		// FILTROS MAIS COMUNS
		orgaoFiltro.setNome(textoParcial);
		orgaoFiltro.setMunicipio(municipio);
		orgaoFiltro.setTipoOrgao(tipoOrgao);
		orgaoFiltro.setUf(uf);
		orgaoFiltro.setTema(tema);
		orgaoFiltro.setServico(servico);

		// COORDENADAS GEOGRAFICAS
		String valorVindoDaApresentacaoCoordenadas = getCarregarOrgaosInformacaoLatitudeLongitude();
		Boolean booleanValueOrgaosComCoordenadas = getBooleanValueOrgaosInformacaoLatitudeLongitude(valorVindoDaApresentacaoCoordenadas);
		orgaoFiltro
		.setOrgaosComLatitudeLongitude(booleanValueOrgaosComCoordenadas);

		// ERRO INFORMACAO
		String valorVindoDaApresentacaoErro = getCarregarOrgaosErro();
		Boolean booleanValueOrgaosComErro = getBooleanValueOrgaosComErro(valorVindoDaApresentacaoErro);
		orgaoFiltro.setOrgaosComErro(booleanValueOrgaosComErro);

		// SITUACAO DO REGISTRO
		String valorVindoDaApresentacaoSituacaoRegistro = getCarregarOrgaosSituacaoRegistro();
		String stringValueOrgaosSituacaoRegistro = getStringValueOrgaosSituacaoRegistro(valorVindoDaApresentacaoSituacaoRegistro);
		orgaoFiltro
		.setOrgaosSituacaoRegistro(stringValueOrgaosSituacaoRegistro);

		orgaosPesquisa.setParametros(orgaoFiltro);
		efetuarPesquisa(orgaoFiltro);
	}

	public void excluir(Representante representante){
		representantes.remove(representante);
	}

	public void adicionarRepresentanteAction(){
		if(validaRepresentante(representante)){
			if(representantes == null){
				representantes = new ArrayList<>();
			}
			representantes.add(representante);
			representante = new Representante();
		}
	}

	private boolean validaRepresentante(Representante representante) {
		representanteValido = true;
		Severity messageType = FacesMessage.SEVERITY_WARN;
		String cabecalho = "ATENÇÃO";
		if(representante != null){
			
			representanteValido = true;
			boolean emailValido = true;
			if (representante.getNome() == null || representante.getNome().trim().equals("")) {
				representanteValido = false;
				addMessage("", messageType, cabecalho+" Nome do representante é obrigatório!");
				
			}
	
			if (representante.getCargo() == null||(representante.getCargo().getNome() == null | representante.getCargo().getNome().trim().equals(""))) {
				representanteValido = false;
				addMessage("", messageType, cabecalho+" Cargo do representante é obrigatório!");
			}
	
			if (representante.getEmail() != null && !representante.getEmail().isEmpty()) {
				if (!Valida.validaEmail(representante.getEmail())) {
					addMessage("", messageType, cabecalho+" Email Invalido!");
					emailValido = false;
				}
			}
			
			
			if (!emailValido) {
				representanteValido = emailValido;
			}
			if(!representanteValido){
			}
			
			return representanteValido;
	
		}
		return true;
	}

	protected void efetuarPesquisa(OrgaoDTO orgaoFiltro) throws AtlasAcessoBancoDadosException {
		ListaPaginada<Orgao> listaPaginada = new ListaPaginada<Orgao>();
		listaPaginada.setQtdRegistroPorPagina(100);
		orgaosPesquisa.setListaPaginada(orgaoServico.recuperarOrgaosPaginados(
				orgaoFiltro, null, null, listaPaginada));
	}

	@SuppressWarnings("unchecked")
	public final String proximoPorFiltro() throws AtlasAcessoBancoDadosException{
		if(!isFiltroPreenchido()){
			addMessage(null, FacesMessage.SEVERITY_WARN, "Atenção! Informe pelo menos um filtro para realizar a operação!");
			return getTelaConsulta();
		}else{
			dadosEmBlocoDTO = new DadosEmBlocoDTO();
			dadosEmBlocoDTO.setOrgaosSelecionados(null);
			dadosEmBlocoDTO.setOrgaoFiltro(orgaoFiltro);
			representante = new Representante();
			representantes = new ArrayList<>();
			temasList = new DualListModel<>(temaServico.buscarTodos(Tema.class,"nome"), new ArrayList<Tema>());
			servicosList = new DualListModel<>(servicoServico.buscarTodos(Servico.class, "nome"), new ArrayList<Servico>());
			orgaosSelecionados = new ArrayList<>();
			orgaosSelecaoPagina = new HashMap<>();
			return getTelaCadastro();
		}
	}

	private boolean isFiltroPreenchido() {
		if(	orgaoFiltro != null &&
				orgaoFiltro.getMunicipio() == null &&
				(orgaoFiltro.getNome() == null || orgaoFiltro.getNome().trim().equals("")) &&
				orgaoFiltro.getTipoOrgao() == null &&
				orgaoFiltro.getTema() == null &&
				orgaoFiltro.getServico() == null &&
				orgaoFiltro.getOrgaosComLatitudeLongitude() == null &&
				orgaoFiltro.getOrgaosComErro() == null &&
				orgaoFiltro.getUf() == null){
			return false;
		}
		return true;
	}

	@SuppressWarnings("unchecked")
	public final String proximo() throws AtlasAcessoBancoDadosException {
		atribuirOrgaosPaginaAtual(null);
		if(isOrgaoSelecaoPaginaPreenchido()){
			dadosEmBlocoDTO = new DadosEmBlocoDTO();
			dadosEmBlocoDTO.setOrgaosSelecionados(recuperarOrgaosSelecionadosNasPaginas());
			dadosEmBlocoDTO.setOrgaoFiltro(null);
			representante = new Representante();
			representantes = new ArrayList<>();
			temasList = new DualListModel<>(temaServico.buscarTodos(Tema.class,"nome"), new ArrayList<Tema>());
			servicosList = new DualListModel<>(servicoServico.buscarTodos(Servico.class,"nome"), new ArrayList<Servico>());
			orgaosSelecionados = new ArrayList<>();
			orgaosSelecaoPagina = new HashMap<>();
			return getTelaCadastro();
		}else{
			addMessage(null, FacesMessage.SEVERITY_WARN, "Atenção! Selecione pelo menos um órgão para alteração!");
			return getTelaConsulta();
		}
	}

	private void atribuirOrgaosPaginaAtual(Integer numPaginaAnt) {
		Integer numPaginaAux = (numPaginaAnt == null) ? numPagina : numPaginaAnt;
		if (orgaosSelecionados != null && !orgaosSelecionados.isEmpty()) {
			orgaosSelecaoPagina.put(numPaginaAux, orgaosSelecionados);
		}
	}

	private List<Orgao> recuperarOrgaosSelecionadosNasPaginas() {
		List<Orgao> orgaos = new ArrayList<>();
		if(orgaosSelecaoPagina != null && !orgaosSelecaoPagina.isEmpty()){
			for (Integer numPagina : orgaosSelecaoPagina.keySet()) {
				orgaos.addAll(orgaosSelecaoPagina.get(numPagina));
			}
		}
		return orgaos;
	}

	private boolean isOrgaoSelecaoPaginaPreenchido() {
		boolean isOrgaosPaginaPreenchido = (orgaosSelecaoPagina != null && !orgaosSelecaoPagina.isEmpty() && orgaosSelecaoPagina.size() > 0);
		if(isOrgaosPaginaPreenchido){
			List<Boolean> orgaosMarcados = new ArrayList<>();
			Set<Integer> chaves = orgaosSelecaoPagina.keySet();
			for (Integer chave : chaves) {
				List<Orgao> orgaos = orgaosSelecaoPagina.get(chave);
				if(orgaos != null && !orgaos.isEmpty()){
					orgaosMarcados.add(true);
				}
			}
			isOrgaosPaginaPreenchido = !orgaosMarcados.isEmpty();
		}
		return isOrgaosPaginaPreenchido;
	}

	public final String proximoConfirma(){
		if(!validaRepresentantes(representantes)){
			return getTelaCadastro();
		}
		dadosEmBlocoDTO.setTemas(getTemasList().getTarget());
		dadosEmBlocoDTO.setServicos(getServicosList().getTarget());
		dadosEmBlocoDTO.setRepresentantes(representantes);
		dadosEmBlocoDTO.setOrgaosSelecionadosResumo(getOrgaosSelecionadosResumo());
		if(isDadoInformado()){
			if(!isEmailValido()){
				return getTelaCadastro();
			}
			addMessage("Clique em CONFIRMA para realizar a alteração!", FacesMessage.SEVERITY_WARN, "Atenção!");
			return getTelaConfirma();
		}
		addMessage("Informe algum dado básico ou um representante para prosseguir!", FacesMessage.SEVERITY_WARN, "Atenção!");
		return getTelaCadastro();
	}

	private boolean validaRepresentantes(List<Representante> representantes) {
		if(representantes != null && !representantes.isEmpty()){
			for (Representante representante : representantes) {
				if(!validaRepresentante(representante)){
					return false;
				}
			}
		}
		return true;
	}

	private List<Orgao> getOrgaosSelecionadosResumo() {
		List<Orgao> orgaosAtualizados = new ArrayList<>();
		String sigla = dadosEmBlocoDTO.getSigla().trim();
		String descricao = dadosEmBlocoDTO.getDescricao().trim();
		TipoOrgao tipo = dadosEmBlocoDTO.getTipoOrgao();
		String homePage = dadosEmBlocoDTO.getHomePage().trim();
		String emailInstitucionalInformado = dadosEmBlocoDTO.getEmail().trim();
		EmailUsuario emailUsuario = null;
		List<EmailUsuario> emails = new ArrayList<>();
		if(emailInstitucionalInformado != null && !emailInstitucionalInformado.trim().equals("")){
			emailUsuario = new EmailUsuario();
			emailUsuario.setNome(emailInstitucionalInformado);
			emailUsuario.setAsInstitucional();
			emails.add(emailUsuario);
		}
		if(dadosEmBlocoDTO.getOrgaosSelecionados() != null){
			for (Orgao orgaoSelecionado : dadosEmBlocoDTO.getOrgaosSelecionados()) {
				Orgao orgaoAtualizado = new Orgao();
				orgaoAtualizado.setSigla((sigla.equals("")) ? orgaoSelecionado.getSigla() : sigla);
				orgaoAtualizado.setNome(orgaoSelecionado.getNome());
				orgaoAtualizado.setDescricao((descricao.equals("")) ? orgaoSelecionado.getDescricao() : descricao);
				orgaoAtualizado.setTipoOrgao((tipo == null) ? orgaoSelecionado.getTipoOrgao() : tipo);
				orgaoAtualizado.setEmails(emails.isEmpty() ? orgaoSelecionado.getEmails() : emails);
				orgaoAtualizado.setHomePage((homePage.equals("")) ? orgaoSelecionado.getHomePage() : homePage);
				orgaosAtualizados.add(orgaoAtualizado);
			}
		}

		return orgaosAtualizados;
	}

	private boolean isEmailValido() {
		if(dadosEmBlocoDTO != null){
			if (dadosEmBlocoDTO.getEmail() != null && !dadosEmBlocoDTO.getEmail().trim().equals("")) {
				if (!Valida.validaEmail(dadosEmBlocoDTO.getEmail())) {
					addMessage("E-mail do órgão inválido!", FacesMessage.SEVERITY_ERROR, "Atenção!");
					return false;
				}
			}
		}
		return true;
	}

	private boolean isDadoInformado() {
		if(dadosEmBlocoDTO != null){
			if((dadosEmBlocoDTO.getSigla() == null || dadosEmBlocoDTO.getSigla().isEmpty()) &&
					dadosEmBlocoDTO.getTipoOrgao() == null &&
					(dadosEmBlocoDTO.getDescricao() == null || dadosEmBlocoDTO.getDescricao().isEmpty()) &&
					(dadosEmBlocoDTO.getHomePage() == null || dadosEmBlocoDTO.getHomePage().isEmpty()) &&
					(dadosEmBlocoDTO.getEmail() == null || dadosEmBlocoDTO.getEmail().isEmpty()) &&
					(dadosEmBlocoDTO.getRepresentantes() == null || dadosEmBlocoDTO.getRepresentantes().isEmpty()) &&
					(dadosEmBlocoDTO.getTemas() == null || dadosEmBlocoDTO.getTemas().isEmpty())&&
					(dadosEmBlocoDTO.getServicos() == null || dadosEmBlocoDTO.getServicos().isEmpty()) &&
					(dadosEmBlocoDTO.getOrgaoSuperior() == null)){
				return false;
			}
		}
		return true;
	}

	public final String voltar() throws AtlasAcessoBancoDadosException{
		return getTelaConsulta();
	}

	private String getTelaConsulta(){
		return "exibirDadosEmBloco";
	}

	private String getTelaCadastro(){
		return "informarDadosEmBloco";
	}

	private String getTelaConfirma(){
		return "confirmaDadosEmBloco";
	}

	public final String voltarCadastro(){
		return getTelaCadastro();
	}

	public void addMessage(String mensagem, Severity tipoMensagem, String cabecalho) {
		FacesContext context = FacesContext.getCurrentInstance();
		context.addMessage(null, new FacesMessage(tipoMensagem, cabecalho,  mensagem) );
	}

	public void confirma() throws AtlasAcessoBancoDadosException {
		int qtdRegistrosAlterados = orgaoServico.salvarEmBloco(dadosEmBlocoDTO);
		StringBuilder sb = new StringBuilder(qtdRegistrosAlterados+" registro(s) alterado(s)!");
		addMessage(sb.toString(), FacesMessage.SEVERITY_INFO, "Operação realizada com sucesso!");
	}

	private void inicializarMapOrgaosSelecao() {
		if (orgaosSelecaoPagina.keySet().isEmpty()
				|| !orgaosSelecaoPagina.keySet().contains(numPagina)) {
			if(numPagina == null){
				numPagina = 0;
			}
			orgaosSelecaoPagina.put(numPagina, new ArrayList<Orgao>());
		}
	}

	public void onRowSelect(SelectEvent event){
		inicializarMapOrgaosSelecao();
		Orgao orgaoAdicionado = ((Orgao)event.getObject());
		List<Orgao> listOrgao = orgaosSelecaoPagina.get(numPagina);
		if(!listOrgao.contains(orgaoAdicionado)){
			listOrgao.add(orgaoAdicionado);
		}
	}

	public void onRowUnselect(UnselectEvent event){
		inicializarMapOrgaosSelecao();
		Orgao orgaoRemovido = ((Orgao)event.getObject());
		List<Orgao> listOrgao = orgaosSelecaoPagina.get(numPagina);
		if(listOrgao.contains(orgaoRemovido)){
			listOrgao.remove(orgaoRemovido);
		}
	}

	public void onToggleSelect(ToggleSelectEvent event){
		inicializarMapOrgaosSelecao();
		List<Orgao> listOrgao = orgaosSelecaoPagina.get(numPagina);
		if(event.isSelected()){
			for (Orgao orgaoSelecionado : orgaosSelecionados) {
				if(!listOrgao.contains(orgaoSelecionado)){
					listOrgao.add(orgaoSelecionado);
				}
			}
		}else{
			orgaosSelecaoPagina.get(numPagina).removeAll(listOrgao);
		}
	}

	public void onPageChange(PageEvent event){
		atribuirOrgaosPaginaAtual(numPaginaAnterior);

		numPagina = event.getPage();
		inicializarMapOrgaosSelecao();
		// Marca os órgãos da página que está sendo exibida
		if (orgaosSelecaoPagina.get(numPagina) != null
				&& !orgaosSelecaoPagina.get(numPagina).isEmpty()) {
			orgaosSelecionados = orgaosSelecaoPagina.get(numPagina);
		}

		numPaginaAnterior = numPagina;

		System.out.println("Pagina: "+numPagina);
		for (Orgao orgao : orgaosSelecionados) {
			System.out.println("Orgao Selecionado: "+orgao.getId());
		}
		for (Integer pagina : orgaosSelecaoPagina.keySet()) {
			for (Orgao orgao : orgaosSelecaoPagina.get(pagina)) {
				System.out.println(pagina+" - "+orgao.getId());
			}
		}
	}

	public void onCellEdit(CellEditEvent event) {
		Object oldValue = event.getOldValue();
		Object newValue = event.getNewValue();

		if(newValue != null && !newValue.equals("") && !newValue.equals(oldValue)) {
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Célula editada. Valor antigo: "+oldValue+", valor novo: "+newValue, "");
			FacesContext.getCurrentInstance().addMessage(null, msg);
		}
	}
}