package br.gov.atlas.bean;

import javax.el.ELContext;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

@ManagedBean(name = "breadcrumbBean")
@ViewScoped
public class BreadcrumbBean {
	
	private String paginaAtual;

	/**
	 * @return the paginaAtual
	 */
	public String getPaginaAtual() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		String currentPage = facesContext.getViewRoot().getViewId();
		String paginaInicial = " » <a href=\"/\"> Página Inicial</a>";
//		ELContext elContext2 = facesContext.getELContext();
		
		if (paginaAtual == null && currentPage.endsWith(getTelaIndicadores())){
			ELContext elContext = facesContext.getELContext();
			MenuBean menuBean = (MenuBean) facesContext.getApplication().getELResolver().getValue(elContext, null, "menuBean");
			if(menuBean.getOpcaoIndicadores().equals("relatorio")){
				paginaAtual = paginaInicial + " » <a href=\"#\">Indicadores</a> » <a href=\"#\">Indicadores</a>";
			} else{
				paginaAtual = paginaInicial + " » <a href=\"#\">Indicadores</a> » <a href=\"#\">Referências</a>";
			}
		} else if (paginaAtual == null && currentPage.endsWith(getTelaAbcDireitos())){
			ELContext elContext = facesContext.getELContext();
			MenuBean menuBean = (MenuBean) facesContext.getApplication().getELResolver().getValue(elContext, null, "menuBean");
			
			if (menuBean.getInterna().equals(MenuBean.INTERNA_DICIONARIO)){
				paginaAtual = paginaInicial + " » <a href=\"#\">ABC dos seus Direitos</a> » <a href=\"#\">Dicionário</a>";
			} else if (menuBean.getInterna().equals(MenuBean.INTERNA_VIDEO)){
				paginaAtual = paginaInicial + " » <a href=\"#\">ABC dos seus Direitos</a> » <a href=\"#\">Vídeo</a>";
			} else if (menuBean.getInterna().equals(MenuBean.INTERNA_MAPA)){
				paginaAtual = paginaInicial + " » <a href=\"#\">ABC dos seus Direitos</a> » <a href=\"#\">Sistema de Justiça</a>";
			} else {
				paginaAtual = paginaInicial + " » <a href=\"#\">ABC dos seus Direitos</a> » <a href=\"#\">Cartilha</a>";
			}
			
		} else if (paginaAtual == null && currentPage.endsWith(getTelaPortasJustica())){
			paginaAtual = paginaInicial + " » <a href=\"#\">Mapa da Justiça</a>";
		} else if (paginaAtual == null && currentPage.endsWith(getTelaNovoOrgao())) {
			paginaAtual = paginaInicial + " » <a href=\"#\">Mapa da Justiça</a>  » <a href=\"#\">Novo Órgão</a>";
		} else if (paginaAtual == null && currentPage.endsWith(getTelaDownload())){
			paginaAtual = paginaInicial + " » <a href=\"#\">Downloads</a>";
//		} else if (paginaAtual == null && currentPage.endsWith(getExibirOAtlas())){
//			paginaAtual = paginaInicial + " » <a href=\"#\">O Atlas</a>";
		} else if (paginaAtual == null && currentPage.endsWith(getTelaParceiro())){
			paginaAtual = paginaInicial + " » <a href=\"#\">Parceiros</a>";
		} else if (paginaAtual == null && currentPage.endsWith(getTelaAcessibilidade())){
			paginaAtual = paginaInicial + " » <a href=\"#\">Acessibilidade</a>";
		} else if (paginaAtual == null){
			paginaAtual = paginaInicial;
		} 
		
		return paginaAtual;
	}

	/**
	 * @param paginaAtual the paginaAtual to set
	 */
	public void setPaginaAtual(String paginaAtual) {
		this.paginaAtual = paginaAtual;
	}
	
	/**
	 * @return tela de indicadores
	 */
	public String getTelaIndicadores() {
		return "/pub/template/tela_interna_05.xhtml";
	}
	
	/**
	 * @return tela de ABC dos direitos
	 */
	public String getTelaAbcDireitos() {
		return "/pub/seusDireitos/seusDireitos.xhtml";
	}
	
	/**
	 * @return tela portas da justiça
	 */
	public String getTelaPortasJustica() {
		return "/pub/exibirOrgaoJustica/orgaoConsulta.xhtml";
	}
	
	/**
	 * @return tela portas da justiça
	 */
	public String getTelaDownload() {
		return "/pub/exibirDownload/downloadConsulta.xhtml";
	}
	
	/**
	 * @return tela portas da justiça
	 */
	public String getTelaParceiro() {
		return "/pub/parceiros.xhtml";
	}
	
	public String getTelaAcessibilidade() {
		return "/pub/acessibilidade.xhtml";
	}
	
	public String getTelaNovoOrgao() {
		return "/pub/manterOrgaoJustica/orgaoCadastro.xhtml";
	}

//	public String getExibirOAtlas() {
//		return "/pub/sobre/oAtlas/exibir.xhtml";
//	}
	
}
