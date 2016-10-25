package br.gov.atlas.enums;


public enum TipoRamo {
	JUDICIAL("1", "Judicial"),
	ESSENCIAL("2", "Essencial"),
	EXTRAJUDICIAL("3", "Extrajudicial");

	private String codigo;
	private String descricao;

	private TipoRamo(String codigo, String descricao) {
		this.setCodigo(codigo);
		this.setDescricao(descricao);
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public static TipoRamo recuperarPeloCodigo(String codigo) {
		TipoRamo tipoRamoPesquisado = null;
		if ((codigo != null) && !codigo.trim().equals("")) {
			for (TipoRamo tr : TipoRamo.values()) {
				if (tr.getCodigo().equals(codigo)) {
					tipoRamoPesquisado = tr;
				}
			}
		}
		return tipoRamoPesquisado;
	}

}
