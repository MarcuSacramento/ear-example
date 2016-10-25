package br.gov.atlas.dao.arquitetura;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.TransactionRequiredException;
import javax.persistence.criteria.CriteriaQuery;

import br.gov.atlas.exception.AtlasAcessoBancoDadosException;

/**
 * Class AtlasEntityManager.
 * 
 * @param <ENTITY> - tipo genérico
 */
public class AtlasEntityManager<ENTITY> {

	private EntityManager em;

	/**
	 * Construtor da classe.
	 * 
	 * @param em - em
	 */
	public AtlasEntityManager(EntityManager em) {
		super();
		this.em = em;
	}
	
	/**
	 * Construtor da classe.
	 */
	protected AtlasEntityManager() {
		super();
	}

	/**
	 * Efetua o método Merge no EntityManage.
	 * 
	 * @param entidade - entidade
	 * @return entity
	 * @throws AtlasAcessoBancoDadosException - atlas acesso banco dados exception
	 */
	protected ENTITY merge(ENTITY entidade) throws AtlasAcessoBancoDadosException {
		try {
			return em.merge(entidade);
		} catch (IllegalArgumentException | TransactionRequiredException e) {
			e.printStackTrace();
			throw new AtlasAcessoBancoDadosException("MSG19");
		} catch (Exception e) {
			e.printStackTrace();
			throw new AtlasAcessoBancoDadosException("MSG19");
		}
	}

	/**
	 * Efetua o método Persist no EntityManage.
	 * 
	 * @param entidade - entidade
	 * @throws AtlasAcessoBancoDadosException - atlas acesso banco dados exception
	 */
	protected void persist(ENTITY entidade) throws AtlasAcessoBancoDadosException {
		try {
			em.persist(entidade);
		} catch (EntityExistsException | IllegalArgumentException | TransactionRequiredException e) {
			e.printStackTrace();
			throw new AtlasAcessoBancoDadosException("MSG17");
		} catch (Exception e) {
			e.printStackTrace();
			throw new AtlasAcessoBancoDadosException("MSG17");
		}
	}

	/**
	 * Efetua o método Remove no EntityManage.
	 * 
	 * @param entidade - entidade
	 * @throws AtlasAcessoBancoDadosException - atlas acesso banco dados exception
	 */
	protected void remove(ENTITY entidade) throws AtlasAcessoBancoDadosException {
		try {
			em.remove(entidade);
		} catch (IllegalArgumentException | TransactionRequiredException e) {
			e.printStackTrace();
			throw new AtlasAcessoBancoDadosException("MSG18");
		} catch (Exception e) {
			e.printStackTrace();
			throw new AtlasAcessoBancoDadosException("MSG18");
		}
	}

	/**
	 * Efetua o método Find no EntityManage.
	 * 
	 * @param clazz - clazz
	 * @param pk - pk
	 * @return entity
	 * @throws AtlasAcessoBancoDadosException - atlas acesso banco dados exception
	 */
	protected ENTITY find(Class<ENTITY> clazz, Integer pk) throws AtlasAcessoBancoDadosException { 
		try {
			return em.find(clazz, pk);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			throw new AtlasAcessoBancoDadosException("MSG19");
		} catch (Exception e) {
			e.printStackTrace();
			throw new AtlasAcessoBancoDadosException("MSG19");
		}

	}

	/**
	 * Create native query.
	 * 
	 * @param queryString - query string
	 * @param resultSetMapping - result set mapping
	 * @return atlas query
	 * @throws AtlasAcessoBancoDadosException - atlas acesso banco dados exception
	 */
	protected AtlasQuery createNativeQuery(String queryString, String resultSetMapping) throws AtlasAcessoBancoDadosException {
		try {
			return new AtlasQuery(em.createNativeQuery(queryString, resultSetMapping));
		} catch (PersistenceException e) {
			e.printStackTrace();
			throw new AtlasAcessoBancoDadosException("ERRO_QUERY");
		} catch (Exception e) {
			e.printStackTrace();
			throw new AtlasAcessoBancoDadosException("ERRO_QUERY");
		}
	}

	/**
	 * Create native query.
	 * 
	 * @param queryString - query string
	 * @param resultClass - result class
	 * @return atlas query
	 * @throws AtlasAcessoBancoDadosException - atlas acesso banco dados exception
	 */
	protected AtlasQuery createNativeQuery(String queryString, Class<ENTITY> resultClass) throws AtlasAcessoBancoDadosException {
		try {
			return new AtlasQuery(em.createNativeQuery(queryString, resultClass));
		} catch (PersistenceException e) {
			e.printStackTrace();
			throw new AtlasAcessoBancoDadosException("ERRO_QUERY");
		} catch (Exception e) {
			e.printStackTrace();
			throw new AtlasAcessoBancoDadosException("ERRO_QUERY");
		}
	}

