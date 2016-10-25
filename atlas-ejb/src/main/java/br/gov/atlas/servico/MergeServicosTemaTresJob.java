package br.gov.atlas.servico;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Schedule;
import javax.ejb.Stateless;

import br.gov.atlas.dao.ServicoDao;
import br.gov.atlas.entidade.Servico;
import br.gov.atlas.exception.AtlasAcessoBancoDadosException;

@Stateless(name = "MergeServicosTemaTresJob")
@LocalBean
public class MergeServicosTemaTresJob extends AtlasServicoImpl {

	private static final List<Servico> LISTA_SERVICOS = new ArrayList<Servico>();

	@EJB(name = "ManterTematresServico")
	private ManterTematresServico tematresServico;

	private ServicoDao dao;

	@Override
	public ServicoDao getDao() {
		if (dao == null) {
			dao = new ServicoDao(em);
		}
		return dao;
	}

//	@Schedule(second = "0", minute = "10", hour = "*/1")
	public void mergeServicosTemaTres() throws AtlasAcessoBancoDadosException {
		if (!LISTA_SERVICOS.isEmpty()) {
			getDao().mergeTematresServico(LISTA_SERVICOS);
		}
		LISTA_SERVICOS.clear();
	}

//	@Schedule(second = "0", minute = "0", hour = "*/1")
	public void carregarServicos() throws AtlasAcessoBancoDadosException {
		if (LISTA_SERVICOS.isEmpty()) {
			LISTA_SERVICOS.addAll(tematresServico.recuperarServicos());
		}
	}

}
