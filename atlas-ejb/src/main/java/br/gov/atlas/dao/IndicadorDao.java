package br.gov.atlas.dao;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import br.gov.atlas.dao.arquitetura.AtlasQuery;
import br.gov.atlas.dto.IndiceDTO;
import br.gov.atlas.dto.PopulacaDTO;
import br.gov.atlas.dto.TipoOrgaoProfissaoDTO;
import br.gov.atlas.entidade.Indicador;
import br.gov.atlas.entidade.Ramo;
import br.gov.atlas.entidade.Regiao;
import br.gov.atlas.entidade.TipoOrgao;
import br.gov.atlas.entidade.UF;
import br.gov.atlas.exception.AtlasAcessoBancoDadosException;

/**
 * Classe DAO para tratar os acessos as informações do Indicador no banco de dados.
 */
public class IndicadorDao extends AtlasDao<Indicador> {

	/**
	 * Construtor da classe IndicadorDao
	 * @param em
	 */
	public IndicadorDao(EntityManager em) {
		super(em);
	}
	
	public List<IndiceDTO> buscarIndices(String param, UF uf, Regiao reg) throws AtlasAcessoBancoDadosException{
		StringBuffer query = new StringBuffer();
		query.append(" 	SELECT %s IND.NME_INDICE,  ");
		query.append(" 	  CASE ");
		query.append(" 	    WHEN IND.AGG_INDICE = 'SUM' THEN ");
		query.append(" 		SUM(INDUF.VAL_INDICE) ");
		query.append(" 	    ELSE ");
		query.append(" 	        ROUND(AVG(INDUF.VAL_INDICE),2) ");
		query.append(" 	  END VALOR ");
		query.append(" 	FROM ATLAS.INDICE_UF INDUF  ");		
		query.append(" 	JOIN ATLAS.UF ON UF.ID_UF = INDUF.ID_UF  ");	
		if(uf != null){
			query.append(" 	AND UF.ID_UF = "+uf.getId());	
		}
		query.append(" 	JOIN ATLAS.INDICE IND ON IND.ID_INDICE = INDUF.ID_INDICE "); 
		query.append(" 	JOIN ATLAS.REGIAO REG ON REG.ID_REGIAO = UF.ID_REGIAO	 "); 
		if(reg != null){
			query.append(" 	AND REG.ID_REGIAO = "+reg.getId());	
		}
		query.append(" 	WHERE IND.GRP_INDICE = 'DSP'  ");
		query.append(" 	GROUP BY %s IND.NME_INDICE, IND.AGG_INDICE ");
		
		query.append(" 	UNION ALL ");
				
		query.append(" 	SELECT %s 'INAJ' NME_INDICE, ");
		query.append(" 	ROUND(AVG(((CAST((UNI.QT_UNI+OPE.QT_OP) AS NUMERIC))/POP.QT_POP)*100),2) VL_INAJ ");
		query.append(" 	FROM ");
		query.append(" 	(SELECT  R.NME_REGIAO, U.COD_UF, COUNT(ID_ORGAO) QT_UNI ");
		query.append(" 	FROM ATLAS.ORGAO O ");
		query.append(" 	JOIN ATLAS.MUNICIPIO M ON M.ID_MUNICIPIO = O.ID_MUNICIPIO ");
		query.append(" 	JOIN ATLAS.UF U ON U.ID_UF = M.ID_UF ");
		if(uf != null){
			query.append(" 	AND U.ID_UF = "+uf.getId());	
		}
		query.append(" 	JOIN ATLAS.REGIAO R ON R.ID_REGIAO = U.ID_REGIAO	  ");
		if(reg != null){
			query.append(" 	AND R.ID_REGIAO = "+reg.getId());	
		}
		query.append(" 	GROUP BY R.NME_REGIAO, U.COD_UF ");
		query.append(" 	) AS UNI, ");

		query.append(" 	(SELECT OP.ANO_BASE, R.NME_REGIAO, U.COD_UF, SUM(OP.QTD_OPERADORES) QT_OP ");
		query.append(" 	FROM ATLAS.TIPO_ORGAO_PROFISSAO OP ");
		query.append(" 	JOIN ATLAS.UF U ON U.ID_UF = OP.ID_UF ");
		if(uf != null){
			query.append(" 	AND U.ID_UF = "+uf.getId());	
		}
		query.append(" 	JOIN ATLAS.REGIAO R ON R.ID_REGIAO = U.ID_REGIAO	  ");
		if(reg != null){
			query.append(" 	AND R.ID_REGIAO = "+reg.getId());	
		}
		query.append(" 	WHERE ");
		query.append(" 	    OP.ANO_BASE =  (SELECT MAX(ANO_BASE) FROM ATLAS.TIPO_ORGAO_PROFISSAO) ");
		query.append(" 	GROUP BY OP.ANO_BASE,R.NME_REGIAO,U.COD_UF) AS OPE, ");

		query.append(" 	(SELECT PO.ANO, R.NME_REGIAO, U.COD_UF, SUM(QTD_POPULACAO) QT_POP ");
		query.append(" 	FROM ATLAS.POPULACAO PO ");
		query.append(" 	JOIN ATLAS.MUNICIPIO M ON M.ID_MUNICIPIO = PO.ID_MUNICIPIO ");
		query.append(" 	JOIN ATLAS.UF U ON U.ID_UF = M.ID_UF  ");
		if(uf != null){
			query.append(" 	AND U.ID_UF = "+uf.getId());	
		}
		query.append(" 	JOIN ATLAS.REGIAO R ON R.ID_REGIAO = U.ID_REGIAO ");
		if(reg != null){
			query.append(" 	AND R.ID_REGIAO = "+reg.getId());	
		}
		query.append(" 	WHERE ");
		query.append(" 	   PO.ANO =  (SELECT MAX(ANO) FROM ATLAS.POPULACAO) ");
		query.append(" 	GROUP BY PO.ANO,R.NME_REGIAO,U.COD_UF ");
		query.append(" 	ORDER BY PO.ANO) AS POP, ");

		query.append(" 	(SELECT I.ID_ANO,R.NME_REGIAO, U.COD_UF, I.VAL_INDICE VL_IDH ");
		query.append(" 	FROM ATLAS.INDICE_UF I ");
		query.append(" 	JOIN ATLAS.UF U ON U.ID_UF = I.ID_UF ");
		if(uf != null){
			query.append(" 	AND U.ID_UF = "+uf.getId());	
		}
		query.append(" 	JOIN ATLAS.REGIAO R ON R.ID_REGIAO = U.ID_REGIAO ");	
		if(reg != null){
			query.append(" 	AND R.ID_REGIAO = "+reg.getId());	
		}
		query.append(" 	WHERE I.ID_INDICE = 1 AND ");
		query.append(" 	      I.ID_ANO =  (SELECT MAX(ID_ANO) FROM ATLAS.INDICE_UF ) ");
		query.append(" 	) AS IDH ");
		query.append(" 	WHERE UNI.COD_UF = OPE.COD_UF AND ");
		query.append(" 	      OPE.COD_UF = POP.COD_UF AND ");
		query.append(" 	      POP.COD_UF = IDH.COD_UF ");
		query.append(" 	GROUP BY %s NME_INDICE ");
		query.append(" 	ORDER BY  NME_INDICE ");
		AtlasQuery atlasQuery = createNativeQuery(String.format(query.toString(), 
														param,
														param,
														param.replace("REG.", "UNI.").replace("UF.", "UNI."),
														param.replace("REG.", "UNI.").replace("UF.", "UNI.")));
		List<Object[]> resultado = atlasQuery.getResultList();
		List<IndiceDTO> listaRetorno = new ArrayList<>();
		List<IndiceDTO> indices = new ArrayList<>();
		String unidade = null;
		if(param.isEmpty()){
			return getIndicadoresBrasil(resultado);
		}
		for (Object[] obj : resultado) {
			String regiao = obj[0].toString();
			if(unidade == null){
				unidade = regiao;
			}else if(!unidade.equals(regiao)){	
				listaRetorno.add(new IndiceDTO(unidade, indices));
				unidade = regiao;
				indices = new ArrayList<>();
			}	
			String nome = obj[1].toString();
			Double valor= Double.parseDouble(obj[2].toString());
			indices.add(new IndiceDTO(nome, valor));
			if(nome.equals("IDH")){
				
			}
		}
		listaRetorno.add(new IndiceDTO(unidade, indices));

		return listaRetorno;
		
	}

