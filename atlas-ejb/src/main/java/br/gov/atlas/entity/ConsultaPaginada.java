package br.gov.atlas.entity;

import java.util.HashMap;
import java.util.Map;

public class ConsultaPaginada {

	private String queryString;
	private String hqlString;
	private Integer primeiroRegistro;

	private Map<String, Object> parametros;

	/**
	 * Construtor da classe.
	 */
	public ConsultaPaginada(Integer primeiroRegistro) {
		this.primeiroRegistro = primeiroRegistro;
	}

	/**
	 * @return o atributo queryString
	 */
	public String getQueryString() {
		return queryString;
	}

	/**
	 * @param queryString preenche o atributo queryString
	 */
	public void setQueryString(String queryString) {
		this.queryString = queryString;
	}

	/**
	 * @return o atributo hqlString
	 */
	public String getHqlString() {
		return hqlString;
	}

	/**
	 * @param hqlString preenche o atributo hqlString
	 */
	public void setHqlString(String hqlString) {
		this.hqlString = hqlString;
	}

	/**
	 * @return o atributo parametros
	 */
	public Map<String, Object> getParametros() {
		if (parametros == null) {
			parametros = new HashMap<String, Object>();
		}
		return parametros;
	}

	/**
	 * @param parametros preenche o atributo parametros
	 */
	public void setParametros(Map<String, Object> parametros) {
		this.parametros = parametros;
	}

	/**
	 * @return o atributo primeiroRegistro
	 */
	public Integer getPrimeiroRegistro() {
		return primeiroRegistro;
	}

}
