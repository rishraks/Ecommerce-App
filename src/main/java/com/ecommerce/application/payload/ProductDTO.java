package com.ecommerce.application.payload;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {

    private Long productId;
    private String productName;
    private String productDescription;
    private String imageUrl;
    private Integer productQuantity;
    private double productPrice;
    private double productSpecialPrice;
    private double discount;

}
