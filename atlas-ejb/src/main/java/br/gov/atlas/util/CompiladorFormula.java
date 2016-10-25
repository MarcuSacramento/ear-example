package br.gov.atlas.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.gov.atlas.entidade.Indicador;
import br.gov.atlas.entidade.Parametro;
import br.gov.atlas.exception.AtlasAcessoBancoDadosException;
import br.gov.atlas.exception.indicadores.AnaliseLexicaFormulaException;
import br.gov.atlas.exception.indicadores.AnaliseSintaticaFormulaException;
import br.gov.atlas.exception.indicadores.OperadorInvalidoException;
import br.gov.atlas.exception.indicadores.ParametroInvalidoException;
import br.gov.atlas.servico.ParametroServico;

public class CompiladorFormula {
	
	private int parenteses = 0;

	private Map<String, Parametro> mapParametros;

	/**
	 * Construtor do compilador.
	 * @param parametroServico
	 */
	@SuppressWarnings("unchecked")
	public CompiladorFormula(ParametroServico parametroServico) {
		mapParametros = new HashMap<>();
		
		try {
			List<Parametro> parametros = parametroServico.buscarTodos(Parametro.class);
			
			for (Parametro parametro : parametros) {
				mapParametros.put(parametro.getNome(), parametro);
			}
			
		} catch (AtlasAcessoBancoDadosException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Compila a formula do indicador informado, efetuando as etapas de:
	 * - Análise Léxica da expressão;
	 * - Análise Sintática da expressão e
	 * - Análise Semântica da expressão.
	 * 
	 * @param indicador
	 * 
	 * @throws AnaliseLexicaFormulaException 
	 * @throws AnaliseSintaticaFormulaException 
	 */
	public void compilarFormula(Indicador indicador) throws AnaliseLexicaFormulaException, AnaliseSintaticaFormulaException {
		List<Token> tokens = new ArrayList<Token>();
			
		try {
			tokens = efetuarAnaliseLexica(indicador);
		} catch (AnaliseLexicaFormulaException e) {
			System.out.println(e.getMessage());
			throw e;
		}
		
		try {
			parenteses = 0;
			efetuarAnaliseSintatica(tokens);
			
			if (parenteses > 0){
				throw new AnaliseSintaticaFormulaException("ANALISE SINTATICA - Parentese aberto e nao fechado na formula: ");
			}
			
		} catch (AnaliseSintaticaFormulaException e) {
			System.out.println(e.getMessage() + indicador.getFormula());
			throw e;
		}
		
		efetuaAnaliseSemantica(indicador, tokens);
		
	}

	/**
	 * Efetua a ANÁLISE LÉXICA da formula do Indicador.
	 * Com base nessa análise é possível verificar se os "tokens" informados na fôrmula são válidos.
	 *  
	 * @param indicador
	 * 
	 * @return lista de tokens
	 * 
	 * @throws AnaliseLexicaFormulaException
	 */
	protected List<Token> efetuarAnaliseLexica(Indicador indicador) throws AnaliseLexicaFormulaException {
		// le os parametros, operadores e parenteses e cria os tokens
		List<Token> tokens = getTokensFormula(indicador);
		
		System.out.println("Tokens: " + tokens);
		System.out.println("ANALISE LEXICA efetuada com sucesso!");
		return tokens;
	}
	
	/**
	 * Recupera os tokens da fórmula do indicador, ou seja, cada parte da fórmula, que pode ser:
	 * - Operador Simples (+, -, * e /)
	 * - Numeral
	 * - Parametro
	 * - Parenteses: ( e )
	 * 
	 * @param indicador
	 * 
	 * @return lista de tokens
	 * 
	 * @throws AnaliseLexicaFormulaException
	 */
	private List<Token> getTokensFormula(Indicador indicador) throws AnaliseLexicaFormulaException {
		List<Token> tokens = new ArrayList<Token>();
		String formula = indicador.getFormula().toUpperCase().replaceAll(" ", "");
		
		String token = "";
		String tipoTokenAnterior = Token.TOKEN_INICIO;
		String tipoTokenAtual = null;
		
		for (int i = 0; i < formula.toCharArray().length; i++) {
			Character caracter = formula.toCharArray()[i];
			
			if (Character.isLetter(caracter) || caracter.equals('_')){
				tipoTokenAtual = Token.TOKEN_PARAMETRO;
			} else if (isOperadorSimples(caracter)){
				tipoTokenAtual = Token.TOKEN_OPERADOR;
			} else if (caracter.equals('(')){
				tipoTokenAtual = Token.TOKEN_ABRE_PARENTESES;
			} else if (caracter.equals(')')){
				tipoTokenAtual = Token.TOKEN_FECHA_PARENTESES;
			} else if (Character.isDigit(caracter) || 
							(caracter.equals(',') && tipoTokenAnterior.equals(Token.TOKEN_NUMERAL))) {
				tipoTokenAtual = Token.TOKEN_NUMERAL;
			} else {
				throw new AnaliseLexicaFormulaException("Erro ao analisar a formula: " + formula.substring(0, i));
			}
				
			if (!tipoTokenAtual.equals(tipoTokenAnterior) || tipoTokenAnterior.equals(Token.TOKEN_OPERADOR)
					|| tipoTokenAnterior.equals(Token.TOKEN_ABRE_PARENTESES) || tipoTokenAnterior.equals(Token.TOKEN_FECHA_PARENTESES)){
				atualizaListaTokens(tokens, token, tipoTokenAnterior);
				
				token = caracter.toString();
				
				tipoTokenAnterior = tipoTokenAtual;
			} else {
				token += caracter.toString();
			}
		}
		
		atualizaListaTokens(tokens, token, tipoTokenAnterior);
		
		return tokens;
	}

	/**
	 * Atualiza a lista de tokens.
	 * 
	 * @param lista de tokens
	 * @param token
	 * @param tipoTokenAnterior
	 */
	private void atualizaListaTokens(List<Token> tokens, String token, String tipoTokenAnterior) {
		if (!token.equals("")){
			tokens.add(new Token(token, tipoTokenAnterior));
		}
	}
	
	/**
	 * Efetua a ANÁLISE SINTÁTICA da fórmula do Indicador.
	 * Com base nessa análise é possível verificar se os "tokens" capturados na etapa anterior formam
	 * uma expressão válida, de acordo com a gramática das fórmulas.
	 * 
	 * @param lista de tokens
	 * 
	 * @throws AnaliseSintaticaFormulaException 
	 */
	protected void efetuarAnaliseSintatica(List<Token> tokens) throws AnaliseSintaticaFormulaException {
		
		try {
			// Valida a formula, de acordo com a gramatica
			
			if (tokens.get(0).getTipo().equals(Token.TOKEN_PARAMETRO)){
				validaParametro(tokens, 0);
				
			} else if (tokens.get(0).getTipo().equals(Token.TOKEN_NUMERAL)){
				validaNumeral(tokens, 0);
				
			} else if (tokens.get(0).getTipo().equals(Token.TOKEN_ABRE_PARENTESES)){
				validaAberturaParenteses(tokens, 0);
				
			} else {
				throw new AnaliseSintaticaFormulaException();
			}
			
			System.out.println("ANALISE SINTATICA efetuada com sucesso!");
			
		} catch (ParametroInvalidoException e) {
			throw new AnaliseSintaticaFormulaException(e.getMessage());
		} catch (OperadorInvalidoException e) {
			throw new AnaliseSintaticaFormulaException(e.getMessage());
		} catch (IndexOutOfBoundsException e) {
			throw new AnaliseSintaticaFormulaException(e.getMessage());
		}
		
	}

	/**
	 * Valida se o parametro recuperado é valido.
	 * 
	 * @param lista de tokens
	 * @param indice
	 * 
	 * @throws ParametroInvalidoException
	 * @throws OperadorInvalidoException
	 * @throws AnaliseSintaticaFormulaException 
	 */
	private void validaParametro(List<Token> tokens, int indice) throws ParametroInvalidoException, OperadorInvalidoException, AnaliseSintaticaFormulaException {
		Token token = tokens.get(indice);
		
		Parametro parametro = mapParametros.get(token.getToken());
		if (parametro == null){
			throw new ParametroInvalidoException("ANALISE SINTATICA - Parametro '" + token.getToken() + "' nao localizado na Base de Dados");
		}
		
		verificaSeProximoToken_OperadorOuParenteses(tokens, indice);
	}

	/**
	 * Faz uma validação simples do numeral.
	 * 
	 * @param lista de tokens
	 * @param indice
	 * 
	 * @throws ParametroInvalidoException
	 * @throws OperadorInvalidoException
	 * @throws AnaliseSintaticaFormulaException 
	 */
	private void validaNumeral(List<Token> tokens, int indice) throws ParametroInvalidoException, OperadorInvalidoException, AnaliseSintaticaFormulaException {
		Token token = tokens.get(indice);

		if (token.getToken().endsWith(",")){
			token.setToken(token.getToken() + "0");
		}
		
		verificaSeProximoToken_OperadorOuParenteses(tokens, indice);
	}

	/**
	 * Verifica se o operador informado no token é válido.
	 * 
	 * @param lista de tokens
	 * @param indice
	 * 
	 * @throws OperadorInvalidoException
	 * @throws ParametroInvalidoException
	 * @throws AnaliseSintaticaFormulaException 
	 */
	private void validaOperador(List<Token> tokens, int indice) throws OperadorInvalidoException, ParametroInvalidoException, AnaliseSintaticaFormulaException {
		Token token = tokens.get(indice);
		
		if (token.getToken().length() > 1 || !isOperadorSimples(token.getToken().toCharArray()[0])){
			throw new OperadorInvalidoException("Operador Simples '" + token.getToken() + "' invalido");
		}
		
		indice++;
		
		if (tokens.get(indice).getTipo().equals(Token.TOKEN_PARAMETRO)){
			validaParametro(tokens, indice);
		} else if (tokens.get(indice).getTipo().equals(Token.TOKEN_NUMERAL)){
			validaNumeral(tokens, indice);
		} else if (tokens.get(indice).getTipo().equals(Token.TOKEN_ABRE_PARENTESES)){
			validaAberturaParenteses(tokens, indice);
		} else {
			throw new AnaliseSintaticaFormulaException();
		}
	}

	/**
	 * Valida a abertura do parenteses, atualizando o contador de parenteses
	 * 
	 * @param lista de tokens
	 * @param indice
	 * 
	 * @throws ParametroInvalidoException
	 * @throws OperadorInvalidoException
	 * @throws AnaliseSintaticaFormulaException
	 */
	private void validaAberturaParenteses(List<Token> tokens, int indice) throws ParametroInvalidoException, OperadorInvalidoException, AnaliseSintaticaFormulaException {
		indice++;
		parenteses++;
		
		if (tokens.get(indice).getTipo().equals(Token.TOKEN_PARAMETRO)){
			validaParametro(tokens, indice);
		} else if (tokens.get(indice).getTipo().equals(Token.TOKEN_NUMERAL)){
			validaNumeral(tokens, indice);
		} else if (tokens.get(indice).getTipo().equals(Token.TOKEN_ABRE_PARENTESES)){
			validaAberturaParenteses(tokens, indice);
		} else {
			throw new AnaliseSintaticaFormulaException();
		}
	}
	
	/**
	 * Valida o fechamento do parenteses, atualizando o contador de parenteses.
	 * 
	 * @param tokens
	 * @param indice
	 * 
	 * @throws AnaliseSintaticaFormulaException 
	 * @throws ParametroInvalidoException 
	 * @throws OperadorInvalidoException 
	 */
	private void validaFechamentoParenteses(List<Token> tokens, int indice) throws AnaliseSintaticaFormulaException, OperadorInvalidoException, ParametroInvalidoException {
		parenteses--;
		
		verificaSeProximoToken_OperadorOuParenteses(tokens, indice);
	}

	/**
	 * Verifica se o próximo token, caso exista é um OPERADOR ou FECHAMENTO DE PARENTESES.
	 * 
	 * @param lista de tokens
	 * @param indice
	 * 
	 * @throws OperadorInvalidoException
	 * @throws ParametroInvalidoException
	 * @throws AnaliseSintaticaFormulaException
	 */
	private void verificaSeProximoToken_OperadorOuParenteses(List<Token> tokens, int indice) throws OperadorInvalidoException, 
			ParametroInvalidoException, AnaliseSintaticaFormulaException {
		if (++indice < tokens.size()) {
			if (tokens.get(indice).getTipo().equals(Token.TOKEN_OPERADOR)){
				validaOperador(tokens, indice);
			} else if (tokens.get(indice).getTipo().equals(Token.TOKEN_FECHA_PARENTESES)){
				validaFechamentoParenteses(tokens, indice);
			} else {
				throw new AnaliseSintaticaFormulaException();
			}
		}
	}

	/**
	 * Verifica se caracter informado é um operador simples, ou seja: +, -, * ou /
	 * @param caracter
	 * @return true/false
	 */
	private boolean isOperadorSimples(Character caracter){
		return caracter.equals('+') || caracter.equals('-') || caracter.equals('*') || caracter.equals('/'); 
	}

	/**
	 * Efetua a analise semantica, interpretando a formula.
	 * 
	 * @param indicador
	 * @param tokens
	 */
	protected void efetuaAnaliseSemantica(Indicador indicador, List<Token> tokens) {
		String formulaInterpretada = "";
		for (Token token : tokens) {
			if (token.getTipo().equals(Token.TOKEN_PARAMETRO)){
				formulaInterpretada += mapParametros.get(token.getToken()).getValor() + " ";
			} else {
				formulaInterpretada += token.getToken();
			}
		}
		indicador.setFormulaInterpretada(formulaInterpretada);
	}


}
