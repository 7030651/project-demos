-- 锁的 KEY
local key = KEYS[1];
-- 当前获得锁的线程的 ID.
local threadId = ARGV[1];
-- 锁的过期时间。
local releaseTime = ARGV[2];

-- 当锁不存在时，进行加锁。
if(redis.call('exists', key) == 0) then
    redis.call('hset', key, threadId, '1');
    redis.call('expire', key, releaseTime);
    return 1;
end;

-- 当锁存在时，累加一次锁的调用次数。
if(redis.call('hexists', key, threadId) == 1) then
    redis.call('hincrby', key, threadId, '1');
    redis.call('expire', key, releaseTime);
    return 1;
end;
-- 非持锁的线程，直接返回。
return 0;