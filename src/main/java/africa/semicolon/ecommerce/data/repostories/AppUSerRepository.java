package africa.semicolon.ecommerce.data.repostories;

import africa.semicolon.ecommerce.data.models.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AppUSerRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByEmail(String email);
}
