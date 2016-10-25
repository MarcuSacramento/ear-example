package br.gov.atlas.bean;

import static br.gov.atlas.util.ArquivoUtil.MSG_TAMANHO_ARQUIVO_INVALIDO;
import static br.gov.atlas.util.ArquivoUtil.MSG_TIPO_ARQUIVO_INVALIDO;
import static br.gov.atlas.util.TipoArquivo.DOC;
import static br.gov.atlas.util.TipoArquivo.GIF;
import static br.gov.atlas.util.TipoArquivo.JPEG;
import static br.gov.atlas.util.TipoArquivo.JPG;
import static br.gov.atlas.util.TipoArquivo.PDF;
import static br.gov.atlas.util.TipoArquivo.PNG;
import static br.gov.atlas.util.TipoArquivo.PPS;
import static br.gov.atlas.util.TipoArquivo.XLS;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.PhaseId;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

import br.gov.atlas.component.model.AtlasLazyDataModel;
import br.gov.atlas.entidade.Cartilha;
import br.gov.atlas.exception.AtlasAcessoBancoDadosException;
import br.gov.atlas.servico.AtlasServicoImpl;
import br.gov.atlas.servico.ExibirCartilhaServico;
import br.gov.atlas.util.ArquivoUtil;
import br.gov.atlas.util.TipoArquivo;

@ManagedBean(name = "manterCartilhaBean")
@SessionScoped
public class ManterCartilhaBean extends AtlasCrudBean<Cartilha> {

	private static final long serialVersionUID = 8547104624905661929L;

	@EJB(name = "ExibirCartilhaServico")
	ExibirCartilhaServico cartilhaServico;

	private String termoPesquisa;

	private Cartilha cartilhaFiltro;

	private AtlasLazyDataModel<Cartilha> cartilhas;

	private UploadedFile arquivoImagemCartilha;

	private UploadedFile arquivoCartilha;

	private boolean arquivoEmLink;

	private static final List<TipoArquivo> TIPOS_VALIDOS_ARQUIVO_IMAGEM_CARTILHA = new ArrayList<TipoArquivo>();

	private static final long TAMANHO_MAXIMO_ARQUIVO_IMAGEM_CARTILHA = new Long(
			100000);

	private static final List<TipoArquivo> TIPOS_VALIDOS_ARQUIVO_CARTILHA = new ArrayList<TipoArquivo>();

	private static final long TAMANHO_MAXIMO_ARQUIVO_CARTILHA = new Long(
			15728640);

	static {
		TIPOS_VALIDOS_ARQUIVO_IMAGEM_CARTILHA.add(PNG);
		TIPOS_VALIDOS_ARQUIVO_IMAGEM_CARTILHA.add(GIF);
		TIPOS_VALIDOS_ARQUIVO_IMAGEM_CARTILHA.add(JPG);
		TIPOS_VALIDOS_ARQUIVO_IMAGEM_CARTILHA.add(JPEG);

		TIPOS_VALIDOS_ARQUIVO_CARTILHA.add(DOC);
		TIPOS_VALIDOS_ARQUIVO_CARTILHA.add(PDF);
		TIPOS_VALIDOS_ARQUIVO_CARTILHA.add(PPS);
		TIPOS_VALIDOS_ARQUIVO_CARTILHA.add(XLS);
	}

	@PostConstruct
	public void init() throws AtlasAcessoBancoDadosException {
	}

	@Override
	public Boolean validar() {
		boolean valido = validarCamposObrigatorios();
		valido = valido & validarArquivos();
		return valido;
	}

