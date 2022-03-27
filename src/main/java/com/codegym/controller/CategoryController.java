package com.codegym.controller;

import com.codegym.model.Category;
import com.codegym.model.Product;
import com.codegym.service.category.ICategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Optional;

@Controller
@RequestMapping("/categories")
public class CategoryController {
    @Autowired
    ICategoryService categoryService;

    @GetMapping
    private ModelAndView showAllCategories(Pageable pageable) {
            Page<Category> categories = this.categoryService.findAll(pageable);
            ModelAndView modelAndView = new ModelAndView("/category/list");
            modelAndView.addObject("categories",categories);
            return modelAndView;
    }
    @GetMapping("/create")
    private ModelAndView showCreateForm(){
        ModelAndView modelAndView = new ModelAndView("/category/create");
        modelAndView.addObject("category",new Category());
        return modelAndView;
    }
    @PostMapping("/create")
    private ModelAndView createCategory(@ModelAttribute Category category){
        Category newCategory = new Category(category.getName());
        this.categoryService.save(newCategory);
        ModelAndView modelAndView = new ModelAndView("redirect:/categories");
        return modelAndView;
    }
    @GetMapping("/edit/{id}")
    private ModelAndView showEditForm(@PathVariable Long id){
        Optional<Category> category = this.categoryService.findById(id);
        if (!category.isPresent()){
            ModelAndView modelAndView = new ModelAndView("/category/error-404");
            return modelAndView;
        }
        ModelAndView modelAndView = new ModelAndView("/category/edit");
        modelAndView.addObject("category",category.get());
        return modelAndView;
    }
    @PostMapping("/edit/{id}")
    private ModelAndView editCategory(@PathVariable Long id, @ModelAttribute Category category){
        Category newCategory = new Category(category.getId(), category.getName());
        this.categoryService.save(newCategory);
        ModelAndView modelAndView = new ModelAndView("redirect:/categories");
        return modelAndView;
    }
    @GetMapping("/delete/{id}")
    private ModelAndView showDeleteForm(@PathVariable Long id) {
        Optional<Category> category = this.categoryService.findById(id);
        if (!category.isPresent()) {
            ModelAndView modelAndView = new ModelAndView("/category/error-404");
            return modelAndView;
        }
        ModelAndView modelAndView = new ModelAndView("/category/delete");
        modelAndView.addObject("category", category.get());
        return modelAndView;
    }
    @PostMapping("/delete/{id}")
    private ModelAndView deleteCategory(@PathVariable Long id){
        this.categoryService.deleteById(id);
        ModelAndView modelAndView = new ModelAndView("redirect:/categories");
        return modelAndView;
    }
    @GetMapping("/view/{id}")
    private ModelAndView showCategoryDetaild(@PathVariable Long id) {
        Optional<Category> category = this.categoryService.findById(id);
        if (!category.isPresent()) {
            ModelAndView modelAndView = new ModelAndView("/category/error-404");
            return modelAndView;
        }
        ModelAndView modelAndView = new ModelAndView("/category/view");
        modelAndView.addObject("category", category.get());
        return modelAndView;
    }
}

