package br.gov.atlas.servico;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import br.gov.atlas.dao.ProjetoDao;
import br.gov.atlas.entidade.Projeto;
import br.gov.atlas.entity.ListaPaginada;
import br.gov.atlas.exception.AtlasAcessoBancoDadosException;

@Stateless(name="ManterProjetoServico")
@LocalBean
public class ManterProjetoServico extends AtlasServicoImpl {

	private ProjetoDao dao;
	
	@Override
	public ProjetoDao getDao() {
		if(dao==null)
			dao=new ProjetoDao(em);
		return dao;
	}
	
	public ListaPaginada<Projeto> recuperarProjetos(Projeto filtro) throws AtlasAcessoBancoDadosException {
		return getDao().recuperarProjetos(filtro);
	}

}
