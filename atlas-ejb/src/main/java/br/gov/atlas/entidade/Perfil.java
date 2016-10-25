package br.gov.atlas.entidade;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Table(name="seguranca.perfil")
@SequenceGenerator(name="seguranca.perfil_id_perfil_seq", sequenceName="seguranca.perfil_id_perfil_seq", allocationSize=1)
@Entity
public class Perfil extends AtlasEntidade {
	
	private static final long serialVersionUID = 4648120461183352560L;

	public static String SIT_ATIVO = "A";
	public static String SIT_INATIVO = "I";
	
	public static final String MASTER = "M";
	public static final String GESTOR = "G";
	public static final String COMUM = "C";
	
	public Perfil() {
		this.setSituacao(SIT_ATIVO);
	}
	
	@Id
	@Column(name="id_perfil")
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="seguranca.perfil_id_perfil_seq")
	private Integer id;
	
	@Column(name="nme_perfil")
	String nome;
	
	@Column(name="dsc_finalidade")
	String descricao;
	
	@Column(name="ind_situacao_perfil")
	String situacao;
	
	@Column(name="ind_tipo_perfil")
	private String tipo;
	
	@Column(name="dta_expiracao_perfil")
	Date dataExpiracao;
	
	@ManyToMany( mappedBy="perfis")
	private Set<Usuario> usuarios;

	@OneToMany(mappedBy="perfil", cascade = CascadeType.ALL, orphanRemoval=true, fetch = FetchType.EAGER)
	private List<NivelAcesso> niveisAcesso;
	
	@Override
	public Integer getId() {
		return this.id;
	}

	@Override
	public void setId(Integer id) {
		this.id=id;
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

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getSituacao() {
		return situacao;
	}
	
	public String getSituacaoFormatada() {
		if (SIT_ATIVO.equals(getSituacao())) {
			situacao = "Ativo";
		} else {
			situacao = "Inativo";
		}
		return situacao;
	}


	public void setSituacao(String situacao) {
		this.situacao = situacao;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public Date getDataExpiracao() {
		return dataExpiracao;
	}
	
	public String getDataExpiracaoFormatada() {
		try {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
			return simpleDateFormat.format( this.dataExpiracao );        
		} catch(NullPointerException e) {
			return "-"; // IGNORAR, SE VALOR NULO
		}
	}

	public void setDataExpiracao(Date dataExpiracao) {
		this.dataExpiracao = dataExpiracao;
	}
	
	public Set<Usuario> getUsuarios() {
		return usuarios;
	}

	public void setUsuarios(Set<Usuario> usuarios) {
		this.usuarios = usuarios;
	}

	public List<NivelAcesso> getNiveisAcesso() {
		if(niveisAcesso == null){
			niveisAcesso = new ArrayList<>();
		}
		return niveisAcesso;
	}

	public void setNiveisAcesso(List<NivelAcesso> niveisAcesso) {
		this.niveisAcesso = niveisAcesso;
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
		Perfil other = (Perfil) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
}
