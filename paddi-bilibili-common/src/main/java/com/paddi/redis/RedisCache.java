package com.paddi.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * spring redis 工具类
 * 
 * @author ruoyi
 **/
@SuppressWarnings(value = { "unchecked", "rawtypes" })
@Component
public class RedisCache
{
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 缓存基本的对象，Integer、String、实体类等
     *
     * @param key 缓存的键值
     * @param value 缓存的值
     */
    public <T> void setCacheObject(final String key, final T value)
    {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 缓存基本的对象，Integer、String、实体类等
     *
     * @param key 缓存的键值
     * @param value 缓存的值
     * @param timeout 时间
     * @param timeUnit 时间颗粒度
     */
    public <T> void setCacheObject(final String key, final T value, final Integer timeout, final TimeUnit timeUnit)
    {
        redisTemplate.opsForValue().set(key, value, timeout, timeUnit);
    }

    /**
     * 设置有效时间
     *
     * @param key Redis键
     * @param timeout 超时时间
     * @return true=设置成功；false=设置失败
     */
    public boolean expire(final String key, final long timeout)
    {
        return expire(key, timeout, TimeUnit.SECONDS);
    }

    /**
     * 设置有效时间
     *
     * @param key Redis键
     * @param timeout 超时时间
     * @param unit 时间单位
     * @return true=设置成功；false=设置失败
     */
    public boolean expire(final String key, final long timeout, final TimeUnit unit)
    {
        return redisTemplate.expire(key, timeout, unit);
    }

    /**
     * 获得缓存的基本对象。
     *
     * @param key 缓存键值
     * @return 缓存键值对应的数据
     */
    public <T> T getCacheObject(final String key)
    {
        ValueOperations<String, T> operation = redisTemplate.opsForValue();
        return operation.get(key);
    }

    /**
     * 删除单个对象
     *
     * @param key
     */
    public boolean deleteObject(final String key)
    {
        return redisTemplate.delete(key);
    }

    /**
     * 删除集合对象
     *
     * @param collection 多个对象
     * @return
     */
    public long deleteObject(final Collection collection)
    {
        return redisTemplate.delete(collection);
    }

    /**
     * 缓存List数据
     *
     * @param key 缓存的键值
     * @param values 待缓存的List数据
     * @return 缓存的对象
     */
    public <T> long setCacheList(final String key, final List<T> dataList)
    {
        Long count = redisTemplate.opsForList().rightPushAll(key, dataList);
        return count == null ? 0 : count;
    }

    /**
     * 获得缓存的list对象
     *
     * @param key 缓存的键值
     * @return 缓存键值对应的数据
     */
    public <T> List<T> getCacheList(final String key)
    {
        return redisTemplate.opsForList().range(key, 0, -1);
    }

    /**
     * 缓存Set
     *
     * @param key 缓存键值
     * @param dataSet 缓存的数据
     * @return 缓存数据的对象
     */
    public <T> BoundSetOperations<String, T> setCacheSet(final String key, final Set<T> dataSet)
    {
        BoundSetOperations<String, T> setOperation = redisTemplate.boundSetOps(key);
        Iterator<T> it = dataSet.iterator();
        while (it.hasNext())
        {
            setOperation.add(it.next());
        }
        return setOperation;
    }

    public <T> boolean addValueToSet(final String key, T item) {
        return redisTemplate.opsForSet().add(key, item) == 1;
    }

    /**
     * 获得缓存的set
     *
     * @param key
     * @return
     */
    public <T> Set<T> getCacheSet(final String key)
    {
        return redisTemplate.opsForSet().members(key);
    }

    /**
     * 缓存Map
     *
     * @param key
     * @param dataMap
     */
    public <T> void setCacheMap(final String key, final Map<String, T> dataMap)
    {
        if (dataMap != null) {
            redisTemplate.opsForHash().putAll(key, dataMap);
        }
    }

    /**
     * 获得缓存的Map
     *
     * @param key
     * @return
     */
    public <T> Map<String, T> getCacheMap(final String key)
    {
        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * 往Hash中存入数据
     *
     * @param key Redis键
     * @param hKey Hash键
     * @param value 值
     */
    public <T> void setCacheMapValue(final String key, final String hKey, final T value)
    {
        redisTemplate.opsForHash().put(key, hKey, value);
    }

    /**
     * 获取Hash中的数据
     *
     * @param key Redis键
     * @param hKey Hash键
     * @return Hash中的对象
     */
    public <T> T getCacheMapValue(final String key, final String hKey)
    {
        HashOperations<String, String, T> opsForHash = redisTemplate.opsForHash();
        return opsForHash.get(key, hKey);
    }

    /**
     * 获取多个Hash中的数据
     *
     * @param key Redis键
     * @param hKeys Hash键集合
     * @return Hash对象集合
     */
    public <T> List<T> getMultiCacheMapValue(final String key, final Collection<Object> hKeys)
    {
        return redisTemplate.opsForHash().multiGet(key, hKeys);
    }

    /**
     * 获得缓存的基本对象列表
     * 
     * @param pattern 字符串前缀
     * @return 对象列表
     */
    public Collection<String> keys(final String pattern)
    {
        return redisTemplate.keys(pattern);
    }

    public Boolean hasKey(final String key) {
        return redisTemplate.hasKey(key);
    }


    public void increment(final String key) {
        redisTemplate.opsForValue().increment(key);
    }

    public void decrement(final String key) {
        redisTemplate.opsForValue().decrement(key);
    }

    public void increment(final String key,  Long delta) {
        redisTemplate.opsForValue().increment(key, delta);
    }

    public <T> Boolean isMember(final String key, T value) {
        return redisTemplate.opsForSet().isMember(key, value);
    }

    public <T> void removeValueFromSet(final String key, T value) {
        redisTemplate.opsForSet().remove(key, value);
    }

    public Long getSetSize(final String key) {
        return redisTemplate.opsForSet().size(key);
    }

    public Long getZSetSize(final String key) {
        return redisTemplate.opsForZSet().size(key);
    }

    public <T> void addValuesToSet(String key, Collection<T> values) {
        redisTemplate.opsForSet().add(key, values);
    }

    public <V> void addValueToZSet(String key, V value, double score) {
        redisTemplate.opsForZSet().add(key, value, score);
    }

    public <E> Set<E> getCacheZSetValues(String key) {
        return redisTemplate.opsForZSet().reverseRange(key, 0, -1);
    }

    public <E> Set<E> getCacheZSetValuesGradually(String key) {
        Set<E> result = new HashSet<>();
        ScanOptions options = ScanOptions.scanOptions().match("*").count(100).build();
        Cursor<ZSetOperations.TypedTuple<E>> cursor = redisTemplate.opsForZSet().scan(key, options);
        while(cursor.hasNext()) {
            ZSetOperations.TypedTuple<E> typedTuple = cursor.next();
            result.add(typedTuple.getValue());
        }
        return result;
    }

    public <E> Set<E> getCacheZSetValues(String key, int startIndex, int endIndex) {
        return redisTemplate.opsForZSet().reverseRange(key, startIndex, endIndex);
    }

    public <T> T execute(RedisScript<T> script, List<String> keys, Object... args) {
        return (T) redisTemplate.execute(script, keys, args);
    }

    public <F, V> void addValueToHash(String key, F field, V value) {
        redisTemplate.opsForHash().put(key, field, value);
    }
}
