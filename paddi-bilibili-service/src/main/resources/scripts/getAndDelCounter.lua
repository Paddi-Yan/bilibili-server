local key = KEYS[1]
local value = redis.call('GET', key)
if value then
    redis.call('DEL', key)
    return tonumber(value)
else
    return nil
end