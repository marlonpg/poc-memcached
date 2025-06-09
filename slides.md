# Memcached

## Table of Contents
1. [What is Memcached?](#what-is-memcached)
2. [Why Use Memcached?](#why-use-memcached)
3. [Who Uses?](#who-uses)
4. [Performance comparison?](#performance-comparison)
5. [Installation & Startup](#installation--startup)
6. [Command Line Commands](#cool-command-line-commands)
7. [Java Client Example](#java-client-examples)
8. [References](#references)

---

## What is Memcached?

- In-memory key-value store for caching (not persistent storage)
- Created by Brad Fitzpatrick (2003) for LiveJournal to reduce database load
- Simple architecture: easy to deploy, configure, and use—no complex setup or management required.
- Scaling up is simple-just add more servers to expand capacity
- Memcached servers are unaware of each other. There is no crosstalk, no synchronization, no broadcasting, no replication. Adding servers increases the available memory. 
- Logic Half in Client, Half in Server, clients understand how to choose which server to read or write

---

## Why Use Memcached?

Memcached is widely used to improve the performance and scalability of web applications by reducing the load on databases and speeding up data retrieval. Here’s why you might choose Memcached:

- **Ultra-fast caching:** Stores frequently accessed data in memory, allowing for sub-millisecond retrieval times.
- **Reduces database load:** By caching results of expensive database queries, it minimizes repeated access to slower backend storage.
- **Scalable and simple:** Easily scale horizontally by adding more servers; no complex configuration or coordination required.
- **Stateless and lightweight:** Each server operates independently, making deployment, maintenance, and scaling straightforward.
- **Broad language support:** Mature client libraries are available for most popular programming languages.
- **Ideal for caching API responses and rendered web pages:** Commonly used to cache API responses and web fragments for high-traffic sites.

> **Note:** Memcached is generally **not recommended for session storage** because data is not persistent and may be lost if a server is restarted or fails.

---

## Who Uses?

Widely adopted for caching large-scale systems:

- **Facebook**  
  Used Memcached since ~2005 to cache the social graph and sessions.

- **Twitter**  
  Uses Memcached for timelines and profile caching.

- **Wikipedia**  
  Caches page views and database queries.

- **Craigslist**  
  Uses Memcached for caching search queries and user data.

- **Reddit**  
  Relies on Memcached for caching sessions, posts, and comments.

- **YouTube**  
  Uses Memcached to cache video metadata and user preferences.

- **Flickr**  
  Leverages Memcached for caching photo metadata.

- **Apple**  
  Listed among corporate Memcached adopters.

- **Pinterest**, **Shopify**, **Instagram**, **Udemy**, **Instacart**, **Slack**, **Robinhood**, **LinkedIn**  
  Widely known to use Memcached for high-performance caching needs.

- **Amazon**, **Walmart**, **Oracle**  
  Appear in listings of enterprise Memcached users.

> According to StackShare and other sources, Memcached is used by **1,200–1,300 notable companies**, with over **2,700+ companies** using it worldwide.


---

## Performance comparison
  
**Instance:** m5.2xlarge (8 vCPUs, 32 GB RAM)  
**Workload:** 80% Read / 20% Write

| System   | Ops/Sec   | P90 Latency (ms) | P99 Latency (ms) | Throughput (GB/s) |
|----------|-----------|------------------|------------------|-------------------|
| Memcached | 1,200,000 | 0.25             | 0.35             | 1.2               |
| Redis 7   | 1,000,000 | 0.30             | 0.40             | 1.0               |

Source: [Performance and Scalability Analysis of Redis and Memcached](https://dzone.com/articles/performance-and-scalability-analysis-of-redis-memcached)

---

## Installation & Startup

### Linux (Debian/Ubuntu)
```bash
sudo apt install memcached
sudo service memcached start
``` 

## macOS (Homebrew)
```bash
brew install memcached
brew services start memcached
```

## Configuration
Edit /etc/memcached.conf to adjust:
```bash
# memory
-m 64
# Default connection port is 11211
-p 11211
# -c 1024
```

## Command Line Commands

You can interact with Memcached using the `telnet`. Here are some common operations:

### Connect to Memcached

```bash
telnet localhost 11211
```

### Store a Value

```
set mykey 0 900 5
hello
STORED
```
- `mykey`: key name
- `0`: flags (usually 0)
- `900`: expiration time in seconds (900 = 15 minutes)
- `5`: number of bytes in the value ("hello" is 5 bytes)

### Retrieve a Value

```
get mykey
VALUE mykey 0 5
hello
END
```

### Delete a Value

```
delete mykey
DELETED
```

### Increment/Decrement a Value

```
incr counter 1
decr counter 1
```
*(The key must already exist and be an integer value.)*

### List All Keys (not officially supported, but possible with `stats items` and `stats cachedump`)*

```
stats items
stats cachedump <slab_id> <limit>
```
> *Note: Listing all keys is not recommended in production and may not be available in all builds.*

### Other Useful Commands

- **Flush all data:**  
  ```
  flush_all
  ```

- **Check server stats:**  
  ```
  stats
  ```

- **Close connection:**  
  ```
  quit
  ```

---

## Java Client Examples
### Using spymemcached

## References
[Official Documentation](https://memcached.org/)

[Facts - 37 facts about memcached](https://facts.net/tech-and-sciences/computing/37-facts-about-memcached) 

[6sense - memcached market share](https://6sense.com/tech/technology-design-and-architecture/memcached-market-share)

[DZone - Performance and Scalability Analysis of Redis and Memcached](https://dzone.com/articles/performance-and-scalability-analysis-of-redis-memcached)




## Alternatives for manual sharding technique

Approach	Library Needed	Scalability	Used By
CRC32 % N	None (pure Java)	Poor	Small systems
Ketama (custom)	smile-ketama	High	Custom deployments
XMemcached	xmemcached	High	Java ecosystems
SpyMemcached	spymemcached	High	Netflix, legacy apps

import net.spy.memcached.*;
import net.spy.memcached.ConnectionFactoryBuilder.Locator;

MemcachedClient client = new MemcachedClient(
    new ConnectionFactoryBuilder()
        .setLocatorType(Locator.CONSISTENT) // Ketama
        .build(),
    AddrUtil.getAddresses("memcached1:11211,memcached2:11211")
);


How Memcached Scaling Really Works
(No Replication, No Built-in Consistent Hashing)

1. Client-Side Sharding (Key-Based Distribution)
Clients (application code) decide which Memcached node to use by hashing the key (e.g., CRC32(key) % number_of_servers).

No coordination between nodes—each Memcached server operates independently.

Problem: If you add/remove a node, most keys will be remapped (% N changes), causing a thundering herd of cache misses.

2. No Replication → Cache Misses on Failures
If a Memcached node fails, all its data is lost (since it’s purely in-memory).

Clients must fall back to the database and repopulate the new node.

3. Scaling = Just Adding More Nodes
More nodes = More total RAM available for caching.

Traffic spreads across nodes, reducing per-server load.

