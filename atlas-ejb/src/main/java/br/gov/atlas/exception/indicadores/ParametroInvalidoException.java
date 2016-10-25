package br.gov.atlas.exception.indicadores;

import br.gov.atlas.exception.AtlasException;

/**
 * Exce��o que dever� ser lan�ada no momento um par�metro da formula n�o seja identificado.
 */
public class ParametroInvalidoException extends AtlasException {

	private static final long serialVersionUID = -9129985034956825203L;

	/**
	 * Construtor da classe ParametroInvalidoException
	 * @param mensagem
	 */
	public ParametroInvalidoException(String mensagem) {
		super(mensagem);
	}

}
