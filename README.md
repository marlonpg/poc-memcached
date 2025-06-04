# poc-memcached
[Documentation](https://docs.memcached.org/)

Memcached is a widely-used, open-source, high-performance, distributed memory object caching system. It is primarily designed to speed up dynamic web applications by alleviating database load. Essentially, it acts as a short-term memory for your applications, storing frequently accessed pieces of data in RAM, which is significantly faster to access than disk-based storage.

Think of it like this: if your application frequently needs to fetch the same piece of information from a database (e.g., a user's profile, a product catalog), doing so repeatedly can be slow and resource-intensive. Memcached allows you to store a copy of this information in memory. When the application needs it again, it first checks memcached. If the data is there (a "cache hit"), it's retrieved quickly. If not (a "cache miss"), the application fetches it from the database and then stores it in memcached for future requests.

---

### Key Concepts and Architecture

#### In-Memory Key-Value Store:
- **Data Storage**: Memcached stores data as key-value pairs. The "key" is a unique identifier (a string up to 250 bytes), and the "value" is the actual data (can be strings or objects, typically serialized, up to 1MB by default, though this is configurable).
- **RAM-Based**: All data is stored directly in the server's Random Access Memory (RAM), making it incredibly fast.
- **Volatility**: Data stored in memcached is volatile. If a server restarts or crashes, all data is lost. Memcached should be used as a cache, not a persistent data store.

#### Distributed System:
- **Client-Server Model**: Applications interact with memcached through client libraries that implement the memcached protocol.
- **Independent Servers**: Memcached servers operate independently, with no communication or data sharing between them.
- **Client-Side Hashing/Distribution**: The client library determines which server holds a key using a hashing algorithm, often consistent hashing.

#### Memory Management:
- **Slab Allocator**: Memory is pre-allocated into fixed-size chunks called "slabs" for different item sizes, reducing fragmentation.
- **LRU Eviction**: When memory is full, older or less frequently accessed data is evicted using the Least Recently Used (LRU) policy.

#### Multithreaded Architecture:
- Modern memcached versions are multithreaded, allowing a single server process to utilize multiple CPU cores for high throughput and low latency.

---

### How Memcached Works (Simplified Flow)
1. **Application Request**: An application needs a piece of data.
2. **Check Cache**: The memcached client library checks if the data exists in the cache:
   - **Cache Hit**: Data is retrieved from RAM and returned to the application.
   - **Cache Miss**: Data is fetched from the primary data store, stored in memcached, and returned to the application.

---

### Advantages of Memcached
- **Speed and Performance**: Sub-millisecond response times due to its in-memory nature.
- **Reduced Database Load**: Fewer read queries to the database, freeing up resources.
- **Scalability**:
  - **Horizontal Scaling**: Add more servers to the pool.
  - **Vertical Scaling**: Utilize multi-core processors with its multithreaded architecture.
- **Simplicity**: Straightforward key-value model and API.
- **Wide Language Support**: Client libraries available for most popular programming languages.
- **Open Source**: Freely available with an active community.

---

### Disadvantages of Memcached
- **Volatility**: Data is lost if a server crashes or restarts.
- **Limited Data Types**: Values are treated as opaque blobs; no support for complex data structures.
- **Basic Security Model**: Minimal built-in security; relies on network configurations and external solutions for encryption.
- **No Native Clustering or Replication**: Independent servers with no built-in replication or failover.
- **Memory Size Limitation**: Cache size is limited by available RAM, with a default item size limit of 1MB.

---

### Common Use Cases
- **Database Query Caching**: Store results of expensive or frequent database queries.
- **Session Management**: Store user session data in distributed environments.
- **Web Page Caching**: Cache frequently requested web pages or fragments.
- **API Response Caching**: Cache API responses to reduce latency and load.
- **Object Caching**: Cache application objects like user profiles or product information.
- **Rate Limiting**: Track request counts for rate-limiting purposes.


### Installation

## Linux (Debian/Ubuntu)
``` bash
sudo apt-get update
sudo apt-get install memcached
sudo apt-get install libevent-dev # Dependency, usually installed with memcached
```

## Linux (Red Hat/Fedora/CentOS)
``` bash
sudo yum install memcached
sudo yum install libevent-devel # Dependency
```

## macOS (using Homebrew)
``` bash
brew install memcached
brew install libevent # Dependency, often handled by the memcached formula
```

## How to start it
``` bash
sudo systemctl start memcached  # For systemd-based Linux
sudo service memcached start   # For older init systems
brew services start memcached # For macOS with Homebrew services

```

## Configuration

Memcached is typically configured via command-line arguments when the service starts or through a configuration file (e.g., /etc/memcached.conf on Debian/Ubuntu, or /etc/sysconfig/memcached on Red Hat based systems).


## Basic commands
TODO


## Client Library Examples

### Java (using Spymemcached):

``` java
import net.spy.memcached.MemcachedClient;
import net.spy.memcached.AddrUtil;
import net.spy.memcached.ConnectionFactoryBuilder;
import net.spy.memcached.auth.AuthDescriptor;
import net.spy.memcached.auth.PlainCallbackHandler;

import java.net.InetSocketAddress;
import java.util.concurrent.Future;

public class MemcachedJavaExample {
    public static void main(String[] args) {
        MemcachedClient mcc = null;
        try {
            // Connect to a single server
            mcc = new MemcachedClient(new InetSocketAddress("localhost", 11211));
            System.out.println("Connection to server successful.");

            // Set a value (expiration time in seconds)
            Future<Boolean> setResult = mcc.set("my_java_key", 3600, "Hello from Java!");
            if (setResult.get()) {
                System.out.println("Set 'my_java_key'");
            } else {
                System.out.println("Failed to set 'my_java_key'");
            }

            // Get a value
            Object value = mcc.get("my_java_key");
            if (value != null) {
                System.out.println("Got 'my_java_key': " + value.toString());
            } else {
                System.out.println("'my_java_key' not found.");
            }

            // Delete a value
            Future<Boolean> deleteResult = mcc.delete("my_java_key");
            if (deleteResult.get()) {
                System.out.println("Deleted 'my_java_key'");
            } else {
                System.out.println("Failed to delete 'my_java_key' (or key didn't exist).");
            }

            // Verify deletion
            if (mcc.get("my_java_key") == null) {
                 System.out.println("'my_java_key' is successfully deleted.");
            }

        } catch (Exception e) {
            System.err.println("Memcached error: " + e.getMessage());
        } finally {
            if (mcc != null) {
                mcc.shutdown();
                System.out.println("Connection closed.");
            }
        }
    }
}

```

### References
- For comparison with Redis
    https://dev.to/scalegrid/redis-vs-memcached-2021-comparison-5hep
    https://dzone.com/articles/performance-and-scalability-analysis-of-redis-memcached
