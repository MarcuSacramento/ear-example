package br.gov.atlas.servico;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import br.gov.atlas.dao.ServicoDao;
import br.gov.atlas.entidade.Servico;
import br.gov.atlas.entity.ListaPaginada;
import br.gov.atlas.exception.AtlasAcessoBancoDadosException;

@Stateless(name = "ManterServicoServico")
@LocalBean
public class ManterServicoServico extends AtlasServicoImpl {

	private ServicoDao dao;

	@Override
	public ServicoDao getDao() {
		if (dao == null) {
			dao = new ServicoDao(em);
		}
		return dao;
	}

	public ListaPaginada<Servico> recuperarServicos(Servico servico)
			throws AtlasAcessoBancoDadosException {
		return getDao().recuperarServicos(servico);
	}
}