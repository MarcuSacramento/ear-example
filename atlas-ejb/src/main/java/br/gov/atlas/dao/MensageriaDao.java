package br.gov.atlas.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

import br.gov.atlas.dao.arquitetura.AtlasQuery;
import br.gov.atlas.entidade.Mensageria;
import br.gov.atlas.entidade.MensageriaOrgao;
import br.gov.atlas.entidade.Orgao;
import br.gov.atlas.entity.ConsultaPaginada;
import br.gov.atlas.entity.ListaPaginada;
import br.gov.atlas.exception.AtlasAcessoBancoDadosException;

public class MensageriaDao extends AtlasDao<Mensageria> {

	public MensageriaDao(EntityManager em) {
		super(em);
	}

	@SuppressWarnings("unchecked")
	public ListaPaginada<Mensageria> recuperarMensagem(Mensageria mensagem)
			throws AtlasAcessoBancoDadosException {

		ConsultaPaginada consultaPaginada = new ConsultaPaginada(
				mensagem.getPrimeiroRegistro());

		StringBuilder query = new StringBuilder();

		query.append(" select mensageria.id_mensageria, mensageria.dta_envio, mensageria.dsc_assunto, mensageria.nme_remetente, mensageria.dsc_mensagem,");
		query.append(" count(mro.id_orgao) as totalDestinatários, count(mro.dta_visualizacao) as totalVisualizadas");
		query.append(" from mensageria_orgao mro");
		query.append(" join mensageria on mro.id_mensageria = mensageria.id_mensageria");
		query.append(" and mensageria.id_mensageria = mensageria.id_mensageria");

		if (mensagem != null && mensagem.getDataInicio() != null && mensagem.getDataFim() != null) {
			query.append(" where date_trunc('day', mensageria.dta_envio) between :dtaIni and :dtaFim");
			consultaPaginada.getParametros().put("dtaIni", mensagem.getDataInicio());
			consultaPaginada.getParametros().put("dtaFim", mensagem.getDataFim());
		}else if(mensagem.getDataInicio() != null && mensagem.getDataFim() == null){
			query.append(" where date_trunc('day', mensageria.dta_envio) >= :dtaIni");
			consultaPaginada.getParametros().put("dtaIni", mensagem.getDataInicio());
		}else if(mensagem.getDataInicio() == null && mensagem.getDataFim() != null){
			query.append(" where date_trunc('day', mensageria.dta_envio) <= :dtaFim");
			consultaPaginada.getParametros().put("dtaFim", mensagem.getDataFim());
		}

		query.append(" group by mensageria.id_mensageria, mensageria.dta_envio, mensageria.dsc_assunto, mensageria.nme_remetente, mensageria.dsc_mensagem");
		query.append(" order by mensageria.id_mensageria ");

		consultaPaginada.setQueryString(query.toString());

		ListaPaginada<Mensageria> lista = efetuarPesquisaPaginada(consultaPaginada);

		Mensageria mensagemConsulta = new Mensageria();

		for (Object obj : lista.getResultadoPesquisa()) {
			Object[] item = (Object[]) obj;

			mensagemConsulta = new Mensageria();
			mensagemConsulta.setId(new Integer(item[0].toString()));
			mensagemConsulta.setDataEnvio((Date) item[1]);
			mensagemConsulta.setAssunto((item[2] != null) ? String
					.valueOf(item[2]) : null);
			mensagemConsulta.setRemetente((item[3] != null) ? String
					.valueOf(item[3]) : null);
			mensagemConsulta.setMensagem((item[4] != null) ? String
					.valueOf(item[4]) : null);
			mensagemConsulta.setTotalDestinatarios(new Integer(item[5]
					.toString()));
			mensagemConsulta.setTotalAbertos(new Integer(item[6].toString()));

			lista.getResultadoRetorno().add(mensagemConsulta);
		}

		return lista;

	}

