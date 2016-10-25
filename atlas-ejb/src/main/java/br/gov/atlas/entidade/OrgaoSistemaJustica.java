package br.gov.atlas.entidade;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

@Table(name = "orgao_sistema_justica", schema="atlas")
@Inheritance(strategy = InheritanceType.JOINED)
@SequenceGenerator(name = "orgao_sistema_justica_id_orgao_sistema_justica_seq", sequenceName = "orgao_sistema_justica_id_orgao_sistema_justica_seq", allocationSize = 1)
@Entity
public class OrgaoSistemaJustica extends AtlasEntidade {

	private static final long serialVersionUID = 8115582803233319415L;

	@Id
	@Column(name = "id_orgao_sistema_justica")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "orgao_sistema_justica_id_orgao_sistema_justica_seq")
	private Integer id;
	
	@Column(name="nme_orgao")
	private String nome;
	
	@Column(name="sgl_orgao")
	private String sigla;
	
	@Column(name="id_orgao_sistema_justica_pai")
	private Integer idOrgaoPai;
	
	@Transient
	private String breadcrumb;
	

	/*
	 * (non-Javadoc)
	 * @see br.gov.atlas.entidade.AtlasEntidade#getId()
	 */
	@Override
	public Integer getId() {
		return id;
	}

	/*
	 * (non-Javadoc)
	 * @see br.gov.atlas.entidade.AtlasEntidade#setId(java.lang.Integer)
	 */
	@Override
	public void setId(Integer id) {
		this.id = id;
	}

	/*
	 * (non-Javadoc)
	 * @see br.gov.atlas.entidade.AtlasEntidade#getLabel()
	 */
	@Override
	public String getLabel() {
		// TODO Auto-generated method stub
		return null;
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
	 * @return the idOrgaoPai
	 */
	public Integer getIdOrgaoPai() {
		return idOrgaoPai;
	}

	/**
	 * @param idOrgaoPai the idOrgaoPai to set
	 */
	public void setIdOrgaoPai(Integer idOrgaoPai) {
		this.idOrgaoPai = idOrgaoPai;
	}

	/**
	 * @return the breadcrumb
	 */
	public String getBreadcrumb() {
		return breadcrumb;
	}

	/**
	 * @param breadcrumb the breadcrumb to set
	 */
	public void setBreadcrumb(String breadcrumb) {
		this.breadcrumb = breadcrumb;
	}


}