	private List<IndiceDTO> getIndicadoresBrasil(List<Object[]> resultado) {
		List<IndiceDTO> indices = new ArrayList<>();
		for (Object[] obj : resultado) {			
			String nome = obj[0].toString();
			Double valor= Double.parseDouble(obj[1].toString());
			indices.add(new IndiceDTO(nome, valor));				
		}
		
		return indices;
	}
	
	public List<TipoOrgao> buscarOperadoresBrasilRegiaoUf(Regiao regiao, UF uf) throws AtlasAcessoBancoDadosException{
		StringBuffer query = new StringBuffer();
		query.append(" 	SELECT OP.ANO_BASE, RM.ID_RAMO,RM.NME_RAMO,TP.NME_TIPO_ORGAO, SUM(QTD_OPERADORES)  ");
		query.append(" 	FROM ATLAS.TIPO_ORGAO_PROFISSAO OP ");
		query.append(" 	JOIN ATLAS.TIPO_ORGAO TP ON TP.ID_TIPO_ORGAO = OP.ID_TIPO_ORGAO ");
		query.append(" 	JOIN ATLAS.RAMO RM ON RM.ID_RAMO = TP.ID_RAMO ");
		query.append(" 	JOIN ATLAS.UF ON UF.ID_UF = OP.ID_UF  ");
		if (uf != null) {
			query.append(" AND UF.ID_UF ="+uf.getId());
		}
		query.append(" 	JOIN ATLAS.REGIAO R ON R.ID_REGIAO = UF.ID_REGIAO ");
		if (regiao != null) {
			query.append(" AND R.ID_REGIAO ="+regiao.getId());
		}
		query.append(" 	WHERE ");
		query.append(" 	    OP.ANO_BASE =  (SELECT MAX(ANO_BASE) FROM ATLAS.TIPO_ORGAO_PROFISSAO) ");
		query.append(" 	GROUP BY OP.ANO_BASE,RM.ID_RAMO,RM.NME_RAMO,TP.NME_TIPO_ORGAO ");
		query.append(" 	ORDER BY OP.ANO_BASE,RM.ID_RAMO, TP.NME_TIPO_ORGAO; ");

		AtlasQuery atlasQuery = createNativeQuery(query.toString());
		List<Object[]> resultado = atlasQuery.getResultList();
		List<TipoOrgaoProfissaoDTO> listaOperadores = new ArrayList<TipoOrgaoProfissaoDTO>();
		List<TipoOrgao> listaTipos = new ArrayList<TipoOrgao>();
		TipoOrgao tipoOrgao = null;
		for (Object[] obj : resultado) {
			Integer ano = (Integer) obj[0];
			Integer idRamo = (Integer) obj[1];
			String nmeRamo = obj[2].toString();
			String tipo	= obj[3].toString();
			BigInteger valor 	= (BigInteger) obj[4];
			if(valor == null || valor.equals(BigInteger.ZERO)){
				continue;
			}
			Ramo ramo = new Ramo();
			ramo.setId(idRamo);
			ramo.setNome(nmeRamo);
			if(tipoOrgao == null){
				tipoOrgao = new TipoOrgao();
				tipoOrgao.setRamo(ramo);				
			}else if (!tipoOrgao.getRamo().equals(ramo)){
				tipoOrgao.setOperadores(listaOperadores);
				listaTipos.add(tipoOrgao);
				tipoOrgao = new TipoOrgao();
				tipoOrgao.setRamo(ramo);
				listaOperadores =  new ArrayList<TipoOrgaoProfissaoDTO>();
			}
			listaOperadores.add(new TipoOrgaoProfissaoDTO(ano, tipo, null,uf, valor));
		}
		tipoOrgao.setOperadores(listaOperadores);
		listaTipos.add(tipoOrgao);

		return listaTipos;

	}

