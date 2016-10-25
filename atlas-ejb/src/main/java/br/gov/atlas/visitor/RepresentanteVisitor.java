package br.gov.atlas.visitor;

import java.util.Arrays;

import br.gov.atlas.dto.OrgaoImportadoDTO;
import br.gov.atlas.enums.TipoTelefone;

public class RepresentanteVisitor implements Visitor {

	private static final int TAMANHO_MAXIMO_NOME = 256;
	private static final int TAMANHO_MAXIMO_EMAIL = 512;
	private static final int TAMANHO_MAXIMO_DDD = 3;
	private static final int TAMANHO_MAXIMO_TELEFONE = 10;

	@Override
	public void visit(Visitable visitable) {
		OrgaoImportadoDTO orgaoImportadoDTO = (OrgaoImportadoDTO) visitable;
		visitDadosBasicos(orgaoImportadoDTO);
		visitDadosContato(orgaoImportadoDTO);
	}

	private void visitDadosBasicos(OrgaoImportadoDTO orgaoImportadoDTO) {
		if (orgaoImportadoDTO.getNomeRepresentante() == null
				|| orgaoImportadoDTO.getNomeRepresentante().equals("")
				|| orgaoImportadoDTO.getNomeRepresentante().length() > TAMANHO_MAXIMO_NOME) {
			orgaoImportadoDTO.getVisitorMessages().add(
					"Nome do representante inválido");
		}
		if (orgaoImportadoDTO.getCargo() == null) {
			orgaoImportadoDTO.getVisitorMessages().add(
					"Cargo do representante inválido");
		}
	}

	private void visitDadosContato(OrgaoImportadoDTO orgaoImportadoDTO) {
		if (orgaoImportadoDTO.getEmailRepresentante() != null
				&& !orgaoImportadoDTO.getEmailRepresentante().equals("")
				&& orgaoImportadoDTO.getEmailRepresentante().length() > TAMANHO_MAXIMO_EMAIL) {
			orgaoImportadoDTO.getVisitorMessages().add(
					"E-mail do representante inválido");
		}
		validarTelefone(orgaoImportadoDTO,
				orgaoImportadoDTO.getDddTelefone01Representante(),
				orgaoImportadoDTO.getTelefone01Representante(),
				orgaoImportadoDTO.getIdentificadorTelefone01Representante(), 1);
		validarTelefone(orgaoImportadoDTO,
				orgaoImportadoDTO.getDddTelefone02Representante(),
				orgaoImportadoDTO.getTelefone02Representante(),
				orgaoImportadoDTO.getIdentificadorTelefone02Representante(), 2);
	}

	private void validarTelefone(OrgaoImportadoDTO orgaoImportadoDTO,
			String ddd, String numero, String tipoTelefone, int indexTelefone) {

		validarDdd(orgaoImportadoDTO, ddd, numero, indexTelefone);
		validarNumeroTelefone(orgaoImportadoDTO, ddd, numero, indexTelefone);
		validarTipoTelefone(orgaoImportadoDTO, ddd, numero, tipoTelefone,
				indexTelefone);
	}

	private void validarTipoTelefone(OrgaoImportadoDTO orgaoImportadoDTO,
			String ddd, String numero, String tipoTelefone, int indexTelefone) {
		if (tipoTelefone != null && !tipoTelefone.trim().equals("")) {
			if (ddd == null || ddd.equals("") || numero == null
					|| numero.equals("")) {
				orgaoImportadoDTO.getVisitorMessages().add(
						"Identificador do telefone 0" + indexTelefone
						+ " inválido");
			} else if (Arrays.asList(TipoTelefone.values()).contains(
					tipoTelefone.toUpperCase())) {
				orgaoImportadoDTO.getVisitorMessages().add(
						"Identificador do telefone 0" + indexTelefone
						+ " inválido");
			}
		}
	}

	private void validarNumeroTelefone(OrgaoImportadoDTO orgaoImportadoDTO,
			String ddd, String numero, int indexTelefone) {
		if (numero != null && !numero.equals("")) {
			if (ddd == null || ddd.equals("")) {
				orgaoImportadoDTO.getVisitorMessages().add(
						"DDD 0" + indexTelefone
						+ " telefone do representante inválido");
			}
			if (numero.length() > TAMANHO_MAXIMO_TELEFONE) {
				orgaoImportadoDTO.getVisitorMessages().add(
						"Telefone 0" + indexTelefone
						+ " do representante inválido");
			}
		}
	}

	private void validarDdd(OrgaoImportadoDTO orgaoImportadoDTO, String ddd,
			String numero, int indexTelefone) {
		if (ddd != null && !ddd.equals("")) {
			if (ddd.length() > TAMANHO_MAXIMO_DDD) {
				orgaoImportadoDTO.getVisitorMessages().add(
						"DDD 0" + indexTelefone
						+ " telefone do representante inválido");
			}
			if (numero == null || numero.equals("")) {
				orgaoImportadoDTO.getVisitorMessages().add(
						"Telefone 0" + indexTelefone
						+ " do representante inválido");
			}
		}
	}

}
