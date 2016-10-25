package br.gov.atlas.dao;

import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;

import br.gov.atlas.dao.arquitetura.AtlasEntityManager;
import br.gov.atlas.dao.arquitetura.AtlasQuery;
import br.gov.atlas.entidade.AtlasEntidade;
import br.gov.atlas.entidade.AtlasEntidadeAtivo;
import br.gov.atlas.entity.ConsultaPaginada;
import br.gov.atlas.entity.ListaPaginada;
import br.gov.atlas.exception.AtlasAcessoBancoDadosException;

/**
 * Class AtlasDao.
 * 
 * @param <ENTITY>
 *            - tipo generico
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class AtlasDao<ENTITY extends AtlasEntidade> extends AtlasEntityManager<ENTITY> {

	private Class<ENTITY> persistentClass;

	/**
	 * Cria uma nova instancia de atlas dao impl.
	 * 
	 * @param em
	 *            - em
	 */

	public AtlasDao(EntityManager em) {
		super(em);
	}

	public AtlasDao(EntityManager em, final Class<ENTITY> entidade) {
		super(em);
		this.persistentClass = entidade;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.gov.atlas.dao.interfaces.AtlasDao#salvar(java.lang.Object)
	 */
	public ENTITY salvar(ENTITY entidade) throws AtlasAcessoBancoDadosException {
		if (entidade.getId() == null) {
			persist(entidade);
			return entidade;
		}
		return merge(entidade);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.gov.atlas.dao.interfaces.AtlasDao#remover(java.lang.Object)
	 */
	public void remover(ENTITY entidade) throws AtlasAcessoBancoDadosException {
		entidade = find((Class<ENTITY>) entidade.getClass(), entidade.getId());
		if (entidade instanceof AtlasEntidadeAtivo) {
			throw new AtlasAcessoBancoDadosException("REMOVER_ATIVO");
		}
		remove(entidade);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.gov.atlas.dao.interfaces.AtlasDao#desativar(java.lang.Object)
	 */
	public void desativar(Integer pk) throws AtlasAcessoBancoDadosException {
		ENTITY entidade = find(getPersistentClass(), pk);
		if (!(entidade instanceof AtlasEntidadeAtivo)) {
			throw new AtlasAcessoBancoDadosException("INATIVO", getPersistentClass().toString());
		}
		((AtlasEntidadeAtivo) entidade).setAtivo(false);
		salvar(entidade);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.gov.atlas.dao.interfaces.AtlasDao#desativar(java.util.Collection)
	 */
	public void desativar(Collection<ENTITY> entidades) throws AtlasAcessoBancoDadosException {
		for (ENTITY entidade : entidades) {
			desativar(entidade.getId());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.gov.atlas.dao.interfaces.AtlasDao#ativar(java.lang.Object)
	 */
	public void ativar(Integer pk) throws AtlasAcessoBancoDadosException {
		ENTITY entidade = find(getPersistentClass(), pk);
		if (!(entidade instanceof AtlasEntidadeAtivo)) {
			throw new AtlasAcessoBancoDadosException("INATIVO", getPersistentClass().toString());
		}
		((AtlasEntidadeAtivo) entidade).setAtivo(true);
		salvar(entidade);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.gov.atlas.dao.interfaces.AtlasDao#ativar(java.util.Collection)
	 */
	public void ativar(Collection<ENTITY> entidades) throws AtlasAcessoBancoDadosException {
		for (ENTITY entidade : entidades) {
			ativar(entidade.getId());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.gov.atlas.dao.interfaces.AtlasDao#pesquisarPorId(java.lang.Object)
	 */
	public ENTITY pesquisarPorId(Integer id, Class classe) throws AtlasAcessoBancoDadosException {
		return (ENTITY) find(classe, id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.gov.atlas.dao.interfaces.AtlasDao#buscarTodos()
	 */
	public List buscarTodos(Class classe) throws AtlasAcessoBancoDadosException {
		return buscarTodos(classe, null);
	}

	public List buscarTodos(Class classe, String order) throws AtlasAcessoBancoDadosException {
		String query = "select a from " + classe.getName() + " a ";
		if (order != null && !order.trim().isEmpty()) {
			query += ("order by a." + order);
		}
		AtlasQuery atlasQuery = createQuery(query);
		return atlasQuery.getResultList();
	}

	public Long contarRegistros()
			throws AtlasAcessoBancoDadosException {
		return contarRegistros(getPersistentClass(), false);
	}

	public Long contarRegistros(boolean somenteAtivos)
			throws AtlasAcessoBancoDadosException {
		return contarRegistros(getPersistentClass(), somenteAtivos);
	}

	public Long contarRegistros(Class classe, boolean somenteAtivos)
			throws AtlasAcessoBancoDadosException {
		StringBuilder query = new StringBuilder();
		query.append("select count(a.id) from ").append(classe.getName()).append(" a ");
		if (somenteAtivos) {
			query.append(" where a.ativo = true ");
		}
		return (Long) createQuery(query.toString()).getSingleResult();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.gov.atlas.dao.interfaces.AtlasDao#buscarTodos()
	 */
	public List<ENTITY> buscarTodosAtivos() throws AtlasAcessoBancoDadosException {
		String order = "";
		return createQuery("select a from " + getPersistentClass().getName() + " a where a.ativo = true" + order).getResultList();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.gov.atlas.dao.interfaces.AtlasDao#pesquisaPagina(br.gov.atlas.paginacao
	 * .entity.ConsultaPaginada)
	 */
	public ListaPaginada efetuarPesquisaPaginada(ConsultaPaginada consulta) throws AtlasAcessoBancoDadosException {
		ListaPaginada listaPaginada = new ListaPaginada();
		int inicioGroupBy = -1;
		int inicioOrderBy = -1;

		AtlasQuery qry = null;
		AtlasQuery qryCount = null;
		String queryString = consulta.getQueryString();
		String hqlString = consulta.getHqlString();
		String queryCount = "";

		if (queryString != null && !queryString.isEmpty()) {
			int inicioFrom = queryString.toUpperCase().indexOf("FROM");
			inicioGroupBy = queryString.toUpperCase().indexOf("GROUP BY");
			inicioOrderBy = queryString.toUpperCase().indexOf("ORDER BY");
			inicioOrderBy = (inicioOrderBy == -1) ? queryString.length() : inicioOrderBy;

			queryCount = " SELECT count(*) " + queryString.substring(inicioFrom, inicioOrderBy);
			queryString += "  LIMIT " + listaPaginada.getQtdRegistroPorPagina() + " OFFSET(" + consulta.getPrimeiroRegistro() + ")  ";

			qryCount = createNativeQuery(queryCount);
			qry = createNativeQuery(queryString);
		} else if (hqlString != null && !hqlString.isEmpty()) {
			int inicioFrom = hqlString.toUpperCase().indexOf("FROM");
			inicioGroupBy = hqlString.toUpperCase().indexOf("GROUP BY");
			inicioOrderBy = hqlString.toUpperCase().indexOf("ORDER BY");
			inicioOrderBy = (inicioOrderBy == -1) ? hqlString.length() : inicioOrderBy;

			queryCount = " SELECT count(*) " + hqlString.substring(inicioFrom, inicioOrderBy);

			qryCount = createQuery(queryCount);

			qry = createQuery(hqlString);
			qry.setMaxResults(listaPaginada.getQtdRegistroPorPagina());
			if (consulta.getPrimeiroRegistro() != null) {
				qry.setFirstResult(consulta.getPrimeiroRegistro());
			}

		} else {
			throw new AtlasAcessoBancoDadosException("QUERY_NAO_INFORMADA");
		}

		for (String key : consulta.getParametros().keySet()) {
			qry.setParameter(key, consulta.getParametros().get(key));
			qryCount.setParameter(key, consulta.getParametros().get(key));
		}

		listaPaginada.setQtdRegistros(getQuantidadeRegistroConsulta(inicioGroupBy, qryCount));
		listaPaginada.setResultadoPesquisa(qry.getResultList());
		listaPaginada.setPrimeiroRegistro(consulta.getPrimeiroRegistro());

		return listaPaginada;
	}

	/**
	 * Recupera o quantidade registro consulta.
	 * 
	 * @param inicioGroupBy
	 *            - inicio group by
	 * @param qryCount
	 *            - qry count
	 * @return quantidade registro consulta
	 */
	private int getQuantidadeRegistroConsulta(int inicioGroupBy, AtlasQuery qryCount) throws AtlasAcessoBancoDadosException {
		int qtdRegistros = 0;
		if (inicioGroupBy > 0) {
			qtdRegistros = qryCount.getResultList().size();
		} else {
			qtdRegistros = Integer.parseInt(qryCount.getResultList().toString().replace("[", "").replace("]", ""));
		}
		return qtdRegistros;
	}

	/**
	 * @return o atributo persistentClass
	 */
	public Class<ENTITY> getPersistentClass() throws AtlasAcessoBancoDadosException {
		if (persistentClass == null) {
			ParameterizedType paramType = (ParameterizedType) getClass().getGenericSuperclass();
			persistentClass = (Class<ENTITY>) paramType.getActualTypeArguments()[0];
		}
		return persistentClass;
	}

}
