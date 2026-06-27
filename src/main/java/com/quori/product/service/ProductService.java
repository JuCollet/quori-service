package com.quori.product.service;

import com.quori.common.NotFoundException;
import com.quori.event.service.EventService;
import com.quori.product.dto.CreateProductRequest;
import com.quori.product.dto.ProductResponse;
import com.quori.product.dto.UpdateProductRequest;
import com.quori.product.entity.Product;
import com.quori.product.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ProductService {

    private final ProductRepository repository;
    private final EventService eventService;

    public ProductService(ProductRepository repository, EventService eventService) {
        this.repository = repository;
        this.eventService = eventService;
    }

    @Transactional
    public ProductResponse create(UUID eventId, CreateProductRequest request) {
        var event = eventService.findOrThrow(eventId);
        var entity = new Product();
        entity.setEvent(event);
        entity.setName(request.name());
        entity.setDescription(request.description());
        entity.setPriceCents(request.priceCents());
        entity.setCategory(request.category());
        return ProductResponse.from(repository.save(entity));
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> listByEvent(UUID eventId) {
        return repository.findByEventId(eventId).stream()
                .map(ProductResponse::from)
                .toList();
    }

    @Transactional
    public ProductResponse update(UUID id, UpdateProductRequest request) {
        var entity = findOrThrow(id);
        entity.setName(request.name());
        entity.setDescription(request.description());
        entity.setPriceCents(request.priceCents());
        entity.setCategory(request.category());
        entity.setActive(request.active());
        return ProductResponse.from(repository.save(entity));
    }

    @Transactional
    public void delete(UUID id) {
        var entity = findOrThrow(id);
        entity.setActive(false);
        repository.save(entity);
    }

    public Product findOrThrow(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product", id));
    }
}