	public List<PopulacaDTO> buscarPopulacaoBrasilRegiaoUf(Regiao regiao, UF uf, int ano) throws AtlasAcessoBancoDadosException{
		StringBuffer query = new StringBuffer();
		query.append(" 	SELECT POP.ANO, U.COD_UF, SUM(QTD_POPULACAO)  ");
		query.append(" 	FROM ATLAS.POPULACAO POP ");
		query.append(" 	JOIN ATLAS.UF U ON U.ID_UF = POP.ID_UF ");
		if (uf != null) {
			query.append(" AND UF.ID_UF ="+uf.getId());
		}
		query.append(" 	WHERE POP.ANO = "+ano);
		query.append(" 	GROUP BY POP.ANO, U.COD_UF ");
		query.append(" 	ORDER BY POP.ANO, U.COD_UF ");
		
		AtlasQuery atlasQuery = createNativeQuery(query.toString());
		List<Object[]> resultado = atlasQuery.getResultList();
		List<PopulacaDTO> listaPopupalacao = new ArrayList<PopulacaDTO>();
		for (Object[] obj : resultado) {
			PopulacaDTO populacao = new PopulacaDTO();
			populacao.setAno((int) obj[0]);
			populacao.setUf(obj[1].toString());
			populacao.setQuantidade((int) obj[2]);
			listaPopupalacao.add(populacao);
		}

		return listaPopupalacao;

	}



}
