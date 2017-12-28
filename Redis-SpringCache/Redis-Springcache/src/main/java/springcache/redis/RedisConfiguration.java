package springcache.redis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.Jedis;

/**
*
* @author Deepak Mudigonda
*/

@Configuration
@EnableCaching
public class RedisConfiguration {

	@Value("${redis.ip.address}")
	private String redisIpAddress;
	
	@Value("#{new Integer('${redis.port.number}')}")
	private Integer redisPortNumber;
	
	
	@Bean
	JedisConnectionFactory jedisConnectionFactory() {
		JedisConnectionFactory jedisConFactory = new JedisConnectionFactory();
		jedisConFactory.setHostName(redisIpAddress);
		jedisConFactory.setPort(redisPortNumber);
		return jedisConFactory;
	}

	@Bean
	RedisTemplate<String, Object> redisTemplateBook() {
		RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
		redisTemplate.setConnectionFactory(jedisConnectionFactory());
		redisTemplate.setKeySerializer(stringRedisSerializer());
		redisTemplate.setHashKeySerializer(stringRedisSerializer());
		redisTemplate.setValueSerializer(jackson2JsonRedisSerializer());
		return redisTemplate;
	}
	
	@Bean
	public CacheManager cacheManager(RedisTemplate<String, Object> redisTemplate) {

		RedisCacheManager cacheManager = new RedisCacheManager(redisTemplate);
		cacheManager.setUsePrefix(true);
		cacheManager.setDefaultExpiration(100L); 
		return cacheManager;

	}
	
	@Bean
	public StringRedisSerializer stringRedisSerializer(){
		return new StringRedisSerializer();
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Bean
	public Jackson2JsonRedisSerializer<? extends Book> jackson2JsonRedisSerializer(){
		Class<? extends Book> book = new Book("", "").getClass();
		return new Jackson2JsonRedisSerializer(book);
	}
	
	@Bean
	public Jedis jedisConnection(){
		return new Jedis(redisIpAddress);
	}
	
	
}
