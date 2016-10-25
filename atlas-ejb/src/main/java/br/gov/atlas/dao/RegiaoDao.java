package br.gov.atlas.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import br.gov.atlas.dao.arquitetura.AtlasQuery;
import br.gov.atlas.dto.OrgaoDTO;
import br.gov.atlas.entidade.Regiao;
import br.gov.atlas.entidade.UF;
import br.gov.atlas.exception.AtlasAcessoBancoDadosException;

public class RegiaoDao extends AtlasDao<Regiao> {

	/**
	 * Construtor da classe RegiaoDao
	 * @param em
	 */
	public RegiaoDao(EntityManager em) {
		super(em);
	}

	
	public List<OrgaoDTO> recuperarTipoOrgaoPorRegiao() throws AtlasAcessoBancoDadosException 
	{	
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT REG.NME_REGIAO, COUNT(*) AS TOTAL	 					");
		sql.append("FROM ORGAO ORG													");
		sql.append("JOIN TIPO_ORGAO TIPO ON ORG.ID_TIPO_ORGAO = TIPO.ID_TIPO_ORGAO 	");
		sql.append("JOIN MUNICIPIO MUN ON ORG.ID_MUNICIPIO = MUN.ID_MUNICIPIO 		");
		sql.append("JOIN UF UE ON MUN.ID_UF = UE.ID_UF  ");
		sql.append("JOIN REGIAO REG ON UE.ID_REGIAO = REG.ID_REGIAO 				");
		sql.append("GROUP BY REG.NME_REGIAO 										");

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
