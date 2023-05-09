package com.app.zcustom.service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.app.repository.entity.SchedulerConfiguration;
import com.app.util.ApplicationUtil;
import com.app.zcustom.integration.MagentoRestService;
import com.app.zcustom.modal.request.magento.CompanyCatalogItem;
import com.app.zcustom.modal.request.magento.MagentoUpdatePriceInventoryRequest;
import com.app.zcustom.repository.ProductLinkRepository;
import com.app.zcustom.repository.entity.ProductLink;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MagentoService {
	
	@Autowired
	private ProductLinkRepository repository;
	
	@Autowired
	private MagentoRestService magentoRestService;
	
	public void updatePriceInventoryToMagento(SchedulerConfiguration cg) {
		long count = repository.countByProcessedAndRetryCountLessThanAndNextRunAtBefore(
				false, cg.getRetryCount(), OffsetDateTime.now());
		
		if (count > 0 && cg.getBatchSize() > 0) {
			long noOfPages = (count % cg.getBatchSize() > 0) ? (count / cg.getBatchSize()) + 1 : count / cg.getBatchSize();
			if (noOfPages > 0) {
				processRecords(noOfPages, cg);
			}
		}
	}
	
	private void processRecords(long noOfPages, SchedulerConfiguration cg) {
		//noOfPages = 1;
		AtomicInteger pageNo = new AtomicInteger(0);
		ExecutorService executorService = Executors.newFixedThreadPool(cg.getThreadCount());
		OffsetDateTime currentTime = OffsetDateTime.now();
		CountDownLatch latch = new CountDownLatch((int) noOfPages);
		String token = magentoRestService.getToken();
		log.info("Magento Token:" + token);
		if (StringUtils.isBlank(token)) {
			return;
		}
		for (int i = 0; i < noOfPages; i++) {
			executorService.submit(() -> {
				try {
					int actualPageNo = pageNo.getAndAdd(1);
					Pageable pageable = PageRequest.of(actualPageNo, cg.getBatchSize(), Sort.by(Direction.ASC, "id"));
					
					Page<ProductLink> productLinkPage = repository.findByProcessedAndRetryCountLessThanAndNextRunAtBefore(
							false, cg.getRetryCount(), currentTime, pageable);
					
					MagentoUpdatePriceInventoryRequest request = null;
					for (ProductLink productLink : productLinkPage.getContent()) {
						
						request = new MagentoUpdatePriceInventoryRequest();
						CompanyCatalogItem item = new CompanyCatalogItem();
						item.setCompanyId(Integer.parseInt(productLink.getStoreId()));
						item.setPrice(productLink.getListPrice() == null ? new BigDecimal(9999999) : productLink.getListPrice());
						item.setSpecialPrice(productLink.getSalePrice());
						item.setSku(productLink.getProductId());
						item.setQuantity(productLink.getInventory() == null ? 0 : productLink.getInventory());
						item.setStatus(item.getQuantity() == 0 ? 0 : 1);
						request.getCompanyCatalogItems().add(item);
						
						try {
							magentoRestService.updatePriceAndInventory(request, token);
							productLink.setProcessed(true);
							productLink.setRetryCount(0);
							productLink.setApiError(null);
						} catch (Exception e) {
							productLink.setProcessed(false);
							productLink.setNextRunAt(ApplicationUtil.getNextRunTime(cg, productLink.getRetryCount()));
							productLink.setRetryCount(ApplicationUtil.getNextRetryCount(cg, productLink.getRetryCount()));
							productLink.setApiError("" + e.getLocalizedMessage().substring(0, Math.min(e.getLocalizedMessage().length(),2000)));
						}
					}
					repository.saveAll(productLinkPage.getContent());
				} catch(Exception e) {
					log.error(e.getLocalizedMessage());
				} finally {
					latch.countDown();
				}
			});
		}
		try {
			latch.await();
		} catch (InterruptedException e) {
			log.error(e.getLocalizedMessage());
		}
		executorService.shutdownNow(); 
	}

}
