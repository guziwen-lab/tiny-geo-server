package com.supermap.common.constant;

/**
 * @author gzw
 */
public class RedisScriptConstant {

    /**
     * 获取值对比+对比成功删除原子操作。
     * 如果执行redis.call('del', KEYS[1])成功删除了键KEYS[1]，那么返回的结果会是被成功删除的键的数量。如果键不存在或者删除操作失败，返回的结果会是0。
     */
    public static final String DELETE_SCRIPT = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";

    /**
     * 兼容 GETDEL 功能的 Lua 脚本
     */
    public static final String GET_DEL_SCRIPT = "local val = redis.call('GET', KEYS[1]) if val then redis.call('DEL', KEYS[1]) end return val";

}
