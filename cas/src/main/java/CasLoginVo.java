import org.hibernate.validator.constraints.NotEmpty;

import java.io.Serializable;

/**
 * Created by benma on 2017/4/10.
 */

public class CasLoginVo implements Serializable {
    @NotEmpty(message = " 登录凭证不能为空")
    private String principal;
    @NotEmpty(message = " 密码不能为空")
    private String password;
    private Boolean rememberMe;
    private String service;
    private String captchaCode;

    public CasLoginVo() {

    }

    @Override
    public String toString() {
        return "CasLoginVo{" +
                "principal='" + principal + '\'' +
                ", password='" + password + '\'' +
                ", rememberMe=" + rememberMe +
                ", service='" + service + '\'' +
                ", captchaCode='" + captchaCode + '\'' +
                '}';
    }

    public String getCaptchaCode() {
        return captchaCode;
    }

    public void setCaptchaCode(String captchaCode) {
        this.captchaCode = captchaCode;
    }

    public String getPrincipal() {
        return principal;
    }

    public void setPrincipal(String principal) {
        this.principal = principal;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getRememberMe() {
        return rememberMe;
    }

    public void setRememberMe(Boolean rememberMe) {
        this.rememberMe = rememberMe;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }
}

