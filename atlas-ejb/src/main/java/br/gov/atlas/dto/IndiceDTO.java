package br.gov.atlas.dto;

import java.text.DecimalFormat;
import java.util.List;




public class IndiceDTO {
	
	private String unidade;
	private String nome;
	private Double valor;	
	private List<IndiceDTO> indices;
	
	public IndiceDTO(String unidade, List<IndiceDTO> indices){
		this.unidade = unidade;
		this.indices = indices;
	}
	
	public IndiceDTO(String nome, Double valor) {
		this.nome = nome;
		this.valor = valor;
	}
	
	public String getUnidade() {
		return unidade;
	}

	public void setUnidade(String unidade) {
		this.unidade = unidade;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Double getValor() {		
		return valor;
	}

	public void setValor(Double valor) {
		this.valor = valor;
	}

	public String getValorFormatado() {	
		DecimalFormat decimal = new DecimalFormat( "0.###" );
		String formatado = decimal.format(valor);
		return formatado;
    }
	
	public String getNomeComLegenda(){
		switch (getNome()) {
		case "CMH":
			return "CMH	- Coeficiente de mortalidade por homicídio";
		case "CPP":
			return "CPP	- Coeficiente de Pessoas Pobres";
		case "GINI":
			return "GINI - Índice de distribuição de renda";
		case "IDH":
			return "IDH	- Índice de Desenvolvimento Humano";
		case "NPP":
			return "NPP	- Número de pessoas pobres";
		case "OBT VIOL":
			return "OBT VIOL - Óbitos violentos";
		case "POP":
			return "POP	- População";
		case "TXURB (%)":
			return "TXURB (%) - Taxa de Urbanização";	
		case "INAJ":
			return "INAJ - Índice Nacional de Acesso a Justiça";	
		default:
			return null;
		}
	}

	public List<IndiceDTO> getIndices() {
		return indices;
	}

	public void setIndices(List<IndiceDTO> indices) {
		this.indices = indices;
	}

	
	
	
}
