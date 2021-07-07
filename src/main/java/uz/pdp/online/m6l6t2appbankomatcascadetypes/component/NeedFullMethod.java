//package uz.pdp.online.m6l6t2appbankomatcascadetypes.component;
//
//import org.springframework.stereotype.Component;
//
//import java.util.HashMap;
//
//@Component
//public class NeedFullMethod {
//
//    public static void main(String[] args) {
//
//
//        int yuz=13;
//        int ellik=11;
//        int on=32;
//        int besh=5;
//        int ming=71;
//
//        int all = yuz * 100_000 + ellik * 50_000 + on * 10_000+ besh * 5_000 + ming * 1_000;
//        HashMap<String,Integer> map = new HashMap<>();
//        map.put("yuz",yuz);
//        map.put("ellik",ellik);
//        map.put("on",on);
//        map.put("besh",besh);
//        map.put("ming",ming);
//        map.put("baza",all);
//    }
//
//
//    public static boolean calculate(HashMap<String,Integer> baza, int sum){
//        if (sum < baza.get("baza") || sum % 1000 != 0)
//            return false;
//        int res = sum / 1000;
//        if (res > 100)
//            return yuzming(baza, res);
//        if (res > 50)
//            return ellikming(baza, res);
//        if (res > 10)
//            return onming(baza, res);
//        if (res > 5)
//            return beshming(baza, res);
//        return false;
//    }
//
//    public static boolean yuzming(HashMap<String,Integer> baza, int sum){
//        int qoldiq = sum % 100;
//        int count = sum / 100;
//
//        if (baza.get("yuz") > count){
//            baza.put("yuz",baza.get("yuz") - count);
//            sum = qoldiq;
//            return ellikming(baza, sum);
//        }
//        int x = baza.get("yuz") * 100;
//        sum = sum - x;
//        baza.put("yuz",0);
//        if (sum == 0)
//            return true;
//        return ellikming(baza, sum);
//    }
//
//
//    public static boolean ellikming(HashMap<String,Integer> baza, int sum){
//
//        int qoldiq = sum % 50;
//        int count = sum / 50;
//        if (baza.get("ellik") > count){
//            baza.put("ellik",baza.get("ellik") - count);
//            sum = qoldiq;
//            return ellikming(baza, sum);
//        }
//        int ellik = baza.get("ellik") * 50;
//        sum = sum - ellik;
//        baza.put("ellik",0);
//        return ellikming(baza, sum);
//    }
//
//    public static boolean onming(HashMap<String,Integer> baza, int sum){
//return true;
//    }
//
//    public static boolean beshming(HashMap<String,Integer> baza, int sum){
//return true;
//    }
//
//    public static boolean ming(HashMap<String,Integer> baza, int sum){
//return true;
//    }
//
//
//}
