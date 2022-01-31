package com.onurgundogdu.springelasticsearch.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onurgundogdu.springelasticsearch.document.Vehicle;
import com.onurgundogdu.springelasticsearch.helper.Indices;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.rest.RestStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VehicleService {
    private static final ObjectMapper MAPPER=new ObjectMapper();
    private static final Logger LOG= LoggerFactory.getLogger(VehicleService.class);

    private final RestHighLevelClient levelClient;

    @Autowired
    public VehicleService(RestHighLevelClient levelClient){
        this.levelClient=levelClient;
    }

    public Boolean index(final Vehicle vehicle){
        try
        {
            final String vehicleString=MAPPER.writeValueAsString(vehicle);
            final IndexRequest indexRequest=new IndexRequest(Indices.VEHICLE_INDEX);
            indexRequest.id(vehicle.getId());
            indexRequest.source(vehicleString, XContentType.JSON);

            final IndexResponse indexResponse=levelClient.index(indexRequest, RequestOptions.DEFAULT);

            return indexResponse !=null&&indexResponse.status().equals(RestStatus.OK);
        }
        catch (final Exception e){
            LOG.error(e.getMessage(),e);
            return false;
        }
    }

    public Vehicle getById(final String vehicleId){
        try
        {
            final GetResponse documentFields=levelClient.get(new GetRequest(Indices.VEHICLE_INDEX,vehicleId),
                    RequestOptions.DEFAULT);
            if (documentFields==null || documentFields.isSourceEmpty()){
                return null;
            }
            return  MAPPER.readValue(documentFields.getSourceAsString(),Vehicle.class);
        }
        catch (final Exception e){
            LOG.error(e.getMessage(),e);
            return null;
        }
    }

}
