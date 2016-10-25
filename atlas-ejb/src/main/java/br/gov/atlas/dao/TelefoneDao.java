package br.gov.atlas.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import br.gov.atlas.dao.arquitetura.AtlasQuery;
import br.gov.atlas.entidade.Orgao;
import br.gov.atlas.entidade.Telefone;
import br.gov.atlas.exception.AtlasAcessoBancoDadosException;

public class TelefoneDao extends AtlasDao<Telefone> {

	public TelefoneDao(EntityManager em) {
		super(em);
	}

	@SuppressWarnings("unchecked")
	public List<Telefone> recuperarTelefones(Orgao o)
			throws AtlasAcessoBancoDadosException {
		StringBuilder query = new StringBuilder();
		query.append(" SELECT ");
		query.append(" 		id_telefone, ");
		query.append(" 		id_orgao, ");
		query.append(" 		nu_ddd, ");
		query.append(" 		nu_telefone, ");
		query.append(" 		ind_tipo_telefone, ");
		query.append(" 		dsc_telefone ");
		query.append(" FROM ");
		query.append(" 		atlas.telefone ");
		query.append(" WHERE ");
		query.append(" 		id_orgao = :id_orgao  ");

		AtlasQuery atlasQuery = createNativeQuery(query.toString());
		atlasQuery.setParameter("id_orgao", o.getId());

		List<Object[]> resultado = atlasQuery.getResultList();
		List<Telefone> lista = new ArrayList<>();

		Telefone telefone;
		Orgao orgao;
		for (Object[] obj : resultado) {
			orgao = new Orgao();
			orgao.setId((Integer) obj[1]);

			telefone = new Telefone((Integer) obj[0], orgao, (String) obj[2],
					(String) obj[3], obj[4] != null ? String.valueOf(obj[4])
							: null);
			lista.add(telefone);
		}
		return lista;
	}

}
