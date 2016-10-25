package br.gov.atlas.validacao.anotacoes;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CampoObrigatorio {

	ValidacaoType[] validacao();

	String codigoMensagem() default "MSG1";

	String nomeCampo();
}
