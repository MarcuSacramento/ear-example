package br.gov.atlas.servico;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import br.gov.atlas.dao.AtlasDao;
import br.gov.atlas.entidade.Assunto;

@Stateless(name = "assuntoServico")
@LocalBean
public class AssuntoServico extends AtlasServicoImpl {

	@Override
	public AtlasDao<Assunto> getDao() {
		return new AtlasDao<>(em, Assunto.class);
	}

}
