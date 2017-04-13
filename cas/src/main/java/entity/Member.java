package entity;

import java.io.Serializable;

/**
 * Created by benma on 2017/4/11.
 */
public class Member implements Serializable {
    private static final long serialVersionUID = -3326367801971536705L;
    private Long id;
    private String username;
    private String password;
    private String openId;

    public Member() {
    }

    @Override
    public String toString() {
        return "entity.Member{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", openId='" + openId + '\'' +
                '}';
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }
}
