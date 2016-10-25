package br.gov.atlas.entidade;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@SuppressWarnings("serial")
@Table(name = "seguranca.funcionalidade")
@SequenceGenerator(name = "seguranca.funcionalidade_id_funcionalidade_seq", sequenceName = "seguranca.funcionalidade_id_funcionalidade_seq", allocationSize = 1)
@Entity
public class Funcionalidade extends AtlasEntidade implements Comparable<Funcionalidade>{

	@Id
	@Column(name="id_funcionalidade")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seguranca.funcionalidade_id_funcionalidade_seq")
	private Integer id;
	
	@Column(name="cod_funcionalidade")
	private String codigo;
	
	@Column(name="nme_funcionalidade")
	private String nome;
	
	@Column(name="dsc_funcionalidade")
	private String descricao;
	
	@Override
	public Integer getId() {
		return id;
	}

	@Override
	public void setId(Integer id) {
		this.id = id;
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Override
	public String getLabel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int compareTo(Funcionalidade other) {
		return this.codigo.compareTo(other.getCodigo());
	}
}
