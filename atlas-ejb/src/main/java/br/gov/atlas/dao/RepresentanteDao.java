package br.gov.atlas.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import br.gov.atlas.dao.arquitetura.AtlasQuery;
import br.gov.atlas.entidade.Mensageria;
import br.gov.atlas.entidade.Orgao;
import br.gov.atlas.entidade.Representante;
import br.gov.atlas.exception.AtlasAcessoBancoDadosException;

public class RepresentanteDao extends AtlasDao<Representante>
{

	public RepresentanteDao(EntityManager em) {
		super(em);
	}

	@SuppressWarnings("unchecked")
	public List<Representante> buscarTodos(Orgao orgao) throws AtlasAcessoBancoDadosException
	{
		StringBuilder query = new StringBuilder();
		query.append("SELECT repr FROM Representante repr WHERE orgao = :orgao ");
		AtlasQuery atlasQuery = createQuery(query.toString());
		atlasQuery.setParameter("orgao", orgao);
		return atlasQuery.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<Representante> buscarDivulgaveis(Orgao orgao) throws AtlasAcessoBancoDadosException
	{
		StringBuilder query = new StringBuilder();
		query.append("SELECT repr FROM Representante repr WHERE orgao = :orgao AND statusDivulgacao = :statusDivulgacao");
		AtlasQuery atlasQuery = createQuery(query.toString());
		atlasQuery.setParameter("orgao", orgao);
		atlasQuery.setParameter("statusDivulgacao", Representante.DIVULGAVEL);
		return atlasQuery.getResultList();
	}
	
	public List<Representante> recuperarRepresentantes(String consulta) throws AtlasAcessoBancoDadosException {
		StringBuilder sql = new StringBuilder();
		sql.append(" select org.nme_orgao, rep.nme_representante, rep.nme_email, rep.id_representante ");
		sql.append(" from 	representante_orgao rep ");
		sql.append(" join 	orgao org on org.id_orgao = rep.id_orgao  ");
		sql.append(" and 	rep.nme_email is not null and trim(rep.nme_email) <> '' ");
		if (consulta != null && !consulta.trim().equals("")) {
			sql.append(" and 	upper(rep.nme_representante) like upper(:consulta) ");
		}
		sql.append(" order by rep.nme_representante ");
		
		AtlasQuery atlasQuery = createNativeQuery(sql.toString());
		if (consulta != null && !consulta.trim().equals("")) {
			atlasQuery.setParameter("consulta", "%"+consulta+"%");
		}
		
		List<Representante> lista = new ArrayList<>();

		for (Object obj : atlasQuery.getResultList()) {
			Object[] item = (Object[]) obj;
			
			Orgao orgaoResposta = new Orgao();
			orgaoResposta.setNome((item[0] != null) ? String.valueOf(item[0]) : null);
			
			Representante representanteConsulta = new Representante();
			representanteConsulta.setOrgao(orgaoResposta);
			representanteConsulta.setNome((item[1] != null) ? String.valueOf(item[1]) : null);
			representanteConsulta.setEmail((item[2] != null) ? String.valueOf(item[2]) : null);
			representanteConsulta.setId((item[3] != null) ? Integer.valueOf(String.valueOf(item[3])) : null);
			lista.add(representanteConsulta);
		}
		return lista;
	}

	@SuppressWarnings("unchecked")
	public List<Orgao> recuperarDestinatarios(Mensageria mensageria) throws AtlasAcessoBancoDadosException {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT msgOrg.orgao FROM MensageriaOrgao msgOrg ");
		sql.append(" join msgOrg.mensageria mensageria ");
		sql.append("WHERE mensageria = :mensageria ");
		AtlasQuery atlasQuery = createQuery(sql.toString());
		atlasQuery.setParameter("mensageria", mensageria);
		return atlasQuery.getResultList();
	}

}