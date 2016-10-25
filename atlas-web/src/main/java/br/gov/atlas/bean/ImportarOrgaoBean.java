package br.gov.atlas.bean;

import static br.gov.atlas.dto.OrgaoImportadoDTO.INDEX_COLUNA_IDENTIFICADOR_TELEFONE_02;
import static br.gov.atlas.dto.OrgaoImportadoDTO.INDEX_COLUNA_RESULTADO_IMPORTACAO;
import static br.gov.atlas.entidade.Representante.NAO_DIVULGAVEL;
import static br.gov.atlas.enums.TipoSituacaoRegistroOrgao.EM_ANALISE;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import br.gov.atlas.dto.OrgaoImportadoDTO;
import br.gov.atlas.entidade.Cargo;
import br.gov.atlas.entidade.EmailUsuario;
import br.gov.atlas.entidade.Municipio;
import br.gov.atlas.entidade.Orgao;
import br.gov.atlas.entidade.Representante;
import br.gov.atlas.entidade.Telefone;
import br.gov.atlas.entidade.TipoOrgao;
import br.gov.atlas.exception.AtlasAcessoBancoDadosException;
import br.gov.atlas.exception.AtlasNegocioException;
import br.gov.atlas.servico.AtlasServicoImpl;
import br.gov.atlas.servico.EmailServico;
import br.gov.atlas.servico.ManterCargoServico;
import br.gov.atlas.servico.ManterTipoOrgaoServico;
import br.gov.atlas.servico.MunicipioServico;
import br.gov.atlas.servico.OrgaoServico;
import br.gov.atlas.servico.RepresentanteServico;
import br.gov.atlas.servico.TelefoneServico;
import br.gov.atlas.util.ArquivoUtil;
import br.gov.atlas.util.AtlasWebUtil;
import br.gov.atlas.util.XlsUtil;
import br.gov.atlas.visitor.CepOrgaoVisitor;
import br.gov.atlas.visitor.EnderecoOrgaoVisitor;
import br.gov.atlas.visitor.HomepageVisitor;
import br.gov.atlas.visitor.MunicipioVisitor;
import br.gov.atlas.visitor.NomeBairroVisitor;
import br.gov.atlas.visitor.NomeOrgaoVisitor;
import br.gov.atlas.visitor.RepresentanteVisitor;
import br.gov.atlas.visitor.SiglaOrgaoVisitor;
import br.gov.atlas.visitor.TipoOrgaoVisitor;
import br.gov.atlas.visitor.Visitor;

@ManagedBean(name = "importarOrgaoBean")
@SessionScoped
public class ImportarOrgaoBean extends AtlasBean {

	private int totalRegistros;
	private int registrosInvalidos;
	private int registrosValidos;

	private LinkedList<Orgao> orgaosValidos;
	private LinkedList<OrgaoImportadoDTO> orgaosInvalidos;
	private LinkedList<Visitor> visitors;

	private static final int INDEX_PLANILHA_ORGAOS_IMPORTACAO = 1;

	private String nomeArquivoImportacao;

	@EJB(name = "OrgaoServico")
	private OrgaoServico orgaoServico;

	@EJB(name = "RepresentanteServico")
	private RepresentanteServico representanteServico;

	@EJB(name = "TelefoneServico")
	private TelefoneServico telefoneServico;

	@EJB(name = "EmailServico")
	private EmailServico emailServico;

	@EJB(name = "MunicipioServico")
	private MunicipioServico municipioServico;

	@EJB(name = "ManterTipoOrgaoServico")
	private ManterTipoOrgaoServico tipoOrgaoServico;

	@EJB(name = "ManterCargoServico")
	private ManterCargoServico manterCargoServico;

	private static final long serialVersionUID = 1L;

	public String importar() {
		limparDadosImportacao();
		return "orgaoImportacao";
	}

	private void limparDadosImportacao() {
		orgaosValidos = new LinkedList<>();
		orgaosInvalidos = new LinkedList<>();

		setTotalRegistros(0);
		setRegistrosValidos(0);
		setRegistrosInvalidos(0);
	}

