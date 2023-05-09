package com.app.zcustom.service;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.supercsv.cellprocessor.ParseBigDecimal;
import org.supercsv.cellprocessor.ParseBool;
import org.supercsv.cellprocessor.constraint.StrMinMax;
import org.supercsv.cellprocessor.ift.CellProcessor;

import com.app.exception.CustomApplicationException;
import com.app.service.CsvService;
import com.app.zcustom.repository.ProductRepository;
import com.app.zcustom.repository.entity.Product;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ProductService {
	
	@Autowired
	private ProductRepository repository;
	
	@Autowired
	private CsvService csvService;
	
	private Set<String> productsIdsCache = new HashSet<String>();
	
	public Set<String> getproductsIdsCache() {
		return productsIdsCache;
	}
	
	@PostConstruct
	public void init() {
		productsIdsCache = new HashSet<>(repository.findAllIds());
		log.info("Product Ids successfully loaded, count:" + productsIdsCache.size());
	}
	
	public Page<Product> get(Specification<Product> spec, Integer pageNo, Integer pageSize, String sortBy, String sortDirection) {
        Pageable pageable = PageRequest.of(pageNo, pageSize, 
        		Sort.by((StringUtils.isBlank(sortDirection) || sortDirection.equalsIgnoreCase("asc")) 
        				? Direction.ASC : Direction.DESC, sortBy));
 
        return repository.findAll(spec, pageable);
	}
	
	public List<Product> get(Specification<Product> spec, String sortBy, String sortDirection) {
		Sort sort = Sort.by((StringUtils.isBlank(sortDirection) || sortDirection.equalsIgnoreCase("asc")) 
				? Direction.ASC : Direction.DESC, sortBy);
        return repository.findAll(spec, sort);
	}
	
	@Cacheable(value = "Product", key = "#id")
	public Product getById(String id) {
		return repository.findById(id).orElseThrow(() -> 
			new CustomApplicationException(HttpStatus.NOT_FOUND, "ENTITY_NOT_FOUND", "Entity not found"));
	}
	
	public Product create(Product request) {
		Optional<Product> checkOptionalEntity = repository.findById(request.getId());
		if (checkOptionalEntity.isPresent()) {
			throw new CustomApplicationException(HttpStatus.BAD_REQUEST, "ENTITY_ID_TAKEN", "Entity ID is taken");
		}
		log.info("Product ID is good to create:" + request.getId());
		Product product =  repository.save(request);
		productsIdsCache.add(product.getId());
		return product;
	}

	@CachePut(value = "Product", key = "#id")
	public Product update(String id, Product request) {
		getById(id);
		request.setId(id);
		return repository.save(request);
	}

	@CacheEvict(value = "Product", key = "#id")
	public void delete(String id) {
		getById(id);
		repository.deleteById(id);
		productsIdsCache.remove(id);
	}

	public void upload(MultipartFile file) {
		String[] headers = new String[] {"enabled", "id", "listPrice", "name"};
		final CellProcessor[] processors = new CellProcessor[] {
		        new ParseBool(), // enabled
		        new StrMinMax(1, 25), // id
		        new ParseBigDecimal(), // listPrice
		        new StrMinMax(3, 255), //name 
		    };
		List<Product> products = null;
		try {
			products = csvService.readCsvFile(file, Product.class, headers, processors);
		} catch (IOException e) {
			e.printStackTrace();
		}
		repository.saveAll(products);
	}

}
