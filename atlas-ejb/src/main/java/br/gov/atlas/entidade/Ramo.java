package br.gov.atlas.entidade;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Table(name = "atlas.ramo")
@SequenceGenerator(name = "atlas.ramo_id_ramo_seq", sequenceName = "atlas.ramo_id_ramo_seq", allocationSize = 1)
@Entity
public class Ramo extends AtlasEntidade {

	private static final long serialVersionUID = -815081105096323697L;

	@Id
	@Column(name = "id_ramo")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "atlas.ramo_id_ramo_seq")
	private Integer id;

	@Column(name = "nme_ramo", length = 100)
	private String nome;

	@Column(name = "dta_cadastro")
	private Date dataCadastro;

	@OneToMany(mappedBy = "ramo", fetch = FetchType.EAGER)
	@OrderBy(value = "nome")
	private List<TipoOrgao> tipoOrgaos;

	@Override
	public Integer getId() {
		return this.id;
	}

	@Override
	public void setId(Integer id) {
		this.id = id;
	}

	@Override
	public String getLabel() {
		return this.nome;
	}

	public String getNome() {
		return this.nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Date getDataCadastro() {
		return this.dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = (prime * result)
				+ ((this.id == null) ? 0 : this.id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Ramo other = (Ramo) obj;
		if (this.id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!this.id.equals(other.id)) {
			return false;
		}
		return true;
	}

	public List<TipoOrgao> getTipoOrgaos() {
		return tipoOrgaos;
	}

	public void setTipoOrgaos(List<TipoOrgao> tipoOrgaos) {
		this.tipoOrgaos = tipoOrgaos;
	}



}
