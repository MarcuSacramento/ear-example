package br.gov.atlas.dao;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.persistence.EntityManager;

import br.gov.atlas.dao.arquitetura.AtlasQuery;
import br.gov.atlas.entidade.Assunto;
import br.gov.atlas.entidade.FonteReferencia;
import br.gov.atlas.entidade.TermoGlossario;
import br.gov.atlas.entity.ConsultaPaginada;
import br.gov.atlas.entity.ListaPaginada;
import br.gov.atlas.exception.AtlasAcessoBancoDadosException;
import br.gov.atlas.servico.AssuntoServico;

public class TermoGlossarioDao extends AtlasDao<TermoGlossario> {

	private static final String SQL_SELECIONAR_TERMOS  = "SELECT id_termo, nme_termo, dsc_termo, dsc_saiba_mais, ind_situacao, dta_inclusao, id_termo_origem, id_assunto FROM atlas.termo_glossario as termo ";
	private static final String SQL_SELECIONAR_FONTES  = "SELECT fonte FROM FonteReferencia fonte WHERE fonte.termoGlossario = :termo ";
	private static final String SQL_SELECIONAR_FONTES_SUGESTOES  = "SELECT id_fonte_referencia, id_termo, nme_fonte, url_fonte, dsc_legislacao FROM fonte_referencia ";

	@EJB(name = "assuntoServico")
	AssuntoServico assuntoServico;

	public TermoGlossarioDao(EntityManager em) {
		super(em);
	}

	@SuppressWarnings("unchecked")
	public ListaPaginada<TermoGlossario> recuperarTermosPropostos(ArrayList<TermoGlossario> termos) throws AtlasAcessoBancoDadosException {
		ConsultaPaginada consulta = new ConsultaPaginada(0);
		StringBuilder query = new StringBuilder();
		query.append(SQL_SELECIONAR_TERMOS);
		// VERIFICA SE EXISTEM TERMOS PENDENTES/ PROPOSTOS.
		if (termos != null && termos.size() > 0) {
			query.append(" where id_termo in ( ");
			for (TermoGlossario termoGlossario : termos) {
				query.append(termoGlossario.getIdTermoOrigem() + ",");
			}
			query.delete(query.length() - 1, query.length()); // DELETA A ULTIMA ","
			query.append(" ) ");
		} else {
			query.append(" where id_termo in (0) ");
		}
		query.append(" GROUP BY termo.id_termo");
		query.append(" ORDER BY termo.nme_termo, dta_inclusao DESC ");
		consulta.setQueryString(query.toString());

		ListaPaginada<TermoGlossario> lista = efetuarPesquisaPaginada(consulta);

		TermoGlossario t = new TermoGlossario();

		for (Object obj : lista.getResultadoPesquisa()) {
			Object[] item = (Object[]) obj;

			t = new TermoGlossario();
			t.setId(new Integer(item[0].toString()));
			t.setNome(String.valueOf(item[1]));
			t.setDescricao(String.valueOf(item[2]));
			t.setSaibaMais(String.valueOf(item[3]));
			t.setIndicadorSituacao(String.valueOf(item[4]));
			t.setDataInclusao((java.util.Date)item[5]);
			t.setIdTermoOrigem(new Integer(item[6]==null?"0":item[6].toString()));
			t.setAssunto(new Assunto(new Integer(item[7].toString())));

			lista.getResultadoRetorno().add(t);
		}

		return lista;
	}

	@SuppressWarnings("unchecked")
	public ListaPaginada<TermoGlossario> recuperarTermosGlossario(TermoGlossario termo) throws AtlasAcessoBancoDadosException {
		ConsultaPaginada consulta = new ConsultaPaginada(termo.getPrimeiroRegistro());
		StringBuilder query = new StringBuilder();
		query.append(SQL_SELECIONAR_TERMOS);

		query.append(filtro(termo, consulta));
		query.append(" GROUP BY termo.id_termo, termo.nme_termo, termo.dsc_termo, termo.dsc_saiba_mais ");
		query.append(" ORDER BY termo.nme_termo, dta_inclusao DESC ");
		consulta.setQueryString(query.toString());

		ListaPaginada<TermoGlossario> lista = efetuarPesquisaPaginada(consulta);

		TermoGlossario t = new TermoGlossario();

		for (Object obj : lista.getResultadoPesquisa()) {
			Object[] item = (Object[]) obj;

			t = new TermoGlossario();
			t.setId(new Integer(item[0].toString()));
			t.setNome(String.valueOf(item[1]));
			t.setDescricao(String.valueOf(item[2]));
			t.setSaibaMais(String.valueOf(item[3]));
			t.setIndicadorSituacao(String.valueOf(item[4]));
			t.setDataInclusao((java.util.Date)item[5]);
			t.setIdTermoOrigem(new Integer(item[6]==null?"0":item[6].toString()));
			t.setAssunto(new Assunto(new Integer(item[7].toString())));

			lista.getResultadoRetorno().add(t);
		}

		return lista;
	}

