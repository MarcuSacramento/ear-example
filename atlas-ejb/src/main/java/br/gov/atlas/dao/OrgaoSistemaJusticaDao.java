package br.gov.atlas.dao;

import java.util.List;

import javax.persistence.EntityManager;

import br.gov.atlas.dao.arquitetura.AtlasQuery;
import br.gov.atlas.entidade.OrgaoSistemaJustica;
import br.gov.atlas.exception.AtlasAcessoBancoDadosException;

/**
 * Classe DAO para tratar os acessos as informações do Indicador no banco de dados.
 */
public class OrgaoSistemaJusticaDao extends AtlasDao<OrgaoSistemaJustica> {

	/**
	 * Construtor da classe IndicadorDao
	 * @param em
	 */
	public OrgaoSistemaJusticaDao(EntityManager em) {
		super(em);
	}

	/**
	 * Buscar todos os filhos do orgao passado como parametro
	 * @param orgaoSistemaJustica
	 * @return lista de orgaoSistemaJustica filhos
	 * @throws AtlasAcessoBancoDadosException 
	 */
	@SuppressWarnings("unchecked")
	public List<OrgaoSistemaJustica> buscarOrgaosFilhos(OrgaoSistemaJustica orgaoSistemaJustica) throws AtlasAcessoBancoDadosException {
		String query = "select o from " + OrgaoSistemaJustica.class.getName() + " o  where o.idOrgaoPai = :id ";

		AtlasQuery atlasQuery = createQuery(query, OrgaoSistemaJustica.class);
		atlasQuery.setParameter("id", orgaoSistemaJustica.getId());
		return atlasQuery.getResultList();
	}

	/**
	 * Busca o orgaoSistemaJustica raiz da arvore
	 * @return orgaoSistemaJustica raiz
	 * @throws AtlasAcessoBancoDadosException 
	 */
	public OrgaoSistemaJustica buscarOrgaosRaiz() throws AtlasAcessoBancoDadosException {
		String query = "select o from " + OrgaoSistemaJustica.class.getName() + " o  ";
		query += " where o.idOrgaoPai is null ";

		AtlasQuery atlasQuery = createQuery(query, OrgaoSistemaJustica.class);
		return (OrgaoSistemaJustica) atlasQuery.getSingleResult();
	}

}
