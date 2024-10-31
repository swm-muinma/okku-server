package kr.okku.server.adapters.persistence;

import com.mongodb.lang.Nullable;
import kr.okku.server.adapters.persistence.repository.pick.PickEntity;
import kr.okku.server.adapters.persistence.repository.pick.PickRepository;
import kr.okku.server.domain.PickDomain;
import kr.okku.server.mapper.PickMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PickPersistenceAdapter {

    private final PickRepository pickRepository;

    // PickRepository 의존성 주입
    public PickPersistenceAdapter(PickRepository pickRepository) {
        this.pickRepository = pickRepository;
    }

    // Domain -> Entity로 변환 후 저장
    public PickDomain save(PickDomain pickDomain) {
        PickEntity pickEntity = PickMapper.toEntity(pickDomain);
        PickEntity savedEntity = pickRepository.save(pickEntity);
        return PickMapper.toDomain(savedEntity);
    }

    public Optional<Date> getCreatedAt(String pickId){
        try {
            return Optional.ofNullable(pickRepository.findById(pickId).get().getCreatedAt());
        }catch (Exception e){
            return Optional.ofNullable(null);
        }
    }

    // 사용자 ID로 Pick 조회 (Pageable)
    public Page<PickDomain> findByUserId(String userId, Pageable pageable) {
        return pickRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable)
                .map(PickMapper::toDomain);
    }
    public Optional<PickDomain> findById(String id) {
        return Optional.ofNullable(PickMapper.toDomain(pickRepository.findByIdOrderByCreatedAtDesc(id).get()));
    }
    // 사용자 ID로 Pick 조회
    public List<PickDomain> findByUserId(String userId) {
        return pickRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(PickMapper::toDomain)
                .collect(Collectors.toList());
    }

    // 여러 Pick ID로 조회
    public List<PickDomain> findByIdIn(List<String> pickIds) {
        return pickRepository.findByIdInOrderByCreatedAtDesc(pickIds)
                .stream()
                .map(PickMapper::toDomain)
                .collect(Collectors.toList());
    }

    public Page<PickDomain> findByIdIn(List<String> pickIds, Pageable pageable) {
        // 데이터베이스에서 엔티티를 페이지 단위로 가져온다
        Page<PickEntity> pickEntityPage = pickRepository.findByIdInOrderByCreatedAtDesc(pickIds, pageable);

        // 엔티티 리스트를 도메인 객체 리스트로 변환
        List<PickDomain> pickDomainList = pickEntityPage.getContent().stream()
                .map(PickMapper::toDomain)
                .collect(Collectors.toList());

        // 변환된 도메인 객체 리스트로 Page 객체 생성
        return new PageImpl<>(pickDomainList, pageable, pickEntityPage.getTotalElements());
    }

    // 여러 Pick ID로 삭제
    public long deleteByIdIn(List<String> pickIds) {
        return pickRepository.deleteByIdIn(pickIds);
    }
}
