package br.gov.atlas.dao;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;

import br.gov.atlas.dao.arquitetura.AtlasQuery;
import br.gov.atlas.entidade.Cartilha;
import br.gov.atlas.entity.ConsultaPaginada;
import br.gov.atlas.entity.ListaPaginada;
import br.gov.atlas.exception.AtlasAcessoBancoDadosException;

public class CartilhaDao extends AtlasDao<Cartilha> {

	public CartilhaDao(EntityManager em) {
		super(em);
	}

	public Cartilha recuperarCartilha(Integer pk) throws AtlasAcessoBancoDadosException {
		StringBuilder sb = new StringBuilder("");
		sb.append(" FROM Cartilha cartilha  ");
		sb.append(" WHERE cartilha.id = :pk ");

		AtlasQuery query = createQuery(sb.toString(), Cartilha.class);
		query.setParameter("pk", pk);

		return (Cartilha) query.getSingleResult();
	}

	public List<Cartilha> recuperarCartilhas(Integer page, boolean somentePublicacoesConsumidor) throws AtlasAcessoBancoDadosException {
		final int REGISTERS_PER_PAGE = 9;

		StringBuilder query = new StringBuilder();
		query.append(" SELECT ");
		query.append(" 		id_cartilha ");
		query.append(" FROM ");
		query.append(" 		atlas.cartilha as cartilha ");
		if (somentePublicacoesConsumidor) {
			query.append(" WHERE ");
			query.append(" 		cartilha.ind_consumidor = :indicadorConsumidor ");
		}
		query.append(" ORDER BY ");
		query.append(" 		nme_titulo_cartilha ");

		int firstRegister = page - 1;
		query.append(" OFFSET ").append(REGISTERS_PER_PAGE * firstRegister);
		query.append(" LIMIT  ").append(REGISTERS_PER_PAGE);

		AtlasQuery nativeQuery = createNativeQuery(query.toString());

		if (somentePublicacoesConsumidor) {
			nativeQuery.setParameter("indicadorConsumidor", "S");
		}

		List<Cartilha> listCartilha = new ArrayList<>();

		Cartilha c;

		for (Object obj : nativeQuery.getResultList()) {

			c = pesquisarPorId((Integer) obj, Cartilha.class);

			listCartilha.add(c);
		}

		return listCartilha;
	}

	public Integer recuperarTotalDePaginas(boolean somentePublicacoesConsumidor) throws AtlasAcessoBancoDadosException {
		final double REGISTERS_PER_PAGE = 9;

		StringBuilder query = new StringBuilder();

		query.append("SELECT  		 	   ");
		query.append("  count(*) as CONTAGEM ");
		query.append("FROM  ");
		query.append("  atlas.cartilha cartilha ");
		if (somentePublicacoesConsumidor) {
			query.append(" WHERE ");
			query.append(" 		cartilha.ind_consumidor = :indicadorConsumidor ");
		}

		AtlasQuery nativeQuery = createNativeQuery(query.toString());
		if (somentePublicacoesConsumidor) {
			nativeQuery.setParameter("indicadorConsumidor", "S");
		}

		BigInteger contagem = (BigInteger) nativeQuery.getResultList().get(0);

		double totalOfPagesDouble = contagem.doubleValue() / REGISTERS_PER_PAGE;
		Integer totalOfPagesInteger = (int) Math.ceil(totalOfPagesDouble);

		return totalOfPagesInteger;
	}

	
	public ListaPaginada<Cartilha> recuperarCartilhas(Cartilha cartilhaFiltro) throws AtlasAcessoBancoDadosException {
		ConsultaPaginada consultaPaginada = new ConsultaPaginada(cartilhaFiltro.getPrimeiroRegistro());
		StringBuilder query = new StringBuilder();
		query.append("SELECT id_cartilha FROM atlas.cartilha cartilha ");

		if (cartilhaFiltro.getTitulo() != null && !cartilhaFiltro.getTitulo().trim().isEmpty()) {
			query.append("WHERE upper(cartilha.nme_titulo_cartilha) like :titulo ");
			consultaPaginada.getParametros().put("titulo", cartilhaFiltro.getTitulo().toUpperCase());
		}

		query.append(" ORDER BY cartilha.nme_titulo_cartilha");
		consultaPaginada.setQueryString(query.toString());

		ListaPaginada<Cartilha> lista = efetuarPesquisaPaginada(consultaPaginada);

		Cartilha cartilha = new Cartilha();

		for (Object obj : lista.getResultadoPesquisa()) {
			cartilha = pesquisarPorId((Integer) obj, Cartilha.class);
			lista.getResultadoRetorno().add(cartilha);
		}

		return lista;

	}

}