	@SuppressWarnings({ "unchecked" })
	public ListaPaginada<Mensageria> recuperarDestinatarios(Mensageria mensagem)
			throws AtlasAcessoBancoDadosException {

		ConsultaPaginada consultaPaginada = new ConsultaPaginada(
				mensagem.getPrimeiroRegistro());

		StringBuilder query = new StringBuilder();

		query.append(" select org.nme_orgao, rep.nme_representante, rep.nme_email");
		query.append(" from orgao org");
		query.append(" join representante_orgao rep on org.id_orgao = rep.id_orgao and rep.nme_email is not null");
		query.append(" join municipio municipio on org.id_municipio = municipio.id_municipio");
		query.append(" join uf uf on municipio.id_uf = uf.id_uf");

		if (mensagem != null && mensagem.getNomeOrgao() != null) {
			query.append(" and upper(org.nme_orgao) like :nomeOrgao");
			consultaPaginada.getParametros().put("nomeOrgao", mensagem.getNomeOrgao().toUpperCase());
		}

		if (mensagem != null && mensagem.getTipoOrgao() != null) {
			query.append(" and org.id_tipo_orgao = ").append(mensagem.getTipoOrgao().getId()).append(" ");
		}

		if (mensagem != null && mensagem.getUf() != null) {
			query.append(" and uf.id_uf = ").append(mensagem.getUf().getId()).append(" ");
		}

		if (mensagem != null && mensagem.getMunicipio() != null) {
			query.append(" and org.id_municipio = ").append(mensagem.getMunicipio().getId()).append(" ");
		}

		query.append(" group by org.nme_orgao, rep.nme_representante, rep.nme_email");
		query.append(" order by org.nme_orgao");

		consultaPaginada.setQueryString(query.toString());

		ListaPaginada<Mensageria> lista = efetuarPesquisaPaginada(consultaPaginada);

		Mensageria mensagemConsulta = new Mensageria();

		for (Object obj : lista.getResultadoPesquisa()) {
			Object[] item = (Object[]) obj;

			mensagemConsulta = new Mensageria();
			mensagemConsulta.setNomeOrgao((item[0] != null) ? String.valueOf(item[0]) : null);
			mensagemConsulta.setNomeDestinatario((item[1] != null) ? String.valueOf(item[1]) : null);
			mensagemConsulta.setEmailDestinatario((item[2] != null) ? String.valueOf(item[2]) : null);

			lista.getResultadoRetorno().add(mensagemConsulta);
		}

		return lista;

	}

	public void salvar(Mensageria mensageria,
			List<Orgao> destinatariosSelecionadosGeral)
					throws AtlasAcessoBancoDadosException {
		mensageria.setDataEnvio(new Date());
		mensageria = salvar(mensageria);
		Integer idMensageria = mensageria.getId();
		if(idMensageria != null){
			List<MensageriaOrgao> mensagensOrgaos = new ArrayList<>();
			for (Orgao orgaoDestinatario : destinatariosSelecionadosGeral) {
				MensageriaOrgao mensageriaOrgao = new MensageriaOrgao();
				mensageriaOrgao.setMensagem(mensageria);
				mensageriaOrgao.setOrgao(orgaoDestinatario);
				mensagensOrgaos.add(mensageriaOrgao);
			}
			mensageria.setMensagensOrgaos(mensagensOrgaos);

			salvar(mensageria);
		}else{
			remover(mensageria);
			throw new AtlasAcessoBancoDadosException("MSG17");
		}
	}

	@SuppressWarnings({ "unchecked" })
	public List<Mensageria> consultarDestinatarios(Mensageria mensagem)
			throws AtlasAcessoBancoDadosException {
		StringBuilder sql = new StringBuilder();

		sql.append(" select rep.nme_orgao, o.nme_representante, o.nme_email");
		sql.append(" from mensageria_orgao 	mro");
		sql.append(" join orgao 				rep on mro.id_orgao = rep.id_orgao");
		sql.append(" join representante_orgao o on o.id_orgao = rep.id_orgao");
		sql.append(" and 	mro.id_mensageria = :idMensageria");
		sql.append(" order by o.nme_representante");

		AtlasQuery query = createNativeQuery(sql.toString());
		query.setParameter("idMensageria", mensagem.getId());

		List<Mensageria> retorno = new ArrayList<Mensageria>();
		Mensageria mensagemConsulta = new Mensageria();

		for (Object obj : query.getResultList()) {
			Object[] item = (Object[]) obj;

			mensagemConsulta = new Mensageria();
			mensagemConsulta.setNomeOrgao((item[0] != null) ? String
					.valueOf(item[0]) : null);
			mensagemConsulta.setNomeDestinatario((item[1] != null) ? String
					.valueOf(item[1]) : null);
			mensagemConsulta.setEmailDestinatario((item[2] != null) ? String
					.valueOf(item[2]) : null);

			retorno.add(mensagemConsulta);
		}

		return retorno;
	}

}