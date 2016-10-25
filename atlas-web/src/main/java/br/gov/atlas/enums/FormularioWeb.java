package br.gov.atlas.enums;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.primefaces.component.inputtext.InputText;
import org.primefaces.component.selectmanycheckbox.SelectManyCheckbox;
import org.primefaces.component.selectoneradio.SelectOneRadio;

import br.gov.atlas.enums.TipoResposta;

public final class FormularioWeb {

	/**
	 * Constantes de css
	 */
	public static final String STYLE_INPUT = "sispaj_input_480 sispaj_input_formata";
	public static final String STYLE_ROW = "sispaj_sistema_formulario_tabela_linha_01, sispaj_sistema_formulario_tabela_linha_02";
	public static final String STYLE_HEADER = "sispaj_sistema_formulario_tabela_th_titulo";
	public static final String STYLE_GRID_CONTENT = "sispaj_sistema_formulario_tabela sispaj_sistema_formulario_tabela_linha_final";
	public static final String STYLE_BUTTON_VOLTAR = "sispaj_button_filtrar";

	/**
	 * path do formulario
	 */
	public static final String FORMULARIO_PATH = "configurarFormularioBean.formulario.";

	/**
	 * Enum com as querys do relatório de superavit consolidado por fonte.
	 */
	public static enum TIPO {
		TEXTO(TipoResposta.T.toString().toCharArray()[0], "respostaTexto", String.class, InputText.class), MULTIVALORADO(TipoResposta.M.toString()
				.toCharArray()[0], "respostaMultipla", ArrayList.class, SelectManyCheckbox.class), UNIVALORADO(TipoResposta.U.toString()
				.toCharArray()[0], "respostaUnica", String.class, SelectOneRadio.class);

		private final Character tipo;
		private final String atributo;
		private final Class<?> clazz;
		private final Class<?> UIComponent;
		private static Map<Character, FormularioWeb.TIPO> values;

		/**
		 * Construtor com parametros.
		 * 
		 * @param tipo
		 * @param atributo
		 * @param clazz
		 */
		private TIPO(Character tipo, String atributo, Class<?> clazz, Class<?> UIComponent) {
			this.tipo = tipo;
			this.atributo = atributo;
			this.clazz = clazz;
			this.UIComponent = UIComponent;
		}

		/**
		 * @return o atributo tipo
		 */
		public Character getTipo() {
			return tipo;
		}

		/**
		 * @return o atributo atributo
		 */
		public String getAtributo() {
			return atributo;
		}

		/**
		 * @return o atributo clazz
		 */
		public Class<?> getClazz() {
			return clazz;
		}

		/**
		 * @return o atributo uiComponent
		 */
		public Class<?> getUIComponent() {
			return UIComponent;
		}

		public static Map<Character, TIPO> getValues() {
			if (values == null) {
				values = new HashMap<Character, FormularioWeb.TIPO>();
				values.put(FormularioWeb.TIPO.TEXTO.getTipo(), FormularioWeb.TIPO.TEXTO);
				values.put(FormularioWeb.TIPO.MULTIVALORADO.getTipo(), FormularioWeb.TIPO.MULTIVALORADO);
				values.put(FormularioWeb.TIPO.UNIVALORADO.getTipo(), FormularioWeb.TIPO.UNIVALORADO);
			}
			return values;
		}
	}
}
