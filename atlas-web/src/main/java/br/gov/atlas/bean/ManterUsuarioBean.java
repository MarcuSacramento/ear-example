package br.gov.atlas.bean;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.primefaces.event.FlowEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.DualListModel;
import org.primefaces.model.TreeNode;

import br.gov.atlas.component.model.AtlasLazyDataModel;
import br.gov.atlas.controller.PageController;
import br.gov.atlas.dto.OrgaoDTO;
import br.gov.atlas.dto.UsuarioDTO;
import br.gov.atlas.entidade.Municipio;
import br.gov.atlas.entidade.Orgao;
import br.gov.atlas.entidade.Perfil;
import br.gov.atlas.entidade.Tema;
import br.gov.atlas.entidade.TipoOrgao;
import br.gov.atlas.entidade.UF;
import br.gov.atlas.entidade.Usuario;
import br.gov.atlas.enums.TipoSituacaoRegistroOrgao;
import br.gov.atlas.exception.AtlasAcessoBancoDadosException;
import br.gov.atlas.exception.AtlasNegocioException;
import br.gov.atlas.seguranca.bean.AutenticarUsuarioBean;
import br.gov.atlas.servico.AtlasServicoImpl;
import br.gov.atlas.servico.ManterPerfilServico;
import br.gov.atlas.servico.ManterTemaServico;
import br.gov.atlas.servico.ManterUsuarioServico;
import br.gov.atlas.servico.MunicipioServico;
import br.gov.atlas.servico.OrgaoServico;
import br.gov.atlas.servico.UsuarioServico;
import br.gov.atlas.util.Valida;

@ManagedBean(name = "manterUsuarioBean")
@javax.faces.bean.SessionScoped
public class ManterUsuarioBean extends AtlasCrudBean<Usuario> {

	private static final long serialVersionUID = -1842633566063941653L;

	// SERVIÇOS
	@EJB(name = "OrgaoServico")
	OrgaoServico orgaoServico;

	@EJB(name = "UsuarioServico")
	private UsuarioServico usuarioServico;

	@EJB(name = "ManterTemaServico")
	private ManterTemaServico temaServico;

	@EJB(name = "MunicipioServico")
	private MunicipioServico municipioServico;

	@EJB(name = "ManterUsuarioServico")
	ManterUsuarioServico manterUsuarioServico;

	@EJB(name = "ManterPerfilServico")
	ManterPerfilServico manterPerfilServico;

	private AutenticarUsuarioBean autenticarUsuarioBean;
	private AtlasLazyDataModel<Usuario> usuarios;
	private UsuarioDTO usuarioFiltro;
	private Usuario usuarioAutenticado;
	private String termoPesquisa;
	private String nomeOrgaoPesquisa;
	private String senha;
	private String novaSenha;
	private String confirmacaoSenha;
	private String loginUsuario;
	private boolean logar = false;
	private boolean alterado = false;
	private DualListModel<Perfil> perfis;
	private Boolean isDeveExibirAlterarSenha = false;
	private Boolean isEstahProcedendoPesquisaAgora = false;

	// FILTROS ÓRGÃOS
	private UF uf;
	private Municipio municipio;
	private TipoOrgao tipoOrgao;
	private Tema tema;
	private String carregarOrgaosInformacaoLatitudeLongitude;
	private String carregarOrgaosSituacaoRegistro;
	private String carregarOrgaosErro;

	// LISTAS
	private TreeNode orgaosRootTree;
	private TreeNode[] orgaosSelecionadosTree;
	private List<Orgao> orgaosPesquisa;
	private List<Orgao> orgaosSelecionados; // órgãos selecionados na pesquisa
	private List<Orgao> orgaosUsuario; // órgãos relacionados ao usuário
	private List<Orgao> orgaosUsuarioSelecionados; // órgãos do usuário e
	// selecionados para remoção
	// do relacionamento

