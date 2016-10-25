package br.gov.atlas.servico;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import br.gov.atlas.dao.TermoGlossarioDao;
import br.gov.atlas.entidade.FonteReferencia;
import br.gov.atlas.entidade.TermoGlossario;
import br.gov.atlas.entity.TermoGlossarioLinhaComTresRegistrosAdapter;
import br.gov.atlas.enums.SituacaoTermoGloassario;
import br.gov.atlas.exception.AtlasAcessoBancoDadosException;

@Stateless(name = "exibirGlossarioServico")
@LocalBean
public class ExibirGlossarioServico extends AtlasServicoImpl {

	private TermoGlossarioDao dao;

	@Override
	public TermoGlossarioDao getDao(){
		if(dao==null){
			dao = new TermoGlossarioDao(em);
		}
		return dao;
	}

	public List<TermoGlossarioLinhaComTresRegistrosAdapter>
	recuperarTermosComLinhasDeTresRegistros(TermoGlossario termo, Integer page) throws AtlasAcessoBancoDadosException {
		termo.setIndicadorSituacao(SituacaoTermoGloassario.DISPONIVEL.indicadorSituacao());
		List<TermoGlossario> listaDeTermos = recuperarTermosGlossarioPorNomeSituacao(termo);

		LadoProximoRegistro ladoProximoRegistro = LadoProximoRegistro.ESQUERDA;
		TermoGlossarioLinhaComTresRegistrosAdapter estaLinhaComTresRegistros = new TermoGlossarioLinhaComTresRegistrosAdapter();

		List<TermoGlossarioLinhaComTresRegistrosAdapter> resultado = new ArrayList<>();

		for (TermoGlossario registroCorrente : listaDeTermos) {
			// ESQUERDA
			if( ladoProximoRegistro.equals(LadoProximoRegistro.ESQUERDA) ){

				ladoProximoRegistro = LadoProximoRegistro.CENTRO;
				estaLinhaComTresRegistros = new TermoGlossarioLinhaComTresRegistrosAdapter();
				resultado.add(estaLinhaComTresRegistros);
				estaLinhaComTresRegistros.setRegistroEsquerda(registroCorrente);
				//CENTRO
			}else if ( ladoProximoRegistro.equals(LadoProximoRegistro.CENTRO) ) {
				ladoProximoRegistro = LadoProximoRegistro.DIREITA;
				estaLinhaComTresRegistros.setRegistroCentro(registroCorrente);
				//DIREITA
			}else {
				ladoProximoRegistro = LadoProximoRegistro.ESQUERDA;
				estaLinhaComTresRegistros.setRegistroDireita(registroCorrente);
			}
		}

		return resultado;
	}

	public List<TermoGlossario> recuperarTermosGlossarioPorNomeSituacao(TermoGlossario termo)
			throws AtlasAcessoBancoDadosException {
		return getDao().recuperarTermosGlossarioPorNomeSituacao(termo);
	}

	public List<FonteReferencia> recuperarFontesReferencia(TermoGlossario termo) throws AtlasAcessoBancoDadosException {
		return getDao().recuperarFontesReferencia(termo);
	}

	public Integer recuperarTotalDePaginas(String termoPesquisa) throws AtlasAcessoBancoDadosException {
		return getDao().recuperarTotalDePaginas(termoPesquisa);
	}

	public Integer recuperarTotalDePaginas(TermoGlossario termo) throws AtlasAcessoBancoDadosException {
		return getDao().recuperarTotalDePaginas(termo);
	}


	enum LadoProximoRegistro { ESQUERDA, CENTRO, DIREITA; }

}
