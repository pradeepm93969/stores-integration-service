package com.app.zcustom.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.zcustom.repository.entity.Store;

@Service
public class StoreSyncService {
	
	@Autowired
	private StoreService storeService;
	
	@Autowired
	private LumensoftService lumensoftService;
	
	public void fetchPriceFromVendor(@Valid List<String> storeIds) {
		List<Store> stores = storeService.getStoresByIds(storeIds);
		Map<String, List<Store>> storesMap = new HashMap<>();
		for (Store store : stores) {
			if (!storesMap.containsKey(store.getVendorName())) {
				storesMap.put(store.getVendorName(), new ArrayList<>());
			}
			storesMap.get(store.getVendorName()).add(store);
		}
		for (Map.Entry<String, List<Store>> entry : storesMap.entrySet()) {
			if (entry.getKey().equals(LumensoftService.VENDOR_LUMENSOFT)) {
				lumensoftService.fetchPriceFromLumensoft(entry.getValue());
			}
		}
	}

	public void fetchInventoryFromVendor(@Valid List<String> storeIds) {
		List<Store> stores = storeService.getStoresByIds(storeIds);
		Map<String, List<Store>> storesMap = new HashMap<>();
		for (Store store : stores) {
			if (!storesMap.containsKey(store.getVendorName())) {
				storesMap.put(store.getVendorName(), new ArrayList<>());
			}
			storesMap.get(store.getVendorName()).add(store);
		}
		for (Map.Entry<String, List<Store>> entry : storesMap.entrySet()) {
			if (entry.getKey().equals(LumensoftService.VENDOR_LUMENSOFT)) {
				lumensoftService.fetchInventoryFromLumensoft(entry.getValue());
			}
		}
		
	}

}
