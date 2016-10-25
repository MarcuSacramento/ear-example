package br.gov.atlas.visitor;

import br.gov.atlas.dto.OrgaoImportadoDTO;

public class EnderecoOrgaoVisitor implements Visitor {

	private static final int TAMANHO_MAXIMO = 512;

	@Override
	public void visit(Visitable visitable) {
		if (visitable instanceof OrgaoImportadoDTO) {
			OrgaoImportadoDTO orgaoImportadoDTO = (OrgaoImportadoDTO) visitable;
			if (orgaoImportadoDTO.getEnderecoOrgao() == null
					|| orgaoImportadoDTO.getEnderecoOrgao().trim().equals("")
					|| orgaoImportadoDTO.getEnderecoOrgao().trim().length() > TAMANHO_MAXIMO) {
				orgaoImportadoDTO.getVisitorMessages().add("Endereço inválido");
			}
		}
	}

}
