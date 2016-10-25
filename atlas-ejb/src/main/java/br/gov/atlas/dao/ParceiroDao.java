package br.gov.atlas.dao;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import br.gov.atlas.dao.arquitetura.AtlasQuery;
import br.gov.atlas.entidade.Parceiro;
import br.gov.atlas.entity.ConsultaPaginada;
import br.gov.atlas.entity.ListaPaginada;
import br.gov.atlas.exception.AtlasAcessoBancoDadosException;

public class ParceiroDao extends AtlasDao<Parceiro> {
	
	public static final String SQL_SELECT_PARCEIROS = "SELECT id_parceiro "
			+ "FROM atlas.parceiro as parceiro "
			+ "WHERE ind_ativo = 'S' "
			+ "ORDER BY nme_parceiro ASC";
	
	public ParceiroDao(EntityManager em) {
		super(em);
	}
		
	public ListaPaginada<Parceiro> recuperarParceiros(Parceiro filtro) throws AtlasAcessoBancoDadosException {
		ConsultaPaginada consultaPaginada = new ConsultaPaginada(filtro.getPrimeiroRegistro());
		StringBuilder query = new StringBuilder();
		query.append("SELECT id_parceiro FROM atlas.parceiro parceiro ");

		if (filtro.getNome() != null && !filtro.getNome().trim().isEmpty()){
			query.append("WHERE upper(parceiro.nme_parceiro) like :nome ");
			consultaPaginada.getParametros().put("nome", filtro.getNome().toUpperCase());
		}

		query.append(" ORDER BY parceiro.nme_parceiro");
		consultaPaginada.setQueryString(query.toString());
		ListaPaginada<Parceiro> lista = efetuarPesquisaPaginada(consultaPaginada);
		Parceiro parceiro = new Parceiro();

		for (Object obj : lista.getResultadoPesquisa()) {
			parceiro = pesquisarPorId((Integer) obj, Parceiro.class);
			lista.getResultadoRetorno().add(parceiro);
		}
		return lista;
	}
	
	public List<Parceiro> recuperarParceiros(Integer page) throws AtlasAcessoBancoDadosException {
		final int REGISTROS_POR_PAGINA = 21;

		StringBuilder query = new StringBuilder();
		query.append(SQL_SELECT_PARCEIROS);

		int firstRegister = page - 1;
		query.append(" OFFSET ").append(REGISTROS_POR_PAGINA * firstRegister);
		query.append(" LIMIT  ").append(REGISTROS_POR_PAGINA);

		AtlasQuery nativeQuery = createNativeQuery(query.toString());

		List<Parceiro> listParceiro = new ArrayList<>();
		Parceiro p;

		for (Object obj : nativeQuery.getResultList()) {
			p = pesquisarPorId((Integer) obj, Parceiro.class);
			listParceiro.add(p);
		}
		return listParceiro;
	}
	
	public Integer recuperarTotalDePaginas() throws AtlasAcessoBancoDadosException {
		final double REGISTROS_POR_PAGINA = 21;

		StringBuilder query = new StringBuilder();

		query.append("SELECT ");
		query.append(" count(*) as CONTAGEM ");
		query.append("FROM ");
		query.append(" atlas.parceiro parceiro ");

		AtlasQuery nativeQuery = createNativeQuery(query.toString());
		BigInteger contagem = (BigInteger) nativeQuery.getResultList().get(0);

		double totalOfPagesDouble = contagem.doubleValue() / REGISTROS_POR_PAGINA;
		Integer totalOfPagesInteger = (int) Math.ceil(totalOfPagesDouble);

		return totalOfPagesInteger;
	}
	


}