	private boolean validarCamposObrigatorios() {
		boolean valido = true;
		Set<String> camposObrigatorios = new LinkedHashSet<String>();

		if (getEntidade().getTitulo() == null
				|| getEntidade().getTitulo().trim().isEmpty()) {
			valido = false;
			camposObrigatorios.add("Publicação");
		}
		if (arquivoImagemCartilha == null
				&& (getEntidade().getByteArquivo() == null)) {
			valido = false;
			camposObrigatorios.add("Capa da Publicação");
		}
		if (isArquivoEmLink()
				&& (getEntidade().getLinkDoArquivoFisico() == null || getEntidade()
				.getLinkDoArquivoFisico().trim().isEmpty())) {
			valido = false;
			camposObrigatorios.add("Link da Publicação");
		} else if (!isArquivoEmLink() && arquivoCartilha == null) {
			valido = false;
			camposObrigatorios.add("Arquivo da Publicação");
		}
		if (getEntidade().getIndicadorConsumidor() == null
				|| getEntidade().getIndicadorConsumidor().trim().isEmpty()) {
			valido = false;
			camposObrigatorios.add("Direito do Consumidor");
		}
		if (!valido) {
			StringBuilder sb = new StringBuilder();
			for (String campoObrigatorio : camposObrigatorios) {
				sb.append(campoObrigatorio).append(", ");
			}
			String msg = sb.toString();
			msg = msg.substring(0, msg.lastIndexOf(","));
			error(null, "MSG20", msg);
		}
		return valido;
	}

	private boolean validarArquivos() {
		boolean valido = true;
		Set<String> errosValidacao = new LinkedHashSet<String>();

		valido = validarArquivo(errosValidacao, arquivoImagemCartilha,
				TIPOS_VALIDOS_ARQUIVO_IMAGEM_CARTILHA,
				TAMANHO_MAXIMO_ARQUIVO_IMAGEM_CARTILHA);
		valido = valido
				|| (!isArquivoEmLink() && validarArquivo(errosValidacao,
						arquivoCartilha, TIPOS_VALIDOS_ARQUIVO_CARTILHA,
						TAMANHO_MAXIMO_ARQUIVO_CARTILHA));

		if (!valido) {
			for (String erro : errosValidacao) {
				error(erro);
			}
		}

		return valido;
	}

	private boolean validarArquivo(Set<String> errosValidacao,
			UploadedFile file, List<TipoArquivo> tiposValidos,
			Long tamanhoMaximo) {
		boolean valido = true;
		if (file == null) {
			valido = false;
		} else {
			TipoArquivo tipoArquivo = ArquivoUtil.getTipoArquivo(file
					.getFileName());
			if (!ArquivoUtil.isTipoValido(tipoArquivo, tiposValidos)) {
				valido = false;
				errosValidacao.add(MSG_TIPO_ARQUIVO_INVALIDO);
			}

			if (!ArquivoUtil.isTamanhoValido(file.getSize(), tamanhoMaximo)) {
				valido = false;
				errosValidacao.add(MSG_TAMANHO_ARQUIVO_INVALIDO);
			}
		}
		return valido;
	}

	public void limpar() {
		this.termoPesquisa = null;
	}

	public void limparPesquisar() throws AtlasAcessoBancoDadosException {
		limpar();
		carregarTermos(null);
	}

	public void alterarTipoLocalArquivo(AjaxBehaviorEvent event) {
		getEntidade().setLinkDoArquivoFisico(null);
		getEntidade().setByteArquivoCartilha(null);
		getEntidade().setNomeArquivoCartilha(null);
	}

	public StreamedContent getImagemOpen() {
		return new DefaultStreamedContent(new ByteArrayInputStream(
				getEntidade().getByteArquivo()));
	}

	@Override
	public String salvar() {
		if (validar()) {
			if (arquivoImagemCartilha != null) {
				getEntidade().setByteArquivo(
						arquivoImagemCartilha.getContents());
				getEntidade().setLinkDaImagem(
						arquivoImagemCartilha.getFileName());
			}
			if (!isArquivoEmLink() && arquivoCartilha != null) {
				getEntidade().setByteArquivoCartilha(
						arquivoCartilha.getContents());
				getEntidade().setNomeArquivoCartilha(
						arquivoCartilha.getFileName());
			}
			super.salvar();
		}
		return null;
	}

	@Override
	protected AtlasServicoImpl getServico() {
		return cartilhaServico;
	}

	@Override
	protected void efetuarPesquisa() throws AtlasAcessoBancoDadosException {
		cartilhas.setListaPaginada(cartilhaServico
				.recuperarCartilhas(cartilhaFiltro));
	}

	public void carregarTermos(String textoParcial)
			throws AtlasAcessoBancoDadosException {
		cartilhas = new AtlasLazyDataModel<Cartilha>();
		cartilhas.setServico(cartilhaServico);
		cartilhas.setMetodo("recuperarCartilhas");

		cartilhaFiltro = new Cartilha();
		cartilhaFiltro.setTitulo(textoParcial);
		cartilhas.setParametros(cartilhaFiltro);
		pesquisar();
	}

