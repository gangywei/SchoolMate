package schoolmate.control;

import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;
import com.sun.mail.util.MailSSLSocketFactory;
//http://blog.csdn.net/a60782885/article/details/69945622
public class SendEmail
{
   public static boolean sendMail(String to, String code) {  
	   String email = "1219615109@qq.com";
	   // 配置  
	   Properties prop=new Properties();  
	   // 设置邮件服务器主机名，这里是163  
	   prop.put("mail.host","smtp.163.com" );  
	   // 发送邮件协议名称  
	   prop.put("mail.transport.protocol", "smtp");  
	   // 是否认证  
	   prop.put("mail.smtp.auth", true);  
	   
	   try {  
		   // SSL加密  
		   MailSSLSocketFactory sf = null;  
		   sf = new MailSSLSocketFactory();  
		   // 设置信任所有的主机  
		   sf.setTrustAllHosts(true);  
		   prop.put("mail.smtp.ssl.enable", "true");  
		   prop.put("mail.smtp.ssl.socketFactory", sf);  
		   // 创建会话对象  
		   Session session = Session.getDefaultInstance(prop, new Authenticator() {  
			   // 认证信息，需要提供"用户账号","授权码"  
			   public PasswordAuthentication getPasswordAuthentication() {  
				   return new PasswordAuthentication("18439331592@163.com", "ab121961");  
			   }  
		   });  
		   // 是否打印出debug信息  
		   session.setDebug(true);  
		   // 创建邮件  
		   Message message = new MimeMessage(session);  
		   // 邮件发送者  
		   message.setFrom(new InternetAddress("18439331592@163.com"));  
		   // 邮件接受者  
		   message.addRecipient(Message.RecipientType.TO, new InternetAddress(email));  
		   // 邮件主题  
		   message.setSubject("河南师范大学-校友通讯客户端");  
		   String content = "<html><head></head><body><h1>请点击连接激活</h1></body></html>"; 
		   message.setContent(content, "text/html;charset=UTF-8");  
		   // 邮件发送  
		   Transport transport = session.getTransport();  
		   transport.connect();  
		   transport.sendMessage(message, message.getAllRecipients());  
		   transport.close();  
	   } catch (Exception e) {  
		   e.printStackTrace();  
	   }  
       return true;  
   }  
}
