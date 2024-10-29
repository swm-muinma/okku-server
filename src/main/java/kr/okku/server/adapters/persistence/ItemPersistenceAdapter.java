package kr.okku.server.adapters.persistence;

import kr.okku.server.adapters.persistence.repository.item.ItemEntity;
import kr.okku.server.adapters.persistence.repository.item.ItemRepository;
import kr.okku.server.adapters.persistence.repository.pick.PickEntity;
import kr.okku.server.adapters.persistence.repository.pick.PickRepository;
import kr.okku.server.adapters.scraper.ScraperAdapter;
import kr.okku.server.domain.PickDomain;
import kr.okku.server.domain.ScrapedDataDomain;
import kr.okku.server.mapper.PickMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ItemPersistenceAdapter {

    private final ItemRepository itemRepository;

    // PickRepository 의존성 주입
    public ItemPersistenceAdapter(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public Optional<ScrapedDataDomain> findByUrl(String url) {
        return itemRepository.findByUrl(url)
                .map(item -> ScrapedDataDomain.builder()
                        .id(item.getId())
                        .url(url)
                        .name(item.getName())
                        .image(item.getImage())
                        .productPk(item.getPk())
                        .platform(item.getPlatform())
                        .price(item.getPrice())
                        .build());
    }

    public Optional<ScrapedDataDomain> findByPlatformAndProductpk(String platform, String productPk) {
        return itemRepository.findByPlatformAndPk(platform,productPk)
                .map(item -> ScrapedDataDomain.builder()
                        .id(item.getId())
                        .url(item.getUrl())
                        .name(item.getName())
                        .image(item.getImage())
                        .productPk(item.getPk())
                        .platform(item.getPlatform())
                        .price(item.getPrice())
                        .build());
    }

    public Integer getPickNum(String platform, String productPk) {
        Optional<ItemEntity> optionalItemEntity = itemRepository.findByPlatformAndPk(platform, productPk);
        ItemEntity itemEntity = optionalItemEntity.orElse(null);
        if(itemEntity==null || itemEntity.getPickNum()==null){
            return 0;
        }
        return itemEntity.getPickNum();
    }

    public Boolean save(String url, String name, String image,Integer price, String pk, String platform, Integer pickNum){

        try {
            ItemEntity item = new ItemEntity();
            item.setPk(pk);
            item.setName(name);
            item.setPlatform(platform);
            item.setPrice(price);
            item.setImage(image);
            item.setUrl(url);
            item.setPickNum(pickNum);
            itemRepository.save(item);

            return true;
        }catch (Exception e){
            return false;
        }
    }

    public Boolean update(String platform,String pk, Integer pickNum){

        try {
            ItemEntity item = itemRepository.findByPlatformAndPk(platform,pk).orElse(null);
            if(item==null){
                return false;
            }
            item.setPickNum(pickNum);
            itemRepository.save(item);

            return true;
        }catch (Exception e){
            return false;
        }
    }
}
