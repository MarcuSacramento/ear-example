package br.gov.atlas.servico;

import java.util.Date;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import br.gov.atlas.dao.TermoDao;
import br.gov.atlas.dto.TermoBuscaDTO;
import br.gov.atlas.entidade.Termo;
import br.gov.atlas.entity.ListaPaginada;
import br.gov.atlas.exception.AtlasAcessoBancoDadosException;

@Stateless(name="ManterTermoServico")
@LocalBean
public class ManterTermoServico extends AtlasServicoImpl {

	TermoDao dao;

	@Override
	public TermoDao getDao() {
		if(dao==null) {
			dao = new TermoDao(em);
		}
		return dao;
	}

	public ListaPaginada<TermoBuscaDTO> recuperarTermos(Termo termo) throws AtlasAcessoBancoDadosException {
		return getDao().recuperarTermos(termo);
	}

	public Termo recuperarUltimoRegistroAtualizadoPorNome(String nomeTermo) throws AtlasAcessoBancoDadosException {
		return getDao().recuperarUltimoRegistroAtualizadoPorNome(nomeTermo);
	}

	public int alterarDataBloqueioPorNomeTermo(String nomeTermo, Date dataBloqueio) throws AtlasAcessoBancoDadosException {
		return getDao().alterarDataBloqueioPorNomeTermo(nomeTermo, dataBloqueio);
	}
}