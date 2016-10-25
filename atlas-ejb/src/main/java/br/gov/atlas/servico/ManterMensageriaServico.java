package br.gov.atlas.servico;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.activation.DataHandler;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import br.gov.atlas.dao.MensageriaDao;
import br.gov.atlas.entidade.Anexo;
import br.gov.atlas.entidade.EmailUsuario;
import br.gov.atlas.entidade.Mensageria;
import br.gov.atlas.entidade.Orgao;
import br.gov.atlas.entidade.Representante;
import br.gov.atlas.entidade.Telefone;
import br.gov.atlas.entity.ListaPaginada;
import br.gov.atlas.enums.MensagemTag;
import br.gov.atlas.exception.AtlasAcessoBancoDadosException;
import br.gov.atlas.util.InputStreamDataSource;

@Stateless(name = "ManterMensageriaServico")
@LocalBean
public class ManterMensageriaServico extends AtlasServicoImpl {
	private static final String NAO_INFORMADO = "Não informado";
	public static final String TYPE_TEXT_PLAIN = "text/plain; charset=UTF-8";
	public static final String TYPE_TEXT_HTML = "text/html; charset=UTF-8";
	protected ResourceBundle webmail = ResourceBundle.getBundle("webmail");
	private MensageriaDao dao;
	@EJB(name = "OrgaoServico")
	private OrgaoServico orgaoServico;

	@Override
	public MensageriaDao getDao() {
		if(dao==null)
			dao=new MensageriaDao(em);
		return dao;
	}

	public ListaPaginada<Mensageria> recuperarMensagens(Mensageria mensageria) throws AtlasAcessoBancoDadosException {
		return getDao().recuperarMensagem(mensageria);
	}

	public ListaPaginada<Mensageria> recuperarDestinatarios(Mensageria mensageria) throws AtlasAcessoBancoDadosException {
		return getDao().recuperarDestinatarios(mensageria);
	}

	public List<Mensageria> consultarDestinatarios(Mensageria mensageria) throws AtlasAcessoBancoDadosException {
		return getDao().consultarDestinatarios(mensageria);
	}

	public void salvarMensagem(Mensageria mensageria,
			List<Orgao> destinatariosSelecionadosGeral)
					throws AtlasAcessoBancoDadosException {
		getDao().salvar(mensageria, destinatariosSelecionadosGeral);
	}

	public void enviarEmail(Mensageria mensagem,
			List<Orgao> destinatariosSelecionadosGeral,
			List<Anexo> anexos) throws MessagingException,
	FileNotFoundException, IOException, ParseException, AtlasAcessoBancoDadosException {
		for (Orgao orgaoDestinatario : destinatariosSelecionadosGeral) {
			orgaoDestinatario.setTelefones(orgaoServico.recuperarTelefones(orgaoDestinatario));

			// Enviando e-mail para os representantes dos órgãos
			List<Representante> representantes = orgaoDestinatario.getRepresentantes();
			for (Representante representanteOrgao : representantes) {
				postMail(representanteOrgao, mensagem, anexos, orgaoDestinatario);
			}
		}
	}

	private void postMail(Representante destinatario, Mensageria mensagem,
			List<Anexo> anexos, Orgao orgaoDestinatario) throws MessagingException,
	FileNotFoundException, IOException, ParseException {
		boolean debug = true;

		Session session = configurarPropriedadesDeAutenticacao();

		session.setDebug(debug);

		// Criando a mensagem
		Message msg = new MimeMessage(session);

		// TODO Setando o remetente >> verificar como enviar o e-mail em nome de outra pessoa
		InternetAddress addressFrom = new InternetAddress(webmail.getString("user"));
		msg.setFrom(addressFrom);

		// Recuperando o e-mail institucional do órgão
		String emailInstitucional = "";
		List<EmailUsuario> emails = orgaoDestinatario.getEmails();
		for (EmailUsuario emailUsuario : emails) {
			if(emailUsuario.isInstitucional()){
				emailInstitucional = emailUsuario.getNome();
				break;
			}
		}
		// Setando o destinatário "para"
		InternetAddress[] addressTo = new InternetAddress[]{new InternetAddress(destinatario.getEmail())};
		msg.setRecipients(Message.RecipientType.TO, addressTo);

		// Setando o destinatário "com cópia"
		if(emailInstitucional != null && !emailInstitucional.trim().equals("")){
			InternetAddress[] addressCc = new InternetAddress[]{new InternetAddress(emailInstitucional)};
			msg.setRecipients(Message.RecipientType.CC, addressCc);
		}

		// Informando assunto do email
		msg.setSubject(MimeUtility.encodeText(mensagem.getAssunto(), "UTF-8", null));

		// Cria o objeto que recebe o texto do corpo do email
		MimeBodyPart textPart = new MimeBodyPart();
		textPart.setContent(processarConteudoEmail(destinatario, orgaoDestinatario, emailInstitucional, mensagem), TYPE_TEXT_HTML);

		// Monta a mensagem SMTP  inserindo o conteudo, texto e anexos
		Multipart mps = new MimeMultipart();
		if(anexos != null && !anexos.isEmpty()){
			for (int index = 0; index < anexos.size(); index++) {

				// Armazenando os arquivos em memória
				Anexo anexo = anexos.get(index);
				ByteArrayInputStream streamArquivo = new ByteArrayInputStream(anexo.getArquivo());
				InputStreamDataSource dataSource = new InputStreamDataSource(anexo.getNome(), anexo.getMimetype(), streamArquivo);

				// Anexando ao e-mail
				MimeBodyPart attachFilePart = new MimeBodyPart();
				attachFilePart.setDataHandler(new DataHandler(dataSource));
				attachFilePart.setFileName(dataSource.getName());

				// Adiciona os anexos da mensagem
				mps.addBodyPart(attachFilePart, index);
			}
		}

		// adiciona o corpo texto da mensagem
		mps.addBodyPart(textPart);

		//adiciona a mensagem o conteudo texto e anexo
		msg.setContent(mps);

		Transport.send(msg);
	}

