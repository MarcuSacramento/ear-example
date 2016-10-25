package br.gov.atlas.exception;

public class AtlasNegocioException extends AtlasException {

	private static final long serialVersionUID = -2347031811338077042L;

	/**
	 * Construtor da classe.
	 * 
	 * @param codigoMensagem
	 */
	public AtlasNegocioException(String codigoMensagem) {
		super(codigoMensagem);
	}
	
	public AtlasNegocioException(String campo, String codigoMensagem) {
		super(campo, codigoMensagem);
	}

	/**
	 * Construtor da classe.
	 */
	public AtlasNegocioException(String campo, String codigoMensagem, String... parametros) {
		super(campo, codigoMensagem, parametros);
	}

}
