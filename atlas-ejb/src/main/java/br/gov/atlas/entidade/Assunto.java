package br.gov.atlas.entidade;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Table(name = "assunto", schema="atlas")
@SequenceGenerator(name = "assunto_id_assunto_seq", sequenceName = "assunto_id_assunto_seq", allocationSize = 1)
@Entity
public class Assunto extends AtlasEntidade {

	private static final long serialVersionUID = -7084733799753463972L;

	@Id
	@Column(name = "id_assunto")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "assunto_id_assunto_seq")
	private Integer id;
	
	@Column(name = "nme_assunto")
	String nome;
	
	@OneToMany(mappedBy = "assunto", cascade=CascadeType.ALL, orphanRemoval=true, fetch = FetchType.LAZY)
	private List<TermoGlossario> termosGlossario;

	public Assunto() {
	}
	
	public Assunto(Integer id) {
		this.id = id;
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
	
	public List<TermoGlossario> getTermosGlossario() {
		return termosGlossario;
	}

	public void setTermosGlossario(List<TermoGlossario> termosGlossario) {
		this.termosGlossario = termosGlossario;
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
		Assunto other = (Assunto) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
}
