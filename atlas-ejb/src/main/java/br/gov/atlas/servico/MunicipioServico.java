package br.gov.atlas.servico;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import br.gov.atlas.dao.MunicipioDao;
import br.gov.atlas.entidade.Municipio;
import br.gov.atlas.entidade.UF;
import br.gov.atlas.exception.AtlasAcessoBancoDadosException;

@Stateless(name = "MunicipioServico")
@LocalBean
public class MunicipioServico extends AtlasServicoImpl {

	public List<Municipio> recuperarMunicipios(UF uf) throws AtlasAcessoBancoDadosException {
		MunicipioDao dao = new MunicipioDao(em);
		return dao.recuperarMunicipios(uf);
	}

	@Override
	public MunicipioDao getDao() {
		return new MunicipioDao(em);
	}

	public List<Municipio> recuperarMunicipiosSemCoordenadas() throws AtlasAcessoBancoDadosException {
		return getDao().recuperarMunicipiosSemCoordenadas();
	}

	public Municipio recuperPorNomeMunicipioSiglaUf(String nomeMunicipio,
			String siglaUf) throws AtlasAcessoBancoDadosException {
		return getDao().recuperPorNomeMunicipioSiglaUf(nomeMunicipio, siglaUf);
	}

}
