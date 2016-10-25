package br.gov.atlas.dao;

import javax.persistence.EntityManager;

import br.gov.atlas.entidade.Pessoa;
import br.gov.atlas.entity.ConsultaPaginada;
import br.gov.atlas.entity.ListaPaginada;
import br.gov.atlas.exception.AtlasAcessoBancoDadosException;

public class PessoaDao extends AtlasDao<Pessoa>{

	public PessoaDao(EntityManager em) {
		super(em);
	}
	
	@SuppressWarnings("unchecked")
	public ListaPaginada<Pessoa> recuperarPessoa(Pessoa pessoa) throws AtlasAcessoBancoDadosException {
		ConsultaPaginada consultaPaginada = new ConsultaPaginada(pessoa.getPrimeiroRegistro());
		StringBuilder query = new StringBuilder();
		query.append("SELECT id_pessoa, nme_pessoa, cod_email, cod_cpf, cod_ddd, cod_telefone, ind_situacao, ind_sexo FROM org.pessoa ");
		
		if(pessoa.getNome() != null){
			query.append("WHERE UPPER(nme_pessoa) like :nome");
			consultaPaginada.getParametros().put("nome", pessoa.getNome().toUpperCase());
		}
		
		consultaPaginada.setQueryString(query.toString());
		ListaPaginada<Pessoa> lista = efetuarPesquisaPaginada(consultaPaginada);

		for (Object obj : lista.getResultadoPesquisa()) {
			Object[] item = (Object[]) obj;
			
			Pessoa p = new Pessoa();
			p.setId(new Integer(item[0].toString()));
			p.setNome(String.valueOf(item[1]));
			p.setEmail(String.valueOf(item[2]));
			p.setCpf(String.valueOf(item[3]));
			p.setDdd(String.valueOf(item[4]));
			p.setTelefone(String.valueOf(item[5]));
			p.setSituacao(String.valueOf(item[6]));
			p.setSexo(String.valueOf(item[7]));
			
			lista.getResultadoRetorno().add(p);
		}
		return lista;
	}
}
