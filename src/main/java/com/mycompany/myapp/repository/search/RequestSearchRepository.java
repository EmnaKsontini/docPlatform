package com.mycompany.myapp.repository.search;

import com.mycompany.myapp.domain.Request;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the Request entity.
 */
public interface RequestSearchRepository extends ElasticsearchRepository<Request, Long> {
}
