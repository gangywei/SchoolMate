package schoolmate.control;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
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
	
	//手机号正则表达式
	public static boolean matchPhone(String phone){
		String regPhone = "^1([358][0-9]|4[579]|66|7[0135678]|9[89])[0-9]{8}$";
		return Helper.matchRegular(phone, regPhone);
	}
	
	//得到当前时间戳
	public static long dataTime(String str){
		Date date = null;
		if(str!=null){
			SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
			try {
				date = df.parse(str);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			String timestamp = String.valueOf(date.getTime()/1000);  
			return Integer.valueOf(timestamp); 
		}else
			return new Date().getTime()/1000;
	}
	
	public static Object[] arrayUnique(String [] arr){
        //实例化一个set集合
        Set<String> set = new HashSet<String>();
        //遍历数组并存入集合,如果元素已存在则不会重复存入
        for (int i = 0; i < arr.length; i++)
            set.add(arr[i]);
        //返回Set集合的数组形式
        return set.toArray();
    }
}