	public void pesquisarTermo() throws AtlasAcessoBancoDadosException {
		if (this.termoPesquisa != null && !this.termoPesquisa.trim().isEmpty()) {
			carregarTermos("%" + this.termoPesquisa + "%");
		} else {
			carregarTermos(null);
		}
	}

	public final String novo() {
		setEntidade(new Cartilha());
		return "informarPublicacoes";
	}

	@Override
	public String cancelar() {
		limpar();
		return "/admin/multimidia/publicacoes/exibirPublicacoes.faces";
	}

	public final String editar(Cartilha cartilha) {
		try {
			setEntidade(cartilha);
			initCarregarEntidade();
		} catch (AtlasAcessoBancoDadosException e) {
			error(e);
		}
		return getTelaCadastro();
	}

	public String recuperarTipoArquivo(Cartilha cartilha) {
		TipoArquivo tipoArquivo = ArquivoUtil.getTipoArquivo((cartilha
				.getLinkDoArquivoFisico() != null && !cartilha
				.getLinkDoArquivoFisico().trim().isEmpty()) ? cartilha
						.getLinkDoArquivoFisico() : cartilha.getNomeArquivoCartilha());
		return tipoArquivo != null ? tipoArquivo.getFormato() : "";
	}

	public StreamedContent getImagem() {
		FacesContext context = FacesContext.getCurrentInstance();
		Cartilha cartilha = null;
		if (context.getCurrentPhaseId() == PhaseId.RENDER_RESPONSE) {
			// So, we're rendering the HTML. Return a stub StreamedContent so
			// that it will generate right URL.
			return new DefaultStreamedContent();
		} else {
			// So, browser is requesting the image. Return a real
			// StreamedContent with the image bytes.
			String cartilhaId = context.getExternalContext()
					.getRequestParameterMap().get("cartilhaId");
			if (cartilhaId != null && !cartilhaId.trim().isEmpty()) {
				try {
					cartilha = (Cartilha) cartilhaServico.pesquisarPorId(
							Integer.valueOf(cartilhaId), Cartilha.class);
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (AtlasAcessoBancoDadosException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if (cartilha.getByteArquivo() != null) {
					return new DefaultStreamedContent(new ByteArrayInputStream(
							cartilha.getByteArquivo()));
				} else {
					FileInputStream fis = null;
					try {
						fis = new FileInputStream(FacesContext
								.getCurrentInstance().getExternalContext()
								.getRealPath("/pub/_imagens/semImagem.jpg"));
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return new DefaultStreamedContent(fis);
				}
			} else {
				return new DefaultStreamedContent();
			}
		}
	}

	@Override
	protected String getTelaPesquisa() {
		return "cartilhaConsulta";
	}

	@Override
	protected String getTelaCadastro() {
		return "informarPublicacoes";
	}

	public String getTermoPesquisa() {
		return termoPesquisa;
	}

	public void setTermoPesquisa(String termoPesquisa) {
		this.termoPesquisa = termoPesquisa;
	}

	public AtlasLazyDataModel<Cartilha> getCartilhas()
			throws AtlasAcessoBancoDadosException {
		if (cartilhas == null) {
			carregarTermos(null);
		}
		return cartilhas;
	}

	public void setCartilhas(AtlasLazyDataModel<Cartilha> cartilhas) {
		this.cartilhas = cartilhas;
	}

	public boolean isArquivoEmLink() {
		return arquivoEmLink;
	}

	public void setArquivoEmLink(boolean arquivoEmLink) {
		this.arquivoEmLink = arquivoEmLink;
	}

	public UploadedFile getArquivoImagemCartilha() {
		return arquivoImagemCartilha;
	}

	public void setArquivoImagemCartilha(UploadedFile arquivoImagemCartilha) {
		this.arquivoImagemCartilha = arquivoImagemCartilha;
	}

	public UploadedFile getArquivoCartilha() {
		return arquivoCartilha;
	}

	public void setArquivoCartilha(UploadedFile arquivoCartilha) {
		this.arquivoCartilha = arquivoCartilha;
	}

}
