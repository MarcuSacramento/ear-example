package br.gov.atlas.entidade;

import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;


@Table(name = "seguranca.usuario")
@SequenceGenerator(name = "seguranca.usuario_id_usuario_seq", sequenceName = "seguranca.usuario_id_usuario_seq", allocationSize = 1)
@Entity
@Audited
@AuditTable(schema="auditoria", value="usuario_aud")
public class Usuario extends AtlasEntidade {

	private static final long serialVersionUID = 4486480859586130509L;
	public static String SIT_ATIVO = "A";
	public static String SIT_BLOQUEADO = "B";
	public static String SIT_INATIVO = "I";

	@Id
	@Column(name = "id_usuario")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seguranca.usuario_id_usuario_seq")
	private Integer id;

	@Column(name = "nme_usuario")
	String nome;

	@Column(name = "cod_cpf")
	String cpf;

	@Column(name = "cod_email")
	String email;

	@Column(name = "cod_senha")
	String senha;

	@Column(name = "cod_ddd")
	String ddd;

	@Column(name = "cod_telefone")
	String telefone;

	@Column(name = "ind_situacao")
	String situacao;

	@Column(name = "ind_sexo")
	String sexo;

	@Column(name = "dta_inclusao")
	Date dataInclusao;
	
	@Transient
	boolean alterarExcluir;
	
	@ManyToMany
	@JoinTable(name = "seguranca.perfil_usuario", joinColumns = { @JoinColumn(name = "id_usuario") }, inverseJoinColumns = { @JoinColumn(name = "id_perfil") })
	@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
	private List<Perfil> perfis;

	@ManyToMany
	@JoinTable(name = "seguranca.orgao_usuario", joinColumns = { @JoinColumn(name = "id_usuario") }, inverseJoinColumns = { @JoinColumn(name = "id_orgao") })
	@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
	private List<Orgao> orgaos;

	@Override
	public Integer getId() {
		return this.id;
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

	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		/* MÁSCARA PARA ELIMINAR TODOS OS DÍGITOS NÃO-NUMÉRICOS. (fernando.rolim/ 13 set 2013) */
		StringBuffer sBuffer = new StringBuffer();
		Pattern p = Pattern.compile("[0-9]+");
		Matcher m = p.matcher(cpf);
		while (m.find()) 
			sBuffer.append(m.group());
		
		this.cpf = sBuffer.toString();
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
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
		/* MÁSCARA PARA ELIMINAR TODOS OS DÍGITOS NÃO-NUMÉRICOS. (fernando.rolim/ 16 set 2013) */
		StringBuffer sBuffer = new StringBuffer();
		Pattern p = Pattern.compile("[0-9]+");
		Matcher m = p.matcher(telefone);
		while (m.find()) 
			sBuffer.append(m.group());
		
		this.telefone = sBuffer.toString();
	}

	public String getSituacao() {
		return situacao;
	}

	public String getSituacaoFormatada() {
		if (SIT_ATIVO.equals(getSituacao())) {
			return "Ativo";
		} else if (SIT_BLOQUEADO.equals(getSituacao())) {
			return "Bloqueado";
		} else {
			return "Inativo";
		}
	}

	public void setSituacao(String situacao) {
		this.situacao = situacao;
	}

	public String getSexo() {
		return sexo;
	}

	public String getSexoFormatado() {
		if (this.sexo.equals("M")) {
			return "Masculino";
		} else {
			return "Feminino";
		}
	}

	public void setSexo(String sexo) {
		this.sexo = sexo;
	}

	public Date getDataInclusao() {
		return dataInclusao;
	}

	public void setDataInclusao(Date dataInclusao) {
		this.dataInclusao = dataInclusao;
	}

	public List<Perfil> getPerfis() {
		return perfis;
	}

	public void setPerfis(List<Perfil> perfis) {
		this.perfis = perfis;
	}
	
	public List<Orgao> getOrgaos() {
		return orgaos;
	}

	public void setOrgaos(List<Orgao> orgaos) {
		this.orgaos = orgaos;
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
		Usuario other = (Usuario) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public boolean isAlterarExcluir() {
		return alterarExcluir;
	}

	public void setAlterarExcluir(boolean alterarExcluir) {
		this.alterarExcluir = alterarExcluir;
	}
	
}
