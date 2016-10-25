package br.gov.atlas.bean;

import java.math.BigDecimal;
import java.net.ConnectException;
import java.sql.Connection;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.AjaxBehaviorEvent;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import org.primefaces.context.RequestContext;
import org.primefaces.event.CellEditEvent;
import org.primefaces.event.map.MarkerDragEvent;
import org.primefaces.event.map.OverlaySelectEvent;
import org.primefaces.model.DualListModel;
import org.primefaces.model.UploadedFile;
import org.primefaces.model.map.DefaultMapModel;
import org.primefaces.model.map.MapModel;
import org.primefaces.model.map.Marker;

import br.gov.atlas.component.model.AtlasLazyDataModel;
import br.gov.atlas.controller.PageController;
import br.gov.atlas.dto.OrgaoDTO;
import br.gov.atlas.entidade.Cargo;
import br.gov.atlas.entidade.EmailUsuario;
import br.gov.atlas.entidade.Municipio;
import br.gov.atlas.entidade.Orgao;
import br.gov.atlas.entidade.Perfil;
import br.gov.atlas.entidade.Representante;
import br.gov.atlas.entidade.Servico;
import br.gov.atlas.entidade.Telefone;
import br.gov.atlas.entidade.Tema;
import br.gov.atlas.entidade.TipoOrgao;
import br.gov.atlas.entidade.UF;
import br.gov.atlas.entidade.Usuario;
import br.gov.atlas.enums.AmbitoAtuacaoOrgao;
import br.gov.atlas.enums.TipoSituacaoRegistroOrgao;
import br.gov.atlas.exception.AtlasAcessoBancoDadosException;
import br.gov.atlas.exception.AtlasNegocioException;
import br.gov.atlas.seguranca.bean.AutenticarUsuarioBean;
import br.gov.atlas.servico.AtlasServicoImpl;
import br.gov.atlas.servico.ConexaoServico;
import br.gov.atlas.servico.EmailServico;
import br.gov.atlas.servico.ManterCargoServico;
import br.gov.atlas.servico.ManterServicoServico;
import br.gov.atlas.servico.ManterTemaServico;
import br.gov.atlas.servico.ManterTematresServico;
import br.gov.atlas.servico.ManterTipoOrgaoServico;
import br.gov.atlas.servico.ManterUsuarioServico;
import br.gov.atlas.servico.MunicipioServico;
import br.gov.atlas.servico.OrgaoServico;
import br.gov.atlas.servico.RepresentanteServico;
import br.gov.atlas.servico.TelefoneServico;
import br.gov.atlas.servico.UsuarioServico;
import br.gov.atlas.servico.WebMailServico;
import br.gov.atlas.util.AtlasWebUtil;
import br.gov.atlas.util.GeoProcessamento;
import br.gov.atlas.util.RelatorioUtil;
import br.gov.atlas.util.Valida;

@ManagedBean(name = "manterOrgaoBean")
@SessionScoped
public class ManterOrgaoBean extends AtlasCrudBean<Orgao> {

	private static final long serialVersionUID = 8547104624905661929L;

	@EJB(name = "OrgaoServico")
	private OrgaoServico orgaoServico;

	@EJB(name = "MunicipioServico")
	private MunicipioServico municipioServico;

	@EJB(name = "ManterTipoOrgaoServico")
	private ManterTipoOrgaoServico tipoOrgaoServico;

	@EJB(name = "UsuarioServico")
	private UsuarioServico usuarioServico;

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

	@EJB(name = "ConexaoServico")
	private ConexaoServico conexaoServico;

	@EJB(name = "WebMailServico")
	private WebMailServico webMailServico;

	@EJB(name = "ManterCargoServico")
	ManterCargoServico cargoServico;

	private Usuario usuarioAutenticado = ((PageController) getBean("pageController")).getUsuario();

	private DualListModel<Tema> temasList;

	private String[] parametrosPesquisaMapa;

	private String termoPesquisa;

	private AtlasLazyDataModel<Orgao> orgaos;

	private UploadedFile file;

	private OrgaoDTO orgaoFiltro;

	private UF ufCadastro;

	private UF ufPesquisa;

	private Municipio municipioCadastro;

	private Municipio municipioPesquisa;

	private TipoOrgao tipoOrgao;

	private AmbitoAtuacaoOrgao ambitoAtuacaoOrgao;

	private Tema tema;

	private Servico servicoOrgao;

	private String carregarOrgaosErro;

	private String carregarOrgaosInformacaoLatitudeLongitude;

	private String carregarOrgaosSituacaoRegistro;

	private String formatoSelecionado;

	private Representante representanteEmFoco;

	private Servico servicoEmFoco;

	private List<Representante> listaComRepresentantes;

	private List<Servico> servicosParaSelecao;

	private List<Servico> servicosSelecionados;

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

	private boolean erroRepresentante = false;

	private boolean valido = true;

	private StringBuffer campoObrigatorio2 = new StringBuffer();

	private GeoProcessamento geoProcessamento = new GeoProcessamento();

	private MapModel draggableModel;

	private Marker marker;

	public static String PESQUISA_ENDERECO = "E";

	public static String PESQUISA_CEP = "C";

	public static String PESQUISA_NOME = "N";

	private boolean enderecoAlterado;

