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
			return "CMH	- Coeficiente de mortalidade por homic�dio";
		case "CPP":
			return "CPP	- Coeficiente de Pessoas Pobres";
		case "GINI":
			return "GINI - �ndice de distribui��o de renda";
		case "IDH":
			return "IDH	- �ndice de Desenvolvimento Humano";
		case "NPP":
			return "NPP	- N�mero de pessoas pobres";
		case "OBT VIOL":
			return "OBT VIOL - �bitos violentos";
		case "POP":
			return "POP	- Popula��o";
		case "TXURB (%)":
			return "TXURB (%) - Taxa de Urbaniza��o";	
		case "INAJ":
			return "INAJ - �ndice Nacional de Acesso a Justi�a";	
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
