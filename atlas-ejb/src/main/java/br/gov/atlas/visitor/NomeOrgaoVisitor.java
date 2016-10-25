package br.gov.atlas.visitor;

import br.gov.atlas.dto.OrgaoImportadoDTO;

public class NomeOrgaoVisitor implements Visitor {

	private static final int TAMANHO_MAXIMO = 256;

	@Override
	public void visit(Visitable visitable) {
		OrgaoImportadoDTO orgaoImportadoDTO = (OrgaoImportadoDTO) visitable;
		if (orgaoImportadoDTO.getNomeOrgao() == null
				|| orgaoImportadoDTO.getNomeOrgao().equals("")
				|| orgaoImportadoDTO.getNomeOrgao().length() > TAMANHO_MAXIMO) {
			orgaoImportadoDTO.getVisitorMessages()
			.add("Nome do órgão inválido");
		}
	}

}
