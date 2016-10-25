package br.gov.atlas.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

import br.gov.atlas.dao.arquitetura.AtlasQuery;
import br.gov.atlas.entidade.Servico;
import br.gov.atlas.exception.AtlasAcessoBancoDadosException;

public class TematresDao extends AtlasDao<Servico> {
	
	public TematresDao(EntityManager em) {
		super(em);
	}

	public List<Servico> recuperarServicos() throws AtlasAcessoBancoDadosException {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT TEMA_ID, TEMA, CUANDO, CUANDO_ESTADO ");
		sql.append(" FROM tematres.vocab_tema tema ");
		sql.append(" WHERE tema.isMetaTerm = 1;");

		AtlasQuery atlasQuery = createNativeQuery(sql.toString());
		List<Servico> lista = new ArrayList<>();

		for (Object obj : atlasQuery.getResultList()) {
			Object[] item = (Object[]) obj;

			Servico servico = new Servico();
			servico.setId(new Integer(item[0].toString()));
			servico.setNome(String.valueOf(item[1]));
			servico.setDataCadastro((Date) item[2]);
			servico.setDataAtualizacao((Date) item[3]);

			lista.add(servico);
		}
		return lista;

	}
}