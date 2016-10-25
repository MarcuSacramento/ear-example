package br.gov.atlas.dao;

import java.util.List;

import javax.persistence.EntityManager;

import br.gov.atlas.dao.arquitetura.AtlasQuery;
import br.gov.atlas.entidade.Anexo;
import br.gov.atlas.entidade.Mensageria;
import br.gov.atlas.exception.AtlasAcessoBancoDadosException;

public class AnexoDao extends AtlasDao<Anexo> {

	public AnexoDao(EntityManager em) {
		super(em);
	}

	public void salvar(List<Anexo> anexos) throws AtlasAcessoBancoDadosException {
		for (Anexo anexo : anexos) {
			salvar(anexo);
		}
	}

	@SuppressWarnings("unchecked")
	public List<Anexo> recuperarAnexosMensagem(Mensageria mensageria) throws AtlasAcessoBancoDadosException {
		StringBuilder query = new StringBuilder();
		query.append("SELECT anexo FROM Anexo anexo WHERE mensagem = :mensageria ");
		AtlasQuery atlasQuery = createQuery(query.toString());
		atlasQuery.setParameter("mensageria", mensageria);
		return atlasQuery.getResultList();
	}
	
}