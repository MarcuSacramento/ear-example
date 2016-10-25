package br.gov.atlas.servico;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import br.gov.atlas.dao.PerfilDao;
import br.gov.atlas.entidade.NivelAcesso;
import br.gov.atlas.entidade.Perfil;
import br.gov.atlas.entity.ListaPaginada;
import br.gov.atlas.exception.AtlasAcessoBancoDadosException;

@Stateless(name="ManterPerfilServico")
@LocalBean
public class ManterPerfilServico extends AtlasServicoImpl {

	PerfilDao dao;
	
	@Override
	public PerfilDao getDao() {
		if(dao==null) {
			dao = new PerfilDao(em);
		}
		return dao;
	}

	public ListaPaginada<Perfil> recuperarPerfis(Perfil perfil) throws AtlasAcessoBancoDadosException {
		return getDao().recuperarPerfis(perfil);
	}
	
	public ListaPaginada<Perfil> recuperarPerfis(Perfil perfil, String ordenacao) throws AtlasAcessoBancoDadosException {
		return getDao().recuperarPerfis(perfil, ordenacao);
	}

	public Boolean isPerfilAssociadoAAlgumUsuario(Perfil perfil) throws AtlasAcessoBancoDadosException {
		return getDao().isPerfilAssociadoAAlgumUsuario(perfil);
	}

	public Boolean isPerfilExiste(Perfil perfil) throws AtlasAcessoBancoDadosException {
		return getDao().isPerfilExiste(perfil);
	}

	public List<NivelAcesso> recuperarNiveisAcesso(Perfil perfil) throws AtlasAcessoBancoDadosException {
		return getDao().recuperarNiveisAcesso(perfil);
	}

	public void removerNiveisAcesso(Perfil perfil) throws AtlasAcessoBancoDadosException {
		getDao().removerNiveisAcesso(perfil);
	}

	public boolean possuiNiveisAcesso(Perfil perfil) throws AtlasAcessoBancoDadosException {
		List<NivelAcesso> niveisAcessoList = recuperarNiveisAcesso(perfil);
		return (niveisAcessoList != null && !niveisAcessoList.isEmpty());
	}
}
