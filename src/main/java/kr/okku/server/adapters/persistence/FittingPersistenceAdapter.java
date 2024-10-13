package kr.okku.server.adapters.persistence;

import kr.okku.server.adapters.persistence.repository.fitting.FittingEntity;
import kr.okku.server.adapters.persistence.repository.fitting.FittingRepository;
import kr.okku.server.adapters.persistence.repository.user.UserEntity;
import kr.okku.server.domain.FittingDomain;
import kr.okku.server.domain.PickDomain;
import kr.okku.server.domain.UserDomain;
import kr.okku.server.mapper.FittingMapper;
import kr.okku.server.mapper.PickMapper;
import kr.okku.server.mapper.UserMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    public List<FittingDomain> findByIdIn(List<String> ids) {
        return fittingRepository.findByIdInOrderByCreatedAtDesc(ids)
                .stream()
                .map(FittingMapper::toDomain)
                .collect(Collectors.toList());
    }

    public List<FittingDomain> findByIdUserId(String id) {
        return fittingRepository.findByUserPkOrderByCreatedAtDesc(id)
                .stream()
                .map(FittingMapper::toDomain)
                .collect(Collectors.toList());
    }
}
