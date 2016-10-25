package br.gov.atlas.seguranca.bean;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.servlet.http.HttpServletRequest;

import br.gov.atlas.bean.AtlasCrudBean;
import br.gov.atlas.entidade.Usuario;
import br.gov.atlas.exception.AtlasAcessoBancoDadosException;
import br.gov.atlas.exception.AtlasNegocioException;
import br.gov.atlas.servico.AtlasServicoImpl;
import br.gov.atlas.servico.UsuarioServico;
import br.gov.atlas.servico.WebMailServico;
import br.gov.atlas.util.Valida;

/**
 * Class redefinirSenhaUsuarioBean.
 */
@ManagedBean(name = "redefinirSenhaUsuarioBean")
@SessionScoped
public class RedefinirSenhaUsuarioBean extends AtlasCrudBean<Usuario> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 786975851425583687L;

	@EJB(name = "UsuarioServico")
	private UsuarioServico usuarioServico;
	
	@EJB(name = "WebMailServico")
	private WebMailServico mailServico;
	
	private String emailInformado;
	
	private String novaSenha;

	private boolean ok = false;
	
	public void enviarSenha() {
		Usuario usuario;
		getRequestURL();
		try {
			usuario = usuarioServico.recuperarEmailPotUsuario(emailInformado);
			if (usuario == null){
				error("MSG2");
				return;
			}
			novaSenha = calcularHashParaSenha(usuario.getNome()+System.currentTimeMillis()).substring(0, 6);
			if (usuario.getEmail().equalsIgnoreCase(emailInformado)) {
				mailServico.sendAlteracaoSenha(getRequestURL(), usuario.getNome(), novaSenha, usuario.getEmail());
				alterarSenha(usuario);
			}
		} catch (AtlasAcessoBancoDadosException e) {
			error(null,"MSG0",e.getMessage());
			e.printStackTrace();
			return;
		} catch (AddressException e) {
			error(null,"MSG0",e.getMessage());
			e.printStackTrace();
			return;
		} catch (MessagingException e) {
			error(null,"MSG0",e.getMessage());
			e.printStackTrace();
			return;
		} catch (Exception e) {
			error(null,"MSG0",e.getMessage());
			e.printStackTrace();
			return;
		}
		getFacesContext()
				.addMessage(
						null,
						new FacesMessage(
								FacesMessage.SEVERITY_INFO,
								"Foi enviada uma nova senha ao email informado. Favor verificar.",
								null));
		setOk(true);
	}
	
	public String getRequestURL() {
		String url = "";
		Object request = FacesContext.getCurrentInstance().getExternalContext().getRequest();
		if (request instanceof HttpServletRequest) {
			url = ((HttpServletRequest) request).getRequestURL().toString();
		} 
		String array[] = url.split("/");
		return array[0]+array[1]+"//"+array[2]+"/"+array[3];
	}
	
	public void alterarSenha(Usuario usuario) {
		setEntidade(usuario);				
		String hashParaSenha = calcularHashParaSenha(novaSenha);
		getEntidade().setSenha(hashParaSenha);
		super.salvarSemExibirMensagem();
	}
	
	private String calcularHashParaSenha(String senha) {
		String resultado = "";
		
		try {
			byte[] digest = MessageDigest.getInstance("MD5").digest(senha.getBytes());
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

	/**
	 * Redireciona para a tela de inicial.
	 * 
	 * @return string
	 */
	public String talaInicial() {
		setOk(false);
		return "/admin/autenticarUsuario/login.xhtm";
	}

	public String getEmailInformado() {
		return emailInformado;
	}
	
	public void setEmailInformado(String emailInformado) {
		this.emailInformado = emailInformado;
	}
	
	public String getNovaSenha() {
		return novaSenha;
	}
	
	public void setNovaSenha(String novaSenha) {
		this.novaSenha = novaSenha;
	}

	@Override
	public Boolean validar() throws AtlasNegocioException {
		
		if (emailInformado.isEmpty()) {
			error(null, "MSG20", "Campo E-mail:");
			return false;
		}
			
		if (!Valida.validaEmail(emailInformado)) {
			error(null, "MSG8");
			return false;
		}
		
		return true;
	}

	@Override
	protected void efetuarPesquisa() throws AtlasAcessoBancoDadosException {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected AtlasServicoImpl getServico() {
		// TODO Auto-generated method stub
		return usuarioServico;
	}

	@Override
	protected String getTelaPesquisa() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String getTelaCadastro() {
		// TODO Auto-generated method stub
		return null;
	}


	public boolean isOk() {
		return ok;
	}


	public void setOk(boolean ok) {
		this.ok = ok;
	}
		
}
