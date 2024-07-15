package com.project.shopapp.controller;


import com.github.javafaker.Faker;
import com.project.shopapp.dtos.ProductDTO;
import com.project.shopapp.dtos.ProductImageDTO;
import com.project.shopapp.exceptions.DataNotFoundException;
import com.project.shopapp.models.Product;
import com.project.shopapp.models.ProductImage;
import com.project.shopapp.responses.ProductListResponse;
import com.project.shopapp.responses.ProductResponse;
import com.project.shopapp.services.impl.IProductService;
import jakarta.validation.Path;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/products")
public class ProductController {
    private final IProductService productService;

    @GetMapping("")
    public ResponseEntity<ProductListResponse> getProducts(
            @RequestParam("page")   int page,
            @RequestParam("limit")  int limit
    ){
        //Tạo Pagetable từ thông tin trang và giới hạn
        PageRequest pageRequest = PageRequest.of(
                page,limit,
                Sort.by("createAt").descending());
        Page<ProductResponse> productPage = productService.getAllProduct(pageRequest);
        // Lấy tổng số trang
        int totalPages = productPage.getTotalPages();
        List<ProductResponse> products = productPage.getContent();
        return ResponseEntity.ok(ProductListResponse.builder()
                        .products(products)
                        .totalPages(totalPages)
                .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable("id") Long productId) {
        try {
            Product product = productService.getProductById(productId);
            return ResponseEntity.ok(ProductResponse.fromProduct(product));

        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("")
    public ResponseEntity<?> createProduct(
            @Valid @RequestBody ProductDTO productDTO,
            BindingResult result
            ){
        try {
            if (result.hasErrors()){
                List<String> errorMessages = result.getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();
                return ResponseEntity.badRequest().body(errorMessages);
            }

            Product newProduct = productService.createProduct(productDTO);

            return ResponseEntity.ok(newProduct);
        }catch (Exception e){
            return  ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping(value = "uploads/{id}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadImages(
            @PathVariable("id") Long productId,
            @ModelAttribute("files") List<MultipartFile> files){

        try {
            Product existingProduct = productService.getProductById(productId);

            files = files == null ? new ArrayList<MultipartFile>() : files; // Để khi ko tích ô file thì nó sẽ tự tạo file
            if(files.size() > ProductImage.MAXIMUM_IMAGES_PER_PRODUCT){
                return ResponseEntity.badRequest().body("You cant only upload maximum 5 images.");
            }
            // mới để không bị ảnh hưởng nội dung khi tạo
            // Nếu ko có nó sẽ nhảy sang exception
            List<ProductImage> productImages = new ArrayList<>();
            for (MultipartFile file : files){
                //   Kiểm tra kích thước file và định dạng

                if (file.getSize() == 0){  // Có tích file nhưng ko add ảnh thì sẽ ko nhảy vào phần "UNSUPPORTED_MEDIA_TYPE"
                    continue;
                }

                if (file.getSize() > 10 * 1024 * 1024){ // Kích thước > 10MB
                    return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                            .body("File is too large, Maximum size is 10MB");
                }
                // Kiểm tra đây có phải file ảnh ko
                String contentType = file.getContentType();
                if (contentType == null || !contentType.startsWith("image/")){
                    return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                            .body("File must be an image");
                }
                // Lưu file và cập nhập thumbnail trong DTO
                String filename = storeFile(file); // Thay thế hàm này với code của bạn để lưu file
                // Lưu vào đối tường Product trong BD
                ProductImage productImage = productService.createProductImage(
                        existingProduct.getId(),
                        ProductImageDTO.builder()
                                .urlImage(filename)
                                .build());
                // Lưu vào bảng product_images
                productImages.add(productImage);
            }
            return ResponseEntity.ok().body(productImages);
        }catch (Exception e){
            return  ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    private String storeFile(MultipartFile file) throws IOException {
        String filename = StringUtils.cleanPath(file.getOriginalFilename());

        //Thêm UUID vào trước tên file để đảm bảo tên file là duy nhất
        String uniqueFilename = UUID.randomUUID().toString() + "_" + filename;

        //Đường dẫn đến thư mục mà bạn muốn lưu file
        java.nio.file.Path uploadDir = Paths.get("uploads");

        // Kiểm tra và tạo thư mục nếu nó ko tồn tại
        if (!Files.exists(uploadDir)){
            Files.createDirectories(uploadDir);
        }

        //Đường dẫn đầy đủ đến file
        java.nio.file.Path description = Paths.get(uploadDir.toString(), uniqueFilename);

        //Sao chép file vào thư mục đích
        Files.copy(file.getInputStream(), description, StandardCopyOption.REPLACE_EXISTING);

        return uniqueFilename;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long id){
        try {
            productService.deleteProduct(id);

        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }

        return ResponseEntity.ok(String.format("Product with id=%d deleted successfully", id));
    }

    @PostMapping("/generateFakeProducts")
    public ResponseEntity<String> generateFakeProducts(){
        Faker faker = new Faker();
        for (int i = 0; i < 100; i++) {
            String productName = faker.commerce().productName();
            if(productService.existsByName(productName)){
                continue;
            }
            ProductDTO productDTO = ProductDTO.builder()
                    .name(productName)
                    .price((float)faker.number().numberBetween(10, 90000000))
                    .description(faker.lorem().sentence())
                    .thumbnail("")
                    .categoryId((long)faker.number().numberBetween(2,5))
                    .build();
            try {
                productService.createProduct(productDTO);
            }catch (Exception e){
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        }
        return ResponseEntity.ok("Fake products created successfully");

    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(
            @PathVariable long id,
            @RequestBody ProductDTO productDTO
    ){
        try {
            Product updateProduct = productService.updateProduct(id,productDTO);
            return ResponseEntity.ok(updateProduct);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
