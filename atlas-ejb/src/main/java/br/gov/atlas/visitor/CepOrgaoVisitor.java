package br.gov.atlas.visitor;

import br.gov.atlas.dto.OrgaoImportadoDTO;

public class CepOrgaoVisitor implements Visitor {

	private static final int TAMANHO_MAXIMO = 9;

	@Override
	public void visit(Visitable visitable) {
		if (visitable instanceof OrgaoImportadoDTO) {
			OrgaoImportadoDTO orgaoImportadoDTO = (OrgaoImportadoDTO) visitable;
			if (orgaoImportadoDTO.getCepOrgao() == null
					|| orgaoImportadoDTO.getCepOrgao().trim().equals("")
					|| orgaoImportadoDTO.getCepOrgao().trim().length() > TAMANHO_MAXIMO) {
				orgaoImportadoDTO.getVisitorMessages().add("CEP inválido");
			}
		}
	}

}
