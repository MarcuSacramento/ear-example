package br.gov.atlas.enums;


public enum AmbitoAtuacaoOrgao {
	M("M", "Municipal"), E("E", "Estadual"), F("F", "Federal");

	private String indicador;
	private String descricao;

	private AmbitoAtuacaoOrgao(String indicador, String descricao) {
		this.indicador = indicador;
		this.descricao = descricao;
	}

	public String getIndicador() {
		return indicador;
	}

	public void setIndicador(String indicador) {
		this.indicador = indicador;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
}
