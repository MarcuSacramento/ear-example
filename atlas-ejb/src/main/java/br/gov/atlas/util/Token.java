package br.gov.atlas.util;

public class Token {
	
	public static String TOKEN_INICIO				= "";
	public static String TOKEN_OPERADOR 			= "O";
	public static String TOKEN_PARAMETRO	 		= "P";
	public static String TOKEN_ABRE_PARENTESES 		= "A";
	public static String TOKEN_FECHA_PARENTESES		= "F";
	public static String TOKEN_NUMERAL				= "D";
	
	private String token;
	private String tipo;
	
	public Token(String token, String tipo) {
		this.token = token;
		this.tipo = tipo;
	}
	
	/**
	 * @return the token
	 */
	public String getToken() {
		return token;
	}
	/**
	 * @param token the token to set
	 */
	public void setToken(String token) {
		this.token = token;
	}
	/**
	 * @return the tipo
	 */
	public String getTipo() {
		return tipo;
	}
	/**
	 * @param tipo the tipo to set
	 */
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getToken();
	}

}
