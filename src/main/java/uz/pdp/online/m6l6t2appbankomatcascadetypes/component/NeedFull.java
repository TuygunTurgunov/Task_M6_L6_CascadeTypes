//package uz.pdp.online.m6l6t2appbankomatcascadetypes.component;
//
//import org.springframework.stereotype.Component;
//
//
//import java.util.HashMap;
//
//@Component
//public class NeedFull {
//
//    public static void main(String[] args) {
//
//
//        HashMap<String,Long> baza = new HashMap<>();
//
//        Long yuzMing = 1l;
//        Long ellikMing = 113l;
//        Long onMing = 271l;
//        Long beshMing = 51l;
//        Long ming = 6l;
//
//        Long min = 5_000l;
//        Long max = 10_500_000l;
//
//
//        Long all = yuzMing * 100_000 + ellikMing * 50_000 + onMing * 10_000 + beshMing * 5_000 + ming * 1_000;
//
//        baza.put("yuz",yuzMing);
//        baza.put("ellik",ellikMing);
//        baza.put("on",onMing);
//        baza.put("besh",beshMing);
//        baza.put("ming",ming);
//        baza.put("baza",all);
//        baza.put("min",min);
//        baza.put("max",max);
//
//        System.out.println(all);
//
//        ApiResponse apiResponse = exchange(baza, 7_784_000l);
//        System.out.println(apiResponse);
//        System.out.println(apiResponse.getBaza().get("baza"));
//        ApiResponse apiResponse1 = exchange(apiResponse.getBaza(), 787_000l);
//        System.out.println(apiResponse1);
//        System.out.println(apiResponse1.getBaza().get("baza"));
//
//
//    }
//
//    public static ApiResponse exchange(HashMap<String,Long> baza, Long sum){
//        if (sum < baza.get("min") || sum > baza.get("max"))
//            return new ApiResponse(false,null);
//        if (!(sum % 1000 == 0 && sum >= 5000 && baza.get("baza") > sum))
//            return new ApiResponse(false,null);
//        int lastNumberOfThousand = Math.toIntExact(sum % 10_000 / 1_000);//oxirgi mingtalik raqami masalan 17 000 bo'lsa javob 7 qaytadi
//
//        //Oxirgi 4 yoki 7 ming kabi sonlarni berish uchun bazada yetarli pul bormi
//        boolean enoughLastNumber = false;
//        if (lastNumberOfThousand > 5){//agar beshdan katta bo'lsa bitta beshmingtalik va mingtalik yoki ming taliklar soni yetarli bo'lishi kerak
//            if (baza.get("ming") >= lastNumberOfThousand || (baza.get("besh") > 0 && baza.get("ming") >= lastNumberOfThousand - 5))
//                enoughLastNumber = true;
//        }else if (lastNumberOfThousand == 5){
//            if (baza.get("besh") > 0 || baza.get("ming") > 4)
//                enoughLastNumber = true;
//        }else {
//            if (baza.get("ming") >= lastNumberOfThousand)
//                enoughLastNumber = true;
//        }
//
//        //Agar oxirgi summasini berolmasak pul yechishni imkoni yo'q
//        if (!enoughLastNumber)
//            return new ApiResponse(false,null);
//
//
//        if (sum > 100_000)
//            return yuzming(baza, sum);
//        if (sum > 50_000)
//            return ellikming(baza, sum);
//        if (sum > 10_000)
//            return onming(baza, sum);
//        return beshming(baza, sum);
//    }
//
//
//    public static ApiResponse yuzming(HashMap<String,Long> baza, long sum){
//        long count = sum / 100_000;
//        if (baza.get("yuz") >= count) {
//            baza.put("yuz",baza.get("yuz") - count);
//            if (sum % 100_000 == 0) //bizga kelgan summa 300 200 kabi yaxlit summa bo'lsa method ishi tugaydi
//                return new ApiResponse(true, updateBaza(baza));
//            sum = sum - count * 100_000;
//            return ellikming(baza, sum);
//        }
//        sum = sum - baza.get("yuz") * 100_000;
//        baza.put("yuz",0L);
//        return ellikming(baza,sum);
//    }
//
//    public static ApiResponse ellikming(HashMap<String,Long> baza, long sum){
//        if (sum >= 50_000){
//            long count = sum / 50_000;
//            if (baza.get("ellik") >= count){
//                baza.put("ellik",baza.get("ellik") - count);
//                if (sum % 50_000 == 0)
//                    return new ApiResponse(true,updateBaza(baza));
//                sum = sum - count * 50_000;
//                return onming(baza, sum);
//            }
//            sum = sum - baza.get("ellik") * 50_000;
//            baza.put("ellik", 0L);
//            return onming(baza,sum);
//        }else {
//            return onming(baza, sum);
//        }
//    }
//
//    public static ApiResponse onming(HashMap<String,Long> baza, long sum){
//        if (sum >= 10_000){
//            long count = sum / 10_000;
//            if (baza.get("on") >= count){
//                baza.put("on",baza.get("on") - count);
//                if (sum % 10_000 == 0)
//                    return new ApiResponse(true,updateBaza(baza));
//                sum = sum - count * 10_000;
//                return onming(baza, sum);
//            }
//            sum = sum - baza.get("on") * 10_000;
//            baza.put("on",0L);
//            return beshming(baza,sum);
//        }else {
//            return beshming(baza, sum);
//        }
//    }
//
//    public static ApiResponse beshming(HashMap<String,Long> baza, long sum){
//        if (sum >= 5_000){
//            long count = sum / 5_000;
//            if (baza.get("besh") >= count){
//                baza.put("besh",baza.get("besh") - count);
//                if (sum % 5_000 == 0)
//                    return new ApiResponse(true,updateBaza(baza));
//                sum = sum - count * 5_000;
//                return onming(baza, sum);
//            }
//            sum = sum - baza.get("besh") * 5_000;
//            baza.put("besh",0L);
//            return ming(baza,sum);
//        }else {
//            return ming(baza, sum);
//        }
//    }
//
//    public static ApiResponse ming(HashMap<String,Long> baza, long sum){
//        if (sum >= 1_000){
//            long count = sum / 1_000;
//            if (baza.get("ming") >= count){
//                baza.put("ming",baza.get("ming") - count);
//                return new ApiResponse(true,updateBaza(baza));
//            }
//        }
//        return new ApiResponse(false,null);
//    }
//
//
//    public static HashMap<String,Long> updateBaza(HashMap<String,Long> baza){
//        Long yuz = baza.get("yuz");
//        Long ellik = baza.get("ellik");
//        Long on = baza.get("on");
//        Long besh = baza.get("besh");
//        Long ming = baza.get("ming");
//        Long all = yuz * 100_000 + ellik * 50_000 + on * 10_000 + besh * 5_000 + ming * 1_000;
//        baza.put("baza",all);
//        return baza;
//    }
//}