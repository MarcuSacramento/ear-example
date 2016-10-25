package br.gov.atlas.seguranca.listener;

import java.util.Arrays;

import javax.faces.application.NavigationHandler;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import br.gov.atlas.constantes.ConstantesWeb;

public class SegurancaPhaseListener implements PhaseListener {

	private static final long serialVersionUID = 6316018366181229185L;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.faces.event.PhaseListener#afterPhase(javax.faces.event.PhaseEvent)
	 */
	@Override
	public void afterPhase(PhaseEvent event) {
		FacesContext facesContext = event.getFacesContext();
		String currentPage = facesContext.getViewRoot().getViewId();

		boolean isTelaSemLogin = isTelaSemLogin(currentPage);

		if (!isTelaSemLogin) {

			boolean autorizado = false;
			HttpSession session = ((HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest()).getSession(false);
			if (session != null) {
				autorizado = (session.getAttribute(ConstantesWeb.USUARIO_LOGADO) != null) ? true : false;
			}

			if (!isTelaSemLogin && !autorizado) {
				NavigationHandler nh = facesContext.getApplication().getNavigationHandler();
				nh.handleNavigation(facesContext, null, ConstantesWeb.PAGE_LOGIN + "?faces-redirect=true");
			}
		}

	}

	/**
	 * Verifica se a tela acessada pode ser vista sem estar logado no sistema.
	 * 
	 * @param currentPage
	 * @return true/false
	 */
	private boolean isTelaSemLogin(String currentPage) {
		return currentPage.lastIndexOf("login.xhtml") > -1 || currentPage.lastIndexOf("acessibilidade.xhtml") > -1
				|| Arrays.asList(ConstantesWeb.PAGINAS_PUBLICAS).contains(currentPage);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.faces.event.PhaseListener#beforePhase(javax.faces.event.PhaseEvent)
	 */
	@Override
	public void beforePhase(PhaseEvent event) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.faces.event.PhaseListener#getPhaseId()
	 */
	@Override
	public PhaseId getPhaseId() {
		return PhaseId.RESTORE_VIEW;
	}

}
