package br.gov.atlas.entidade;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class MensageriaOrgaoID implements Serializable {
	private static final long serialVersionUID = 5957712781178214433L;

	@Column(name="id_mensageria")
	private Integer idMensageria;
	
	@Column(name="id_orgao")
	private Integer idOrgao;
	
	public Integer getIdMensageria() {
		return idMensageria;
	}

	public void setIdMensageria(Integer idMensageria) {
		this.idMensageria = idMensageria;
	}

	public Integer getIdOrgao() {
		return idOrgao;
	}

	public void setIdOrgao(Integer idOrgao) {
		this.idOrgao = idOrgao;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((idMensageria == null) ? 0 : idMensageria.hashCode());
		result = prime * result
				+ ((idOrgao == null) ? 0 : idOrgao.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MensageriaOrgaoID other = (MensageriaOrgaoID) obj;
		if (idMensageria == null) {
			if (other.idOrgao != null)
				return false;
		} else if (!idMensageria.equals(other.idOrgao))
			return false;
		if (idOrgao == null) {
			if (other.idOrgao != null)
				return false;
		} else if (!idOrgao.equals(other.idOrgao))
			return false;
		return true;
	}
}