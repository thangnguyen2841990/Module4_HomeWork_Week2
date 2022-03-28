package com.codegym.controller;

import com.codegym.model.Category;
import com.codegym.model.Product;
import com.codegym.model.ProductForm;
import com.codegym.service.category.ICategoryService;
import com.codegym.service.product.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

@Controller
@RequestMapping("/products")
public class ProductController {
    @Autowired
    private IProductService productService;
    @Autowired
    private ICategoryService categoryService;
    @Value("C:/Users/nguye/OneDrive/Desktop/image/")
    private String fileUpload;

    @ModelAttribute(name = "categories")
    private Page<Category> categories(Pageable pageable) {
        return this.categoryService.findAll(pageable);
    }

    @GetMapping
    private ModelAndView showAllProducts(Optional<String> name, @PageableDefault(value = 10) Pageable pageable) {
        Page<Product> products;
        if (!name.isPresent()) {
            ModelAndView modelAndView = new ModelAndView("/product/list");
             products = this.productService.findAll(pageable);
            modelAndView.addObject("products", products);
            return modelAndView;


        } else {
            ModelAndView modelAndView = new ModelAndView("/product/list");
             products = this.productService.findByName(name.get(), pageable);
            modelAndView.addObject("products", products);
            modelAndView.addObject("name", name.get());
            return modelAndView;

        }


    }

    @GetMapping("/create")
    private ModelAndView showCreateForm() {
        ProductForm productForm = new ProductForm();
        ModelAndView modelAndView = new ModelAndView("/product/create");
        modelAndView.addObject("productForm", productForm);
        return modelAndView;
    }

    @PostMapping("/create")
    private ModelAndView createProduct(@Validated @ModelAttribute ProductForm productForm, BindingResult bindindResult) {
        if (bindindResult.hasFieldErrors()) {
            ModelAndView modelAndView = new ModelAndView("/product/create");
            modelAndView.addObject("productForm", productForm);
            return modelAndView;
        }
        MultipartFile imageFile = productForm.getImage();
        String fileName = imageFile.getOriginalFilename();
        long currentTime = System.currentTimeMillis();
        fileName = currentTime + fileName;
        try {
            FileCopyUtils.copy(imageFile.getBytes(), new File(fileUpload + fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Product newProduct = new Product(productForm.getName(), productForm.getPrice(), productForm.getQuantity(),
                productForm.getDescription(), fileName, productForm.getCategory());
        this.productService.save(newProduct);
        ModelAndView modelAndView = new ModelAndView("redirect:/products");
        return modelAndView;
    }

    @GetMapping("/edit/{id}")
    private ModelAndView showEditForm(@PathVariable Long id) {
        Optional<Product> product = this.productService.findById(id);
        if (!product.isPresent()) {
            ModelAndView modelAndView = new ModelAndView("/product/error-404");
            return modelAndView;
        }
        ModelAndView modelAndView = new ModelAndView("/product/edit");
        modelAndView.addObject("product", product.get());
        return modelAndView;
    }

    @PostMapping("/edit/{id}")
    private ModelAndView editProduct(@PathVariable Long id, @ModelAttribute ProductForm productForm) {
        Optional<Product> product = this.productService.findById(id);
        if (!product.isPresent()) {
            ModelAndView modelAndView = new ModelAndView("/product/error-404");
            return modelAndView;
        }
        Product oldProduct = product.get();
        String image;
        MultipartFile imageFile = productForm.getImage();
        if (imageFile == null) {
            image = oldProduct.getImage();
        } else {
            String fileName = imageFile.getOriginalFilename();
            long currentTime = System.currentTimeMillis();
            fileName = currentTime + fileName;
            image = fileName;
            try {
                FileCopyUtils.copy(imageFile.getBytes(), new File(fileUpload + fileName));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Product newProduct = new Product(productForm.getId(), productForm.getName(), productForm.getPrice(),
                productForm.getQuantity(), productForm.getDescription(), image, productForm.getCategory());
        this.productService.save(newProduct);
        ModelAndView modelAndView = new ModelAndView("redirect:/products");
        return modelAndView;
    }

    @GetMapping("/delete/{id}")
    private ModelAndView showDeleteForm(@PathVariable Long id) {
        Optional<Product> product = this.productService.findById(id);
        if (!product.isPresent()) {
            ModelAndView modelAndView = new ModelAndView("/product/error-404");
            return modelAndView;
        }
        ModelAndView modelAndView = new ModelAndView("/product/delete");
        modelAndView.addObject("product", product.get());
        return modelAndView;
    }

    @PostMapping("/delete/{id}")
    private ModelAndView deleteProduct(@PathVariable Long id) {
        Optional<Product> product = this.productService.findById(id);
        if (!product.isPresent()) {
            ModelAndView modelAndView = new ModelAndView("/product/error-404");
            return modelAndView;
        }
        Product oldProduct = product.get();
        File file = new File(fileUpload + oldProduct.getImage());
        if (file.exists()) {
            file.delete();
        }
        this.productService.deleteById(id);
        ModelAndView modelAndView = new ModelAndView("redirect:/products");
        return modelAndView;
    }

    @GetMapping("/view/{id}")
    private ModelAndView showProductDetails(@PathVariable Long id) {

        Optional<Product> product = this.productService.findById(id);
        if (!product.isPresent()) {
            ModelAndView modelAndView = new ModelAndView("/product/error-404");
            return modelAndView;
        }
        ModelAndView modelAndView = new ModelAndView("/product/view");
        modelAndView.addObject("product", product.get());
        return modelAndView;
    }

    @GetMapping("/search")
    private ModelAndView findProductByCategory(Long categoryId, Optional<Integer> page) {
        Pageable pageable = PageRequest.of(page.orElse(0), 10);
        Page<Product> products = this.productService.findByCategory(categoryId, pageable);
        ModelAndView modelAndView = new ModelAndView("/product/listSearch1");
        modelAndView.addObject("products", products);
        return modelAndView;

    }


}
