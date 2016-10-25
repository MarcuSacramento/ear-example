package br.gov.atlas.dto;

import java.math.BigDecimal;

public class GraficoDTO {
	
	private Integer ano;
	private String uf;
	private BigDecimal resultado;
	private BigDecimal resultadoCalculado;
	
	/**
	 * @return the ano
	 */
	public Integer getAno() {
		return ano;
	}
	
	/**
	 * @param ano the ano to set
	 */
	public void setAno(Integer ano) {
		this.ano = ano;
	}
	
	/**
	 * @return the uf
	 */
	public String getUf() {
		return uf;
	}
	
	/**
	 * @param uf the uf to set
	 */
	public void setUf(String uf) {
		this.uf = uf;
	}
	
	/**
	 * @return the resultado
	 */
	public BigDecimal getResultado() {
		return resultado;
	}

	/**
	 * @param resultado the resultado to set
	 */
	public void setResultado(BigDecimal resultado) {
		this.resultado = resultado;
	}

	/**
	 * Gets the resultado calculado.
	 *
	 * @param multiplicador 
	 * @param divisor 
	 * @return o resultado calculado
	 */
	public BigDecimal getResultadoCalculado(Double multiplicador, Double divisor) {
		if (resultadoCalculado == null) {
			double resultado = (getResultado().doubleValue() * multiplicador) / divisor;
			resultadoCalculado = BigDecimal.valueOf(resultado);
		}
		
		return resultadoCalculado;
	}

	/**
	 * @return the resultadoCalculado
	 */
	public BigDecimal getResultadoCalculado() {
		return resultadoCalculado;
	}

	/**
	 * @param resultadoCalculado the resultadoCalculado to set
	 */
	public void setResultadoCalculado(BigDecimal resultadoCalculado) {
		this.resultadoCalculado = resultadoCalculado;
	}

}
