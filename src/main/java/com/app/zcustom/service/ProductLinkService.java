package com.app.zcustom.service;

import java.io.IOException;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.supercsv.cellprocessor.ParseInt;
import org.supercsv.cellprocessor.ParseLong;
import org.supercsv.cellprocessor.constraint.StrMinMax;
import org.supercsv.cellprocessor.ift.CellProcessor;

import com.app.exception.CustomApplicationException;
import com.app.service.CsvService;
import com.app.zcustom.repository.ProductLinkRepository;
import com.app.zcustom.repository.entity.ProductLink;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ProductLinkService {
	
	@Autowired
	private ProductLinkRepository repository;
	
	@Autowired
	private StoreService storeService;
	
	@Autowired
	private ProductService productService;
	
	@Autowired
	private CsvService csvService;
	
	public Page<ProductLink> get(Specification<ProductLink> spec, Integer pageNo, Integer pageSize, String sortBy, String sortDirection) {
        Pageable pageable = PageRequest.of(pageNo, pageSize, 
        		Sort.by((StringUtils.isBlank(sortDirection) || sortDirection.equalsIgnoreCase("asc")) 
        				? Direction.ASC : Direction.DESC, sortBy));
 
        return repository.findAll(spec, pageable);
	}
	
	public List<ProductLink> get(Specification<ProductLink> spec, String sortBy, String sortDirection) {
		Sort sort = Sort.by((StringUtils.isBlank(sortDirection) || sortDirection.equalsIgnoreCase("asc")) 
				? Direction.ASC : Direction.DESC, sortBy);
        return repository.findAll(spec, sort);
	}
	
	public ProductLink getById(Long id) {
		return repository.findById(id).orElseThrow(() -> 
			new CustomApplicationException(HttpStatus.NOT_FOUND, "ENTITY_NOT_FOUND", "Entity not found"));
	}
	
	public ProductLink create(ProductLink request) {
		log.info("ProductLink ID is good to create:" + request.getId());
		storeService.getById(request.getStoreId());
		if (!productService.getproductsIdsCache().contains(request.getProductId())) {
			throw new CustomApplicationException(HttpStatus.NOT_FOUND, "ENTITY_NOT_FOUND", "Product not found");
		}
		return repository.save(request);
	}

	public ProductLink update(Long id, ProductLink request) {
		getById(id);
		storeService.getById(request.getStoreId());
		if (!productService.getproductsIdsCache().contains(request.getProductId())) {
			throw new CustomApplicationException(HttpStatus.NOT_FOUND, "ENTITY_NOT_FOUND", "Product not found");
		}
		request.setId(id);
		return repository.save(request);
	}

	public void delete(Long id) {
		getById(id);
		repository.deleteById(id);
	}

	public void upload(MultipartFile file) {
		String[] headers = new String[] {"enabled", "id", "inventory", "listPrice", "productId", "salePrice", "storeId", "vendorProductId"};
		final CellProcessor[] processors = new CellProcessor[] {
		        new ParseBool(), // enabled
		        new org.supercsv.cellprocessor.Optional(new ParseLong()), // id
		        new org.supercsv.cellprocessor.Optional(new ParseInt()), // Inventory
		        new org.supercsv.cellprocessor.Optional(new ParseBigDecimal()), // listPrice
		        new StrMinMax(1, 50), // productId
		        new org.supercsv.cellprocessor.Optional(new ParseBigDecimal()), // salePrice
		        new StrMinMax(1, 50), //storeId
		        new StrMinMax(1, 50), // vendorProductId
		    };
		List<ProductLink> products = null;
		try {
			products = csvService.readCsvFile(file, ProductLink.class, headers, processors);
		} catch (IOException e) {
			e.printStackTrace();
		}
		for (ProductLink product : products) {
			storeService.getById(product.getStoreId());
			if (!productService.getproductsIdsCache().contains(product.getProductId())) {
				throw new CustomApplicationException(HttpStatus.NOT_FOUND, "ENTITY_NOT_FOUND", 
						"Product with ID " + product.getProductId() + " not found");
			}
		}
		repository.saveAll(products);
	}

	public void saveAll(List<ProductLink> productLinks) {
		repository.saveAll(productLinks);
	}

	public ProductLink getByStoreIdAndVendorProductId(String storeId, String vendorProductId) {
		return repository.findByStoreIdAndVendorProductId(storeId, vendorProductId);
	}

}
