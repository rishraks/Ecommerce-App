package com.ecommerce.application.service;


import com.ecommerce.application.payload.CartDTO;
import jakarta.transaction.Transactional;

import java.util.List;

public interface CartService {

    CartDTO addProductToCart(Long productId, Integer quantity);

    List<CartDTO> getAllCarts();

    CartDTO getUserCart(String emailId, Long cartId);

    @Transactional
    CartDTO updateCartProductQuantity(Long productID, Integer delete);

    String deleteProductFromCart(Long cartId, Long productId);

    void updateProductsInCarts(Long cartId, Long productId);
}
