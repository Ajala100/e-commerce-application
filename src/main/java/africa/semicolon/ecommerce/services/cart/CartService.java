package africa.semicolon.ecommerce.services.cart;

import africa.semicolon.ecommerce.data.dtos.CartItemRequestDto;
import africa.semicolon.ecommerce.data.dtos.CartItemResponseDto;
import africa.semicolon.ecommerce.data.dtos.CartUpdateRequestDto;
import africa.semicolon.ecommerce.data.dtos.CartUpdateResponseDto;
import africa.semicolon.ecommerce.web.exceptions.*;

public interface CartService {
    CartItemResponseDto viewCart(String userEmail) throws UserNotFoundException;
    CartItemResponseDto increaseCartItemQuantity(CartItemRequestDto request) throws UserNotFoundException, BusinessLogicException, ProductDoesNotExistException, OutOfStockException;
    CartItemResponseDto decreaseCartItemQuantity(CartItemRequestDto requestDto) throws UserNotFoundException, ProductDoesNotExistException, BusinessLogicException;
    CartUpdateResponseDto addItemToCart(CartUpdateRequestDto request) throws UserNotFoundException, OutOfStockException, BusinessLogicException, ItemDoesNotExistException, ProductDoesNotExistException;
    CartUpdateResponseDto removeItemFromCart(CartUpdateRequestDto request) throws UserNotFoundException, BusinessLogicException, ProductDoesNotExistException, ItemDoesNotExistException;
}
