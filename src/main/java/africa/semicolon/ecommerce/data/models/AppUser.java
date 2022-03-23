package africa.semicolon.ecommerce.data.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
public class AppUser {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String firstName;
    private String lastName;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    private final Cart cart;

    @Column(length = 500)
    private String address;

    @ElementCollection
    private List<Authority> authorities;

    public AppUser(){
        this.cart = new Cart();
    }
}
