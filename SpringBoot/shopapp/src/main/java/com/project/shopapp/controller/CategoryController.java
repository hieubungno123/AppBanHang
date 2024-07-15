package com.project.shopapp.controller;

import com.project.shopapp.dtos.CategoryDTO;
import com.project.shopapp.models.Category;
import com.project.shopapp.services.CategoryService;
import com.project.shopapp.services.impl.ICategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/categories")
// Dependency Injection
@RequiredArgsConstructor
public class CategoryController {
    private final ICategoryService categoryService;

    @PostMapping("")
//    Nếu tham số truyền vào là 1 object -> Data Transfer Object = Request Object
    public ResponseEntity<?> createCategories(
            @Valid @RequestBody CategoryDTO categoryDTO,
            BindingResult result
    ){
        if (result.hasErrors()){
            List<String> errorMessages = result.getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();
            return ResponseEntity.badRequest().body(errorMessages);
        }

        categoryService.createCategory(categoryDTO);
        return ResponseEntity.ok("Insert category successfully");
    }

    @GetMapping("")
    public ResponseEntity<List<Category>> getAllCategories(
            @RequestParam("page")   int page,
            @RequestParam("limit")  int limit
//  @RequestParam("page") : Biến ta gửi trong trình duyệt
    ){
        List<Category> categories =  categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }



    @PutMapping("/{id}")
    public ResponseEntity<String> updateCategories(
            @PathVariable Long id,
            @RequestBody CategoryDTO categoryDTO
    ){
        categoryService.updateCategory(id,categoryDTO);
        return ResponseEntity.ok("Update category successfully!");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCategories(@PathVariable Long id){
        categoryService.deleteCategory(id);
        return ResponseEntity.ok("Delete category successfully!");
    }
}
