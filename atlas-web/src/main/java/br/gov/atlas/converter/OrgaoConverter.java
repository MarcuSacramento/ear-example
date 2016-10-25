package br.gov.atlas.converter;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

import br.gov.atlas.entidade.Orgao;
import br.gov.atlas.servico.OrgaoServico;

@FacesConverter("orgaoConverter")
public class OrgaoConverter implements Converter {
	 
	    public Object getAsObject(FacesContext fc, UIComponent uic, String value) {
	        if(value != null && value.trim().length() > 0) {
	            try {
	                OrgaoServico service = (OrgaoServico) fc.getExternalContext().getApplicationMap().get("orgaoServico");
	                return null;// service.getOrgaos().get(Integer.parseInt(value));
	            } catch(NumberFormatException e) {
	                throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Conversion Error", "Not a valid theme."));
	            }
	        }
	        else {
	            return null;
	        }
	    }
	 
	    public String getAsString(FacesContext fc, UIComponent uic, Object object) {
	        if(object != null) {
	            return String.valueOf(((Orgao) object).getId());
	        }
	        else {
	            return null;
	        }
	    }  
}