	public void realizarImportacaoOrgaos(FileUploadEvent fileUploadEvent) {
		try {
			nomeArquivoImportacao = null;
			realizarUpload(fileUploadEvent);
			if (nomeArquivoImportacao != null) {
				extrairOrgaosPlanilha();
				if (!orgaosValidos.isEmpty()) {
					incluirOrgaosImportados();
				}
				informarResultadoImportacao();
			}
		} catch (Exception e) {
			error("PROBLEMA_IMPORTACAO");
		}
	}

	private void informarResultadoImportacao() {
		informarResumoImportacao();

		if (!orgaosValidos.isEmpty() && orgaosInvalidos.isEmpty()) {
			// Todos os registros importados com sucesso
			info("SUCESSO_IMPORTACAO_REGISTROS");
		} else {
			if (!orgaosValidos.isEmpty() && !orgaosInvalidos.isEmpty()) {
				// Alguns registros importados com sucesso e outros sem sucesso
				warn("SUCESSO_IMPORTACAO_REGISTROS_COM_RESSALVA");
			} else if (orgaosValidos.isEmpty() && !orgaosInvalidos.isEmpty()) {
				// Nenhum registro importado com sucesso
				warn("NENHUM_REGISTRO_IMPORTADO");
			} else {
				// Arquivo não possui registros
				warn("ARQUIVO_SEM_REGISTROS");
			}
		}
	}

	private void informarResumoImportacao() {
		registrosInvalidos = orgaosInvalidos.size();
		registrosValidos = orgaosValidos.size();
		totalRegistros = registrosValidos + registrosInvalidos;
	}

	public boolean isMostrarBotaoDownload() {
		boolean mostrarBotaoDownload = false;
		if (orgaosInvalidos != null && !orgaosInvalidos.isEmpty()) {
			mostrarBotaoDownload = true;
		}
		return mostrarBotaoDownload;
	}

	public void exportarOrgaosInvalidos() throws Exception {
		HSSFWorkbook workbook = XlsUtil
				.obterArquivoExcel(nomeArquivoImportacao);
		HSSFSheet planilha = XlsUtil.obterPlanilha(workbook,
				INDEX_PLANILHA_ORGAOS_IMPORTACAO);

		LinkedList<HSSFRow> linhasInvalidas = new LinkedList<>();
		informarErrosLinha(planilha, linhasInvalidas);
		removerLinhasValidas(planilha, linhasInvalidas);

		File file = new File(ArquivoUtil.obterPastaTemporariaAtlasComHorario(),
				"falha_importacao.xls");

		FileOutputStream outFile = new FileOutputStream(file);
		workbook.write(outFile);
		outFile.close();

		AtlasWebUtil.downloadFile(file);
	}

	public void downloadPlanilhaModelo() {

	}

	private void removerLinhasValidas(HSSFSheet planilha,
			LinkedList<HSSFRow> linhasInvalidas) {
		int lastRowNum = planilha.getLastRowNum();
		HSSFRow linha;

		for (int indexLinha = lastRowNum; indexLinha > 0; indexLinha--) {
			linha = planilha.getRow(indexLinha);
			if (!linhasInvalidas.contains(linha)) {
				if (indexLinha >= 0 && indexLinha < lastRowNum) {
					planilha.shiftRows(indexLinha + 1,
							planilha.getLastRowNum(), -1);
				}

				if (indexLinha == lastRowNum) {
					planilha.removeRow(linha);
				}
			}
		}
	}

	private void informarErrosLinha(HSSFSheet planilha,
			LinkedList<HSSFRow> linhasInvalidas) {
		HSSFRow linha;
		for (OrgaoImportadoDTO o : orgaosInvalidos) {
			linha = planilha.getRow(o.getNumeroLinha());
			HSSFCell cell = linha.createCell(INDEX_COLUNA_RESULTADO_IMPORTACAO);
			cell.setCellValue(o.getErrosValidacao());
			linhasInvalidas.add(linha);
		}
	}