	public void init() throws AtlasAcessoBancoDadosException {
		usuarioAutenticado = ((PageController) getBean("pageController"))
				.getUsuario();
		if (isEstahProcedendoPesquisaAgora == false) {
			this.usuarioFiltro = new UsuarioDTO();
			carregarTermos(null);
			pesquisarOrgaos(null);
		} else {
			termoPesquisa = "";
		}
		this.isEstahProcedendoPesquisaAgora = false;
		orgaosUsuario = null;
		orgaosUsuarioSelecionados = null;
	}

	public String novoRegistroManterUsuario() {
		super.novoRegistro();
		try {
			this.inicializarPerfisQuandoInclusao();
			this.inicializarDataInclusao();
		} catch (AtlasAcessoBancoDadosException e) {
			e.printStackTrace();
		}
		this.isDeveExibirAlterarSenha = false;
		return getTelaCadastro();
	}

	public String editarManterUsuario(Integer pk) {
		super.editar(pk);
		return getTelaCadastro();
	}

	public String voltarTelaPesquisa() {
		return getTelaPesquisa();
	}

	@Override
	protected void initCarregarEntidade() throws AtlasAcessoBancoDadosException {
		inicializarDataInclusao();
		inicializarPerfis();
		inicializarOrgaosRelacionados();
	}

	private void inicializarOrgaosRelacionados()
			throws AtlasAcessoBancoDadosException {
		if (isEstahAlterando()) {
			orgaosUsuario = orgaoServico
					.recuperarOrgaosRelacionados(getEntidade());
		}
	}

	public boolean isEstahIncluindo() {
		Usuario usuario = getEntidade();
		if (usuario == null || usuario.getId() == null)
			return true;
		return false;
	}

	public boolean isEstahAlterando() {
		return !isEstahIncluindo();
	}

	public void exibirAlterarSenha() {
		this.isDeveExibirAlterarSenha = true;
	}

	private void inicializarDataInclusao() {
		if (isEstahIncluindo())
			getEntidade().setDataInclusao(new Date(System.currentTimeMillis()));
	}

	private void inicializarPerfis() throws AtlasAcessoBancoDadosException {
		if (isEstahIncluindo())
			inicializarPerfisQuandoInclusao();

		if (isEstahAlterando())
			inicializarPerfisQuandoAlteracao();
	}

	@SuppressWarnings("unchecked")
	private void inicializarPerfisQuandoInclusao()
			throws AtlasAcessoBancoDadosException {
		List<Perfil> todosOsPerfis = manterUsuarioServico.buscarTodos(
				Perfil.class, "nome");

		// Caso o usuario não seja MASTER, é apresentado o perfil do usuário
		// autenticado (GESTOR) e os perfis COMUNS para seleção
		if (!isPerfilMaster()) {
			todosOsPerfis = filtrarPerfisDisponiveis(todosOsPerfis,
					usuarioAutenticado.getPerfis());
		}
		this.perfis = new DualListModel<>(todosOsPerfis,
				new ArrayList<Perfil>());
	}

	private List<Perfil> filtrarPerfisDisponiveis(List<Perfil> todosOsPerfis,
			List<Perfil> perfisUsuarioAutenticado) {
		List<Perfil> perfilAux = new ArrayList<>();
		for (Perfil perfil : todosOsPerfis) {
			if (!perfil.getTipo().equals(Perfil.MASTER)) {
				if (perfisUsuarioAutenticado.contains(perfil)
						|| perfil.getTipo().equals(Perfil.COMUM)) {
					perfilAux.add(perfil);
				}
			}
		}
		return perfilAux;
	}

