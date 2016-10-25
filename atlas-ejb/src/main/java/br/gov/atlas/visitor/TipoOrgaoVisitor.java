package br.gov.atlas.visitor;

import javax.ejb.EJB;

import br.gov.atlas.dto.OrgaoImportadoDTO;
import br.gov.atlas.servico.ManterTipoOrgaoServico;

public class TipoOrgaoVisitor implements Visitor {

	@EJB(name = "TipoOrgaoServico")
	private ManterTipoOrgaoServico tipoOrgaoServico;

	@Override
	public void visit(Visitable visitable) {
		OrgaoImportadoDTO orgaoImportadoDTO = (OrgaoImportadoDTO) visitable;
		if (orgaoImportadoDTO.getTipoOrgao() == null) {
			orgaoImportadoDTO.getVisitorMessages()
					.add("Tipo de órgão inválido");
		}
	}

}
