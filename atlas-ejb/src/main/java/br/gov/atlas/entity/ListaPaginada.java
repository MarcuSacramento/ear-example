package br.gov.atlas.entity;

import java.util.ArrayList;
import java.util.List;

public class ListaPaginada<T> {

	private Integer primeiroRegistro;
	private Integer qtdRegistroPorPagina;
	private Integer qtdRegistros;
	private Integer qtdPaginas;

	private List<Object> resultadoPesquisa;
	private List<T> resultadoRetorno;

	/**
	 * construtor default
	 */
	public ListaPaginada() {
		this.qtdRegistroPorPagina = 10;
		this.qtdRegistros = 0;
		this.qtdPaginas = 0;
	}

	/**
	 * Construtor default
	 * 
	 * @param qtdRegistroPorPagina
	 */
	public ListaPaginada(Integer primeiroRegistro) {
		this();
		this.primeiroRegistro = primeiroRegistro;
	}

	/**
	 * @return o atributo qtdPaginas
	 */
	public Integer getQtdPaginas() {
		if (qtdPaginas == 0 && getQtdRegistros() != 0) {
			qtdPaginas = qtdRegistros / qtdRegistroPorPagina + ((qtdRegistros % qtdRegistroPorPagina == 0) ? 0 : 1);
		}

		return qtdPaginas;
	}

	/**
	 * @return o atributo resultadoPesquisa
	 */
	public List<Object> getResultadoPesquisa() {
		return resultadoPesquisa;
	}

	/**
	 * @param resultadoPesquisa preenche o atributo resultadoPesquisa
	 */
	public void setResultadoPesquisa(List<Object> resultadoPesquisa) {
		this.resultadoPesquisa = resultadoPesquisa;
	}

	/**
	 * @return o atributo resultadoRetorno
	 */
	public List<T> getResultadoRetorno() {
		if (resultadoRetorno == null) {
			resultadoRetorno = new ArrayList<T>();
		}
		return resultadoRetorno;
	}

	/**
	 * @param resultadoRetorno preenche o atributo resultadoRetorno
	 */
	public void setResultadoRetorno(List<T> resultadoRetorno) {
		this.resultadoRetorno = resultadoRetorno;
	}

	/**
	 * @return o atributo primeiroRegistro
	 */
	public Integer getPrimeiroRegistro() {
		if (primeiroRegistro == null) {
			primeiroRegistro = 0;
		}
		return primeiroRegistro;
	}

	/**
	 * @param primeiroRegistro preenche o atributo primeiroRegistro
	 */
	public void setPrimeiroRegistro(Integer primeiroRegistro) {
		this.primeiroRegistro = primeiroRegistro;
	}

	/**
	 * @return o atributo qtdRegistros
	 */
	public Integer getQtdRegistros() {
		return qtdRegistros;
	}

	/**
	 * @param qtdRegistros preenche o atributo qtdRegistros
	 */
	public void setQtdRegistros(Integer qtdRegistros) {
		this.qtdRegistros = qtdRegistros;
	}

	/**
	 * @return o atributo qtdRegistroPorPagina
	 */
	public Integer getQtdRegistroPorPagina() {
		return qtdRegistroPorPagina;
	}

	/**
	 * @param qtdRegistroPorPagina preenche o atributo qtdRegistroPorPagina
	 */
	public void setQtdRegistroPorPagina(Integer qtdRegistroPorPagina) {
		this.qtdRegistroPorPagina = qtdRegistroPorPagina;
	}

}
