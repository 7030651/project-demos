-- key1: redis key, key2: hash element.
if (redis.call('hexists', KEYS[1], KEYS[2])) then
    local stock = tonumber(redis.call('hget', KEYS[1], KEYS[2]));
    if (stock > 0) then
        -- result is a string.
        redis.call('hincrby', KEYS[1], KEYS[2], -1);
        return stock;
    end;
    return 0;
end;