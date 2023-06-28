-- 设置计数器key和阈值
local key = KEYS[1]
local threshold = tonumber(ARGV[1])

-- 递增计数器
local counter = redis.call('incr', key)

-- 如果计数器值达到阈值返回1并删除key
if counter >= threshold then
    redis.call('del', key)
    return counter
else
    -- 如果计数器是新创建的，返回-1
    if counter == 1 then
        return -1
    -- 否则返回0
    else
        return 0
    end
end