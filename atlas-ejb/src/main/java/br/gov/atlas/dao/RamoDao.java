package br.gov.atlas.dao;

import javax.persistence.EntityManager;

import br.gov.atlas.entidade.Ramo;
import br.gov.atlas.entity.ConsultaPaginada;
import br.gov.atlas.entity.ListaPaginada;
import br.gov.atlas.exception.AtlasAcessoBancoDadosException;

public class RamoDao extends AtlasDao<Ramo> {

	public RamoDao(EntityManager em) {
		super(em);
	}

	@SuppressWarnings("unchecked")
	public ListaPaginada<Ramo> recuperarRamos(Ramo ramo)
			throws AtlasAcessoBancoDadosException {
		ConsultaPaginada consultaPaginada = new ConsultaPaginada(
				ramo.getPrimeiroRegistro());
		StringBuilder query = new StringBuilder();
		query.append("select r from Ramo as r ");

		if ((ramo != null) && (ramo.getNome() != null)) {
			query.append("where upper(r.nome) like :nome ");
			consultaPaginada.getParametros().put("nome",
					ramo.getNome().toUpperCase());
		}
		query.append("order by r.nome ");
		consultaPaginada.setHqlString(query.toString());

		ListaPaginada<Ramo> lista = efetuarPesquisaPaginada(consultaPaginada);
		for (Object object : lista.getResultadoPesquisa()) {
			lista.getResultadoRetorno().add((Ramo) object);
		}

		return lista;
	}
}