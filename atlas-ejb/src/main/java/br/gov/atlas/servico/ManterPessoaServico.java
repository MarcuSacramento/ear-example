package br.gov.atlas.servico;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import br.gov.atlas.dao.PessoaDao;
import br.gov.atlas.exception.AtlasAcessoBancoDadosException;

@Stateless(name="ManterPessoaServico")
@LocalBean
public class ManterPessoaServico extends AtlasServicoImpl {

	 PessoaDao dao;
	
//	@Override
//	public AtlasDao getDao() {
//		return new AtlasDao<>(em, Pessoa.class);
//	}

	@Override
	public PessoaDao getDao(){
		if(dao==null){
			dao = new PessoaDao(em);
		}
		return dao;
	}
	
	@Override
	public List buscarTodos(Class classe) throws AtlasAcessoBancoDadosException {
		return super.buscarTodos(classe);
	}

}
