package com.mycompany.myapp.repository.search;

import com.mycompany.myapp.domain.Appointment;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the Appointment entity.
 */
public interface AppointmentSearchRepository extends ElasticsearchRepository<Appointment, Long> {
}
