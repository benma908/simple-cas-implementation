package util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by benma on 2017/4/10.
 */
public class DemoUtils {

    private static String[] chars = new String[]{"a", "b", "c", "d", "e", "f",
            "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s",
            "t", "u", "v", "w", "x", "y", "z", "0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H", "I",
            "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
            "W", "X", "Y", "Z"};

    /**
     * 得到Cookie的值, 不编码
     *
     * @param request
     * @param cookieName
     * @return
     */
    public static String getCookieValue(HttpServletRequest request, String cookieName) {
        return getCookieValue(request, cookieName, false);
    }

    //httponly 设置的
    public static void setCookie(boolean httpOnly, HttpServletRequest request, HttpServletResponse response, String cookieName, String cookieValue) {
        setCookie(request, response, cookieName, cookieValue, -1, null, httpOnly);
    }

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

    public static void setCookie(HttpServletRequest request, HttpServletResponse response, String cookieName, String cookieValue, int cookieMaxage, String encodeString, boolean httpOnly) {
        try {
            if (cookieValue == null) {
                cookieValue = "";
            } else {
                if (encodeString != null) {
                    cookieValue = URLEncoder.encode(cookieValue, encodeString);
                } else {
                    cookieValue = URLEncoder.encode(cookieValue, "utf-8");
                }
            }
            Cookie cookie = new Cookie(cookieName, cookieValue);
            // if (cookieMaxage > 0)
            cookie.setMaxAge(cookieMaxage);

            if (null != request) {// 设置域名的cookie
                String domainName = getDomainName(request.getRequestURL().toString());
                if (!"".equals(domainName)) {
                    cookie.setDomain(domainName);
                } else {
                    cookie.setDomain("localhost");
                }
            }
            cookie.setPath("/");
            cookie.setHttpOnly(httpOnly);
            response.addCookie(cookie);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 得到Cookie的值,
     *
     * @param request
     * @param cookieName
     * @return
     */
    public static String getCookieValue(HttpServletRequest request, String cookieName, boolean isDecoder) {
        Cookie[] cookieList = request.getCookies();
        if (cookieList == null || cookieName == null) {
            return null;
        }
        String retValue = null;
        try {
            for (Cookie aCookieList : cookieList) {
                if (aCookieList.getName().equals(cookieName)) {
                    if (isDecoder) {
                        retValue = URLDecoder.decode(aCookieList.getValue(), "UTF-8");
                    } else {
                        retValue = aCookieList.getValue();
                    }
                    break;
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return retValue;
    }

    public static void deleteCookie(HttpServletRequest request, HttpServletResponse response,
                                    String cookieName) {
        Cookie[] cookies = request.getCookies();
        if (null != cookies && cookies.length > 0) {
            for (Cookie c : cookies) {
                if (cookieName.equals(c.getName())) {
                    c.setMaxAge(0);
                    String domainName = getDomainName(request.getRequestURL().toString());
                    if (!"".equals(domainName)) {
                        c.setDomain(domainName);
                    } else {
                        c.setDomain("localhost");
                    }
                    c.setPath("/");
                    response.addCookie(c);
                }
            }
        }
    }

    public static Date getExpireTime(int unit, int AMOUNT) {
        Date dt = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(dt);
        c.add(Calendar.MINUTE, AMOUNT);
        return c.getTime();
    }

    /**
     * 根据UUID生成指定长随机串
     *
     * @param length
     * @return
     */
    public static String genUUIDLengthOf(int length) {
        StringBuilder shortBuffer = new StringBuilder();
        String uuid = UUID.randomUUID().toString().replace("-", "");
        for (int i = 0; i < length; i++) {
            String str = uuid.substring(i * 4, i * 4 + 4);
            int x = Integer.parseInt(str, 16);
            shortBuffer.append(chars[x % 0x3E]);
        }
        return shortBuffer.toString();
    }
}
