package com.ecommerce.application.service;

import com.ecommerce.application.exceptions.APIException;
import com.ecommerce.application.exceptions.ResourceNotFoundException;
import com.ecommerce.application.model.Cart;
import com.ecommerce.application.model.CartItem;
import com.ecommerce.application.model.Product;
import com.ecommerce.application.payload.CartDTO;
import com.ecommerce.application.payload.ProductDTO;
import com.ecommerce.application.repository.CartItemRepository;
import com.ecommerce.application.repository.CartRepository;
import com.ecommerce.application.repository.ProductRepository;
import com.ecommerce.application.util.AuthenticationUtil;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Service
@Transactional
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final AuthenticationUtil authUtil;
    private final ProductRepository productRepository;
    private final CartItemRepository cartItemRepository;
    private final ModelMapper modelMapper;

    public CartServiceImpl(CartRepository cartRepository, AuthenticationUtil authUtil, ProductRepository productRepository, CartItemRepository cartItemRepository, ModelMapper modelMapper) {
        this.cartRepository = cartRepository;
        this.authUtil = authUtil;
        this.productRepository = productRepository;
        this.cartItemRepository = cartItemRepository;
        this.modelMapper = modelMapper;
    }


    @Override
    public CartDTO addProductToCart(Long productId, Integer quantity) {
        // Find existing cart or create one
        Cart cart = createCart();
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        // Check if the product already exists in the cart
        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cart.getCartId(), productId);
        if (cartItem != null) {
            throw new APIException("Product " + product.getProductName() + " already exists");
        }

        // Check product availability
        if (product.getProductQuantity() == 0) {
            throw new APIException("Product " + product.getProductName() + " is not available!");
        }
        if (product.getProductQuantity() < quantity) {
            throw new APIException("Please, make an order of the " + product.getProductName() + " less than or equal to the quantity " + product.getProductQuantity() + ".");
        }

        // Create new cart item
        CartItem newCartItem = new CartItem();
        newCartItem.setCart(cart);
        newCartItem.setProduct(product);
        newCartItem.setQuantity(quantity);
        newCartItem.setProductPrice(product.getProductSpecialPrice());
        newCartItem.setDiscount(product.getDiscount());

        // Add new CartItem to Cart's CartItemList
        cart.getCartItemList().add(newCartItem);

        // Save new cart item
        cartItemRepository.save(newCartItem);

        // Update product quantity and cart total price
        product.setProductQuantity(product.getProductQuantity());
        cart.setTotalPrice(cart.getTotalPrice() + (product.getProductSpecialPrice() * quantity));

        // Save updated cart
        cartRepository.save(cart);

        // Map to CartDTO and ProductDTO
        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
        List<CartItem> cartItemList = cart.getCartItemList();
        List<ProductDTO> productDTOList = cartItemList.stream().map(items -> {
            ProductDTO productDTO = modelMapper.map(items.getProduct(), ProductDTO.class);
            productDTO.setProductQuantity(items.getQuantity());
            return productDTO;
        }).toList();

        cartDTO.setProductDTOList(productDTOList);

        return cartDTO;
    }

    private Cart createCart() {
        Cart userCart = cartRepository.findCartByEmail(authUtil.loggedInEmail());
        if (userCart != null) {
            return userCart;
        }
        Cart cart = new Cart();
        cart.setTotalPrice(0.00);
        cart.setUser(authUtil.loggedInUser());
        return cartRepository.save(cart);
    }


    @Override
    public List<CartDTO> getAllCarts() {
        List<Cart> carts = cartRepository.findAll();
        if (carts.isEmpty()) {
            throw new APIException("No cart exists");
        }
        List<CartDTO> cartDTOList = carts.stream().map(cart -> {
            CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);

            List<ProductDTO> products = cart.getCartItemList().stream().map(product -> {
                ProductDTO productDTO = modelMapper.map(product, ProductDTO.class);
                productDTO.setProductQuantity(product.getQuantity());
                return productDTO;
            }).toList();
            cartDTO.setProductDTOList(products);
            return cartDTO;
        }).toList();
        return cartDTOList;
    }

    @Override
    public CartDTO getUserCart(String emailId, Long cartId) {
        Cart cart = cartRepository.findCartByEmailAndCartId(emailId, cartId);
        if (cart == null) {
            throw new ResourceNotFoundException("Cart", "cartId", cartId);
        }
        cart.getCartItemList().forEach(c -> c.getProduct().setProductQuantity(c.getQuantity()));
        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
        List<ProductDTO> products = cart.getCartItemList().stream().map(product -> modelMapper.map(product.getProduct(), ProductDTO.class)).toList();
        cartDTO.setProductDTOList(products);
        return cartDTO;
    }

    @Transactional
    @Override
    public CartDTO updateCartProductQuantity(Long productID, Integer quantity) {
        String emailId = authUtil.loggedInEmail();
        Cart userCart = cartRepository.findCartByEmail(emailId);
        Long cartId = userCart.getCartId();

        Cart cart = cartRepository.findById(cartId).orElseThrow(() -> new ResourceNotFoundException("Cart", "cartId", cartId));
        Product product = productRepository.findById(productID).orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productID));

        if (product.getProductQuantity() == 0) {
            throw new APIException("Product " + product.getProductName() + " is not available!");
        }
        if (product.getProductQuantity() < quantity) {
            throw new APIException("Please, make an order of the " + product.getProductName() + " less than or equal to the quantity " + product.getProductQuantity() + ".");
        }

        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cartId, productID);
        if (cartItem == null) {
            throw new APIException("Product " + product.getProductName() + " is not available!");
        }
        int netQuantity = cartItem.getQuantity() + quantity;
        if (netQuantity == 0) {
            deleteProductFromCart(cartId, productID);
        } else {
            cartItem.setProductPrice(product.getProductSpecialPrice());
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
            cartItem.setDiscount(product.getDiscount());
            cart.setTotalPrice(cart.getTotalPrice() + (cartItem.getProductPrice() * quantity));
            cartRepository.save(cart);
        }
        CartItem updatedCartItem = cartItemRepository.save(cartItem);

        if (updatedCartItem.getQuantity() == 0) {
            cartItemRepository.deleteById(updatedCartItem.getCartItemId());
        }

        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
        List<CartItem> cartItems = cart.getCartItemList();
        Stream<ProductDTO> productDTOStream = cartItems.stream().map(item -> {
            ProductDTO productDTO = modelMapper.map(item.getProduct(), ProductDTO.class);
            productDTO.setProductQuantity(item.getQuantity());
            return productDTO;
        });

        cartDTO.setProductDTOList(productDTOStream.toList());
        return cartDTO;
    }

    @Transactional
    @Override
    public String deleteProductFromCart(Long cartId, Long productId) {
        Cart cart = cartRepository.findById(cartId).orElseThrow(() -> new ResourceNotFoundException("Cart", "cartId", cartId));
        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cartId, productId);
        if (cartItem == null) {
            throw new APIException("Product " + productId + " is not available!");
        }
        cart.setTotalPrice(cart.getTotalPrice() - (cartItem.getProductPrice() * cartItem.getQuantity()));
        cartItemRepository.deleteCartItemByCartIdAndProductId(cartId, productId);
        if (cart.getCartItemList().isEmpty()) {
            cart.setTotalPrice(0.00); // Or any additional logic for empty cart
        }
        cartRepository.save(cart);
        return "Product has been deleted!";
    }

    @Transactional
    @Override
    public void updateProductsInCarts(Long cartId, Long productId) {
        Cart cart = cartRepository.findById(cartId).orElseThrow(() -> new ResourceNotFoundException("Cart", "cartId", cartId));
        Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));
        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cartId, productId);
        if (cartItem == null) {
            throw new APIException("Product " + productId + " is not available!");
        }
        double cartPrice = cart.getTotalPrice() - (cartItem.getProductPrice() * cartItem.getQuantity());
        cartItem.setProductPrice(product.getProductSpecialPrice());
        cart.setTotalPrice(cartPrice + (cartItem.getProductPrice() * cartItem.getQuantity()));
        cartItemRepository.save(cartItem);
    }

}
