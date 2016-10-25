package br.gov.atlas.dao;

import java.util.List;

import javax.persistence.EntityManager;

import br.gov.atlas.dao.arquitetura.AtlasQuery;
import br.gov.atlas.entidade.Funcionalidade;
import br.gov.atlas.entidade.Perfil;
import br.gov.atlas.entity.ConsultaPaginada;
import br.gov.atlas.entity.ListaPaginada;
import br.gov.atlas.exception.AtlasAcessoBancoDadosException;

public class FuncionalidadeDao extends AtlasDao<Funcionalidade> {

	public FuncionalidadeDao(EntityManager em) {
		super(em);
	}
	
	public String recuperarProximoCodigo() throws AtlasAcessoBancoDadosException {
		StringBuilder queryString = new StringBuilder();
		queryString.append(" SELECT MAX(COD_FUNCIONALIDADE) FROM SEGURANCA.FUNCIONALIDADE ");
		AtlasQuery query = createNativeQuery(queryString.toString());
		Integer valorAtual = Integer.valueOf((String) query.getSingleResult());
		int proximoValor = valorAtual.intValue()+1;
		return String.format("%03d", proximoValor);
	}

	@SuppressWarnings("unchecked")
	public ListaPaginada<Funcionalidade> recuperarFuncionalidades(
			Funcionalidade funcionalidade) throws AtlasAcessoBancoDadosException {
		ConsultaPaginada consultaPaginada = new ConsultaPaginada(funcionalidade.getPrimeiroRegistro());
		StringBuilder query = new StringBuilder();
		query.append("select id_funcionalidade, cod_funcionalidade, nme_funcionalidade, dsc_funcionalidade ");
		query.append("from seguranca.funcionalidade ");

		if(funcionalidade != null){
			if (funcionalidade.getNome() != null || funcionalidade.getCodigo() != null) {
				query.append("where ");
			}
			if (funcionalidade.getNome() != null) {
				query.append(" upper(nme_funcionalidade) like :nome ");
				consultaPaginada.getParametros().put("nome", funcionalidade.getNome().toUpperCase());
			}
			if (funcionalidade.getCodigo() != null) {
				if(funcionalidade.getNome() != null){
					query.append(" and cod_funcionalidade = :codigo ");
				}else{
					query.append(" cod_funcionalidade = :codigo ");
				}
				consultaPaginada.getParametros().put("codigo", funcionalidade.getCodigo());
			}
		}
		query.append("order by cod_funcionalidade ");
		consultaPaginada.setQueryString(query.toString());

		ListaPaginada<Funcionalidade> lista = efetuarPesquisaPaginada(consultaPaginada);

		Funcionalidade funcionalidadeConsulta = new Funcionalidade();

		for (Object obj : lista.getResultadoPesquisa()) {
			Object[] item = (Object[]) obj;

			funcionalidadeConsulta = new Funcionalidade();
			funcionalidadeConsulta.setId(new Integer(item[0].toString()));
			funcionalidadeConsulta.setCodigo(String.valueOf(item[1]));
			funcionalidadeConsulta.setNome(String.valueOf(item[2]));
			funcionalidadeConsulta.setDescricao((item[3] != null) ? String.valueOf(item[3]) : null);

			lista.getResultadoRetorno().add(funcionalidadeConsulta);
		}

		return lista;
	}

	@SuppressWarnings("unchecked")
	public List<Funcionalidade> recuperarFuncionalidadesNaoEstaoVinculadasPerfil(
			Perfil perfil) throws AtlasAcessoBancoDadosException {
		StringBuilder queryString = new StringBuilder();
		queryString.append(" select");
		queryString.append(" 		f ");
		queryString.append(" from ");
		queryString.append(" 		Funcionalidade f ");
		queryString.append(" where ");
		queryString.append(" 		f not in( ");
		queryString.append(" 			select ");
		queryString.append(" 				DISTINCT(na.funcionalidade) ");
		queryString.append(" 			from ");
		queryString.append(" 				NivelAcesso na ");
		queryString.append(" 			where ");
		queryString.append(" 				na.perfil.id = :idPerfil ");
		queryString.append(" 				 ");
		queryString.append(" 			 ");
		queryString.append(" 		) ");
		queryString.append(" order by ");
		queryString.append(" 		f.codigo ");
		AtlasQuery query = createQuery(queryString.toString());
		query.setParameter("idPerfil", perfil.getId());
		return query.getResultList();
	}
	
	public Boolean isFuncionalidadeAssociadaAAlgumNivelAcesso(Funcionalidade funcionalidade) throws AtlasAcessoBancoDadosException 
	{
		StringBuilder queryString = new StringBuilder();	
		queryString.append(" SELECT 'TRUE'  FROM seguranca.nivel_acesso "									 ); 
		queryString.append(" 		WHERE EXISTS (SELECT id_funcionalidade FROM  seguranca.nivel_acesso WHERE id_funcionalidade = " ).append  ( funcionalidade.getId() 	);
		queryString.append(" 	 ) "																	 );
		queryString.append("     UNION "															 	 );
		queryString.append(" SELECT 'FALSE' FROM seguranca.nivel_acesso "									 );
		queryString.append(" 		WHERE NOT EXISTS (SELECT id_funcionalidade FROM  seguranca.nivel_acesso WHERE id_funcionalidade = " ).append  ( funcionalidade.getId() );
		queryString.append(" 	 ) "																	 );
		queryString.append(" limit 1 " 																	 );

		AtlasQuery atlasQuery = createNativeQuery(queryString.toString());

		Boolean isJaAssociado = null;
		String valor = atlasQuery.getSingleResult().toString();
		if(valor != null && !valor.equals("")){
			isJaAssociado = new Boolean(valor);
		}
		return isJaAssociado;

	}

}