	private void incluirOrgaosImportados()
			throws AtlasAcessoBancoDadosException, AtlasNegocioException {
		ArrayList<Representante> listaRepresentantes = null;

		// orgaoServico.salvar(orgaosValidos);

		for (Orgao o : orgaosValidos) {

			listaRepresentantes = new ArrayList<Representante>();
			if (o.getRepresentantes() != null
					&& !o.getRepresentantes().isEmpty()) {
				listaRepresentantes.addAll(o.getRepresentantes());
				o.setRepresentantes(null);
			}

			o.setDataCadastro(new Date());
			o.setDataAtualizacao(new Date());

			orgaoServico.salvar(o);

			if (o.getId() != null) {
				o.setEmails(salvarEmail(o, criarEmailOrgao(o)));
				o.setTelefones(salvarTelefone(o));
				o.setRepresentantes(salvarRepresentante(o, listaRepresentantes));
			}
		}
	}

	private List<Telefone> salvarTelefone(Orgao orgao) {
		try {
			// Removendo a lista de telefones da base
			List<Telefone> listaTelefonesBase = orgaoServico
					.recuperarTelefones(orgao);
			int sizeListTelefone = listaTelefonesBase.size();
			for (int i = 0; i < sizeListTelefone; i++) {
				telefoneServico.remover(listaTelefonesBase.get(i));
			}

			// Adicionando a lista de telefones informada na tela
			List<Telefone> listaComTelefones = new ArrayList<>();
			listaComTelefones.add(orgao.getPrimeiroTelefoneAsObject());
			listaComTelefones.add(orgao.getSegundoTelefoneAsObject());
			if (!listaComTelefones.isEmpty()) {
				for (Telefone telefone : listaComTelefones) {
					if (telefone != null) {
						telefone.setOrgao(orgao);
						telefoneServico.salvar(telefone);
					}
				}
				orgao.setTelefones(listaComTelefones);
			}
			return listaComTelefones;
		} catch (AtlasAcessoBancoDadosException | AtlasNegocioException e) {
			e.printStackTrace();
		}
		return null;
	}

	private List<Representante> salvarRepresentante(Orgao orgao,
			List<Representante> listaComRepresentantes) {
		List<Representante> listaComRepresentantesLocal = new ArrayList<>();
		for (Representante representante : listaComRepresentantes) {
			representante.setOrgao(orgao);
			try {
				representanteServico.salvar(representante);
			} catch (AtlasAcessoBancoDadosException e) {
				e.printStackTrace();
			} catch (AtlasNegocioException e) {
				e.printStackTrace();
			}
			listaComRepresentantesLocal.add(representante);
		}

		return listaComRepresentantesLocal;
	}

	private List<EmailUsuario> salvarEmail(Orgao orgao,
			EmailUsuario emailInstitucional) {

		List<EmailUsuario> listaComEmailInstitucional = new ArrayList<>();
		listaComEmailInstitucional.add(emailInstitucional);
		for (EmailUsuario email : listaComEmailInstitucional) {
			if (email != null) {
				email.setOrgao(orgao);
				try {
					emailServico.salvar(email);
				} catch (AtlasAcessoBancoDadosException | AtlasNegocioException e) {
					e.printStackTrace();
				}
			}
		}
		orgao.setEmails(listaComEmailInstitucional);

		return listaComEmailInstitucional;
	}

	private EmailUsuario criarEmailOrgao(Orgao o) {
		EmailUsuario emailInstitucional = o
				.recuperarEmailInstitucionalOuCriar();
		emailInstitucional.setOrgao(o);
		emailInstitucional.setNome(AtlasWebUtil.estaVazio(o
				.getDescricaoEmailInstitucional()) ? null : o
						.getDescricaoEmailInstitucional());
		emailInstitucional.setAsInstitucional();
		return emailInstitucional;
	}

