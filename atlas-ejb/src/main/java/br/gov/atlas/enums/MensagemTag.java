package br.gov.atlas.enums;

import java.util.ArrayList;
import java.util.List;

public enum MensagemTag {
	DESINENCIA("DESINENCIA"),
	NOME_ORGAO("NOME_ORGAO"),
	EMAIL_ORGAO("EMAIL_ORGAO"),
	ENDERECO_ORGAO("ENDERECO_ORGAO"),
	TELEFONES_ORGAO("TELEFONES_ORGAO"),
	DESINENCIA_REPRESENTANTE("DESINENCIA_REPRESENTANTE"),
	VOCATIVO_REPRESENTANTE("VOCATIVO_REPRESENTANTE"),
	PRONOME_REPRESENTANTE("PRONOME_REPRESENTANTE"),
	NOME_REPRESENTANTE("NOME_REPRESENTANTE"),
	CARGO_REPRESENTANTE("CARGO_REPRESENTANTE"),
	EMAIL_REPRESENTANTE("EMAIL_REPRESENTANTE"),
	TRATAMENTO_REPRESENTANTE("TRATAMENTO_REPRESENTANTE");
//	NOME_REMETENTE("NOME_REMETENTE"),
//	CARGO_REMETENTE("CARGO_REMETENTE"),
//	EMAIL_REMETENTE("EMAIL_REMETENTE");
	
	private String valor;
	
	private MensagemTag(String tag) {
		this.valor = tag;
	}

	/**
	 * @return the valor
	 */
	public String getValor() {
		return valor;
	}

	/**
	 * @param valor the valor to set
	 */
	public void setValor(String valor) {
		this.valor = valor;
	}
	
	public static List<String> getTags() {
		MensagemTag[] tags = MensagemTag.values();
		List<String> tagsList = new ArrayList<>();
		for (MensagemTag mensagemTag : tags) {
			tagsList.add(mensagemTag.valor);
		}
		return tagsList;
	}
}