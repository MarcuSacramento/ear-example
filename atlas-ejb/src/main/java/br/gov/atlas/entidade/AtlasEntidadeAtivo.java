package br.gov.atlas.entidade;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class AtlasEntidadeAtivo extends AtlasEntidade {

	private static final long serialVersionUID = -6914840827795246191L;

	@Column(name = "st_ativo")
	protected Boolean ativo = Boolean.TRUE;

	/**
	 * @return the ativo
	 */
	public final Boolean getAtivo() {
		if (ativo == null) {
			ativo = Boolean.TRUE;
		}
		return ativo;
	}

	/**
	 * @param ativo the ativo to set
	 */
	public final void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}
	
	public String getStatus() {
		if (getAtivo()) {
			return "Ativo";
		}
		return "Inativo";
	}
}
