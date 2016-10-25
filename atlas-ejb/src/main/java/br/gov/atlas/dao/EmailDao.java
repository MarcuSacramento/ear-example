package br.gov.atlas.dao;

import javax.persistence.EntityManager;

import br.gov.atlas.entidade.EmailUsuario;

public class EmailDao extends AtlasDao<EmailUsuario>{

	public EmailDao(EntityManager em) {
		super(em);
	}

}
