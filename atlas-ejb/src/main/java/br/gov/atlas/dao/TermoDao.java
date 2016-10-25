package br.gov.atlas.dao;

import java.util.Date;

import javax.persistence.EntityManager;

import br.gov.atlas.dao.arquitetura.AtlasQuery;
import br.gov.atlas.dto.TermoBuscaDTO;
import br.gov.atlas.entidade.Termo;
import br.gov.atlas.entity.ConsultaPaginada;
import br.gov.atlas.entity.ListaPaginada;
import br.gov.atlas.exception.AtlasAcessoBancoDadosException;

public class TermoDao extends AtlasDao<Termo> {

	public TermoDao(EntityManager em) {
		super(em);
	}

	@SuppressWarnings("unchecked")
	public ListaPaginada<TermoBuscaDTO> recuperarTermos(Termo termo) throws AtlasAcessoBancoDadosException {
		ConsultaPaginada consultaPaginada = new ConsultaPaginada(termo.getPrimeiroRegistro());
		StringBuilder query = new StringBuilder();
		query.append(" SELECT ");
		query.append(" 		termo.nme_termo_busca, ");
		query.append(" 		SUM(termo.qtd_termo_busca) AS soma ");
		query.append(" FROM ");
		query.append(" 		atlas.termo_busca AS termo ");
		if (termo != null && termo.getNome() != null) {
			query.append(" WHERE ");
			query.append(" 		UPPER(termo.nme_termo_busca) LIKE :nome ");
			consultaPaginada.getParametros().put("nome", termo.getNome().toUpperCase());
		}
		query.append(" GROUP BY ");
		query.append(" 		termo.nme_termo_busca ");
		query.append(" ORDER BY ");
		query.append(" 		termo.nme_termo_busca ");

		consultaPaginada.setQueryString(query.toString());

		ListaPaginada<TermoBuscaDTO> lista = efetuarPesquisaPaginada(consultaPaginada);

		TermoBuscaDTO termoConsulta = null;

		for (Object obj : lista.getResultadoPesquisa()) {
			Object[] item = (Object[]) obj;

			termoConsulta = new TermoBuscaDTO();
			termoConsulta.setNmeTermoBusca(String.valueOf(item[0]));
			termoConsulta.setQtdTotal(Integer.valueOf(item[1].toString()));
			lista.getResultadoRetorno().add(termoConsulta);
		}

		return lista;
	}

	public Termo recuperarUltimoRegistroAtualizadoPorNome(String nomeTermo) throws AtlasAcessoBancoDadosException {
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" 		t ");
		sb.append(" FROM ");
		sb.append(" 		Termo t ");
		sb.append(" WHERE ");
		sb.append(" 		UPPER(t.nome) LIKE :nome ");
		sb.append(" ORDER BY ");
		sb.append(" 		t.dataAtualizacao ");

		AtlasQuery query = createQuery(sb.toString());
		query.setParameter("nome", nomeTermo.toUpperCase());
		query.setMaxResults(1);
		return (Termo) query.getSingleResult();
	}

	public int alterarDataBloqueioPorNomeTermo(String nomeTermo, Date dataBloqueio) throws AtlasAcessoBancoDadosException {
		StringBuilder sb = new StringBuilder();
		sb.append(" UPDATE ");
		sb.append(" 		atlas.termo_busca ");
		sb.append(" SET ");
		// Solução alternativa, JPA não aceita bulk updates quando está setando valor nulo
		if(dataBloqueio == null){
			sb.append(" 		dta_bloqueio = NULL ");
		} else {
			sb.append(" 		dta_bloqueio = :dataBloqueio ");
		}
		sb.append(" WHERE ");
		sb.append(" 		nme_termo_busca = :nomeTermo ");

		AtlasQuery query = createNativeQuery(sb.toString());
		query.setParameter("nomeTermo", nomeTermo);
		if(dataBloqueio != null){
			query.setParameter("dataBloqueio", dataBloqueio);
		}
		int updateCount = query.executeUpdate();
		return updateCount;

	}
}