package br.gov.atlas.dao;

import java.util.List;

import javax.persistence.EntityManager;

import br.gov.atlas.dao.arquitetura.AtlasQuery;
import br.gov.atlas.dto.UsuarioDTO;
import br.gov.atlas.entidade.Orgao;
import br.gov.atlas.entidade.Perfil;
import br.gov.atlas.entidade.Usuario;
import br.gov.atlas.entity.ConsultaPaginada;
import br.gov.atlas.entity.ListaPaginada;
import br.gov.atlas.exception.AtlasAcessoBancoDadosException;

public class UsuarioDao extends AtlasDao<Usuario> {

	public UsuarioDao(EntityManager em) {
		super(em);
	}

	public Usuario recuperarUsuario(String login, String senha) throws AtlasAcessoBancoDadosException {
		StringBuilder sb = new StringBuilder("");
		sb.append(" FROM Usuario usuario ");
		sb.append(" WHERE usuario.email like :login AND usuario.senha like md5(:senha) ");

		AtlasQuery query = createQuery(sb.toString(), Usuario.class);
		query.setParameter("login", login);
		query.setParameter("senha", senha);

		return (Usuario) query.getSingleResult();
	}
	
	@SuppressWarnings("unchecked")
	public List<Usuario> recuperarUsuariosPorOrgaos(List<Orgao> orgaos) throws AtlasAcessoBancoDadosException {
		StringBuilder query = new StringBuilder();
		query.append("select o.usuarios from Orgao o where o in :orgaos ORDER BY nme_usuario");
		AtlasQuery atlasQuery = createQuery(query.toString());
		atlasQuery.setParameter("orgaos", orgaos);
		return atlasQuery.getResultList();
	}
	
	public ListaPaginada<Usuario> recuperarUsuarios(UsuarioDTO usuarioDTO) throws AtlasAcessoBancoDadosException {
		return recuperarUsuarios(usuarioDTO, "nme_usuario");
	}
	
	@SuppressWarnings("unchecked")
    public ListaPaginada<Usuario> recuperarUsuarios(UsuarioDTO usuarioDTO, String orderBy) throws AtlasAcessoBancoDadosException {
            ConsultaPaginada consultaPaginada = new ConsultaPaginada(usuarioDTO.getPrimeiroRegistro());
            StringBuilder query = new StringBuilder();
            query.append("SELECT id_usuario, nme_usuario, cod_cpf, cod_email, cod_senha, cod_ddd, cod_telefone, ind_sexo, ind_situacao, dta_inclusao FROM seguranca.usuario ");
            
    		StringBuilder where = new StringBuilder();
            
            if (usuarioDTO.getNome() != null) {
                where.append("where upper(usuario.nme_usuario) like :nome");
                consultaPaginada.getParametros().put("nome", usuarioDTO.getNome().toUpperCase());
            }
            
            if(usuarioDTO.getUsuarios() != null && !usuarioDTO.getUsuarios().isEmpty()){
            	if(where.length() == 0){
            		where.append(" where ");
            	}else{
            		where.append(" and ");
            	}
            	where.append(" id_usuario in ("+recuperarIDs(usuarioDTO.getUsuarios())+")");
            }
            
            query.append(where);
            
            if(orderBy != null && !orderBy.equals("")){
            	query.append(" ORDER BY "+orderBy);
            }
            
            consultaPaginada.setQueryString(query.toString());
            ListaPaginada<Usuario> lista = efetuarPesquisaPaginada(consultaPaginada);
            
            Usuario u = new Usuario();
            
            for (Object obj : lista.getResultadoPesquisa()) {
                    Object[] item = (Object[]) obj;
                    
                    u = new Usuario();
                    u.setId(new Integer(item[0].toString()));
                    u.setNome(String.valueOf(item[1]));
                    u.setCpf(String.valueOf(item[2]));
                    u.setEmail(String.valueOf(item[3]));
                    u.setSenha(String.valueOf(item[4]));
                    u.setDdd(String.valueOf(item[5]));
                    u.setTelefone(String.valueOf(item[6]));
                    u.setSexo(String.valueOf(item[7]));
                    u.setSituacao(String.valueOf(item[8]));
                    u.setAlterarExcluir(false);
                    if (usuarioDTO.getUsuarios() != null && usuarioDTO.getUsuarios().size() > 0){
                    	for (UsuarioDTO usuarioDTOTemp : usuarioDTO.getUsuarios()) {
                    		if (usuarioDTOTemp.getId().equals(u.getId())){
                    			u.setAlterarExcluir(true);
                    		}
                    	}
                    } else {
                    	u.setAlterarExcluir(true);
                    }
                    
                    lista.getResultadoRetorno().add(u);
            }
            return lista;
    }

	private String recuperarIDs(List<UsuarioDTO> usuarios) {
		if (usuarios != null && !usuarios.isEmpty()) {
			StringBuilder sbID = new StringBuilder();
			for (UsuarioDTO usuarioDTO : usuarios) {
				sbID.append(usuarioDTO.getId()+",");
			}
			String valores = sbID.substring(0,sbID.toString().length()-1);
			return valores;
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public List<Perfil> recuperarPerfis(Usuario usuario) throws AtlasAcessoBancoDadosException {
		StringBuilder query = new StringBuilder();
		query.append("select perfil from Perfil perfil where :usuario in elements(perfil.usuarios)");
		AtlasQuery atlasQuery = createQuery(query.toString());
		atlasQuery.setParameter("usuario", usuario);
		return atlasQuery.getResultList();
	}
	
	public Usuario recuperarEmailPotUsuario(String login) throws AtlasAcessoBancoDadosException {
		StringBuilder sb = new StringBuilder("");
		sb.append(" FROM Usuario usuario ");
		sb.append(" WHERE usuario.email like :login");

		AtlasQuery query = createQuery(sb.toString(), Usuario.class);
		query.setParameter("login", login);
		
		return (Usuario) query.getSingleResult();
	}
	
	
}
