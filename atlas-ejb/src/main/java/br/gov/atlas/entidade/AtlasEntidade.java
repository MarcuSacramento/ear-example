package br.gov.atlas.entidade;

import java.io.Serializable;

import javax.persistence.Transient;

public abstract class AtlasEntidade implements Serializable {

	private static final long serialVersionUID = -476762693350890769L;

	
	@Transient 
	private Integer primeiroRegistro;

	
	public abstract Integer getId();


	public abstract void setId(Integer id);


	public abstract String getLabel();


	@Override
	public int hashCode() {
		final int prime = 37;
		int result = (prime * ((getId() == null) ? 1 : getId().hashCode())) ^ 31;
		return result;
	}

	
	public Integer getPrimeiroRegistro() {
		return primeiroRegistro;
	}


	public void setPrimeiroRegistro(Integer primeiroRegistro) {
		this.primeiroRegistro = primeiroRegistro;
	}
	
	
	@Override
	public String toString() {
		return getId().toString();
	}

}
