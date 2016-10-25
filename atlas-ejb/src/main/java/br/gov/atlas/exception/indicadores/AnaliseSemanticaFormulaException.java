package br.gov.atlas.exception.indicadores;

import br.gov.atlas.exception.AtlasException;

/**
 * Exceção que deverá ser lançada quando ocorrer um erro na Análise Semântica da fórmula.
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
