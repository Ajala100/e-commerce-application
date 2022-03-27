package africa.semicolon.ecommerce.services.cart;

import africa.semicolon.ecommerce.data.dtos.CartItemRequestDto;
import africa.semicolon.ecommerce.data.dtos.CartItemResponseDto;
import africa.semicolon.ecommerce.data.dtos.CartUpdateRequestDto;
import africa.semicolon.ecommerce.data.dtos.CartUpdateResponseDto;
import africa.semicolon.ecommerce.data.models.AppUser;
import africa.semicolon.ecommerce.data.models.Cart;
import africa.semicolon.ecommerce.data.models.Item;
import africa.semicolon.ecommerce.data.models.Product;
import africa.semicolon.ecommerce.data.repostories.AppUSerRepository;
import africa.semicolon.ecommerce.data.repostories.CartRepository;
import africa.semicolon.ecommerce.data.repostories.ProductRepository;
import africa.semicolon.ecommerce.web.exceptions.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@Slf4j
public class CartServiceImpl implements CartService {

    @Autowired
    AppUSerRepository appUSerRepository;

    @Autowired
    CartRepository cartRepository;

    @Autowired
    ProductRepository productRepository;

    @Override
    public CartItemResponseDto viewCart(String userEmail) throws UserNotFoundException {

        //retrieve user from database
        Optional<AppUser> queryResult = appUSerRepository.findByEmail(userEmail);
        if (queryResult.isEmpty()) throw new UserNotFoundException("User Does Not Exist");
        AppUser targetUser = queryResult.get();

        //retrieve cart from app user
        Cart cart = targetUser.getCart();

        //return cart response dto
        return buildCartItemResponseDto(cart);
    }

    @Override
    public CartItemResponseDto increaseCartItemQuantity(CartItemRequestDto request) throws UserNotFoundException, ProductDoesNotExistException, OutOfStockException {

        //retrieve appUser
        Optional<AppUser> queryResult = appUSerRepository.findByEmail(request.getUserEmail());
        if (queryResult.isEmpty()) throw new UserNotFoundException("User Does Not Exist!!");
        AppUser targetUser = queryResult.get();

        //check that product exists
        Optional<Product> productQueryResult = productRepository.findById(request.getProductId());
        if (productQueryResult.isEmpty()) throw new ProductDoesNotExistException("Product does not exist");
        Product targetProduct = productQueryResult.get();

        //get cart
        Cart cart = targetUser.getCart();

        //increase cart item quantity
        for (Item itemInCart : cart.getItems()) {
            if (itemInCart.getProduct().getId() == (request.getProductId())) {
                if (targetProduct.getQuantity() > 0) {
                    itemInCart.setQuantityAddedToCart(itemInCart.getQuantityAddedToCart() + 1);
                    cart.setTotalPrice(cart.getTotalPrice() + targetProduct.getPrice());
                    targetProduct.setQuantity(targetProduct.getQuantity() - 1);
                    productRepository.save(targetProduct);
                    break;
                } else throw new OutOfStockException("Product is out of stock");

            } else throw new ProductDoesNotExistException("Product not in cart");
        }

        cartRepository.save(cart);
        return buildCartItemResponseDto(cart);
    }

    @Override
    public CartItemResponseDto decreaseCartItemQuantity(CartItemRequestDto requestDto) throws UserNotFoundException, ProductDoesNotExistException, BusinessLogicException {

        //check that product exists
        Optional<Product> productQuery = productRepository.findById(requestDto.getProductId());
        if (productQuery.isEmpty()) throw new ProductDoesNotExistException("Product Does Not Exist");
        Product targetProduct = productQuery.get();

        //retrieve user
        Optional<AppUser> queryResult = appUSerRepository.findByEmail(requestDto.getUserEmail());
        if (queryResult.isEmpty()) throw new UserNotFoundException("User Does Not exist");
        AppUser appUser = queryResult.get();

        //retrieve user's cart
        Cart userCart = appUser.getCart();

        //remove decrease target item quantity in cart
        for (Item item : userCart.getItems()) {
            if (item.getProduct().getId() == requestDto.getProductId()) {
                if (item.getQuantityAddedToCart() > 0) {
                    item.setQuantityAddedToCart(item.getQuantityAddedToCart() - 1);
                    userCart.setTotalPrice(userCart.getTotalPrice() - targetProduct.getPrice());
                    targetProduct.setQuantity(targetProduct.getQuantity() + 1);
                    productRepository.save(targetProduct);
                    break;
                } else throw new BusinessLogicException("Product has been removed from cart");
            } else throw new ProductDoesNotExistException("Product does not exist in cart");
        }

        Cart savedCart = cartRepository.save(userCart);
        return buildCartItemResponseDto(savedCart);
    }

