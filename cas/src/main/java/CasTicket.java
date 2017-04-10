import java.io.Serializable;
import java.util.Date;

/**
 * Created by benma on 2017/4/10.
 */
public class CasTicket implements Serializable {
    private String id;
    private String domainName;
    private Long memberId;
    private String memberName;
    private String qqOpenid;
    private String tgtId;
    private Boolean result = Boolean.FALSE;
    private Boolean rememberMe = Boolean.FALSE;
    private Date expireTime;
    private String errorMsg;

    public Date getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Date expireTime) {
        this.expireTime = expireTime;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public String getQqOpenid() {
        return qqOpenid;
    }

    public void setQqOpenid(String qqOpenid) {
        this.qqOpenid = qqOpenid;
    }

    public Boolean getRememberMe() {
        return rememberMe;
    }

    public void setRememberMe(Boolean rememberMe) {
        this.rememberMe = rememberMe;
    }

    public Boolean getResult() {
        return result;
    }

    public void setResult(Boolean result) {
        this.result = result;
    }

    public String getTgtId() {
        return tgtId;
    }

    public void setTgtId(String tgtId) {
        this.tgtId = tgtId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDomainName() {
        return domainName;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public CasTicket() {
    }

    private CasTicket(String id, String domainName, Long memberId, String tgtId) {
        this.id = id;
        this.domainName = domainName;
        this.memberId = memberId;
        this.tgtId = tgtId;
    }

    public CasTicket(String id, String domainName, Long memberId, String tgtId, String qqOpenid, String memberName) {
        this(id, domainName, memberId, tgtId, qqOpenid);
        this.memberName = memberName;
    }

    public CasTicket(String id, String domainName, Long memberId, String tgtId, String qqOpenid) {
        this(id, domainName, memberId, tgtId);
        this.qqOpenid = qqOpenid;
    }

    @Override
    public String toString() {
        return "CasTicket{" +
                "id='" + id + '\'' +
                ", domainName='" + domainName + '\'' +
                ", memberId=" + memberId +
                ", tgtId='" + tgtId + '\'' +
                ", result=" + result +
                ", rememberMe=" + rememberMe +
                '}';
    }
}
