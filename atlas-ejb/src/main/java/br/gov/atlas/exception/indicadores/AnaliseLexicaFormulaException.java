package br.gov.atlas.exception.indicadores;

import br.gov.atlas.exception.AtlasException;

/**
 * Exce��o que dever� ser lan�ada quando ocorrer um erro na An�lise L�xica da f�rmula.
 */
public class AnaliseLexicaFormulaException extends AtlasException {

	private static final long serialVersionUID = 6702638810277530500L;

	/**
	 * Construtor da classe ParametroInvalidoException
	 * @param mensagem
	 */
	public AnaliseLexicaFormulaException(String mensagem) {
		super(mensagem);
	}
	
}
