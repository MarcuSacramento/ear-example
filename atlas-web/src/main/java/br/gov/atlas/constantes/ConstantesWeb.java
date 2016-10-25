package br.gov.atlas.constantes;

public class ConstantesWeb {

	// TODO Tiago - Redirecionar para a home.
	public static final String PAGE_HOME = "home";
	public static final String PAGE_LOGIN = "/admin/autenticarUsuario/login.xhtml"; //antes estava assim "/admin/login.xhtml" e tava funcinando ao chamar localhost:8080/admin e agora não
	public static final String PAGE_ACESSIBILIDADE = "/acessibilidade.xhtml";

	public static final String LOGIN_SUCESSO = "/admin/conteudo/enderecosJustica/exibirOrgao.xhtml"; // "/admin/template/template_atlas_admin.xhtml";
	public static final String LOGIN_SAIR = "logout";

	public static final String USUARIO_LOGADO = "usuarioLogado";

	public static final String[] PAGINAS_PUBLICAS = {
		    //PAGINA PARA REDEFINIÇÂO DE SENHA
			"/admin/autenticarUsuario/redefinirSenhaUsuario.xhtml",
			"/admin/gerenciamento/usuarios/alterarSenhaUsuario.xhtml",
			"/pub/index.xhtml",
			// CENTRAIS DE CONTEÚDOS
			"/pub/centraisDeConteudo/exibirCentraisDeConteudos.xhtml",
			"/pub/centraisDeConteudo/publicacoes.xhtml",
			"/pub/centraisDeConteudo/indicadores.xhtml",
			"/pub/centraisDeConteudo/indicadores_preDefinidos.xhtml",
			"/pub/centraisDeConteudo/indicadores_relatoriosAnual.xhtml",
			"/pub/centraisDeConteudo/indicadores_orgaos.xhtml",
			"/pub/centraisDeConteudo/monitoramentoIndicadores.xhtml",
			"/pub/centraisDeConteudo/indicadores_relatoriosPrototipo.xhtml",
			"/pub/centraisDeConteudo/indicadores_referencias.xhtml",
			"/pub/centraisDeConteudo/videos.xhtml",
			"/pub/centraisDeConteudo/exibirVideos.xhtml",
			"/pub/centraisDeConteudo/aplicativos.xhtml",
			// SOBRE
			"/pub/sobre/exibirSobre.xhtml",
			"/pub/sobre/portasDaJustica/consultarPortasDaJustica.xhtml",
			"/pub/sobre/portasDaJustica/informarPortasDaJustica.xhtml",
			"/pub/sobre/mapaConsumidor/consultarMapaConsumidor.xhtml",
			"/pub/sobre/oAtlas/exibirOAtlas.xhtml",
			"/pub/sobre/dicionario/exibirDicionario.xhtml",
			"/pub/sobre/dicionario/exibirDicionarioConsulta.xhtml",
			"/pub/sobre/dicionario/exibirDicionarioDetalhe.xhtml",
			"/pub/sobre/parceiros/exibirParceiro.xhtml",
			"/pub/sobre/projetos/exibirProjeto.xhtml",
			// SOBRE O SITE
			"/pub/sobreOSite/exibirSobreOSite.xhtml",
			"/pub/sobreOSite/acessibilidade/exibirAcessibilidade.xhtml",
			"/pub/sobreOSite/mapaDoSite/exibirMapaDoSite.xhtml",
			// VERSÃO
			"/pub/versao.xhtml" };
}
