package com.project.shopapp.services;

import com.project.shopapp.dtos.CategoryDTO;
import com.project.shopapp.dtos.ProductDTO;
import com.project.shopapp.dtos.ProductImageDTO;
import com.project.shopapp.exceptions.DataNotFoundException;
import com.project.shopapp.exceptions.InvalidParamException;
import com.project.shopapp.models.Category;
import com.project.shopapp.models.Product;
import com.project.shopapp.models.ProductImage;
import com.project.shopapp.repositories.CategoryRepository;
import com.project.shopapp.repositories.ProductImageRepository;
import com.project.shopapp.repositories.ProductRepository;
import com.project.shopapp.responses.ProductResponse;
import com.project.shopapp.services.impl.ICategoryService;
import com.project.shopapp.services.impl.IProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService implements IProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductImageRepository productImageRepository;

    //Tạo Sản phẩm
    @Override
    public Product createProduct(ProductDTO productDTO) throws DataNotFoundException {
        Category existtingCategory = categoryRepository.
                findById(productDTO.getCategoryId())
                .orElseThrow(() -> new DataNotFoundException(
                        "Cannot find category with id" + productDTO.getCategoryId()));

        Product newProduct = Product.builder()
                .name(productDTO.getName())
                .price(productDTO.getPrice())
                .thumbnail(productDTO.getThumbnail())
                .description(productDTO.getDescription())
                .category(existtingCategory)
                .build();
        return productRepository.save(newProduct) ;
    }

    //Lấy sản phảm bằng id
    @Override
    public Product getProductById(Long id) throws DataNotFoundException {
        return productRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Cannot find product with id " + id));
    }

    //Lấy tất cả sản phẩm
    @Override
    public Page<ProductResponse> getAllProduct(PageRequest pageRequest) {
        // Lấy danh sách sản phẩm theo trang(page) và giới hạn (limit)

        return productRepository.findAll(pageRequest).map(
                product -> ProductResponse.fromProduct(product));
    }

    //Cập nhập sản phẩm
    @Override
    public Product updateProduct(long id, ProductDTO productDTO) throws DataNotFoundException {
        Product existingProduct = getProductById(id);
        if(existingProduct!=null){
            // copy các thuộc tính dto -> Product
            Category existingCategory = categoryRepository.
                    findById(productDTO.getCategoryId())
                            .orElseThrow(() -> new DataNotFoundException(
                                    "Cannot find category with id: "+productDTO.getCategoryId()));

            existingProduct.setName(productDTO.getName());
            existingProduct.setCategory(existingCategory);
            existingProduct.setPrice(productDTO.getPrice());
            existingProduct.setDescription(productDTO.getDescription());
            existingProduct.setThumbnail(productDTO.getThumbnail());
            return productRepository.save(existingProduct);
        }
        return null;
    }

    // Xóa sản phẩm theo id
    @Override
    public void deleteProduct(long id) {
        Optional<Product> optionalProduct = productRepository.findById(id);
        if(optionalProduct.isPresent()){
            productRepository.delete(optionalProduct.get());
        }
    }

    // Kiểm tra tên Sản phẩm có tồn tại
    @Override
    public boolean existsByName(String name) {
        return productRepository.existsByName(name);
    }

    @Override
    public ProductImage createProductImage(
            Long productId,
            ProductImageDTO productImageDTO) throws Exception {
        Product existingProduct = productRepository
                .findById(productId)
                .orElseThrow(() -> new DataNotFoundException(
                   "Cannt find product with id: " + productImageDTO.getProductId()));

        ProductImage newProductImage = ProductImage.builder()
                .product(existingProduct)
                .imageUrl(productImageDTO.getUrlImage())
                .build();

        //Ko insert 5 ảnh cho 1 sản phẩm
        //Nó sẽ insert từng cái
        //Khi mà muốn lưu thì nó check trong db xem có file nào ko
        //Nếu ko nó sẽ lưu vào db và khi mà đủ 5 ảnh thì nó sẽ check lại xem có
        //Nếu đủ 5 sẽ ko lưu nữa
        int size = productImageRepository.findByProductId(productId).size();
        if(size >= ProductImage.MAXIMUM_IMAGES_PER_PRODUCT){
            throw new InvalidParamException("Number of images must be <=" + ProductImage.MAXIMUM_IMAGES_PER_PRODUCT);
        }

        return productImageRepository.save(newProductImage);
    }
}
