package br.gov.atlas.exception.indicadores;

import br.gov.atlas.exception.AtlasException;

/**
 * Exce��o que dever� ser lan�ada quando ocorrer um erro na An�lise Sint�tica da f�rmula.
 */
public class AnaliseSintaticaFormulaException extends AtlasException {

	private static final long serialVersionUID = 822327558725597675L;

	/**
	 * Construtor da classe AnaliseSintaticaFormulaException
	 */
	public AnaliseSintaticaFormulaException() {
		super("ANALISE SINTATICA - Formula invalida: ");
	}

	/**
	 * Construtor da classe AnaliseSintaticaFormulaException
	 * @param mensagem
	 */
	public AnaliseSintaticaFormulaException(String mensagem) {
		super(mensagem);
	}
	
}
