package uz.pdp.online.m6l6t2appbankomatcascadetypes.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.pdp.online.m6l6t2appbankomatcascadetypes.payload.ApiResponse;
import uz.pdp.online.m6l6t2appbankomatcascadetypes.payload.TransferDto;
import uz.pdp.online.m6l6t2appbankomatcascadetypes.service.TransferIncomeService;
import uz.pdp.online.m6l6t2appbankomatcascadetypes.service.TransferOutcomeDollarService;
import uz.pdp.online.m6l6t2appbankomatcascadetypes.service.TransferOutcomeService;

@RestController
@RequestMapping("/api/transfer")
public class TransferController {

    @Autowired
    TransferOutcomeService transferOutcomeService;
    @Autowired
    TransferIncomeService transferIncomeService;
    @Autowired
    TransferOutcomeDollarService transferOutcomeDollarService;


    @PostMapping("/outcomeMoney")
    public HttpEntity<?>addOutcomeHistory(@RequestBody TransferDto transferDto){

        ApiResponse apiResponse = transferOutcomeService.calculate(transferDto);
        return ResponseEntity.status(apiResponse.getIsSuccess()?200:409).body(apiResponse);

    }

    @PostMapping("/incomeMoney")
    public HttpEntity<?>addIncomeHistory(@RequestBody TransferDto transferDto){

        ApiResponse apiResponse = transferIncomeService.calculate(transferDto);
        return ResponseEntity.status(apiResponse.getIsSuccess()?200:409).body(apiResponse);

    }

    @PostMapping("/outcomeMoney/dollar")
    public HttpEntity<?>addOutcomeHistoryDollar(@RequestBody TransferDto transferDto){

        ApiResponse apiResponse = transferOutcomeDollarService.calculateDollar(transferDto);
        return ResponseEntity.status(apiResponse.getIsSuccess()?200:409).body(apiResponse);

    }
}