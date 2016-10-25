package br.gov.atlas.interceptor;

import java.lang.reflect.Method;

import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

public class ValidInputInterceptor {

	/**
	 * Método responsável por interceptar a chamada de outro método e efetuar as validações dos valores de entrada do sistema.
	 * 
	 * @param context - {@link InvocationContext}
	 * @return {@link Object}
	 * @throws Exception
	 */
	@AroundInvoke
	public Object intercept(InvocationContext context) throws Exception {
		Method method = context.getMethod();
		// MySQLCodec codec = new MySQLCodec(Mode.ANSI);

		for (Object param : context.getParameters()) {

			if (param instanceof String) {
				// Codificação dos dados utilizando a biblioteca ESAPI para evitar que caracteres especiais sejam interpretados
				// ESAPI.encoder().encodeForSQL(codec, param.toString());
			}

		}

		System.out.println("Interceptando o método: " + method.getName());
		return context.proceed();
	}

}
