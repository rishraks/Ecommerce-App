package com.ecommerce.application.controller;


import com.ecommerce.application.model.Cart;
import com.ecommerce.application.payload.CartDTO;
import com.ecommerce.application.repository.CartRepository;
import com.ecommerce.application.service.CartService;
import com.ecommerce.application.util.AuthenticationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CartController {

    @Autowired
    private CartService cartService;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private AuthenticationUtil authUtil;


    @PostMapping("/carts/products/{productId}/quantity/{quantity}")
    public ResponseEntity<CartDTO> addProductToCart(@PathVariable Long productId, @PathVariable Integer quantity) {
        return new ResponseEntity<>(cartService.addProductToCart(productId, quantity), HttpStatus.CREATED);
    }


    @GetMapping("/carts")
    public ResponseEntity<List<CartDTO>> getCarts() {
        List<CartDTO> cartDTOList = cartService.getAllCarts();
        return new ResponseEntity<>(cartDTOList, HttpStatus.FOUND);
    }


    @GetMapping("/carts/user/cart")
    public ResponseEntity<CartDTO> getUserCart() {
        String emailId = authUtil.loggedInEmail();
        Cart cart = cartRepository.findCartByEmail(emailId);
        Long cartId = cart.getCartId();
        CartDTO cartDTO = cartService.getUserCart(emailId, cartId);
        return new ResponseEntity<>(cartDTO, HttpStatus.OK);
    }


    @PutMapping("/cart/products/{productID}/quantity/{operations}")
    public ResponseEntity<CartDTO> updateCartProductQuantity(@PathVariable Long productID, @PathVariable String operations) {
        CartDTO cartDTO=cartService.updateCartProductQuantity(productID,operations.equalsIgnoreCase("delete")?-1:1);
        return new ResponseEntity<>(cartDTO, HttpStatus.OK);
    }


    @DeleteMapping("/carts/{cartId}/product/{productId}")
    public ResponseEntity<String> deleteProductFromCart(@PathVariable Long cartId, @PathVariable Long productId) {
        String status=cartService.deleteProductFromCart(cartId,productId);
        return new ResponseEntity<>(status, HttpStatus.OK);
    }
}
