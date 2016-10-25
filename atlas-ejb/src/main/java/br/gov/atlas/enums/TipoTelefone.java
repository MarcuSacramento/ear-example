package br.gov.atlas.enums;

public enum TipoTelefone {
	CELULAR("C"), TELEFONE_FIXO("T"), FAX("F");

	private String valor;

	TipoTelefone(String valor) {
		this.setValor(valor);
	}

	public String getValor() {
		return valor;
	}

	public void setValor(String valor) {
		this.valor = valor;
	}

}
