package com.campus.service;

import com.campus.entity.SiteFeedback;
import com.campus.repository.SiteFeedbackRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class SiteFeedbackService {

    private final SiteFeedbackRepository siteFeedbackRepository;

    public SiteFeedbackService(SiteFeedbackRepository siteFeedbackRepository) {
        this.siteFeedbackRepository = siteFeedbackRepository;
    }

    public Page<SiteFeedback> list(int page, int size, SiteFeedback search) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createTime").descending());

        Specification<SiteFeedback> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (search.getUserId() != null) {
                predicates.add(cb.equal(root.get("userId"), search.getUserId()));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return siteFeedbackRepository.findAll(spec, pageable);
    }

    public SiteFeedback findById(Integer id) {
        return siteFeedbackRepository.findById(id).orElse(null);
    }

    @Transactional
    public void save(SiteFeedback feedback) {
        siteFeedbackRepository.save(feedback);
    }

    @Transactional
    public void delete(Integer id) {
        siteFeedbackRepository.deleteById(id);
    }
}