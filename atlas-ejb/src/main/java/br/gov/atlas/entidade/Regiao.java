package br.gov.atlas.entidade;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Table(name = "regiao", schema="atlas")
@SequenceGenerator(name = "regiao_id_regiao_seq", sequenceName = "regiao_id_regiao_seq", allocationSize = 1)
@Entity
public class Regiao extends AtlasEntidade {

	private static final long serialVersionUID = -7556468841772029728L;
	
	@Id
	@Column(name = "id_regiao")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "regiao_id_regiao_seq")
	private Integer id;
	
	@Column(name = "nme_regiao")
	private String nome;

	/**
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * @return the nome
	 */
	public String getNome() {
		return nome;
	}

	/**
	 * @param nome the nome to set
	 */
	public void setNome(String nome) {
		this.nome = nome;
	}

	/*
	 * (non-Javadoc)
	 * @see br.gov.atlas.entidade.AtlasEntidade#getLabel()
	 */
	@Override
	public String getLabel() {
		return getNome();
	}

}
