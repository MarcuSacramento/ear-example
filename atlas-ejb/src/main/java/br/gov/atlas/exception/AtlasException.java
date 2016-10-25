package br.gov.atlas.exception;

public abstract class AtlasException extends Exception {

	private static final long serialVersionUID = -8363805956162424155L;
	private String codigoMensagem;
	private Object[] parametros;
	private String campo;

	/**
	 * Construtor da classe.
	 */
	protected AtlasException() {
		// N�o instanciar as exce��es sem o c�digo da mensagem.
	}

	/**
	 * Construtor da classe.
	 */
	protected AtlasException(String codigoMensagem) {
		this(null, codigoMensagem, null);
	}

	/**
	 * Construtor da classe.
	 * 
	 * @param codigoMensagem
	 */
	public AtlasException(String campo, String codigoMensagem) {
		this(campo, codigoMensagem, null);
	}

	/**
	 * Construtor da classe.
	 * 
	 * @param codigoMensagem
	 * @param parametros
	 */
	public AtlasException(String campo, String codigoMensagem, Object[] parametros) {
		this.codigoMensagem = codigoMensagem;
		this.parametros = parametros;
		this.campo = campo;
	}

	/**
	 * @return o atributo codigoMensagem
	 */
	public String getCodigoMensagem() {
		return codigoMensagem;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Throwable#getMessage()
	 */
	@Override
	public String getMessage() {
		return this.codigoMensagem;
	}

	/**
	 * @return o atributo parametros
	 */
	public Object[] getParametros() {
		return parametros;
	}

	public String getCampo() {
		return campo;
	}

}
