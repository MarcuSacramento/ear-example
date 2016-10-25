package br.gov.atlas.servico;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import br.gov.atlas.dao.UFDao;
import br.gov.atlas.dto.OrgaoDTO;
import br.gov.atlas.entidade.UF;
import br.gov.atlas.exception.AtlasAcessoBancoDadosException;

@Stateless(name = "UFServico")
@LocalBean
public class UFServico extends AtlasServicoImpl {

	@Override
	public UFDao getDao() {
		return new UFDao(em);
	}

	public UF recuperarUFPorSigla(String sigla) throws AtlasAcessoBancoDadosException {
		return getDao().recuperarUFPorSigla(sigla);
	}

	public List<UF> recuperarUFsPorRegiao(Integer idRegiao) throws AtlasAcessoBancoDadosException {
		return getDao().recuperarUFsPorRegiao(idRegiao);
	}
	
	public List<UF> recuperarTodasAsUFs() throws AtlasAcessoBancoDadosException {
		return getDao().recuperarTodasAsUFs();
	}
	
	public List<OrgaoDTO> recuperarTipoOrgaoPorUF(String nomeRegiao) throws AtlasAcessoBancoDadosException {
		return getDao().recuperarTipoOrgaoPorUF(nomeRegiao);
	}

}
