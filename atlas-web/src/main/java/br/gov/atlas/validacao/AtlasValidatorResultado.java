package br.gov.atlas.validacao;

import java.util.ArrayList;
import java.util.List;

/**
 * Class AtlasValidatorResultado - Classe responsável por conter o resultado da validação.
 */
public class AtlasValidatorResultado {

	private final List<AtlasMensagem> motivos = new ArrayList<AtlasMensagem>();

	/**
	 * Add motivo.
	 * 
	 * @param motivo - motivo
	 */
	public void addMotivo(AtlasMensagem motivo) {
		motivos.add(motivo);
	}

	/**
	 * Remove motivo.
	 * 
	 * @param motivo - motivo
	 */
	public void removeMotivo(AtlasMensagem motivo) {
		motivos.remove(motivo);
	}

	/**
	 * Verifica se é valid.
	 * 
	 * @return <code>true</code> se verdadeiro, e <code>false</code> caso contrário.
	 */
	public boolean isValid() {
		return motivos.isEmpty();
	}

	/**
	 * Recupera os motivos.
	 * 
	 * @return o atributo motivos
	 */
	public List<AtlasMensagem> getMotivos() {
		return motivos;
	}

}
