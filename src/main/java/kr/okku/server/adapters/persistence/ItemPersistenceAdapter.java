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
                        .url(url)
                        .name(item.getName())
                        .image(item.getImage())
                        .productPk(item.getPk())
                        .platform(item.getPlatform())
                        .price(item.getPrice())
                        .build());
    }

    public Boolean save(String url, String name, String image,Integer price, String pk, String platform){

        try {
            ItemEntity item = new ItemEntity();
            item.setPk(pk);
            item.setName(name);
            item.setPlatform(platform);
            item.setPrice(price);
            item.setImage(image);
            item.setUrl(url);
            itemRepository.save(item);

            return true;
        }catch (Exception e){
            return false;
        }
    }
}
