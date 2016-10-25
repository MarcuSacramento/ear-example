package br.gov.atlas.servico;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import br.gov.atlas.dao.RamoDao;
import br.gov.atlas.entidade.Ramo;
import br.gov.atlas.entity.ListaPaginada;
import br.gov.atlas.exception.AtlasAcessoBancoDadosException;

@Stateless(name = "ManterRamoServico")
@LocalBean
public class ManterRamoServico extends AtlasServicoImpl {

	RamoDao dao;

	@Override
	public RamoDao getDao() {
		if(this.dao==null) {
			this.dao = new RamoDao(this.em);
		}
		return this.dao;
	}

	public ListaPaginada<Ramo> recuperarRamos(Ramo ramo)
			throws AtlasAcessoBancoDadosException {
		return getDao().recuperarRamos(ramo);
	}
}