	private String processarConteudoEmail(Representante destinatario, Orgao orgao,
			String emailInstitucional, Mensageria mensageria) throws ParseException {
		String desinencia = (orgao.getDesinencia() != null) ? orgao.getDesinencia() : "";
		String textoEmail = mensageria.getMensagem();
		String desinenciaRepresentante = (destinatario.getDesinencia() != null) ? destinatario.getDesinencia() : "";
		String vocativoRepresentante = (destinatario.getVocativo() != null) ? destinatario.getVocativo() : "";
		String pronomeRepresentante = (destinatario.getPronome() != null) ? destinatario.getPronome() : "";
		String nomeCargoRepresentante = (destinatario.getCargo().getNome() != null) ? destinatario.getCargo().getNome() : "";
		String nomeRepresentante = (destinatario.getNome() != null) ? destinatario.getNome() : "";
		String emailRepresentante = (destinatario.getEmail() != null) ? destinatario.getEmail() : "";
		String enderecoOrgaoFormatado = (orgao.getEnderecoFormatado() != null) ? orgao.getEnderecoFormatado() : "";
		String nomeOrgao = (orgao.getNome() != null) ? orgao.getNome() : "";
		String tratamentoRepresentante = (destinatario.getCargo().getTratamento() != null) ? destinatario.getCargo().getTratamento() : "";

		textoEmail = textoEmail.replaceAll("\\["+MensagemTag.DESINENCIA_REPRESENTANTE.getValor()+"\\]", desinenciaRepresentante);
		textoEmail = textoEmail.replaceAll("\\["+MensagemTag.VOCATIVO_REPRESENTANTE.getValor()+"\\]", vocativoRepresentante);
		textoEmail = textoEmail.replaceAll("\\["+MensagemTag.PRONOME_REPRESENTANTE.getValor()+"\\]", pronomeRepresentante);
		textoEmail = textoEmail.replaceAll("\\["+MensagemTag.CARGO_REPRESENTANTE.getValor()+"\\]", nomeCargoRepresentante);
		textoEmail = textoEmail.replaceAll("\\["+MensagemTag.NOME_REPRESENTANTE.getValor()+"\\]", nomeRepresentante);
		textoEmail = textoEmail.replaceAll("\\["+MensagemTag.EMAIL_REPRESENTANTE.getValor()+"\\]", emailRepresentante);
		textoEmail = textoEmail.replaceAll("\\["+MensagemTag.ENDERECO_ORGAO.getValor()+"\\]", enderecoOrgaoFormatado);
		textoEmail = textoEmail.replaceAll("\\["+MensagemTag.NOME_ORGAO.getValor()+"\\]", nomeOrgao);
		textoEmail = textoEmail.replaceAll("\\["+MensagemTag.TELEFONES_ORGAO.getValor()+"\\]", formatarTelefones(orgao));
		textoEmail = textoEmail.replaceAll("\\["+MensagemTag.DESINENCIA.getValor()+"\\]", desinencia);
		textoEmail = textoEmail.replaceAll("\\["+MensagemTag.EMAIL_ORGAO.getValor()+"\\]", emailInstitucional);
		textoEmail = textoEmail.replaceAll("\\["+MensagemTag.TRATAMENTO_REPRESENTANTE.getValor()+"\\]", tratamentoRepresentante);

		return textoEmail;
	}

	private String formatarTelefones(Orgao orgao) throws ParseException {
		StringBuffer sbTelefone = new StringBuffer();
		if(orgao.getTelefones() != null && !orgao.getTelefones().isEmpty()){
			for (Telefone telefone : orgao.getTelefones()) {
				sbTelefone.append(telefone.getTelefoneFormatado()+", ");
			}
			if(sbTelefone.length() > 1){
				return sbTelefone.toString().substring(0, sbTelefone.toString().length()-2);
			}
		}
		return NAO_INFORMADO;
	}

	private Session configurarPropriedadesDeAutenticacao() {
		//Setando o host
		Enumeration<String> keys = webmail.getKeys();
		Properties props = new Properties();
		while (keys.hasMoreElements()) {
			String key = keys.nextElement();
			String value = webmail.getString(key);
			props.put(key, value);
		}

		// Setando a autenticação
		Authenticator auth = new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(webmail
						.getString("user"), webmail
						.getString("password"));
			}
		};
		Session session = Session.getDefaultInstance(props, auth);
		return session;
	}
}