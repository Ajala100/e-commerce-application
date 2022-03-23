package africa.semicolon.ecommerce.data.models;

import africa.semicolon.ecommerce.web.exceptions.OutOfStockException;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Product product;

    private int quantityAddedToCart;

    public Item(Product product, int quantityAddedToCart) throws OutOfStockException {
        if(quantityAddedToCart > product.getQuantity()) {
            throw new OutOfStockException("Product is out of stock!!");
        }
        this.quantityAddedToCart = quantityAddedToCart;
        this.product = product;
    }
}
