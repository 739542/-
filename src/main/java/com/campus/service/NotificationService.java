package com.campus.service;

import com.campus.entity.IdleItem;
import com.campus.entity.Notification;
import com.campus.entity.User;
import com.campus.repository.IdleItemRepository;
import com.campus.repository.NotificationRepository;
import com.campus.repository.UserRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
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
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private IdleItemRepository idleItemRepository;

    public Page<Notification> list(int page, int size, Notification search) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createTime").descending());

        Specification<Notification> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (search.getUserId() != null) {
                predicates.add(cb.equal(root.get("userId"), search.getUserId()));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<Notification> result = notificationRepository.findAll(spec, pageable);

        // 填充关联信息
        result.getContent().forEach(n -> {
            if(n.getSenderId() != null) {
                n.setSender(userRepository.findById(n.getSenderId()).orElse(new User()));
            }
            if(n.getItemId() != null) {
                n.setIdleItem(idleItemRepository.findById(n.getItemId()).orElse(new IdleItem()));
            }
        });

        return result;
    }

    /**
     * 获取聊天记录
     */
    public List<Notification> getChatHistory(Integer myId, Integer otherId, Integer itemId) {
        List<Notification> list = notificationRepository.findChatHistory(myId, otherId, itemId);
        // 填充发送者信息
        list.forEach(n -> {
            if(n.getSenderId() != null) {
                n.setSender(userRepository.findById(n.getSenderId()).orElse(new User()));
            }
        });
        return list;
    }

    @Transactional
    public void save(Notification notification) {
        notificationRepository.save(notification);
    }

    @Transactional
    public void delete(Integer id) {
        notificationRepository.deleteById(id);
    }

    public long countUnread(Integer userId) {
        return notificationRepository.countByUserIdAndIsRead(userId, 0);
    }
}