package com.project.shopapp.services.impl;

import com.project.shopapp.dtos.ProductDTO;
import com.project.shopapp.dtos.ProductImageDTO;
import com.project.shopapp.exceptions.DataNotFoundException;
import com.project.shopapp.models.Product;
import com.project.shopapp.models.ProductImage;
import com.project.shopapp.responses.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface IProductService {

    public Product createProduct(ProductDTO productDTO) throws DataNotFoundException;

    public Product getProductById(Long id) throws DataNotFoundException;

    Page<ProductResponse> getAllProduct(PageRequest pageRequest);

    public Product updateProduct(long id, ProductDTO productDTO) throws DataNotFoundException;

    void deleteProduct(long id);

    boolean existsByName(String name);

    //
    public ProductImage createProductImage(
            Long productId,
            ProductImageDTO productImageDTO) throws Exception;
}
