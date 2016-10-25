package br.gov.atlas.enums;

import java.util.Arrays;
import java.util.List;

public enum SituacaoTermoGloassario {

	DISPONIVEL("D", "Disponível"), PROPOSTO("P", "Proposto"), VERIFICADO("A",
			"Verificado");

	private final String indicadorSituacao;
	private final String descricao;

	private SituacaoTermoGloassario(String indicadorSituacao, String descricao) {
		this.indicadorSituacao = indicadorSituacao;
		this.descricao = descricao;
	}

	public static SituacaoTermoGloassario create(String indicadorSituacao) {
		if (DISPONIVEL.indicadorSituacao.equals(indicadorSituacao))
			return DISPONIVEL;
		if (PROPOSTO.indicadorSituacao.equals(indicadorSituacao))
			return PROPOSTO;
		if (VERIFICADO.indicadorSituacao.equals(indicadorSituacao))
			return VERIFICADO;
		return null;
	}

	public static List<SituacaoTermoGloassario> getValuesAsList() {
		return Arrays.asList(SituacaoTermoGloassario.values());
	}

	public String indicadorSituacao() {
		return indicadorSituacao;
	}

	public String descricao() {
		return this.descricao;
	}
}
