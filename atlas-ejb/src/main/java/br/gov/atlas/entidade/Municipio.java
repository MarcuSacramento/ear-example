package br.gov.atlas.entidade;

import java.math.BigDecimal;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Table(name = "municipio", schema="atlas")
@SequenceGenerator(name = "municipio_id_municipio_seq", sequenceName = "municipio_id_municipio_seq", allocationSize = 1)
@Entity
public class Municipio extends AtlasEntidade {

	private static final long serialVersionUID = 5246978216793162093L;

	@Id
	@Column(name = "id_municipio")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "municipio_id_municipio_seq")
	private Integer id;

	@Column(name = "nme_municipio")
	private String nome;

	@ManyToOne
	@JoinColumn(name = "id_uf")
	private UF uf;

	@OneToMany(mappedBy = "municipio", fetch = FetchType.LAZY)
	private Set<Orgao> orgaos;
	
	@Column(name = "num_longitude")
	private BigDecimal longitude;

	@Column(name = "num_latitude")
	private BigDecimal latitude;

	@Override
	public Integer getId() {
		return id;
	}

	@Override
	public void setId(Integer id) {
		this.id = id;
	}
	
	public void setId(String idAsString) {
		this.id = Integer.parseInt( idAsString );
	}

	@Override
	public String getLabel() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public UF getUf() {
		return uf;
	}

	public void setUf(UF uf) {
		this.uf = uf;
	}

	public Set<Orgao> getOrgaos() {
		return orgaos;
	}

	public void setOrgaos(Set<Orgao> orgaos) {
		this.orgaos = orgaos;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		Municipio other = (Municipio) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public BigDecimal getLongitude() {
		return longitude;
	}

	public void setLongitude(BigDecimal longitude) {
		this.longitude = longitude;
	}

	public BigDecimal getLatitude() {
		return latitude;
	}

	public void setLatitude(BigDecimal latitude) {
		this.latitude = latitude;
	}

}
