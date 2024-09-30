package com.ecommerce.application.service;

import com.ecommerce.application.exceptions.APIException;
import com.ecommerce.application.exceptions.ResourceNotFoundException;
import com.ecommerce.application.model.Cart;
import com.ecommerce.application.model.Category;
import com.ecommerce.application.model.Product;
import com.ecommerce.application.payload.CartDTO;
import com.ecommerce.application.payload.ProductDTO;
import com.ecommerce.application.payload.ProductResponse;
import com.ecommerce.application.repository.CartRepository;
import com.ecommerce.application.repository.CategoryRepository;
import com.ecommerce.application.repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;


@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;
    private final ProductResponse productResponse;
    private final FileService fileService;
    private final CartRepository cartRepository;
    private final CartService cartService;

    @Value("${project.image}")
    private String path;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository, CategoryRepository categoryRepository, ModelMapper modelMapper,
                              ProductResponse productResponse, FileService fileService, CartRepository cartRepository,
                              CartService cartService) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.modelMapper = modelMapper;
        this.productResponse = productResponse;
        this.fileService = fileService;
        this.cartRepository = cartRepository;
        this.cartService = cartService;
    }


    @Override
    public ProductDTO addProduct(ProductDTO productDTO, Long categoryId) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));
        Product product = modelMapper.map(productDTO, Product.class);
        Product existingProduct = productRepository.findByProductName(product.getProductName());
        if (existingProduct != null) {
            throw new APIException("Product " + productDTO.getProductName() + " already exists");
        }
        product.setCategory(category);
        product.setImageUrl("#####.png");
        double specialPrice = product.getProductPrice() - ((product.getDiscount() * 0.01) * product.getProductPrice());
        product.setProductSpecialPrice(specialPrice);
        Product savedProduct = productRepository.save(product);
        return modelMapper.map(savedProduct, ProductDTO.class);
    }

    @Override
    public ProductResponse getAllProducts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Product> productPage = productRepository.findAll(pageDetails);
        List<Product> products = productPage.getContent();
        if (products.isEmpty()) {
            throw new APIException("No products found");
        }
        return getProductResponse(productPage, products);
    }

    private ProductResponse getProductResponse(Page<Product> productPage, List<Product> products) {
        List<ProductDTO> productDTOS = products.stream().map(product -> modelMapper.map(product, ProductDTO.class)).toList();
        productResponse.setContent(productDTOS);
        productResponse.setPageNumber(productPage.getNumber());
        productResponse.setPageSize(productPage.getSize());
        productResponse.setTotalElements(productPage.getTotalElements());
        productResponse.setTotalPages(productPage.getTotalPages());
        productResponse.setLast(productPage.isLast());
        return productResponse;
    }

    @Override
    public ProductResponse getProductsByCategory(Long categoryId, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Product> productPage = productRepository.findByCategory(category, pageDetails);
        List<Product> products = productPage.getContent();
        if (products.isEmpty()) {
            throw new APIException("No products found with categoryId " + categoryId);
        }
        return getProductResponse(productPage, products);
    }

    @Override
    public ProductResponse getProductsByKeyword(String keyword, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Product> productPage = productRepository.findByProductNameLikeIgnoreCase('%' + keyword + '%', pageDetails);
        List<Product> products = productPage.getContent();
        if (products.isEmpty()) {
            throw new APIException("No products found with keyword " + keyword);
        }
        return getProductResponse(productPage, products);
    }

    @Transactional
    @Override
    public ProductDTO updateProduct(Long productId, ProductDTO productDTO) {
        Product product = modelMapper.map(productDTO, Product.class);
        Product existingProduct = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));
        if (product.getProductName() != null) {
            Product savedProduct = productRepository.findByProductName(product.getProductName());
            if (savedProduct != null && !savedProduct.getProductId().equals(productId)) {
                throw new APIException("Product " + productDTO.getProductName() + " already exists");
            }
            existingProduct.setProductName(product.getProductName());
        }
        if (product.getProductDescription() != null) {
            existingProduct.setProductDescription(product.getProductDescription());
        }
        if (product.getProductPrice() != null) {
            existingProduct.setProductPrice(product.getProductPrice());
        }
        existingProduct.setProductQuantity(product.getProductQuantity());
        if (product.getDiscount() != null) {
            existingProduct.setDiscount(product.getDiscount());
            double specialPrice = existingProduct.getProductPrice() -
                    ((product.getDiscount() * 0.01) * existingProduct.getProductPrice());
            existingProduct.setProductSpecialPrice(specialPrice);
        }
        Product updatedProduct = productRepository.save(existingProduct);

        List<Cart> carts = cartRepository.findCartsByProductId(productId);
        List<CartDTO> cartDTOs = carts.stream().map(cart -> {
            CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
            List<ProductDTO> products = cart.getCartItemList().stream().map(p -> modelMapper.map(p.getProduct(), ProductDTO.class)).toList();
            cartDTO.setProductDTOList(products);
            return cartDTO;
        }).toList();
        cartDTOs.forEach(cart -> cartService.updateProductsInCarts(cart.getCartId(), productId));
        return modelMapper.map(updatedProduct, ProductDTO.class);
    }

    @Transactional
    @Override
    public ProductDTO deleteProduct(Long productId) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));
        List<Cart> carts = cartRepository.findCartsByProductId(productId);
        carts.forEach(cart->cartService.deleteProductFromCart(cart.getCartId(), productId));
        productRepository.delete(product);
        return modelMapper.map(product, ProductDTO.class);
    }

    @Override
    public ProductDTO updateProductImage(Long productId, MultipartFile image) throws IOException {
        Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));
        String fileName = fileService.uploadImage(path, image);
        product.setImageUrl(fileName);
        Product saveProduct = productRepository.save(product);
        return modelMapper.map(saveProduct, ProductDTO.class);
    }


}
