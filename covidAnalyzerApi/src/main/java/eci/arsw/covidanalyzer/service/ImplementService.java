package eci.arsw.covidanalyzer.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import eci.arsw.covidanalyzer.model.Result;
import eci.arsw.covidanalyzer.model.ResultType;

@Component("ImplementService")
public class ImplementService implements ICovidAggregateService {

    private List<Result> results = new ArrayList<>();

    @Override
    public void aggregateResult(Result result, ResultType type) {
        for(Result rs: results){
            if(rs.getId().equals(result.getId())){
                result.sumCount();
                result.setResultType(type);
            }
        }
    }

    @Override
    public List<Result> getResult(ResultType type) {
        List<Result> newResults = new ArrayList<>();
        for(Result result: results){
            if(result.getResultType().equals(type));
            newResults.add(result);
        }
        return newResults;
        /* // TODO Auto-generated method stub
        return false; */
    }

    @Override
    public void upsertPersonWithMultipleTests(UUID id, ResultType type) {
        // TODO Auto-generated method stub
        for(Result result: results){
            if(id.equals(result.getId())){
                result.sumCount();
                result.setResultType(type);
            }
        }
    }
    
}
