package br.gov.atlas.enums;

import java.util.Arrays;
import java.util.List;

public enum TipoSituacaoRegistroOrgao {

	PROPOSTO 	("0", "Proposto"   ),
	EM_ANALISE	("1", "Em análise" ),
	DISPONIVEL	("2", "Disponível" ),
	NAO_EH_PORTA_DA_JUSTICA	("3", "Não é uma porta da justiça" );
	
	private final String  indiceSituacao;
	private final String  descricao;
	
	
	
	private TipoSituacaoRegistroOrgao(String indiceSituacaoParam, String descricao)
	{
		this.indiceSituacao = indiceSituacaoParam;
		this.descricao      = descricao;  
	}

	
	public static TipoSituacaoRegistroOrgao create(String indiceSituacaoParam)
	{
		if( PROPOSTO   .indiceSituacao.equals( indiceSituacaoParam ) )  return PROPOSTO;
		if( EM_ANALISE .indiceSituacao.equals( indiceSituacaoParam ) )  return EM_ANALISE;
		if( DISPONIVEL .indiceSituacao.equals( indiceSituacaoParam ) )  return DISPONIVEL;
		if( NAO_EH_PORTA_DA_JUSTICA .indiceSituacao.equals( indiceSituacaoParam ) )  return NAO_EH_PORTA_DA_JUSTICA;
		return null; 
	}
	
	public static List<TipoSituacaoRegistroOrgao> getValuesAsList(){
		return Arrays.asList(TipoSituacaoRegistroOrgao.values());
	}
	
	public Boolean isProposto()
	{
		return PROPOSTO   .indiceSituacao.equals( this.indiceSituacao );
	}
	
	public Boolean isEmAnalise(String indice)
	{
		return EM_ANALISE .indiceSituacao.equals( this.indiceSituacao );
	}
	
	public Boolean isDisponivel(String indice)
	{
		return DISPONIVEL .indiceSituacao.equals( this.indiceSituacao );
	}
	
	public Boolean isPortaDaJusticaNaoDisponivel(String indice)
	{
		return NAO_EH_PORTA_DA_JUSTICA .indiceSituacao.equals( this.indiceSituacao );
	}
	
	public String indiceSituacao()
	{
		return indiceSituacao;
	}
	
	public String descricao()
	{
		return this.descricao;
	}
}
