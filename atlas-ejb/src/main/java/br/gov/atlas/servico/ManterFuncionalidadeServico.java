package br.gov.atlas.servico;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import br.gov.atlas.dao.FuncionalidadeDao;
import br.gov.atlas.entidade.Funcionalidade;
import br.gov.atlas.entidade.Perfil;
import br.gov.atlas.entity.ListaPaginada;
import br.gov.atlas.enums.TipoFuncionalidade;
import br.gov.atlas.exception.AtlasAcessoBancoDadosException;

@Stateless(name="ManterFuncionalidadeServico")
@LocalBean
public class ManterFuncionalidadeServico extends AtlasServicoImpl {

	FuncionalidadeDao dao;

	@Override
	public FuncionalidadeDao getDao() {
		if(dao==null) {
			dao = new FuncionalidadeDao(em);
		}
		return dao;
	}

	public ListaPaginada<Funcionalidade> recuperarFuncionalidades(
			Funcionalidade funcionalidadeFiltro) throws AtlasAcessoBancoDadosException {
		return getDao().recuperarFuncionalidades(funcionalidadeFiltro);
	}

	public String recuperarProximoCodigo() throws AtlasAcessoBancoDadosException {
		return getDao().recuperarProximoCodigo();
	}

	public boolean isExclusaoPermitida(Funcionalidade funcionalidade) {
		TipoFuncionalidade tipoFuncionalidade = TipoFuncionalidade.recuperarPeloCodigo(funcionalidade.getCodigo());
		if(tipoFuncionalidade != null){
			switch (tipoFuncionalidade) {
			case ORGAO: 			return false;
			case CARTILHAS: 		return false;
			case DADOS_EM_BLOCO: 	return false;
			case DICIONARIO: 		return false;
			case DOWNLOADS: 		return false;
			case FUNCIONALIDADE: 	return false;
			case PARCEIROS: 		return false;
			case PERFIL: 			return false;
			case PROJETOS: 			return false;
			case TEMAS: 			return false;
			case USUARIOS: 			return false;
			case VIDEOS: 			return false;
			case RAMOS:				return false;
			case TIPOS_ORGAO:		return false;
			case CARGOS:			return false;
			case IMPORTAR_ORGAO:	return false;
			case MENSAGERIAS:		return false;
			case SERVICOS:			return false;
			case TERMOS:			return false;
			}
		}
		return true;
	}

	public List<Funcionalidade> recuperarFuncionalidadesNaoEstaoVinculadasPerfil(
			Perfil perfil) throws AtlasAcessoBancoDadosException {
		return getDao()
				.recuperarFuncionalidadesNaoEstaoVinculadasPerfil(perfil);
	}
	
	public Boolean isFuncionalidadeAssociadaAAlgumNivelAcesso(Funcionalidade funcionalidade) throws AtlasAcessoBancoDadosException {
		return getDao().isFuncionalidadeAssociadaAAlgumNivelAcesso(funcionalidade);
	}

}