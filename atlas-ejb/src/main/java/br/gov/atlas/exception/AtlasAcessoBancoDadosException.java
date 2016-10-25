package br.gov.atlas.exception;

public class AtlasAcessoBancoDadosException extends AtlasException {

	private static final long serialVersionUID = -8583197750553358874L;

	/**
	 * Construtor da classe.
	 * 
	 * 
	 */
	public AtlasAcessoBancoDadosException(String codigoMensagem) {
		super(codigoMensagem);
	}

	/**
	 * Construtor da classe.
	 */
	public AtlasAcessoBancoDadosException(String codigoMensagem, String... parametros) {
		super(null, codigoMensagem, parametros);
	}

}