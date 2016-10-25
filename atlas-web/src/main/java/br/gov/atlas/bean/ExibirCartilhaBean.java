package br.gov.atlas.bean;

import java.io.File;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.primefaces.context.RequestContext;

import br.gov.atlas.entidade.Cartilha;
import br.gov.atlas.entity.CartilhaLinhaComTresRegistrosAdapter;
import br.gov.atlas.exception.AtlasAcessoBancoDadosException;
import br.gov.atlas.exception.AtlasNegocioException;
import br.gov.atlas.servico.AtlasServicoImpl;
import br.gov.atlas.servico.ExibirCartilhaServico;
import br.gov.atlas.util.ArquivoUtil;
import br.gov.atlas.util.AtlasWebUtil;

@SuppressWarnings("rawtypes")
@ManagedBean(name = "exibirCartilhaBean")
@javax.faces.bean.ViewScoped
public class ExibirCartilhaBean extends AtlasPaginatedBean {

	private static final long serialVersionUID = 1L;

	@EJB(name = "ExibirCartilhaServico")
	private ExibirCartilhaServico cartilhaServico;

	private List<CartilhaLinhaComTresRegistrosAdapter> cartilhasComLinhasDeTresRegistros;

	private boolean somentePublicacoesConsumidor;

	private String idCartilha;

	private Cartilha cartilhaSelecionada;

	@PostConstruct
	public void init() {
		pesquisar();
	}

	public void cliqueCartilha() throws Exception {
		if (idCartilha != null && !idCartilha.trim().isEmpty()) {

			cartilhaSelecionada = (Cartilha) cartilhaServico.pesquisarPorId(
					Integer.valueOf(idCartilha), Cartilha.class);

			if (cartilhaSelecionada != null) {
				RequestContext requestContext = RequestContext
						.getCurrentInstance();
				if (cartilhaSelecionada.isArquivoEmLink()) {
					requestContext.execute("window.open(\""
							+ cartilhaSelecionada.getLinkDoArquivoFisico()
							+ "\");");
				} else {
					// Necessário efetuar a chamada dessa forma pq o
					// remoteCommand executa uma chamada ajax, o que não é
					// válido para downlaod
					requestContext
					.execute("$('#buttonDownloadCartilha').click();");
				}
			}
		}
	}

	public void efetuarDownloadCartilha() throws Exception {
		if (cartilhaSelecionada != null) {
			File arquivoCartilhaTemp = ArquivoUtil.criarArquivo(
					cartilhaSelecionada.getNomeArquivoCartilha(),
					cartilhaSelecionada.getByteArquivoCartilha());
			AtlasWebUtil.downloadFile(arquivoCartilhaTemp);

			// Faces.sendFile(cartilhaSelecionada.getByteArquivoCartilha(),
			// cartilhaSelecionada.getNomeArquivoCartilha(), true);
		}
	}

	@Override
	protected void efetuarPesquisa() throws AtlasAcessoBancoDadosException {
		efetuarPesquisaPorPagina(getCurrentPage());
	}

	public void efetuarPesquisaPorPagina(Integer page)
			throws AtlasAcessoBancoDadosException {

		HttpServletRequest req = (HttpServletRequest) FacesContext
				.getCurrentInstance().getExternalContext().getRequest();
		somentePublicacoesConsumidor = req
				.getParameter("somentePublicacoesConsumidor") != null ? Boolean
						.valueOf(req.getParameter("somentePublicacoesConsumidor"))
						: false;

						this.cartilhasComLinhasDeTresRegistros = cartilhaServico
								.recuperarCartilhasComLinhasDeTresRegistros(page,
										somentePublicacoesConsumidor);
	}

	@Override
	protected AtlasServicoImpl getServico() {
		return cartilhaServico;
	}

	@Override
	protected String getTelaPesquisa() {
		return "abcDosDireitosConsulta";
	}

	@Override
	protected String getTelaCadastro() {
		return null;
	}

	public List<CartilhaLinhaComTresRegistrosAdapter> getCartilhasComLinhasDeTresRegistros() {
		return cartilhasComLinhasDeTresRegistros;
	}

	public void setCartilhasComLinhasDeTresRegistros(
			List<CartilhaLinhaComTresRegistrosAdapter> cartilhas) {
		this.cartilhasComLinhasDeTresRegistros = cartilhas;
	}

	@Override
	public Integer recuperarTotalDePaginas()
			throws AtlasAcessoBancoDadosException {
		return cartilhaServico
				.recuperarTotalDePaginas(somentePublicacoesConsumidor);
	}

	@Override
	public Boolean validar() throws AtlasNegocioException {
		return null;
	}

	public boolean isSomentePublicacoesConsumidor() {
		return somentePublicacoesConsumidor;
	}

	public void setSomentePublicacoesConsumidor(
			boolean somentePublicacoesConsumidor) {
		this.somentePublicacoesConsumidor = somentePublicacoesConsumidor;
	}

	public String getIdCartilha() {
		return idCartilha;
	}

	public void setIdCartilha(String idCartilha) {
		this.idCartilha = idCartilha;
	}

}
