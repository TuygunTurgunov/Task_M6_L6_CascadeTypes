package uz.pdp.online.m6l6t2appbankomatcascadetypes.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uz.pdp.online.m6l6t2appbankomatcascadetypes.repository.CardRepository;

import java.util.Random;

@Service
public class RandomCardNumber {
    @Autowired
    private CardRepository cardRepository;

    public Long getNumber(){
        Random rand = new Random();
        long x = (long)(rand.nextDouble()*10000000000000000L);
        boolean existsByCardNumber = cardRepository.existsByCardNumber(x);
        if (existsByCardNumber)
            return getNumber();
        return x;


    }



}
