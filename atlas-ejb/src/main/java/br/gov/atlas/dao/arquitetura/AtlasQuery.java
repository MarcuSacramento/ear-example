package br.gov.atlas.dao.arquitetura;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;
import javax.persistence.NoResultException;
import javax.persistence.Parameter;
import javax.persistence.Query;
import javax.persistence.TemporalType;

import br.gov.atlas.exception.AtlasAcessoBancoDadosException;

public class AtlasQuery {

	private Query query;

	/**
	 * Construtor da classe.
	 * 
	 * @param query
	 *            - query
	 */
	public AtlasQuery(Query query) {
		this.query = query;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.persistence.Query#executeUpdate()
	 */
	public int executeUpdate() {
		return query.executeUpdate();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.persistence.Query#getFirstResult()
	 */
	public int getFirstResult() {
		return query.getFirstResult();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.persistence.Query#getFlushMode()
	 */
	public FlushModeType getFlushMode() {
		return query.getFlushMode();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.persistence.Query#getHints()
	 */
	public Map<String, Object> getHints() {
		return query.getHints();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.persistence.Query#getLockMode()
	 */
	public LockModeType getLockMode() {
		return query.getLockMode();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.persistence.Query#getMaxResults()
	 */
	public int getMaxResults() {
		return query.getMaxResults();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.persistence.Query#getParameter(java.lang.String)
	 */
	public Parameter<?> getParameter(String name) {
		return query.getParameter(name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.persistence.Query#getParameter(int)
	 */
	public Parameter<?> getParameter(int position) {
		return query.getParameter(position);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.persistence.Query#getParameter(java.lang.String,
	 * java.lang.Class)
	 */
	public <T> Parameter<T> getParameter(String name, Class<T> type) {
		return query.getParameter(name, type);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.persistence.Query#getParameter(int, java.lang.Class)
	 */
	public <T> Parameter<T> getParameter(int position, Class<T> type) {
		return query.getParameter(position, type);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.persistence.Query#getParameterValue(javax.persistence.Parameter)
	 */
	public <T> T getParameterValue(Parameter<T> param) {
		return query.getParameterValue(param);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.persistence.Query#getParameterValue(java.lang.String)
	 */
	public Object getParameterValue(String name) {
		return query.getParameterValue(name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.persistence.Query#getParameterValue(int)
	 */
	public Object getParameterValue(int position) {
		return query.getParameterValue(position);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.persistence.Query#getParameters()
	 */
	public Set<Parameter<?>> getParameters() {
		return query.getParameters();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.persistence.Query#getResultList()
	 */
	@SuppressWarnings("rawtypes")
	public List getResultList() throws AtlasAcessoBancoDadosException {
		try {
			return query.getResultList();
		} catch (Exception e) {
			throw new AtlasAcessoBancoDadosException("MSG16");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.persistence.Query#getSingleResult()
	 */
	public Object getSingleResult() throws AtlasAcessoBancoDadosException {
		try {
			return query.getSingleResult();
		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			throw new AtlasAcessoBancoDadosException("MSG16");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.persistence.Query#isBound(javax.persistence.Parameter)
	 */
	public boolean isBound(Parameter<?> param) {
		return query.isBound(param);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.persistence.Query#setFirstResult(int)
	 */
	public Query setFirstResult(int startPosition) {
		return query.setFirstResult(startPosition);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.persistence.Query#setFlushMode(javax.persistence.FlushModeType)
	 */
	public Query setFlushMode(FlushModeType flushMode) {
		return query.setFlushMode(flushMode);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.persistence.Query#setHint(java.lang.String, java.lang.Object)
	 */
	public Query setHint(String hintName, Object value) {
		return query.setHint(hintName, value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.persistence.Query#setLockMode(javax.persistence.LockModeType)
	 */
	public Query setLockMode(LockModeType lockMode) {
		return query.setLockMode(lockMode);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.persistence.Query#setMaxResults(int)
	 */
	public Query setMaxResults(int maxResult) {
		return query.setMaxResults(maxResult);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.persistence.Query#setParameter(javax.persistence.Parameter,
	 * java.lang.Object)
	 */
	public <T> Query setParameter(Parameter<T> param, T value)
			throws AtlasAcessoBancoDadosException {
		verificaValuePreenchido(param.getName(), value);
		return query.setParameter(param, value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.persistence.Query#setParameter(java.lang.String,
	 * java.lang.Object)
	 */
	public Query setParameter(String name, Object value)
			throws AtlasAcessoBancoDadosException {
		verificaValuePreenchido(name, value);
		return query.setParameter(name, value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.persistence.Query#setParameter(int, java.lang.Object)
	 */
	public Query setParameter(int position, Object value)
			throws AtlasAcessoBancoDadosException {
		verificaValuePreenchido(String.valueOf(position), value);
		return query.setParameter(position, value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.persistence.Query#setParameter(javax.persistence.Parameter,
	 * java.util.Calendar, javax.persistence.TemporalType)
	 */
	public Query setParameter(Parameter<Calendar> param, Calendar value,
			TemporalType temporalType) throws AtlasAcessoBancoDadosException {
		verificaValuePreenchido(param.getName(), value);
		return query.setParameter(param, value, temporalType);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.persistence.Query#setParameter(javax.persistence.Parameter,
	 * java.util.Date, javax.persistence.TemporalType)
	 */
	public Query setParameter(Parameter<Date> param, Date value,
			TemporalType temporalType) throws AtlasAcessoBancoDadosException {
		verificaValuePreenchido(param.getName(), value);
		return query.setParameter(param, value, temporalType);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.persistence.Query#setParameter(java.lang.String,
	 * java.util.Calendar, javax.persistence.TemporalType)
	 */
	public Query setParameter(String name, Calendar value,
			TemporalType temporalType) throws AtlasAcessoBancoDadosException {
		verificaValuePreenchido(name, value);
		return query.setParameter(name, value, temporalType);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.persistence.Query#setParameter(java.lang.String,
	 * java.util.Date, javax.persistence.TemporalType)
	 */
	public Query setParameter(String name, Date value, TemporalType temporalType)
			throws AtlasAcessoBancoDadosException {
		verificaValuePreenchido(name, value);
		return query.setParameter(name, value, temporalType);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.persistence.Query#setParameter(int, java.util.Calendar,
	 * javax.persistence.TemporalType)
	 */
	public Query setParameter(int position, Calendar value,
			TemporalType temporalType) throws AtlasAcessoBancoDadosException {
		verificaValuePreenchido(String.valueOf(position), value);
		return query.setParameter(position, value, temporalType);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.persistence.Query#setParameter(int, java.util.Date,
	 * javax.persistence.TemporalType)
	 */
	public Query setParameter(int position, Date value,
			TemporalType temporalType) throws AtlasAcessoBancoDadosException {
		verificaValuePreenchido(String.valueOf(position), value);
		return query.setParameter(position, value, temporalType);
	}

	/**
	 * @param value
	 * @throws AtlasAcessoBancoDadosException
	 */
	private void verificaValuePreenchido(String name, Object value)
			throws AtlasAcessoBancoDadosException {
		if (value == null) {
			throw new AtlasAcessoBancoDadosException("PARAM_NULL", name);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.persistence.Query#unwrap(java.lang.Class)
	 */
	public <T> T unwrap(Class<T> clazz) {
		return query.unwrap(clazz);
	}

}
