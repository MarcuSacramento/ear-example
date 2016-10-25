package br.gov.atlas.servico;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import br.gov.atlas.dao.TermoGlossarioDao;
import br.gov.atlas.entidade.FonteReferencia;
import br.gov.atlas.entidade.TermoGlossario;
import br.gov.atlas.entity.ListaPaginada;
import br.gov.atlas.exception.AtlasAcessoBancoDadosException;

@Stateless(name = "TermoGlossarioServico")
@LocalBean
public class TermoGlossarioServico extends AtlasServicoImpl {

	private TermoGlossarioDao dao;

	@Override
	public TermoGlossarioDao getDao(){
		if(dao==null){
			dao = new TermoGlossarioDao(em);
		}
		return dao;
	}

	public Integer pesquisarTermoPorNome(String nome) throws AtlasAcessoBancoDadosException {
		return getDao().pesquisarTermoPorNome(nome);
	}

	public ListaPaginada<TermoGlossario> recuperarTermosGlossario(TermoGlossario termo) throws AtlasAcessoBancoDadosException {
		return getDao().recuperarTermosGlossario(termo);
	}

	public ListaPaginada<TermoGlossario> recuperarTermosPropostos(ArrayList<TermoGlossario> termos) throws AtlasAcessoBancoDadosException{
		return getDao().recuperarTermosPropostos(termos);
	}

	public List<FonteReferencia> recuperarFontesReferencia(TermoGlossario termoGLossario) throws AtlasAcessoBancoDadosException {
		return getDao().recuperarFontesReferencia(termoGLossario);
	}

	public ListaPaginada<FonteReferencia> recuperarFontesReferenciaPaginada(ArrayList<TermoGlossario> termos) throws AtlasAcessoBancoDadosException {
		return getDao().recuperarFontesReferenciaPaginada(termos);
	}

	enum LadoProximoRegistro { ESQUERDA, CENTRO, DIREITA; }

}
