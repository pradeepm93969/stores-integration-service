package com.app.zcustom.service;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.repository.entity.SchedulerConfiguration;
import com.app.service.SchedulerConfigurationService;
import com.app.zcustom.integration.LumensoftRestService;
import com.app.zcustom.modal.response.lumensoft.Datum;
import com.app.zcustom.modal.response.lumensoft.InventoryData;
import com.app.zcustom.modal.response.lumensoft.InventoryResponse;
import com.app.zcustom.modal.response.lumensoft.ProductsResponse;
import com.app.zcustom.modal.response.lumensoft.Variant;
import com.app.zcustom.repository.entity.ProductLink;
import com.app.zcustom.repository.entity.Store;
import com.app.zcustom.scheduler.LumensoftInboundPriceScheduler;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class LumensoftService {
	
	public static final String VENDOR_LUMENSOFT = "LUMENSOFT";
	
	@Autowired
	private StoreService storeService;
	
	@Autowired
	private ProductLinkService productLinkService;
	
	@Autowired
	private LumensoftRestService lumensoftRestService;
	
	@Autowired
	private SchedulerConfigurationService schedulerConfigurationService;
	
	public void fetchPriceFromLumensoft(SchedulerConfiguration cg) {
		List<Store> stores = storeService.getStoresByVendorNameForProductPriceSync(
        		VENDOR_LUMENSOFT, true, true);
		fetchPriceFromLumensoft(cg, stores);
	}
	
	public void fetchPriceFromLumensoft(List<Store> stores) {
		SchedulerConfiguration cg = schedulerConfigurationService.getById(
				LumensoftInboundPriceScheduler.SCHEDULER_NAME);
		fetchPriceFromLumensoft(cg, stores);
	}
	
	public void fetchPriceFromLumensoft(SchedulerConfiguration cg, List<Store> stores) {
		log.info("Lumensoft Price Sync Scheduler started:" + OffsetDateTime.now());
        
		AtomicInteger offset = new AtomicInteger(0);
		ExecutorService executorService = Executors.newFixedThreadPool(cg.getThreadCount());
		CountDownLatch latch = new CountDownLatch(stores.size());
		for (int i = 0; i < stores.size(); i++) {
			executorService.submit(() -> {
				Store actualStore = null;
				int productsCount = 0;
				int productsOffset = 0;
				ProductsResponse response = null;
				try {
					actualStore = stores.get(offset.getAndAdd(1));
					storeService.productPriceSyncStart(actualStore);
					List<ProductLink> productLinks = new ArrayList<>();
					ProductLink productLink = null;
					do {
						productsCount = 0;
						productLinks = new ArrayList<>();
						
						//Call Lumensoft
						response = lumensoftRestService.getProductsV2Response(actualStore, productsOffset);
						productsOffset = productsOffset + cg.getBatchSize();
						productsCount = response.getData() != null ? response.getData().size() : 0;
						
						//Update Repository
						for (Datum product : response.getData()) {
							for (Variant variant : product.getVariants()) {
								productLink = productLinkService.getByStoreIdAndVendorProductId(actualStore.getId(), variant.getProductCode());
								if (productLink != null) {
									productLink.setListPrice(variant.getProductPrice());
									productLink.setProcessed(false);
									productLink.setRetryCount(0);
									productLinks.add(productLink);
								}
							}
						}
						productLinkService.saveAll(productLinks);
					} while (productsCount > 0);
					
					storeService.productPriceSyncFinished(actualStore);
				} catch(Exception e) {
					log.error(e.getLocalizedMessage());
					storeService.productPriceSyncFailed(actualStore);
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
       
        log.info("Lumensoft Price Sync Scheduler Ended:" + OffsetDateTime.now());
    }
	
	public void fetchInventoryFromLumensoft(SchedulerConfiguration cg) {
		List<Store> stores = storeService.getStoresByVendorNameForInventorySync(
        		VENDOR_LUMENSOFT, true, true);
		fetchInventoryFromLumensoft(cg, stores);
	}
	
	public void fetchInventoryFromLumensoft(List<Store> stores) {
		SchedulerConfiguration cg = schedulerConfigurationService.getById(
				LumensoftInboundPriceScheduler.SCHEDULER_NAME);
		fetchInventoryFromLumensoft(cg, stores);
	}
	
	public void fetchInventoryFromLumensoft(SchedulerConfiguration cg, List<Store> stores) {
		log.info("Lumensoft Inventory Sync Scheduler started:" + OffsetDateTime.now());
	       
		AtomicInteger offset = new AtomicInteger(0);
		ExecutorService executorService = Executors.newFixedThreadPool(cg.getThreadCount());
		CountDownLatch latch = new CountDownLatch(stores.size());
		for (int i = 0; i < stores.size(); i++) {
			executorService.submit(() -> {
				Store actualStore = null;
				InventoryResponse response = null;
				try {
					actualStore = stores.get(offset.getAndAdd(1));
					storeService.inventorySyncStart(actualStore);
					List<ProductLink> productLinks = new ArrayList<>();
					ProductLink productLink = null;
					
					//Call Lumensoft
					response = lumensoftRestService.getInventoryResponse(actualStore);
					
					//Update Repository
					for (InventoryData inventory : response.getData()) {
						productLink = productLinkService.getByStoreIdAndVendorProductId(
								actualStore.getId(), inventory.getProductCode());
						if (productLink != null) {
							productLink.setInventory((int) (inventory.getQuantity() - inventory.getHoldQuantity()));
							productLink.setProcessed(false);
							productLink.setRetryCount(0);
							productLinks.add(productLink);
						}
					}
					productLinkService.saveAll(productLinks);
					
					storeService.inventorySyncFinished(actualStore);
				} catch(Exception e) {
					log.error(e.getLocalizedMessage());
					storeService.inventorySyncFailed(actualStore);
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
       
        log.info("Lumensoft Inventory Sync Scheduler Ended:" + OffsetDateTime.now());
    }


}
