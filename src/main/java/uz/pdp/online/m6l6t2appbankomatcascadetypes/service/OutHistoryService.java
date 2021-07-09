package uz.pdp.online.m6l6t2appbankomatcascadetypes.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import uz.pdp.online.m6l6t2appbankomatcascadetypes.entity.Bankomat;
import uz.pdp.online.m6l6t2appbankomatcascadetypes.entity.OutHistory;
import uz.pdp.online.m6l6t2appbankomatcascadetypes.entity.User;
import uz.pdp.online.m6l6t2appbankomatcascadetypes.payload.HistoryDto;
import uz.pdp.online.m6l6t2appbankomatcascadetypes.repository.BankomatRepository;
import uz.pdp.online.m6l6t2appbankomatcascadetypes.repository.CardRepository;
import uz.pdp.online.m6l6t2appbankomatcascadetypes.repository.OutHistoryRepository;

import java.util.List;
import java.util.Optional;

@Service
public class OutHistoryService {
    @Autowired
    private OutHistoryRepository outHistoryRepository;
    @Autowired
    private BankomatRepository bankomatRepository;
    @Autowired
    private UserType userType;

    public List<OutHistory> getOutHistory(HistoryDto historyDto){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        Integer userType = this.userType.getUserType(user);
        if (userType!=1)
            return null;


        Optional<Bankomat> optionalBankomat = bankomatRepository.findById(historyDto.getBankomatId());
        if (!optionalBankomat.isPresent())
            return null;
        Bankomat bankomat = optionalBankomat.get();
        List<OutHistory> allByCardAndBankomat = outHistoryRepository.findAllByBankomat(bankomat);
        return allByCardAndBankomat;
    }




}
