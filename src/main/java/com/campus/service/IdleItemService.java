package com.campus.service;

import com.campus.entity.IdleItem;
import com.campus.repository.IdleItemRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class IdleItemService {

    private final IdleItemRepository idleItemRepository;

    public IdleItemService(IdleItemRepository idleItemRepository) {
        this.idleItemRepository = idleItemRepository;
    }

    /**
     * 多条件分页查询物品
     */
    public Page<IdleItem> list(int page, int size, IdleItem searchItem) {
        // 默认按发布时间降序
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("releaseTime").descending());

        Specification<IdleItem> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(searchItem.getName())) {
                predicates.add(cb.like(root.get("name"), "%" + searchItem.getName() + "%"));
            }
            if (searchItem.getStatus() != null) {
                predicates.add(cb.equal(root.get("status"), searchItem.getStatus()));
            }
            if (searchItem.getCategoryId() != null) {
                predicates.add(cb.equal(root.get("categoryId"), searchItem.getCategoryId()));
            }
            if (searchItem.getUserId() != null) {
                predicates.add(cb.equal(root.get("userId"), searchItem.getUserId()));
            }
            if (searchItem.getIsRecommended() != null) {
                predicates.add(cb.equal(root.get("isRecommended"), searchItem.getIsRecommended()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return idleItemRepository.findAll(spec, pageable);
    }

    public IdleItem findById(Integer id) {
        return idleItemRepository.findById(id).orElse(null);
    }

    @Transactional
    public void save(IdleItem item) {
        if (item.getId() == null) {
            item.setReleaseTime(new Date());
        }
        idleItemRepository.save(item);
    }

    @Transactional
    public void delete(Integer id) {
        idleItemRepository.deleteById(id);
    }

    public long count() {
        return idleItemRepository.count();
    }

    // 获取推荐过期的商品ID列表
    public List<Integer> getExpiredRecommendItemIds() {
        // 在Repository 中定义的@Query
        return idleItemRepository.findExpiredRecommendIds();
    }
}