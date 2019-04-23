package com.mycompany.myapp.repository.search;

import com.mycompany.myapp.domain.Tip;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the Tip entity.
 */
public interface TipSearchRepository extends ElasticsearchRepository<Tip, Long> {
}
