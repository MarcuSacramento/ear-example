package br.gov.atlas.visitor;

import br.gov.atlas.dto.OrgaoImportadoDTO;

public class MunicipioVisitor implements Visitor {

	@Override
	public void visit(Visitable visitable) {
		if (visitable instanceof OrgaoImportadoDTO) {
			OrgaoImportadoDTO orgaoImportadoDTO = (OrgaoImportadoDTO) visitable;
			if (orgaoImportadoDTO.getMunicipio() == null) {
				orgaoImportadoDTO.getVisitorMessages()
				.add("Município inválido");
			}
		}
	}

}
