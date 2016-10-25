package br.gov.atlas.servico;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import br.gov.atlas.dao.UsuarioDao;
import br.gov.atlas.dto.UsuarioDTO;
import br.gov.atlas.entidade.Orgao;
import br.gov.atlas.entidade.Perfil;
import br.gov.atlas.entidade.Usuario;
import br.gov.atlas.entity.ListaPaginada;
import br.gov.atlas.exception.AtlasAcessoBancoDadosException;

@Stateless(name="ManterUsuarioServico")
@LocalBean
public class ManterUsuarioServico extends AtlasServicoImpl{

	private UsuarioDao dao;
	
	@EJB(name = "OrgaoServico")
	private OrgaoServico orgaoServico;

	@Override
	public UsuarioDao getDao() {
		if(dao==null){
			dao = new UsuarioDao(em);
		}
		return dao;
	}
	
	public ListaPaginada<Usuario> recuperarUsuarios(UsuarioDTO usuarioDTO, String orderBy ) throws AtlasAcessoBancoDadosException {
		return getDao().recuperarUsuarios(usuarioDTO, orderBy);
	}
	
	public ListaPaginada<Usuario> recuperarUsuarios(UsuarioDTO usuarioDTO) throws AtlasAcessoBancoDadosException {
		return getDao().recuperarUsuarios(usuarioDTO);
	}

	public List<Perfil> recuperarPerfis(Usuario usuario) throws AtlasAcessoBancoDadosException {
		return getDao().recuperarPerfis(usuario);
	}

	public static boolean isUsuarioMaster(Usuario usuario){
    	if (usuario != null){
			for (Perfil perfil : usuario.getPerfis()) {
				if (perfil.getTipo().equals(Perfil.MASTER)){
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean isUsuarioHabilitadoExcluir(Usuario usuarioSessao) {
		return isUsuarioMaster(usuarioSessao);
	}

	public boolean isUsuarioHabilitadoAlterar(Usuario usuarioSessao, Usuario usuarioAlterado) throws AtlasAcessoBancoDadosException {
    	if (usuarioSessao != null){
			for (Perfil perfil : usuarioSessao.getPerfis()) {
				if (perfil.getTipo().equals(Perfil.MASTER)){
					return true;
				}
				if(perfil.getTipo().equals(Perfil.GESTOR) && usuarioAlterado.getId() != null){
					List<Orgao> orgaosUsuarioAlterado = orgaoServico.recuperarOrgaosRelacionadosESubordinados(usuarioAlterado);
					List<Orgao> orgaosUsuarioLogado = orgaoServico.recuperarOrgaosRelacionadosESubordinados(usuarioSessao);
					if (orgaosUsuarioAlterado != null
							&& !orgaosUsuarioAlterado.isEmpty()
							&& orgaosUsuarioLogado != null
							&& !orgaosUsuarioLogado.isEmpty()) {
						for (Orgao orgaoUsuarioAlterado : orgaosUsuarioAlterado) {
							if(orgaosUsuarioLogado.contains(orgaoUsuarioAlterado)){
								return true;
							}
						}
					}
				}
			}
		}
		return false;
	}

	public boolean isUsuarioHabilitadoInserir(Usuario usuarioSessao) {
    	if (usuarioSessao != null){
			for (Perfil perfil : usuarioSessao.getPerfis()) {
				if (perfil.getTipo().equals(Perfil.MASTER)
						|| perfil.getTipo().equals(Perfil.GESTOR)) {
					return true;
				}
			}
		}
		return false;
	}
}
