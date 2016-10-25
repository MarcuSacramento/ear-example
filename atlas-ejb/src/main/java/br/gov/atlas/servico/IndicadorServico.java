package br.gov.atlas.servico;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import br.gov.atlas.dao.IndicadorDao;
import br.gov.atlas.dto.GraficoDTO;
import br.gov.atlas.dto.IndiceDTO;
import br.gov.atlas.entidade.Indicador;
import br.gov.atlas.entidade.Ramo;
import br.gov.atlas.entidade.Regiao;
import br.gov.atlas.entidade.TipoOrgao;
import br.gov.atlas.entidade.UF;
import br.gov.atlas.exception.AtlasAcessoBancoDadosException;
import br.gov.atlas.exception.indicadores.AnaliseLexicaFormulaException;
import br.gov.atlas.exception.indicadores.AnaliseSintaticaFormulaException;
import br.gov.atlas.util.CompiladorFormula;

/**
 * Classe de servico para a entidade Indicador
 */
@Stateless(name = "IndicadorServico")
@LocalBean
public class IndicadorServico extends AtlasServicoImpl {

	@EJB(name = "ParametroServico")
	private ParametroServico parametroServico;

	/*
	 * (non-Javadoc)
	 * @see br.gov.atlas.servico.AtlasServicoImpl#getDao()
	 */
	@Override
	public IndicadorDao getDao() {
		return new IndicadorDao(em);
	}

	/**
	 * Busca todos os indicadores, validando as suas respectivas formulas.
	 * 
	 * @return lista de indicadores
	 * 
	 * @throws AtlasAcessoBancoDadosException
	 */
	@SuppressWarnings("unchecked")
	public List<Indicador> buscarTodos() throws AtlasAcessoBancoDadosException {
		List<Indicador> indicadores = super.buscarTodos(Indicador.class, "sigla");
		List<Indicador> indicadoresValidos = new ArrayList<>();
		
		for (Indicador indicador : indicadores) {
			CompiladorFormula compilador = new CompiladorFormula(parametroServico);
			try {
				compilador.compilarFormula(indicador);
				
				indicadoresValidos.add(indicador);
			} catch (AnaliseLexicaFormulaException | AnaliseSintaticaFormulaException e) {
				System.out.println(e.getMessage());
			}
		}
		
		return indicadoresValidos;
	}
	
	public List<IndiceDTO> buscarIndicesBrasil() throws AtlasAcessoBancoDadosException {
		return buscarIndices("", null, null);
	}
	
	public List<IndiceDTO> buscarIndicesPorRegiao(Regiao reg) throws AtlasAcessoBancoDadosException{
		return buscarIndices("REG.NME_REGIAO,", null, reg);
	}	
	
	public List<IndiceDTO> buscarIndicesPorUf(UF uf) throws AtlasAcessoBancoDadosException{
		return buscarIndices("UF.COD_UF,", uf, null);
	}
	
	public List<IndiceDTO> buscarIndices(String param, UF uf, Regiao reg) throws AtlasAcessoBancoDadosException{
		return getDao().buscarIndices(param, uf, reg);
	}
	
	public List<TipoOrgao> buscarOperadoresBrasil() throws AtlasAcessoBancoDadosException{
		return getDao().buscarOperadoresBrasilRegiaoUf(null,null);
	}

	public List<TipoOrgao> buscarOperadoresPorRegiao(Regiao regiao) throws AtlasAcessoBancoDadosException{
		return getDao().buscarOperadoresBrasilRegiaoUf(regiao,null);
	}

	public List<TipoOrgao> buscarOperadoresPorEstado(UF uf) throws AtlasAcessoBancoDadosException{
		return getDao().buscarOperadoresBrasilRegiaoUf(null,uf);
	}

	public int contarPorRamo(Ramo ramo) throws AtlasAcessoBancoDadosException {
		return 0;//TODO CESAR getDao().contarPorRamo(ramo);
	}
}
