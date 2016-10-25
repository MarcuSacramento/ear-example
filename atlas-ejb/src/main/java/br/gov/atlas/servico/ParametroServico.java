package br.gov.atlas.servico;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import br.gov.atlas.dao.ParametroDao;

/**
 * Classe de servico para a entidade Parametro
 */
@Stateless(name = "ParametroServico")
@LocalBean
public class ParametroServico extends AtlasServicoImpl {

	/*
	 * (non-Javadoc)
	 * @see br.gov.atlas.servico.AtlasServicoImpl#getDao()
	 */
	@Override
	public ParametroDao getDao() {
		return new ParametroDao(em);
	}

}
