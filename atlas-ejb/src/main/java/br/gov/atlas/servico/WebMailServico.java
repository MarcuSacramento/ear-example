package br.gov.atlas.servico;

import java.util.Enumeration;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import br.gov.atlas.entidade.Problema;

@Stateless(name = "WebMailServico")
@LocalBean
public class WebMailServico {
	
	protected ResourceBundle webmail = ResourceBundle.getBundle("webmail");
	Address[] toUser;
	
	public void send(String assunto, String mensagem) throws AddressException, MessagingException{
		Enumeration<String> keys = webmail.getKeys();
		Properties props = new Properties();
		while (keys.hasMoreElements()) {
			String key = keys.nextElement();
			String value = webmail.getString(key);
			props.put(key, value);
		}
		/** Parâmetros de conexão com servidor Gmail */

		Session session = Session.getDefaultInstance(props,
				new javax.mail.Authenticator() {
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(webmail
								.getString("user"), webmail
								.getString("password"));
					}
				});
		/** Ativa Debug para sessão */
		session.setDebug(true);
		Message message = new MimeMessage(session);
		message.setFrom(new InternetAddress(webmail.getString("user"))); // Remetente
		message.setRecipients(Message.RecipientType.TO, getToUser());
		message.setSubject(assunto);// Assunto
		message.setContent(mensagem, "text/html");
		/** Método para enviar a mensagem criada */
		Transport.send(message);
		limparToUser();
	}

	public void sendAlteracaoSenha(String url, String usuario, String novaSenha, String email) throws AddressException, MessagingException {
		String mensagem = String.format(getMensagemAlteracaoSenha(), email, novaSenha, url, email);
		String assunto = "Redefinir Senha - Portal Atlas";
		setToUser(InternetAddress.parse(email));
		send(assunto, mensagem);
	}
	
	public void sendInformarProblema(Problema problema) throws AddressException, MessagingException{
		String mensagem = String.format(getMensagemInformarProblema(), 
										problema.getOrgao().getNome(),
										problema.getOrgao().getMunicipio().getNome(),
										problema.getOrgao().getMunicipio().getUf().getSigla(),
										problema.getTipoProblemaFormatado(), 
										problema.getDescricao());
		String assunto = "Erro Informado";
		send(assunto, mensagem);
	}
	
	public void sendNovoOrgao(String mensagem) throws AddressException, MessagingException{
		String assunto = "Novo Orgao Cadastrado";
		send(assunto, mensagem);
	}
	
	private String getMensagemAlteracaoSenha(){
		StringBuffer msg = new StringBuffer();
		msg.append("<p>Foi solicitada a alteração de senha para a seguinte conta:<br/><br/>");
		msg.append("Usuário:<font style='font-weight:bold;'> %s,</font><br/>");
		msg.append("Senha provisória:<font style='font-weight:bold;'> %s</font><br/><br/>");
		msg.append("<font style='font-weight:bold;'>Para realizar a alteração click no link abaixo:</font><br/>");
		msg.append("%s/gerenciamento/usuarios/alterarSenhaUsuario.faces?emailUsuario=%s&logar=true<br/><br/>");				
		return msg.toString();
	}	
	
	private String getMensagemInformarProblema(){
		StringBuffer msg = new StringBuffer();		
		msg.append("<p>Foi reportado um novo erro no cadastro do órgao.<br/><br/>");
		msg.append("Órgão:<font style='font-weight:bold;'> %s, %s - %s</font><br/>");
		msg.append("Tipo do Problema:<font style='font-weight:bold;'> %s</font><br/>");
		msg.append("Problema:<font style='font-weight:bold;'> %s</font><br/><br/>");
		msg.append("</p>");
		return msg.toString();
	}	
	
	public Address[] getToUser() {
		if (toUser == null) {
			try {
				toUser = InternetAddress.parse(webmail.getString("user"));
			} catch (AddressException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}		
		return toUser;
	}

	public void setToUser(Address[] toUser) {
		this.toUser = toUser;
	}
	
	private void limparToUser(){
		this.toUser = null;
	}

}
