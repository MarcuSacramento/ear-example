package br.gov.atlas.visitor;

import br.gov.atlas.dto.OrgaoImportadoDTO;

public class NomeBairroVisitor implements Visitor {

	private static final int TAMANHO_MAXIMO = 128;

	@Override
	public void visit(Visitable visitable) {
		OrgaoImportadoDTO orgaoImportadoDTO = (OrgaoImportadoDTO) visitable;
		if (orgaoImportadoDTO.getBairro() == null
				|| orgaoImportadoDTO.getBairro().equals("")
				|| orgaoImportadoDTO.getBairro().length() > TAMANHO_MAXIMO) {
			orgaoImportadoDTO.getVisitorMessages().add(
					"Nome do bairro inválido");
		}
	}

}
