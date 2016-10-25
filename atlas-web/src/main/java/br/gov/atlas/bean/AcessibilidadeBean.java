package br.gov.atlas.bean;

import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

@ManagedBean(name = "acessibilidadeBean")
@ViewScoped
public class AcessibilidadeBean extends AtlasFacesBean {

	public String getTextoContraste() {
		String result = "";
		Map requestMap = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
		String parametro = (String) requestMap.get("contraste");

		if (parametro != null && parametro.equals("1")) {
			result += "javascript:history.go(-1)";
		} else {
			result += "?contraste=1";
		}
		return result;
	}
	
	public String getUrlCss(){
		Map requestMap = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
		String parametro = (String) requestMap.get("contraste");
		if (parametro != null && parametro.equals("1")) {
			 return FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath()+"/pub/_css/estilo_cor_contraste.css";
		} else {
			return "";
		}
	}

}
