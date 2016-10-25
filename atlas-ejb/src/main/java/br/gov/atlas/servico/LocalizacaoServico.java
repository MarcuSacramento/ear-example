package br.gov.atlas.servico;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import br.gov.atlas.dao.AtlasDao;
import br.gov.atlas.entidade.Orgao;

@Stateless(name = "LocalizacaoServico")
@LocalBean
public class LocalizacaoServico extends AtlasServicoImpl {

	@Override
	public AtlasDao getDao() {
		return new AtlasDao<>(em, Orgao.class);
	}

	

}