	private List<AmbitoAtuacaoOrgao> listaAmbitoAtuacao;

	private static final Integer ZOOM = 8;

	public ManterOrgaoBean() {
		limpar();
	}

	public void limpar() {
		setEntidade(new Orgao());
		this.listaComRepresentantes = new ArrayList<>();
		this.representanteEmFoco = new Representante();
		setServicoEmFoco(new Servico());
		setServicosParaSelecao(new ArrayList<Servico>());
		setServicosSelecionados(new ArrayList<Servico>());
		getEntidade().setIndicadorSituacao(
				TipoSituacaoRegistroOrgao.EM_ANALISE.indiceSituacao());

		this.setUfPesquisa(null);
		this.setUfCadastro(null);
		this.setMunicipioPesquisa(null);
		this.setMunicipioCadastro(null);
		this.termoPesquisa = null;
		this.tipoOrgao = null;
		this.ambitoAtuacaoOrgao = null;
		this.tema = null;
		this.servicoOrgao = null;
		this.carregarOrgaosErro = ""; // ANTES HAVIA "S"
		this.carregarOrgaosInformacaoLatitudeLongitude = ""; // ANTES HAVIA "A"
		this.carregarOrgaosSituacaoRegistro = ""; // ANTES HAVIA "1"
		this.parametrosPesquisaMapa = null;
	}

	public void limparPesquisar() throws AtlasAcessoBancoDadosException {
		limpar();
		carregarTermos(null);
	}

	public boolean isEstahIncluindo() {
		Orgao orgao = getEntidade();
		if (orgao == null || orgao.getId() == null)
			return true;
		return false;
	}

	public boolean isEstahAlterando() {
		return isEstahIncluindo()==false;
	}

	private void inicializarTemas() throws AtlasAcessoBancoDadosException{
		if ( isEstahIncluindo() )
			inicializarTemasQuandoInclusao();

		if ( isEstahAlterando() )
			inicializarTemasQuandoAlteracao();
	}

	@SuppressWarnings("unchecked")
	private void inicializarTemasQuandoInclusao() throws AtlasAcessoBancoDadosException{
		List<Tema> todosOsTemas = temaServico.buscarTodos(Tema.class, "nome");
		this.temasList = new DualListModel<>(todosOsTemas, new ArrayList<Tema>());
	}

	@SuppressWarnings("unchecked")
	private void inicializarTemasQuandoAlteracao() throws AtlasAcessoBancoDadosException{
		Orgao orgao = getEntidade();
		List<Tema> temas = orgaoServico.recuperarTemas(orgao);
		orgao.setTemas(temas);
		List<Tema> temasSelecionados = orgao.getTemas();
		List<Tema>  temasDisponiveis = new ArrayList<>();
		temasDisponiveis = temaServico.buscarTodos(Tema.class,"nome");
		temasDisponiveis.removeAll(temasSelecionados);

		this.temasList = new DualListModel<>(temasDisponiveis, temasSelecionados);
	}

	@Override
	public Boolean validar() {

		valido = true;
		boolean emailValido = true;

		StringBuffer campoObrigatorio = new StringBuffer();

		if (getEntidade().getSigla() == null
				|| getEntidade().getSigla().trim().isEmpty()) {
			valido = false;
			campoObrigatorio.append(campoObrigatorio.toString().trim()
					.isEmpty() ? "Sigla" : ", Sigla");
		}
		if (getEntidade().getTipoOrgao() == null) {
			valido = false;
			campoObrigatorio.append(campoObrigatorio.toString().trim()
					.isEmpty() ? "Tipo do Órgão" : ", Tipo do Órgão");
		}

		if (getEntidade().getIndicadorSituacao().equals("Selecione")
				|| getEntidade().getIndicadorSituacao().trim().isEmpty()
				|| getEntidade().getIndicadorSituacao().equals("")) {
			valido = false;
			campoObrigatorio.append(campoObrigatorio.toString().trim()
					.isEmpty() ? "Situação do Registro" : ", Situação do Registro");
		}

		if (getEntidade().getNome() == null
				|| getEntidade().getNome().trim().isEmpty()) {
			valido = false;
			campoObrigatorio.append(campoObrigatorio.toString().trim()
					.isEmpty() ? "Nome" : ", Nome");
		}
		if (getEntidade().getDescricao() == null
				|| getEntidade().getDescricao().trim().isEmpty()) {
			valido = false;
			campoObrigatorio.append(campoObrigatorio.toString().trim()
					.isEmpty() ? "Descrição" : ", Descrição");
		}

		if (getUfCadastro() == null) {
			valido = false;
			campoObrigatorio.append(campoObrigatorio.toString().trim()
					.isEmpty() ? "Estado" : ", Estado");
		}

		if (getUfCadastro() != null && getMunicipioCadastro() == null) {
			valido = false;
			campoObrigatorio.append(campoObrigatorio.toString().trim()
					.isEmpty() ? "Municipio" : ", Municipio");
		}

		if (getEntidade().getEndereco() == null
				|| getEntidade().getEndereco().trim().isEmpty()) {
			valido = false;
			campoObrigatorio.append(campoObrigatorio.toString().trim()
					.isEmpty() ? "Endereço" : ", Endereço");
		}

		if (getEntidade().getBairro() == null
				|| getEntidade().getBairro().trim().isEmpty()) {
			valido = false;
			campoObrigatorio.append(campoObrigatorio.toString().trim()
					.isEmpty() ? "Bairro" : ", Bairro");
		}
		if (getEntidade().getCep() == null || getEntidade().getCep().equals("")) {
			valido = false;
			campoObrigatorio.append(campoObrigatorio.toString().trim()
					.isEmpty() ? "CEP" : ", CEP");
		}
		if (!getEntidade().getDescricaoEmailInstitucional().isEmpty()) {
			if (!Valida.validaEmail(getEntidade()
					.getDescricaoEmailInstitucional())) {
				error(null, "MSG8");
				emailValido = false;
			}
		}

		if (!valido) {
			error(null, "MSG20", campoObrigatorio.toString());
		}

		if (!emailValido) {
			valido = emailValido;
		}

		return valido;
	}

