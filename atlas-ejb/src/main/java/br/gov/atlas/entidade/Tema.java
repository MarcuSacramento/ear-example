package br.gov.atlas.entidade;

import java.util.Date;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Table(name="atlas.tema")
@SequenceGenerator(name="atlas.tema_id_tema_seq", sequenceName="atlas.tema_id_tema_seq", allocationSize=1)
@Entity
public class Tema extends AtlasEntidade {

	private static final long serialVersionUID = -3094077179565624270L;

	@Id
	@Column(name="id_tema")
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="atlas.tema_id_tema_seq")
	private Integer id;

	@Column(name="nme_tema")
	private
	String nome;

	@Column(name="dta_cadastro")
	private Date dataCadastro;

	@Column(name = "mapa", length = 2)
	private String indicadorMapa;

	@ManyToMany(mappedBy = "temas")
	private Set<Orgao> orgaos;

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
		Tema other = (Tema) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public Set<Orgao> getOrgaos() {
		return orgaos;
	}

	public void setOrgaos(Set<Orgao> orgaos) {
		this.orgaos = orgaos;
	}

	public String getIndicadorMapa() {
		return indicadorMapa;
	}

	public void setIndicadorMapa(String indicadorMapa) {
		this.indicadorMapa = indicadorMapa;
	}

}