local key = KEYS[1];
local threadId = ARGV[1];

if (redis.call('hexists', key, threadId) == 0) then
    return nil;
end;

local count = redis.call('hincrby', key, threadId, -1);

-- 当 count == 0 时，说明持锁的线程已经执行完所有任务，此时可以释放锁。
if (count == 0) then
    redis.call('del', key);
    return nil;
end;