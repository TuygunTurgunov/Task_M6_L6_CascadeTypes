package uz.pdp.online.m6l6t2appbankomatcascadetypes.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.pdp.online.m6l6t2appbankomatcascadetypes.payload.ApiResponse;
import uz.pdp.online.m6l6t2appbankomatcascadetypes.payload.TransferDto;
import uz.pdp.online.m6l6t2appbankomatcascadetypes.service.TransferService;

@RestController
@RequestMapping("/api/transfer")
public class TransferController {

    @Autowired
    TransferService transferService;


    @PostMapping("/outcomeMoney")
    public HttpEntity<?>addOutcomeHistory(@RequestBody TransferDto transferDto){

        ApiResponse apiResponse = transferService.addOutcomeHistory(transferDto);
        return ResponseEntity.status(apiResponse.getIsSuccess()?200:409).body(apiResponse);


    }
}
