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
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@Table(name = "uf", schema = "atlas")
@SequenceGenerator(name = "uf_id_uf_seq", sequenceName = "uf_id_uf_seq", allocationSize = 1)
@Entity
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "uf")
public class UF extends AtlasEntidade {


	private static final long serialVersionUID = -4716355960950684674L;

	@Id
	@Column(name = "id_uf")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "uf_id_uf_seq")
	@XmlAttribute(name = "id")
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "id_regiao")
	private Regiao  regiao;

	
	@Column(name = "cod_uf")
	@XmlAttribute(name = "sigla")
	private String sigla;

	@Column(name = "nme_uf")
	@XmlAttribute(name = "nome")
	private String nome;
	
	@OneToMany(mappedBy = "uf", fetch = FetchType.LAZY)
	private Set<Municipio> municipios;
	
	@Column(name = "num_longitude")
	private BigDecimal longitude;

	@Column(name = "num_latitude")
	private BigDecimal latitude;

	
	
	public UF() {
		//
	}
	
	public UF(String sigla, String nome) {
		this.sigla = sigla;
		this.nome  = nome;
	}
	
	
	
	@Override
	public Integer getId() {
		return id;
	}

	@Override
	public void setId(Integer id) {
		this.id = id;
	}

	@Override
	public String getLabel() {
		return null;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Set<Municipio> getMunicipios() {
		return municipios;
	}

	public void setMunicipios(Set<Municipio> municipios) {
		this.municipios = municipios;
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
		UF other = (UF) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public String getSigla() {
		return sigla;
	}

	public void setSigla(String sigla) {
		this.sigla = sigla;
	}

	public BigDecimal getLatitude() {
		return latitude;
	}

	public void setLatitude(BigDecimal latitude) {
		this.latitude = latitude;
	}

	public BigDecimal getLongitude() {
		return longitude;
	}

	public void setLongitude(BigDecimal longitude) {
		this.longitude = longitude;
	}

}
