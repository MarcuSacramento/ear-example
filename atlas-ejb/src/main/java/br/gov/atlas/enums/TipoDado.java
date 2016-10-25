package br.gov.atlas.enums;

import java.util.HashMap;
import java.util.Map;

public enum TipoDado {
	A('A', "Alfanumérico"), D('D', "Data"), M('M', "Moeda"), N('N', "Numérico");

	private Character tipo;

	private String nome;

	private static Map<Character, TipoDado> values;

	/**
	 * Construtor padrão
	 */
	private TipoDado(Character tipo, String nome) {
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
	 * Recupera o tipo dado.
	 * 
	 * @param tipo - tipo
	 * @return tipo dado
	 */
	public static TipoDado getTipoDado(Character tipo) {
		if (values == null) {
			values = new HashMap<Character, TipoDado>();
			values.put(A.getTipo(), A);
			values.put(D.getTipo(), D);
			values.put(M.getTipo(), M);
			values.put(N.getTipo(), N);
		}
		return values.get(tipo);
	}

}
