package br.gov.atlas.enums;

public enum TipoFuncionalidade {
	ORGAO			("001","Órgão"),
	DICIONARIO		("002","Dicionário"),
	PARCEIROS		("003","Parceiros"),
	PROJETOS		("004","Projetos"),
	CARTILHAS		("005","Cartilhas"),
	DOWNLOADS		("006","Downloads"),
	VIDEOS			("007","Vídeos"),
	USUARIOS		("008","Usuários"),
	PERFIL			("009","Perfil"),
	TEMAS			("010","Temas"),
	DADOS_EM_BLOCO	("011","Dados em bloco"),
	FUNCIONALIDADE	("012","Funcionalidade"),
	RAMOS			("013","Funcionalidade"),
	TIPOS_ORGAO		("015","Funcionalidade"),
	IMPORTAR_ORGAO	("016","Importar órgão"),
	SERVICOS		("017","Serviços"),
	CARGOS			("018","Cargos"),
	MENSAGERIAS		("019","Mensagerias"),
	TERMOS			("020","Termos");
	
	private String codigo;
	private String descricao;

	private TipoFuncionalidade(String codigo, String descricao) {
		this.setCodigo(codigo);
		this.setDescricao(descricao);
	}

	public String getCodigo() {
		return this.codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public static TipoFuncionalidade recuperarPeloCodigo(String codigo) {
		TipoFuncionalidade funcionalidadePesquisada = null;
		if ((codigo != null) && !codigo.trim().equals("")) {
			for (TipoFuncionalidade tf : TipoFuncionalidade.values()) {
				if (tf.getCodigo().equals(codigo)) {
					funcionalidadePesquisada = tf;
				}
			}
		}
		return funcionalidadePesquisada;
	}
}