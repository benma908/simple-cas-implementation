package session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.util.Map;

/**
 * Created by benma on 2017/4/11.
 */
@Component
public class RedisTemplate {
    private static Logger logger = LoggerFactory.getLogger("session.RedisTemplate");

    private <T> T execute(RedisCallback<T> callback, Object... args) {
        Jedis jedis = null;
        try {
            Object index = args[0];
            if (null != index && Integer.parseInt(index.toString()) > 0 && Integer.parseInt(index.toString()) < 16) {
                jedis = RedisManager.getRedis(Integer.parseInt(index.toString()));
            } else {
                jedis = RedisManager.getRedis();
            }
            return callback.call(jedis, args);
        } catch (JedisConnectionException e) {
            if (jedis != null)
                RedisManager.returnBrokeRedis(jedis);
            jedis = RedisManager.getRedis();
            logger.error(e.getMessage(), e);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (jedis != null) {
                RedisManager.returnRedis(jedis);
            }
        }
        return null;
    }

    public Boolean hexists(int index, String mapKey, String attributeKey) {
        return execute(new RedisCallback<Boolean>() {
            public Boolean call(Jedis jedis, Object params) {
                String key = ((Object[]) params)[1].toString();
                String field = ((Object[]) params)[2].toString();
                return jedis.hexists(key, field);
            }
        }, index, mapKey, attributeKey);
    }

    public String hget(int index, String key, String field) {
        return execute(new RedisCallback<String>() {
            public String call(Jedis jedis, Object parms) {
                String key = ((Object[]) parms)[1].toString();
                String field = ((Object[]) parms)[2].toString();
                return jedis.hget(key, field);
            }
        }, index, key, field);
    }

    public Map<String, String> hgetAll(int index, String key) {
        return execute(new RedisCallback<Map<String, String>>() {
            public Map<String, String> call(Jedis jedis, Object params) {
                String key = ((Object[]) params)[1].toString();
                return jedis.hgetAll(key);
            }
        }, index, key);
    }

    public Long hdel(int index, String mapKey, String attributeKey) {
        return execute(new RedisCallback<Long>() {
            public Long call(Jedis jedis, Object params) {
                String key = ((Object[]) params)[1].toString();
                String field = ((Object[]) params)[2].toString();
                return jedis.hdel(key, field);
            }
        }, index, mapKey, attributeKey);
    }

    public void hset(int index, String key, String field, String value) {
        execute(new RedisCallback<String>() {
            public String call(Jedis jedis, Object parms) {
                String key = ((Object[]) parms)[1].toString();
                String field = ((Object[]) parms)[2].toString();
                String value = ((Object[]) parms)[3].toString();
                jedis.hset(key, field, value);
                return null;
            }
        }, index, key, field, value);
    }

    public Long expire(int index, String key, int seconds) {
        return execute(new RedisCallback<Long>() {
            public Long call(Jedis jedis, Object parms) {
                String key = ((Object[]) parms)[1].toString();
                String seconds = ((Object[]) parms)[2].toString();
                return jedis.expire(key, Integer.parseInt(seconds));
            }
        }, index, key, seconds);
    }

    public Boolean exists(int index, String key) {
        return execute(new RedisCallback<Boolean>() {
            @Override
            public Boolean call(Jedis jedis, Object params) {
                String key = ((Object[]) params)[1].toString();
                return jedis.exists(key);
            }
        }, index, key);
    }

    public String get(int index, String key) {
        return execute(new RedisCallback<String>() {
            public String call(Jedis jedis, Object parms) {
                String key = ((Object[]) parms)[1].toString();
                return jedis.get(key);
            }
        }, index, key);
    }

    public void set(int index, String key, String value, int seconds) {
        execute(new RedisCallback<String>() {
            public String call(Jedis jedis, Object parms) {
                String key = ((Object[]) parms)[1].toString();
                String value = ((Object[]) parms)[2].toString();
                String seconds = ((Object[]) parms)[3].toString();
                jedis.setex(key, Integer.parseInt(seconds), value);
                return null;
            }
        }, index, key, value, seconds);
    }

    public void set(int index, String key, String value) {
        execute(new RedisCallback<String>() {
            public String call(Jedis jedis, Object parms) {
                String key = ((Object[]) parms)[1].toString();
                String value = ((Object[]) parms)[2].toString();
                jedis.set(key, value);
                return null;
            }
        }, index, key, value);
    }

    public Long del(int index, String key) {
        return execute(new RedisCallback<Long>() {
            public Long call(Jedis jedis, Object parms) {
                String key = ((Object[]) parms)[1].toString();
                return jedis.del(key);
            }
        }, index, key);
    }
}