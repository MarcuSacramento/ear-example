package br.gov.atlas.interceptor;

import java.lang.reflect.Method;

import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

public class ValidInputInterceptor {

	/**
	 * M�todo respons�vel por interceptar a chamada de outro m�todo e efetuar as valida��es dos valores de entrada do sistema.
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
				// Codifica��o dos dados utilizando a biblioteca ESAPI para evitar que caracteres especiais sejam interpretados
				// ESAPI.encoder().encodeForSQL(codec, param.toString());
			}

		}

		System.out.println("Interceptando o m�todo: " + method.getName());
		return context.proceed();
	}

}
