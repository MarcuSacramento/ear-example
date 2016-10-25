package br.gov.atlas.exception.indicadores;

import br.gov.atlas.exception.AtlasException;

/**
 * Exceção que deverá ser lançada quando ocorrer um erro na Análise Léxica da fórmula.
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
