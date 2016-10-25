package br.gov.atlas.entidade;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Table(name="atlas.cargo")
@SequenceGenerator(name="atlas.seq_id_cargo", sequenceName="atlas.seq_id_cargo", allocationSize=1)
@Entity
public class Cargo extends AtlasEntidade{

	private static final long serialVersionUID = 9022050324374862139L;

	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="atlas.seq_id_cargo")
	@Column(name="id_cargo")
	private Integer id;

	@Column(name="nme_tratamento")
	private String tratamento;

	@Column(name="nme_cargo")
	private String nome;

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
		Cargo other = (Cargo) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public String getTratamento(){
		return tratamento;
	}

	public String getNome(){
		return nome;
	}

	public void setTratamento(String tratamento){
		this.tratamento = tratamento;
	}

	public void setNome(String nome){
		this.nome = nome;
	}

}
