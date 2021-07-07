package uz.pdp.online.m6l6t2appbankomatcascadetypes.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.pdp.online.m6l6t2appbankomatcascadetypes.entity.InHistory;
import uz.pdp.online.m6l6t2appbankomatcascadetypes.entity.OutHistory;
import uz.pdp.online.m6l6t2appbankomatcascadetypes.payload.HistoryDto;
import uz.pdp.online.m6l6t2appbankomatcascadetypes.service.InHistoryService;
import uz.pdp.online.m6l6t2appbankomatcascadetypes.service.OutHistoryService;

import java.util.List;


@RestController
@RequestMapping("/api/history")
public class HistoryController {
    @Autowired
    OutHistoryService outHistoryService;

    @Autowired
    InHistoryService inHistoryService;


    @GetMapping("/out")
    public HttpEntity<?>getOutHistory(@RequestBody HistoryDto historyDto){
        List<OutHistory> outHistoryList = outHistoryService.getOutHistory(historyDto);
        return ResponseEntity.status(outHistoryList!=null?200:409).body(outHistoryList);
    }

    @GetMapping("/in")
    public HttpEntity<?>getInHistory(@RequestBody HistoryDto historyDto){

        List<InHistory> inHistory = inHistoryService.getInHistory(historyDto);
        return ResponseEntity.status(inHistory!=null?200:409).body(inHistory);

    }
}