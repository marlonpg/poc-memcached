package com.gamba.software.poc_memcached;

import net.spy.memcached.MemcachedClient;

import java.net.InetSocketAddress;
import java.util.concurrent.Future;

public class MemcachedJavaExample {
    public static void main(String[] args) {
        MemcachedClient memcachedClient = null;
        try {
            // connect to my localhost memcached server
            memcachedClient = new MemcachedClient(new InetSocketAddress("localhost", 11211));
            System.out.println("Connection to server successful.");

            // set value and defining ttl 60 seconds
            Future<Boolean> setResult = memcachedClient.set("myKey", 60, "Hello from Java!");
            if (setResult.get()) {
                System.out.println("Set 'myKey'");
            } else {
                System.out.println("Failed to set 'myKey'");
            }

            // Get a value
            Object value = memcachedClient.get("myKey");
            if (value != null) {
                System.out.println("Got 'myKey': " + value);
            } else {
                System.out.println("'myKey' not found.");
            }

            // delete a value
            Future<Boolean> deleteResult = memcachedClient.delete("myKey");
            if (deleteResult.get()) {
                System.out.println("Deleted 'myKey'");
            } else {
                System.out.println("Failed to delete 'myKey' (or key didn't exist).");
            }

            // verify deletion
            if (memcachedClient.get("myKey") == null) {
                System.out.println("'myKey' is successfully deleted.");
            }

        } catch (Exception e) {
            System.err.println("Memcached error: " + e.getMessage());
        } finally {
            if (memcachedClient != null) {
                memcachedClient.shutdown();
                System.out.println("Connection closed.");
            }
        }
    }
}