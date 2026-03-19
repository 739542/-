package com.campus.service;

import com.campus.entity.UserContact;
import com.campus.repository.UserContactRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserContactService {

    private final UserContactRepository userContactRepository;

    public UserContactService(UserContactRepository userContactRepository) {
        this.userContactRepository = userContactRepository;
    }

    public Page<UserContact> list(int page, int size, UserContact search) {
        Pageable pageable = PageRequest.of(page - 1, size);

        Specification<UserContact> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (search.getUserId() != null) {
                predicates.add(cb.equal(root.get("userId"), search.getUserId()));
            }
            if (StringUtils.hasText(search.getName())) {
                predicates.add(cb.like(root.get("name"), "%" + search.getName() + "%"));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return userContactRepository.findAll(spec, pageable);
    }

    public List<UserContact> findAllByUserId(Integer userId) {
        return userContactRepository.findByUserId(userId);
    }

    public UserContact findById(Integer id) {
        return userContactRepository.findById(id).orElse(null);
    }

    @Transactional
    public void save(UserContact contact) {
        userContactRepository.save(contact);
    }

    @Transactional
    public void delete(Integer id) {
        userContactRepository.deleteById(id);
    }
}