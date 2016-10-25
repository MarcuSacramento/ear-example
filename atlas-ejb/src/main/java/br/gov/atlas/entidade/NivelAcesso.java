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

@Table(name="seguranca.nivel_acesso")
@SequenceGenerator(name="seguranca.nivel_acesso_id_nivel_acesso_seq", sequenceName="seguranca.nivel_acesso_id_nivel_acesso_seq", allocationSize=1)
@Entity
public class NivelAcesso extends AtlasEntidade {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7986207924852178154L;
	
	@Id
	@Column(name="id_nivel_acesso")
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="seguranca.nivel_acesso_id_nivel_acesso_seq")
	private Integer id;

	@ManyToOne
	@JoinColumn(name="id_perfil")
	private Perfil perfil;
	
	@ManyToOne
	@JoinColumn(name="id_funcionalidade")
	private Funcionalidade funcionalidade;
	
	@Column(name="ind_nivel_acesso")
	private String indNivelAcesso;
	
	public static final String CONSULTA = "C";
	
	public static final String ADMINISTRADOR = "A";
	
	@Override
	public Integer getId() {
		return id;
	}

	@Override
	public void setId(Integer id) {
		this.id = id;
	}

	public Perfil getPerfil() {
		return perfil;
	}

	public void setPerfil(Perfil perfil) {
		this.perfil = perfil;
	}

	public Funcionalidade getFuncionalidade() {
		return funcionalidade;
	}

	public void setFuncionalidade(Funcionalidade funcionalidade) {
		this.funcionalidade = funcionalidade;
	}

	public String getIndNivelAcesso() {
		return indNivelAcesso;
	}

	public void setIndNivelAcesso(String indNivelAcesso) {
		this.indNivelAcesso = indNivelAcesso;
	}

	@Override
	public String getLabel() {
		// TODO Auto-generated method stub
		return null;
	}

}