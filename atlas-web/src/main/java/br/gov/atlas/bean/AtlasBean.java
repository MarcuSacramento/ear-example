package br.gov.atlas.bean;

import java.io.Serializable;
import java.text.MessageFormat;

import javax.el.ELContext;
import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.context.FacesContext;

import br.gov.atlas.constantes.ConstantesWeb;
import br.gov.atlas.exception.AtlasAcessoBancoDadosException;
import br.gov.atlas.exception.AtlasException;
import br.gov.atlas.servico.AtlasServicoImpl;
import br.gov.atlas.validacao.AtlasMensagem;

/**
 * Class AtlasBean.
 * 
 * @param <ENTITY> - tipo genérico
 */
public abstract class AtlasBean extends AtlasFacesBean implements Serializable {

	private static final long serialVersionUID = -8401584605138285112L;

	/**
	 * Novo registro.
	 * @return 
	 */
	public final String novoRegistro() {
		try {
			inicializarObjetos();
		} catch (AtlasAcessoBancoDadosException e) {
			error(e);
		}
		return getTelaCadastro();
	}

	/**
	 * Cancelar.
	 * @return 
	 */
	public String cancelar() {
		return getTelaPesquisa();
	}

	/**
	 * Pesquisa.
	 */
	public void pesquisar() {
		try {
			efetuarPesquisa();
		} catch (AtlasAcessoBancoDadosException e) {
			e.printStackTrace();
			error(e);
		}
	}

	/**
	 * Efetuar pesquisa.
	 * 
	 * @throws AtlasAcessoBancoDadosException - atlas acesso banco dados exception
	 */
	protected abstract void efetuarPesquisa() throws AtlasAcessoBancoDadosException;

	/**
	 * Recupera o servico.
	 * 
	 * @return servico
	 */
	protected abstract AtlasServicoImpl getServico();

	/**
	 * Recupera o tela pesquisa.
	 * 
	 * @return tela pesquisa
	 */
	protected abstract String getTelaPesquisa();

	/**
	 * Recupera o tela cadastro.
	 * 
	 * @return tela cadastro
	 */
	protected abstract String getTelaCadastro();

	/**
	 * Inicializar objetos.
	 * 
	 * @throws AtlasAcessoBancoDadosException - atlas acesso banco dados exception
	 */
	public void inicializarObjetos() throws AtlasAcessoBancoDadosException {}

	/**
	 * Recupera o bean.
	 * 
	 * @param <T> - tipo genï¿½rico
	 * @param name - name
	 * @return bean
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getBean(String name) {
		if (FacesContext.getCurrentInstance() == null) {
			return null;
		}
		ELContext elContext = FacesContext.getCurrentInstance().getELContext();
		return (T) FacesContext.getCurrentInstance().getApplication().getELResolver().getValue(elContext, null, name);
	}

	/**
	 * Mï¿½todo que verifica se existe usuï¿½rio logado.
	 * 
	 * @return true/false
	 */
	public boolean isUsuarioLogado() {
		return getSession().getAttribute(ConstantesWeb.USUARIO_LOGADO) != null;
	}

	/**
	 * Cria uma mensagem de INFO a partir do cï¿½digo da mensagem passado como parï¿½metro.
	 * 
	 * @param codigoMensagem - codigo mensagem
	 */
	protected void info(String codigoMensagem) {
		this.info(null, codigoMensagem, new Object[] {});
	}
	
	protected void info(String campo, String codigoMensagem) {
		this.info(campo, codigoMensagem, new Object[] {});
	}

	/**
	 * Cria uma mensagem de INFO a partir do cï¿½digo da mensagem e dos argumentos passados como parï¿½metros.
	 * 
	 * @param codigoMensagem - {@link String}
	 * @param args - {@link Object}...
	 */
	protected void info(String campo, String codigoMensagem, Object... args) {
		MessageFormat formato = new MessageFormat(msg.getString(codigoMensagem));
		this.addFacesMessage(campo, FacesMessage.SEVERITY_INFO, formato.format(args));
	}

	/**
	 * Cria uma mensagem de WARN a partir do cï¿½digo da mensagem passado como parï¿½metro.
	 * 
	 * @param codigoMensagem - codigo mensagem
	 */
	protected void warn(String codigoMensagem) {
		this.warn(null, codigoMensagem, new Object[] {});
	}

	protected void warn(String campo, String codigoMensagem) {
		this.warn(campo, codigoMensagem, new Object[] {});
	}
	/**
	 * Cria uma mensagem de WARN a partir do cï¿½digo da mensagem e dos argumentos passados como parï¿½metros.
	 * 
	 * @param codigoMensagem - {@link String}
	 * @param args - {@link Object}...
	 */
	protected void warn(String campo, String codigoMensagem, Object... args) {
		MessageFormat formato = new MessageFormat(msg.getString(codigoMensagem));
		this.addFacesMessage(campo, FacesMessage.SEVERITY_WARN, formato.format(args));
	}

	/**
	 * Cria uma mensagem de ERROR a partir do cï¿½digo da mensagem passado como parï¿½metro.
	 * 
	 * @param codigoMensagem - codigo mensagem
	 */
	protected void error(String campo, String codigoMensagem) {
		this.error(campo, codigoMensagem, new Object[] {});
	}
	
	protected void error(String codigoMensagem) {
		this.error(null, codigoMensagem, new Object[] {});
	}
	
	/**
	 * Cria uma mensagem de ERROR a partir da exceï¿½ï¿½o AtlasException.
	 * 
	 * @param e - e
	 */
	protected void error(AtlasException e) {
		if (e.getParametros() == null) {
			error(e.getCampo(), e.getCodigoMensagem());
		} else {
			error(e.getCampo(), e.getCodigoMensagem(), e.getParametros());
		}
	}

	/**
	 * Cria uma mensagem de ERROR a partir do cï¿½digo da mensagem e dos argumentos passados como parï¿½metros.
	 * 
	 * @param codigoMensagem - {@link String}
	 * @param args - {@link Object}...
	 */
	protected void error(String campo, String codigoMensagem, Object... args) {
		MessageFormat formato = new MessageFormat(msg.getString(codigoMensagem));
		this.addFacesMessage(campo, FacesMessage.SEVERITY_ERROR, formato.format(args));
	}

	/**
	 * Add atlas message.
	 * 
	 * @param atlasMsg - atlas msg
	 */
	protected void addAtlasMessage(String campo, AtlasMensagem atlasMsg) {
		if (atlasMsg.getSeverity().equals(FacesMessage.SEVERITY_ERROR)) {
			error(campo, atlasMsg.getCodMensagem(), atlasMsg.getParams());
		} else if (atlasMsg.getSeverity().equals(FacesMessage.SEVERITY_INFO)) {
			info(campo, atlasMsg.getCodMensagem(), atlasMsg.getParams());
		} else if (atlasMsg.getSeverity().equals(FacesMessage.SEVERITY_WARN)) {
			warn(campo, atlasMsg.getCodMensagem(), atlasMsg.getParams());
		}
	}

	/**
	 * Adiciona a mensagem no contexto do faces.
	 * 
	 * @param severity - severity
	 * @param mensagem - mensagem
	 */
	private void addFacesMessage(String campo, Severity severity, String mensagem) {
		if (mensagem != null) {
			mensagem = mensagem.replace("\n", "<br/>");
		}
		getFacesContext().addMessage(campo, new FacesMessage(severity, mensagem, null));
	}

	
}
