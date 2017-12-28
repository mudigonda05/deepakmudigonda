package springcache.redis;


import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import redis.clients.jedis.Jedis;

/**
*
* @author Deepak Mudigonda
*/

@RestController
@RequestMapping("/rest/user")
@EnableAspectJAutoProxy(exposeProxy=true)
public class BookResource {

    private final BookRepository bookRepository;
    private Jedis jedisCommand; 
    
    @Value("${expire.reset.time}")
    private int timerReset;
        
    
    public BookResource(BookRepository bookRepository, Jedis jedisCommand) {
        this.bookRepository = bookRepository;
        this.jedisCommand = jedisCommand;
    }
    
   /* @Cacheable annotation caches the data returned from the method to RedisCache(since we have configured SpringCache with RedisCache here)
    If createBook() is invoked with the key which already exists in cache, createBook() won't be invoked but the value of key will be returned from cache*/
   
    @GetMapping("/books/create/{isbn}")
	@Cacheable(value="books", key="#isbn")
	public Book createBook(@PathVariable("isbn") String isbn) {
		System.out.println("Inside Cacheable");
		return bookRepository.getByIsbn(isbn);
	}
    
    //@CachePut annotation invokes the method each time it is invoked and stores/updates the data in the cache
   
    @GetMapping("/books/update/{isbn}")
	@CachePut(value="books", key="#isbn")
	public Book updateBook(@PathVariable("isbn") String isbn) {
		System.out.println("Inside CachePut");
		return bookRepository.getByIsbn(isbn);
	}
    
    @GetMapping("/books/reset/{isbn}")
	public Book testResult(@PathVariable("isbn") String isbn) {
    	System.out.println("Key-Value is "+jedisCommand.get("books:"+isbn)); //Fetches the value from Cache associated with the key
    	jedisCommand.expire("books:"+isbn, timerReset);                     //Resets the default expiration timer of key in Cache 
    	return ((BookResource)AopContext.currentProxy()).retrieveBook(isbn);
	}
    
    @Cacheable(value="books",key="#isbn")
	public Book retrieveBook(String isbn) {
		System.out.println("Retrieving from Backend");
		return bookRepository.getByIsbn(isbn);
	}
    
    //@CacheEvict annotation evicts/deletes the key from the Cache
   
    @GetMapping("/books/delete/{isbn}")
	@CacheEvict(value="books", key="#isbn")
	public void cacheEvictTest(@PathVariable("isbn") String isbn) {
		System.out.println("Inside CacheEvict");
	}
}
