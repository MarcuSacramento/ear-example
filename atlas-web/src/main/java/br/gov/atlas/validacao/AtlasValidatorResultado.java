package br.gov.atlas.validacao;

import java.util.ArrayList;
import java.util.List;

/**
 * Class AtlasValidatorResultado - Classe respons�vel por conter o resultado da valida��o.
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
	 * Verifica se � valid.
	 * 
	 * @return <code>true</code> se verdadeiro, e <code>false</code> caso contr�rio.
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
