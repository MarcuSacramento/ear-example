package br.gov.atlas.servico;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import br.gov.atlas.dao.TipoOrgaoDao;
import br.gov.atlas.entidade.TipoOrgao;
import br.gov.atlas.enums.IndicadorMapa;
import br.gov.atlas.exception.AtlasAcessoBancoDadosException;

@Stateless(name = "TipoOrgaoServico")
@LocalBean
public class TipoOrgaoServico extends AtlasServicoImpl {

	private TipoOrgaoDao dao;

	@Override
	public TipoOrgaoDao getDao() {
		if (dao == null) {
			dao = new TipoOrgaoDao(em);
		}
		return dao;
	}

	public List<TipoOrgao> recuperarTiposOrgaoValidos()
			throws AtlasAcessoBancoDadosException {
		return getDao().recuperarTiposOrgaoValidos();
	}

	public List<TipoOrgao> recuperarTiposOrgaoRelacionadosEnteIndicadorMapa(int idTipoRamo, IndicadorMapa indicadorMapa)
			throws AtlasAcessoBancoDadosException {
		return getDao().recuperarTiposOrgaoRelacionadosEnteIndicadorMapa(idTipoRamo, indicadorMapa);
	}

	public List<TipoOrgao> recuperarTiposOrgaoPorRamo(int idTipoRamo)
			throws AtlasAcessoBancoDadosException {
		return getDao().recuperarTiposOrgaoPorRamo(idTipoRamo);
	}
}
