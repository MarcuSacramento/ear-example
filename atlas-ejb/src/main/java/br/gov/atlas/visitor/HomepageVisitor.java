package br.gov.atlas.visitor;

import br.gov.atlas.dto.OrgaoImportadoDTO;

public class HomepageVisitor implements Visitor {

	private static final int TAMANHO_MAXIMO = 512;

	@Override
	public void visit(Visitable visitable) {
		if (visitable instanceof OrgaoImportadoDTO) {
			OrgaoImportadoDTO orgaoImportadoDTO = (OrgaoImportadoDTO) visitable;
			if (orgaoImportadoDTO.getHomepage() != null
					&& orgaoImportadoDTO.getHomepage().length() > TAMANHO_MAXIMO) {
				orgaoImportadoDTO.getVisitorMessages().add("Homepage inválida");
			}
		}
	}

}
