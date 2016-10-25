package br.gov.atlas.enums;

import java.util.HashMap;
import java.util.Map;

public enum TipoResposta {
	U('U', "Univalorada"), M('M', "Multivalorada"), T('T', "Texto Livre");

	private Character tipo;

	private String nome;

	private static Map<Character, TipoResposta> values;

	/**
	 * Construtor padrão
	 */
	private TipoResposta(Character tipo, String nome) {
		this.tipo = tipo;
		this.nome = nome;
	}

	/**
	 * @return o atributo tipo
	 */
	public Character getTipo() {
		return tipo;
	}

	/**
	 * @return o atributo nome
	 */
	public String getNome() {
		return nome;
	}

	/**
	 * @return o label
	 */
	public String getLabel() {
		return getNome();
	}

	/**
	 * Recupera o tipo resposta.
	 * 
	 * @param tipo - tipo
	 * @return tipo resposta
	 */
	public static TipoResposta getTipoResposta(Character tipo) {
		if (values == null) {
			values = new HashMap<Character, TipoResposta>();
			values.put(U.getTipo(), U);
			values.put(M.getTipo(), M);
			values.put(T.getTipo(), T);
		}
		return values.get(tipo);
	}

}
