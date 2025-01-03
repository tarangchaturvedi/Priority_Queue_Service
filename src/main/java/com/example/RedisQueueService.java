package com.example;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Tuple;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class RedisQueueService implements QueueService {

    private final Jedis jedis;
    protected final Integer visibilityTimeout;

    public RedisQueueService() {
        this.jedis = new Jedis("usable-magpie-43846.upstash.io", 6379, true);
        this.jedis.auth("AatGAAIjcDEwMThkZTJiOGIxODM0YTA5YTYxNDkyMDRkNWI1NjZkNnAxMA");

        String propFileName = "config.properties";
        Properties confInfo = new Properties();
        try (InputStream inStream = getClass().getClassLoader().getResourceAsStream(propFileName)) {
            confInfo.load(inStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.visibilityTimeout = Integer.parseInt(confInfo.getProperty("visibilityTimeout", "30"));
    }
    @Override
    public void push(String queueUrl, String message, int priority) {
        try {
            System.out.println("Pushing message: " + message + " with priority: " + priority);
            // Message msg = new Message(message, priority, System.currentTimeMillis());
            Message msg = new Message(message, priority, System.nanoTime());
            msg.setReceiptId(UUID.randomUUID().toString());
            // System.out.println("Serialized Message: " + new Gson().toJson(msg));
    
            this.jedis.zadd(queueUrl, score(msg), new Gson().toJson(msg)); // Push to Redis using the ZADD command.
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public Message pull(String queueUrl) {
        
        Long nowTime = now();
        try {
            // Finding visible message with the highest priority(lowest score).
            Set<Tuple> tuples = jedis.zrangeWithScores(queueUrl, 0, -1);
            if (tuples.isEmpty()) {
                return null;
            }
            for (Tuple tuple : tuples) {
                String deserializedMessage = tuple.getElement();
                Message msg = new Gson().fromJson(deserializedMessage, Message.class);
                
                if (msg != null && msg.isVisibleAt(nowTime)) {
                    // msg.setReceiptId(UUID.randomUUID().toString()); // Set a new receipt ID and increment attempt count and visibility timeout.
                    msg.incrementAttempts();
                    // msg.setVisibleFrom(System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(visibilityTimeout));
                    msg.setVisibleFrom(System.nanoTime() + TimeUnit.SECONDS.toNanos(visibilityTimeout));
                    
                    // Update the message in the queue with the modified attributes
                    String updatedMessage = new Gson().toJson(msg);
                    double updatedScore = tuple.getScore();
                    this.jedis.zrem(queueUrl, deserializedMessage);
                    this.jedis.zadd(queueUrl, updatedScore, updatedMessage);

                    System.out.println("Pulled mesggae: " + msg);
                    return new Message(msg.getBody(), msg.getReceiptId()); // Return the message with the receipt ID
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void delete(String queueUrl, String receiptId) {
        try {
            Set<String> messages = jedis.zrange(queueUrl, 0, -1);  // Fetch all messages from the sorted set.
    
            for (String serializedMessage : messages) {
                Message msg = new Gson().fromJson(serializedMessage, Message.class);
                
                if (msg.getReceiptId().equals(receiptId)) {
                    jedis.zrem(queueUrl, serializedMessage); // Remove the message from the sorted set using ZREM Command.
                    System.out.println("Deleted message with receiptId: " + receiptId);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    long now() {
        return System.nanoTime();
    }

    private double score(Message message) { // The score is based on the priority and timestamp, Highest Priority message will have Lowest Score.
        double messageScore = -(double) message.getPriority() + (double) message.getTimestamp() / 1e12;
        return messageScore;
        
    }

    @Override
    public void clearQueue(String queueUrl) {
        jedis.del(queueUrl); // Deletes the Redis key corresponding to the queue
        // jedis.close();
    }

}
