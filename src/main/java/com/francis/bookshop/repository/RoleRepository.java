package com.francis.bookshop.repository;

import com.francis.bookshop.entity.Role;
import com.francis.bookshop.enums.UserRole;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
  Optional<Role> findByName(UserRole name);
}
