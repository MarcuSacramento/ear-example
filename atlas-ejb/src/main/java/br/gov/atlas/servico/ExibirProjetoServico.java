package br.gov.atlas.servico;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import br.gov.atlas.dao.ProjetoDao;
import br.gov.atlas.entidade.Projeto;
import br.gov.atlas.entity.ListaPaginada;
import br.gov.atlas.entity.ProjetoLinhaComTresRegistrosAdapter;
import br.gov.atlas.exception.AtlasAcessoBancoDadosException;
import br.gov.atlas.servico.ExibirGlossarioServico.LadoProximoRegistro;

@Stateless(name="ExibirProjetoServico")
@LocalBean
public class ExibirProjetoServico extends AtlasServicoImpl {

	private ProjetoDao dao;
	
	@Override
	public ProjetoDao getDao() {
		if(dao==null)
		   dao = new ProjetoDao(em);
		return dao;
	}
	
	public List<Projeto> recuperarProjetos(Integer page) throws AtlasAcessoBancoDadosException {
		return getDao().recuperarProjetos(page);
	}
	
	public List<ProjetoLinhaComTresRegistrosAdapter> recuperarProjetosComLinhasDeTresRegistros(String page) throws AtlasAcessoBancoDadosException {
		Integer pageAsInteger = Integer.parseInt(page); 
		return recuperarProjetosComLinhasDeTresRegistros(pageAsInteger); 
	}
	
	public List<ProjetoLinhaComTresRegistrosAdapter> recuperarProjetosComLinhasDeTresRegistros(Integer page) throws AtlasAcessoBancoDadosException {
		List<Projeto> listaDeProjetos = recuperarProjetos(page);
		LadoProximoRegistro ladoProximoRegistro = LadoProximoRegistro.ESQUERDA; 
		ProjetoLinhaComTresRegistrosAdapter estaLinhaComTresRegistros = new ProjetoLinhaComTresRegistrosAdapter();
		
		List<ProjetoLinhaComTresRegistrosAdapter> resultado = new ArrayList<>();
		
		for(Projeto registroCorrente : listaDeProjetos){
			//ESQUERDA
			if( ladoProximoRegistro.equals(LadoProximoRegistro.ESQUERDA) ) {
				ladoProximoRegistro = LadoProximoRegistro.CENTRO; 
				estaLinhaComTresRegistros = new ProjetoLinhaComTresRegistrosAdapter();
				resultado.add(estaLinhaComTresRegistros); 
				estaLinhaComTresRegistros.setRegistroEsquerda(registroCorrente);	
			//CENTRO	
			}else if ( ladoProximoRegistro.equals(LadoProximoRegistro.CENTRO) ) {
				ladoProximoRegistro = LadoProximoRegistro.DIREITA;
				estaLinhaComTresRegistros.setRegistroCentro(registroCorrente);
			//DIREITA
			} else { 
				ladoProximoRegistro = LadoProximoRegistro.ESQUERDA; 
				estaLinhaComTresRegistros.setRegistroDireita(registroCorrente);
			}
		}
		return resultado; 
	}
	
	public ListaPaginada<Projeto> recuperarProjetos(Projeto filtro) throws AtlasAcessoBancoDadosException {
		return getDao().recuperarProjetos(filtro);
	}
	
	public Integer recuperarTotalDePaginas() throws AtlasAcessoBancoDadosException {
		return getDao().recuperarTotalDePaginas();
	}

}