	private StringBuilder filtro(TermoGlossario termo, ConsultaPaginada consulta) {
		StringBuilder where = new StringBuilder();

		if (termo!=null && termo.getNome()!=null && termo.getNome().trim() != "") {
			where.append("WHERE atlas.retirar_acento(termo.nme_termo) like UPPER(:nome) ");
			consulta.getParametros().put("nome", termo.getNome().toUpperCase());
		}
		if (termo.getAssunto() != null) {
			if (where.length() <= 0) {
				where.append("WHERE ");
			} else {
				where.append("AND ");
			}
			where.append("termo.id_assunto = :idAssunto ");
			consulta.getParametros().put("idAssunto", termo.getAssunto().getId());
		}
		if (termo.getIdTermoOrigem() != null){
			if (where.length() <= 0) {
				where.append("WHERE ");
			} else {
				where.append("AND ");
			}
			where.append("termo.id_termo_origem = :idTermoOrigem ");
			consulta.getParametros().put("idTermoOrigem", termo.getIdTermoOrigem());
		}
		if (termo.getIndicadorSituacao() != null){
			if (where.length() <= 0) {
				where.append("WHERE ");
			} else {
				where.append("AND ");
			}
			if (termo.getIndicadorSituacao().equals("P")){
				where.append("termo.ind_situacao = :indSituacao");
				consulta.getParametros().put("indSituacao", termo.getIndicadorSituacao());
			} else{
				where.append("termo.ind_situacao in ('P', 'A') ");
			}
		} else {
			if (where.length() <= 0) {
				where.append("WHERE IND_SITUACAO = 'D' ");
			} else {
				where.append(" AND IND_SITUACAO = 'D' ");
			}
		}
		return where;
	}

	public Integer pesquisarTermoPorNome(String nome) throws AtlasAcessoBancoDadosException {
		StringBuilder str = new StringBuilder();
		str.append("SELECT count(*) FROM atlas.termo_glossario WHERE upper(nme_termo) = :nome ");

		AtlasQuery query = createNativeQuery(str.toString());
		query.setParameter("nome", nome);
		BigInteger qtdRegistro = (BigInteger) query.getResultList().get(0);
		return qtdRegistro.intValue();
	}

	@SuppressWarnings("unchecked")
	public List<TermoGlossario> recuperarTermosGlossario(String textoParcial) throws AtlasAcessoBancoDadosException {
		String hql = " from TermoGlossario termo where termo.nome like :nome";
		AtlasQuery qry = createQuery(hql, TermoGlossario.class);
		qry.setParameter("nome", textoParcial);
		return new ArrayList<>(qry.getResultList());
	}

	public List<TermoGlossario> recuperarTermosGlossarioPorNomeSituacao(TermoGlossario termo) throws AtlasAcessoBancoDadosException {

		StringBuilder query = new StringBuilder();
		query.append(SQL_SELECIONAR_TERMOS);

		if(termo!=null && termo.getNome()!=null){
			query.append("WHERE atlas.retirar_acento(termo.nme_termo) like UPPER(:nome) ");
		}
		query.append(" AND IND_SITUACAO = :situacao ");
		query.append("ORDER BY termo.nme_termo ");

		AtlasQuery nativeQuery = createNativeQuery(query.toString());

		nativeQuery.setParameter("situacao", termo.getIndicadorSituacao());
		if(termo.getNome()!=null){
			nativeQuery.setParameter("nome", termo.getNome().toUpperCase());
		}

		List<TermoGlossario> listTermo = new ArrayList<>();

		TermoGlossario t;
		for (Object obj : nativeQuery.getResultList()) {
			Object[] item = (Object[]) obj;

			t = new TermoGlossario();
			t.setId		  ( Integer.valueOf (item[0].toString()));
			t.setNome	  ( String .valueOf (item[1]));
			t.setDescricao( String .valueOf (item[2]));
			t.setSaibaMais( String .valueOf (item[3]));

			listTermo.add(t);
		}

		return listTermo;
	}

