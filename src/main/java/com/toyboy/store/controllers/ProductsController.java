package com.toyboy.store.controllers;

import com.toyboy.store.models.Product;
import com.toyboy.store.models.ProductDto;
import com.toyboy.store.repository.ProductsRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/products")
public class ProductsController {

    @Autowired
    private ProductsRepository productsRepository;

    public ProductsController(ProductsRepository productRepository) {
        this.productsRepository = productRepository;
    }

    @GetMapping({"", "/"})
    public String showProductList(Model model) {
        List<Product> products = productsRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
        model.addAttribute("products", products);
        return "products/index";
    }

    @GetMapping("/create")
    public String showCreatePage(Model model) {
        ProductDto productDto = new ProductDto();
        model.addAttribute("productDto", productDto);
        return "products/CreateProduct";
    }

    @PostMapping("/create")
    public String createProduct(@Valid @ModelAttribute ProductDto productDto, BindingResult bindingResult) {

        if (productDto.getImageFile() == null || productDto.getImageFile().isEmpty()) {
            bindingResult.addError(new FieldError("productDto", "imageFile", "Image file is required"));
        }

        if (productDto.getImageFile().getSize() > 2 * 1024 * 1024) {
            bindingResult.addError(new FieldError("productDto", "imageFile", "Image file size must be less than 2MB"));
        }

        if (bindingResult.hasErrors()) {
            return "products/CreateProduct";
        }

        MultipartFile image = productDto.getImageFile();
        Date createdAt = new Date();
        String storageFileName = createdAt.getTime() + "-" + image.getOriginalFilename();
        try {
            String uploadDir = "public/images/";
            Path uploadPath = Paths.get(uploadDir);

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Save the file
            Path filePath = uploadPath.resolve(storageFileName);
            try (InputStream inputStream = image.getInputStream()) {
                Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
            }

            System.out.println("File saved successfully at: " + filePath.toString());
        } catch (Exception ex) {
            System.out.println("Exception: " + ex.getMessage());
            return "products/CreateProduct";
        }

        Product product = new Product();
        product.setName(productDto.getName());
        product.setBrand(productDto.getBrand());
        product.setCategory(productDto.getCategory());
        product.setPrice(productDto.getPrice());
        product.setDescription(productDto.getDescription());
        product.setImageFileName(storageFileName);
        product.setCreated_at(createdAt);

        productsRepository.save(product);

        return "redirect:/products";
    }

    @GetMapping("/edit")
    public String showEditPage(Model model, @RequestParam int id) {
        try {
            Product product = productsRepository.findById(id).get();
            model.addAttribute("product", product);

            ProductDto productDto = new ProductDto();
            productDto.setName(product.getName());
            productDto.setBrand(product.getBrand());
            productDto.setCategory(product.getCategory());
            productDto.setPrice(product.getPrice());
            productDto.setDescription(product.getDescription());

            model.addAttribute("productDto", productDto);
        } catch (Exception ex) {
            System.out.println("Exception: " + ex.getMessage());
            return "redirect:/products";
        }
        return "products/EditProduct";
    }

    @PostMapping("/edit")
    public String editProduct(Model model, @RequestParam int id, @Valid @ModelAttribute ProductDto productDto, BindingResult bindingResult) {

        try {
            Product product = productsRepository.findById(id).get();
            model.addAttribute("product", product);

            if (bindingResult.hasErrors()) {
                return "products/EditProduct";
            }

            if (!productDto.getImageFile().isEmpty()) {
                // delete old image
                String uploadDir = "public/images/";
                Path uploadPath = Paths.get(uploadDir + product.getImageFileName());

                try {
                    Files.delete(uploadPath);
                } catch (IOException e) {
                    System.out.println("Exception: " + e.getMessage());
                }

                // upload new image
                MultipartFile image = productDto.getImageFile();
                Date createdAt = new Date();
                String storageFileName = createdAt.getTime() + "-" + image.getOriginalFilename();

                try (InputStream inputStream = image.getInputStream()) {
                    Files.copy(inputStream, Paths.get(uploadDir + storageFileName), StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    System.out.println("Exception: " + e.getMessage());
                }

                product.setImageFileName(storageFileName);
            }

            product.setName(productDto.getName());
            product.setBrand(productDto.getBrand());
            product.setCategory(productDto.getCategory());
            product.setPrice(productDto.getPrice());
            product.setDescription(productDto.getDescription());

            productsRepository.save(product);
        } catch (Exception ex) {
            System.out.println("Exception: " + ex.getMessage());
            return "redirect:/products";
        }
        return "redirect:/products";
    }

    @GetMapping("/delete")
    public String deleteProduct(@RequestParam int id) {
        try {
            Product product = productsRepository.findById(id).get();
            Path imagePath = Paths.get("public/images/" + product.getImageFileName());
            try {
                Files.delete(imagePath);
            } catch (IOException e) {
                System.out.println("Exception: " + e.getMessage());
            }
            productsRepository.delete(product);
        } catch (Exception ex) {
            System.out.println("Exception: " + ex.getMessage());
        }
        return "redirect:/products";
    }
}