	@SuppressWarnings("unchecked")
	private void inicializarPerfisQuandoAlteracao()
			throws AtlasAcessoBancoDadosException {
		Usuario usuario = getEntidade();
		usuario.setPerfis(manterUsuarioServico.recuperarPerfis(usuario));
		List<Perfil> auxiliarPerfisJaSelecionadosParaAqueleUsuario = usuario
				.getPerfis();
		List<Perfil> auxiliarPerfisAindaDisponiveisParaAqueleUsuario = new ArrayList<>();
		auxiliarPerfisAindaDisponiveisParaAqueleUsuario = manterUsuarioServico
				.buscarTodos(Perfil.class);

		// Caso o usuario não seja MASTER, é apresentado o perfil do usuário
		// autenticado (GESTOR) e os perfis COMUNS para seleção
		if (!isPerfilMaster()) {
			auxiliarPerfisAindaDisponiveisParaAqueleUsuario = filtrarPerfisDisponiveis(
					auxiliarPerfisAindaDisponiveisParaAqueleUsuario,
					usuarioAutenticado.getPerfis());
		}

		auxiliarPerfisAindaDisponiveisParaAqueleUsuario
		.removeAll(auxiliarPerfisJaSelecionadosParaAqueleUsuario);
		this.perfis = new DualListModel<>(
				auxiliarPerfisAindaDisponiveisParaAqueleUsuario,
				auxiliarPerfisJaSelecionadosParaAqueleUsuario);
	}

	public void pesquisarTermo() throws AtlasAcessoBancoDadosException {
		if (this.termoPesquisa != null && !this.termoPesquisa.trim().isEmpty()) {
			carregarTermos("%" + this.termoPesquisa + "%");
		} else {
			carregarTermos(null);
		}
		this.isEstahProcedendoPesquisaAgora = true;
	}

	public void pesquisarOrgaos(ActionEvent event)
			throws AtlasAcessoBancoDadosException {
		if (this.nomeOrgaoPesquisa != null
				&& !this.nomeOrgaoPesquisa.trim().isEmpty()) {
			pesquisarOrgaosPorFiltro("%" + this.nomeOrgaoPesquisa + "%");
		} else {
			pesquisarOrgaosPorFiltro(null);
		}
		this.isEstahProcedendoPesquisaAgora = true;
	}

	public void limparPesquisaOrgao(ActionEvent event) {
		orgaosPesquisa = new ArrayList<>();
		orgaosRootTree = null;
		orgaosSelecionadosTree = null;
		nomeOrgaoPesquisa = null;
		municipio = null;
		tipoOrgao = null;
		uf = null;
		tema = null;
		carregarOrgaosInformacaoLatitudeLongitude = null;
		carregarOrgaosSituacaoRegistro = null;
		carregarOrgaosErro = null;
	}

	private Orgao recuperarOrgaoPai(TreeNode orgaoNo,
			TreeNode[] orgaosSelecionadosTree) {
		Orgao orgaoRetorno = (Orgao) orgaoNo.getData();
		TreeNode noPai = orgaoNo.getParent();
		for (TreeNode noAtual : orgaosSelecionadosTree) {
			if (noPai.equals(noAtual)) {
				orgaoRetorno = recuperarOrgaoPai(noAtual,
						orgaosSelecionadosTree);
			}
		}
		return orgaoRetorno;
	}

	/**
	 * Caso o usuário selecione todos os órgãos subordinados de um órgão
	 * superior, será relacionado somente o órgao superior. Do contrário será
	 * relacionado os selecionados.
	 *
	 * @param event
	 */
	public void popularGridUsuario(ActionEvent event) {
		boolean isGridPopulada = false;
		boolean isOrgaoSelecionado = orgaosSelecionadosTree != null
				&& orgaosSelecionadosTree.length > 0;
				if (isOrgaoSelecionado) {
					List<Orgao> listAux = new ArrayList<>();
					for (TreeNode orgaoNode : orgaosSelecionadosTree) {
						Orgao orgaoSelecionado = recuperarOrgaoPai(orgaoNode,
								orgaosSelecionadosTree);
						if (listAux.isEmpty()) {
							listAux.add(orgaoSelecionado);
						} else if (!listAux.contains(orgaoSelecionado)) {
							listAux.add(orgaoSelecionado);
						}
					}

					for (Orgao orgaoSelecionado : listAux) {
						if (orgaosUsuario == null) {
							orgaosUsuario = new ArrayList<>();
							orgaosUsuario.add(orgaoSelecionado);
							isGridPopulada = true;
						} else {
							if (!orgaosUsuario.contains(orgaoSelecionado)) {
								orgaosUsuario.add(orgaoSelecionado);
								isGridPopulada = true;
							}
						}
					}
				}
				// Tabela populada com sucesso
				if (isGridPopulada) {
					FacesContext
					.getCurrentInstance()
					.addMessage(
							null,
							new FacesMessage(FacesMessage.SEVERITY_INFO, null,
									"Órgão(s) adicionado(s). Clique em SALVAR para confirmar a operação!"));
				}
				// Tabela não populada porque não houve órgão selecionado
				if (!isGridPopulada && !isOrgaoSelecionado) {
					FacesContext
					.getCurrentInstance()
					.addMessage(
							null,
							new FacesMessage(FacesMessage.SEVERITY_WARN,
									"Atenção",
									"Selecione pelo menos um órgão para relacionar com o usuário."));
				}
				// Órgão(s) selecionado(s) já foram relacionados.
				if (!isGridPopulada && isOrgaoSelecionado) {
					FacesContext
					.getCurrentInstance()
					.addMessage(
							null,
							new FacesMessage(FacesMessage.SEVERITY_WARN,
									"Atenção",
									"Os órgão(s) selecionado(s) foram relacionados anteriormente."));
				}
	}

