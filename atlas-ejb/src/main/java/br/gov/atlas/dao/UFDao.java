package br.gov.atlas.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import br.gov.atlas.dao.arquitetura.AtlasQuery;
import br.gov.atlas.dto.OrgaoDTO;
import br.gov.atlas.entidade.Municipio;
import br.gov.atlas.entidade.UF;
import br.gov.atlas.exception.AtlasAcessoBancoDadosException;

public class UFDao extends AtlasDao<Municipio> {

	public UFDao(EntityManager em) {
		super(em);
	}

	public UF recuperarUFPorSigla(String sigla) throws AtlasAcessoBancoDadosException {
		StringBuilder query = new StringBuilder();
		query.append("select uf from UF uf where uf.sigla = :sigla");
		AtlasQuery atlasQuery = createQuery(query.toString());
		atlasQuery.setParameter("sigla", sigla);
		return (UF) atlasQuery.getSingleResult();
	}
	
	public List<UF> recuperarUFsPorRegiao(Integer idRegiao) throws AtlasAcessoBancoDadosException {
		StringBuilder query = new StringBuilder();
		query.append("select uf from UF uf where regiao.id = :idRegiao");
		AtlasQuery atlasQuery = createQuery(query.toString());
		atlasQuery.setParameter("idRegiao", idRegiao);
		return (List<UF>) atlasQuery.getResultList(); 
	}

	@SuppressWarnings("unchecked")
	public List<UF> recuperarTodasAsUFs() throws AtlasAcessoBancoDadosException {
		StringBuilder query = new StringBuilder();
		query.append("select uf from UF uf order by uf.sigla");
		AtlasQuery atlasQuery = createQuery(query.toString());
		return (List<UF>) atlasQuery.getResultList(); 
	}
	
	public List<OrgaoDTO> recuperarTipoOrgaoPorUF(String nomeRegiao) throws AtlasAcessoBancoDadosException 
	{	
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT UE.COD_UF, COUNT(*) AS TOTAL	 					");
		sql.append(" FROM ORGAO ORG													");
		sql.append(" JOIN TIPO_ORGAO TIPO ON ORG.ID_TIPO_ORGAO = TIPO.ID_TIPO_ORGAO 	");
		sql.append(" JOIN MUNICIPIO MUN ON ORG.ID_MUNICIPIO = MUN.ID_MUNICIPIO 		");
		sql.append(" JOIN UF UE ON MUN.ID_UF = UE.ID_UF  ");
		sql.append(" JOIN REGIAO REG ON UE.ID_REGIAO = REG.ID_REGIAO AND REG.NME_REGIAO = '"+nomeRegiao+"'");
		sql.append(" GROUP BY UE.COD_UF 										");

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

}
