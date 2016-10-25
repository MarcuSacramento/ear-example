package br.gov.atlas.dao;

import static br.gov.atlas.enums.TipoTelefone.TELEFONE_FIXO;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import br.gov.atlas.dao.arquitetura.AtlasQuery;
import br.gov.atlas.dto.DadosEmBlocoDTO;
import br.gov.atlas.dto.EnderecoDTO;
import br.gov.atlas.dto.OrgaoDTO;
import br.gov.atlas.dto.TotalizadorOrgaoDTO;
import br.gov.atlas.dto.TotalizadorOrgaoDTO.NivelTotalizadorOrgao;
import br.gov.atlas.entidade.EmailUsuario;
import br.gov.atlas.entidade.Municipio;
import br.gov.atlas.entidade.Orgao;
import br.gov.atlas.entidade.Problema;
import br.gov.atlas.entidade.Ramo;
import br.gov.atlas.entidade.Representante;
import br.gov.atlas.entidade.Servico;
import br.gov.atlas.entidade.Telefone;
import br.gov.atlas.entidade.Tema;
import br.gov.atlas.entidade.TipoOrgao;
import br.gov.atlas.entidade.UF;
import br.gov.atlas.entidade.Usuario;
import br.gov.atlas.entity.ConsultaPaginada;
import br.gov.atlas.entity.ListaPaginada;
import br.gov.atlas.enums.IndicadorMapa;
import br.gov.atlas.enums.TipoSituacaoRegistroOrgao;
import br.gov.atlas.exception.AtlasAcessoBancoDadosException;

public class OrgaoDao extends AtlasDao<Orgao> {

	public OrgaoDao(EntityManager em) {
		super(em);
	}

