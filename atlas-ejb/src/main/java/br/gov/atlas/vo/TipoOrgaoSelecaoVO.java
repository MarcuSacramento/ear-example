package br.gov.atlas.vo;

import br.gov.atlas.entidade.TipoOrgao;
import br.gov.atlas.exception.AtlasAcessoBancoDadosException;

public class TipoOrgaoSelecaoVO {
	TipoOrgao tipoOrgao;
	Boolean selecionado;

	public TipoOrgaoSelecaoVO(TipoOrgao tipoOrgao) throws AtlasAcessoBancoDadosException {
		this.tipoOrgao = tipoOrgao;
		this.selecionado = false;
	}

	public TipoOrgao getTipoOrgao() {
		return tipoOrgao;
	}

	public void setTipoOrgao(TipoOrgao tipoOrgao) {
		this.tipoOrgao = tipoOrgao;
	}

	public Boolean getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(Boolean selecionado) {
		this.selecionado = selecionado;
	}
}