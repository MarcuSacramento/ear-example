package br.gov.atlas.servico;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import br.gov.atlas.dao.TelefoneDao;
import br.gov.atlas.entidade.Orgao;
import br.gov.atlas.entidade.Telefone;
import br.gov.atlas.exception.AtlasAcessoBancoDadosException;


@Stateless(name = "TelefoneServico")
@LocalBean
public class TelefoneServico extends AtlasServicoImpl {

	private TelefoneDao dao;

	@Override
	public TelefoneDao getDao() {
		if (dao == null) {
			dao = new TelefoneDao(em);
		}
		return dao;
	}

	public List<Telefone> recuperarTelefones(Orgao orgao)
			throws AtlasAcessoBancoDadosException {
		return getDao().recuperarTelefones(orgao);
	}
}
