package br.gov.atlas.servico;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import br.gov.atlas.dao.OrgaoSistemaJusticaDao;
import br.gov.atlas.entidade.OrgaoSistemaJustica;
import br.gov.atlas.exception.AtlasAcessoBancoDadosException;

/**
 * Classe de servico para a entidade OrgaoSistemaJustica
 */
@Stateless(name = "OrgaoSistemaJusticaServico")
@LocalBean
public class OrgaoSistemaJusticaServico extends AtlasServicoImpl {

	private OrgaoSistemaJusticaDao orgaoSistemaJusticaDao;

	/*
	 * (non-Javadoc)
	 * @see br.gov.atlas.servico.AtlasServicoImpl#getDao()
	 */
	@Override
	public OrgaoSistemaJusticaDao getDao() {
		if (orgaoSistemaJusticaDao == null) {
			orgaoSistemaJusticaDao = new OrgaoSistemaJusticaDao(em);
		}
		return orgaoSistemaJusticaDao;
	}
	
	/**
	 * Busca todos os OrgaoSistemaJustica filhos.
	 * 
	 * @return lista de OrgaoSistemaJustica
	 * 
	 * @throws AtlasAcessoBancoDadosException
	 */
	public OrgaoSistemaJustica buscarOrgaosRaiz() throws AtlasAcessoBancoDadosException {
		return getDao().buscarOrgaosRaiz();
	}

	/**
	 * Busca todos os OrgaoSistemaJustica filhos.
	 * 
	 * @return lista de OrgaoSistemaJustica
	 * 
	 * @throws AtlasAcessoBancoDadosException
	 */
	public List<OrgaoSistemaJustica> buscarOrgaosFilhos(OrgaoSistemaJustica orgaoSistemaJustica) throws AtlasAcessoBancoDadosException {
		return getDao().buscarOrgaosFilhos(orgaoSistemaJustica);
	}

}
