package br.gov.atlas.util;

public enum TipoArquivo {
	DOC("doc", "Word"), GIF("gif", "Imagem"), JPEG("jpeg", "Imagem"), JPG(
			"jpg", "Imagem"), PDF("pdf", "PDF"), PNG("png", "Imagem"), PPS(
					"pps", "Apresentação"), XLS("xls", "Excel");

	private String tipo;
	private String formato;

	TipoArquivo(String tipo, String formato) {
		this.setTipo(tipo);
		this.setFormato(formato);
	}

	public String getTipo() {
		return tipo;
	}

	private void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getFormato() {
		return formato;
	}

	public void setFormato(String formato) {
		this.formato = formato;
	}

}
