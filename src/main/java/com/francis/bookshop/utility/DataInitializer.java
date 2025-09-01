package com.francis.bookshop.utility;

import com.francis.bookshop.entity.Role;
import com.francis.bookshop.enums.UserRole;
import com.francis.bookshop.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

  private final RoleRepository roleRepository;

  @Override
  public void run(String... args) throws Exception {
    // Loop through enum values
    for (UserRole userRole : UserRole.values()) {

      roleRepository
          .findByName(userRole)
          .orElseGet(() -> roleRepository.save(new Role(null, userRole)));
    }
  }
}
