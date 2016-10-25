package br.gov.atlas.entidade;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.gov.atlas.util.DataUtils;

@Entity
@Table(name="mensageria_orgao", schema="atlas")
public class MensageriaOrgao implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	@AttributeOverrides( {  
        @AttributeOverride(name = "idMensageria", column = @Column(name = "ID_MENSAGERIA", nullable = false)),  
        @AttributeOverride(name = "idOrgao", column = @Column(name = "ID_ORGAO", nullable = false)) }) 
	private MensageriaOrgaoID pk = new MensageriaOrgaoID();
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="id_mensageria", insertable=false, updatable=false)
	private Mensageria mensageria;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="id_orgao", insertable=false, updatable=false)
	private Orgao orgao;
	
	@Column(name="dta_visualizacao")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dataVisualizao;
	
	public MensageriaOrgaoID getPk() {
		return pk;
	}

	public void setPk(MensageriaOrgaoID id) {
		this.pk = id;
	}

	public Mensageria getMensagem() {
		return mensageria;
	}

	public void setMensagem(Mensageria mensagem) {
		mensageria = mensagem;
		pk.setIdMensageria(mensagem.getId());
	}

	public Orgao getOrgao() {
		return orgao;
	}

	public void setOrgao(Orgao orgao) {
		this.orgao = orgao;
		pk.setIdOrgao(orgao.getId());
	}

	public String getDataVisualizao() {
		String dtFormatada = "";
		if (dataVisualizao != null) {
			dtFormatada = DataUtils.formatDateToString(dataVisualizao, "dd/MM/yyyy");
		}
		return dtFormatada;
	}

	public void setDataVisualizao(Date dataVisualizao) {
		this.dataVisualizao = dataVisualizao;
	}
	
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		MensageriaOrgao that = (MensageriaOrgao) o;

		if (getPk() != null ? !getPk().equals(that.getPk())
				: that.getPk() != null)
			return false;

		return true;
	}

	public int hashCode() {
		return (getPk() != null ? getPk().hashCode() : 0);
	}
}