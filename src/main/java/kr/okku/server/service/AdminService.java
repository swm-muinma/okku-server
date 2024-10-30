package kr.okku.server.service;

import kr.okku.server.adapters.persistence.*;
import kr.okku.server.adapters.scraper.ScraperAdapter;
import kr.okku.server.domain.*;
import kr.okku.server.dto.controller.BasicRequestDto;
import kr.okku.server.dto.controller.PageInfoResponseDto;
import kr.okku.server.dto.controller.admin.FiittingListResponseDto;
import kr.okku.server.dto.controller.pick.*;
import kr.okku.server.exception.ErrorCode;
import kr.okku.server.exception.ErrorDomain;
import kr.okku.server.mapper.PickMapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class AdminService {
    private final PickPersistenceAdapter pickPersistenceAdapter;
    private final CartPersistenceAdapter cartPersistenceAdapter;
    private final ScraperAdapter scraperAdapter;
    private final UserPersistenceAdapter userPersistenceAdapter;
    private final ItemPersistenceAdapter itemPersistenceAdapter;
    private final FittingPersistenceAdapter fittingPersistenceAdapter;

    private final Utils utils;

    @Autowired
    public AdminService(PickPersistenceAdapter pickPersistenceAdapter, CartPersistenceAdapter cartPersistenceAdapter,
                        ScraperAdapter scraperAdapter, UserPersistenceAdapter userPersistenceAdapter, ItemPersistenceAdapter itemPersistenceAdapter, FittingPersistenceAdapter fittingPersistenceAdapter, Utils utils
                     ) {
        this.pickPersistenceAdapter = pickPersistenceAdapter;
        this.cartPersistenceAdapter = cartPersistenceAdapter;
        this.scraperAdapter = scraperAdapter;
        this.userPersistenceAdapter = userPersistenceAdapter;
        this.itemPersistenceAdapter = itemPersistenceAdapter;
        this.fittingPersistenceAdapter = fittingPersistenceAdapter;
        this.utils = utils;
    }

    public FiittingListResponseDto getFiittingList(String input) {
        List<FittingDomain> fittingDomains = fittingPersistenceAdapter.findAll();

        return null;
    }

}
