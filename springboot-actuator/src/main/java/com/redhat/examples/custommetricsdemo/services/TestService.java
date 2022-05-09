package com.redhat.examples.custommetricsdemo.services;

import com.redhat.examples.custommetricsdemo.dao.DummyDatabase;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@NoArgsConstructor
public class TestService {
    private int numberOfAccesses = 0;
    private int numberOfSuccessfulAttempts=0;
    private int numberOfFailureAttempts=0;
    public int getNumberOfAccesses() {
        return numberOfAccesses;
    }

    private void setNumberOfAccesses(int numberOfAccesses) {
        this.numberOfAccesses = numberOfAccesses;
    }

    public int getNumberOfSuccessfulAttempts() {
        return numberOfSuccessfulAttempts;
    }

    public int getNumberOfFailureAttempts() {
        return numberOfFailureAttempts;
    }

    public String getData(Integer number) {
        this.setNumberOfAccesses(this.numberOfAccesses + 1);
        DummyDatabase db = DummyDatabase.builder().build();
        Map<Boolean, String> result = db.accessDatabase(number);

        //the probability that the service failed
        if (result.keySet().contains(false))
        {
            this.numberOfFailureAttempts++;
        }
        //the probability that the service succeeded
        else
        {
            //the probability that the service failed
            this.numberOfSuccessfulAttempts++;
        }
        System.out.println("Number fetched from db = " +  result.values());
        System.out.println("Number of accesses = " + this.numberOfAccesses);
        System.out.println("Number of successful attempts = " + this.numberOfSuccessfulAttempts);
        System.out.println("Number of failure attempts = " + this.numberOfFailureAttempts);


        return "Hello From Service!!, requested Number is: " + number.toString()   + ",Fetched from database the following number: " + result.values().toString();
     }


}
