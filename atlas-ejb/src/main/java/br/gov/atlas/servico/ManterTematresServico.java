package br.gov.atlas.servico;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import br.gov.atlas.dao.TematresDao;
import br.gov.atlas.entidade.Servico;
import br.gov.atlas.exception.AtlasAcessoBancoDadosException;

@Stateless(name="ManterTematresServico")
@LocalBean
public class ManterTematresServico extends AtlasServicoImpl {

	TematresDao dao;
	
	@Override
	public TematresDao getDao() {
		if(dao==null) {
			dao = new TematresDao(emTema3);
		}
		return dao;
	}

	public List<Servico> recuperarServicos() throws AtlasAcessoBancoDadosException {
		return getDao().recuperarServicos();
	}
}