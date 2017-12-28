/**
 * 
 */
package springcache.redis;

/**
*
* @author Deepak Mudigonda
*/

public interface BookRepository {

	Book getByIsbn(String isbn);
	
}
