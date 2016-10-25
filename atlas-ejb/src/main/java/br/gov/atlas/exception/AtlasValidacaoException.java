package br.gov.atlas.exception;

public class AtlasValidacaoException extends AtlasException {

	private static final long serialVersionUID = 3643269481177991701L;

	/**
	 * Construtor da classe.
	 * 
	 * @param codigoMensagem
	 */
	public AtlasValidacaoException(String codigoMensagem) {
		super(codigoMensagem);
	}

	/**
	 * Construtor da classe.
	 */
	public AtlasValidacaoException(String campo, String codigoMensagem, String... parametros) {
		super(campo, codigoMensagem, parametros);
	}

}
