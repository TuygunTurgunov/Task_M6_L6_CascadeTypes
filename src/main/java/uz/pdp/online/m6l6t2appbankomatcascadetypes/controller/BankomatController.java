package uz.pdp.online.m6l6t2appbankomatcascadetypes.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.pdp.online.m6l6t2appbankomatcascadetypes.entity.Bankomat;
import uz.pdp.online.m6l6t2appbankomatcascadetypes.payload.ApiResponse;
import uz.pdp.online.m6l6t2appbankomatcascadetypes.payload.BankomatDto;
import uz.pdp.online.m6l6t2appbankomatcascadetypes.service.BankomatService;

import java.util.List;

@RestController
@RequestMapping("/api/bankomat")
public class BankomatController {
    @Autowired
    BankomatService bankomatService;

    @PostMapping
    public HttpEntity<?>add(@RequestBody BankomatDto bankomatDto){
        ApiResponse apiResponse=bankomatService.add(bankomatDto);
        return ResponseEntity.status(apiResponse.getIsSuccess()?201:409).body(apiResponse);

    }

    @PutMapping("/{id}")
    public HttpEntity<?>edit(@PathVariable Integer id,@RequestBody BankomatDto bankomatDto){
        ApiResponse apiResponse=bankomatService.edit(id,bankomatDto);
        return ResponseEntity.status(apiResponse.getIsSuccess()?200:409).body(apiResponse);
    }

    @DeleteMapping("/{id}")
    public HttpEntity<?>delete(@PathVariable Integer id){
        ApiResponse apiResponse = bankomatService.delete(id);
        return ResponseEntity.status(apiResponse.getIsSuccess()?200:409).body(apiResponse);
    }

    @GetMapping("/{id}")
    public HttpEntity<?>getOne(@PathVariable Integer id){
        Bankomat bankomat = bankomatService.getOne(id);
        return ResponseEntity.status(bankomat!=null?200:409).body(bankomat);
    }

    @GetMapping("/getByBankId/{id}")
    public HttpEntity<?>getBankomatByBankomatId(@PathVariable Integer id){
        List<Bankomat> allBankomatByBankId = bankomatService.getAllBankomatByBankId(id);
        return ResponseEntity.status(allBankomatByBankId!=null?200:409).body(allBankomatByBankId);
    }


}