package com.onurgundogdu.springelasticsearch.repository;

import com.onurgundogdu.springelasticsearch.document.Person;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface PersonRepository extends ElasticsearchRepository<Person,String> {
}
