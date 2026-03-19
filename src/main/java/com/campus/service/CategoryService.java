package com.campus.service;

import com.campus.entity.Category;
import com.campus.repository.CategoryRepository;
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

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<Category> findAll() {
        // 按排序字段升序
        return categoryRepository.findAll(Sort.by("sortOrder").ascending());
    }

    public Page<Category> list(int page, int size, Category searchCategory) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("sortOrder").ascending());

        Specification<Category> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (StringUtils.hasText(searchCategory.getName())) {
                predicates.add(cb.like(root.get("name"), "%" + searchCategory.getName() + "%"));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return categoryRepository.findAll(spec, pageable);
    }

    public Category findById(Integer id) {
        return categoryRepository.findById(id).orElse(null);
    }

    @Transactional
    public void save(Category category) {
        categoryRepository.save(category);
    }

    @Transactional
    public void delete(Integer id) {
        categoryRepository.deleteById(id);
    }
}