package br.gov.atlas.servico;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import br.gov.atlas.dao.CargoDao;
import br.gov.atlas.entidade.Cargo;
import br.gov.atlas.entity.ListaPaginada;
import br.gov.atlas.exception.AtlasAcessoBancoDadosException;

@Stateless(name="ManterCargoServico")
@LocalBean
public class ManterCargoServico extends AtlasServicoImpl {

	CargoDao dao;
	
	@Override
	public CargoDao getDao() {
		if(dao==null) {
			dao = new CargoDao(em);
		}
		return dao;
	}

	public ListaPaginada<Cargo> recuperarCargos(Cargo cargo) throws AtlasAcessoBancoDadosException {
		return getDao().recuperarCargos(cargo);
	}

	public Cargo recuperPorNome(String nomeCargoRepresentante) throws AtlasAcessoBancoDadosException {
		return getDao().recuperPorNome(nomeCargoRepresentante);
	}
	
	public Boolean isCargoAssociadoAAlgumRepresentante(Cargo cargo) throws AtlasAcessoBancoDadosException {
		return getDao().isCargoAssociadoAAlgumRepresentante(cargo);
	}
	
}