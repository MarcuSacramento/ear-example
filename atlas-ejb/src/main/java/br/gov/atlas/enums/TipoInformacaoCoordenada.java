package br.gov.atlas.enums;

public enum TipoInformacaoCoordenada {
	
	SEM_INFORMACAO("S","Sem informação de coordenadas"),
	COM_INFORMACAO("C","Com informação de coordenadas"),
	AMBOS("A","Ambos");
	
	private String valor;
	private String descricao;
	
	private TipoInformacaoCoordenada(String valor, String descricao){
		this.setValor(valor);
		this.setDescricao(descricao);
	}
	
	public static TipoInformacaoCoordenada create(String valor){
		if( SEM_INFORMACAO.getValor().equals( valor ) )  return SEM_INFORMACAO;
		if( COM_INFORMACAO.getValor().equals( valor ) )  return COM_INFORMACAO;
		if( AMBOS.getValor().equals( valor ) )  return AMBOS;
		return null; 
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
	
	
}
