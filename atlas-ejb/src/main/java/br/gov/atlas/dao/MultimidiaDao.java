package br.gov.atlas.dao;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

import br.gov.atlas.dao.arquitetura.AtlasQuery;
import br.gov.atlas.entidade.Multimidia;
import br.gov.atlas.entity.ConsultaPaginada;
import br.gov.atlas.entity.ListaPaginada;
import br.gov.atlas.exception.AtlasAcessoBancoDadosException;

public class MultimidiaDao extends AtlasDao<Multimidia> {

	private static final String SQL_CONTAGEM = "SELECT count(*) as CONTAGEM FROM atlas.multimidia multimidia ";

	private static final String SQL_SELECT_MULTIMIDIAS = ""
			+ "SELECT id_multimidia, nme_multimidia, dsc_multimidia, nme_recurso, url_recurso, ind_tipo_recurso, dta_publicacao, ind_tela_inicial "
			+ "FROM atlas.multimidia multimidia ";

	public MultimidiaDao(EntityManager em) {
		super(em);
	}

	public Multimidia recuperarMultimidia(Integer pk) throws AtlasAcessoBancoDadosException {
		StringBuilder sb = new StringBuilder("");
		sb.append(" FROM Multimidia multimidia  ");
		sb.append(" WHERE multimidia.id = :pk ");

		AtlasQuery query = createQuery(sb.toString(), Multimidia.class);
		query.setParameter("pk", pk);

		return (Multimidia) query.getSingleResult();
	}

	public List<Multimidia> recuperarMultimidias(Integer page) throws AtlasAcessoBancoDadosException {
		final int REGISTERS_PER_PAGE = 4;

		StringBuilder query = new StringBuilder();
		query.append(SQL_SELECT_MULTIMIDIAS);

		int firstRegister = page - 1;
		query.append(" OFFSET ").append(REGISTERS_PER_PAGE * firstRegister);
		query.append(" LIMIT  ").append(REGISTERS_PER_PAGE);

		AtlasQuery nativeQuery = createNativeQuery(query.toString());

		List<Multimidia> listMultimidia = new ArrayList<>();

		Multimidia c;

		for (Object obj : nativeQuery.getResultList()) {
			Object[] item = (Object[]) obj;

			c = new Multimidia();
			c.setId(Integer.valueOf(item[0].toString()));
			c.setNome(String.valueOf(item[1]));
			c.setDescricao(String.valueOf(item[2]));
			c.setRecurso(String.valueOf(item[3]));
			c.setUrl(String.valueOf(item[4]));
			c.setTipoRecurso(String.valueOf(item[5]));

			listMultimidia.add(c);
		}

		return listMultimidia;
	}

	public Integer recuperarTotalDePaginas() throws AtlasAcessoBancoDadosException {
		final double REGISTERS_PER_PAGE = 4;

		StringBuilder query = new StringBuilder();

		query.append(SQL_CONTAGEM);

		AtlasQuery nativeQuery = createNativeQuery(query.toString());
		BigInteger contagem = (BigInteger) nativeQuery.getResultList().get(0);

		double totalOfPagesDouble = contagem.doubleValue() / REGISTERS_PER_PAGE;
		Integer totalOfPagesInteger = (int) Math.ceil(totalOfPagesDouble);

		return totalOfPagesInteger;
	}

	public List<Multimidia> recuperarMultimidiasTexto(Integer page) throws NumberFormatException, AtlasAcessoBancoDadosException {
		final int REGISTERS_PER_PAGE = 9;

		StringBuilder query = new StringBuilder();

		query.append(SQL_SELECT_MULTIMIDIAS);
		query.append("WHERE ind_tipo_recurso like 'T' ORDER BY dta_publicacao DESC");

		int firstRegister = page - 1;
		query.append(" OFFSET ").append(REGISTERS_PER_PAGE * firstRegister);
		query.append(" LIMIT  ").append(REGISTERS_PER_PAGE);

		AtlasQuery nativeQuery = createNativeQuery(query.toString());

		List<Multimidia> listMultimidia = new ArrayList<>();

		Multimidia c;

		for (Object obj : nativeQuery.getResultList()) {
			Object[] item = (Object[]) obj;

			c = new Multimidia();
			c.setId(Integer.valueOf(item[0].toString()));
			c.setNome(String.valueOf(item[1]));
			c.setDescricao(String.valueOf(item[2]));
			c.setRecurso(String.valueOf(item[3]));
			c.setUrl(String.valueOf(item[4]));
			c.setTipoRecurso(String.valueOf(item[5]));
			c.setDataPublicacao((Date) item[6]);

			listMultimidia.add(c);
		}

		return listMultimidia;
	}

	public Integer recuperarTotalDePaginasTexto() throws AtlasAcessoBancoDadosException {
		final double REGISTERS_PER_PAGE = 10;

		StringBuilder query = new StringBuilder();

		query.append(SQL_CONTAGEM);
		query.append("WHERE multimidia.ind_tipo_recurso like 'T'");

		AtlasQuery nativeQuery = createNativeQuery(query.toString());
		BigInteger contagem = (BigInteger) nativeQuery.getResultList().get(0);

		double totalOfPagesDouble = contagem.doubleValue() / REGISTERS_PER_PAGE;
		Integer totalOfPagesInteger = (int) Math.ceil(totalOfPagesDouble);

		return totalOfPagesInteger;
	}

