package kr.okku.server.scheduler;

import kr.okku.server.adapters.persistence.FittingPersistenceAdapter;
import kr.okku.server.domain.FittingDomain;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class FittingStatusScheduler {

    private final FittingPersistenceAdapter fittingPersistenceAdapter;

    private Set<String> processingFittingList;

    public FittingStatusScheduler(FittingPersistenceAdapter fittingPersistenceAdapter) {
        this.fittingPersistenceAdapter = fittingPersistenceAdapter;
    }

    @Scheduled(cron = "* */15 * * * *")
    public void checkStatus () {
        List<FittingDomain> fittingDomains = fittingPersistenceAdapter.findByStatus("waiting");
        fittingDomains.addAll(fittingPersistenceAdapter.findByStatus("processing"));
        Set<String> tempProcessingFittingList = new HashSet<>();
        for (FittingDomain fittingDomain : fittingDomains) {
            if(processingFittingList.contains(fittingDomain.getId())){
                fittingDomain.setStatus("done");
                fittingPersistenceAdapter.save(fittingDomain);
                continue;
            }else{
                tempProcessingFittingList.add(fittingDomain.getId());
            }
        }
        processingFittingList=tempProcessingFittingList;
    }
}
