import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.HashMap;

/**
 * Created by benma on 2017/4/10.
 */
@Controller
@RequestMapping("/")
public class CasController {
    private static final int AMOUNT = 3;// 3分钟
    private static Logger logger = LoggerFactory.getLogger(CasController.class);
    private static class userFacade {
        private static HashMap<String,String> users = Maps.newHashMap();

        public static String selectPwdByPrincipal(String principal) {
            return users.get(principal);

        }
    }

    private static HashMap users = Maps.newHashMap();

    static {
        users.put("test", "testpwd");
    }


    @RequestMapping("/login")
    public String login(HttpServletRequest req, HttpServletResponse resp, @Valid CasLoginVo casLoginVo, Model model) {
        int errorNum = 0;
        String pwdDB = userFacade.selectPwdByPrincipal(casLoginVo.getPrincipal());
        if(StringUtils.isEmpty(pwdDB) || !casLoginVo.getPassword().equals(pwdDB)){
            model.addAttribute("error","用户名或密码错误错误");
            return "login";
        }
        return "";

    }

    @RequestMapping("/validateTicketId")
    public void validateTicketId(HttpServletRequest req, HttpServletResponse resp, String svc, String tid) {
        logger.info("validateTicketId() 认证校验中....");
        CasTicket ticket = null;
        Boolean flag = Boolean.FALSE;
        if (!StringUtils.isEmpty(svc) && !StringUtils.isEmpty(tid)) {
            String domainTicket = CookieUtils.getDomainName(svc) + Constants.separator_duanheng + tid;

        }

        try {
            resp.getWriter().write(JSON.toJSONString(flag ? ticket : new CasTicket()));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }


    /**
     * cas服务中心---已认证则跳转至目标页面界面,若目标页未访问过,则根据tgt生成相应的ticket;未认证则跳转至登录页
     *
     * @author wangqi
     * @since 2017/1/17 14:07
     */
    @RequestMapping("/cas")
    public String cas(HttpServletRequest request, Model model) {


        return "login2";
    }

    /**
     * cas登出接口---认证中心注销,通知各品牌商城系统登出 passport.shopcmd.com/logout.do?redirect=http://xiaohua.shopcmd.com
     *
     * @author wangqi
     * @since 2017/1/17 14:00
     */
    @RequestMapping("/logout")
    public String logout(HttpServletRequest req, HttpServletResponse resp) {
        String redirect = req.getParameter("redirect");

        return "redirect:" + redirect; // return "/登出界面";
    }

}
