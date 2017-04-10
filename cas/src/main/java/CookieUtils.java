import java.net.URLDecoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by benma on 2017/4/10.
 */
public class CookieUtils {
    public static String getDomainName(String url) {
        try {
            Pattern p = Pattern
                    .compile("(?<=http(s?)://|\\.)[^.]*?\\.(com\\.cn|org\\.cn|net\\.cn|com|cn|net|org|biz|info|cc|tv|me|edu|gov|uk|pro|top|so|la)", Pattern.CASE_INSENSITIVE);
            Matcher matcher = p.matcher(URLDecoder.decode(url, "UTF-8"));
            matcher.find();
            return matcher.group();//baidu.com
        } catch (Exception e) {
            return "";
        }
    }
}
