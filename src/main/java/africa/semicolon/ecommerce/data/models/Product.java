package africa.semicolon.ecommerce.data.models;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(length = 300)
    private String description;

    @Column(nullable = false)
    private Double price;

    @Column(nullable = false)
    private int quantity;

    private String imageUrl;

    private Category category;
}
