local ratelimit_info = redis.pcall('HMGET',KEYS[1],'last_time','current_token')  --根据限流key获取限流信息
local last_time = ratelimit_info[1]   --上次时间
local current_token = tonumber(ratelimit_info[2])  --当前剩余token数量
local max_token = tonumber(ARGV[1])  -- 入参 最大token限制数
local token_rate = tonumber(ARGV[2]) -- 入参 token生成速度
local current_time = tonumber(ARGV[3]) --入参 当前时间
local reverse_time = 1000/token_rate  --中间值
if current_token == nil then
    current_token = max_token
    last_time = current_time
else
    local past_time = current_time-last_time   --距上次经过多长时间
    local reverse_token = math.floor(past_time/reverse_time) --经过时间 * token生成速度 = 新增token数量
    current_token = current_token+reverse_token --现在总共有多少token
    last_time = reverse_time*reverse_token+last_time --更新上次时间
    if current_token>max_token then
        current_token = max_token --最大token限额
    end
end
local result = 0
if(current_token>0) then
    result = 1
    current_token = current_token-1  --去掉用掉一个token
end
redis.call('HMSET',KEYS[1],'last_time',last_time,'current_token',current_token)  --更新
redis.call('pexpire',KEYS[1],math.ceil(reverse_time*(max_token-current_token)+(current_time-last_time))) --更新
return result
