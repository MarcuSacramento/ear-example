package br.gov.atlas.dao;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import br.gov.atlas.dao.arquitetura.AtlasQuery;
import br.gov.atlas.entidade.Projeto;
import br.gov.atlas.entity.ConsultaPaginada;
import br.gov.atlas.entity.ListaPaginada;
import br.gov.atlas.exception.AtlasAcessoBancoDadosException;

public class ProjetoDao extends AtlasDao<Projeto>{

	public static final String SQL_SELECT_PROJETOS = "SELECT id_projeto "
			+ "FROM atlas.projeto as projeto "
			+ "WHERE ind_ativo = 'S' "
			+ "ORDER BY nme_projeto ASC";
	
	public ProjetoDao(EntityManager em) {
		super(em);
	}
		
	public ListaPaginada<Projeto> recuperarProjetos(Projeto filtro) throws AtlasAcessoBancoDadosException {
		ConsultaPaginada consultaPaginada = new ConsultaPaginada(filtro.getPrimeiroRegistro());
		StringBuilder query = new StringBuilder();
		query.append("SELECT id_projeto FROM atlas.projeto projeto ");

		if (filtro.getNome() != null && !filtro.getNome().trim().isEmpty()){
			query.append("WHERE upper(projeto.nme_projeto) like :nome ");
			consultaPaginada.getParametros().put("nome", filtro.getNome().toUpperCase());
		}
		
		query.append(" ORDER BY projeto.nme_projeto");
		consultaPaginada.setQueryString(query.toString());
		ListaPaginada<Projeto> lista = efetuarPesquisaPaginada(consultaPaginada);
		Projeto projeto = new Projeto();

		for (Object obj : lista.getResultadoPesquisa()) {
			projeto = pesquisarPorId((Integer) obj, Projeto.class);
			lista.getResultadoRetorno().add(projeto);
		}
		return lista;
	}
	
	public List<Projeto> recuperarProjetos(Integer page) throws AtlasAcessoBancoDadosException {
		final int REGISTROS_POR_PAGINA = 21;

		StringBuilder query = new StringBuilder();
		query.append(SQL_SELECT_PROJETOS);

		int firstRegister = page - 1;
		query.append(" OFFSET ").append(REGISTROS_POR_PAGINA * firstRegister);
		query.append(" LIMIT  ").append(REGISTROS_POR_PAGINA);

		AtlasQuery nativeQuery = createNativeQuery(query.toString());

		List<Projeto> listProjeto = new ArrayList<>();
		Projeto p;

		for (Object obj : nativeQuery.getResultList()) {
			p = pesquisarPorId((Integer) obj, Projeto.class);
			listProjeto.add(p);
		}
		return listProjeto;
	}
	
	public Integer recuperarTotalDePaginas() throws AtlasAcessoBancoDadosException {
		final double REGISTROS_POR_PAGINA = 9;

		StringBuilder query = new StringBuilder();

		query.append("SELECT ");
		query.append(" count(*) as CONTAGEM ");
		query.append("FROM ");
		query.append(" atlas.projeto projeto ");

		AtlasQuery nativeQuery = createNativeQuery(query.toString());
		BigInteger contagem = (BigInteger) nativeQuery.getResultList().get(0);

		double totalOfPagesDouble = contagem.doubleValue() / REGISTROS_POR_PAGINA;
		Integer totalOfPagesInteger = (int) Math.ceil(totalOfPagesDouble);

		return totalOfPagesInteger;
	}

}
