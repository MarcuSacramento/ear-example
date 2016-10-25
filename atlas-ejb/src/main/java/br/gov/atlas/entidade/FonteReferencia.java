package br.gov.atlas.entidade;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Table(name = "fonte_referencia", schema="atlas")
@SequenceGenerator(name = "atlas.fonte_referencia_id_fonte_referencia_seq", sequenceName = "atlas.fonte_referencia_id_fonte_referencia_seq", allocationSize = 1)
@Entity
public class FonteReferencia extends AtlasEntidade {

	private static final long serialVersionUID = 7874470061306777378L;

	@Id
	@Column(name = "id_fonte_referencia")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "atlas.fonte_referencia_id_fonte_referencia_seq")
	private Integer id;

	@Column(name = "nme_fonte")
	private String nome;

	@Column(name = "url_fonte")
	private String URL;
	
	@Column(name = "dsc_legislacao")
	String legislacao;
	
	@ManyToOne
	@JoinColumn(name = "id_termo")
	TermoGlossario termoGlossario;

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

	public String getURL() {
		return URL;
	}

	public void setURL(String uRL) {
		URL = uRL;
	}

	public String getLegislacao() {
		return legislacao;
	}

	public void setLegislacao(String legislacao) {
		this.legislacao = legislacao;
	}
	
	public TermoGlossario getTermoGlossario() {
		return termoGlossario;
	}

	public void setTermoGlossario(TermoGlossario termoGlossario) {
		this.termoGlossario = termoGlossario;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((URL == null) ? 0 : URL.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((nome == null) ? 0 : nome.hashCode());
		result = prime * result + ((termoGlossario == null) ? 0 : termoGlossario.hashCode());
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
		FonteReferencia other = (FonteReferencia) obj;
		if (URL == null) {
			if (other.URL != null)
				return false;
		} else if (!URL.equals(other.URL))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (nome == null) {
			if (other.nome != null)
				return false;
		} else if (!nome.equals(other.nome))
			return false;
		if (termoGlossario == null) {
			if (other.termoGlossario != null)
				return false;
		} else if (!termoGlossario.equals(other.termoGlossario))
			return false;
		return true;
	}

}
