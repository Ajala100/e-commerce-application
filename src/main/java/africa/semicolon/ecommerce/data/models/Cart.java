package africa.semicolon.ecommerce.data.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<Item> items = new ArrayList<>();

    private Double totalPrice = 0.0;
//
//    public void addItemToCart(Item item){
//        items.add(item);
//    }
//
//    public List<Item> removeItemFromCart(int index){
//        items.remove(index);
//        return  items;
//    }
//
//    public List<Item> removeItemFromCart(Item item){
//        for(Item cartItem : items){
//            if(cartItem.getId().equals(item.getId())) items.remove(cartItem);
//            return items;
//        }
//        return null;
//    }

}
