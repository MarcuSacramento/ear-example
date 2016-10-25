package br.gov.atlas.servico;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import br.gov.atlas.dao.EmailDao;

@Stateless(name = "EmailServico")
@LocalBean
public class EmailServico extends AtlasServicoImpl{

	private EmailDao dao;

	@Override
	public EmailDao getDao() {
		if (dao == null) {
			dao = new EmailDao(em);
		}
		return dao;
	}
}
