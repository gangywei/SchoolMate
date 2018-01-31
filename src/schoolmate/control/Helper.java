package schoolmate.control;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;
import schoolmate.view.PencilMain;
import sun.misc.BASE64Encoder;

public class Helper {
	private Logger logger = PencilMain.logger;
	public static String EncoderByMd5(String str) throws NoSuchAlgorithmException, UnsupportedEncodingException{
        //确定计算方法
        MessageDigest md5=MessageDigest.getInstance("MD5");
        BASE64Encoder base64en = new BASE64Encoder();
        //加密后的字符串
        String newstr=base64en.encode(md5.digest(str.getBytes("utf-8")));
        return newstr;
    }
	
	public static boolean matchRegular(String str, String reg) {
		Pattern pattern = Pattern.compile(reg);
		Matcher matcher = pattern.matcher(str);
		return matcher.matches();
	}
}