	public void realizarUpload(FileUploadEvent fileUploadEvent)
			throws Exception {
		UploadedFile uploadedFile = fileUploadEvent.getFile();

		File file = new File(ArquivoUtil.obterPastaTemporariaAtlasComHorario(),
				uploadedFile.getFileName());
		try {
			file.createNewFile();
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(uploadedFile.getContents());
			fos.flush();
			fos.close();

			nomeArquivoImportacao = file.getAbsolutePath();
		} catch (Exception e) {
			throw e;
		}
	}

	private void extrairOrgaosPlanilha() throws Exception {
		try {
			LinkedList<LinkedList<Object>> listaOrgaosObject = XlsUtil
					.lerPlanilha(nomeArquivoImportacao,
							INDEX_PLANILHA_ORGAOS_IMPORTACAO,
							INDEX_COLUNA_IDENTIFICADOR_TELEFONE_02);
			// Remove a linha de cabeçalhos
			listaOrgaosObject.removeFirst();
			OrgaoImportadoDTO orgaoImportadoDTO;

			limparDadosImportacao();

			int i = 1;
			for (LinkedList<Object> linhaOrgao : listaOrgaosObject) {
				orgaoImportadoDTO = new OrgaoImportadoDTO(linhaOrgao);
				orgaoImportadoDTO.setNumeroLinha(i++);
				orgaoImportadoDTO
				.setMunicipio(extrairMunicipioImportado(orgaoImportadoDTO));
				orgaoImportadoDTO
				.setCargo(extrairCargoImportado(orgaoImportadoDTO));
				orgaoImportadoDTO
				.setTipoOrgao(extrairTipoOrgaoImportado(orgaoImportadoDTO));

				validarOrgaoImportado(orgaoImportadoDTO);

				if (orgaoImportadoDTO.getVisitorMessages().isEmpty()) {
					orgaosValidos.add(preencherOrgao(orgaoImportadoDTO));
				} else {
					orgaosInvalidos.add(orgaoImportadoDTO);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	private Cargo extrairCargoImportado(OrgaoImportadoDTO orgaoImportadoDTO) {
		Cargo cargo = null;
		if (AtlasWebUtil.naoEstaVazio(orgaoImportadoDTO.getNomeCargoRepresentante())){
			try {
				cargo = manterCargoServico.recuperPorNome(
						orgaoImportadoDTO.getNomeCargoRepresentante());
			} catch (AtlasAcessoBancoDadosException e) {
				// Não é necessário lançar exceção
				e.printStackTrace();
			}
		}
		return cargo;
	}

	private Municipio extrairMunicipioImportado(
			OrgaoImportadoDTO orgaoImportadoDTO) {
		Municipio municipio = null;
		if (AtlasWebUtil.naoEstaVazio(orgaoImportadoDTO.getNomeMunicipio())
				&& AtlasWebUtil.naoEstaVazio(orgaoImportadoDTO.getSiglaUF())) {
			try {
				municipio = municipioServico.recuperPorNomeMunicipioSiglaUf(
						orgaoImportadoDTO.getNomeMunicipio(),
						orgaoImportadoDTO.getSiglaUF());
			} catch (AtlasAcessoBancoDadosException e) {
				// Não é necessário lançar exceção
				e.printStackTrace();
			}
		}
		return municipio;
	}

	private TipoOrgao extrairTipoOrgaoImportado(
			OrgaoImportadoDTO orgaoImportadoDTO) {
		TipoOrgao tipoOrgao = null;
		if (AtlasWebUtil.naoEstaVazio(orgaoImportadoDTO.getNomeTipoOrgao())) {
			try {
				tipoOrgao = tipoOrgaoServico.recuperarPorNome(orgaoImportadoDTO
						.getNomeTipoOrgao());
			} catch (AtlasAcessoBancoDadosException e) {
				// Não é necessário lançar exceção
				e.printStackTrace();
			}
		}
		return tipoOrgao;
	}

	private void validarOrgaoImportado(OrgaoImportadoDTO orgaoImportadoDTO) {
		for (Visitor visitor : getListVisitors()) {
			orgaoImportadoDTO.accept(visitor);
		}
	}

	private LinkedList<Visitor> getListVisitors() {
		if (visitors == null) {
			visitors = new LinkedList<Visitor>();

			visitors.add(new MunicipioVisitor());
			visitors.add(new TipoOrgaoVisitor());
			visitors.add(new SiglaOrgaoVisitor());
			visitors.add(new NomeOrgaoVisitor());
			visitors.add(new EnderecoOrgaoVisitor());
			visitors.add(new CepOrgaoVisitor());
			visitors.add(new HomepageVisitor());
			visitors.add(new NomeBairroVisitor());
			visitors.add(new RepresentanteVisitor());
		}
		return visitors;
	}

	private Orgao preencherOrgao(OrgaoImportadoDTO orgaoImportadoDTO) {
		Orgao orgao = new Orgao();
		orgao.setIndicadorSituacao(EM_ANALISE.indiceSituacao());
		orgao.setMunicipio(orgaoImportadoDTO.getMunicipio());
		orgao.setTipoOrgao(orgaoImportadoDTO.getTipoOrgao());
		orgao.setSigla(orgaoImportadoDTO.getSiglaOrgao());
		orgao.setNome(orgaoImportadoDTO.getNomeOrgao());
		orgao.setDescricao(orgaoImportadoDTO.getDescricaoOrgao());
		orgao.setEndereco(orgaoImportadoDTO.getEnderecoOrgao());
		orgao.setCep(orgaoImportadoDTO.getCepOrgao());
		orgao.setHomePage(orgaoImportadoDTO.getHomepage());
		orgao.setBairro(orgaoImportadoDTO.getBairro());
		orgao.setRepresentantes(preencerRepresentanteImportado(orgaoImportadoDTO));
		return orgao;
	}

	private List<Representante> preencerRepresentanteImportado(
			OrgaoImportadoDTO orgaoImportadoDTO) {
		List<Representante> listaRepresentantes = new ArrayList<Representante>();
		Representante representante = new Representante();

		representante.setNome(orgaoImportadoDTO.getNomeRepresentante());
		representante.setEmail(orgaoImportadoDTO.getEmailRepresentante());
		representante.setCargo(orgaoImportadoDTO.getCargo());

		representante.setPrimeiroDdd(orgaoImportadoDTO
				.getDddTelefone01Representante());
		representante.setPrimeiroTelefone(orgaoImportadoDTO
				.getTelefone01Representante());
		representante.setPrimeiroTipoTelefone(orgaoImportadoDTO
				.getIdentificadorTelefone01Representante());

		representante.setSegundoDdd(orgaoImportadoDTO
				.getDddTelefone02Representante());
		representante.setSegundoTelefone(orgaoImportadoDTO
				.getTelefone02Representante());
		representante.setSegundoTipoTelefone(orgaoImportadoDTO
				.getIdentificadorTelefone02Representante());

		representante.setStatusDivulgacao(NAO_DIVULGAVEL);

		listaRepresentantes.add(representante);
		return listaRepresentantes;
	}

	@Override
	protected void efetuarPesquisa() throws AtlasAcessoBancoDadosException {
		// TODO Auto-generated method stub

	}

	@Override
	protected AtlasServicoImpl getServico() {
		return orgaoServico;
	}

	@Override
	protected String getTelaPesquisa() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String getTelaCadastro() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getTotalRegistros() {
		return totalRegistros;
	}

	public void setTotalRegistros(int totalRegistros) {
		this.totalRegistros = totalRegistros;
	}

	public int getRegistrosInvalidos() {
		return registrosInvalidos;
	}

	public void setRegistrosInvalidos(int registrosInvalidos) {
		this.registrosInvalidos = registrosInvalidos;
	}

	public int getRegistrosValidos() {
		return registrosValidos;
	}

	public void setRegistrosValidos(int registrosValidos) {
		this.registrosValidos = registrosValidos;
	}

}
