package uz.pdp.online.m6l6t2appbankomatcascadetypes.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uz.pdp.online.m6l6t2appbankomatcascadetypes.repository.BankomatRepository;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

@Service
public class SortNumbers {
    @Autowired
    BankomatRepository bankomatRepository;

    public List<Integer> sort(Integer summa){
        ArrayList<Integer> reverseList=new ArrayList<>();

        summa=summa/1000;

        int digit;
        while (summa>0){
            digit=summa%10;
            reverseList.add(digit);
            summa=summa/10;
        }

        ArrayList<Integer> arrayList=new ArrayList<>();

        for (int i = reverseList.size(); i >0 ; i--) {
            arrayList.add(reverseList.get(i-1));
        }

        return arrayList;

    }




}