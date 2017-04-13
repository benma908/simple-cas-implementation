package session;

import redis.clients.jedis.Jedis;

/**
 * Created by benma on 2017/4/11.
 */
public interface RedisCallback<T> {
    T call(Jedis var1, Object var2);
}