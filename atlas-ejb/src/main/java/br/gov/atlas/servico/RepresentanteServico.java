package br.gov.atlas.servico;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import br.gov.atlas.dao.RepresentanteDao;
import br.gov.atlas.entidade.Mensageria;
import br.gov.atlas.entidade.Orgao;
import br.gov.atlas.entidade.Representante;
import br.gov.atlas.exception.AtlasAcessoBancoDadosException;

@Stateless(name = "RepresentanteServico")
@LocalBean
public class RepresentanteServico extends AtlasServicoImpl {

	private RepresentanteDao dao;

	@Override
	public RepresentanteDao getDao() {
		if (dao == null) {
			dao = new RepresentanteDao(em);
		}
		return dao;
	}

	public List<Representante> buscarTodos(Orgao orgao)
			throws AtlasAcessoBancoDadosException {
		return getDao().buscarTodos(orgao);
	}

	public List<Representante> buscarDivulgaveis(Orgao orgao)
			throws AtlasAcessoBancoDadosException {
		return getDao().buscarDivulgaveis(orgao);
	}

	public List<Representante> recuperarRepresentantes(String consulta) throws AtlasAcessoBancoDadosException {
		return getDao().recuperarRepresentantes(consulta);
	}

	public List<Orgao> recuperarDestinatarios(Mensageria mensageria) throws AtlasAcessoBancoDadosException {
		return getDao().recuperarDestinatarios(mensageria);
	}

}