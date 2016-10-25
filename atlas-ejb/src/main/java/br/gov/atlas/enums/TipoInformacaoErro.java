package br.gov.atlas.enums;

public enum TipoInformacaoErro {
	SEM_ERRO("S","Sem erro na informação"),
	COM_ERRO("C","Com erro na informação"),
	AMBOS("A","Ambos");
	
	private String valor;
	private String descricao;
	
	private TipoInformacaoErro(String valor, String descricao){
		this.setValor(valor);
		this.setDescricao(descricao);
	}
	
	public static TipoInformacaoErro create(String valor){
		if( SEM_ERRO.getValor().equals( valor ) )  return SEM_ERRO;
		if( COM_ERRO.getValor().equals( valor ) )  return COM_ERRO;
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
