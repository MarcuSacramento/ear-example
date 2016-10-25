package br.gov.atlas.entidade;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

@Table(name = "indicador", schema="atlas")
@SequenceGenerator(name = "indicador_id_indicador_seq", sequenceName = "indicador_id_indicador_seq", allocationSize = 1)
@Entity
public class Indicador extends AtlasEntidade {

	private static final long serialVersionUID = -7556468841772029728L;
	
	@Id
	@Column(name = "id_indicador")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "indicador_id_indicador_seq")
	private Integer id;
	
	@Column(name = "nme_indicador")
	private String nome;
	
	@Column(name = "sgl_indicador")
	private String sigla;
	
	@Column(name = "dsc_formula")
	private String formula;
	
	@Column(name = "dsc_explicativo")
	private String explicativo;
	
	@Column(name = "sgl_identificador")
	private String siglaIdentificador;

	@Transient
	private String formulaInterpretada;

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
		return getSigla() + " - " + getNome();
	}

	/**
	 * @return the sigla
	 */
	public String getSigla() {
		return sigla;
	}

	/**
	 * @param sigla the sigla to set
	 */
	public void setSigla(String sigla) {
		this.sigla = sigla;
	}

	/**
	 * @return the formula
	 */
	public String getFormula() {
		return formula;
	}

	/**
	 * @param formula the formula to set
	 */
	public void setFormula(String formula) {
		this.formula = formula;
	}

	/**
	 * @return the formulaInterpretada
	 */
	public String getFormulaInterpretada() {
		return formulaInterpretada;
	}

	/**
	 * @param formulaInterpretada the formulaInterpretada to set
	 */
	public void setFormulaInterpretada(String formulaInterpretada) {
		this.formulaInterpretada = formulaInterpretada;
	}

	/**
	 * @return the explicativo
	 */
	public String getExplicativo() {
		return explicativo;
	}

	/**
	 * @param explicativo the explicativo to set
	 */
	public void setExplicativo(String explicativo) {
		this.explicativo = explicativo;
	}

	public String getSiglaIdentificador() {
		return siglaIdentificador;
	}

	public void setSiglaIdentificador(String siglaIdentificador) {
		this.siglaIdentificador = siglaIdentificador;
	}
}
