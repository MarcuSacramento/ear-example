package br.gov.atlas.servico;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import br.gov.atlas.dao.TipoOrgaoDao;
import br.gov.atlas.entidade.Ramo;
import br.gov.atlas.entidade.Servico;
import br.gov.atlas.entidade.TipoOrgao;
import br.gov.atlas.entity.ListaPaginada;
import br.gov.atlas.exception.AtlasAcessoBancoDadosException;

@Stateless(name = "ManterTipoOrgaoServico")
@LocalBean
public class ManterTipoOrgaoServico extends AtlasServicoImpl {

	TipoOrgaoDao dao;

	@Override
	public TipoOrgaoDao getDao() {
		if(this.dao==null) {
			this.dao = new TipoOrgaoDao(this.em);
		}
		return this.dao;
	}

	public ListaPaginada<TipoOrgao> recuperarTiposOrgao(TipoOrgao tipoOrgao)
			throws AtlasAcessoBancoDadosException {
		return getDao().recuperarTiposOrgao(tipoOrgao);
	}

	public int contarPorRamo(Ramo ramo)
			throws AtlasAcessoBancoDadosException {
		return getDao().contarPorRamo(ramo);
	}

	public TipoOrgao recuperarPorNome(String nome) throws AtlasAcessoBancoDadosException{
		return getDao().recuperarPorNome(nome);
	}

	public List<Servico> recuperarServicosDisponiveisSelecao(TipoOrgao tipoOrgao, String nomeServico)
			throws AtlasAcessoBancoDadosException {
		return getDao().recuperarServicosDisponiveisSelecao(tipoOrgao, nomeServico);
	}
}