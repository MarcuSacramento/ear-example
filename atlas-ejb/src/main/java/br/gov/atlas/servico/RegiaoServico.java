package br.gov.atlas.servico;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import br.gov.atlas.dao.RegiaoDao;
import br.gov.atlas.dto.OrgaoDTO;
import br.gov.atlas.exception.AtlasAcessoBancoDadosException;

@Stateless(name = "regiaoServico")
@LocalBean
public class RegiaoServico extends AtlasServicoImpl {

	/*
	 * (non-Javadoc)
	 * @see br.gov.atlas.servico.AtlasServicoImpl#getDao()
	 */
	@Override
	public RegiaoDao getDao() {
		return new RegiaoDao(em);
	}
	
	
	public List<OrgaoDTO> recuperarTipoOrgaoPorRegiao() throws AtlasAcessoBancoDadosException {
		return getDao().recuperarTipoOrgaoPorRegiao();
	}

}
