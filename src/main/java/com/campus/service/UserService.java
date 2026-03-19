package com.campus.service;

import com.campus.entity.User;
import com.campus.repository.UserRepository;
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
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * 分页查询用户列表 (支持按用户名模糊搜索)
     */
    public Page<User> list(int page, int size, User searchUser) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("id").descending());

        Specification<User> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (StringUtils.hasText(searchUser.getUsername())) {
                predicates.add(cb.like(root.get("username"), "%" + searchUser.getUsername() + "%"));
            }
            // 默认只查询普通用户或特定类型
            if (searchUser.getType() != null) {
                predicates.add(cb.equal(root.get("type"), searchUser.getType()));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return userRepository.findAll(spec, pageable);
    }

    public User findByUsername(String username) {
        // JPA Repository 自动生成的方法
        return userRepository.findByUsername(username);
    }

    public User findById(Integer id) {
        return userRepository.findById(id).orElse(null);
    }

    @Transactional
    public void save(User user) {
        userRepository.save(user); // JPA save 方法兼具新增和修改功能
    }

    @Transactional
    public void delete(Integer id) {
        userRepository.deleteById(id);
    }

    public long count() {
        return userRepository.count();
    }
}