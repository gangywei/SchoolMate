package schoolmate.control;

import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
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
import javax.swing.JOptionPane;

import com.sun.mail.util.MailSSLSocketFactory;

import schoolmate.view.PencilMain;

/**
 * 邮件管理器
 * java 实现邮件的发送， 抄送及多附件
 * @author zhuxiongxian
 * @version 1.0
 * @created at 2016年10月8日 下午3:52:11
 */
public class EmailManager {
	
	private static String smtpHost = "smtp.163.com";
	private static String userName = "18439331592@163.com";
	private static String userPwd = "ab121961";
	private static String sendName = "河南师范大学校友办";
	private static Session session;
	
	public EmailManager() {  
		Properties prop=new Properties();  // 配置  
		prop.put("mail.host",smtpHost);  // 设置邮件服务器主机名，这里是163  
		prop.put("mail.transport.protocol", "smtp");  // 发送邮件协议名称  
		prop.put("mail.smtp.auth", true);  // 是否认证  
		try {  
			// SSL加密  
			MailSSLSocketFactory sf = null;  
			sf = new MailSSLSocketFactory();  
			Multipart mp = new MimeMultipart(); 
			// 设置信任所有的主机  
			sf.setTrustAllHosts(true);  
			prop.put("mail.smtp.ssl.enable", "true");  
			prop.put("mail.smtp.ssl.socketFactory", sf);  
			// 创建会话对象  
			session = Session.getDefaultInstance(prop, new Authenticator() {  
				// 认证信息，需要提供"用户账号","授权码"  
				public PasswordAuthentication getPasswordAuthentication() {  
					return new PasswordAuthentication(userName, userPwd);  
				}  
			});  
			// 是否打印出debug信息  
			session.setDebug(false);  
		} catch (Exception e) {  
			e.printStackTrace();  
		}  
	}
	
	public void sendMail(String toEmail,String main,String[] fileList) throws UnsupportedEncodingException{
		Message mimeMsg = new MimeMessage(session);  // MIME邮件对象 
		Multipart mPart = new MimeMultipart(); //// Multipart对象,邮件内容,标题,附件等内容均添加到其中后再生成MimeMessage对象 
		try{
			// 邮件发送者  
			mimeMsg.setFrom(new InternetAddress(userName,sendName));  
			// 邮件接受者  
			mimeMsg.addRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));  
			// 邮件主题  
			mimeMsg.setSubject("河南师范大学-校友通讯客户端");  
			BodyPart bodyPart = new MimeBodyPart(); 
			String content = "<html><head></head><body>"+main+"</body></html>"; 
	        bodyPart.setContent(content, "text/html;charset=utf-8");
	        mPart.addBodyPart(bodyPart);
	        // 设置附件
	        if (fileList != null && fileList.length > 0) {
	            for (int i = 0; i < fileList.length; i++) {
	                bodyPart = new MimeBodyPart();
	                FileDataSource fds = new FileDataSource(fileList[i]); 
	                bodyPart.setDataHandler(new DataHandler(fds)); 
	                bodyPart.setFileName(MimeUtility.encodeText(fds.getName(), "UTF-8", "B"));
	                mPart.addBodyPart(bodyPart); 
	            }
	        }
	        mimeMsg.setContent(mPart);  
			mimeMsg.saveChanges(); 
			// 邮件发送  
			Transport transport = session.getTransport();
			transport.connect();  
			transport.sendMessage(mimeMsg, mimeMsg.getAllRecipients());  
			transport.close();  
		} catch (MessagingException e) {
			if(e.getMessage().indexOf("SMTP host")>0){
				JOptionPane.showMessageDialog(null, "连接不上网络，检查网络连接是否正确！");
			}
			System.out.println(e.getMessage());
			PencilMain.logger.error("邮件群发出现异常"+e.getMessage());
		}
	}
}
