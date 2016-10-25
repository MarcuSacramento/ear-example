package br.gov.atlas.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import br.gov.atlas.dao.arquitetura.AtlasQuery;
import br.gov.atlas.entidade.Ramo;
import br.gov.atlas.entidade.Servico;
import br.gov.atlas.entidade.TipoOrgao;
import br.gov.atlas.entity.ConsultaPaginada;
import br.gov.atlas.entity.ListaPaginada;
import br.gov.atlas.enums.IndicadorMapa;
import br.gov.atlas.exception.AtlasAcessoBancoDadosException;

public class TipoOrgaoDao extends AtlasDao<Ramo> {

	public TipoOrgaoDao(EntityManager em) {
		super(em);
	}

	@SuppressWarnings("unchecked")
	public ListaPaginada<TipoOrgao> recuperarTiposOrgao(TipoOrgao tipoOrgao)
			throws AtlasAcessoBancoDadosException {
		ConsultaPaginada consultaPaginada = new ConsultaPaginada(
				tipoOrgao.getPrimeiroRegistro());
		StringBuilder query = new StringBuilder();
		query.append("select to from TipoOrgao to ");

		if ((tipoOrgao != null) && (tipoOrgao.getNome() != null)) {
			query.append("where upper(to.nome) like :nome ");
			consultaPaginada.getParametros().put("nome",
					tipoOrgao.getNome().toUpperCase());
		}
		query.append("order by to.nome ");
		consultaPaginada.setHqlString(query.toString());

		ListaPaginada<TipoOrgao> lista = efetuarPesquisaPaginada(consultaPaginada);
		for (Object object : lista.getResultadoPesquisa()) {
			lista.getResultadoRetorno().add((TipoOrgao) object);
		}

		return lista;
	}

	public int contarPorRamo(Ramo ramo) throws AtlasAcessoBancoDadosException {
		StringBuilder query = new StringBuilder();
		query.append("select count(to.id) from TipoOrgao to where to.ramo = :ramo");
		AtlasQuery atlasQuery = createQuery(query.toString());
		atlasQuery.setParameter("ramo", ramo);
		return ((Long) atlasQuery.getSingleResult()).intValue();
	}

	public TipoOrgao recuperarPorNome(String nome)
			throws AtlasAcessoBancoDadosException {
		StringBuilder query = new StringBuilder();
		query.append("select to from TipoOrgao to where upper(to.nome) = :nome");
		AtlasQuery atlasQuery = createQuery(query.toString());
		atlasQuery.setParameter("nome", nome.toUpperCase());
		return ((TipoOrgao) atlasQuery.getSingleResult());

	}


	@SuppressWarnings("unchecked")
	public List<TipoOrgao> recuperarTiposOrgaoValidos() throws AtlasAcessoBancoDadosException {
		StringBuilder query = new StringBuilder();
		query.append("select tipoOrgao from TipoOrgao tipoOrgao where size(tipoOrgao.orgaos)<>0 order by tipoOrgao.nome");
		AtlasQuery atlasQuery = createQuery(query.toString());
		return atlasQuery.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<TipoOrgao> recuperarTiposOrgaoRelacionadosEnteIndicadorMapa(int idTipoRamo, IndicadorMapa indicadorMapa)
			throws AtlasAcessoBancoDadosException {
		StringBuilder query = new StringBuilder();
		query.append(" SELECT ");
		query.append(" 		tipo.id_tipo_orgao id, ");
		query.append(" 		tipo.nme_tipo_orgao nome ");
		query.append(" FROM ");
		query.append(" 		atlas.tipo_orgao tipo ");
		query.append(" 		INNER JOIN atlas.ramo ramo ON(tipo.id_ramo = ramo.id_ramo AND ramo.id_ramo = :idTipoRamo) ");
		query.append(" WHERE ");
		query.append(" 		tipo.id_tipo_orgao IN( ");
		query.append(" 			SELECT ");
		query.append(" 				DISTINCT(orgao.id_tipo_orgao) ");
		query.append(" 			FROM ");

		query.append(" 				atlas.orgao orgao ");
		query.append(" 				INNER JOIN atlas.tema_orgao temaOrgao ON(orgao.id_orgao = temaOrgao.id_orgao) ");
		query.append(" 				INNER JOIN atlas.tema tema ON(tema.id_tema = temaOrgao.id_tema AND tema.mapa = :indicadorMapa) ");
		query.append(" 		) ");
		query.append(" ORDER BY ");
		query.append(" 		nome ");

		AtlasQuery atlasQuery = createNativeQuery(query.toString());
		atlasQuery.setParameter("idTipoRamo", idTipoRamo);
		atlasQuery.setParameter("indicadorMapa", indicadorMapa.getValor());

		List<Object[]> resultado = atlasQuery.getResultList();
		List<TipoOrgao> lista = new ArrayList<>();
		for (Object[] r : resultado) {
			lista.add(new TipoOrgao((Integer) r[0], (String) r[1]));
		}

		return lista;
	}

	@SuppressWarnings("unchecked")
	public List<TipoOrgao> recuperarTiposOrgaoPorRamo(int idTipoRamo)
			throws AtlasAcessoBancoDadosException {
		StringBuilder query = new StringBuilder();
		query.append(" SELECT ");
		query.append(" 		tipo.id_tipo_orgao id, ");
		query.append(" 		tipo.nme_tipo_orgao nome ");
		query.append(" FROM ");
		query.append(" 		atlas.tipo_orgao tipo ");
		query.append(" 		INNER JOIN atlas.ramo ramo ON(tipo.id_ramo = ramo.id_ramo AND ramo.id_ramo = :idTipoRamo) ");
		query.append(" ORDER BY ");
		query.append(" 		nome ");

		AtlasQuery atlasQuery = createNativeQuery(query.toString());
		atlasQuery.setParameter("idTipoRamo", idTipoRamo);
		List<Object[]> resultado = atlasQuery.getResultList();
		List<TipoOrgao> lista = new ArrayList<>();
		for (Object[] r : resultado) {
			lista.add(new TipoOrgao((Integer) r[0], (String) r[1]));
		}

		return lista;
	}

	@SuppressWarnings("unchecked")
	public List<Servico> recuperarServicosDisponiveisSelecao(TipoOrgao tipoOrgao, String nomeServico)
			throws AtlasAcessoBancoDadosException {
		boolean isServicosPreenchidos = tipoOrgao != null && tipoOrgao.getServicos() != null
				&& !tipoOrgao.getServicos().isEmpty();

		StringBuilder query = new StringBuilder();
		query.append(" SELECT ");
		query.append(" 		servico ");
		query.append(" FROM ");
		query.append(" 		Servico servico ");
		query.append(" WHERE 1 = 1");
		if (isServicosPreenchidos) {
			query.append(" 		AND :tipoOrgao not in elements(servico.tiposOrgao) ");
			query.append(" 		AND servico not in :servicos ");
		}

		if (nomeServico != null && !nomeServico.trim().isEmpty()) {
			query.append(" 		AND UPPER(servico.nome) LIKE UPPER(:nomeServico) ");
		}
		query.append(" ORDER BY servico.nome ");
		AtlasQuery atlasQuery = createQuery(query.toString());
		if (isServicosPreenchidos) {
			atlasQuery.setParameter("tipoOrgao", tipoOrgao);
			atlasQuery.setParameter("servicos", tipoOrgao.getServicos());
		}
		if (nomeServico != null && !nomeServico.trim().isEmpty()) {
			atlasQuery.setParameter("nomeServico", "%".concat(nomeServico).concat("%"));
		}
		return atlasQuery.getResultList();
	}
}