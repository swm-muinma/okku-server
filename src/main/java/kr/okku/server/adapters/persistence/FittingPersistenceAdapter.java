package kr.okku.server.adapters.persistence;

import kr.okku.server.adapters.persistence.repository.fitting.FittingEntity;
import kr.okku.server.adapters.persistence.repository.fitting.FittingRepository;
import kr.okku.server.adapters.persistence.repository.user.UserEntity;
import kr.okku.server.domain.FittingDomain;
import kr.okku.server.domain.UserDomain;
import kr.okku.server.mapper.FittingMapper;
import kr.okku.server.mapper.UserMapper;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class FittingPersistenceAdapter {

    private final FittingRepository fittingRepository;

    public FittingPersistenceAdapter(FittingRepository fittingRepository) {
        this.fittingRepository = fittingRepository;
    }

    public Optional<FittingDomain> findById(String id) {
        FittingEntity fittingEntity = fittingRepository.findById(id).orElse(null);
        return Optional.ofNullable(FittingMapper.toDomain(fittingEntity));
    }
}
