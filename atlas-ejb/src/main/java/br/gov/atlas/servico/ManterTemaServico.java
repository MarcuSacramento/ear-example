package br.gov.atlas.servico;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import br.gov.atlas.dao.TemaDao;
import br.gov.atlas.entidade.Tema;
import br.gov.atlas.entity.ListaPaginada;
import br.gov.atlas.exception.AtlasAcessoBancoDadosException;

@Stateless(name="ManterTemaServico")
@LocalBean
public class ManterTemaServico extends AtlasServicoImpl {

	TemaDao dao;
	
	@Override
	public TemaDao getDao() {
		if(dao==null) {
			dao = new TemaDao(em);
		}
		return dao;
	}

	public ListaPaginada<Tema> recuperarTemas(Tema tema) throws AtlasAcessoBancoDadosException {
		return getDao().recuperarTemas(tema);
	}
}