	public void removerRelacionamento(ActionEvent event) {
		if (orgaosUsuarioSelecionados != null
				&& !orgaosUsuarioSelecionados.isEmpty()) {
			for (Orgao orgaoUsuarioSelecionado : orgaosUsuarioSelecionados) {
				if (orgaosUsuario.contains(orgaoUsuarioSelecionado)) {
					orgaosUsuario.remove(orgaoUsuarioSelecionado);
				}
			}
		}
	}

	@Override
	protected void efetuarPesquisa() throws AtlasAcessoBancoDadosException {
		usuarios.setListaPaginada(manterUsuarioServico.recuperarUsuarios(
				usuarioFiltro, "nme_usuario"));
	}

	public void carregarTermos(String textoParcial)
			throws AtlasAcessoBancoDadosException {
		usuarios = new AtlasLazyDataModel<Usuario>();
		usuarios.setServico(manterUsuarioServico);
		usuarios.setMetodo("recuperarUsuarios");

		usuarioFiltro = new UsuarioDTO();
		usuarioFiltro.setNome(textoParcial);

		if (!isPerfilMaster()) {
			// recupera os usuários que estão vinculados aos mesmos orgãos do
			// usuário autenticado.
			usuarioFiltro.setUsuarios(usuarioServico
					.recuperarUsuariosDTO(usuarioAutenticado));
		}

		usuarios.setParametros(usuarioFiltro);
		pesquisar();
	}

