package br.gov.atlas.validacao;

import java.lang.reflect.Field;
import java.util.Collection;

import br.gov.atlas.entidade.AtlasEntidade;
import br.gov.atlas.validacao.anotacoes.CampoObrigatorio;
import br.gov.atlas.validacao.anotacoes.ValidacaoType;

/**
 * Class AtlasValidator, abstrata, respons�vel por efetuar a valida��o.
 * 
 * @param <T> - tipo gen�rico
 */
public final class AtlasValidator {

	private AtlasValidatorResultado resultado;
	private Object registro;
	private ValidacaoType validacao;

	/**
	 * Cria uma nova inst�ncia de atlas validator.
	 * 
	 * @param registro - registro
	 * @param validacao - validacao
	 */
	public AtlasValidator(Object registro, ValidacaoType validacao) {
		this.registro = registro;
		this.validacao = validacao;
	}

	/**
	 * Executa validacao.
	 * 
	 */
	public void executaValidacao() {
		Field[] atributos = registro.getClass().getDeclaredFields();

		for (Field field : atributos) {
			field.setAccessible(true);

			// Valida��o da anota��o @CampoObrigatorio
			if (field.getAnnotation(CampoObrigatorio.class) != null) {
				validaCampoObrigatorio(field);
			}

			field.setAccessible(false);
		}

	}

	/**
	 * Recupera o resultado validacao.
	 * 
	 * @param registro - registro
	 * @return resultado validacao
	 */
	public final AtlasValidatorResultado getResultadoValidacao(Object registro) {
		return getResultado();
	}

	/**
	 * Recupera o resultado.
	 * 
	 * @return the resultado
	 */
	public final AtlasValidatorResultado getResultado() {
		if (resultado == null) {
			resultado = new AtlasValidatorResultado();
		}
		return resultado;
	}

	/**
	 * Recupera o registro.
	 * 
	 * @return o atributo registro
	 */
	public Object getRegistro() {
		return registro;
	}

	/**
	 * Recupera o validacao.
	 * 
	 * @return o atributo validacao
	 */
	public ValidacaoType getValidacao() {
		return validacao;
	}

	/**
	 * Preenche validacao.
	 * 
	 * @param validacao preenche o atributo validacao
	 */
	public void setValidacao(ValidacaoType validacao) {
		this.validacao = validacao;
	}

	/**
	 * Verifica se � executa validacao.
	 * 
	 * @param validacoes - validacoes
	 * @return <code>true</code> se verdadeiro, e <code>false</code> caso contr�rio.
	 */
	private boolean isExecutaValidacao(ValidacaoType[] validacoes) {
		for (ValidacaoType validacaoType : validacoes) {
			if (validacaoType == validacao) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Valida campo obrigatorio.
	 * 
	 * @param field - field
	 */
	@SuppressWarnings("rawtypes")
	private void validaCampoObrigatorio(Field field) {
		try {
			CampoObrigatorio co = field.getAnnotation(CampoObrigatorio.class);

			if (!isExecutaValidacao(co.validacao())) {
				return;
			}

			Object valor = field.get(registro);
			// Verifica se o campo est� preenchido, se � uma entidade preenchida ou uma lista n�o vazia
			if (valor == null || (valor.toString().trim().isEmpty()) || (valor instanceof AtlasEntidade && ((AtlasEntidade) valor).getId() == null)
					|| (valor instanceof Collection && ((Collection) valor).isEmpty())) {
				getResultado().addMotivo(AtlasMensagem.getInstance().criaMensagemERROR(co.codigoMensagem(), co.nomeCampo()));
				getResultado().addMotivo(AtlasMensagem.getInstance().criaMensagemERROR(co.codigoMensagem(), co.nomeCampo()));
			}
		} catch (IllegalArgumentException | IllegalAccessException e) {
			getResultado().addMotivo(AtlasMensagem.getInstance().criaMensagemERROR("VALIDACAO"));
		}
	}

}
