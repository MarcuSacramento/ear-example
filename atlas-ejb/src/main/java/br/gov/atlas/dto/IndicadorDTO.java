package br.gov.atlas.dto;

import java.text.DecimalFormat;
import java.util.Map;

import br.gov.atlas.entidade.UF;




public class IndicadorDTO {
	
	private UF uf;
	private Map<String,Double> indices;
	
	public UF getUf() {
		return uf;
	}
	public void setUf(UF uf) {
		this.uf = uf;
	}
	public Map<String, Double> getIndices() {
		return indices;
	}
	public void setIndices(Map<String, Double> indices) {
		this.indices = indices;
	}
	
	public String getIndices(String key) {	
		DecimalFormat decimal = new DecimalFormat( "0.###" );
		String formatado = decimal.format(indices.get(key));
		return formatado;
    }

	
	
	
}
