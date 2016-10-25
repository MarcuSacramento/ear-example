package br.gov.atlas.dao;

import javax.persistence.EntityManager;

import br.gov.atlas.entidade.Tema;
import br.gov.atlas.entity.ConsultaPaginada;
import br.gov.atlas.entity.ListaPaginada;
import br.gov.atlas.exception.AtlasAcessoBancoDadosException;

public class TemaDao extends AtlasDao<Tema> {
	
	public TemaDao(EntityManager em) {
		super(em);
	}

	@SuppressWarnings("unchecked")
	public ListaPaginada<Tema> recuperarTemas(Tema tema) throws AtlasAcessoBancoDadosException {
		ConsultaPaginada consultaPaginada = new ConsultaPaginada(tema.getPrimeiroRegistro());
		StringBuilder query = new StringBuilder();
		query.append("select tema.id_tema, tema.nme_tema, tema.dta_cadastro ");
		query.append("from atlas.tema as tema ");

		if (tema != null && tema.getNome() != null) {
			query.append("where upper(tema.nme_tema) like :nome ");
			consultaPaginada.getParametros().put("nome", tema.getNome().toUpperCase());
		}
		query.append("order by tema.nme_tema ");
		consultaPaginada.setQueryString(query.toString());

		ListaPaginada<Tema> lista = efetuarPesquisaPaginada(consultaPaginada);

		Tema temaConsulta = new Tema();

		for (Object obj : lista.getResultadoPesquisa()) {
			Object[] item = (Object[]) obj;

			temaConsulta = new Tema();
			temaConsulta.setId(new Integer(item[0].toString()));
			temaConsulta.setNome(String.valueOf(item[1]));

			lista.getResultadoRetorno().add(temaConsulta);
		}

		return lista;
	}
}