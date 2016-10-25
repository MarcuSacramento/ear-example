package br.gov.atlas.entidade;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Table(name = "email", schema="atlas")
@Inheritance(strategy = InheritanceType.JOINED)
@SequenceGenerator(name = "email_id_email_seq", sequenceName = "email_id_email_seq", allocationSize = 1)
@Entity
public class EmailUsuario extends AtlasEntidade {

	private static final long serialVersionUID = -1049696418471507092L;

	@Id
	@Column(name = "id_email")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "email_id_email_seq")
	private Integer id;
	
	@ManyToOne
	@JoinColumn(name = "id_orgao")
	private Orgao orgao;
	
	@Column(name = "nme_email")
	private String nome;
	
	@Column(name = "ind_email_institucional")
	private String indicadorInstitucional;
	
	public final static String INSTITUCIONAL = "I";
	
	public final static String PESSOAL = "P";
	
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

	public String getIndicadorInstitucional() {
		return indicadorInstitucional;
	}

	public void setIndicadorInstitucional(String indicadorInstitucional) {
		this.indicadorInstitucional = indicadorInstitucional;
	}
	
	public void setAsInstitucional() {
		this.indicadorInstitucional = INSTITUCIONAL;
	}

	public void setAsParticular() {
		this.indicadorInstitucional = PESSOAL;
	}
	
	public Orgao getOrgao() {
		return orgao;
	}

	public void setOrgao(Orgao orgao) {
		this.orgao = orgao;
	}
	
	public Boolean isInstitucional() {
		return this.indicadorInstitucional !=null && this.indicadorInstitucional.equals("I");
	}
	
	public Boolean isParticular() {
		return this.indicadorInstitucional !=null && this.indicadorInstitucional.equals("P");
	}

}
