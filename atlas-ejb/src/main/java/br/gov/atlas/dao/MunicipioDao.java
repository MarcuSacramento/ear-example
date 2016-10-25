package br.gov.atlas.dao;

import java.util.List;

import javax.persistence.EntityManager;

import br.gov.atlas.dao.arquitetura.AtlasQuery;
import br.gov.atlas.entidade.Municipio;
import br.gov.atlas.entidade.UF;
import br.gov.atlas.exception.AtlasAcessoBancoDadosException;

public class MunicipioDao extends AtlasDao<Municipio> {

	public MunicipioDao(EntityManager em) {
		super(em);
	}

	public List<Municipio> recuperarMunicipios(UF uf) throws AtlasAcessoBancoDadosException {
		StringBuilder query = new StringBuilder();
		query.append("select municipio from Municipio municipio where municipio.uf = :uf order by municipio.nome");
		AtlasQuery atlasQuery = createQuery(query.toString());
		atlasQuery.setParameter("uf", uf);
		return atlasQuery.getResultList();
	}

	public List<Municipio> recuperarMunicipiosSemCoordenadas() throws AtlasAcessoBancoDadosException {
		StringBuilder query = new StringBuilder();
		query.append("select municipio from Municipio municipio where municipio.latitude is null");
		AtlasQuery atlasQuery = createQuery(query.toString());
		return atlasQuery.getResultList();
	}

	public Municipio recuperPorNomeMunicipioSiglaUf(String nomeMunicipio,
			String siglaUf) throws AtlasAcessoBancoDadosException {
		StringBuilder query = new StringBuilder();
		query.append("select municipio from Municipio municipio where upper(municipio.nome) = :nomeMunicipio AND upper(municipio.uf.sigla) = :siglaUf");
		AtlasQuery atlasQuery = createQuery(query.toString());
		atlasQuery.setParameter("nomeMunicipio", nomeMunicipio.toUpperCase());
		atlasQuery.setParameter("siglaUf", siglaUf.toUpperCase());
		return (Municipio) atlasQuery.getSingleResult();
	}

}
