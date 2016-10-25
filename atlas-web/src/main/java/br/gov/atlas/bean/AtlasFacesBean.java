package br.gov.atlas.bean;

import java.io.Serializable;
import java.util.Map;
import java.util.ResourceBundle;

import javax.el.ELContext;
import javax.el.ValueExpression;
import javax.faces.application.Application;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.Flash;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.primefaces.context.RequestContext;

import br.gov.atlas.controller.PageController;

/**
 * Class AtlasBean.
 */
public abstract class AtlasFacesBean implements Serializable {

	private static final long serialVersionUID = 8524132503486387007L;

	protected ResourceBundle msg = ResourceBundle.getBundle("mensagens");

	/*	*//**
	 * Define a página atual.
	 * 
	 * @param page - page
	 *//*
	protected void setPageAtual(String page) {
		((PageController) getBean("pageController")).setItemSelecionado(new PageController().new Item(null, page, null));
	}*/

	/**
	 * Define a página atual, via {@link PageController#irPara(String)}.
	 * 
	 * @param page - page
	 */
	protected void irPara(String page) {
		((PageController) getBean("pageController")).irPara(page);
	}

	/**
	 * Recupera o {@link Application} da instancia corrente do {@link FacesContext}.
	 */
	protected final Flash getFlash() {
		return getExternalContext().getFlash();
	}

	/**
	 * Recupera o application.
	 * 
	 * @return application
	 */
	protected final Application getApplication() {
		return FacesContext.getCurrentInstance().getApplication();
	}

	/**
	 * Verifica se é postback.
	 * 
	 * @return <code>true</code> se verdadeiro, e <code>false</code> caso contrário.
	 */
	protected final boolean isPostback() {
		return getFacesContext().isPostback();
	}

	/**
	 * Recupera um {@link Map} com os dados de {@link Application} do {@link ExternalContext}.
	 * 
	 * @return {@link Map}
	 */
	protected final Map<String, Object> getApplicationMap() {
		return getExternalContext().getApplicationMap();
	}

	/**
	 * Recupera o {@link Map} do {@link Session}.
	 * 
	 * @return {@link Map}
	 */
	protected final Map<String, Object> getSessionMap() {
		return getExternalContext().getSessionMap();
	}

	/**
	 * Recupera o request map.
	 * 
	 * @return request map
	 */
	protected final Map<String, Object> getRequestMap() {
		return getExternalContext().getRequestMap();
	}

	/**
	 * Recupera o external context.
	 * 
	 * @return external context
	 */
	protected final ExternalContext getExternalContext() {
		return FacesContext.getCurrentInstance().getExternalContext();
	}

	/**
	 * Recupera o faces context.
	 * 
	 * @return faces context
	 */
	protected final FacesContext getFacesContext() {
		return FacesContext.getCurrentInstance();
	}

	/**
	 * Recupera o request context.
	 * 
	 * @return request context
	 */
	protected final RequestContext getRequestContext(){
		return RequestContext.getCurrentInstance();
	}

	/**
	 * Recupera o request.
	 * 
	 * @return request
	 */
	protected final HttpServletRequest getRequest() {
		return (HttpServletRequest) getExternalContext().getRequest();
	}

	/**
	 * Recupera o response.
	 * 
	 * @return response
	 */
	protected final HttpServletResponse getResponse() {
		return (HttpServletResponse) getExternalContext().getResponse();
	}

	/**
	 * Recupera o session.
	 * 
	 * @return session
	 */
	protected final HttpSession getSession() {
		return getRequest().getSession();
	}

	/**
	 * Recupera o bean.
	 * 
	 * @param <T> - tipo genérico
	 * @param name - name
	 * @return bean
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getBean(String name) {
		if (FacesContext.getCurrentInstance() == null) {
			return null;
		}
		ELContext elContext = FacesContext.getCurrentInstance().getELContext();
		return (T) FacesContext.getCurrentInstance().getApplication().getELResolver().getValue(elContext, null, name);
	}

	/**
	 * Recupera o value.
	 * 
	 * @param expr - expr
	 * @return value
	 */
	protected final Object getValue(String expr) {
		ValueExpression valueExpression = getApplication().getExpressionFactory().createValueExpression(getFacesContext().getELContext(), expr,
				Object.class);
		return valueExpression.getValue(getFacesContext().getELContext());
	}

	/**
	 * Preenche o value.
	 * 
	 * @param expr - expr
	 * @param value - value
	 */
	protected final void setValue(String expr, Object value) {
		ValueExpression valueExpression = getApplication().getExpressionFactory().createValueExpression(getFacesContext().getELContext(), expr,
				Object.class);
		valueExpression.setValue(getFacesContext().getELContext(), value);
	}

	/**
	 * Recupera o parameter.
	 * 
	 * @param nome - nome
	 * @return parameter
	 */
	protected final Object getParameter(String nome) {
		return getExternalContext().getRequestParameterMap().get(nome);
	}

	/**
	 * Dummy action method.
	 * 
	 * @param event - event
	 */
	public void dummyActionMethod(ValueChangeEvent event) {}

	/**
	 * Dummy action method.
	 * 
	 * @param event - event
	 */
	public void dummyActionMethod(ActionEvent event) {}


	/**
	 * Realiza o update de um componente da visão
	 *
	 * @param id - componente a sofrer o update
	 */
	protected void updateComponent(String id) {
		if (id != null && !"".equals(id.trim())) {
			getRequestContext().update(id);
		}
	}

}
