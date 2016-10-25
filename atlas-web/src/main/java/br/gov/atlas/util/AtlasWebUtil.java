package br.gov.atlas.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;

public class AtlasWebUtil {

	private AtlasWebUtil() {
	}

	public static boolean estaVazio(String valor) {
		return !naoEstaVazio(valor);
	}

	public static boolean naoEstaVazio(String valor) {
		boolean estaVazio = false;
		if (valor != null && !valor.trim().equals("")) {
			estaVazio = true;
		}
		return estaVazio;
	}

	public static void downloadFile(File file) throws Exception {
		HttpServletResponse response = ((HttpServletResponse) FacesContext
				.getCurrentInstance().getExternalContext().getResponse());

		response.reset();
		response.setContentType("application");
		response.setHeader("Content-disposition", "attachment; filename=\""
				+ file.getName() + "\"");

		BufferedInputStream bis = new BufferedInputStream(new FileInputStream(
				file));

		int rLength = -1;

		byte[] buffer = new byte[1000];

		while ((rLength = bis.read(buffer, 0, 100)) != -1) {
			response.getOutputStream().write(buffer, 0, rLength);
		}

		response.getOutputStream().flush();

		bis.close();
		response.getOutputStream().close();

		FacesContext.getCurrentInstance().responseComplete();
	}

}