	@Override
	public final String editar(Integer idOrgao) {
		try {
			setEntidade(orgaoServico.buscarPorIdLazyLoad(idOrgao));
			initCarregarEntidade();

		} catch (AtlasAcessoBancoDadosException e) {
			error(e);
		}
		return "editarOrgao";
	}

	@Override
	protected void initCarregarEntidade() throws AtlasAcessoBancoDadosException {
		this.parametrosPesquisaMapa = null;
		this.ufCadastro = getEntidade().getMunicipio().getUf();
		this.municipioCadastro = getEntidade().getMunicipio();
		this.listaComRepresentantes = carregarRepresentantes();
		draggableModel = new DefaultMapModel();
		geoProcessamento.adicionarMarcador(getEntidade(), draggableModel);
		permitirArrastarPontoNoMapa();

		List<Telefone> listaComTelefones = orgaoServico
				.recuperarTelefones(getEntidade());
		if (listaComTelefones != null) {
			if (listaComTelefones.size() >= 1)
				getEntidade().setPrimeiroTelefoneAsObject(
						listaComTelefones.get(0));

			if (listaComTelefones.size() >= 2)
				getEntidade().setSegundoTelefoneAsObject(
						listaComTelefones.get(1));
		}

		//Carregando os temas
		inicializarTemas();
	}

	private List<Representante> carregarRepresentantes()
			throws AtlasAcessoBancoDadosException {
		Orgao orgao = getEntidade();
		List<Representante> representantes = representanteServico
				.buscarTodos(orgao);
		return representantes;
	}

	@Override
	protected AtlasServicoImpl getServico() {
		return orgaoServico;
	}

	@Override
	protected void efetuarPesquisa() throws AtlasAcessoBancoDadosException {
		orgaos.setListaPaginada(orgaoServico.recuperarOrgaosPaginados(
				orgaoFiltro, null, null));
	}

	public void pesquisarTermo() throws AtlasAcessoBancoDadosException {
		if (this.termoPesquisa != null && !this.termoPesquisa.trim().isEmpty()) {
			carregarTermos("%" + this.termoPesquisa + "%");
		} else {
			carregarTermos(null);
		}
	}

	public void carregarTermos(String textoParcial)
			throws AtlasAcessoBancoDadosException {
		orgaos = new AtlasLazyDataModel<Orgao>();
		orgaos.setServico(orgaoServico);
		orgaos.setMetodo("recuperarOrgaosPaginados");

		// FILTRO, DTO
		orgaoFiltro = new OrgaoDTO();

		// FILTROS MAIS COMUNS
		orgaoFiltro.setNome(textoParcial);
		orgaoFiltro.setMunicipio(getMunicipioPesquisa());
		orgaoFiltro.setTipoOrgao(tipoOrgao);
		orgaoFiltro.setAmbitoAtuacaoOrgao(ambitoAtuacaoOrgao);
		orgaoFiltro.setUf(getUfPesquisa());
		orgaoFiltro.setTema(tema);
		orgaoFiltro.setServico(servicoOrgao);
		orgaoFiltro.setTipoSituacaoRegistro(TipoSituacaoRegistroOrgao
				.create(getCarregarOrgaosSituacaoRegistro()));

		// ÓRGÃOS DO USUÁRIO
		if(!ManterUsuarioServico.isUsuarioMaster(usuarioAutenticado)){
			List<Orgao> orgaosUsuario = orgaoServico.recuperarOrgaosRelacionadosESubordinados(usuarioAutenticado);
			orgaoFiltro.setOrgaos(converterOrgaosDTO(orgaosUsuario));
		}

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

		orgaos.setParametros(orgaoFiltro);
		pesquisar();
		limpar();
	}

	private List<OrgaoDTO> converterOrgaosDTO(List<Orgao> orgaosUsuario) {
		List<OrgaoDTO> orgaosDTO = new ArrayList<>();
		if(orgaosUsuario != null && !orgaosUsuario.isEmpty()){
			for (Orgao orgaoUsuario : orgaosUsuario) {
				OrgaoDTO orgaoDTO = new OrgaoDTO();
				orgaoDTO.setId(orgaoUsuario.getId());
				orgaoDTO.setNome(orgaoUsuario.getNome());
				orgaoDTO.setTipoOrgao(orgaoUsuario.getTipoOrgao());
				orgaoDTO.setMunicipio(orgaoUsuario.getMunicipio());
				orgaoDTO.setUf(orgaoUsuario.getUf());
				orgaosDTO.add(orgaoDTO);
			}
		}
		return orgaosDTO;
	}

