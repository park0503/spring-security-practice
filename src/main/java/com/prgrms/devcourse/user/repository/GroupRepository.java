package com.prgrms.devcourse.user.repository;

import com.prgrms.devcourse.user.Group;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GroupRepository extends JpaRepository<Group, Long> {
    Optional<Group> findByName(String name);
}
