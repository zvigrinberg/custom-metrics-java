package com.redhat.examples.custommetricsdemo.dao;

import lombok.Builder;

import java.util.Map;


@Builder
public class DummyDatabase {

    private final long seed=786738252;
//returns a map with a signle entry - the key to indicates simulated random generated success or failure boolean indication
// for accessing the database, and a simulated random value in range [1..100] that "returned" from database.
    public Map<Boolean,String> accessDatabase(Integer number)
    {

        int random = (int)(Math.random() * seed) % 100;
        int probabilityOfSuccess = (int)(Math.random() * 1389762) % 6;
        //the probability the the service failed
        if (probabilityOfSuccess == 0)
        {
            return Map.of(false,"0");
        }
        //the probability that the service succeeded
        else
        {
            //the probability that the service failed
            return Map.of(true,Integer.valueOf(random).toString());
        }
    }

}
