package session;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.Properties;

/**
 * Created by benma on 2017/4/11.
 */
public class RedisManager {
    private static Logger logger = LoggerFactory.getLogger("session.RedisManager");
    private static JedisPool pool = null;
    private static String IP_ADDRESS = null;

    static {
        try {
            System.out.println("==============>>>> session.RedisManager redis pool init start");
            Properties props = new Properties();
            props.load(RedisManager.class.getClassLoader().getResourceAsStream("public_system.properties"));
            IP_ADDRESS = props.getProperty("redis.ip");
            JedisPoolConfig config = new JedisPoolConfig();
            config.setTestWhileIdle(false);
            config.setMaxTotal(Integer.valueOf(props.getProperty("redis.pool.maxTotal")));
            config.setMaxIdle(Integer.valueOf(props.getProperty("redis.pool.maxIdle")));
            config.setMaxWaitMillis(Long.valueOf(props.getProperty("redis.pool.maxWaitMillis")));
            config.setTestOnBorrow(Boolean.valueOf(props.getProperty("redis.pool.testOnBorrow")));
            config.setTestOnReturn(Boolean.valueOf(props.getProperty("redis.pool.testOnReturn")));
            String password = props.getProperty("redis.password");
            logger.info("======>>redis config : ip:" + IP_ADDRESS + ",password:" + password);
            if (StringUtils.isBlank(password)) {
                pool = new JedisPool(config, IP_ADDRESS, Integer.valueOf(props.getProperty("redis.port")), Integer.valueOf(props.getProperty("redis.timeout")));
            } else {
                pool = new JedisPool(config, IP_ADDRESS, Integer.valueOf(props.getProperty("redis.port")), Integer.valueOf(props.getProperty("redis.timeout")), password);
            }

            System.out.println("================>>>> session.RedisManager  redis pool init end================= ");
        } catch (Exception var3) {
            logger.error(var3.getMessage(), var3);
            throw new Error("IP:" + IP_ADDRESS + ",设置redis服务器出错", var3);
        }
    }

    public RedisManager() {
    }

    static Jedis getRedis() {
        return pool != null ? pool.getResource() : null;
    }

    public static Jedis getRedis(int index) {
        if (pool != null) {
            Jedis jedis = pool.getResource();
            jedis.select(index);
            return jedis;
        } else {
            return null;
        }
    }

    public static void returnRedis(Jedis jedis) {
        if (jedis != null) {
            pool.returnResource(jedis);
        }

    }

    public static void returnBrokeRedis(Jedis jedis) {
        if (jedis != null) {
            pool.returnBrokenResource(jedis);
        }

    }
}
