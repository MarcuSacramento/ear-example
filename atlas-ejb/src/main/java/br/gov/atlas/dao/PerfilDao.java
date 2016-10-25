package br.gov.atlas.dao;

import java.util.List;

import javax.persistence.EntityManager;

import br.gov.atlas.dao.arquitetura.AtlasQuery;
import br.gov.atlas.entidade.NivelAcesso;
import br.gov.atlas.entidade.Perfil;
import br.gov.atlas.entity.ConsultaPaginada;
import br.gov.atlas.entity.ListaPaginada;
import br.gov.atlas.exception.AtlasAcessoBancoDadosException;

public class PerfilDao extends AtlasDao<Perfil> {

	public PerfilDao(EntityManager em) {
		super(em);
	}

	public ListaPaginada<Perfil> recuperarPerfis(Perfil perfil) throws AtlasAcessoBancoDadosException {
		return recuperarPerfis(perfil, null);
	}
	
	@SuppressWarnings("unchecked")
	public ListaPaginada<Perfil> recuperarPerfis(Perfil perfil, String ordenacao) throws AtlasAcessoBancoDadosException {
		ConsultaPaginada consultaPaginada = new ConsultaPaginada(perfil.getPrimeiroRegistro());
		StringBuilder query = new StringBuilder();
		StringBuilder where = new StringBuilder();
		query.append("select perfil.id_perfil, perfil.nme_perfil, perfil.dsc_finalidade, perfil.ind_situacao_perfil, perfil.dta_expiracao_perfil from seguranca.perfil as perfil ");

		if (perfil != null && perfil.getNome() != null && perfil.getNome().trim() != "") {
			where.append("where upper(perfil.nme_perfil) like :nome");
			consultaPaginada.getParametros().put("nome", perfil.getNome().toUpperCase());
		}

		if (perfil != null && perfil.getDescricao() != null) {
			if (where.length() <= 0) {
				where.append("WHERE ");
			} else {
				where.append(" AND ");
			}
			where.append("upper(perfil.dsc_finalidade) like :descricao");
			consultaPaginada.getParametros().put("descricao", "%" + perfil.getDescricao().toUpperCase() + "%");
		}
		query.append(where);

		if(ordenacao != null && !ordenacao.equals("")){
			query.append(" ORDER BY "+ordenacao);
		}
		
		consultaPaginada.setQueryString(query.toString());
		
		ListaPaginada<Perfil> lista = efetuarPesquisaPaginada(consultaPaginada);

		Perfil perfilConsulta = new Perfil();

		for (Object obj : lista.getResultadoPesquisa()) {
			Object[] item = (Object[]) obj;

			perfilConsulta = new Perfil();
			perfilConsulta.setId(new Integer(item[0].toString()));
			perfilConsulta.setNome(String.valueOf(item[1]));
			perfilConsulta.setDescricao(String.valueOf(item[2]));
			perfilConsulta.setSituacao(String.valueOf(item[3]));

			lista.getResultadoRetorno().add(perfilConsulta);
		}

		return lista;
	}
	
	
	public Boolean isPerfilAssociadoAAlgumUsuario(Perfil perfil) throws AtlasAcessoBancoDadosException {
		StringBuilder queryString = new StringBuilder();
		queryString.append(" select * from ( "															 ); 
		queryString.append(" 	 select true where exists ( "											 );
		queryString.append(" 		select 1 from seguranca.perfil_usuario where id_perfil = " ).append  ( perfil.getId() );
		queryString.append(" 	 ) "																	 );
		queryString.append("     union all "															 );
		queryString.append(" 	 select false "															 );
		queryString.append(" ) perfil_esta_associado_a_algum_usuario "									 );
		queryString.append(" limit 1 " 																	 );
		
		AtlasQuery atlasQuery = createNativeQuery(queryString.toString());
		
		Boolean isJaAssociado = (Boolean) atlasQuery.getSingleResult();
		return isJaAssociado;
	}

	public Boolean isPerfilExiste(Perfil perfil) throws AtlasAcessoBancoDadosException {
		StringBuilder queryString = new StringBuilder();
		queryString.append(" select * from ( "															 ); 
		queryString.append(" 	 select true where exists ( "											 );
		queryString.append(" 		select 1 from seguranca.perfil where nme_perfil = \'" ).append  ( perfil.getNome() ).append("\'");
		if(perfil.getId() != null){
			queryString.append(" 	  	and id_perfil <> ").append(perfil.getId());
		}
		queryString.append(" 	 ) "																	 );
		queryString.append("     union all "															 );
		queryString.append(" 	 select false "															 );
		queryString.append(" ) perfil_existe "									 );
		queryString.append(" limit 1 " 																	 );
		
		AtlasQuery atlasQuery = createNativeQuery(queryString.toString());
		
		return (Boolean) atlasQuery.getSingleResult();
	}

	@SuppressWarnings("unchecked")
	public List<NivelAcesso> recuperarNiveisAcesso(Perfil perfil) throws AtlasAcessoBancoDadosException {
		StringBuilder query = new StringBuilder();
		query.append("select niveisAcesso from Perfil as perfil where perfil = :perfil");
		AtlasQuery atlasQuery = createQuery(query.toString());
		atlasQuery.setParameter("perfil", perfil);
		return atlasQuery.getResultList();
	}
	
	public void removerNiveisAcesso(Perfil perfil) throws AtlasAcessoBancoDadosException {
		StringBuilder queryDelete = new StringBuilder();
		queryDelete.append("DELETE FROM SEGURANCA.NIVEL_ACESSO WHERE ID_PERFIL = "+perfil.getId());
		AtlasQuery atlasQuery = createNativeQuery(queryDelete.toString());
		atlasQuery.executeUpdate();
	}

}
