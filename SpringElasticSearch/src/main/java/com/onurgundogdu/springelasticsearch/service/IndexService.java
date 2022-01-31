package com.onurgundogdu.springelasticsearch.service;

import com.onurgundogdu.springelasticsearch.helper.Indices;
import com.onurgundogdu.springelasticsearch.helper.Util;
import org.apache.juli.logging.Log;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
public class IndexService {

    private static final Logger LOG= LoggerFactory.getLogger(IndexService.class);

    private final List<String> INDICES_TO_CREATE=List.of(Indices.VEHICLE_INDEX);
    private final RestHighLevelClient levelClient;


 @Autowired
    public IndexService(RestHighLevelClient levelClient){
     this.levelClient=levelClient;
 }
 @PostConstruct
 public void tryToCreateIndices(){
     final String settings=Util.loadString("static/settings.json");

 for(final String indexName:INDICES_TO_CREATE)
 {
     try {
         boolean indexExists=levelClient.indices().exists(new GetIndexRequest(indexName), RequestOptions.DEFAULT);
         if(indexExists){
             continue;
         }
         final String mappings=Util.loadString("static/mapping/"+indexName+".json");
         if(settings == null || mappings==null){
             LOG.error("Filed to create index with name'{}'",indexName);
             continue;
         }
         final CreateIndexRequest indexRequest=new CreateIndexRequest(indexName);
         indexRequest.settings(settings, XContentType.JSON);
         indexRequest.mapping(mappings,XContentType.JSON);

         levelClient.indices().create(indexRequest,RequestOptions.DEFAULT);
     }
     catch (final Exception e){
         LOG.error(e.getMessage(),e);
     }
 }
 }
}
