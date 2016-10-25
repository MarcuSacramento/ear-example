package br.gov.atlas.servico;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import br.gov.atlas.dao.UsuarioDao;
import br.gov.atlas.dto.UsuarioDTO;
import br.gov.atlas.entidade.Funcionalidade;
import br.gov.atlas.entidade.NivelAcesso;
import br.gov.atlas.entidade.Orgao;
import br.gov.atlas.entidade.Perfil;
import br.gov.atlas.entidade.Usuario;
import br.gov.atlas.entity.ListaPaginada;
import br.gov.atlas.enums.TipoFuncionalidade;
import br.gov.atlas.enums.TipoNivelAcesso;
import br.gov.atlas.exception.AtlasAcessoBancoDadosException;

@Stateless(name = "UsuarioServico")
@LocalBean
public class UsuarioServico extends AtlasServicoImpl {

	private UsuarioDao dao;
	
	@EJB(name = "OrgaoServico")
	private OrgaoServico orgaoServico;

	@Override
	public UsuarioDao getDao() {
		if (dao == null) {
			dao = new UsuarioDao(em);
		}
		return dao;
	}

	public Usuario recuperarUsuario(String login, String senha) throws AtlasAcessoBancoDadosException {
		return getDao().recuperarUsuario(login, senha);
	}
	
	public Usuario recuperarEmailPotUsuario(String login) throws AtlasAcessoBancoDadosException {
		return getDao().recuperarEmailPotUsuario(login);
	}
	
	public ListaPaginada<Usuario> recuperarUsuarios(UsuarioDTO usuarioDTO) throws AtlasAcessoBancoDadosException {
		return getDao().recuperarUsuarios(usuarioDTO);
	}
	
	public List<Perfil> recuperarPerfis(Usuario usuario) throws AtlasAcessoBancoDadosException{
		return getDao().recuperarPerfis(usuario);
	}

	public List<Usuario> recuperarUsuariosPorOrgaos(List<Orgao> orgaos) throws AtlasAcessoBancoDadosException {
		return getDao().recuperarUsuariosPorOrgaos(orgaos);
	}

	/**
	 * Verifica se o nível de acesso do usuário autenticado é o mesmo do nível de acesso pesquisado por parâmetro.
	 * Este método é utilizado para exibir os botões de exclusão, inserção e gravar.
	 * @param usuarioAutenticado
	 * @param codFuncionalidade
	 * @param nivelAcessoParametro
	 * @return {@link Boolean}
	 */
	public boolean isPossuiAcessoFuncionalidadeServico(
			Usuario usuarioAutenticado, String codFuncionalidade, TipoNivelAcesso nivelAcessoParametro) {
		if (usuarioAutenticado != null && codFuncionalidade != null
				&& !codFuncionalidade.trim().equals("") && nivelAcessoParametro != null) {
			TipoFuncionalidade tpFuncionalidade = TipoFuncionalidade.recuperarPeloCodigo(codFuncionalidade);
			List<Perfil> perfis = usuarioAutenticado.getPerfis();
			for (Perfil perfilUsuario : perfis) {
				List<NivelAcesso> niveisAcesso = perfilUsuario.getNiveisAcesso();
				for (NivelAcesso nivelAcessoUsuario : niveisAcesso) {
					TipoNivelAcesso tipoNivelAcessoUsuario = TipoNivelAcesso.getTipo(nivelAcessoUsuario.getIndNivelAcesso());
					if(tipoNivelAcessoUsuario.equals(nivelAcessoParametro)){
						Funcionalidade funcionalidade = nivelAcessoUsuario.getFuncionalidade();
						if(funcionalidade.getCodigo().equals(tpFuncionalidade.getCodigo())){
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	/**
	 * Verifica se o usuário possui acesso a determinada funcionalidade.
	 * Este método é utilizado para exibir os itens de menu de acordo com o perfil do usuário.
	 * @param usuarioAutenticado
	 * @param codFuncionalidade
	 * @return {@link Boolean}
	 */
	public boolean isPossuiAcessoFuncionalidadeServico(Usuario usuarioAutenticado, String codFuncionalidade) {
		if (usuarioAutenticado != null && codFuncionalidade != null
				&& !codFuncionalidade.trim().equals("")) {
			TipoFuncionalidade tpFuncionalidade = TipoFuncionalidade.recuperarPeloCodigo(codFuncionalidade);
			List<Perfil> perfis = usuarioAutenticado.getPerfis();
			for (Perfil perfilUsuario : perfis) {
				List<NivelAcesso> niveisAcesso = perfilUsuario.getNiveisAcesso();
				for (NivelAcesso nivelAcessoUsuario : niveisAcesso) {
					TipoNivelAcesso tipoNivelAcesso = TipoNivelAcesso.getTipo(nivelAcessoUsuario.getIndNivelAcesso());
					if(!tipoNivelAcesso.equals(TipoNivelAcesso.SEM_ACESSO)){
						Funcionalidade funcionalidade = nivelAcessoUsuario.getFuncionalidade();
						if(funcionalidade.getCodigo().equals(tpFuncionalidade.getCodigo())){
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	/**
	 * Metodo responsavel por recuperar todos os usuarios vinculados aos mesmos orgãos do usuario autenticado e 
	 * transformar o objeto usuario em um objeto do tipo usuarioDTO.
	 * @param listaUsuarios
	 * @return lista de usuarios dto
	 */
	public List<UsuarioDTO> recuperarUsuariosDTO(Usuario usuarioAutenticado) {
		List<Usuario> usuarios = new ArrayList<Usuario>();
		List<UsuarioDTO> usuariosDtos = new ArrayList<UsuarioDTO>();
		
		try {
			// Recupera os orgãos do usuarios autenticado e depois busca os usuarios que estão vinculados aos mesmos orgãos do usuario autenticado.
			List<Orgao> orgaosUsuarioAutenticado = orgaoServico.recuperarOrgaosRelacionadosESubordinados(usuarioAutenticado);
			if(orgaosUsuarioAutenticado != null && !orgaosUsuarioAutenticado.isEmpty()){
				usuarios.addAll(recuperarUsuariosPorOrgaos(orgaosUsuarioAutenticado));
			}
			for (Usuario usuario : usuarios) {
				// Transforma o usuario em usuarioDTO
				usuariosDtos.add(montaObjetoUsuarioDTO(usuario));
			}
			// Caso não encontre usuários vinculados aos mesmos órgãos, será apresentado somente o usuário logado.
			if(usuariosDtos.isEmpty()){
				usuariosDtos.add(montaObjetoUsuarioDTO(usuarioAutenticado));
			}
		} catch (AtlasAcessoBancoDadosException e) {
			e.printStackTrace();
		}
		
		return usuariosDtos;
	}

	private UsuarioDTO montaObjetoUsuarioDTO(Usuario usuario) {
		UsuarioDTO usuarioDTO = new UsuarioDTO();
		usuarioDTO.setId(usuario.getId());
		usuarioDTO.setCpf(usuario.getCpf());
		usuarioDTO.setDataInclusao(usuario.getDataInclusao());
		usuarioDTO.setDdd(usuario.getDdd());
		usuarioDTO.setEmail(usuario.getEmail());
		usuarioDTO.setNome(usuario.getNome());
		usuarioDTO.setSexo(usuario.getSexo());
		usuarioDTO.setSituacao(usuario.getSituacao());
		usuarioDTO.setTelefone(usuario.getTelefone());
		return usuarioDTO;
	}
}
