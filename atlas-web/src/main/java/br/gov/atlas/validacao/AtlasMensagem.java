package br.gov.atlas.validacao;

import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;

/**
 * Class AtlasMensagem - Encapsula as mensagens do JSF para serem apresentadas na tela..
 */
public final class AtlasMensagem {

	private Severity severity;
	private String codMensagem;
	private Object[] params;
	private static AtlasMensagem instance;

	/**
	 * Cria uma nova instância de atlas mensagem.
	 */
	private AtlasMensagem() {}

	/**
	 * Recupera a instância de AtlasMensagem.
	 * 
	 * @return a instância de AtlasMensagem
	 */
	public static AtlasMensagem getInstance() {
		if (instance == null) {
			instance = new AtlasMensagem();
		}
		return instance;
	}

	/**
	 * Cria mensagem info.
	 * 
	 * @param codMensagem - cod mensagem
	 * @return atlas mensagem
	 */
	public AtlasMensagem criaMensagemINFO(String codMensagem) {
		return criaMensagemINFO(codMensagem, new Object[] {});
	}

	/**
	 * Cria mensagem info.
	 * 
	 * @param codMensagem - cod mensagem
	 * @param params - params
	 * @return atlas mensagem
	 */
	public AtlasMensagem criaMensagemINFO(String codMensagem, Object... params) {
		this.severity = FacesMessage.SEVERITY_INFO;
		this.codMensagem = codMensagem;
		if (params.length != 0) {
			this.params = params;
		}
		return instance;
	}

	/**
	 * Cria mensagem warn.
	 * 
	 * @param codMensagem - cod mensagem
	 * @return atlas mensagem
	 */
	public AtlasMensagem criaMensagemWARN(String codMensagem) {
		return criaMensagemWARN(codMensagem, new Object[] {});
	}

	/**
	 * Cria mensagem warn.
	 * 
	 * @param codMensagem - cod mensagem
	 * @param params - params
	 * @return atlas mensagem
	 */
	public AtlasMensagem criaMensagemWARN(String codMensagem, Object... params) {
		this.severity = FacesMessage.SEVERITY_WARN;
		this.codMensagem = codMensagem;
		if (params.length != 0) {
			this.params = params;
		}
		return instance;
	}

	/**
	 * Cria mensagem error.
	 * 
	 * @param codMensagem - cod mensagem
	 * @return atlas mensagem
	 */
	public AtlasMensagem criaMensagemERROR(String codMensagem) {
		return criaMensagemERROR(codMensagem, new Object[] {});
	}

	/**
	 * Cria mensagem error.
	 * 
	 * @param codMensagem - cod mensagem
	 * @param params - params
	 * @return atlas mensagem
	 */
	public AtlasMensagem criaMensagemERROR(String codMensagem, Object... params) {
		this.severity = FacesMessage.SEVERITY_ERROR;
		this.codMensagem = codMensagem;
		if (params.length != 0) {
			this.params = params;
		}
		return instance;
	}

	/**
	 * Recupera o severity.
	 * 
	 * @return severity
	 */
	public Severity getSeverity() {
		return severity;
	}

	/**
	 * Recupera o cod mensagem.
	 * 
	 * @return cod mensagem
	 */
	public String getCodMensagem() {
		return codMensagem;
	}

	/**
	 * Preenche cod mensagem.
	 * 
	 * @param messagem - messagem
	 */
	public void setCodMensagem(String messagem) {
		this.codMensagem = messagem;
	}

	/**
	 * Recupera o params.
	 * 
	 * @return params
	 */
	public Object[] getParams() {
		return params;
	}

	/**
	 * Preenche params.
	 * 
	 * @param params - params
	 */
	public void setParams(Object... params) {
		this.params = params;
	}

}
