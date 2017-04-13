package util;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import entity.CasTicket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import session.RedisTemplate;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by benma on 2017/4/11.
 */
public enum TicketRepoUtil {
    INSTANCE;
    private static final Logger logger = LoggerFactory.getLogger(TicketRepoUtil.class);
    private static String[] chars = new String[]{"a", "b", "c", "d", "e", "f",
            "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s",
            "t", "u", "v", "w", "x", "y", "z", "0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H", "I",
            "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
            "W", "X", "Y", "Z"};
    private Integer INDEX_TICKET_REPO;// redis_index
    private Integer expire;// 过期时间
    private String casTicketsKey;// casTicket repository redis key
    private String authenticatedMembersKey;// Maps the CASTGC to the authenticated member id.
    private String managedSessionsIdKey;// Maps the ID from the CAS controller to the Session ID.
    @Autowired
    private RedisTemplate redisTemplate;

    TicketRepoUtil() {
        INDEX_TICKET_REPO = Constants.REDIS_SESSION_INDEX;
        casTicketsKey = Constants.CASTICKETS_REDIS_KEY;
        authenticatedMembersKey = Constants.AUTHENTICATED_MEMBERS_REDIS_KEY;
        managedSessionsIdKey = Constants.MANAGED_SESSIONSID_REDIS_KEY;
        redisTemplate.expire(INDEX_TICKET_REPO, casTicketsKey, 60 * 35);
        redisTemplate.expire(INDEX_TICKET_REPO, managedSessionsIdKey, 60 * 35);
    }

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

    public void putTicket(String ticket, String sessionId) {
        redisTemplate.hset(INDEX_TICKET_REPO, managedSessionsIdKey, ticket, sessionId);
    }

    public String getTicketBySessionId(String sessionId) {//O(n)
        Map<String, String> map = redisTemplate.hgetAll(INDEX_TICKET_REPO, managedSessionsIdKey);
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (entry.getValue().equals(sessionId)) {
                return entry.getKey();
            }
        }
        return null;
    }

    public String getSessionIdByTicketId(String ticket) {//O(1)
        return redisTemplate.hget(INDEX_TICKET_REPO, managedSessionsIdKey, ticket);
    }

    public void removeTicket(String ticket) {
        redisTemplate.hdel(INDEX_TICKET_REPO, managedSessionsIdKey, ticket);
    }

    public void removeTicketBySessionId(String sessionId) {//O(n)
        Map<String, String> map = redisTemplate.hgetAll(INDEX_TICKET_REPO, managedSessionsIdKey);
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (entry.getValue().equals(sessionId)) {
                redisTemplate.hdel(INDEX_TICKET_REPO, managedSessionsIdKey, entry.getKey());
            }
        }
    }

    public String generateTGT() {
        return Constants.CAS_TGC_PREFIX + DemoUtils.genUUIDLengthOf(8);
    }

    public String generateTicketId() {
        return Constants.CAS_TICKET_PREFIX + DemoUtils.genUUIDLengthOf(6);
    }

    public boolean containsCasTicket(String domainTicket) {
        return redisTemplate.hexists(INDEX_TICKET_REPO, casTicketsKey, domainTicket);
    }

    public CasTicket getTicket(String domainTicket) {
        synchronized (this) {
            String ticket = redisTemplate.hget(INDEX_TICKET_REPO, casTicketsKey, domainTicket);
            CasTicket casTicket = JSON.parseObject(ticket, CasTicket.class);
            String id = casTicket.getId();
            if (!StringUtils.isEmpty(id)) {// 针对一个站点做一次性的 st;取出即失效;有效期3分钟;
                casTicket.setId("");
                putTicket(domainTicket, casTicket);
                casTicket.setId(id);
            }
            return casTicket;
        }
    }

    public void putTicket(String domainTicket, CasTicket ticket) {
        redisTemplate.hset(INDEX_TICKET_REPO, casTicketsKey, domainTicket, JSON.toJSONString(ticket));
    }

    public boolean containsMember(String tgtVal) {
        return redisTemplate.hexists(INDEX_TICKET_REPO, authenticatedMembersKey, tgtVal);
    }

    public void passMemberId(String tgtVal, Long memberId) {
        logger.info("签发认证用户:{} 会员Id:{}", tgtVal, memberId);
        redisTemplate.hset(INDEX_TICKET_REPO, authenticatedMembersKey, tgtVal, memberId.toString());
    }

    public Long getMemberId(String tgtVal) {
        String memberId = redisTemplate.hget(INDEX_TICKET_REPO, authenticatedMembersKey, tgtVal);
        logger.info("获取认证用户:{} 会员Id:{}", tgtVal, memberId);
        return Long.parseLong(memberId);
    }

    public ConcurrentMap<String, CasTicket> getRepo() {
        ConcurrentMap<String, CasTicket> result = Maps.newConcurrentMap();
        Map<String, String> map = redisTemplate.hgetAll(INDEX_TICKET_REPO, casTicketsKey);
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String value = entry.getValue();
            if (!StringUtils.isEmpty(value)) {
                result.put(entry.getKey(), JSON.parseObject(value, CasTicket.class));
            }
        }
        return result;
    }

    public void kickOutMember(String tgtVal) {
        logger.info("从 {} 认证中心删除用户：[{}] ", authenticatedMembersKey, tgtVal);
        redisTemplate.hdel(INDEX_TICKET_REPO, authenticatedMembersKey, tgtVal);
    }

    public void expireTicket(String domainTicket) {
        logger.info("从 {} 仓库中删除认证：[{}] ", casTicketsKey, domainTicket);
        redisTemplate.hdel(INDEX_TICKET_REPO, casTicketsKey, domainTicket);
    }

}
