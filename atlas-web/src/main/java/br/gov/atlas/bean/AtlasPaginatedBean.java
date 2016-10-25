package br.gov.atlas.bean;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import br.gov.atlas.entidade.AtlasEntidade;
import br.gov.atlas.entidade.TermoGlossario;
import br.gov.atlas.entity.LinksPaginacao;
import br.gov.atlas.exception.AtlasAcessoBancoDadosException;

public abstract class AtlasPaginatedBean<ENTITY extends AtlasEntidade> extends AtlasCrudBean<TermoGlossario> {

	private static final long serialVersionUID = 3449500768483269824L;
	protected Integer currentPage = 1;

	public void configurarPaginaAtual(Integer currentPage) {
		this.currentPage = currentPage;
	}

	public Integer getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(Integer currentPage) {
		this.currentPage = currentPage;
	}

	public void setCurrentPage(String currentPage) {
		this.currentPage = Integer.valueOf(currentPage);
	}

	public void goToNextPage() {
		this.currentPage++;
	}

	public void goToPreviousPage() {
		this.currentPage--;
	}

	public void goToFirstPage() {
		this.currentPage = 1;
	}

	public void goToLastPage() throws AtlasAcessoBancoDadosException {
		this.currentPage = recuperarTotalDePaginas();
	}

	public String getNomeStyleClassPrimeiraPagina() {
		if (getCurrentPage() != 1) {
			return "atlas_icone_paginacao_itens_anteriores_ativo";
		} else {
			return "atlas_icone_paginacao_itens_anteriores_inativo";
		}
	}

	public String getNomeStyleClassUltimaPagina()
			throws AtlasAcessoBancoDadosException {
		if (getCurrentPage() != recuperarTotalDePaginas()) {
			return "atlas_icone_paginacao_itens_proximos_ativo";
		} else {
			return "atlas_icone_paginacao_itens_proximos_inativo";
		}
	}

	public String getNomeStyleClassProximaPagina()
			throws AtlasAcessoBancoDadosException {
		if (getCurrentPage() != recuperarTotalDePaginas()) {
			return "atlas_icone_paginacao_item_proximo_ativo";
		} else {
			return "atlas_icone_paginacao_item_proximo_inativo";
		}
	}

	public String getNomeStyleClassPaginaAnterior() {
		if (getCurrentPage() != 1) {
			return "atlas_icone_paginacao_item_anterior_ativo";
		} else {
			return "atlas_icone_paginacao_item_anterior_inativo";
		}
	}

	public boolean isPrimeiraPaginaSelecionada() {
		return getCurrentPage() == 1;
	}

	public boolean isUltimaPaginaSelecionada()
			throws AtlasAcessoBancoDadosException {
		return getCurrentPage() == recuperarTotalDePaginas();
	}

	public List<LinksPaginacao> getListaDeLinksDasPaginas()
			throws AtlasAcessoBancoDadosException {
		List<LinksPaginacao> listaDeLinksDasPaginas = new ArrayList<>();
		Integer totalDePaginas = recuperarTotalDePaginas();

		for (int paginaAtual = 1; paginaAtual <= totalDePaginas; paginaAtual++) {
			String paginaAtualAsString = String.valueOf(paginaAtual);
			HttpSession session = (HttpSession) getFacesContext().getExternalContext().getSession(false);
			if(session!=null){
				Integer attribute = (Integer) session.getAttribute("_atlasCurrentPage");
				setCurrentPage(attribute!=null?attribute:1);
			}
			String nomeClasseCss = (paginaAtual == getCurrentPage()) ? "atlas_paginacao_link_ativo" : "atlas_paginacao_link";
			listaDeLinksDasPaginas.add(new LinksPaginacao(paginaAtualAsString, nomeClasseCss));
		}

		return listaDeLinksDasPaginas;
	}

	public String classePaginaAtual(Integer pagina){
		HttpSession session = (HttpSession) getFacesContext().getExternalContext().getSession(false);
		if(session!=null){
			Integer attribute = (Integer) session.getAttribute("_atlasCurrentPage");
			setCurrentPage(attribute!=null?attribute:1);
		}
		return getCurrentPage().equals(pagina)?"ui-state-active":""; 
	}
	
	public void pesquisar() {
		try {
			HttpSession session = (HttpSession) getFacesContext().getExternalContext().getSession(false);
			if(session!=null){
				session.setAttribute("_atlasCurrentPage", getCurrentPage());
			}
			efetuarPesquisa();
		} catch (AtlasAcessoBancoDadosException e) {
			e.printStackTrace();
			error(e);
		}
	}
	
	public abstract Integer recuperarTotalDePaginas()
			throws AtlasAcessoBancoDadosException;

}