    @Override
    public CartUpdateResponseDto addItemToCart(CartUpdateRequestDto request) throws UserNotFoundException, OutOfStockException, BusinessLogicException, ItemDoesNotExistException, ProductDoesNotExistException {
        //retrieve app user
        Optional<AppUser> queryResult = appUSerRepository.findByEmail(request.getAppUserEmail());
        if (queryResult.isEmpty()) throw new UserNotFoundException("User does not exist");
        AppUser targetUser = queryResult.get();

        //retrieve cart from user
        Cart cart = targetUser.getCart();

        //check that product exits
        Optional<Product> product = productRepository.findById(request.getProductId());
        if (product.isEmpty()) throw new ProductDoesNotExistException("Product does not exist");
        Product targetProduct = product.get();

        //Add item to retrieved cart
        Item newItem = new Item(targetProduct, request.getQuantityAddedToCart());

        //set product quantity to reflect amount of products left then save product
        targetProduct.setQuantity(targetProduct.getQuantity() - request.getQuantityAddedToCart());
        productRepository.save(targetProduct);

        //retrieve cart, add item and set cart total price to include price of added item
        cart.getItems().add(newItem);
        cart.setTotalPrice(totalAfterItemIsAddedToCart(request.getQuantityAddedToCart(), targetProduct.getPrice(), cart));

        Cart savedCart = cartRepository.save(cart);
        return buildCartUpdateResponseDto(savedCart);
    }

    @Override
    public CartUpdateResponseDto removeItemFromCart(CartUpdateRequestDto request) throws UserNotFoundException, BusinessLogicException, ProductDoesNotExistException, ItemDoesNotExistException {
        //retrieve app user
        Optional<AppUser> queryResult = appUSerRepository.findByEmail(request.getAppUserEmail());
        if (queryResult.isEmpty()) throw new UserNotFoundException("User does not exist");
        AppUser appUser = queryResult.get();

        //retrieve cart from user
        Cart cart = appUser.getCart();

        //check that product exits
        Optional<Product> product = productRepository.findById(request.getProductId());
        if (product.isEmpty()) throw new ProductDoesNotExistException("Product does not exist");
        Product targetProduct = product.get();

        //remove item from retrieved cart
        for (Item itemInCart : cart.getItems()) {
            if (itemInCart.getProduct().getId() == targetProduct.getId()) {
                cart.setTotalPrice(totalAfterItemIsRemovedFromCart(cart, targetProduct.getPrice(), itemInCart.getQuantityAddedToCart()));
                cart.getItems().remove(itemInCart);
                break;
            }else throw new ItemDoesNotExistException("Item not in cart!!");
        }

        Cart savedCart = cartRepository.save(cart);
        return buildCartUpdateResponseDto(savedCart);
    }

    private CartItemResponseDto buildCartItemResponseDto(Cart cart) {
        return CartItemResponseDto.builder()
                .cartItems(cart.getItems())
                .totalPrice(cart.getTotalPrice())
                .build();
    }

    private CartUpdateResponseDto buildCartUpdateResponseDto(Cart cart) {
        return CartUpdateResponseDto.builder()
                .totalPrice(cart.getTotalPrice())
                .items(cart.getItems())
                .build();
    }

    private double totalAfterItemIsAddedToCart(int quantityAdded, double price, Cart cart) {
        return cart.getTotalPrice() + (price * quantityAdded);
    }

    private double totalAfterItemIsRemovedFromCart(Cart cart, double productPrice, int quantityAddedToCart) {
        return cart.getTotalPrice() - (productPrice * quantityAddedToCart);
    }
}