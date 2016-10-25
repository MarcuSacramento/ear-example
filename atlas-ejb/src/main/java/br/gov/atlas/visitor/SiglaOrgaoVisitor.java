package br.gov.atlas.visitor;

import br.gov.atlas.dto.OrgaoImportadoDTO;

public class SiglaOrgaoVisitor implements Visitor {

	private static final int TAMANHO_MAXIMO = 20;

	@Override
	public void visit(Visitable visitable) {
		OrgaoImportadoDTO orgaoImportadoDTO = (OrgaoImportadoDTO) visitable;
		if (orgaoImportadoDTO.getSiglaOrgao() == null || orgaoImportadoDTO.getSiglaOrgao().equals("")
				|| orgaoImportadoDTO.getSiglaOrgao().length() > TAMANHO_MAXIMO) {
			orgaoImportadoDTO.getVisitorMessages().add("Sigla do órgão inválida");
		}
	}

}
