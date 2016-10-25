package br.gov.atlas.exception.indicadores;

import br.gov.atlas.exception.AtlasException;

/**
 * Exce��o que dever� ser lan�ada no momento um operador (+, -, * ou /) da formula n�o seja identificado.
 */
public class OperadorInvalidoException extends AtlasException {

	private static final long serialVersionUID = 6684226691202826391L;

	/**
	 * Construtor da classe OperadorInvalidoException
	 * @param mensagem
	 */
	public OperadorInvalidoException(String mensagem) {
		super(mensagem);
	}

}