	private Boolean getBooleanValueOrgaosInformacaoLatitudeLongitude(
			String valorVindoDaApresentacaoCoordenadas) {
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


	public Boolean validarPesquisa() {

		valido = true;
		campoObrigatorio2 = new StringBuffer("");

		if (getEntidade().getSigla() == null
				|| getEntidade().getSigla().trim().isEmpty()) {
			valido = false;
			campoObrigatorio2.append(campoObrigatorio2.toString().trim()
					.isEmpty() ? "Sigla" : ", Sigla");
		}

		if (getUfCadastro() == null) {
			valido = false;
			campoObrigatorio2.append(campoObrigatorio2.toString().trim()
					.isEmpty() ? "Estado" : ", Estado");
		}

		if (getUfCadastro() != null && getMunicipioCadastro() == null) {
			valido = false;
			campoObrigatorio2.append(campoObrigatorio2.toString().trim()
					.isEmpty() ? "Municipio" : ", Municipio");
		}

		if (getEntidade().getEndereco() == null
				|| getEntidade().getEndereco().trim().isEmpty()) {
			valido = false;
			campoObrigatorio2.append(campoObrigatorio2.toString().trim()
					.isEmpty() ? "Endereço" : ", Endereço");
		}

		if (getEntidade().getBairro() == null
				|| getEntidade().getBairro().trim().isEmpty()) {
			valido = false;
			campoObrigatorio2.append(campoObrigatorio2.toString().trim()
					.isEmpty() ? "Bairro" : ", Bairro");
		}
		if (getEntidade().getCep() == null || getEntidade().getCep().equals("")) {
			valido = false;
			campoObrigatorio2.append(campoObrigatorio2.toString().trim()
					.isEmpty() ? "CEP" : ", CEP");
		}

		if (!valido) {
			error(null, "MSG34", campoObrigatorio2.toString());
		}

		return valido;
	}

	public void pesquisarCoordenadasAction(ActionEvent event){

		try {
			if(getEntidade() != null && validarPesquisa()){

				if(parametrosPesquisaMapa != null && parametrosPesquisaMapa.length > 0 ){
					StringBuffer valorUsadoNaPesquisa = new StringBuffer();
					getEntidade().limparCoordenadas();
					geoProcessamento.processarLatitudeLongitude(getEntidade(), parametrosPesquisaMapa, valorUsadoNaPesquisa);
					draggableModel = new DefaultMapModel();
					geoProcessamento.adicionarMarcador(getEntidade(), draggableModel);
					permitirArrastarPontoNoMapa();
					addMessage(null, FacesMessage.SEVERITY_INFO , "Valor pesquisado no google: "+valorUsadoNaPesquisa.toString());
				}
			}else{
				getEntidade().limparCoordenadas();

			}

		} catch (ConnectException | ParseException e) {
			error(null,"MSG0",e.getMessage());
			e.printStackTrace();
		}
	}

	public void alterarFlagEnderecoAlterado(AjaxBehaviorEvent event) {
		enderecoAlterado = true;
	}

	private void permitirArrastarPontoNoMapa() {
		for (Marker premarker : draggableModel.getMarkers()) {
			premarker.setDraggable(true);
		}
	}

	/*
	 *
	 * TODO: Talvéz seja necessario refatorar esta parte do código, pois
	 * coloquei os insertes em casaca. Pode ser que seja necessario colocar
	 * estes insertes na camada de servico/controller
	 *
	 * @see br.gov.atlas.bean.AtlasCrudBean#salvar()
	 */
	@Override
	public String salvar() {
		try{
			if (this.getEntidade().getIndicadorSituacao() != null
					&& this.getEntidade().getIndicadorSituacao().isEmpty()) {
				this.getEntidade().setIndicadorSituacao(
						TipoSituacaoRegistroOrgao.EM_ANALISE.indiceSituacao());
			}

			if((!getEntidade().isIndicadorMarcacaoManual()) && enderecoAlterado){
				getEntidade().limparCoordenadas();
			}

			getEntidade().setMunicipio(getMunicipioCadastro());
			Orgao orgao = getEntidade();

			EmailUsuario emailInstitucional = orgao.recuperarEmailInstitucionalOuCriar();
			emailInstitucional.setOrgao(orgao);
			emailInstitucional.setNome(AtlasWebUtil.estaVazio(getEntidade()
					.getDescricaoEmailInstitucional()) ? null : getEntidade()
							.getDescricaoEmailInstitucional());
			emailInstitucional.setAsInstitucional();
			informarLatitudeLongitude(getEntidade());
			getEntidade().setDataAtualizacao(new Date());
			getEntidade().setMunicipio(getMunicipioCadastro());
			if(getTemasList() != null){
				orgao.setTemas(getTemasList().getTarget());
			}

			super.salvar();

			if (getEntidade().getId() != null) {
				getEntidade().setEmails(
						salvarEmail(emailInstitucional));
				getEntidade().setTelefones(salvarTelefone());
				getEntidade().setRepresentantes(salvarRepresentante(listaComRepresentantes));
				//Envia email para MJ informando cadastro do novo orgao
				webMailServico.sendNovoOrgao("Novo órgão cadastrado: "+orgao.getNome()+", "
						+orgao.getMunicipio().getNome()+" - "
						+orgao.getMunicipio().getUf().getSigla());
			}

			if((!getEntidade().isIndicadorMarcacaoManual()) && enderecoAlterado){
				draggableModel = new DefaultMapModel();
				geoProcessamento.adicionarMarcador(getEntidade(), draggableModel);
				//				updateComponent("tabView:aba-localizacao-geografica");
				//				updateComponent(":form:tabView:aba-localizacao-geografica");
				updateComponent("@page");
			}
			enderecoAlterado = false;
			return "pesquisarOrgao";
		} catch (AddressException e) {
			// Removido por solicitação da Alessandra
//			error(null,"MSG0", "Não foi possível enviar e-mail de aviso sobre o órgão!");
			e.printStackTrace();
		} catch (MessagingException e) {
			error(null,"MSG0",e.getMessage());
			e.printStackTrace();
		} catch (ConnectException e) {
			error(null,"MSG0",e.getMessage());
			e.printStackTrace();
		} catch (ParseException e) {
			error(null,"MSG0",e.getMessage());
			e.printStackTrace();
		} catch (Exception e){
			error(null, "MSG0", e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	public boolean isMostrarAbaErrosInformados() {
		return getEntidade().getProblemas() != null
				&& !getEntidade().getProblemas().isEmpty();
	}

	private void informarLatitudeLongitude(Orgao o) throws ConnectException, ParseException {
		geoProcessamento.processarLatitudeLongitude(o);
	}

	public void onMarkerSelect(OverlaySelectEvent event) {
		marker = (Marker) event.getOverlay();
	}

	public void onMarkerDrag(MarkerDragEvent event) {
		marker = event.getMarker();
		getEntidade().setLatitude(BigDecimal.valueOf(marker.getLatlng().getLat()));
		getEntidade().setLongitude(BigDecimal.valueOf(marker.getLatlng().getLng()));
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Marker Dragged", "Lat:" + marker.getLatlng().getLat() + ", Lng:" + marker.getLatlng().getLng()));
	}

	private List<EmailUsuario> salvarEmail(EmailUsuario emailInstitucional) {

		List<EmailUsuario> listaComEmailInstitucional = new ArrayList<>();
		listaComEmailInstitucional.add(emailInstitucional);
		for (EmailUsuario email : listaComEmailInstitucional) {
			if (email != null) {
				email.setOrgao(getEntidade());
				try {
					emailServico.salvar(email);
				} catch (AtlasAcessoBancoDadosException | AtlasNegocioException e) {
					e.printStackTrace();
				}
			}
		}
		getEntidade().setEmails(listaComEmailInstitucional);

		return listaComEmailInstitucional;
	}

	private List<Telefone> salvarTelefone() {
		try {
			// Removendo a lista de telefones da base
			List<Telefone> listaTelefonesBase = orgaoServico.recuperarTelefones(getEntidade());
			int sizeListTelefone = listaTelefonesBase.size();
			for (int i = 0; i < sizeListTelefone; i++) {
				telefoneServico.remover(listaTelefonesBase.get(i));
			}

			// Adicionando a lista de telefones informada na tela
			List<Telefone> listaComTelefones = new ArrayList<>();
			listaComTelefones.add(getEntidade().getPrimeiroTelefoneAsObject());
			listaComTelefones.add(getEntidade().getSegundoTelefoneAsObject());
			if (!listaComTelefones.isEmpty()) {
				for (Telefone telefone : listaComTelefones) {
					if (telefone != null) {
						telefone.setOrgao(getEntidade());
						telefoneServico.salvar(telefone);
					}
				}
				getEntidade().setTelefones(listaComTelefones);
			}
			return listaComTelefones;
		} catch (AtlasAcessoBancoDadosException
				| AtlasNegocioException e) {
			e.printStackTrace();
		}
		return null;
	}

	private List<Representante> salvarRepresentante(List<Representante> listaComRepresentantes) {
		List<Representante> listaComRepresentantesLocal = new ArrayList<>();
		for (Representante representante : listaComRepresentantes) {
			representante.setOrgao(getEntidade());
			try {
				representanteServico.salvar(representante);
			} catch (AtlasAcessoBancoDadosException e) {
				e.printStackTrace();
			} catch (AtlasNegocioException e) {
				e.printStackTrace();
			}
			listaComRepresentantesLocal.add(representante);
		}

		return listaComRepresentantesLocal;
	}

	public final String novo() throws AtlasAcessoBancoDadosException {
		limparPesquisar();

		this.listaComRepresentantes = new ArrayList<>();
		this.representanteEmFoco = new Representante();
		setEntidade(new Orgao());
		return "informarOrgao";
	}

	@Override
	public String cancelar() {
		limpar();
		try {
			carregarTermos(null);
		} catch (AtlasAcessoBancoDadosException e) {
			e.printStackTrace();
		}
		return super.cancelar();
	}

	public void adicionarRepresentanteAction(ActionEvent event) {
		if (validaRepresentante()) {
			if (listaComRepresentantes == null) {
				listaComRepresentantes = new ArrayList<>();
			}
			if (!listaComRepresentantes.contains(representanteEmFoco)) {
				listaComRepresentantes.add(representanteEmFoco);
			}
			representanteEmFoco = new Representante();

			RequestContext.getCurrentInstance().execute("dlgInformarRepresentante.hide()");
		}
	}

	public void resetDialog() {
        RequestContext.getCurrentInstance().reset("tabView:painelRepresentante");
    }
	
	private boolean validaRepresentante() {
		valido = true;
		boolean emailValido = true;
		StringBuffer campoObrigatorio = new StringBuffer();

		if (representanteEmFoco.getNome() == null || representanteEmFoco.getNome().trim().equals("")) {
			valido = false;
			campoObrigatorio.append(campoObrigatorio.toString().trim()
					.isEmpty() ? "Nome" : ", Nome");
		}

		if (representanteEmFoco.getCargo() == null||(representanteEmFoco.getCargo().getNome() == null | representanteEmFoco.getCargo().getNome().trim().equals(""))) {
			valido = false;
			campoObrigatorio.append(campoObrigatorio.toString().trim()
					.isEmpty() ? "Cargo" : ", Cargo");
		}

		if (representanteEmFoco.getEmail() != null && !representanteEmFoco.getEmail().isEmpty()) {
			if (!Valida.validaEmail(representanteEmFoco.getEmail())) {
				error(null, "MSG8");
				emailValido = false;
			}
		}
		
		if (representanteEmFoco.getSegundoEmail() != null && !representanteEmFoco.getSegundoEmail().isEmpty()) {
			if (!Valida.validaEmail(representanteEmFoco.getSegundoEmail())) {
				error(null, "MSG8");
				emailValido = false;
			}
		}

		if (!valido) {
			error(null, "MSG20", campoObrigatorio.toString());
		}

		if (!emailValido) {
			valido = emailValido;
		}
		return valido;

	}

	public void addMessage(String mensagem, Severity tipoMensagem, String cabecalho) {
		FacesContext context = FacesContext.getCurrentInstance();
		context.addMessage(null, new FacesMessage(tipoMensagem, cabecalho,  mensagem) );
	}

	public void excluirRepresentante(Representante representante){
		listaComRepresentantes.remove(representante);
	}

	public void exportarRelatorio() {
		try {
			if ((getFormatoSelecionado() != null)
					&& (!getFormatoSelecionado().equals("0"))) {
				gerarRelatorioOrgaos(getFormatoSelecionado());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void gerarRelatorioOrgaos(String tipo) throws Exception {
		Connection conn = conexaoServico.conn();
		//List<Orgao> listaDeOrgaos = orgaoServico.recuperarOrgaos(orgaoFiltro);
		if (RelatorioUtil.PDF.equals(tipo)) {
			RelatorioUtil.gerarRelatorio(null,
					"/relatorios/relatorioOrgaosJustica.jasper",
					"relatorioOrgaosJustica.pdf", getParametrosRelatorio(), "pdf",conn, true);
		} else {
			RelatorioUtil.gerarRelatorio(null,
					"/relatorios/relatorioOrgaosJusticaXLS.jasper",
					"relatorioOrgaosJustica.xls", getParametrosRelatorio(), "xls", conn, true);
		}
	}

	public Map<String, Object> getParametrosRelatorio() {
		Map<String, Object> parametros = new HashMap<String, Object>();
		parametros.put("NOME_ORGAO", orgaoFiltro.getNome() == null ? null : orgaoFiltro.getNome());
		parametros.put("INFO_CORD", orgaoFiltro.getOrgaosComLatitudeLongitude());
		parametros.put("INFO_ERRO", orgaoFiltro.getOrgaosComErro());
		parametros.put("ID_SITUACAO", orgaoFiltro.getTipoSituacaoRegistro()== null ? null : orgaoFiltro.getTipoSituacaoRegistro().indiceSituacao());
		parametros.put("TIPO", orgaoFiltro.getTipoOrgao() == null ? null : orgaoFiltro.getTipoOrgao().getId());
		parametros.put("ID_ESTADO", orgaoFiltro.getUf() == null ? null : orgaoFiltro.getUf().getId());
		parametros.put("NOME_ESTADO", orgaoFiltro.getUf() == null ? null : orgaoFiltro.getUf().getNome());
		parametros.put("ID_MUNICIPIO", orgaoFiltro.getMunicipio() == null ? null : orgaoFiltro.getMunicipio().getId());
		parametros.put("TEMA", orgaoFiltro.getTema() == null ? null : orgaoFiltro.getTema().getNome());
		parametros.put("SERVICO", orgaoFiltro.getServico() == null ? null : orgaoFiltro.getServico().getNome());
		parametros.put("AMBITO_ATUACAO", orgaoFiltro.getAmbitoAtuacaoOrgao() == null ? null : orgaoFiltro.getAmbitoAtuacaoOrgao().getDescricao());
		return parametros;
	}

	public void atribuirDescricaoOrgaoAction(ActionEvent actionEvent) {
		if (getEntidade().getTipoOrgao() != null
				&& getEntidade().getTipoOrgao().getDescricao() != null
				&& !getEntidade().getTipoOrgao().getDescricao().equals("")) {
			getEntidade().setDescricao(
					getEntidade().getTipoOrgao().getDescricao());
		}
	}

	public void atribuirServicosTipoOrgaoAction(ActionEvent actionEvent) {
		if (getEntidade().getTipoOrgao() != null
				&& getEntidade().getTipoOrgao().getServicos() != null
				&& !getEntidade().getTipoOrgao().getServicos().isEmpty()) {
			getEntidade().setServicos(new ArrayList<Servico>(getEntidade().getTipoOrgao().getServicos()));
		}
	}

	public void pesquisarServicos() throws AtlasAcessoBancoDadosException {
		setServicosParaSelecao(orgaoServico
				.recuperarServicosDisponiveisSelecao(getEntidade(),
						getServicoEmFoco().getNome()));
	}

	public void adicionarServicoAction() throws AtlasAcessoBancoDadosException,
	AtlasNegocioException {
		if (getServicosSelecionados() != null
				&& !getServicosSelecionados().isEmpty()) {
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


	/**
	 * Metodo responsavel por transformar o objeto orgão em um objeto do tipo orgaoDTO.
	 * @param listaOrgaos
	 * @return lista de orgãos dto
	 * @throws AtlasAcessoBancoDadosException
	 */
	private List<OrgaoDTO> recuperarOrgaosDTO() throws AtlasAcessoBancoDadosException {
		List<OrgaoDTO> orgaoDTOs = new ArrayList<OrgaoDTO>();

		for (Orgao orgao : orgaoServico.recuperarOrgaosRelacionadosESubordinados(usuarioAutenticado)) {
			OrgaoDTO orgaoDTO = new OrgaoDTO();
			orgaoDTO.setId(orgao.getId());
			orgaoDTOs.add(orgaoDTO);
		}

		return orgaoDTOs;
	}

	public void removerOrgaoSuperior(ActionEvent event){
		if(getEntidade().getOrgaoSuperior() != null){
			getEntidade().setOrgaoSuperior(null);
		}
	}

	public void limparFiltroDlgRepresentante(ActionEvent event){
		representanteEmFoco = new Representante();
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
		if (!isAdministrador()){
			List<OrgaoDTO> orgaoDTOs = recuperarOrgaosDTO();
			orgaoFiltro.setOrgaos(orgaoDTOs);
		}

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
			List<Orgao> recuperarOrgaos = orgaoServico.recuperarOrgaos(orgaoFiltro);
			if(recuperarOrgaos == null || recuperarOrgaos.isEmpty()){
				addMessage(null, FacesMessage.SEVERITY_WARN , "Nenhum resultado encontrado para os valores informados");
			} else {
				setFiltroDlgOrgaosPesquisa(recuperarOrgaos);
			}
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

	/**
	 * Returna true se o usuario for administrador.
	 * @return boolean
	 */
	public boolean isAdministrador(){
		if (usuarioAutenticado != null){
			for (Perfil perfil : usuarioAutenticado.getPerfis()) {
				if (perfil.getId().equals(AutenticarUsuarioBean.ADMINISTRADOR)){
					return true;
				}
			}
		}
		return false;
	}

	@Override
	protected String getTelaPesquisa() {
		return "exibirOrgao";
	}

	@Override
	protected String getTelaCadastro() {
		return "informarOrgao";
	}

	public String getNomeTipoOrgaoDoOrgaoSuperior() throws AtlasAcessoBancoDadosException{
		if (getEntidade().getOrgaoSuperior() != null
				&& getEntidade().getOrgaoSuperior().getTipoOrgao() != null) {
			Integer idTipoOrgao = getEntidade().getOrgaoSuperior().getTipoOrgao().getId();
			TipoOrgao tipoDoOrgaoSuperior = (TipoOrgao) tipoOrgaoServico.pesquisarPorId(idTipoOrgao, TipoOrgao.class);
			return tipoDoOrgaoSuperior.getNome();
		}
		return null;
	}

	public String labelAlterarOuCadastrarNovoOrgao() {
		boolean isAlterar = getEntidade() != null
				&& getEntidade().getId() != null;
		if (isAlterar)
			return "Alterar";
		else
			return "Cadastrar novo órgão";
	}

	public String getTermoPesquisa() {
		return termoPesquisa;
	}

	public void setTermoPesquisa(String termoPesquisa) {
		this.termoPesquisa = termoPesquisa;
	}

	public UploadedFile getFile() {
		return file;
	}

	public void setFile(UploadedFile file) {
		this.file = file;
	}

	public AtlasLazyDataModel<Orgao> getOrgaos()
			throws AtlasAcessoBancoDadosException {
		if (orgaos == null) {
			carregarTermos(null);
		}
		return orgaos;
	}

	public void setOrgaos(AtlasLazyDataModel<Orgao> orgaos) {
		this.orgaos = orgaos;
	}

	public OrgaoDTO getOrgaoFiltro() {
		return orgaoFiltro;
	}

	public void setOrgaoFiltro(OrgaoDTO orgaoFiltro) {
		this.orgaoFiltro = orgaoFiltro;
	}

	public TipoOrgao getTipoOrgao() {
		return tipoOrgao;
	}

	public void setTipoOrgao(TipoOrgao tipoOrgao) {
		this.tipoOrgao = tipoOrgao;
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
		if (getUfCadastro() != null) {
			return municipioServico.recuperarMunicipios(getUfCadastro());
		} else {
			return null;
		}
	}

	public List<Municipio> getMunicipiosPesquisa()
			throws AtlasAcessoBancoDadosException {
		return carregarMunicipios(getUfPesquisa());
	}

	private List<Municipio> carregarMunicipios(UF uf) throws AtlasAcessoBancoDadosException{
		if (uf != null) {
			return municipioServico.recuperarMunicipios(uf);
		} else {
			return null;
		}
	}

	public List<Municipio> getFiltroDlgMunicipios()
			throws AtlasAcessoBancoDadosException {
		return carregarMunicipios(getFiltroDlgUf());
	}

	public void informaOrgaoSuperior(ActionEvent event){
		if(orgaoSuperiorSelecionado != null){
			getEntidade().setOrgaoSuperior(orgaoSuperiorSelecionado);
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, null, "Órgão adicionado. Clique em SALVAR para confirmar a operação!"));
		}else{
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_WARN, null, "Selecione um órgão para realizar a operação!"));
		}
	}


	public String getCarregarOrgaosErro() {
		return carregarOrgaosErro;
	}

	public void setCarregarOrgaosErro(String carregarOrgaosErro) {
		this.carregarOrgaosErro = carregarOrgaosErro;
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

	public String getFormatoSelecionado() {
		return formatoSelecionado;
	}

	public void setFormatoSelecionado(String formatoSelecionado) {
		this.formatoSelecionado = formatoSelecionado;
	}

	public Representante getRepresentanteEmFoco() {
		if (this.representanteEmFoco == null)
			this.representanteEmFoco = new Representante();
		return representanteEmFoco;
	}

	public void setRepresentanteEmFoco(Representante representanteEmFoco) {
		this.representanteEmFoco = representanteEmFoco;
	}

	public List<Representante> getListaComRepresentantes() {
		return listaComRepresentantes;
	}

	public void setListaComRepresentantes(
			List<Representante> listaComRepresentantes) {
		this.listaComRepresentantes = listaComRepresentantes;
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

	public Integer getZoom() {
		return ZOOM;
	}

	public MapModel getDraggableModel() {
		return draggableModel;
	}

	public void setDraggableModel(MapModel draggableModel) {
		this.draggableModel = draggableModel;
	}

	public Marker getMarker() {
		return marker;
	}

	public void setMarker(Marker marker) {
		this.marker = marker;
	}

	public String[] getParametrosPesquisaMapa() {
		return parametrosPesquisaMapa;
	}

	public void setParametrosPesquisaMapa(String[] parametrosPesquisaMapa) {
		this.parametrosPesquisaMapa = parametrosPesquisaMapa;
	}

	public DualListModel<Tema> getTemasList() {
		if(temasList == null){
			try {
				inicializarTemas();
			} catch (AtlasAcessoBancoDadosException e) {
				e.printStackTrace();
			}
		}
		return temasList;
	}

	public void setTemasList(DualListModel<Tema> temasList) {
		this.temasList = temasList;
	}

	public Tema getTema() {
		return tema;
	}

	public void setTema(Tema tema) {
		this.tema = tema;
	}

	public Servico getServicoOrgao() {
		return servicoOrgao;
	}

	public void setServicoOrgao(Servico servicoOrgao) {
		this.servicoOrgao = servicoOrgao;
	}

	public boolean isErroRepresentante() {
		return erroRepresentante;
	}

	public void setErroRepresentante(boolean erroRepresentante) {
		this.erroRepresentante = erroRepresentante;
	}

	public void onCellEdit(CellEditEvent event) {
		Object oldValue = event.getOldValue();
		Object newValue = event.getNewValue();

		if(newValue != null && !newValue.equals("") && !newValue.equals(oldValue)) {
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Célula editada. Valor antigo: "+oldValue+", valor novo: "+newValue, "");
			FacesContext.getCurrentInstance().addMessage(null, msg);
		}
	}

	public UF getUfPesquisa() {
		return ufPesquisa;
	}

	public void setUfPesquisa(UF ufPesquisa) {
		this.ufPesquisa = ufPesquisa;
	}

	public Municipio getMunicipioPesquisa() {
		return municipioPesquisa;
	}

	public void setMunicipioPesquisa(Municipio municipioPesquisa) {
		this.municipioPesquisa = municipioPesquisa;
	}

	public UF getUfCadastro() {
		return ufCadastro;
	}

	public void setUfCadastro(UF ufCadastro) {
		this.ufCadastro = ufCadastro;
	}

	public Municipio getMunicipioCadastro() {
		return municipioCadastro;
	}

	public void setMunicipioCadastro(Municipio municipioCadastro) {
		this.municipioCadastro = municipioCadastro;
	}

	public List<AmbitoAtuacaoOrgao> getListaAmbitoAtuacao() {
		if (listaAmbitoAtuacao == null) {
			listaAmbitoAtuacao = Arrays.asList(AmbitoAtuacaoOrgao.values());
		}
		return listaAmbitoAtuacao;
	}

	public AmbitoAtuacaoOrgao getAmbitoAtuacaoOrgao() {
		return ambitoAtuacaoOrgao;
	}

	public void setAmbitoAtuacaoOrgao(AmbitoAtuacaoOrgao ambitoAtuacaoOrgao) {
		this.ambitoAtuacaoOrgao = ambitoAtuacaoOrgao;
	}

	public Servico getServicoEmFoco() {
		return servicoEmFoco;
	}

	public void setServicoEmFoco(Servico servicoEmFoco) {
		this.servicoEmFoco = servicoEmFoco;
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

}
