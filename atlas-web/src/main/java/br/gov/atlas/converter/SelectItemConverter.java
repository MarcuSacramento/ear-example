package br.gov.atlas.converter;

import java.util.List;
import javax.faces.component.UIComponent;
import javax.faces.component.UISelectItem;
import javax.faces.component.UISelectItems;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

import org.primefaces.component.picklist.PickList;
import org.primefaces.model.DualListModel;

/**
 * Class SelectItemConverter.
 */
@FacesConverter("selectItemConverter")
public class SelectItemConverter implements Converter {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.faces.convert.Converter#getAsObject(javax.faces.context.FacesContext
	 * , javax.faces.component.UIComponent, java.lang.String)
	 */
	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Object getAsObject(FacesContext fcontext, UIComponent comp, String valueString) {
		if (valueString == null || valueString.trim().isEmpty())
			return null;

		if (comp instanceof PickList) {
			DualListModel dl = (DualListModel) ((PickList) comp).getValue();

			for (Object o : dl.getSource()) {
				if (valueString.equals(o.toString())) {
					return o;
				}
			}
			for (Object o : dl.getTarget()) {
				if (valueString.equals(o.toString())) {
					return o;
				}
			}
			throw new ConverterException("Não foi possível converter o objeto: " + valueString);
		} else {

			List<UIComponent> children = comp.getChildren();
			for (UIComponent child : children) {

				if (child instanceof UISelectItem) {
					UISelectItem si = (UISelectItem) child;
					if (si.getValue() != null && si.getValue().toString().equals(valueString)) {
						return si.getValue();
					}
				}

				if (child instanceof UISelectItems) {
					UISelectItems sis = (UISelectItems) child;
					List<Object> items = (List<Object>) sis.getValue();
					if (items != null) {
						for (Object si : items) {
							if (si.toString().equals(valueString)) {
								return si;
							}
						}
					}
				}
			}
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.faces.convert.Converter#getAsString(javax.faces.context.FacesContext
	 * , javax.faces.component.UIComponent, java.lang.Object)
	 */
	@Override
	public String getAsString(FacesContext fcontext, UIComponent comp, Object value) {
		return value == null ? null : value.toString();
	}
}
