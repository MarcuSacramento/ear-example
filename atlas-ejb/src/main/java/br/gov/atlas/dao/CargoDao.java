package br.gov.atlas.dao;

import javax.persistence.EntityManager;

import br.gov.atlas.dao.arquitetura.AtlasQuery;
import br.gov.atlas.entidade.Cargo;
import br.gov.atlas.entity.ConsultaPaginada;
import br.gov.atlas.entity.ListaPaginada;
import br.gov.atlas.exception.AtlasAcessoBancoDadosException;

public class CargoDao extends AtlasDao<Cargo> {

	public CargoDao(EntityManager em) {
		super(em);
	}

	@SuppressWarnings("unchecked")
	public ListaPaginada<Cargo> recuperarCargos(Cargo cargo)
			throws AtlasAcessoBancoDadosException {
		
		ConsultaPaginada consultaPaginada = new ConsultaPaginada(cargo.getPrimeiroRegistro());
		
		StringBuilder query = new StringBuilder();
		
		query.append("select cargo.id_cargo, cargo.nme_tratamento, cargo.nme_cargo ");
		query.append("from atlas.cargo as cargo ");

		if (cargo != null && cargo.getNome() != null) {
			query.append("where upper(cargo.nme_cargo) like :nome ");
			consultaPaginada.getParametros().put("nome",
					cargo.getNome().toUpperCase());
		}
		
		if (cargo != null && cargo.getTratamento() != null) {
			query.append("where upper(cargo.nme_tratamento) like :tratamento ");
			consultaPaginada.getParametros().put("tratamento",
					cargo.getTratamento().toUpperCase());
		}
		
		query.append("order by cargo.nme_cargo ");
		consultaPaginada.setQueryString(query.toString());

		ListaPaginada<Cargo> lista = efetuarPesquisaPaginada(consultaPaginada);

		Cargo cargoConsulta = new Cargo();

		for (Object obj : lista.getResultadoPesquisa()) {
			Object[] item = (Object[]) obj;

			cargoConsulta = new Cargo();
			cargoConsulta.setId(new Integer(item[0].toString()));
			cargoConsulta.setTratamento((item[1] != null) ? String
					.valueOf(item[1]) : null);
			cargoConsulta.setNome((item[2] != null) ? String.valueOf(item[2])
					: null);

			lista.getResultadoRetorno().add(cargoConsulta);
		}

		return lista;
	}

	public Cargo recuperPorNome(String nomeCargo)
			throws AtlasAcessoBancoDadosException {
		StringBuilder query = new StringBuilder();
		query.append("select cargo from Cargo cargo where upper(cargo.nome) = :nomeCargo");
		AtlasQuery atlasQuery = createQuery(query.toString());
		atlasQuery.setParameter("nomeCargo", nomeCargo.toUpperCase());
		return (Cargo) atlasQuery.getSingleResult();
	}
	
	public Boolean isCargoAssociadoAAlgumRepresentante(Cargo cargo) throws AtlasAcessoBancoDadosException 
	{
		StringBuilder queryString = new StringBuilder();	
		queryString.append(" SELECT 'TRUE'  FROM representante_orgao "									 ); 
		queryString.append(" 		WHERE EXISTS (SELECT id_cargo FROM  representante_orgao WHERE id_cargo = " ).append  ( cargo.getId() 	);
		queryString.append(" 	 ) "																	 );
		queryString.append("     UNION "															 	 );
		queryString.append(" SELECT 'FALSE' FROM representante_orgao "									 );
		queryString.append(" 		WHERE NOT EXISTS (SELECT id_cargo FROM  representante_orgao WHERE id_cargo = " ).append  ( cargo.getId() );
		queryString.append(" 	 ) "																	 );
		queryString.append(" limit 1 " 																	 );

		AtlasQuery atlasQuery = createNativeQuery(queryString.toString());

		Boolean isJaAssociado = null;
		String valor = atlasQuery.getSingleResult().toString();
		if(valor != null && !valor.equals("")){
			isJaAssociado = new Boolean(valor);
		}
		return isJaAssociado;

	}
}

