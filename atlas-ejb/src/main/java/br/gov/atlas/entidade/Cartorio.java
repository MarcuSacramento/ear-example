package br.gov.atlas.entidade;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Table(name = "cartorio", schema="atlas")
@Entity
public class Cartorio extends Orgao {

	private static final long serialVersionUID = 5795652577395742108L;
	
	@Column(name = "cod_cns")
	private Integer codigoCNS;

	public Integer getCodigoCNS() {
		return codigoCNS;
	}

	public void setCodigoCNS(Integer codigoCNS) {
		this.codigoCNS = codigoCNS;
	}

}
