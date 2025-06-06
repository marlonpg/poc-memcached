# Memcached: High-Performance Caching Simplified

## Table of Contents
1. [What is Memcached?](#what-is-memcached)
2. [Why Use Memcached?](#why-use-memcached)
3. [Pros and Cons](#memcached-pros-and-cons)
4. [Installation & Startup](#installation--startup)
5. [Command Line Commands](#cool-command-line-commands)
6. [Java Client Example](#java-client-examples)
7. [Key Takeaways](#key-takeaways)

---

## What is Memcached?

### Definition
- In-memory key-value store for caching (not persistent storage)
- Designed for speed: Sub-millisecond response times

### Origin
Created by Brad Fitzpatrick (2003) for LiveJournal to reduce database load

### Key Concepts
- **Key-Value Model**:
  - Keys (strings ≤250B), Values (serialized objects, ≤1MB default)
- **Volatile**: Data lives only in RAM; lost on restart
- **Distributed**: Clients hash keys to shard data across independent servers

### How It Works
1. App requests data → Client checks Memcached
2. **Cache Hit**: Data returned from RAM
3. **Cache Miss**: Data fetched from DB → Stored in Memcached for next time



---

## Why Use Memcached?

### Use Cases
- Cache database queries, sessions, API responses, or web fragments
- Reduce latency and database load (e.g., for high-traffic apps like Netflix)

### Performance Gains
Example: 
- DB query: 50ms 
- Memcached fetch: 0.5ms (100× faster)

### Scalability
Add more servers to pool ("horizontal scaling")

---

## Memcached Pros and Cons

| Pros                          | Cons                          |
|-------------------------------|-------------------------------|
| Blazing fast (RAM-based)      | No persistence (data loss on crash) |
| Reduces DB load               | No replication/failover       |
| Simple API                    | Limited security (no encryption) |
| Multithreaded (uses multiple CPUs) | Manual sharding required |

### vs. Redis
- Redis offers persistence, replication, and richer data types
- Memcached is simpler and often faster for basic caching

---

## Installation & Startup

### Linux (Debian/Ubuntu)
```bash
sudo apt-get install memcached libevent-dev
sudo systemctl start memcached
``` 

## macOS (Homebrew)
brew install memcached
brew services start memcached

## Configuration
Edit /etc/memcached.conf to adjust:
- RAM size (-m 512 = 512MB)
- Ports
- Connection limits

## Cool Command Line Commands

### Connect via Telnet
```
telnet localhost 11211
```

### Basic Commands
```
set user:100 0 3600 10
helloworld

get user:100

stats

flush_all
quit
```


## Java Client Examples
### Using spymemcached
´´´ java
import net.spy.memcached.MemcachedClient;
import java.net.InetSocketAddress;

public class MemcachedDemo {
  public static void main(String[] args) throws Exception {
    // Connect to server
    MemcachedClient client = new MemcachedClient(
      new InetSocketAddress("localhost", 11211));
    
    // Store data (expires in 1 hour)
    client.set("user:100", 3600, "{name:'John',role:'admin'}");
    
    // Retrieve
    System.out.println(client.get("user:100"));
    
    client.shutdown();
  }
}

//for sharding
new ConnectionFactoryBuilder()
  .setLocatorType(Locator.CONSISTENT) // Ketama hashing
  .build();
´´´

## References
Official Documentation
Memcached vs Redis Comparison