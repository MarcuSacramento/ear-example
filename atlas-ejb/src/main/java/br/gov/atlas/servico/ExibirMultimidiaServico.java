package br.gov.atlas.servico;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import br.gov.atlas.dao.MultimidiaDao;
import br.gov.atlas.entidade.Multimidia;
import br.gov.atlas.entity.ListaPaginada;
import br.gov.atlas.entity.MultimidiaLinhaComTresRegistrosAdapter;
import br.gov.atlas.exception.AtlasAcessoBancoDadosException;

@Stateless(name = "ExibirMultimidiaServico")
@LocalBean
public class ExibirMultimidiaServico extends AtlasServicoImpl {

	private MultimidiaDao dao;

	@Override
	public MultimidiaDao getDao() {
		if (dao == null)
			dao = new MultimidiaDao(em);
		return dao;
	}

	public List<Multimidia> recuperarMultimidias(Integer page) throws AtlasAcessoBancoDadosException {
		return getDao().recuperarMultimidias(page);
	}

	public List<Multimidia> recuperarMultimidias(String page) throws AtlasAcessoBancoDadosException {
		Integer pageAsInteger = Integer.parseInt(page);
		return recuperarMultimidias(pageAsInteger);
	}

	public Integer recuperarTotalDePaginas() throws AtlasAcessoBancoDadosException {
		return getDao().recuperarTotalDePaginas();
	}

	public List<MultimidiaLinhaComTresRegistrosAdapter> recuperarMultimidiasTextoComLinhasDeDoisRegistros(String page) throws NumberFormatException,
			AtlasAcessoBancoDadosException {
		Integer pageAsInteger = Integer.parseInt(page);
		return recuperarMultimidiasTextoComLinhasDeTresRegistros(pageAsInteger);
	}

	@Deprecated
	public List<MultimidiaLinhaComTresRegistrosAdapter> recuperarMultimidiasTextoComLinhasDeDoisRegistros(Integer page) throws NumberFormatException,
			AtlasAcessoBancoDadosException {
		List<Multimidia> listaDeMultimidias = recuperarMultimidiasTexto(page);
		LadoProximoRegistro ladoProximoRegistro = LadoProximoRegistro.ESQUERDA;
		MultimidiaLinhaComTresRegistrosAdapter estaLinhaComDoisRegistros = new MultimidiaLinhaComTresRegistrosAdapter();

		List<MultimidiaLinhaComTresRegistrosAdapter> resultado = new ArrayList<>();

		for (Multimidia registroCorrente : listaDeMultimidias) {
			// A_ESQUERDA
			if (ladoProximoRegistro.equals(LadoProximoRegistro.ESQUERDA)) {
				ladoProximoRegistro = LadoProximoRegistro.DIREITA;
				estaLinhaComDoisRegistros = new MultimidiaLinhaComTresRegistrosAdapter();
				resultado.add(estaLinhaComDoisRegistros);
				estaLinhaComDoisRegistros.setRegistroAEsquerda(registroCorrente);
				// A_DIREITA
			} else {
				ladoProximoRegistro = LadoProximoRegistro.ESQUERDA;
				estaLinhaComDoisRegistros.setRegistroADireita(registroCorrente);
			}
		}

		return resultado;
	}
	
	public List<MultimidiaLinhaComTresRegistrosAdapter> recuperarMultimidiasTextoComLinhasDeTresRegistros(Integer page) throws NumberFormatException,
		AtlasAcessoBancoDadosException {

		List<Multimidia> listaDeMultimidias = recuperarMultimidiasTexto(page);
		LadoProximoRegistro ladoProximoRegistro = LadoProximoRegistro.ESQUERDA;
		MultimidiaLinhaComTresRegistrosAdapter estaLinhaComTresRegistros = new MultimidiaLinhaComTresRegistrosAdapter();

		List<MultimidiaLinhaComTresRegistrosAdapter> resultado = new ArrayList<>();

		
		for (Multimidia registroCorrente : listaDeMultimidias) {
			if( ladoProximoRegistro.equals(LadoProximoRegistro.ESQUERDA) ) {
				ladoProximoRegistro = LadoProximoRegistro.CENTRO; 
				estaLinhaComTresRegistros = new MultimidiaLinhaComTresRegistrosAdapter();
				resultado.add(estaLinhaComTresRegistros); 
				estaLinhaComTresRegistros.setRegistroAEsquerda(registroCorrente);	
			//CENTRO	
			}else if ( ladoProximoRegistro.equals(LadoProximoRegistro.CENTRO) ) {
				ladoProximoRegistro = LadoProximoRegistro.DIREITA;
				estaLinhaComTresRegistros.setRegistroCentro(registroCorrente);
			//DIREITA
			} else { 
				ladoProximoRegistro = LadoProximoRegistro.ESQUERDA; 
				estaLinhaComTresRegistros.setRegistroADireita(registroCorrente);
			}
		}
		return resultado; 
	}


	public List<Multimidia> recuperarMultimidiasPorTipo(String page, String tipo) throws AtlasAcessoBancoDadosException {
		Integer pageAsInteger = Integer.parseInt(page);
		return recuperarMultimidiasPorTipo(pageAsInteger, tipo);
	}

	public List<Multimidia> recuperarMultimidiasPorTipo(Integer page, String tipo) throws NumberFormatException, AtlasAcessoBancoDadosException {
		return getDao().recuperarMultimidiasPorTipo(page, tipo);
	}
	
	public List<Multimidia> recuperarMultimidiasPorTipo(Integer page, String tipo, int registrosPorPagina) throws NumberFormatException, AtlasAcessoBancoDadosException 
	{
		return getDao().recuperarMultimidiasPorTipo(page, tipo, registrosPorPagina);
	}

	public Integer recuperarTotalDePaginasPorTipo(String tipo) throws AtlasAcessoBancoDadosException {
		return getDao().recuperarTotalDePaginasPorTipo(tipo);
	}

	public List<Multimidia> recuperarMultimidiasTexto(Integer page) throws NumberFormatException, AtlasAcessoBancoDadosException {
		return getDao().recuperarMultimidiasTexto(page);
	}

	public Integer recuperarTotalDePaginasTexto() throws AtlasAcessoBancoDadosException {
		return getDao().recuperarTotalDePaginasTexto();
	}

	public ListaPaginada<Multimidia> recuperarMultimidias(Multimidia termoMultimidiaFiltro, String tipoMultimidia)
			throws AtlasAcessoBancoDadosException {
		return getDao().recuperarMultimidias(termoMultimidiaFiltro, tipoMultimidia);
	}

	public Multimidia recuperarVideoTelaPrincipal() throws AtlasAcessoBancoDadosException {
		return getDao().recuperarVideoTelaPrincipal();

	}

}
