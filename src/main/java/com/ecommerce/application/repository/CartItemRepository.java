package com.ecommerce.application.repository;

import com.ecommerce.application.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    @Query("SELECT ci FROM CartItem  ci WHERE ci.cart.cartId=?1 AND ci.product.productId=?2")
    CartItem findCartItemByProductIdAndCartId(Long cartId, Long productId);

    @Modifying
    @Query("DELETE FROM CartItem ci WHERE ci.cart.cartId=?1 AND ci.product.productId=?2")
    void deleteCartItemByCartIdAndProductId(Long cartId, Long productId);
}
