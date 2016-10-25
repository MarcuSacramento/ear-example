package br.gov.atlas.enums;

public enum IndicadorMapa {
	CONSUMIDOR("CO");

	private String valor;

	private IndicadorMapa(String valor) {
		this.valor = valor;
	}

	public String getValor() {
		return valor;
	}

	public void setValor(String valor) {
		this.valor = valor;
	}
}