	public Integer recuperarTotalDePaginas(TermoGlossario termo) throws AtlasAcessoBancoDadosException {
		final double REGISTERS_PER_PAGE = 45;

		StringBuilder query = new StringBuilder();
		query.append("SELECT DISTINCT COALESCE(COUNT(TOTAL),0) AS TOTAL ");
		query.append("FROM ");
		query.append("(SELECT id_termo, nme_termo, dsc_termo, dsc_saiba_mais , count(*) over (partition by 1) TOTAL ");
		query.append(" FROM	  atlas.termo_glossario ");

		if(termo!=null && termo.getNome()!=null){
			query.append("WHERE atlas.retirar_acento(nme_termo) like UPPER(:termoPesquisado) ");
		}
		query.append("GROUP BY id_termo, nme_termo, dsc_termo, dsc_saiba_mais ");
		query.append("ORDER BY nme_termo) ");
		query.append("AS TOTAL_ROWS ");

		AtlasQuery nativeQuery = createNativeQuery(query.toString());

		if(termo!=null && termo.getNome()!=null){
			nativeQuery.setParameter("termoPesquisado", termo.getNome().toUpperCase());
		}

		BigInteger contagem = (BigInteger) nativeQuery.getResultList().get(0);

		double  totalOfPagesDouble  = contagem.doubleValue() / REGISTERS_PER_PAGE;
		Integer totalOfPagesInteger = (int) Math.ceil(totalOfPagesDouble);

		return totalOfPagesInteger;
	}

	public Integer recuperarTotalDePaginas(String termoPesquisa) throws AtlasAcessoBancoDadosException {
		final double REGISTERS_PER_PAGE = 45;

		StringBuilder query = new StringBuilder();
		query.append("SELECT DISTINCT COALESCE(COUNT(TOTAL),0) AS TOTAL ");
		query.append("FROM ");
		query.append("(SELECT id_termo, nme_termo, dsc_termo, dsc_saiba_mais , count(*) over (partition by 1) TOTAL ");
		query.append(" FROM	  atlas.termo_glossario ");

		if(termoPesquisa != null && termoPesquisa.trim()!=""){
			query.append("WHERE atlas.retirar_acento(nme_termo) like UPPER(:text) ");
		}
		query.append("GROUP BY id_termo, nme_termo, dsc_termo, dsc_saiba_mais ");
		query.append("ORDER BY nme_termo) ");
		query.append("AS TOTAL_ROWS ");

		AtlasQuery nativeQuery = createNativeQuery(query.toString());
		if(termoPesquisa != null && termoPesquisa.trim()!=""){
			nativeQuery.setParameter("text", termoPesquisa+"%");
		}
		BigInteger contagem = (BigInteger) nativeQuery.getResultList().get(0);

		double  totalOfPagesDouble  = contagem.doubleValue() / REGISTERS_PER_PAGE;
		Integer totalOfPagesInteger = (int) Math.ceil(totalOfPagesDouble);

		return totalOfPagesInteger;
	}

	@SuppressWarnings("unchecked")
	public List<FonteReferencia> recuperarFontesReferencia(TermoGlossario termo) throws AtlasAcessoBancoDadosException {
		StringBuilder query = new StringBuilder();
		query.append(SQL_SELECIONAR_FONTES);
		AtlasQuery atlasQuery = createQuery(query.toString());
		atlasQuery.setParameter("termo", termo);
		return atlasQuery.getResultList();
	}

	/**
	 * Recupera a lista de fontes do termo.
	 * @param termo
	 * @return
	 * @throws AtlasAcessoBancoDadosException
	 */
	public ListaPaginada<FonteReferencia> recuperarFontesReferenciaPaginada(ArrayList<TermoGlossario> termos) throws AtlasAcessoBancoDadosException {
		ConsultaPaginada consulta = new ConsultaPaginada(0);
		StringBuilder query = new StringBuilder();
		query.append(SQL_SELECIONAR_FONTES_SUGESTOES);

		if (termos != null && termos.size() > 0) {
			query.append(" where id_termo in ( ");
			for (TermoGlossario termoGlossario : termos) {
				query.append(termoGlossario.getId() + ",");
			}
			query.delete(query.length() - 1, query.length()); // DELETA A ULTIMA ","
			query.append(" ) ");
		} else {
			query.append(" where id_termo in (0) ");
		}

		consulta.setQueryString(query.toString());

		@SuppressWarnings("unchecked")
		ListaPaginada<FonteReferencia> lista = efetuarPesquisaPaginada(consulta);

		FonteReferencia fonte = new FonteReferencia();

		for (Object obj : lista.getResultadoPesquisa()) {
			Object[] item = (Object[]) obj;

			fonte = new FonteReferencia();
			fonte.setId	  			(Integer.valueOf (item[0].toString()));
			fonte.setTermoGlossario (pesquisarPorId(Integer.valueOf(item[1].toString()),TermoGlossario.class));
			fonte.setNome			( String.valueOf (item[2]));
			fonte.setURL			( String.valueOf (item[3]));
			fonte.setLegislacao		(String .valueOf (item[4]));

			lista.getResultadoRetorno().add(fonte);
		}

		return lista;
	}



}
