package br.gov.atlas.util;

public class ThreadLocalUsuarioLogado {
	
	private static ThreadLocal<ThreadLocalUsuarioLogado> threadLocal = new ThreadLocal<ThreadLocalUsuarioLogado>();
	private String usuarioLogado;
		
	private ThreadLocalUsuarioLogado() {}
		
	public static ThreadLocalUsuarioLogado createThreadInstance(String usuarioLogado) {
		ThreadLocalUsuarioLogado thisThreadLocal = new ThreadLocalUsuarioLogado();
		thisThreadLocal.usuarioLogado = usuarioLogado;
		setCurrentInstance(thisThreadLocal);
		return thisThreadLocal;
	}
	
	public static ThreadLocalUsuarioLogado currentInstance() {
		return threadLocal.get();
	}
	
	public static void setCurrentInstance(ThreadLocalUsuarioLogado thisThreadLocal) {
		threadLocal.set(thisThreadLocal);
	}
	
	public static void release() {
		threadLocal.set(null);
	}
	
	public String getUsuarioLogado() {
		return threadLocal.get().usuarioLogado;
	}

}
