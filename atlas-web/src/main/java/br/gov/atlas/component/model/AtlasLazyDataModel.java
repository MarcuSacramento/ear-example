package br.gov.atlas.component.model;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SelectableDataModel;
import org.primefaces.model.SortOrder;

import br.gov.atlas.entidade.AtlasEntidade;
import br.gov.atlas.entity.ListaPaginada;

public class AtlasLazyDataModel<T> extends LazyDataModel<T> implements SelectableDataModel<T> {

	private static final long serialVersionUID = -1603783286173867286L;

	private ListaPaginada<T> listaPaginada;

	private Object servico;

	private String metodo;

	private Object[] parametros;

	/**
	 * Construtor padrão
	 */
	public AtlasLazyDataModel() {
		this(new ListaPaginada<T>());
	}

	/**
	 * Construtor padrão, com parametro da lista paginada
	 * 
	 * @param listaPaginada
	 *            - {@link ListaPaginada}
	 */
	public AtlasLazyDataModel(ListaPaginada<T> listaPaginada) {
		this.listaPaginada = listaPaginada;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.primefaces.model.LazyDataModel#getRowData(java.lang.String)
	 */
	@Override
	public T getRowData(String rowKey) {
		return listaPaginada.getResultadoRetorno().get(Integer.parseInt(rowKey));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.primefaces.model.LazyDataModel#getRowKey(java.lang.Object)
	 */
	@Override
	public Object getRowKey(T registro) {
		return listaPaginada.getResultadoRetorno().indexOf(registro);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.primefaces.model.LazyDataModel#load(int, int, java.lang.String,
	 * org.primefaces.model.SortOrder, java.util.Map)
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public List<T> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, String> filters) {

		if (listaPaginada == null) {
			return new ArrayList<>();
		}

		// rowCount
		this.setRowCount(listaPaginada.getQtdRegistros());
		this.setPageSize(pageSize);

		// if (first != listaPaginada.getPrimeiroRegistro()) {
		try {

			Class[] parameterClasses;
			if (sortField == null) {
				parameterClasses = new Class[parametros.length];
			} else {
				parameterClasses = new Class[parametros.length + 2];
			}
			for (int i = 0; i < parametros.length; i++) {
				if (parametros[i] instanceof AtlasEntidade) {
					((AtlasEntidade) parametros[i]).setPrimeiroRegistro(first);
				}

				parameterClasses[i] = parametros[i].getClass();

			}

			Object[] parametrosTemp;
			if (sortField != null) {
				parameterClasses[parameterClasses.length - 2] = String.class;
				parameterClasses[parameterClasses.length - 1] = String.class;
				parametrosTemp = new Object[parametros.length + 2];
				for (int i = 0; i < parametros.length; i++) {
					parametrosTemp[i] = parametros[i];
				}
				parametrosTemp[parametrosTemp.length - 2] = sortField;
				parametrosTemp[parametrosTemp.length - 1] = sortOrder.toString();

			} else {
				parametrosTemp = parametros.clone();
			}

			Method method = servico.getClass().getDeclaredMethod(metodo, parameterClasses);
			listaPaginada = (ListaPaginada<T>) method.invoke(servico, parametrosTemp);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
		// }

		return listaPaginada.getResultadoRetorno();
	}

	/**
	 * @return o atributo listaPaginada
	 */
	public ListaPaginada<T> getListaPaginada() {
		return listaPaginada;
	}

	/**
	 * @param listaPaginada
	 *            preenche o atributo listaPaginada
	 */
	public void setListaPaginada(ListaPaginada<T> listaPaginada) {
		this.listaPaginada = listaPaginada;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.primefaces.model.LazyDataModel#setRowIndex(int)
	 */
	@Override
	public void setRowIndex(int rowIndex) {
		if (rowIndex >= 0) {
			setPageSize(listaPaginada.getQtdRegistroPorPagina());
		}
		super.setRowIndex(rowIndex);
	}

	/**
	 * @param servico
	 *            preenche o atributo servico
	 */
	public void setServico(Object servico) {
		this.servico = servico;
	}

	/**
	 * @param metodo
	 *            preenche o atributo metodo
	 */
	public void setMetodo(String metodo) {
		this.metodo = metodo;
	}

	/**
	 * @param parametros
	 *            preenche o atributo parametros
	 */
	public void setParametros(Object... parametros) {
		this.parametros = parametros;
	}

}
