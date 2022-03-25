package africa.semicolon.ecommerce.data.repostories;

import africa.semicolon.ecommerce.data.models.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long> {
}
