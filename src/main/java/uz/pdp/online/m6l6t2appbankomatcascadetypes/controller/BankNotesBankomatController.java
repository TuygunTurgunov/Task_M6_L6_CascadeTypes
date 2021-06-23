package uz.pdp.online.m6l6t2appbankomatcascadetypes.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.pdp.online.m6l6t2appbankomatcascadetypes.payload.ApiResponse;
import uz.pdp.online.m6l6t2appbankomatcascadetypes.payload.BNBDto;
import uz.pdp.online.m6l6t2appbankomatcascadetypes.service.BankNotesBankomatService;

import java.util.UUID;

@RestController
@RequestMapping("/api/bnb")
public class BankNotesBankomatController {
//BNB ==>    BankNotesBankomat

    @Autowired
    BankNotesBankomatService bankNotesBankomatService;

    @PostMapping
    public HttpEntity<?>add(@RequestBody BNBDto bnbDto){

        ApiResponse apiResponse=bankNotesBankomatService.add(bnbDto);
        return ResponseEntity.status(apiResponse.getIsSuccess()?201:409).body(apiResponse);

    }

    @PutMapping("/{id}")
    public  HttpEntity<?>edit(@PathVariable UUID id,@RequestBody BNBDto bnbDto){
        ApiResponse apiResponse = bankNotesBankomatService.edit(id, bnbDto);
        return ResponseEntity.status(apiResponse.getIsSuccess()?200:409).body(apiResponse);
    }





}
