package br.gov.atlas.converter;

import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.WeakHashMap;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@FacesConverter("destinatarioConverter")
public class DestinatarioConverter implements Converter {
	
	private static Map<Object, String> entities = new WeakHashMap<Object, String>();
	
	@Override
	public Object getAsObject(FacesContext fc, UIComponent uic, String value) {
        for (Entry<Object, String> entry : entities.entrySet()) {
            if (entry.getValue().equals(value)) {
                return entry.getKey();
            }
        }
        return null;
	}

	@Override
	public String getAsString(FacesContext fc, UIComponent uic, Object entity) {
        synchronized (entities) {
            if (!entities.containsKey(entity)) {
                String uuid = UUID.randomUUID().toString();
                entities.put(entity, uuid);
                return uuid;
            } else {
                return entities.get(entity);
            }
        }
	}
}