package br.gov.atlas.entidade;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Table(name="org.pessoa")
@SequenceGenerator(name="pessoa_id_pessoa_seq", sequenceName="pessoa_id_pessoa_seq", allocationSize=1)
@Entity
public class Pessoa extends AtlasEntidade {

	private static final long serialVersionUID = -1485692502341352392L;
	
	@Id
	@Column(name="id_pessoa")
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="pessoa_id_pessoa_seq")
	private Integer id;
	
	@Column(name="nme_pessoa")
	String nome;
	
	@Column(name="cod_email")
	String email;
	
	@Column(name="cod_cpf")
	String cpf;
	
	@Column(name="cod_ddd")
	String ddd;
	
	@Column(name="cod_telefone")
	String telefone;
	
	@Column(name="ind_situacao")
	String situacao;
	
	@Column(name="ind_sexo")
	String sexo;
	
	//@OneToOne(cascade=CascadeType.ALL)
	//Usuario usuario;
	

	@Override
	public Integer getId() {
		return null;
	}

	@Override
	public void setId(Integer id) {
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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	public String getDdd() {
		return ddd;
	}

	public void setDdd(String ddd) {
		this.ddd = ddd;
	}

	public String getTelefone() {
		return telefone;
	}

	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}

	public String getSituacao() {
		return situacao;
	}

	public void setSituacao(String situacao) {
		this.situacao = situacao;
	}

	public String getSexo() {
		return sexo;
	}

	public void setSexo(String sexo) {
		this.sexo = sexo;
	}

	/*
	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}*/

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
		Pessoa other = (Pessoa) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
	

}
