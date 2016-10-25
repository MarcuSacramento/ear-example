package br.gov.atlas.controller;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import br.gov.atlas.bean.AtlasFacesBean;
import br.gov.atlas.constantes.ConstantesWeb;
import br.gov.atlas.entidade.Usuario;

/**
 * Class PageController.
 */
@ManagedBean(name = "pageController")
@SessionScoped
public class PageController extends AtlasFacesBean {

	private static final long serialVersionUID = 2041465469253136628L;
	private static final String CAMINHO_DEFAULT = "Início";

	//private Item itemSelecionado;

	private Usuario usuario;
	
	/**
	 * Init.
	 */
	@PostConstruct
	public void init() {
		usuario = (Usuario) getSession().getAttribute(ConstantesWeb.USUARIO_LOGADO);
	}

/*
	*//**
	 * Class Item.
	 *//*
	public class Item {

		private Item pai;

		private String url;

		private String caminho;


		*//**
		 * Cria uma nova inst�ncia de item.
		 * 
		 * @param pai
		 *            - pai
		 * @param url
		 *            - url
		 * @param caminho
		 *            - caminho
		 *//*
		public Item(Item pai, String url, String caminho) {
			super();
			this.pai = pai;
			this.url = url;
			this.caminho = caminho;
		}

		*//**
		 * Recupera o pai.
		 * 
		 * @return pai
		 *//*
		public Item getPai() {
			return pai;
		}

		*//**
		 * Preenche pai.
		 * 
		 * @param pai
		 *            - pai
		 *//*
		public void setPai(Item pai) {
			this.pai = pai;
		}

		*//**
		 * Recupera o url.
		 * 
		 * @return url
		 *//*
		public String getUrl() {
			return url;
		}

		*//**
		 * Preenche url.
		 * 
		 * @param url
		 *            - url
		 *//*
		public void setUrl(String url) {
			this.url = url;
		}

		*//**
		 * Recupera o caminho.
		 * 
		 * @return caminho
		 *//*
		public String getCaminho() {
			if (caminho == null)
				caminho = CAMINHO_DEFAULT;
			if (pai != null) {
				String caminhoDoPai = pai.getCaminho();
				if (CAMINHO_DEFAULT.equals(caminhoDoPai))
					caminhoDoPai = "";
				return caminho = caminhoDoPai + " � " + caminho;
			} else {
				return CAMINHO_DEFAULT + (CAMINHO_DEFAULT.equals(caminho) ? "" : " � " + caminho);
			}
		}

		*//**
		 * Preenche caminho.
		 * 
		 * @param caminho
		 *            - caminho
		 *//*
		public void setCaminho(String caminho) {
			this.caminho = caminho;
		}

	}

	*//**
	 * Ir para.
	 * 
	 * @param url
	 *            - url
	 * @param caminho
	 *            - caminho
	 *//*
	public void irPara(String url, String caminho) {
		// Limpa todos os beans carregados anteriormente.
		FacesContext.getCurrentInstance().getViewRoot().getViewMap().clear();
		itemSelecionado = new Item(null, url, caminho);
	}

	*//**
	 * Ir para.
	 * 
	 * @param event
	 *            - event
	 *//*
	public void irPara(ActionEvent event) {
		Map<String, Object> attributes = event.getComponent().getAttributes();
		String url = (String) attributes.get("url");
		String caminho = (String) attributes.get("caminho");
		irPara(url, caminho);
	}

	
	 * (non-Javadoc)
	 * 
	 * @see br.gov.atlas.bean.AtlasBean#irPara(java.lang.String)
	 
	public void irPara(String url) {
		irPara(url, null);
	}

	*//**
	 * Recupera o item selecionado.
	 * 
	 * @return item selecionado
	 *//*
	public Item getItemSelecionado() {
		if (itemSelecionado != null)
			return itemSelecionado;
		itemSelecionado = new Item(null, null, CAMINHO_DEFAULT);

		return itemSelecionado;
	}

	*//**
	 * Preenche item selecionado.
	 * 
	 * @param itemSelecionado
	 *            - item selecionado
	 *//*
	public void setItemSelecionado(Item itemSelecionado) {
		this.itemSelecionado = itemSelecionado;
	}
*/
	/**
	 * Recupera o usuario.
	 * 
	 * @return the usuario
	 */
	public Usuario getUsuario() {
		return usuario;
	}

	/**
	 * Preenche usuario.
	 * 
	 * @param usuario
	 *            the usuario to set
	 */
	public void setUsuario(Usuario usuario) {
		getSession().setAttribute(ConstantesWeb.USUARIO_LOGADO, usuario);
		this.usuario = usuario;
	}

}
