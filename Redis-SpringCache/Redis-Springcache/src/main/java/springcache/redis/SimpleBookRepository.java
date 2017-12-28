package springcache.redis;

import org.springframework.stereotype.Repository;

/**
*
* @author Deepak Mudigonda
*/

@Repository
public class SimpleBookRepository implements BookRepository {

	@Override
	public Book getByIsbn(String isbn) {
		simulateSlowService();
		return new Book(isbn, "Some Book");
	}
	
	private void simulateSlowService(){
		try{
			Thread.sleep(3000L);
		}
		catch(InterruptedException e){
			throw new IllegalStateException();
		}
	}
}
