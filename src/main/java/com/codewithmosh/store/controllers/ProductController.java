package com.codewithmosh.store.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.codewithmosh.store.Dtos.ProductDto;
import com.codewithmosh.store.Dtos.RegisterProductRequest;
import com.codewithmosh.store.Mappers.ProductMapper;
import com.codewithmosh.store.entities.Category;
import com.codewithmosh.store.entities.Product;
import com.codewithmosh.store.repositories.CategoryRepository;
import com.codewithmosh.store.repositories.ProductRepository;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/products")
public class ProductController {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final CategoryRepository categoryRepository;
    @GetMapping
    public List<ProductDto> getAllProducts(@RequestParam(name = "categoryId" ,required = false) Byte CategoryId){
        List<Product> products;
        if(CategoryId !=null){
            products = productRepository.findByCategoryId(CategoryId);
        }else{
            products = productRepository.findAllWithCategory();
        }
        return products
                .stream()
                .map(p->productMapper.toDto(p))
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getById(@PathVariable Long id){
        var product =  productRepository.findById(id).orElse(null);
        if(product == null){
            return ResponseEntity.notFound().build();
        }
        var userDto = new ProductDto(product.getId(),product.getName(),product.getDescription(),product.getPrice(),product.getCategory().getId());
        return ResponseEntity.ok(userDto);
    }

    @PostMapping()
    public ResponseEntity<ProductDto> createProduct(@RequestBody RegisterProductRequest request){
        Product product = new Product();
        Category cate = categoryRepository.findById(request.getCategoryId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));
        cate.setId(request.getCategoryId());
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setCategory(cate);

        Product savedPro = productRepository.save(product);
        ProductDto dto = new ProductDto(savedPro.getId(), savedPro.getName(), savedPro.getDescription(), savedPro.getPrice(), savedPro.getCategory().getId());
        return ResponseEntity.ok(dto);
        
    }
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateProduct(@PathVariable(name = "id") Long id ,@RequestBody ProductDto request){
        var product = productRepository.findById(id).orElseThrow();
        if(product == null){
            return ResponseEntity.notFound().build();
        }
        // product.setId(request.getId());
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());

        Category cate = categoryRepository.findById(request.getCategoryId()).orElseThrow();
        cate.setId(request.getCategoryId());
        product.setCategory(cate);
        productRepository.save(product);
        return ResponseEntity.ok().build();

    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable(name = "id") Long id){
        var user = productRepository.findById(id).orElseThrow();
        if(user == null){
            return ResponseEntity.notFound().build();
        }
       
        productRepository.delete(user);
        return ResponseEntity.noContent().build();
    }
}