	/**
	 * Create native query.
	 * 
	 * @param queryString - query string
	 * @return atlas query
	 * @throws AtlasAcessoBancoDadosException - atlas acesso banco dados exception
	 */
	protected AtlasQuery createNativeQuery(String queryString) throws AtlasAcessoBancoDadosException {
		try {
			return new AtlasQuery(em.createNativeQuery(queryString));
		} catch (PersistenceException e) {
			e.printStackTrace();
			throw new AtlasAcessoBancoDadosException("ERRO_QUERY");
		} catch (Exception e) {
			e.printStackTrace();
			throw new AtlasAcessoBancoDadosException("ERRO_QUERY");
		}
	}

	/**
	 * Create named query.
	 * 
	 * @param queryString - query string
	 * @return atlas query
	 * @throws AtlasAcessoBancoDadosException - atlas acesso banco dados exception
	 */
	protected AtlasQuery createNamedQuery(String queryString) throws AtlasAcessoBancoDadosException {
		try {
			return new AtlasQuery(em.createNamedQuery(queryString));
		} catch (PersistenceException e) {
			e.printStackTrace();
			throw new AtlasAcessoBancoDadosException("ERRO_QUERY");
		} catch (Exception e) {
			e.printStackTrace();
			throw new AtlasAcessoBancoDadosException("ERRO_QUERY");
		}
	}

	/**
	 * Create named query.
	 * 
	 * @param queryString - query string
	 * @param resultClass - result class
	 * @return atlas query
	 * @throws AtlasAcessoBancoDadosException - atlas acesso banco dados exception
	 */
	protected AtlasQuery createNamedQuery(String queryString, Class<ENTITY> resultClass) throws AtlasAcessoBancoDadosException {
		try {
			return new AtlasQuery(em.createNamedQuery(queryString, resultClass));
		} catch (PersistenceException e) {
			e.printStackTrace();
			throw new AtlasAcessoBancoDadosException("ERRO_QUERY");
		} catch (Exception e) {
			e.printStackTrace();
			throw new AtlasAcessoBancoDadosException("ERRO_QUERY");
		}
	}

	/**
	 * Create query.
	 * 
	 * @param queryString - query string
	 * @return atlas query
	 * @throws AtlasAcessoBancoDadosException - atlas acesso banco dados exception
	 */
	protected AtlasQuery createQuery(String queryString) throws AtlasAcessoBancoDadosException {
		try {
			return new AtlasQuery(em.createQuery(queryString));
		} catch (PersistenceException e) {
			e.printStackTrace();
			throw new AtlasAcessoBancoDadosException("ERRO_QUERY");
		} catch (Exception e) {
			e.printStackTrace();
			throw new AtlasAcessoBancoDadosException("ERRO_QUERY");
		}
	}

	/**
	 * Create query.
	 * 
	 * @param criteriaQuery - criteria query
	 * @return atlas query
	 * @throws AtlasAcessoBancoDadosException - atlas acesso banco dados exception
	 */
	protected AtlasQuery createQuery(CriteriaQuery<ENTITY> criteriaQuery) throws AtlasAcessoBancoDadosException {
		try {
			return new AtlasQuery(em.createQuery(criteriaQuery));
		} catch (PersistenceException e) {
			e.printStackTrace();
			throw new AtlasAcessoBancoDadosException("ERRO_QUERY");
		} catch (Exception e) {
			e.printStackTrace();
			throw new AtlasAcessoBancoDadosException("ERRO_QUERY");
		}
	}

	/**
	 * Create query.
	 * 
	 * @param queryString - query string
	 * @param resultClass - result class
	 * @return atlas query
	 * @throws AtlasAcessoBancoDadosException - atlas acesso banco dados exception
	 */
	protected AtlasQuery createQuery(String queryString, Class<ENTITY> resultClass) throws AtlasAcessoBancoDadosException {
		try {
			return new AtlasQuery(em.createQuery(queryString, resultClass));
		} catch (PersistenceException e) {
			e.printStackTrace();
			throw new AtlasAcessoBancoDadosException("ERRO_QUERY");
		} catch (Exception e) {
			e.printStackTrace();
			throw new AtlasAcessoBancoDadosException("ERRO_QUERY");
		}
	}

}