	@SuppressWarnings("unchecked")
	public List<Orgao> buscarTodos(int offset, int tamanhoLote)
			throws AtlasAcessoBancoDadosException {
		return createQuery(
				"select a from " + Orgao.class.getName() + " a where id>="
						+ offset + " order by id").setMaxResults(tamanhoLote)
				.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<Orgao> recuperarOrgaosSemLatitude()
			throws AtlasAcessoBancoDadosException {
		return createQuery(
				"select a from " + Orgao.class.getName()
				+ " a where latitude is null order by id")
				.getResultList();
	}

	public int getIdProximoRegistroAPreencher()
			throws AtlasAcessoBancoDadosException {
		try {
			return (Integer) createNativeQuery(
					"select max(id_orgao) from atlas.orgao where num_latitude is not null and num_longitude is not null")
					.getSingleResult();
		} catch (NullPointerException e) {
			return 0;
		}
	}

	@SuppressWarnings("unchecked")
	public List<Telefone> recuperarTelefones(Orgao orgao)
			throws AtlasAcessoBancoDadosException {
		StringBuilder query = new StringBuilder();
		query.append("select telefones from Orgao orgao where orgao = :orgao ");
		AtlasQuery atlasQuery = createQuery(query.toString());
		atlasQuery.setParameter("orgao", orgao);
		return atlasQuery.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<Problema> recuperarProblemas(Orgao orgao)
			throws AtlasAcessoBancoDadosException {
		StringBuilder query = new StringBuilder();
		query.append("select problemas from Orgao orgao where orgao = :orgao");
		AtlasQuery atlasQuery = createQuery(query.toString());
		atlasQuery.setParameter("orgao", orgao);
		return atlasQuery.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<EmailUsuario> recuperarEmails(Orgao orgao)
			throws AtlasAcessoBancoDadosException {
		StringBuilder query = new StringBuilder();
		query.append("select emails from Orgao orgao where orgao = :orgao");
		AtlasQuery atlasQuery = createQuery(query.toString());
		atlasQuery.setParameter("orgao", orgao);
		return atlasQuery.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<TipoOrgao> recuperarTiposOrgao(Ramo ramo)
			throws AtlasAcessoBancoDadosException {
		StringBuilder query = new StringBuilder();
		query.append("select tipoOrgao from TipoOrgao tipoOrgao where ramo = :ramo");
		AtlasQuery atlasQuery = createQuery(query.toString());
		atlasQuery.setParameter("ramo", ramo);
		return atlasQuery.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<Tema> recuperarTemas(Orgao orgao)
			throws AtlasAcessoBancoDadosException {
		StringBuilder query = new StringBuilder();
		query.append("select tema from Tema tema where :orgao in elements(tema.orgaos)");
		AtlasQuery atlasQuery = createQuery(query.toString());
		atlasQuery.setParameter("orgao", orgao);
		return atlasQuery.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<Servico> recuperarServicos(Orgao orgao)
			throws AtlasAcessoBancoDadosException {
		StringBuilder query = new StringBuilder();
		query.append("select servico from Servico servico where :orgao in elements(servico.orgaos)");
		AtlasQuery atlasQuery = createQuery(query.toString());
		atlasQuery.setParameter("orgao", orgao);
		return atlasQuery.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<Servico> recuperarServicosDisponiveisSelecao(Orgao orgao, String nomeServico)
			throws AtlasAcessoBancoDadosException {

		boolean isServicosPreenchidos = orgao != null && orgao.getServicos() != null && !orgao.getServicos().isEmpty();

		StringBuilder query = new StringBuilder();
		query.append(" SELECT ");
		query.append(" 		servico ");
		query.append(" FROM ");
		query.append(" 		Servico servico ");
		query.append(" WHERE 1=1 ");

		if (isServicosPreenchidos) {
			query.append(" 		AND :orgao not in elements(servico.orgaos) ");
			query.append(" 		AND servico not in :servicos ");
		}

		if (nomeServico != null && !nomeServico.trim().isEmpty()) {
			query.append(" 		AND UPPER(servico.nome) LIKE UPPER(:nomeServico) ");
		}
		AtlasQuery atlasQuery = createQuery(query.toString());

		if (isServicosPreenchidos) {
			atlasQuery.setParameter("orgao", orgao);
			atlasQuery.setParameter("servicos", orgao.getServicos());
		}

		if (nomeServico != null && !nomeServico.trim().isEmpty()) {
			atlasQuery.setParameter("nomeServico", "%".concat(nomeServico).concat("%"));
		}
		return atlasQuery.getResultList();
	}

	/**
	 * Recupera os órgãos onde a data de atualização é nula limitada a
	 * quantidade de registros informado no parâmetro tamanhoLote.
	 *
	 * @param municipio
	 * @param tiposOrgao
	 * @param tamanhoLote
	 * @return
	 * @throws AtlasAcessoBancoDadosException
	 */
	public List<Orgao> recuperarOrgaosSemAtualizacao(Municipio municipio,
			List<TipoOrgao> tiposOrgao, int tamanhoLote)
					throws AtlasAcessoBancoDadosException {
		Integer idMunicipio = municipio.getId();
		String strListIdTipoOrgao = strListIdTipoOrgao(tiposOrgao);

		StringBuilder sql = new StringBuilder();
		sql.append("select ");
		sql.append("   id_orgao ");
		sql.append("from ");
		sql.append("  atlas.orgao ");
		sql.append("where ");
		sql.append("  orgao.id_municipio = ").append(idMunicipio);
		sql.append("  and ");
		sql.append("  orgao.id_tipo_orgao in (").append(strListIdTipoOrgao)
		.append(") ");
		sql.append("  and ");
		sql.append("  orgao.ind_situacao = '2' ");
		sql.append("  and ");
		sql.append("  orgao.dta_atualizacao is null ");
		sql.append("order by ");
		sql.append("  orgao.nme_orgao ");

		AtlasQuery atlasQuery = createNativeQuery(sql.toString());
		List<Orgao> lista = new ArrayList<>();

		for (Object obj : atlasQuery.setMaxResults(tamanhoLote).getResultList()) {
			Orgao orgao = pesquisarPorId((Integer) obj, Orgao.class);
			lista.add(orgao);
		}
		return lista;
	}

	/**
	 * Recupera os órgãos onde a data de atualização é nula limitada a
	 * quantidade de registros informado no parâmetro tamanhoLote.
	 *
	 * @param uf
	 * @param tiposOrgao
	 * @param qtdMaximaRegistros
	 * @return
	 * @throws AtlasAcessoBancoDadosException
	 */
	public List<Orgao> recuperarOrgaosSemAtualizacao(UF uf,
			List<TipoOrgao> tiposOrgao, int qtdMaximaRegistros)
					throws AtlasAcessoBancoDadosException {
		String strListIdTipoOrgao = strListIdTipoOrgao(tiposOrgao);

		StringBuilder sql = new StringBuilder();
		sql.append("SELECT 	ID_ORGAO ");
		sql.append("FROM	ATLAS.ORGAO ");
		sql.append("WHERE	ID_MUNICIPIO IN (	SELECT 	MUN.ID_MUNICIPIO ");
		sql.append("							FROM 	MUNICIPIO MUN ");
		sql.append("							JOIN	UF ON MUN.ID_UF = UF.ID_UF ");
		sql.append("							WHERE	COD_UF = '" + uf.getSigla() + "') ");
		sql.append("AND	IND_SITUACAO = '2' ");
		sql.append("AND	DTA_ATUALIZACAO IS NULL ");
		sql.append("AND	ID_TIPO_ORGAO IN (" + strListIdTipoOrgao + ") ");
		sql.append("ORDER BY NME_ORGAO ");

		AtlasQuery atlasQuery = createNativeQuery(sql.toString());
		List<Orgao> lista = new ArrayList<>();

		for (Object obj : atlasQuery.setMaxResults(qtdMaximaRegistros)
				.getResultList()) {
			Orgao orgao = pesquisarPorId((Integer) obj, Orgao.class);
			lista.add(orgao);
		}
		return lista;
	}

	@SuppressWarnings("unchecked")
	public List<Orgao> recuperarLonLatOrgaos(OrgaoDTO orgaoFiltro)	throws AtlasAcessoBancoDadosException {
		StringBuilder hql = new StringBuilder();
		hql.append(" SELECT ");
		hql.append("	o.id_orgao,  ");
		hql.append("	o.nme_orgao,  ");
		hql.append("	o.num_latitude,  ");
		hql.append("	o.num_longitude ");
		hql.append("FROM atlas.orgao o  ");
		hql.append("JOIN atlas.tipo_orgao t ON o.id_tipo_orgao = t.id_tipo_orgao ");
		if(orgaoFiltro.getTipoOrgao()!=null){
			hql.append(" AND t.id_tipo_orgao = "+orgaoFiltro.getTipoOrgao().getId());
		}
		hql.append("JOIN atlas.ramo r ON t.id_ramo = r.id_ramo ");
		if(orgaoFiltro.getRamo()!=null){
			hql.append(" AND r.id_ramo = "+orgaoFiltro.getRamo().getId());
		}
		hql.append("JOIN atlas.tema_orgao tmo ON o.id_orgao = tmo.id_orgao  ");
		if(orgaoFiltro.getTema()!=null){
			hql.append(" AND tmo.id_tema = "+orgaoFiltro.getTema().getId());
		}

		AtlasQuery atlasQuery = createNativeQuery(hql.toString());
		List<Object[]> resultado = atlasQuery.getResultList();
		List<Orgao> lista = new ArrayList<>();

		Integer idOrgao = null;
		String nomeOrgao = null;
		Double latitude = null;
		Double longitude = null;
		for (Object[] obj : resultado) {
			idOrgao = (Integer) obj[0];
			nomeOrgao = (String) obj[1];
			latitude = (Double) obj[2];
			longitude = (Double) obj[3];

			Orgao orgao = new Orgao();
			orgao.setId(idOrgao);
			orgao.setNome(nomeOrgao);
			if (latitude != null) {
				orgao.setLatitude(BigDecimal.valueOf(latitude));
			}
			if (longitude != null) {
				orgao.setLongitude(BigDecimal.valueOf(longitude));
			}
			lista.add(orgao);
		}

		return lista;
	}

	@SuppressWarnings("unchecked")
	public List<Orgao> recuperarOrgaosPorTema(Municipio municipio,
			List<Tema> temasSelecionados) throws AtlasAcessoBancoDadosException {

		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ");
		sql.append(" 	orgao ");
		sql.append(" FROM ");
		sql.append(" 	Tema tema ");
		sql.append(" 	JOIN tema.orgaos orgao ");
		sql.append(" 	JOIN orgao.tipoOrgao tipo ");
		sql.append(" 	JOIN orgao.municipio municipio ");
		sql.append(" 	JOIN municipio.uf uf ");
		sql.append(" WHERE ");
		sql.append(" 	orgao.indicadorSituacao = :indicadorSituacao ");
		sql.append(" 	AND municipio.id = :idMunicipio ");
		sql.append(" 	AND tema IN (:listaTemas) ");
		sql.append(" ORDER BY ");
		sql.append(" 	uf.nome, municipio.nome, tipo.nome, orgao.nome ");

		AtlasQuery atlasQuery = createQuery(sql.toString());
		atlasQuery.setParameter("indicadorSituacao", TipoSituacaoRegistroOrgao.DISPONIVEL.indiceSituacao());
		atlasQuery.setParameter("idMunicipio", municipio.getId());
		atlasQuery.setParameter("listaTemas", temasSelecionados);
		List<Orgao> lista = atlasQuery.getResultList();
		return lista;
	}

	public List<Orgao> recuperarOrgaosPorServico(Municipio municipio,
			List<Servico> servicosSelecionados) throws AtlasAcessoBancoDadosException {
		Integer idMunicipio = municipio.getId();
		String strListIdServico = strListIdServico(servicosSelecionados);

		StringBuilder sql = new StringBuilder();
		sql.append("select 	distinct orgao.id_orgao ");
		sql.append("from 	atlas.servico_orgao ");
		sql.append("join	atlas.orgao using (id_orgao) ");
		sql.append("where 	id_servico in 		(").append(strListIdServico).append(") ");
		sql.append("and 	orgao.ind_situacao = '2' ");
		sql.append("and 	orgao.id_municipio = ").append(idMunicipio);

		AtlasQuery atlasQuery = createNativeQuery(sql.toString());
		List<Orgao> lista = new ArrayList<>();

		for (Object obj : atlasQuery.getResultList()) {
			Orgao orgao = pesquisarPorId((Integer) obj, Orgao.class);
			lista.add(orgao);
		}
		return lista;
	}

	@SuppressWarnings("unchecked")
	public List<Orgao> recuperarOrgaos(Municipio municipio, List<TipoOrgao> listaTipos)
			throws AtlasAcessoBancoDadosException {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ");
		sql.append(" 	orgao ");
		sql.append(" FROM ");
		sql.append(" 	Orgao orgao ");
		sql.append(" 	JOIN orgao.tipoOrgao tipo ");
		sql.append(" 	JOIN orgao.municipio municipio ");
		sql.append(" 	JOIN municipio.uf uf ");
		sql.append(" WHERE ");
		sql.append(" 	orgao.indicadorSituacao = :indicadorSituacao ");
		sql.append(" 	AND municipio.id = :idMunicipio ");
		sql.append(" 	AND tipo IN (:listaTipos) ");
		sql.append(" ORDER BY ");
		sql.append(" 	uf.nome, municipio.nome, tipo.nome, orgao.nome ");

		AtlasQuery atlasQuery = createQuery(sql.toString());
		atlasQuery.setParameter("indicadorSituacao", TipoSituacaoRegistroOrgao.DISPONIVEL.indiceSituacao());
		atlasQuery.setParameter("idMunicipio", municipio.getId());
		atlasQuery.setParameter("listaTipos", listaTipos);
		List<Orgao> lista = atlasQuery.getResultList();
		return lista;
	}

	private String strListIdTipoOrgao(List<TipoOrgao> list) {
		String strListIdTipoOrgao = "";
		for (TipoOrgao tipoOrgao : list) {
			strListIdTipoOrgao += tipoOrgao.getId() + ",";
		}
		strListIdTipoOrgao += "0";
		return strListIdTipoOrgao;
	}

	private String strListIdOrgao(List<Orgao> list) {
		String strListIdOrgao = "";
		for (Orgao orgao : list) {
			strListIdOrgao += orgao.getId() + ",";
		}
		strListIdOrgao += "0";
		return strListIdOrgao;
	}

	private String strListIdServico(List<Servico> list) {
		String strListIdServico = "";
		for (Servico servico : list) {
			strListIdServico += servico.getId() + ",";
		}
		strListIdServico += "0";
		return strListIdServico;
	}


	public int possuiOrgaos(TipoOrgao tipoOrgao)
			throws AtlasAcessoBancoDadosException {
		StringBuilder query = new StringBuilder();
		query.append("select size(tipoOrgao.orgaos) from TipoOrgao tipoOrgao where tipoOrgao = :tipoOrgao");
		AtlasQuery atlasQuery = createQuery(query.toString());
		atlasQuery.setParameter("tipoOrgao", tipoOrgao);
		return (int) atlasQuery.getSingleResult();
	}


	public List<Orgao> recuperarDadosOgaos(OrgaoDTO orgaoFiltro) throws AtlasAcessoBancoDadosException {
		List<Orgao> lista = new ArrayList<>();
		String query = recuperarQueryComSuperiores(orgaoFiltro);
		AtlasQuery atlasQuery = createNativeQuery(query.toString());
		for (Object obj : atlasQuery.getResultList()) {
			Object[] array = (Object[]) obj;

			Integer idOrgao = (Integer) array[0];
			String nomeOrgao = String.valueOf(array[1]);
			Integer idSuperior = (Integer) array[2];
			String nomeTipoOrgao = String.valueOf(array[3]);
			String codUFOrgao = String.valueOf(array[4]);
			String nomeMunicipioOrgao = String.valueOf(array[5]);

			TipoOrgao tipoOrgao = new TipoOrgao();
			tipoOrgao.setNome(nomeTipoOrgao);

			UF uf = new UF();
			uf.setSigla(codUFOrgao);

			Municipio municipio = new Municipio();
			municipio.setNome(nomeMunicipioOrgao);
			municipio.setUf(uf);

			Orgao orgao = new Orgao();
			orgao.setId(idOrgao);
			orgao.setNome(nomeOrgao);
			orgao.setTipoOrgao(tipoOrgao);
			orgao.setMunicipio(municipio);

			if(idSuperior != null){
				Orgao orgaoSuperior = new Orgao();
				orgaoSuperior.setId(idSuperior);
				orgao.setOrgaoSuperior(orgaoSuperior);
			}

			lista.add(orgao);
		}

		return lista;
	}

	@SuppressWarnings("unchecked")
	public ListaPaginada<Orgao> recuperarOrgaos(OrgaoDTO orgaoFiltro,
			String sortField, String sortOrder,
			ListaPaginada<Orgao> listaPaginada)
					throws AtlasAcessoBancoDadosException {

		String query = recuperarQuery(orgaoFiltro, sortField, sortOrder);
		listaPaginada.setPrimeiroRegistro(orgaoFiltro.getPrimeiroRegistro());
		listaPaginada.setQtdRegistros(qtdRegistros(query));
		orgaoFiltro.setQtdRegistros(listaPaginada.getQtdRegistros());

		AtlasQuery atlasQuery = createNativeQuery(query + " LIMIT "
				+ listaPaginada.getQtdRegistroPorPagina() + " OFFSET "
				+ orgaoFiltro.getPrimeiroRegistro());

		listaPaginada.setResultadoPesquisa(atlasQuery.getResultList());

		Orgao orgao;
		for (Object obj : listaPaginada.getResultadoPesquisa()) {
			orgao = pesquisarPorId((Integer) obj, Orgao.class);
			orgao.setProblemas(recuperarProblemas(orgao));
			listaPaginada.getResultadoRetorno().add(orgao);
		}

		return listaPaginada;
	}

	private int qtdRegistros(String query)
			throws AtlasAcessoBancoDadosException {
		AtlasQuery atlasQuery = createNativeQuery(query);
		return atlasQuery.getResultList().size();
	}

	private boolean isSituacaoRegistroEntreOsValoresAceitos(OrgaoDTO orgaoFiltro) {
		String valorFornecido = orgaoFiltro.getOrgaosSituacaoRegistro();
		TipoSituacaoRegistroOrgao situacaoRegistro = TipoSituacaoRegistroOrgao.create(valorFornecido);
		if(situacaoRegistro != null){
			return true;
		}
		return false;
	}

	public List<Orgao> recuperarOrgaos(OrgaoDTO orgaoFiltro)
			throws AtlasAcessoBancoDadosException {
		List<Orgao> lista = new ArrayList<>();

		AtlasQuery atlasQuery = createNativeQuery(recuperarQuery(orgaoFiltro,
				null, null).toString());

		for (Object obj : atlasQuery.getResultList()) {
			Orgao orgao = pesquisarPorId((Integer) obj, Orgao.class);
			// orgao.setProblemas(recuperarProblemas(orgao));
			lista.add(orgao);
		}

		return lista;
	}

	@SuppressWarnings("unchecked")
	public ListaPaginada<Orgao> recuperarOrgaosPaginados(OrgaoDTO orgaoFiltro,
			String sortField, String sortOrder)
					throws AtlasAcessoBancoDadosException {
		ConsultaPaginada consultaPaginada = new ConsultaPaginada(
				orgaoFiltro.getPrimeiroRegistro());
		consultaPaginada.setQueryString(recuperarQuery(orgaoFiltro, sortField,
				sortOrder).toString());
		ListaPaginada<Orgao> lista = efetuarPesquisaPaginada(consultaPaginada);
		Orgao orgao;

		for (Object obj : lista.getResultadoPesquisa()) {
			orgao = pesquisarPorId((Integer) obj, Orgao.class);
			orgao.setProblemas(recuperarProblemas(orgao));
			lista.getResultadoRetorno().add(orgao);
		}

		return lista;
	}

	private String recuperarQueryComSuperiores(OrgaoDTO orgaoFiltro) {
		StringBuilder query = new StringBuilder();
		query.append("SELECT orgao.id_orgao, orgao.nme_orgao, orgao.id_orgao_superior as id_orgao_superior,");
		query.append("		 tipoOrgao.nme_tipo_orgao, uf.cod_uf, municipio.nme_municipio ");
		query.append(" FROM atlas.orgao orgao ");
		query.append("INNER JOIN atlas.tipo_orgao tipoOrgao on orgao.id_tipo_orgao = tipoOrgao.id_tipo_orgao ");
		query.append("INNER JOIN atlas.municipio municipio on orgao.id_municipio = municipio.id_municipio ");
		query.append("INNER JOIN atlas.uf uf on municipio.id_uf = uf.id_uf ");
		if (orgaoFiltro.getUf() != null) {
			query.append(" and uf.id_uf = ")
			.append(orgaoFiltro.getUf().getId());
		}
		query.append("LEFT JOIN atlas.tema_orgao temaOrgao on orgao.id_orgao = temaOrgao.id_orgao ");

		query.append("LEFT JOIN atlas.servico_orgao servicoOrgao on orgao.id_orgao = servicoOrgao.id_orgao ");

		StringBuilder where = aplicarFiltros(orgaoFiltro);

		query.append(where);

		aplicarOrdenacaoDados(null, null, query);

		return query.toString();
	}


	private void aplicarOrdenacaoDados(String sortField, String sortOrder,
			StringBuilder query) {
		if (sortField == null || sortField.equals("nome")) {
			query.append(" GROUP BY orgao.nme_orgao, orgao.id_orgao, orgao.id_orgao_superior, ");
			query.append("		 	tipoOrgao.nme_tipo_orgao, uf.cod_uf, municipio.nme_municipio ORDER BY orgao.nme_orgao ");
		} else if (sortField.equals("municipio")) {
			query.append(" GROUP BY orgao.nme_orgao, orgao.id_orgao, orgao.id_orgao_superior, ");
			query.append("		 	tipoOrgao.nme_tipo_orgao, uf.cod_uf, municipio.nme_municipio ORDER BY municipio.nme_municipio ");
		} else if (sortField.equals("municipio.uf")) {
			query.append(" GROUP BY orgao.nme_orgao, orgao.id_orgao, orgao.id_orgao_superior, ");
			query.append("		 	tipoOrgao.nme_tipo_orgao, uf.cod_uf, municipio.nme_municipio ORDER BY uf.nme_uf ");
		} else if (sortField.equals("tipoOrgao")) {
			query.append(" GROUP BY orgao.nme_orgao, orgao.id_orgao, orgao.id_orgao_superior, ");
			query.append("		 	tipoOrgao.nme_tipo_orgao, uf.cod_uf, municipio.nme_municipio ORDER BY tipoOrgao.nme_tipo_orgao ");
		}
		if (sortOrder == null || sortOrder.equals("ASCENDING")) {
			query.append("ASC ");
		} else {
			query.append("DESC ");
		}
	}

	private String recuperarQuery(OrgaoDTO orgaoFiltro, String sortField,
			String sortOrder) {
		StringBuilder query = new StringBuilder();
		query.append("SELECT orgao.id_orgao FROM atlas.orgao orgao ");

		query.append("INNER JOIN atlas.tipo_orgao tipoOrgao on orgao.id_tipo_orgao = tipoOrgao.id_tipo_orgao ");
		query.append("INNER JOIN atlas.municipio municipio on orgao.id_municipio = municipio.id_municipio ");
		query.append("INNER JOIN atlas.uf uf on municipio.id_uf = uf.id_uf ");
		if (orgaoFiltro.getUf() != null) {
			query.append(" and uf.id_uf = ")
			.append(orgaoFiltro.getUf().getId()+" ");
		}
		if(orgaoFiltro.getTema() != null){
			query.append("LEFT JOIN atlas.tema_orgao temaOrgao on orgao.id_orgao = temaOrgao.id_orgao ");
		}

		if(orgaoFiltro.getServico() != null){
			query.append("LEFT JOIN atlas.servico_orgao servicoOrgao on orgao.id_orgao = servicoOrgao.id_orgao ");
		}

		StringBuilder where = aplicarFiltros(orgaoFiltro);

		query.append(where);

		aplicarOrdenacao(sortField, sortOrder, query);

		return query.toString();
	}

	private void aplicarOrdenacao(String sortField, String sortOrder,
			StringBuilder query) {
		if(sortField == null){
			query.append(" GROUP BY orgao.id_orgao, uf.nme_uf, municipio.nme_municipio, tipoOrgao.nme_tipo_orgao, orgao.nme_orgao ");
			query.append(" ORDER BY uf.nme_uf, municipio.nme_municipio, tipoOrgao.nme_tipo_orgao, orgao.nme_orgao ");
		} else if (sortField.equals("nome")) {
			query.append(" GROUP BY orgao.nme_orgao, orgao.id_orgao ORDER BY orgao.nme_orgao ");
		} else if (sortField.equals("municipio")) {
			query.append(" GROUP BY municipio.nme_municipio, orgao.id_orgao ORDER BY municipio.nme_municipio ");
		} else if (sortField.equals("municipio.uf")) {
			query.append(" GROUP BY uf.nme_uf, orgao.id_orgao ORDER BY uf.nme_uf ");
		} else if (sortField.equals("tipoOrgao")) {
			query.append(" GROUP BY tipoOrgao.nme_tipo_orgao, orgao.id_orgao ORDER BY tipoOrgao.nme_tipo_orgao ");
		}
		if (sortOrder == null || sortOrder.equals("ASCENDING")) {
			query.append("ASC ");
		} else {
			query.append("DESC ");
		}
	}

	private StringBuilder aplicarFiltros(OrgaoDTO orgaoFiltro) {
		StringBuilder where = new StringBuilder();
		where.append(" WHERE 1 = 1 ");
		if (orgaoFiltro.getNome() != null
				&& orgaoFiltro.getNome().trim().length() > 0) {
			where.append(" AND upper(orgao.nme_orgao) like '%")
			.append(orgaoFiltro.getNome().toUpperCase()).append("%' ");
		}
		if (orgaoFiltro.getOrgaos() != null
				&& !orgaoFiltro.getOrgaos().isEmpty()) {
			where.append(" AND orgao.id_orgao in (");
			for (OrgaoDTO orgaoDTO : orgaoFiltro.getOrgaos()) {
				where.append(orgaoDTO.getId() + ", ");
			}
			where.replace(where.length() - 2, where.length(), "");
			where.append(") ");
		}
		if (orgaoFiltro.getTipoOrgao() != null) {
			where.append(" AND orgao.id_tipo_orgao = ")
			.append(orgaoFiltro.getTipoOrgao().getId()).append(" ");
		}
		if (orgaoFiltro.getAmbitoAtuacaoOrgao() != null) {
			where.append(" AND orgao.ind_ambito_atuacao = '")
			.append(orgaoFiltro.getAmbitoAtuacaoOrgao().getIndicador()).append("' ");
		}
		if (orgaoFiltro.getTema() != null) {
			where.append(" AND temaOrgao.id_tema = ")
			.append(orgaoFiltro.getTema().getId()).append(" ");
		}
		if (orgaoFiltro.getServico() != null) {
			where.append(" AND servicoOrgao.id_servico = ")
			.append(orgaoFiltro.getServico().getId()).append(" ");
		}
		if (orgaoFiltro.getMunicipio() != null) {
			where.append(" AND orgao.id_municipio = ")
			.append(orgaoFiltro.getMunicipio().getId()).append(" ");
		}

		if (orgaoFiltro.getOrgaosComErro() != null) {
			if (orgaoFiltro.getOrgaosComErro()) {
				where.append(" AND orgao.id_orgao in (SELECT distinct id_orgao FROM atlas.problema WHERE ind_status_problema = 'N')");
			} else {
				where.append(" AND orgao.id_orgao in (SELECT distinct id_orgao FROM atlas.problema WHERE ind_status_problema = 'S')");
			}
		}

		if (orgaoFiltro.getOrgaosComLatitudeLongitude() != null) {
			if (!orgaoFiltro.getOrgaosComLatitudeLongitude()) {
				where.append(" AND (orgao.num_latitude is null OR orgao.num_longitude is null) ");
			} else {
				where.append(" AND (orgao.num_latitude is not null AND orgao.num_longitude is not null )");
			}
		}

		if (orgaoFiltro.getOrgaosSituacaoRegistro() != null
				&& !"".equals(orgaoFiltro.getOrgaosSituacaoRegistro())) {
			if (isSituacaoRegistroEntreOsValoresAceitos(orgaoFiltro)) {
				where.append(" AND (orgao.ind_situacao = '")
				.append(orgaoFiltro.getOrgaosSituacaoRegistro())
				.append("') ");
			}
		}
		return where;
	}

	@SuppressWarnings("unchecked")
	public List<OrgaoDTO> recuperarTipoOrgaoPorRegiaoEstadoMunicipio(
			Integer codRegiao, String siglaUf, String municipio)
					throws AtlasAcessoBancoDadosException {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT TIPO.NME_TIPO_ORGAO, COUNT(*) AS TOTAL ");
		sql.append(" FROM ATLAS.ORGAO ORG ");
		sql.append("JOIN ATLAS.TIPO_ORGAO TIPO ON ORG.ID_TIPO_ORGAO = TIPO.ID_TIPO_ORGAO ");
		sql.append("JOIN ATLAS.MUNICIPIO MUN ON ORG.ID_MUNICIPIO = MUN.ID_MUNICIPIO ");
		if (municipio != null) {
			sql.append("  AND MUN.NME_MUNICIPIO = '" + municipio + "'");
		}
		sql.append("  JOIN ATLAS.UF UE ON MUN.ID_UF = UE.ID_UF  ");
		if (siglaUf != null) {
			sql.append("  AND UE.COD_UF = '" + siglaUf + "'");
		}
		sql.append("  JOIN ATLAS.REGIAO REG ON UE.ID_REGIAO = REG.ID_REGIAO ");
		if (codRegiao != null) {
			sql.append("  AND REG.ID_REGIAO = '" + codRegiao + "'");
		}
		sql.append("  GROUP BY TIPO.NME_TIPO_ORGAO");
		sql.append("  ORDER BY TIPO.NME_TIPO_ORGAO");
		AtlasQuery atlasQuery = createNativeQuery(sql.toString());
		List<Object[]> resultado = atlasQuery.getResultList();
		List<OrgaoDTO> lista = new ArrayList<>();
		for (Object[] obj : resultado) {
			OrgaoDTO orgao = new OrgaoDTO();

			orgao.setNome(obj[0].toString());
			orgao.setQtdTipoOrgao(Integer.parseInt(obj[1].toString()));

			lista.add(orgao);
		}

		return lista;
	}

	@SuppressWarnings("unchecked")
	public List<OrgaoDTO> recuperarTipoOrgaoPorBrasil()
			throws AtlasAcessoBancoDadosException {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT TIPO.NME_TIPO_ORGAO, COUNT(*) AS TOTAL ");
		sql.append(" FROM ATLAS.ORGAO ORG ");
		sql.append(" JOIN ATLAS.TIPO_ORGAO TIPO ON ORG.ID_TIPO_ORGAO = TIPO.ID_TIPO_ORGAO ");
		sql.append(" GROUP BY TIPO.NME_TIPO_ORGAO");
		sql.append(" ORDER BY TIPO.NME_TIPO_ORGAO");

		AtlasQuery atlasQuery = createNativeQuery(sql.toString());
		List<Object[]> resultado = atlasQuery.getResultList();
		List<OrgaoDTO> lista = new ArrayList<>();
		for (Object[] obj : resultado) {
			OrgaoDTO orgao = new OrgaoDTO();

			orgao.setNome(obj[0].toString());
			orgao.setQtdTipoOrgao(Integer.parseInt(obj[1].toString()));

			lista.add(orgao);
		}

		return lista;
	}

	@SuppressWarnings("unchecked")
	public List<Orgao> recuperarOrgaosPorTema(Integer idTema)
			throws AtlasAcessoBancoDadosException {
		StringBuilder hql = new StringBuilder();
		hql.append(" SELECT ");
		hql.append(" 	o.id_orgao, ");
		hql.append(" 	tipo.id_tipo_orgao, ");
		hql.append(" 	ramo.id_ramo, ");
		hql.append(" 	o.nme_orgao, ");
		hql.append(" 	o.num_latitude, ");
		hql.append(" 	o.num_longitude ");
		hql.append(" FROM ");
		hql.append(" 	atlas.tema t ");
		hql.append(" 	INNER JOIN atlas.tema_orgao rto ON(t.id_tema = :idTema AND t.id_tema = rto.id_tema) ");
		hql.append(" 	INNER JOIN atlas.orgao o ON(rto.id_orgao = o.id_orgao) ");
		hql.append(" 	INNER JOIN atlas.tipo_orgao tipo ON(o.id_tipo_orgao = tipo.id_tipo_orgao) ");
		hql.append(" 	INNER JOIN atlas.ramo ramo ON(tipo.id_ramo = ramo.id_ramo) ");

		AtlasQuery atlasQuery = createNativeQuery(hql.toString());
		atlasQuery.setParameter("idTema", idTema);

		List<Object[]> resultado = atlasQuery.getResultList();
		List<Orgao> lista = new ArrayList<>();

		Integer idOrgao = null;
		Integer idTipoOrgao = null;
		Integer idRamo = null;
		String nomeOrgao = null;
		Double latitude = null;
		Double longitude = null;

		TipoOrgao to = null;
		Ramo ramo = null;
		for (Object[] obj : resultado) {
			idOrgao = (Integer) obj[0];
			idTipoOrgao = (Integer) obj[1];
			idRamo = (Integer) obj[2];
			nomeOrgao = (String) obj[3];
			latitude = (Double) obj[4];
			longitude = (Double) obj[5];

			Orgao orgao = new Orgao();
			orgao.setId(idOrgao);
			orgao.setNome(nomeOrgao);

			to = new TipoOrgao();
			to.setId(idTipoOrgao);

			ramo = new Ramo();
			ramo.setId(idRamo);

			to.setRamo(ramo);
			orgao.setTipoOrgao(to);

			if (latitude != null) {
				orgao.setLatitude(BigDecimal.valueOf(latitude));
			}
			if (longitude != null) {
				orgao.setLongitude(BigDecimal.valueOf(longitude));
			}
			lista.add(orgao);
		}

		return lista;
	}

	@SuppressWarnings("unchecked")
	public List<Orgao> recuperarOrgaosPorServico(Integer idServico)
			throws AtlasAcessoBancoDadosException {
		StringBuilder hql = new StringBuilder();
		hql.append(" select  ");
		hql.append(" 	o.id_orgao,  ");
		hql.append("  	tipo.id_tipo_orgao,  ");
		hql.append("  	ramo.id_ramo,  ");
		hql.append("  	o.nme_orgao,  ");
		hql.append("  	o.num_latitude,  ");
		hql.append("  	o.num_longitude   ");
		hql.append(" from  ");
		hql.append(" 	orgao o ");
		hql.append(" 	INNER JOIN atlas.servico_orgao so ON so.id_servico = :idServico AND so.id_orgao = o.id_orgao  ");
		hql.append(" 	INNER JOIN atlas.tipo_orgao tipo ON(o.id_tipo_orgao = tipo.id_tipo_orgao)  ");
		hql.append(" 	INNER JOIN atlas.ramo ramo ON(tipo.id_ramo = ramo.id_ramo)  ");

		AtlasQuery atlasQuery = createNativeQuery(hql.toString());
		atlasQuery.setParameter("idServico", idServico);

		List<Object[]> resultado = atlasQuery.getResultList();
		List<Orgao> lista = new ArrayList<>();

		Integer idOrgao = null;
		Integer idTipoOrgao = null;
		Integer idRamo = null;
		String nomeOrgao = null;
		Double latitude = null;
		Double longitude = null;

		TipoOrgao to = null;
		Ramo ramo = null;
		for (Object[] obj : resultado) {
			idOrgao = (Integer) obj[0];
			idTipoOrgao = (Integer) obj[1];
			idRamo = (Integer) obj[2];
			nomeOrgao = (String) obj[3];
			latitude = (Double) obj[4];
			longitude = (Double) obj[5];

			Orgao orgao = new Orgao();
			orgao.setId(idOrgao);
			orgao.setNome(nomeOrgao);

			to = new TipoOrgao();
			to.setId(idTipoOrgao);

			ramo = new Ramo();
			ramo.setId(idRamo);

			to.setRamo(ramo);
			orgao.setTipoOrgao(to);

			if (latitude != null) {
				orgao.setLatitude(BigDecimal.valueOf(latitude));
			}
			if (longitude != null) {
				orgao.setLongitude(BigDecimal.valueOf(longitude));
			}
			lista.add(orgao);
		}

		return lista;
	}

	@SuppressWarnings("unchecked")
	public List<TotalizadorOrgaoDTO> recuperarTotalizadoresPorIndicadorMapaNivelRegiao(
			IndicadorMapa indicadorMapa) throws AtlasAcessoBancoDadosException {
		StringBuilder hql = new StringBuilder();
		hql.append(" SELECT ");
		hql.append(" 	r.id_regiao id, ");
		hql.append(" 	r.nme_regiao nome, ");
		hql.append(" 	COUNT(o.id_orgao) total ");
		hql.append(" FROM ");
		hql.append(" 	atlas.tema t ");
		hql.append(" 	INNER JOIN atlas.tema_orgao rto ON(t.id_tema = rto.id_tema) ");
		hql.append(" 	INNER JOIN atlas.tema tema ON(tema.id_tema = rto.id_tema AND tema.mapa = :indicadorMapa) ");
		hql.append(" 	INNER JOIN atlas.orgao o ON(rto.id_orgao = o.id_orgao) ");
		hql.append(" 	INNER JOIN atlas.tipo_orgao tipo ON(o.id_tipo_orgao = tipo.id_tipo_orgao) ");
		hql.append(" 	INNER JOIN atlas.municipio m ON(o.id_municipio = m.id_municipio) ");
		hql.append(" 	INNER JOIN atlas.uf u ON(m.id_uf = u.id_uf) ");
		hql.append(" 	INNER JOIN atlas.regiao r ON(u.id_regiao = r.id_regiao) ");
		hql.append(" GROUP BY ");
		hql.append(" 	id, ");
		hql.append(" 	nome ");
		hql.append(" ORDER BY ");
		hql.append(" 	nome ");

		AtlasQuery atlasQuery = createNativeQuery(hql.toString());
		atlasQuery.setParameter("indicadorMapa", indicadorMapa.getValor());

		List<Object[]> resultado = atlasQuery.getResultList();

		List<TotalizadorOrgaoDTO> totalizadores = new ArrayList<TotalizadorOrgaoDTO>();

		for (Object[] obj : resultado) {
			totalizadores.add(new TotalizadorOrgaoDTO((Integer) obj[0],
					(BigInteger) obj[2], (String) obj[1],
					NivelTotalizadorOrgao.REGIAO));
		}

		return totalizadores;
	}

	@SuppressWarnings("unchecked")
	public List<TotalizadorOrgaoDTO> recuperarTotalizadoresPorIndicadorMapaNivelEstado(
			IndicadorMapa indicadorMapa, Integer idRegiao)
					throws AtlasAcessoBancoDadosException {
		StringBuilder hql = new StringBuilder();
		hql.append(" SELECT ");
		hql.append(" 	u.id_uf id, ");
		hql.append(" 	u.nme_uf nome, ");
		hql.append(" 	COUNT(o.id_orgao) total ");
		hql.append(" FROM ");
		hql.append(" 	atlas.tema t ");
		hql.append(" 	INNER JOIN atlas.tema_orgao rto ON(t.id_tema = rto.id_tema) ");
		hql.append(" 	INNER JOIN atlas.tema tema ON(tema.id_tema = rto.id_tema AND tema.mapa = :indicadorMapa) ");
		hql.append(" 	INNER JOIN atlas.orgao o ON(rto.id_orgao = o.id_orgao) ");
		hql.append(" 	INNER JOIN atlas.tipo_orgao tipo ON(o.id_tipo_orgao = tipo.id_tipo_orgao) ");
		hql.append(" 	INNER JOIN atlas.municipio m ON(o.id_municipio = m.id_municipio) ");
		hql.append(" 	INNER JOIN atlas.uf u ON(m.id_uf = u.id_uf) ");
		hql.append(" 	INNER JOIN atlas.regiao r ON(u.id_regiao = r.id_regiao) ");
		hql.append(" WHERE ");
		hql.append(" 	r.id_regiao = :idRegiao ");
		hql.append(" GROUP BY ");
		hql.append(" 	id, ");
		hql.append(" 	nome ");
		hql.append(" ORDER BY ");
		hql.append(" 	nome ");

		AtlasQuery atlasQuery = createNativeQuery(hql.toString());
		atlasQuery.setParameter("indicadorMapa", indicadorMapa.getValor());
		atlasQuery.setParameter("idRegiao", idRegiao);

		List<Object[]> resultado = atlasQuery.getResultList();

		List<TotalizadorOrgaoDTO> totalizadores = new ArrayList<TotalizadorOrgaoDTO>();

		for (Object[] obj : resultado) {
			totalizadores.add(new TotalizadorOrgaoDTO((Integer) obj[0],
					(BigInteger) obj[2], (String) obj[1],
					NivelTotalizadorOrgao.ESTADO));
		}

		return totalizadores;
	}

	@SuppressWarnings("unchecked")
	public List<TotalizadorOrgaoDTO> recuperarTotalizadoresPorIndicadorMapa(
			IndicadorMapa indicadorMapa, Integer idEstado)
					throws AtlasAcessoBancoDadosException {
		StringBuilder hql = new StringBuilder();
		hql.append(" SELECT ");
		hql.append(" 	tipo.id_tipo_orgao id, ");
		hql.append(" 	tipo.nme_tipo_orgao nome, ");
		hql.append(" 	COUNT(o.id_orgao) total ");
		hql.append(" FROM ");
		hql.append(" 	atlas.tema t ");
		hql.append(" 	INNER JOIN atlas.tema_orgao rto ON(t.id_tema = rto.id_tema) ");
		hql.append(" 	INNER JOIN atlas.tema tema ON(tema.id_tema = rto.id_tema AND tema.mapa = :indicadorMapa) ");
		hql.append(" 	INNER JOIN atlas.orgao o ON(rto.id_orgao = o.id_orgao) ");
		hql.append(" 	INNER JOIN atlas.tipo_orgao tipo ON(o.id_tipo_orgao = tipo.id_tipo_orgao) ");
		hql.append(" 	INNER JOIN atlas.municipio m ON(o.id_municipio = m.id_municipio) ");
		hql.append(" 	INNER JOIN atlas.uf u ON(m.id_uf = u.id_uf) ");
		hql.append(" WHERE ");
		hql.append(" 	u.id_uf = :idEstado ");
		hql.append(" GROUP BY ");
		hql.append(" 	id, ");
		hql.append(" 	nome ");
		hql.append(" ORDER BY ");
		hql.append(" 	nome ");

		AtlasQuery atlasQuery = createNativeQuery(hql.toString());
		atlasQuery.setParameter("indicadorMapa", indicadorMapa.getValor());
		atlasQuery.setParameter("idEstado", idEstado);

		List<Object[]> resultado = atlasQuery.getResultList();

		List<TotalizadorOrgaoDTO> totalizadores = new ArrayList<TotalizadorOrgaoDTO>();

		for (Object[] obj : resultado) {
			totalizadores.add(new TotalizadorOrgaoDTO((Integer) obj[0],
					(BigInteger) obj[2], (String) obj[1],
					NivelTotalizadorOrgao.TIPO_ORGAO));
		}

		return totalizadores;
	}

	@SuppressWarnings("unchecked")
	public List<TotalizadorOrgaoDTO> recuperarTotalizadoresPorIndicadorMapaNivelBrasil(
			IndicadorMapa indicadorMapa) throws AtlasAcessoBancoDadosException {
		StringBuilder hql = new StringBuilder();
		hql.append(" SELECT ");
		hql.append(" 	tipo.id_tipo_orgao id, ");
		hql.append(" 	tipo.nme_tipo_orgao nome, ");
		hql.append(" 	COUNT(o.id_orgao) total ");
		hql.append(" FROM ");
		hql.append(" 	atlas.tema t ");
		hql.append(" 	INNER JOIN atlas.tema_orgao rto ON(t.id_tema = rto.id_tema) ");
		hql.append(" 	INNER JOIN atlas.tema tema ON(tema.id_tema = rto.id_tema AND tema.mapa = :indicadorMapa) ");
		hql.append(" 	INNER JOIN atlas.orgao o ON(rto.id_orgao = o.id_orgao) ");
		hql.append(" 	INNER JOIN atlas.tipo_orgao tipo ON(o.id_tipo_orgao = tipo.id_tipo_orgao) ");
		hql.append(" GROUP BY ");
		hql.append(" 	id, ");
		hql.append(" 	nome ");
		hql.append(" ORDER BY ");
		hql.append(" 	nome ");

		AtlasQuery atlasQuery = createNativeQuery(hql.toString());
		atlasQuery.setParameter("indicadorMapa", indicadorMapa.getValor());

		List<Object[]> resultado = atlasQuery.getResultList();

		List<TotalizadorOrgaoDTO> totalizadores = new ArrayList<TotalizadorOrgaoDTO>();

		for (Object[] obj : resultado) {
			totalizadores.add(new TotalizadorOrgaoDTO((Integer) obj[0],
					(BigInteger) obj[2], (String) obj[1],
					NivelTotalizadorOrgao.BRASIL));
		}

		return totalizadores;
	}

	@SuppressWarnings("unchecked")
	public Map<String, List<OrgaoDTO>> recuperarTipoOrgaoPorUnidade(
			String unidade) throws AtlasAcessoBancoDadosException {
		StringBuilder sql = new StringBuilder();
		sql.append("  SELECT REG.NME_REGIAO, TIPO.NME_TIPO_ORGAO, COUNT(*) AS TOTAL ");
		sql.append("  FROM ATLAS.ORGAO ORG ");
		sql.append("  JOIN ATLAS.TIPO_ORGAO TIPO ON ORG.ID_TIPO_ORGAO = TIPO.ID_TIPO_ORGAO AND  ");
		sql.append("  JOIN ATLAS.MUNICIPIO MUN ON ORG.ID_MUNICIPIO = MUN.ID_MUNICIPIO ");
		sql.append("  JOIN ATLAS.UF UF ON MUN.ID_UF = UF.ID_UF ");
		sql.append("  JOIN ATLAS.REGIAO REG ON UF.ID_REGIAO = REG.ID_REGIAO ");
		sql.append("  GROUP BY REG.NME_REGIAO, TIPO.NME_TIPO_ORGAO ");
		sql.append("  ORDER BY REG.NME_REGIAO, TIPO.NME_TIPO_ORGAO");

		AtlasQuery atlasQuery = createNativeQuery(sql.toString());
		List<Object[]> resultado = atlasQuery.getResultList();
		List<OrgaoDTO> lista = new ArrayList<>();
		for (Object[] obj : resultado) {
			OrgaoDTO orgao = new OrgaoDTO();

			orgao.setNome(obj[0].toString());
			orgao.setQtdTipoOrgao(Integer.parseInt(obj[1].toString()));

			lista.add(orgao);
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	public List<EnderecoDTO> recuperarEnderecoPorTipoMunicipio(String tipo,
			String municipio) throws AtlasAcessoBancoDadosException {
		StringBuilder sql = new StringBuilder();
		sql.append("  SELECT DSC_ORGAO orgao, DSC_ENDERECO endereco, NU_CEP cep, ");
		sql.append("         '('||FONE.NU_DDD||')'||FONE.NU_TELEFONE fone ");
		sql.append("  FROM ATLAS.ORGAO ORG ");
		sql.append("  LEFT JOIN ATLAS.TELEFONE FONE     ON ORG.ID_ORGAO = FONE.ID_ORGAO ");
		sql.append("  JOIN ATLAS.TIPO_ORGAO  TIPO  ON ORG.ID_TIPO_ORGAO = TIPO.ID_TIPO_ORGAO AND NME_TIPO_ORGAO  = ? ");
		sql.append("  JOIN ATLAS.MUNICIPIO   MUN   ON ORG.ID_MUNICIPIO  = MUN.ID_MUNICIPIO AND MUN.NME_MUNICIPIO = ? ");
		sql.append("  ORDER BY MUN.NME_MUNICIPIO, TIPO.NME_TIPO_ORGAO ");

		AtlasQuery atlasQuery = createNativeQuery(sql.toString());
		atlasQuery.setParameter(1, tipo);
		atlasQuery.setParameter(2, municipio);
		List<Object[]> resultado = atlasQuery.getResultList();
		List<EnderecoDTO> lista = new ArrayList<>();

		for (Object[] obj : resultado) {
			EnderecoDTO endereco = new EnderecoDTO();

			String org = (String) obj[0];
			String end = (String) obj[1];
			String cep = (String) obj[2];
			String fone = (String) obj[3];

			endereco.setOrgao(org);
			endereco.setEndereco(end);
			endereco.setCep(cep);
			endereco.setFone(fone);

			lista.add(endereco);
		}

		return lista;
	}

	public int salvarEmBloco(DadosEmBlocoDTO dadosEmBlocoDTO)
			throws AtlasAcessoBancoDadosException {
		int qtdOrgaosAlterados = 0;
		if (dadosEmBlocoDTO.isAlteracaoPorFiltro()) {
			String consultaPorFiltro = recuperarQuery(
					dadosEmBlocoDTO.getOrgaoFiltro(), null, null);
			AtlasQuery atlasQuery = createNativeQuery(consultaPorFiltro);
			List<Orgao> orgaosRecuperados = new ArrayList<>();
			for (Object obj : atlasQuery.getResultList()) {
				Integer idOrgao = (Integer) obj;
				Orgao orgao = new Orgao();
				orgao.setId(idOrgao);
				orgaosRecuperados.add(orgao);
			}
			dadosEmBlocoDTO.setOrgaosSelecionados(orgaosRecuperados);
		}
		alterarTemasOrgaos(dadosEmBlocoDTO);
		alterarServicosOrgaos(dadosEmBlocoDTO);
		alterarRepresentantesOrgaos(dadosEmBlocoDTO);
		alterarEmailsOrgaos(dadosEmBlocoDTO);
		if(dadosEmBlocoDTO.getOrgaoSuperior() != null){
			alterarOrgaosSuperiores(dadosEmBlocoDTO);
		}
		qtdOrgaosAlterados = alterarDadosOrgao(dadosEmBlocoDTO);
		if (dadosEmBlocoDTO.isAlteracaoPorFiltro()) {
			dadosEmBlocoDTO.setOrgaosSelecionados(null);
		}
		return qtdOrgaosAlterados;
	}

	private int alterarOrgaosSuperiores(DadosEmBlocoDTO dadosEmBlocoDTO) throws AtlasAcessoBancoDadosException {
		StringBuilder sqlUpdate = new StringBuilder();
		sqlUpdate.append("UPDATE ORGAO SET 	");
		if (dadosEmBlocoDTO.getOrgaoSuperior() != null) {
			sqlUpdate.append("		ID_ORGAO_SUPERIOR = "
					+ dadosEmBlocoDTO.getOrgaoSuperior().getId() + ", ");
		}
		sqlUpdate.append(" DTA_ATUALIZACAO = CURRENT_TIMESTAMP ");

		sqlUpdate.append("WHERE ID_ORGAO IN ("
				+ strListIdOrgao(dadosEmBlocoDTO.getOrgaosSelecionados())
				+ ") ");
		if (dadosEmBlocoDTO.getOrgaoSuperior() != null) {
			sqlUpdate.append(" AND ID_ORGAO != " + dadosEmBlocoDTO.getOrgaoSuperior().getId() + " ");
		}
		AtlasQuery atlasQuery = createNativeQuery(sqlUpdate.toString());

		int qtdOrgaosAlterados = atlasQuery.executeUpdate();
		return qtdOrgaosAlterados;
	}

	private void alterarEmailsOrgaos(DadosEmBlocoDTO dadosEmBlocoDTO)
			throws AtlasAcessoBancoDadosException {
		if (dadosEmBlocoDTO.getEmail() != null
				&& !dadosEmBlocoDTO.getEmail().trim().equals("")) {
			// Removendo os emails dos órgãos
			StringBuilder queryDelete = new StringBuilder();
			queryDelete.append("DELETE FROM EMAIL WHERE ID_ORGAO IN ("
					+ strListIdOrgao(dadosEmBlocoDTO.getOrgaosSelecionados())
					+ ") ");
			AtlasQuery atlasQuery = createNativeQuery(queryDelete.toString());
			atlasQuery.executeUpdate();

			// Relacionando os órgãos aos novos emails
			for (Orgao orgao : dadosEmBlocoDTO.getOrgaosSelecionados()) {
				StringBuilder queryInsert = new StringBuilder();
				queryInsert
				.append("INSERT INTO EMAIL (id_email, id_orgao, nme_email, ind_email_institucional, dsc_email) ");
				queryInsert.append("VALUES (NEXTVAL('email_id_email_seq'), "
						+ orgao.getId() + ", '" + dadosEmBlocoDTO.getEmail()
						+ "', ");
				queryInsert.append("'" + EmailUsuario.INSTITUCIONAL + "','"
						+ dadosEmBlocoDTO.getEmail() + "') ");
				atlasQuery = createNativeQuery(queryInsert.toString());
				atlasQuery.executeUpdate();
			}
		}
	}

	private void alterarRepresentantesOrgaos(DadosEmBlocoDTO dadosEmBlocoDTO)
			throws AtlasAcessoBancoDadosException {
		if (dadosEmBlocoDTO.getRepresentantes() != null
				&& !dadosEmBlocoDTO.getRepresentantes().isEmpty()) {
			// Removendo os representantes dos órgãos
			StringBuilder queryDelete = new StringBuilder();
			queryDelete
			.append("DELETE FROM REPRESENTANTE_ORGAO WHERE ID_ORGAO IN ("
					+ strListIdOrgao(dadosEmBlocoDTO
							.getOrgaosSelecionados()) + ") ");
			AtlasQuery atlasQuery = createNativeQuery(queryDelete.toString());
			atlasQuery.executeUpdate();

			// Relacionando os representantes aos órgãos
			for (Representante representante : dadosEmBlocoDTO
					.getRepresentantes()) {
				for (Orgao orgao : dadosEmBlocoDTO.getOrgaosSelecionados()) {
					StringBuilder queryInsert = new StringBuilder();
					queryInsert.append("INSERT INTO REPRESENTANTE_ORGAO ");
					queryInsert
					.append("	(ID_REPRESENTANTE, ID_ORGAO, NME_REPRESENTANTE, NME_EMAIL, NME_CARGO, ");
					if (representante.getPrimeiroDdd() != null
							&& !representante.getPrimeiroDdd().trim()
							.equals("")
							&& representante.getPrimeiroTelefone() != null
							&& !representante.getPrimeiroTelefone().trim()
							.equals("")) {
						queryInsert.append("	NU_DDD_1, NU_TELEFONE_1, ");
					}
					queryInsert.append("	IND_TIPO_TELEFONE_1,");
					if (representante.getSegundoDdd() != null
							&& !representante.getSegundoDdd().trim().equals("")
							&& representante.getSegundoTelefone() != null
							&& !representante.getSegundoTelefone().trim()
							.equals("")) {
						queryInsert.append("	NU_DDD_2, NU_TELEFONE_2, ");
					}
					queryInsert.append("	IND_TIPO_TELEFONE_2, ");
					queryInsert.append("	IND_STATUS_DIVULGA) ");
					queryInsert.append("VALUES( ");
					queryInsert
					.append("	NEXTVAL('representante_orgao_id_representante_seq'), ");
					queryInsert.append("	" + orgao.getId() + ",'"
							+ representante.getNome() + "','"
							+ representante.getEmail() + "','"
							+ representante.getCargo() + "', ");
					if (representante.getPrimeiroDdd() != null
							&& !representante.getPrimeiroDdd().trim()
							.equals("")
							&& representante.getPrimeiroTelefone() != null
							&& !representante.getPrimeiroTelefone().trim()
							.equals("")) {
						queryInsert.append(" '"
								+ representante.getPrimeiroDdd() + "','"
								+ representante.getPrimeiroTelefone() + "', ");
					}
					String indTipoTelefoneUm = representante
							.getPrimeiroTipoTelefone();
					indTipoTelefoneUm = (indTipoTelefoneUm == null || indTipoTelefoneUm
							.equals("")) ? TELEFONE_FIXO.getValor()
									: indTipoTelefoneUm;
							queryInsert.append(" 	'" + indTipoTelefoneUm + "', ");
							if (representante.getSegundoDdd() != null
									&& !representante.getSegundoDdd().trim().equals("")
									&& representante.getSegundoTelefone() != null
									&& !representante.getSegundoTelefone().trim()
									.equals("")) {
								queryInsert.append(" '" + representante.getSegundoDdd()
								+ "','" + representante.getSegundoTelefone()
								+ "', ");
							}
							String indTipoTelefoneDois = representante
									.getSegundoTipoTelefone();
							indTipoTelefoneDois = (indTipoTelefoneDois == null || indTipoTelefoneDois
									.equals("")) ? TELEFONE_FIXO.getValor()
											: indTipoTelefoneDois;
									queryInsert.append(" 	'" + indTipoTelefoneDois + "', ");
									if (representante.isDivulgavel()) {
										queryInsert.append("	'S') ");
									} else {
										queryInsert.append("	'N') ");
									}
									atlasQuery = createNativeQuery(queryInsert.toString());
									atlasQuery.executeUpdate();
				}
			}
		}
	}

	private void alterarTemasOrgaos(DadosEmBlocoDTO dadosEmBlocoDTO)
			throws AtlasAcessoBancoDadosException {
		if (dadosEmBlocoDTO.getTemas() != null
				&& !dadosEmBlocoDTO.getTemas().isEmpty()) {
			// Removendo os temas dos órgãos
			StringBuilder queryDelete = new StringBuilder();
			queryDelete.append("DELETE FROM TEMA_ORGAO WHERE ID_ORGAO IN ("
					+ strListIdOrgao(dadosEmBlocoDTO.getOrgaosSelecionados())
					+ ") ");
			AtlasQuery atlasQuery = createNativeQuery(queryDelete.toString());
			atlasQuery.executeUpdate();

			// Relacionando os órgãos aos novos temas
			for (Tema tema : dadosEmBlocoDTO.getTemas()) {
				for (Orgao orgao : dadosEmBlocoDTO.getOrgaosSelecionados()) {
					StringBuilder queryInsert = new StringBuilder();
					queryInsert
					.append("INSERT INTO TEMA_ORGAO (id_tema, id_orgao) VALUES ("
							+ tema.getId() + "," + orgao.getId() + ")");
					atlasQuery = createNativeQuery(queryInsert.toString());
					atlasQuery.executeUpdate();
				}
			}
		}
	}

	private void alterarServicosOrgaos(DadosEmBlocoDTO dadosEmBlocoDTO)
			throws AtlasAcessoBancoDadosException {
		if (dadosEmBlocoDTO.getServicos() != null
				&& !dadosEmBlocoDTO.getServicos().isEmpty()) {
			// Removendo os servicos dos órgãos
			StringBuilder queryDelete = new StringBuilder();
			queryDelete.append("DELETE FROM SERVICO_ORGAO WHERE ID_ORGAO IN ("
					+ strListIdOrgao(dadosEmBlocoDTO.getOrgaosSelecionados())
					+ ") ");
			AtlasQuery atlasQuery = createNativeQuery(queryDelete.toString());
			atlasQuery.executeUpdate();

			// Relacionando os órgãos aos novos temas
			for (Servico servico : dadosEmBlocoDTO.getServicos()) {
				for (Orgao orgao : dadosEmBlocoDTO.getOrgaosSelecionados()) {
					StringBuilder queryInsert = new StringBuilder();
					queryInsert
					.append("INSERT INTO SERVICO_ORGAO (id_servico, id_orgao) VALUES ("
							+ servico.getId() + "," + orgao.getId() + ")");
					atlasQuery = createNativeQuery(queryInsert.toString());
					atlasQuery.executeUpdate();
				}
			}
		}
	}

	private int alterarDadosOrgao(DadosEmBlocoDTO dadosEmBlocoDTO)
			throws AtlasAcessoBancoDadosException {
		StringBuilder sqlUpdate = new StringBuilder();
		sqlUpdate.append("UPDATE ORGAO SET 	");
		if (dadosEmBlocoDTO.getTipoOrgao() != null) {
			sqlUpdate.append("		ID_TIPO_ORGAO = "
					+ dadosEmBlocoDTO.getTipoOrgao().getId() + ", ");
		}
		if (dadosEmBlocoDTO.getSigla() != null
				&& !dadosEmBlocoDTO.getSigla().trim().equals("")) {
			sqlUpdate.append(" 		COD_SIGLA_ORGAO = '"
					+ dadosEmBlocoDTO.getSigla() + "', ");
		}
		if (dadosEmBlocoDTO.getDescricao() != null
				&& !dadosEmBlocoDTO.getDescricao().trim().equals("")) {
			sqlUpdate.append(" 		DSC_ORGAO = '"
					+ dadosEmBlocoDTO.getDescricao() + "', ");
		}
		if (dadosEmBlocoDTO.getHomePage() != null
				&& !dadosEmBlocoDTO.getHomePage().trim().equals("")) {
			sqlUpdate.append(" 		NME_HOMEPAGE = '"
					+ dadosEmBlocoDTO.getHomePage() + "', ");
		}

		sqlUpdate.append(" DTA_ATUALIZACAO = CURRENT_TIMESTAMP ");

		sqlUpdate.append("WHERE ID_ORGAO IN ("
				+ strListIdOrgao(dadosEmBlocoDTO.getOrgaosSelecionados())
				+ ") ");
		AtlasQuery atlasQuery = createNativeQuery(sqlUpdate.toString());

		int qtdOrgaosAlterados = atlasQuery.executeUpdate();
		return qtdOrgaosAlterados;
	}

	// Alterar para query nativa por causa da performance
	@SuppressWarnings("unchecked")
	public List<Orgao> recuperarOrgaosPorMunicipioTipo(Municipio municipio,
			List<Integer> tiposOrgaoSelecionados, Integer idTipoParaOrdenacao)
					throws AtlasAcessoBancoDadosException {

		boolean municipioInformado = municipio != null;

		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ");
		sql.append(" 		orgao.id_orgao, "); //0
		sql.append(" 		orgao.nme_orgao, ");//1
		sql.append(" 		orgao.num_latitude, ");//2
		sql.append(" 		orgao.num_longitude, ");//3
		sql.append(" 		orgao.dsc_endereco, ");//4
		sql.append(" 		orgao.dsc_bairro, ");//5
		sql.append(" 		orgao.nu_cep, ");//6

		sql.append(" 		municipio.id_municipio, ");//7
		sql.append(" 		municipio.nme_municipio, ");//8

		sql.append(" 		uf.id_uf, ");//9
		sql.append(" 		uf.nme_uf, ");//10
		sql.append(" 		uf.cod_uf, ");//11

		sql.append(" 		tipo.id_tipo_orgao, ");//12
		sql.append(" 		tipo.nme_tipo_orgao, ");//13

		sql.append(" 		ramo.id_ramo, ");//14
		sql.append(" 		ramo.nme_ramo, ");//15

		if(idTipoParaOrdenacao != null){
			sql.append(" 		CASE WHEN tipo.id_tipo_orgao = :idTipoParaOrdenacao THEN '1' ELSE '9' END ordenacaoTipoOrgao ");// 16
		}
		sql.append(" FROM ");
		sql.append(" 		atlas.orgao orgao ");
		sql.append(" 		INNER JOIN atlas.tipo_orgao tipo ON(orgao.id_tipo_orgao = tipo.id_tipo_orgao) ");
		sql.append(" 		INNER JOIN atlas.ramo ramo ON(tipo.id_ramo = ramo.id_ramo) ");
		sql.append(" 		INNER JOIN atlas.municipio municipio ON(orgao.id_municipio = municipio.id_municipio) ");
		sql.append(" 		INNER JOIN atlas.uf uf ON(uf.id_uf = municipio.id_uf) ");
		sql.append(" WHERE ");
		sql.append(" 		orgao.id_tipo_orgao IN(:tiposOrgaoSelecionados) ");
		sql.append(" 		AND orgao.ind_situacao = '2' ");
		if (municipioInformado) {
			sql.append(" 		AND municipio.id_municipio = :idMunicipio ");
		}
		sql.append(" ORDER BY ");
		if(idTipoParaOrdenacao != null){
			sql.append(" 		 ordenacaoTipoOrgao, ");
		}
		sql.append(" 		 orgao.nme_orgao ");

		AtlasQuery atlasQuery = createNativeQuery(sql.toString());
		atlasQuery.setParameter("tiposOrgaoSelecionados",
				tiposOrgaoSelecionados);
		if(idTipoParaOrdenacao != null){
			atlasQuery.setParameter("idTipoParaOrdenacao",idTipoParaOrdenacao);
		}
		if(municipioInformado){
			atlasQuery.setParameter("idMunicipio", municipio.getId());
		}

		List<Object[]> resultado = atlasQuery.getResultList();
		List<Orgao> lista = new ArrayList<>();
		Orgao orgao;
		TipoOrgao tipoOrgao;
		Ramo ramo;
		Municipio m;
		UF uf;

		for (Object[] obj : resultado) {
			orgao = new Orgao();
			tipoOrgao = new TipoOrgao();
			ramo = new Ramo();
			m = new Municipio();
			uf = new UF();

			orgao.setId(Integer.parseInt(obj[0].toString()));
			orgao.setNome(obj[1].toString());
			orgao.setLatitude(obj[2] != null ? new BigDecimal(obj[2].toString())
					: null);
			orgao.setLongitude(obj[3] != null ? new BigDecimal(obj[3]
					.toString()) : null);
			orgao.setEndereco(obj[4] != null ? obj[4].toString() : null);
			orgao.setBairro(obj[5] != null ? obj[5].toString() : null);
			orgao.setCep(obj[6] != null ? obj[6].toString() : null);

			m.setId(Integer.parseInt(obj[7].toString()));
			m.setNome(obj[8].toString());

			uf.setId(Integer.parseInt(obj[9].toString()));
			uf.setNome(obj[10].toString());
			uf.setSigla(obj[11].toString());

			m.setUf(uf);
			orgao.setMunicipio(m);

			tipoOrgao.setId(Integer.parseInt(obj[12].toString()));
			tipoOrgao.setNome(obj[13].toString());

			ramo.setId(Integer.parseInt(obj[14].toString()));
			ramo.setNome(obj[15].toString());

			tipoOrgao.setRamo(ramo);
			orgao.setTipoOrgao(tipoOrgao);

			lista.add(orgao);
		}

		return lista;

	}

	public Orgao recuperarOrgaoSuperior(Integer idOrgao) throws AtlasAcessoBancoDadosException {
		StringBuilder query = new StringBuilder();
		query.append("select orgaoSuperior from Orgao orgao where orgao.id = :idOrgao ");
		AtlasQuery atlasQuery = createQuery(query.toString());
		atlasQuery.setParameter("idOrgao", idOrgao);
		return (Orgao) atlasQuery.getSingleResult();
	}

	/**
	 * Recupera todos os órgãos relacionados ao usuário.
	 * No caso de um relaciomento com um órgão superior, será recuperado ele e seus subordinados.
	 *
	 * @param usuario
	 * @return {@link List}{@link Orgao}
	 * @throws AtlasAcessoBancoDadosException
	 */
	public List<Orgao> recuperarOrgaosRelacionadosESubordinados(Usuario usuario) throws AtlasAcessoBancoDadosException {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ORG.ID_ORGAO  ");
		sql.append(" FROM 	ATLAS.ORGAO ORG ");
		sql.append(" JOIN	ATLAS.ORGAO ORG_SUP ON ORG.ID_ORGAO_SUPERIOR = ORG_SUP.ID_ORGAO ");
		sql.append(" JOIN	SEGURANCA.ORGAO_USUARIO ORG_USU ON ORG_SUP.ID_ORGAO = ORG_USU.ID_ORGAO ");
		sql.append(" WHERE 	ORG_USU.ID_USUARIO = "+usuario.getId());
		sql.append(" UNION ");
		sql.append(" SELECT ORG.ID_ORGAO ");
		sql.append(" FROM 	ATLAS.ORGAO ORG ");
		sql.append(" JOIN	SEGURANCA.ORGAO_USUARIO ORG_USU ON ORG.ID_ORGAO = ORG_USU.ID_ORGAO ");
		sql.append(" WHERE 	ORG_USU.ID_USUARIO = "+usuario.getId());

		AtlasQuery atlasQuery = createNativeQuery(sql.toString());
		List<Orgao> lista = new ArrayList<>();

		for (Object obj : atlasQuery.getResultList()) {
			Orgao orgao = pesquisarPorId((Integer) obj, Orgao.class);
			lista.add(orgao);
		}
		return lista;
	}

	@SuppressWarnings("unchecked")
	public List<Orgao> recuperarOrgaosRelacionados(Usuario usuarioAutenticado) throws AtlasAcessoBancoDadosException {
		StringBuilder query = new StringBuilder();
		query.append("select orgao from Orgao orgao where :usuario in elements(orgao.usuarios)");
		AtlasQuery atlasQuery = createQuery(query.toString());
		atlasQuery.setParameter("usuario", usuarioAutenticado);
		return atlasQuery.getResultList();
	}

	@SuppressWarnings("unchecked")
	public ListaPaginada<Representante> recuperarDestinatarios(
			Representante representanteFiltro)
					throws AtlasAcessoBancoDadosException {

		ConsultaPaginada consultaPaginada = new ConsultaPaginada(
				representanteFiltro.getPrimeiroRegistro());

		Orgao orgao = representanteFiltro.getOrgao();

		StringBuilder query = new StringBuilder();

		query.append(" select org.nme_orgao, rep.nme_representante, rep.nme_email, rep.id_representante");
		query.append(" from representante_orgao rep ");
		query.append(" join orgao org on org.id_orgao = rep.id_orgao ");
		query.append(" and rep.nme_email is not null and trim(rep.nme_email) <> '' ");
		query.append(" join municipio municipio on org.id_municipio = municipio.id_municipio");
		query.append(" join uf uf on municipio.id_uf = uf.id_uf");

		if (orgao != null && orgao.getNome() != null) {
			query.append(" and upper(org.nme_orgao) like upper(:nomeOrgao)");
			consultaPaginada.getParametros().put("nomeOrgao", "%"+orgao.getNome().toUpperCase()+"%");
		}

		if (orgao != null && orgao.getTipoOrgao() != null) {
			query.append(" and org.id_tipo_orgao = ").append(orgao.getTipoOrgao().getId()).append(" ");
		}

		if (orgao != null && orgao.getUf() != null) {
			query.append(" and uf.id_uf = ").append(orgao.getUf().getId()).append(" ");
		}

		if (orgao != null && orgao.getMunicipio() != null) {
			query.append(" and org.id_municipio = ").append(orgao.getMunicipio().getId()).append(" ");
		}
		query.append(" order by org.nme_orgao");

		consultaPaginada.setQueryString(query.toString());

		ListaPaginada<Representante> lista = efetuarPesquisaPaginada(consultaPaginada);

		for (Object obj : lista.getResultadoPesquisa()) {
			Object[] item = (Object[]) obj;

			Orgao orgaoResposta = new Orgao();
			orgaoResposta.setNome((item[0] != null) ? String.valueOf(item[0]) : null);

			Representante representanteConsulta = new Representante();
			representanteConsulta.setOrgao(orgaoResposta);
			representanteConsulta.setNome((item[1] != null) ? String.valueOf(item[1]) : null);
			representanteConsulta.setEmail((item[2] != null) ? String.valueOf(item[2]) : null);
			representanteConsulta.setId((item[3] != null) ? Integer.valueOf(String.valueOf(item[3])) : null);

			lista.getResultadoRetorno().add(representanteConsulta);
		}

		return lista;
	}
}