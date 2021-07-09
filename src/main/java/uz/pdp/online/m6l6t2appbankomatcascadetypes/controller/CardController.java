package uz.pdp.online.m6l6t2appbankomatcascadetypes.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.pdp.online.m6l6t2appbankomatcascadetypes.entity.Card;
import uz.pdp.online.m6l6t2appbankomatcascadetypes.payload.ApiResponse;
import uz.pdp.online.m6l6t2appbankomatcascadetypes.payload.CardBlockedDto;
import uz.pdp.online.m6l6t2appbankomatcascadetypes.payload.CardDto;
import uz.pdp.online.m6l6t2appbankomatcascadetypes.service.CardService;

import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/api/card")
public class CardController {
    @Autowired
    private CardService cardService;

    @PostMapping
    public HttpEntity<?>add(@RequestBody CardDto cardDto){
        ApiResponse apiResponse = cardService.add(cardDto);
        return ResponseEntity.status(apiResponse.getIsSuccess()?201:409).body(apiResponse);
    }

    @PutMapping("/{id}")
    public HttpEntity<?> edit(@PathVariable UUID id){
        ApiResponse apiResponse = cardService.edit(id);
        return ResponseEntity.status(apiResponse.getIsSuccess()? HttpStatus.OK:HttpStatus.CONFLICT).body(apiResponse);

    }

    @PutMapping("/block/{cardNumber}")
    public HttpEntity<?>editBlocked(@PathVariable Long cardNumber){
        ApiResponse apiResponse=cardService.editBlocked(cardNumber);
        return ResponseEntity.status(apiResponse.getIsSuccess()?200:409).body(apiResponse);

    }

    @GetMapping("/{id}")
    public HttpEntity<?>getOne(@PathVariable UUID id){

        Card oneCard = cardService.getOneCard(id);
        return ResponseEntity.status(oneCard!=null?200:409).body(oneCard);

    }
    @GetMapping
    public HttpEntity<?>getAllCard(){

        List<Card> cardList = cardService.getALlCard();
        return ResponseEntity.status(cardList!=null?200:409).body(cardList);
    }


    @DeleteMapping("/{id}")
    public HttpEntity<?> delete(@PathVariable UUID id){
        ApiResponse apiResponse=cardService.delete(id);
        return ResponseEntity.status(apiResponse.getIsSuccess()?200:409).body(apiResponse);
    }
}