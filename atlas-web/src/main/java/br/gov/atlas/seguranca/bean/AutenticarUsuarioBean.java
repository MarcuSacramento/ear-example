package br.gov.atlas.seguranca.bean;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.gov.atlas.bean.AtlasBean;
import br.gov.atlas.component.model.AtlasLazyDataModel;
import br.gov.atlas.constantes.ConstantesWeb;
import br.gov.atlas.controller.PageController;
import br.gov.atlas.entidade.Orgao;
import br.gov.atlas.entidade.Perfil;
import br.gov.atlas.entidade.Usuario;
import br.gov.atlas.enums.TipoFuncionalidade;
import br.gov.atlas.enums.TipoNivelAcesso;
import br.gov.atlas.exception.AtlasAcessoBancoDadosException;
import br.gov.atlas.servico.UsuarioServico;
import br.gov.atlas.util.DataUtils;
import br.gov.atlas.util.ThreadLocalUsuarioLogado;

/**
 * Class autenticarUsuarioBean.
 */
@ManagedBean(name = "autenticarUsuarioBean")
@SessionScoped
public class AutenticarUsuarioBean extends AtlasBean {

	public static final int ADMINISTRADOR = 6;

	private static final long serialVersionUID = 7480342524054076422L;

	@EJB(name = "UsuarioServico")
	private UsuarioServico usuarioServico;

	private String login;
	private String senha;

	private Usuario usuarioAutenticado;
	
	private TipoFuncionalidade tipoFuncionalidade;

	private AtlasLazyDataModel<Orgao> orgaosPesquisa;
	
