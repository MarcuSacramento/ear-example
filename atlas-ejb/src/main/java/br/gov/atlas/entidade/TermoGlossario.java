package br.gov.atlas.entidade;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.hibernate.envers.RelationTargetAuditMode;

@Table(name = "termo_glossario", schema = "atlas")
@SequenceGenerator(name = "termo_glossario_id_termo_seq", sequenceName = "termo_glossario_id_termo_seq", allocationSize = 1)
@Entity
@Audited
@AuditTable(schema = "auditoria", value = "termo_glossario_aud")
public class TermoGlossario extends AtlasEntidade {

	private static final String SITUACAO_PROPOSTO = "P";

	private static final long serialVersionUID = 6084160213254796976L;

	@Id
	@Column(name = "id_termo")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "termo_glossario_id_termo_seq")
	private Integer id;

	@Column(name = "id_termo_origem")
	private Integer idTermoOrigem;

	@Column(name = "dta_inclusao")
	private Date dataInclusao;

	@Column(name = "ind_situacao")
	private String indicadorSituacao;

	@Column(name = "nme_termo")
	String nome;

	@Column(name = "dsc_termo")
	String descricao;

	@Column(name = "dsc_saiba_mais")
	String saibaMais;

	@ManyToOne
	@JoinColumn(name = "id_assunto")
	@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
	Assunto assunto;

	@OneToMany(mappedBy = "termoGlossario", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	@NotAudited
	private List<FonteReferencia> fontesReferencia;

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

	public String getLetraInicio() {
		String letraIni = nome.substring(0, 1);
		return letraIni;
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

	public String getSaibaMais() {
		return saibaMais;
	}

	public void setSaibaMais(String saibaMais) {
		this.saibaMais = saibaMais;
	}

	public Assunto getAssunto() {
		return assunto;
	}

	public void setAssunto(Assunto assunto) {
		this.assunto = assunto;
	}

	public List<FonteReferencia> getFontesReferencia() {
		if (fontesReferencia == null) {
			fontesReferencia = new ArrayList<FonteReferencia>();
		}
		return fontesReferencia;
	}

	public void setFontesReferencia(List<FonteReferencia> fontesReferenia) {
		this.fontesReferencia = fontesReferenia;
	}

	public Integer getIdTermoOrigem() {
		return idTermoOrigem;
	}

	public void setIdTermoOrigem(Integer idTermoOrigem) {
		this.idTermoOrigem = idTermoOrigem;
	}

	public Date getDataInclusao() {
		return dataInclusao;
	}

	public void setDataInclusao(Date dataInclusao) {
		this.dataInclusao = dataInclusao;
	}

	public String getIndicadorSituacao() {
		return indicadorSituacao;
	}

	public void setIndicadorSituacao(String indicadorSituacao) {
		this.indicadorSituacao = indicadorSituacao;
	}

	public boolean isProposto() {
		if (getIndicadorSituacao().equals(SITUACAO_PROPOSTO)) {
			return true;
		}
		return false;
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
		TermoGlossario other = (TermoGlossario) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}
