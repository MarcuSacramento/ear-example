package br.gov.atlas.exception.indicadores;

import br.gov.atlas.exception.AtlasException;

/**
 * Exce��o que dever� ser lan�ada quando ocorrer um erro na An�lise Sem�ntica da f�rmula.
 */
public class AnaliseSemanticaFormulaException extends AtlasException {

	private static final long serialVersionUID = -4221456443580476134L;

	/**
	 * Construtor da classe AnaliseSemanticaFormulaException
	 * @param mensagem
	 */
	public AnaliseSemanticaFormulaException(String mensagem) {
		super(mensagem);
	}
	
}