	/**
	 * Efetua a autenticacao do usuario no sistema.
	 * 
	 * @return string
	 */
	public String login() {

		if (getLogin() == null || getLogin().trim().isEmpty()) {
			error("MSG1");
			return null;
		}
		if (getSenha() == null || getSenha().trim().isEmpty()) {
			error("MSG1");
			return null;
		}

		try {
			usuarioAutenticado = getServico().recuperarUsuario(login, senha);
							         
			if (usuarioAutenticado != null) {
				
				if(isUsuarioAtivo()){
					// Busca os perfis e os orgãos do usuário logado.
					usuarioAutenticado.setPerfis(usuarioServico.recuperarPerfis(usuarioAutenticado));
					
					logAutenticacao(); //Log de Informações de autenticação
					
					((PageController) getBean("pageController")).setUsuario(usuarioAutenticado);
					FacesContext context = FacesContext.getCurrentInstance();
					HttpServletResponse response = (HttpServletResponse)context.getExternalContext().getResponse();
					try {
						response.sendRedirect(ConstantesWeb.LOGIN_SUCESSO.replace("xhtml", "faces"));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return null;
				}else{
					error(null, "MSG33", usuarioAutenticado.getSituacaoFormatada());
					return null;
				}
				
			}else{
				error("MSG9");
				return null;
			}
		} catch (AtlasAcessoBancoDadosException e) {
			error("MSG9");
			return null;
		} 
	}
	
	private boolean isUsuarioAtivo() {
		return usuarioAutenticado.getSituacao() != null
				&& usuarioAutenticado.getSituacao().equals(Usuario.SIT_ATIVO);
	}

	public void logAutenticacao(){
		ThreadLocalUsuarioLogado.createThreadInstance(login);
		HttpServletRequest req = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
		
		InetAddress i = null;
		try {
			i = InetAddress.getLocalHost();
		} catch (UnknownHostException e1) {
			error("Host desconhecido");
		}
		
		try {
			System.out.println("-------------------------------------------------------------");	
			System.out.println("Informações de Autenticação");
			System.out.println("Usuário : "+ThreadLocalUsuarioLogado.currentInstance().getUsuarioLogado()+ 
					" Logado dia " +DataUtils.getHojePorExtenso()+ " às " + DataUtils.dateToString(new Date(), "HH:mm:ss"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("IP Usuário: " + req.getRemoteAddr() + req.getRemotePort());
		System.out.println("Servidor:   " + i);
		System.out.println("-------------------------------------------------------------");
	}

	/**
	 * Encerra sessão http.
	 * 
	 * @return string
	 */
	public String logout() {
		ThreadLocalUsuarioLogado.release();
		getFacesContext().getExternalContext().invalidateSession();
		return ConstantesWeb.PAGE_LOGIN;
	}
	
	/**
	 * Redireciona para a tela de acessibilidade.
	 * 
	 * @return string
	 */
	public String talaAcessibilidade() {
		return ConstantesWeb.PAGE_ACESSIBILIDADE;
	}

	/**
	 * Redireciona para a tela de acessibilidade.
	 * 
	 * @return string
	 */
	public String talaInicial() {
		if (isUsuarioLogado()) {
			return ConstantesWeb.PAGE_HOME;
		}
		return ConstantesWeb.PAGE_LOGIN;
	}

	/** 
	 * Returna true se o usuario for administrador.
	 * @return boolean 
	 */
	public boolean isAdministrador(){
		if (getUsuarioAutenticado() != null){
			for (Perfil perfil : getUsuarioAutenticado().getPerfis()) {
				if (perfil.getId().equals(ADMINISTRADOR)){
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean isPossuiAcessoAdministrador(String codFuncionalidade){
		return isPossuiAcessoFuncionalidade(codFuncionalidade, TipoNivelAcesso.ADMINISTRADOR);
	}
	
	public boolean isPossuiAcessoFuncionalidade(String codFuncionalidade, TipoNivelAcesso nivelAcessoParametro){
		return usuarioServico.isPossuiAcessoFuncionalidadeServico(getUsuarioAutenticado(), codFuncionalidade, nivelAcessoParametro);
	}

	public boolean isPossuiAcessoFuncionalidade(String codFuncionalidade){
		return usuarioServico.isPossuiAcessoFuncionalidadeServico(getUsuarioAutenticado(), codFuncionalidade);
	}

	public boolean isPossuiAcessoConteudo(){
		return 	isPossuiAcessoFuncionalidade(TipoFuncionalidade.DICIONARIO.getCodigo()) ||
				isPossuiAcessoFuncionalidade(TipoFuncionalidade.PARCEIROS.getCodigo()) ||
				isPossuiAcessoFuncionalidade(TipoFuncionalidade.PROJETOS.getCodigo());
	}
	
	public boolean isPossuiAcessoMultimidia(){
		return 	isPossuiAcessoFuncionalidade(TipoFuncionalidade.CARTILHAS.getCodigo()) ||
				isPossuiAcessoFuncionalidade(TipoFuncionalidade.DOWNLOADS.getCodigo()) ||
				isPossuiAcessoFuncionalidade(TipoFuncionalidade.VIDEOS.getCodigo());
	}
	
	public boolean isPossuiAcessoGerenciamento(){
		return 	isPossuiAcessoFuncionalidade(TipoFuncionalidade.USUARIOS.getCodigo()) ||
				isPossuiAcessoFuncionalidade(TipoFuncionalidade.PERFIL.getCodigo()) ||
				isPossuiAcessoFuncionalidade(TipoFuncionalidade.TEMAS.getCodigo()) ||
				isPossuiAcessoFuncionalidade(TipoFuncionalidade.DADOS_EM_BLOCO.getCodigo()) ||
				isPossuiAcessoFuncionalidade(TipoFuncionalidade.FUNCIONALIDADE.getCodigo())||
				isPossuiAcessoFuncionalidade(TipoFuncionalidade.SERVICOS.getCodigo())||
				isPossuiAcessoFuncionalidade(TipoFuncionalidade.CARGOS.getCodigo()) ||
				isPossuiAcessoFuncionalidade(TipoFuncionalidade.MENSAGERIAS.getCodigo());
	}
	
	/**
	 * Recupera o login.
	 * 
	 * @return o atributo login
	 */
	public String getLogin() {
		return login;
	}

	/**
	 * Preenche login.
	 * 
	 * @param login	
	 *            preenche o atributo login
	 */
	public void setLogin(String login) {
		this.login = login;
	}

	/**
	 * Recupera o senha.
	 * 
	 * @return o atributo senha
	 */
	public String getSenha() {
		return senha;
	}

	/**
	 * Preenche senha.
	 * 
	 * @param senha
	 *            preenche o atributo senha
	 */
	public void setSenha(String senha) {
		this.senha = senha;
	}

	/**
	 * O método principal.
	 * 
	 * @param args
	 *            - args
	 */
	public static void main(String[] args) {
		System.out.println(Math.scalb(1, 63));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.gov.dpu.epaj.bean.EpajBean#getTelaPesquisa()
	 */
	@Override
	public String getTelaPesquisa() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.gov.dpu.epaj.bean.EpajBean#getTelaCadastro()
	 */
	@Override
	protected String getTelaCadastro() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void efetuarPesquisa() throws AtlasAcessoBancoDadosException {
		// TODO Auto-generated method stub

	}

	@Override
	protected UsuarioServico getServico() {
		return usuarioServico;
	}

	public Usuario getUsuarioAutenticado() {
		if(usuarioAutenticado == null){
			usuarioAutenticado = ((PageController) getBean("pageController")).getUsuario();
		}
		return usuarioAutenticado;
	}

	public void setUsuarioAutenticado(Usuario usuarioAutenticado) {
		this.usuarioAutenticado = usuarioAutenticado;
	}

	public TipoFuncionalidade getTipoFuncionalidade() {
		return tipoFuncionalidade;
	}

	public void setTipoFuncionalidade(TipoFuncionalidade tipoFuncionalidade) {
		this.tipoFuncionalidade = tipoFuncionalidade;
	}

	public AtlasLazyDataModel<Orgao> getOrgaosPesquisa() {
		return orgaosPesquisa;
	}

	public void setOrgaosPesquisa(AtlasLazyDataModel<Orgao> orgaosPesquisa) {
		this.orgaosPesquisa = orgaosPesquisa;
	}

}
