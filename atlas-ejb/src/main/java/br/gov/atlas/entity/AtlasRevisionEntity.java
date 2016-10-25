package br.gov.atlas.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.envers.RevisionEntity;
import org.hibernate.envers.RevisionNumber;
import org.hibernate.envers.RevisionTimestamp;

@Entity
@Table(schema="auditoria", name="revinfo")
@RevisionEntity(AtlasRevisionListener.class)
public class AtlasRevisionEntity {
	private static final long serialVersionUID = -7197453146670996125L;

	@Id
    @GeneratedValue
    @RevisionNumber
    private int rev;

	@RevisionTimestamp
    private long revtstmp;

	@Column(name="nme_usuario")
	private String nome;
	
	public int getRev() {
		return rev;
	}
	public void setRev(int rev) {
		this.rev = rev;
	}
	
	public long getRevtstmp() {
		return revtstmp;
	}
	public void setRevtstmp(long revtstmp) {
		this.revtstmp = revtstmp;
	}
	
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + rev;
		result = prime * result + (int) (revtstmp ^ (revtstmp >>> 32));
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
		AtlasRevisionEntity other = (AtlasRevisionEntity) obj;
		if (rev != other.rev)
			return false;
		if (revtstmp != other.revtstmp)
			return false;
		return true;
	}
	
}
