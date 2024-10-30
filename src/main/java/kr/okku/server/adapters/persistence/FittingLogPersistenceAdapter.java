package kr.okku.server.adapters.persistence;

import kr.okku.server.adapters.persistence.repository.fitting.FittingEntity;
import kr.okku.server.adapters.persistence.repository.fitting.FittingRepository;
import kr.okku.server.adapters.persistence.repository.fittinglog.FittingLogEntity;
import kr.okku.server.adapters.persistence.repository.fittinglog.FittingLogRepository;
import kr.okku.server.adapters.persistence.repository.pick.PickEntity;
import kr.okku.server.domain.FittingDomain;
import kr.okku.server.domain.FittingLogDomain;
import kr.okku.server.domain.PickDomain;
import kr.okku.server.mapper.FittingLogMapper;
import kr.okku.server.mapper.FittingMapper;
import kr.okku.server.mapper.PickMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FittingLogPersistenceAdapter {

    private final FittingLogRepository fittingLogRepository;

    public FittingLogPersistenceAdapter(FittingLogRepository fittingLogRepository) {
        this.fittingLogRepository = fittingLogRepository;
    }

    public List<FittingLogDomain> findAll() {
        return fittingLogRepository.findAll()
                .stream()
                .map(FittingLogMapper::toDomain)
                .collect(Collectors.toList());
    }
    public FittingLogDomain save(FittingLogDomain fittingLogDomain) {
        FittingLogEntity fittingLogEntity = FittingLogMapper.toEntity(fittingLogDomain);
        FittingLogEntity savedEntity = fittingLogRepository.save(fittingLogEntity);
        return FittingLogMapper.toDomain(savedEntity);
    }

}
