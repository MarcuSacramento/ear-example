package br.gov.atlas.servico;

import java.util.Collection;
import java.util.List;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import br.gov.atlas.dao.AtlasDao;
import br.gov.atlas.entidade.AtlasEntidade;
import br.gov.atlas.exception.AtlasAcessoBancoDadosException;
import br.gov.atlas.exception.AtlasNegocioException;

@SuppressWarnings("rawtypes")
@TransactionManagement(TransactionManagementType.BEAN)
public abstract class AtlasServicoImpl {
  
	@PersistenceContext(unitName = "AtlasJPA")
	protected EntityManager em;

	@PersistenceContext(unitName = "Tema3JPA")
	protected EntityManager emTema3;

	public abstract AtlasDao getDao();

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.gov.atlas.servico.interfaces.AtlasServicoLocal#salvar(java.lang.Object
	 * )
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public AtlasEntidade salvar(AtlasEntidade entidade) throws AtlasAcessoBancoDadosException, AtlasNegocioException {
		return getDao().salvar(entidade);
	}

	/**
	 * Método responsável por realizar inserção em batch
	 * 
	 * @param listaEntidades
	 * @throws AtlasAcessoBancoDadosException
	 * @throws AtlasNegocioException
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void salvar(List<? extends AtlasEntidade> listaEntidades)
			throws AtlasAcessoBancoDadosException, AtlasNegocioException {
		em.getTransaction().begin();
		int contador = 1;
		for (AtlasEntidade atlasEntidade : listaEntidades) {
			em.persist(atlasEntidade);

			if (contador % 50 == 0) {
				em.flush();
				em.clear();
			}
			contador++;
		}
		em.getTransaction().commit();
		em.close();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.gov.atlas.servico.interfaces.AtlasServicoLocal#remover(java.lang.Object
	 * )
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void remover(AtlasEntidade entidade) throws AtlasAcessoBancoDadosException, AtlasNegocioException {
		getDao().remover(entidade);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.gov.atlas.servico.interfaces.AtlasServicoLocal#pesquisarPorId(java
	 * .lang.Object)
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public AtlasEntidade pesquisarPorId(Integer pk, Class classe) throws AtlasAcessoBancoDadosException {
		return getDao().pesquisarPorId(pk, classe);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.gov.atlas.servico.interfaces.AtlasServicoLocal#buscarTodos()
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public List buscarTodos(Class classe) throws AtlasAcessoBancoDadosException {
		return buscarTodos(classe, null);
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public List buscarTodos(Class classe, String order) throws AtlasAcessoBancoDadosException {
		return getDao().buscarTodos(classe, order);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.gov.atlas.servico.interfaces.AtlasServicoLocal#buscarTodos()
	 */
	public List<AtlasEntidade> buscarTodosAtivos() throws AtlasAcessoBancoDadosException {
		return getDao().buscarTodosAtivos();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.gov.atlas.servico.interfaces.AtlasServicoLocal#desativar(java.lang
	 * .Object)
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void desativar(Integer pk) throws AtlasAcessoBancoDadosException, AtlasNegocioException {
		getDao().desativar(pk);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.gov.atlas.servico.interfaces.AtlasServicoLocal#desativar(java.util
	 * .Collection)
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void desativar(Collection<AtlasEntidade> entidades) throws AtlasAcessoBancoDadosException, AtlasNegocioException {
		getDao().desativar(entidades);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.gov.atlas.servico.interfaces.AtlasServicoLocal#ativar(java.lang.Object
	 * )
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void ativar(Integer pk) throws AtlasAcessoBancoDadosException, AtlasNegocioException {
		getDao().ativar(pk);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.gov.atlas.servico.interfaces.AtlasServicoLocal#ativar(java.util.Collection
	 * )
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void ativar(Collection<AtlasEntidade> entidades) throws AtlasAcessoBancoDadosException, AtlasNegocioException {
		getDao().ativar(entidades);
	}

	public Long contarRegistros(boolean somenteAtivos) throws AtlasAcessoBancoDadosException{
		return getDao().contarRegistros(somenteAtivos);
	}

	public Long contarRegistros() throws AtlasAcessoBancoDadosException{
		return getDao().contarRegistros();
	}

}
