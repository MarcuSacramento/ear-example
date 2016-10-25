package br.gov.atlas.servico;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import br.gov.atlas.dao.ParceiroDao;
import br.gov.atlas.entidade.Parceiro;
import br.gov.atlas.entity.ListaPaginada;
import br.gov.atlas.exception.AtlasAcessoBancoDadosException;

@Stateless(name="ManterParceiroServico")
@LocalBean
public class ManterParceiroServico extends AtlasServicoImpl{
	
	private ParceiroDao dao;
	
	@Override
	public ParceiroDao getDao() {
		if(dao==null)
			dao=new ParceiroDao(em);
		return dao;
	}
	
	public ListaPaginada<Parceiro> recuperarParceiros(Parceiro filtro) throws AtlasAcessoBancoDadosException {
		return getDao().recuperarParceiros(filtro);
	}

}
