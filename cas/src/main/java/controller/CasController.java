package controller;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.google.common.net.HttpHeaders;
import entity.CasTicket;
import entity.Member;
import entity.vo.CasLoginVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import util.Constants;
import util.DemoUtils;
import util.TicketRepoUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by benma on 2017/4/10.
 */
@Controller
@RequestMapping("/")
public class CasController {
    private static final int AMOUNT = 3;// 3分钟
    private static Logger logger = LoggerFactory.getLogger(CasController.class);
    private static HashMap users = Maps.newHashMap();

    static {
        users.put("test", "testpwd");
    }

    @RequestMapping("/login")
    public String login(HttpServletRequest req, HttpServletResponse resp, @Valid CasLoginVo casLoginVo, Model model) {
        int errorNum = 0;
        Member member = userFacade.selectPwdByPrincipal(casLoginVo.getPrincipal());
        String pwdDB = member.getPassword();
        if (StringUtils.isEmpty(pwdDB) || !casLoginVo.getPassword().equals(pwdDB)) {
            model.addAttribute("error", "用户名或密码错误错误");
            return "login";
        }
        String tgtVal = TicketRepoUtil.INSTANCE.generateTGT();
        String[] split = req.getHeader(HttpHeaders.REFERER).split(Constants.separator_wenhao);
        if (split.length == 3) {// 避免丢失&后的参数 例 http://passport.shopcmd.com/cas.do?svc=http://qcrfm1001.shopcmd.com/member/goods-comment.html?parcelId=333&ogrId=275&type=1
            casLoginVo.setService(split[1].split(Constants.separator_denghao)[1] + "?" + split[2]);
        }
        final String service = casLoginVo.getService();
        final String ticketId = doCASSignature(casLoginVo.getRememberMe(), service, member.getId(), member.getUsername(), tgtVal, null);

        DemoUtils.deleteCookie(req, resp, Constants.CAS_INPUT_ERROR_COUNT);// 登录成功清除 错误cookie
        DemoUtils.setCookie(req, resp, Constants.CASTGC, tgtVal, 60 * 32, null, Boolean.TRUE);
        return "redirect:" + casLoginVo.getService();
    }

    @RequestMapping("/validateTicketId")
    public void validateTicketId(HttpServletRequest req, HttpServletResponse resp, String svc, String tid) {
        logger.info("认证校验中....");
        CasTicket ticket = null;
        Boolean flag = Boolean.FALSE;
        if (!StringUtils.isEmpty(svc) && !StringUtils.isEmpty(tid)) {
            String domainTicket = DemoUtils.getDomainName(svc) + Constants.separator_duanheng + tid;
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

    private String doCASSignature(Boolean rememberMe, String service, Long memberId, String memberName, String tgtVal, String openID) {
        String ticketId = null;
        if (!StringUtils.isEmpty(service)) {
            ticketId = TicketRepoUtil.INSTANCE.generateTicketId();
            String domainName = DemoUtils.getDomainName(service);
            CasTicket ticket = new CasTicket(ticketId, domainName, memberId, tgtVal, openID, memberName);
            ticket.setRememberMe(rememberMe == null ? Boolean.FALSE : rememberMe);
            ticket.setExpireTime(DemoUtils.getExpireTime(Calendar.MINUTE, AMOUNT));
            TicketRepoUtil.INSTANCE.putTicket(domainName + Constants.separator_duanheng + ticketId, ticket);
        }
        TicketRepoUtil.INSTANCE.passMemberId(tgtVal, memberId);

        return ticketId;
    }

    private static class userFacade {
        private static HashMap<String, Member> users = Maps.newHashMap();

        public static Member selectPwdByPrincipal(String principal) {
            return users.get(principal);
        }
    }

}
