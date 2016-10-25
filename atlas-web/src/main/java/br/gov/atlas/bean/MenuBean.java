package br.gov.atlas.bean;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean(name="menuBean")
@SessionScoped
public class MenuBean {
	
	private String interna;
	private String subInterna;
	
	public static final String INTERNA_CARTILHA   = "cartilha";
	public static final String INTERNA_DICIONARIO = "dicionario";
	public static final String INTERNA_VIDEO      = "video";
	public static final String INTERNA_MAPA       = "mapa";

	public static final String SUB_INTERNA_CONSULTA = "consulta";
	public static final String SUB_INTERNA_DETALHE  = "detalhe";

	public static final String ITEM_MENU_MONTAR_RELATORIO = "relatorio";
	public static final String ITEM_MENU_REFERENCIA		  = "referencia";
	
	public String montarMenu(){
		return "seusDireitos";
	}
	
	public boolean isCartilha(){
		return interna.equals(INTERNA_CARTILHA) ? true : false;
	}
	
	public boolean isDicionario(){
		return interna.equals(INTERNA_DICIONARIO) ? true : false;
	}
	
	public boolean isVideo(){
		return interna.equals(INTERNA_VIDEO) ? true : false;
	}
	
	public boolean isMapa(){
		return interna.equals(INTERNA_MAPA) ? true : false;
	}
	
	public boolean isSubInternaConsulta(){
		return subInterna.equals(SUB_INTERNA_CONSULTA) ? true : false;
	}
	
	public boolean isSubInternaDetalhe(){
		return subInterna.equals(SUB_INTERNA_DETALHE) ? true : false;
	}

	public boolean isRelatorio(){
		return getOpcaoIndicadores().equals(ITEM_MENU_MONTAR_RELATORIO) ? true : false;
	}
	
	public boolean isReferencia(){
		return getOpcaoIndicadores().equals(ITEM_MENU_REFERENCIA) ? true : false;
	}
	
	public String getInterna() {
		return interna;
	}

	public void setInterna(String interna) {
		this.interna = interna;
	}
	
	public String getSubInterna() {
		return subInterna;
	}


	public void setSubInterna(String subInterna) {
		this.subInterna = subInterna;
	}
	
	public String getOpcaoIndicadores(){
		if (interna == null || 
				(!interna.equals(ITEM_MENU_MONTAR_RELATORIO) &&
				 !interna.equals(ITEM_MENU_REFERENCIA)) ){
			interna = ITEM_MENU_MONTAR_RELATORIO;
		}
		return interna;
	}
	
	public void setOpcaoIndicadores(String interna){
		setInterna(interna);
	}
	

}
