package br.gov.atlas.enums;


public enum TipoNivelAcesso {
	SEM_ACESSO("S","Sem acesso"),
	CONSULTA("C","Consulta"),
	ADMINISTRADOR("A","Administrdor");
	
	private String valor;
	private String descricao;
	
	private TipoNivelAcesso(String valor, String descricao) {
		this.valor = valor;
		this.descricao = descricao;
	}
	
	public String getValor() {
		return valor;
	}
	public void setValor(String valor) {
		this.valor = valor;
	}
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public static TipoNivelAcesso getTipo(String nivelAcesso) {
		if(nivelAcesso != null){
			if(nivelAcesso.trim().equals(TipoNivelAcesso.ADMINISTRADOR.getValor())){
				return TipoNivelAcesso.ADMINISTRADOR;
			}
			if(nivelAcesso.trim().equals(TipoNivelAcesso.CONSULTA.getValor())){
				return TipoNivelAcesso.CONSULTA;
			}
			if(nivelAcesso.trim().equals(TipoNivelAcesso.SEM_ACESSO.getValor())){
				return TipoNivelAcesso.SEM_ACESSO;
			}
			throw new RuntimeException("Nivel de acesso "+nivelAcesso+" não localizado!");
		}
		return null;
	}
	
}
