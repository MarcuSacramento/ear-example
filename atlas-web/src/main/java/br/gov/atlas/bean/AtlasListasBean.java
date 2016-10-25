package br.gov.atlas.bean;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

import br.gov.atlas.enums.TipoDado;
import br.gov.atlas.enums.TipoResposta;

/**
 * Bean com métodos úteis para apresentação de perguntas.
 */
@ApplicationScoped
@ManagedBean(name = "atlasListasBean")
public class AtlasListasBean {

	private List<TipoResposta> tiposResposta;
	private List<TipoDado> tiposDados;

	/**
	 * Cria uma nova instância de pergunta dominio bean.
	 */
	public AtlasListasBean() {}

	/**
	 * Recupera o tipos resposta.
	 * 
	 * @return o atributo tiposResposta
	 */
	public List<TipoResposta> getTiposResposta() {
		if (tiposResposta == null) {
			tiposResposta = Arrays.asList(TipoResposta.values());
			Collections.sort(tiposResposta);
		}
		return tiposResposta;
	}

	/**
	 * Recupera o tipos dados.
	 * 
	 * @return o atributo tiposDados
	 */
	public List<TipoDado> getTiposDado() {
		if (tiposDados == null) {
			tiposDados = Arrays.asList(TipoDado.values());
			Collections.sort(tiposDados);
		}
		return tiposDados;
	}
}
