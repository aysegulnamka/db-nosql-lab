package org.example.store;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;


public class HazelcastServer {
    public static void main(String[] args) {
        HazelcastInstance hazelcastInstance = Hazelcast.newHazelcastInstance();
        System.out.println("Hazelcast sunucusu başlatıldı!");
    }
}
