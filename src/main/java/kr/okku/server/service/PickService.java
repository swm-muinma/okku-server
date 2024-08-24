package kr.okku.server.service;
import kr.okku.server.adapters.persistence.pick.PickRepository;
import kr.okku.server.adapters.scraper.ScraperAdapter;
import kr.okku.server.domain.ErrorDomain;
import kr.okku.server.domain.PickDomain;
import kr.okku.server.domain.PlatformDomain;
import org.springframework.stereotype.Service;

@Service
public class PickService {

    private final PickRepository pickRepository;
    private final CartRepository cartRepository;
    private final ScraperAdapter scraperAdapter;
    private final UserRepository userRepository;

    public PickService(PickRepository pickRepository, CartRepository cartRepository, ScraperAdapter scraperAdapter, UserRepository userRepository) {
        this.pickRepository = pickRepository;
        this.cartRepository = cartRepository;
        this.scraperAdapter = scraperAdapter;
        this.userRepository = userRepository;
    }

    public PickDomain createPick(String userId, String url) {
        var user = userRepository.findById(userId).orElseThrow(() -> new ErrorDomain("User not found", 404));

        if (!user.isPremium()) {
            var picks = pickRepository.findByUserId(userId, 1, 10);
            if (picks.getTotalElements() > 8) {
                throw new ErrorDomain("must invite", 402);
            }
        }

        var scrapedData = scraperAdapter.scrape(url);
        if (scrapedData == null) {
            throw new ErrorDomain("domain invalid", 400);
        }

        var platform = new PlatformDomain(
                scrapedData.getPlatform(),
                "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABwAAAAcCAMAAABF0y+mAAAAe1BMVEUAAACtrarj5eb19vempqY2MzC7uLXk4+Hx8fGmqamusK/P0dD////FxsZHR0X8+/nGx8LFysyRk5B5fX5+fnphY2ago550dXlaWligoKCztLY4OTciIiCsq610cm6Li40tLSsbGxm9wrlMS0u3ubHs8fT0+/+EgoLY1dcDqKGoAAAAlElEQVR4AeSPgwEEQQxFs8b8tW30X+Gt1cINk7yQ/mxxvCBugiQrqvZmvKAzGItgWopuw3kyFx6Rj4BI5YkoRPSAcbK+SCnDKuUo6LNKVFRDXnND/LAaa0Z1zW0jfrNcQLM1BNaC/zCgO5rrhzF7Q/XOpCP6znk5S3DeLIVqyLIxLWLDD/kbGgoPAPpacv5NgmGkAQAbCgckaxy7FQAAAABJRU5ErkJggg==",
                "https://www.29cm.co.kr/home/"
        );

        var pick = new PickDomain(
                url, userId, scrapedData.getName(), scrapedData.getPrice(), scrapedData.getThumbnailUrl(), platform, scrapedData.getProductPk()
        );

        return pickRepository.save(pick);
    }
}
