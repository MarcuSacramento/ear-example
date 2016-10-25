package br.gov.atlas.dto;

import java.util.HashMap;
import java.util.Map;

import br.gov.atlas.entidade.Funcionalidade;

public class FuncionalidadeDTO {

	private Map<Funcionalidade, String> mapFuncionalidade = new HashMap<Funcionalidade, String>();

	public Map<Funcionalidade, String> getMapFuncionalidade() {
		return mapFuncionalidade;
	}

	public void setMapFuncionalidade(Map<Funcionalidade, String> mapFuncionalidade) {
		this.mapFuncionalidade = mapFuncionalidade;
	}

}
