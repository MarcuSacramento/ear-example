package br.gov.atlas.entidade;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Table(name = "indicador_parametro", schema="atlas")
@SequenceGenerator(name = "indicador_parametro_id_indicador_parametro_seq", sequenceName = "indicador_parametro_id_indicador_parametro_seq", allocationSize = 1)
@Entity
public class Parametro extends AtlasEntidade {

	private static final long serialVersionUID = -7556468841772029728L;
	
	@Id
	@Column(name = "id_indicador_parametro")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "indicador_parametro_id_indicador_parametro_seq")
	private Integer id;
	
	@Column(name = "nme_parametro")
	private String nome;
	
	@Column(name = "dsc_parametro")
	private String valor;

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

	/**
	 * @return the valor
	 */
	public String getValor() {
		return valor;
	}

	/**
	 * @param valor the valor to set
	 */
	public void setValor(String valor) {
		this.valor = valor;
	}


}
