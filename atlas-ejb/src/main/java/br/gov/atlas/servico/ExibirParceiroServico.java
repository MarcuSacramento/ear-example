package br.gov.atlas.servico;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import br.gov.atlas.dao.ParceiroDao;
import br.gov.atlas.entidade.Parceiro;
import br.gov.atlas.entity.ListaPaginada;
import br.gov.atlas.entity.ParceiroLinhaComTresRegistrosAdapter;
import br.gov.atlas.exception.AtlasAcessoBancoDadosException;
import br.gov.atlas.servico.ExibirGlossarioServico.LadoProximoRegistro;

@Stateless(name="ExibirParceiroServico")
@LocalBean
public class ExibirParceiroServico extends AtlasServicoImpl {

	private ParceiroDao dao;
	
	@Override
	public ParceiroDao getDao() {
		if(dao==null)
			dao = new ParceiroDao(em);
		return dao;
	}
	
	public List<Parceiro> recuperarParceiros(Integer page) throws AtlasAcessoBancoDadosException {
		return getDao().recuperarParceiros(page);
	}
	
	public List<ParceiroLinhaComTresRegistrosAdapter> recuperarParceirosComLinhasDeTresRegistros(String page) throws AtlasAcessoBancoDadosException {
		Integer pageAsInteger = Integer.parseInt(page); 
		return recuperarParceirosComLinhasDeTresRegistros(pageAsInteger); 
	}
	
	public List<ParceiroLinhaComTresRegistrosAdapter> recuperarParceirosComLinhasDeTresRegistros(Integer page) throws AtlasAcessoBancoDadosException {
		List<Parceiro> listaDeParceiros = recuperarParceiros(page);
		LadoProximoRegistro ladoProximoRegistro = LadoProximoRegistro.ESQUERDA; 
		ParceiroLinhaComTresRegistrosAdapter estaLinhaComTresRegistros = new ParceiroLinhaComTresRegistrosAdapter();
		
		List<ParceiroLinhaComTresRegistrosAdapter> resultado = new ArrayList<>();
		
		for(Parceiro registroCorrente : listaDeParceiros){
			//ESQUERDA
			if( ladoProximoRegistro.equals(LadoProximoRegistro.ESQUERDA) ) {
				ladoProximoRegistro = LadoProximoRegistro.CENTRO; 
				estaLinhaComTresRegistros = new ParceiroLinhaComTresRegistrosAdapter();
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
	
	public ListaPaginada<Parceiro> recuperarParceiros(Parceiro filtro) throws AtlasAcessoBancoDadosException {
		return getDao().recuperarParceiros(filtro);
	}
	
	public Integer recuperarTotalDePaginas() throws AtlasAcessoBancoDadosException {
		return getDao().recuperarTotalDePaginas();
	}

}