	@Override
	public Boolean validar() throws AtlasNegocioException {

		boolean valido = true;
		boolean emailValido = true;

		StringBuffer campoObrigatorio = new StringBuffer();

		if (getEntidade().getNome() == null
				|| getEntidade().getNome().trim().isEmpty()) {
			valido = false;
			campoObrigatorio.append(campoObrigatorio.toString().trim()
					.isEmpty() ? "Nome" : ", Nome");
		}
		if (getEntidade().getEmail() == null
				|| getEntidade().getEmail().trim().isEmpty()) {
			valido = false;
			campoObrigatorio.append(campoObrigatorio.toString().trim()
					.isEmpty() ? "E-mail" : ", E-mail");
		}
		if (getEntidade().getSexo() == null
				|| getEntidade().getSexo().trim().isEmpty()) {
			valido = false;
			campoObrigatorio.append(campoObrigatorio.toString().trim()
					.isEmpty() ? "Sexo" : ", Sexo");
		}
		if (getEntidade().getSituacao() == null
				|| getEntidade().getSituacao().trim().isEmpty()) {
			valido = false;
			campoObrigatorio.append(campoObrigatorio.toString().trim()
					.isEmpty() ? "Situação" : ", Situação");
		}

		if (getEntidade().getPerfis() == null
				|| getEntidade().getPerfis().isEmpty()) {
			valido = false;
			campoObrigatorio.append(campoObrigatorio.toString().trim()
					.isEmpty() ? "Perfil" : ", Perfil");
		}

		if (isEstahIncluindo()) {
			if (getSenha() == null || getSenha().trim().isEmpty()) {
				valido = false;
				campoObrigatorio.append(campoObrigatorio.toString().trim()
						.isEmpty() ? "Senha" : ", Senha");
			}
			if (getConfirmacaoSenha() == null
					|| getConfirmacaoSenha().trim().isEmpty()) {
				valido = false;
				campoObrigatorio.append(campoObrigatorio.toString().trim()
						.isEmpty() ? "Confirmação de senha"
								: ", Confirmação de senha");
			}
			if (getSenha() == null || getConfirmacaoSenha() == null
					|| !getSenha().equals(getConfirmacaoSenha())) {
				error(null, "MSG21");
				return false;
			}
		}

		if (!getEntidade().getEmail().isEmpty()) {
			if (!Valida.validaEmail(getEntidade().getEmail())) {
				error(null, "MSG8");
				emailValido = false;
			}
		}

		String cpf = getEntidade().getCpf();
		if (cpf != null && !cpf.trim().isEmpty() && !Valida.isCPF(cpf)) {
			error(null, "MSG38");
			return false;
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
	public String salvar() {
		try {
			Usuario usuario = getEntidade();
			//Valida o CPF do usuário
			if (usuario.getCpf() != null && usuario.getCpf().trim().length() > 0 && !Valida.isCPF(usuario.getCpf())) {
				error(null, "MSG38");
				return null;
			}
			if (getSenha() != null && !getSenha().isEmpty()) {
				String hashParaSenha = calcularHashParaSenha(usuario);
				usuario.setSenha(hashParaSenha);
			}
			if (getPerfis() != null) {
				usuario.setPerfis(getPerfis().getTarget());
			}

			String email = usuario.getEmail();
			if (email != null && !"".equals(email)) {
				Integer id = usuario.getId();
				Usuario usuarioRecuperado = usuarioServico
						.recuperarEmailPotUsuario(email);
				if (usuarioRecuperado != null) {
					boolean ehMesmoUsuario = usuarioRecuperado.getId().equals(
							id);
					if (!ehMesmoUsuario) {
						error(null, "MSG37");
						return null;
					}
				}
			}

			usuario.setOrgaos(getOrgaosUsuario());
			super.salvar();
			this.isDeveExibirAlterarSenha = false;
			return "pesquisarUsuario";
		} catch (AtlasAcessoBancoDadosException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public void redefenirSenha() {
		try {
			usuarioAutenticado = usuarioServico.recuperarUsuario(loginUsuario,
					senha);
			if (usuarioAutenticado == null) {
				warn(null, "MSG0", "Senha informada não confere.");
				return;
			}
			if (novaSenha == null || novaSenha.trim().isEmpty()) {
				error(null, "MSG20", "Nova Senha*:");
				return;
			}
			if (confirmacaoSenha == null || confirmacaoSenha.trim().isEmpty()) {
				error(null, "MSG20", "Confirmação de senha*:");
				return;
			}
			if (!novaSenha.equals(confirmacaoSenha)) {
				error(null, "MSG21");
				return;
			}
			setEntidade(usuarioAutenticado);
			setSenha(novaSenha);
			setConfirmacaoSenha(confirmacaoSenha);
			inicializarPerfisQuandoAlteracao();
		} catch (AtlasAcessoBancoDadosException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		salvar();
		setAlterado(true);
	}

	private String calcularHashParaSenha(Usuario usuario) {
		String resultado = "";

		try {
			byte[] digest = MessageDigest.getInstance("MD5").digest(
					getSenha().getBytes());
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < digest.length; i++) {
				int high = ((digest[i] >> 4) & 0xf) << 4;
				int low = digest[i] & 0xf;
				if (high == 0)
					sb.append('0');
				sb.append(Integer.toHexString(high | low));
				resultado = sb.toString();
			}
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		return resultado;
	}

	public void limpar() {
		this.termoPesquisa = null;
	}

	public void limparPesquisar() throws AtlasAcessoBancoDadosException {
		limpar();
		carregarTermos(null);
	}

	public String getHome() {
		setAlterado(false);
		if (isLogar()) {
			((PageController) getBean("pageController"))
			.setUsuario(getEntidade());
		}
		return "/admin/home.xhtml";
	}

	@Override
	protected AtlasServicoImpl getServico() {
		return manterUsuarioServico;
	}

	@Override
	protected String getTelaPesquisa() {
		return "exibirUsuarios";
	}

	@Override
	protected String getTelaCadastro() {
		return "informarUsuario";
	}

	public String getTermoPesquisa() {
		return termoPesquisa;
	}

	public void setTermoPesquisa(String termoPesquisa) {
		this.termoPesquisa = termoPesquisa;
	}

	public UsuarioDTO getUsuarioFiltro() {
		return usuarioFiltro;
	}

	public void setUsuarioFiltro(UsuarioDTO usuarioFiltro) {
		this.usuarioFiltro = usuarioFiltro;
	}

	public AtlasLazyDataModel<Usuario> getUsuarios()
			throws AtlasAcessoBancoDadosException {
		if (usuarios == null) {
			carregarTermos(null);
		}
		return usuarios;
	}

	public void setUsuarios(AtlasLazyDataModel<Usuario> usuarios) {
		this.usuarios = usuarios;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public String getNovaSenha() {
		return novaSenha;
	}

	public void setNovaSenha(String novaSenha) {
		this.novaSenha = novaSenha;
	}

	public String getConfirmacaoSenha() {
		return confirmacaoSenha;
	}

	public void setConfirmacaoSenha(String confirmacaoSenha) {
		this.confirmacaoSenha = confirmacaoSenha;
	}

	public String getLoginUsuario() {
		return loginUsuario;
	}

	public void setLoginUsuario(String loginUsuario) {
		this.loginUsuario = loginUsuario;
	}

	public boolean isLogar() {
		return logar;
	}

	public void setLogar(boolean logar) {
		this.logar = logar;
	}

	public boolean isAlterado() {
		return alterado;
	}

	public void setAlterado(boolean alterado) {
		this.alterado = alterado;
	}

	public DualListModel<Perfil> getPerfis() {
		if (perfis == null)
			try {
				inicializarPerfis();
			} catch (AtlasAcessoBancoDadosException e) {
				e.printStackTrace();
			}
		return this.perfis;
	}

	public void setPerfis(DualListModel<Perfil> perfis) {
		this.perfis = perfis;
	}

	public Boolean getIsDeveExibirAlterarSenha() {
		return isDeveExibirAlterarSenha;
	}

	public void setIsDeveExibirAlterarSenha(Boolean isDeveExibirAlterarSenha) {
		this.isDeveExibirAlterarSenha = isDeveExibirAlterarSenha;
	}

	public Boolean getIsEstahProcedendoPesquisaAgora() {
		return isEstahProcedendoPesquisaAgora;
	}

	public void setIsEstahProcedendoPesquisaAgora(
			Boolean isEstahProcedendoPesquisaAgora) {
		this.isEstahProcedendoPesquisaAgora = isEstahProcedendoPesquisaAgora;
	}

	public void save() {
		FacesMessage msg = new FacesMessage("Successful", "Welcome :");
		FacesContext.getCurrentInstance().addMessage(null, msg);
	}

	private boolean skip;

	public boolean isSkip() {
		return skip;
	}

	public void setSkip(boolean skip) {
		this.skip = skip;
	}

	public String onFlowProcess(FlowEvent event) {
		if (skip) {
			skip = false; // reset in case user goes back
			return "confirm";
		} else {
			return event.getNewStep();
		}
	}

	public boolean isPerfilMaster() {
		return ManterUsuarioServico.isUsuarioMaster(usuarioAutenticado);
	}

	public boolean isPerfilHabilitadoExcluir() {
		return manterUsuarioServico
				.isUsuarioHabilitadoExcluir(usuarioAutenticado);
	}

	public boolean isPerfilHabilitadoAlterar()
			throws AtlasAcessoBancoDadosException {
		return manterUsuarioServico.isUsuarioHabilitadoAlterar(
				usuarioAutenticado, getEntidade());
	}

	public boolean isPerfilHabilitadoInserir()
			throws AtlasAcessoBancoDadosException {
		return manterUsuarioServico
				.isUsuarioHabilitadoInserir(usuarioAutenticado);
	}

	/**
	 * Returna true se o usuario for administrador.
	 *
	 * @return boolean
	 */
	@SuppressWarnings("static-access")
	public boolean isAdministrador() {
		if (usuarioAutenticado != null) {
			for (Perfil perfil : usuarioAutenticado.getPerfis()) {
				if (perfil.getId().equals(autenticarUsuarioBean.ADMINISTRADOR)) {
					return true;
				}
			}
		}
		return false;
	}

	public Usuario getUsuarioAutenticado() {
		return usuarioAutenticado;
	}

	public void setUsuarioAutenticado(Usuario usuarioAutenticado) {
		this.usuarioAutenticado = usuarioAutenticado;
	}

	public List<Orgao> getOrgaosPesquisa() {
		return orgaosPesquisa;
	}

	public void setOrgaosPesquisa(List<Orgao> orgaosPesquisa) {
		this.orgaosPesquisa = orgaosPesquisa;
	}

	public List<TipoSituacaoRegistroOrgao> getSituacoes() {
		return TipoSituacaoRegistroOrgao.getValuesAsList();
	}

	/**
	 * Metodo responsavel por carregar os orgão de acordo com os filtros.
	 *
	 * @param textoParcial
	 * @throws AtlasAcessoBancoDadosException
	 */
	public void pesquisarOrgaosPorFiltro(String textoParcial)
			throws AtlasAcessoBancoDadosException {

		// FILTRO, DTO
		OrgaoDTO orgaoFiltro = new OrgaoDTO();

		// FILTROS MAIS COMUNS
		orgaoFiltro.setNome(textoParcial);
		orgaoFiltro.setMunicipio(municipio);
		orgaoFiltro.setTipoOrgao(tipoOrgao);
		orgaoFiltro.setUf(uf);
		orgaoFiltro.setTema(tema);
		if (!isAdministrador()) {
			List<OrgaoDTO> orgaoDTOs = recuperarOrgaosDTO();
			orgaoFiltro.setOrgaos(orgaoDTOs);
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

		if (isFiltroInformado(orgaoFiltro)) {
			setOrgaosPesquisa(orgaoServico.recuperarDadosOgaos(orgaoFiltro));
			converterParaOrgaosTree();
		}

	}

	public static TreeNode getNoPai(Orgao orgaoPai,
			Map<Orgao, TreeNode> nosMap, TreeNode noRaiz) {
		TreeNode noPai = nosMap.get(orgaoPai);
		if (noPai != null) {
			return noPai;
		}
		return noRaiz;
	}

	private void converterParaOrgaosTree() {
		if (orgaosPesquisa != null && !orgaosPesquisa.isEmpty()) {
			Map<Orgao, TreeNode> nosMap = new LinkedHashMap<Orgao, TreeNode>();
			orgaosRootTree = new DefaultTreeNode();

			// cria todos os nós num mesmo nível
			for (Orgao orgaoPesquisado : orgaosPesquisa) {
				nosMap.put(orgaoPesquisado, new DefaultTreeNode(
						orgaoPesquisado, null));
			}

			// organiza a arvore (pai e filhos)
			for (TreeNode no : nosMap.values()) {
				Orgao orgaoSuperior = ((Orgao) no.getData()).getOrgaoSuperior();
				no.setParent(getNoPai(orgaoSuperior, nosMap, orgaosRootTree));
			}

		}
	}

	private boolean isFiltroInformado(OrgaoDTO orgaoFiltro) {
		boolean isFiltroPreenchido = true;
		if (orgaoFiltro == null) {
			isFiltroPreenchido = false;
		} else {
			if ((orgaoFiltro.getNome() == null || orgaoFiltro.getNome().trim()
					.equals(""))
					&& orgaoFiltro.getMunicipio() == null
					&& orgaoFiltro.getTipoOrgao() == null
					&& orgaoFiltro.getUf() == null
					&& orgaoFiltro.getTema() == null
					&& orgaoFiltro.getOrgaosComLatitudeLongitude() == null
					&& orgaoFiltro.getOrgaosComErro() == null
					&& orgaoFiltro.getOrgaosSituacaoRegistro() == null) {
				isFiltroPreenchido = false;
			}
		}

		return isFiltroPreenchido;
	}

	/**
	 * Metodo responsavel por transformar o objeto orgão em um objeto do tipo
	 * orgaoDTO.
	 *
	 * @param listaOrgaos
	 * @return lista de orgãos dto
	 * @throws AtlasAcessoBancoDadosException
	 */
	private List<OrgaoDTO> recuperarOrgaosDTO()
			throws AtlasAcessoBancoDadosException {
		List<OrgaoDTO> orgaoDTOs = new ArrayList<OrgaoDTO>();

		for (Orgao orgao : orgaoServico
				.recuperarOrgaosRelacionados(usuarioAutenticado)) {
			OrgaoDTO orgaoDTO = new OrgaoDTO();
			orgaoDTO.setId(orgao.getId());
			orgaoDTOs.add(orgaoDTO);
		}

		return orgaoDTOs;
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

	@SuppressWarnings("unchecked")
	public List<TipoOrgao> getTiposOrgaos()
			throws AtlasAcessoBancoDadosException {
		return orgaoServico.buscarTodos(TipoOrgao.class, "nome");
	}

	@SuppressWarnings("unchecked")
	public List<Tema> getTemas() throws AtlasAcessoBancoDadosException {
		return temaServico.buscarTodos(Tema.class, "nome");
	}

	@SuppressWarnings("unchecked")
	public List<UF> getUfs() throws AtlasAcessoBancoDadosException {
		return orgaoServico.buscarTodos(UF.class, "nome");
	}

	public List<Municipio> getMunicipios()
			throws AtlasAcessoBancoDadosException {
		if (getUf() != null) {
			return municipioServico.recuperarMunicipios(getUf());
		} else {
			return null;
		}
	}

	private List<Orgao> recuperarOrgaosGestor()
			throws AtlasAcessoBancoDadosException {
		return orgaoServico.recuperarOrgaosRelacionados(usuarioAutenticado);
	}

	public List<Orgao> getOrgaosSelecionados() {
		return orgaosSelecionados;
	}

	public void setOrgaosSelecionados(List<Orgao> orgaosSelecionados) {
		this.orgaosSelecionados = orgaosSelecionados;
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

	public String getNomeOrgaoPesquisa() {
		return nomeOrgaoPesquisa;
	}

	public void setNomeOrgaoPesquisa(String nomeOrgaoPesquisa) {
		this.nomeOrgaoPesquisa = nomeOrgaoPesquisa;
	}

	public List<Orgao> getOrgaosUsuario() throws AtlasAcessoBancoDadosException {
		if (!isPerfilMaster()
				&& (orgaosUsuario == null || orgaosUsuario.isEmpty())) {
			orgaosUsuario = recuperarOrgaosGestor();
		}
		return orgaosUsuario;
	}

	public void setOrgaosUsuario(List<Orgao> orgaosUsuario) {
		this.orgaosUsuario = orgaosUsuario;
	}

	public List<Orgao> getOrgaosUsuarioSelecionados() {
		return orgaosUsuarioSelecionados;
	}

	public void setOrgaosUsuarioSelecionados(
			List<Orgao> orgaosUsuarioSelecionados) {
		this.orgaosUsuarioSelecionados = orgaosUsuarioSelecionados;
	}

	public TreeNode getOrgaosRootTree() {
		return orgaosRootTree;
	}

	public void setOrgaosRootTree(TreeNode orgaosRootTree) {
		this.orgaosRootTree = orgaosRootTree;
	}

	public TreeNode[] getOrgaosSelecionadosTree() {
		return orgaosSelecionadosTree;
	}

	public void setOrgaosSelecionadosTree(TreeNode[] orgaosSelecionadosTree) {
		this.orgaosSelecionadosTree = orgaosSelecionadosTree;
	}
}