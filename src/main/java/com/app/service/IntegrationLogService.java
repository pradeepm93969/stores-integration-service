package com.app.service;

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

import com.app.exception.CustomApplicationException;
import com.app.repository.IntegrationLogRepository;
import com.app.repository.entity.IntegrationLog;

@Service
public class IntegrationLogService {
	
	@Autowired
	private IntegrationLogRepository repository;
	
	//private Queue<IntegrationLog> integrationLogQueue = new ArrayDeque<>();

	public Page<IntegrationLog> get(Specification<IntegrationLog> spec, Integer pageNo, Integer pageSize, String sortBy, String sortDirection) {
		 Pageable pageable = PageRequest.of(pageNo, pageSize, 
	        		Sort.by((StringUtils.isBlank(sortDirection) || sortDirection.equalsIgnoreCase("asc")) 
	        				? Direction.ASC : Direction.DESC, sortBy));
	 
	    return repository.findAll(spec, pageable);
	}

	public IntegrationLog getById(Long id) {
		return repository.findById(id).orElseThrow(() -> 
			new CustomApplicationException(HttpStatus.NOT_FOUND, "ENTITY_NOT_FOUND", "Entity not found"));
	}

	public void save(IntegrationLog integrationLog) {
		repository.save(integrationLog);
		//integrationLogQueue.add(integrationLog);
	}
	
	/**@Scheduled(cron="0 * * * * *")
	public void writeToDatabase() {
		List<IntegrationLog> logsList = new ArrayList<IntegrationLog>();
		try {
			if (!integrationLogQueue.isEmpty()) {
				int size = integrationLogQueue.size();
				while (size > 0) {
					logsList.add(integrationLogQueue.poll());
					size--;
				}
				repository.saveAll(logsList);
			}
		} catch (Exception ex) {
			log.error("Failed to to save the integration Log to DB:" + ex.getLocalizedMessage());
			integrationLogQueue.addAll(logsList);
		}
	}**/

}
