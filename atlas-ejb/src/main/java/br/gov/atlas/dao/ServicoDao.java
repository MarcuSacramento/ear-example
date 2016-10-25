package br.gov.atlas.dao;

import java.util.List;

import javax.persistence.EntityManager;

import br.gov.atlas.entidade.Servico;
import br.gov.atlas.entity.ConsultaPaginada;
import br.gov.atlas.entity.ListaPaginada;
import br.gov.atlas.exception.AtlasAcessoBancoDadosException;

public class ServicoDao extends AtlasDao<Servico> {
	
	public ServicoDao(EntityManager em) {
		super(em);
	}

	@SuppressWarnings("unchecked")
	public ListaPaginada<Servico> recuperarServicos(Servico servico) throws AtlasAcessoBancoDadosException {
		ConsultaPaginada consultaPaginada = new ConsultaPaginada(servico.getPrimeiroRegistro());
		StringBuilder query = new StringBuilder();
		query.append("select servico.id_servico, servico.nme_servico ");
		query.append("from atlas.servico as servico ");

		if (servico != null && servico.getNome() != null) {
			query.append("where upper(servico.nme_servico) like :nome ");
			consultaPaginada.getParametros().put("nome", servico.getNome().toUpperCase());
		}
		query.append("order by servico.nme_servico ");
		consultaPaginada.setQueryString(query.toString());

		ListaPaginada<Servico> lista = efetuarPesquisaPaginada(consultaPaginada);

		Servico servicoConsulta = new Servico();

		for (Object obj : lista.getResultadoPesquisa()) {
			Object[] item = (Object[]) obj;

			servicoConsulta = new Servico();
			servicoConsulta.setId(new Integer(item[0].toString()));
			servicoConsulta.setNome(String.valueOf(item[1]));

			lista.getResultadoRetorno().add(servicoConsulta);
		}

		return lista;
	}
	
	public void mergeTematresServico(List<Servico> servicos){
		if(servicos!=null){
			for (Servico servico : servicos) {
				try {
					this.merge(servico);
				} catch (AtlasAcessoBancoDadosException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}			
		}
	}
}