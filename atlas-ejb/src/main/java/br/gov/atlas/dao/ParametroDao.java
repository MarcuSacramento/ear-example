package br.gov.atlas.dao;

import javax.persistence.EntityManager;

import br.gov.atlas.entidade.Parametro;

/**
 * Classe DAO para tratar os acessos as informações do Parametro no banco de dados.
 */
public class ParametroDao extends AtlasDao<Parametro> {

	/**
	 * Construtor da classe ParametroDao
	 * @param em
	 */
	public ParametroDao(EntityManager em) {
		super(em);
	}

}