	public List<Multimidia> recuperarMultimidiasPorTipo(Integer page, String tipo) throws NumberFormatException, AtlasAcessoBancoDadosException {
		final int REGISTERS_PER_PAGE = 30;
		return this.recuperarMultimidiasPorTipo(page, tipo, REGISTERS_PER_PAGE);
	}
	
	public List<Multimidia> recuperarMultimidiasPorTipo(Integer page, String tipo, int registrosPorPagina) throws NumberFormatException, AtlasAcessoBancoDadosException 
	{
		StringBuilder query = new StringBuilder();

		query.append(SQL_SELECT_MULTIMIDIAS);
		query.append("WHERE multimidia.ind_tipo_recurso like ").append("'" + tipo + "'");

		int firstRegister = page - 1;
		query.append(" OFFSET ").append(registrosPorPagina * firstRegister);
		query.append(" LIMIT  ").append(registrosPorPagina);

		AtlasQuery nativeQuery = createNativeQuery(query.toString());

		List<Multimidia> listMultimidia = new ArrayList<>();

		Multimidia c;

		for (Object obj : nativeQuery.getResultList()) {
			Object[] item = (Object[]) obj;

			c = new Multimidia();
			c.setId(Integer.valueOf(item[0].toString()));
			c.setNome(String.valueOf(item[1]));
			c.setDescricao(String.valueOf(item[2]));
			c.setRecurso(String.valueOf(item[3]));
			c.setUrl(String.valueOf(item[4]));
			c.setTipoRecurso(String.valueOf(item[5]));
			c.setDataPublicacao((Date) item[6]);

			listMultimidia.add(c);
		}

		return listMultimidia;
	}

	public Integer recuperarTotalDePaginasPorTipo(String tipo) throws AtlasAcessoBancoDadosException {
		final double REGISTERS_PER_PAGE = 4;

		StringBuilder query = new StringBuilder();

		query.append(SQL_CONTAGEM);
		query.append("WHERE multimidia.ind_tipo_recurso like ").append("'" + tipo + "'");

		AtlasQuery nativeQuery = createNativeQuery(query.toString());
		BigInteger contagem = (BigInteger) nativeQuery.getResultList().get(0);

		double totalOfPagesDouble = contagem.doubleValue() / REGISTERS_PER_PAGE;
		Integer totalOfPagesInteger = (int) Math.ceil(totalOfPagesDouble);

		return totalOfPagesInteger;
	}

	public ListaPaginada<Multimidia> recuperarMultimidias(Multimidia termoMultimidiaFiltro, String tipoMultimidia)
			throws AtlasAcessoBancoDadosException {
		ConsultaPaginada consultaPaginada = new ConsultaPaginada(termoMultimidiaFiltro.getPrimeiroRegistro());
		StringBuilder query = new StringBuilder();
		query.append(SQL_SELECT_MULTIMIDIAS);
		query.append("WHERE multimidia.ind_tipo_recurso like :tipoMultimidia ");

		consultaPaginada.getParametros().put("tipoMultimidia", tipoMultimidia);

		if (termoMultimidiaFiltro.getNome() != null) {
			query.append("and upper(multimidia.nme_multimidia) like :nome ");
			consultaPaginada.getParametros().put("nome", termoMultimidiaFiltro.getNome().toUpperCase());
		}
		query.append(" ORDER BY multimidia.nme_multimidia");
		consultaPaginada.setQueryString(query.toString());

		@SuppressWarnings("unchecked")
		ListaPaginada<Multimidia> lista = efetuarPesquisaPaginada(consultaPaginada);

		Multimidia termo = new Multimidia();

		for (Object obj : lista.getResultadoPesquisa()) {
			Object[] item = (Object[]) obj;

			termo = new Multimidia();
			termo.setId(new Integer(item[0].toString()));
			termo.setNome(String.valueOf(item[1]));
			termo.setDescricao(String.valueOf(item[2]));
			termo.setRecurso(String.valueOf(item[3]));
			termo.setUrl(String.valueOf(item[4]));
			termo.setTipoRecurso(String.valueOf(item[5]));
			if (item[7] != null) {
				termo.setTelaInicial(String.valueOf(item[7]));
			}
			lista.getResultadoRetorno().add(termo);
		}

		return lista;
	}

	public Multimidia recuperarVideoTelaPrincipal() throws AtlasAcessoBancoDadosException {
		StringBuilder sb = new StringBuilder("");
		sb.append(" FROM Multimidia multimidia  ");
		sb.append(" WHERE multimidia.tipoRecurso = 'V' AND multimidia.telaInicial ='S' ");

		AtlasQuery query = createQuery(sb.toString(), Multimidia.class);

		return (Multimidia) query.getSingleResult();
	}

}
