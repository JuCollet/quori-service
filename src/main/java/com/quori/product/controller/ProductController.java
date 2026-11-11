package com.quori.product.controller;

import com.quori.product.dto.CreateProductRequest;
import com.quori.product.dto.ProductResponse;
import com.quori.product.dto.UpdateProductRequest;
import com.quori.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Products")
public class ProductController {

    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    @PostMapping("/events/{eventId}/products")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ORGANIZER') and @authz.isEventOrganizationOwner(principal, #eventId)")
    @Operation(summary = "Create a product for an event")
    public ProductResponse create(@PathVariable UUID eventId,
                                  @Valid @RequestBody CreateProductRequest request) {
        return service.create(eventId, request);
    }

    @GetMapping("/events/{eventId}/products")
    @Operation(summary = "List products for an event")
    public List<ProductResponse> listByEvent(@PathVariable UUID eventId) {
        return service.listByEvent(eventId);
    }

    @PutMapping("/products/{id}")
    @PreAuthorize("hasRole('ORGANIZER') and @authz.isProductOrganizationOwner(principal, #id)")
    @Operation(summary = "Update a product")
    public ProductResponse update(@PathVariable UUID id,
                                  @Valid @RequestBody UpdateProductRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/products/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ORGANIZER') and @authz.isProductOrganizationOwner(principal, #id)")
    @Operation(summary = "Soft-delete a product")
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}
