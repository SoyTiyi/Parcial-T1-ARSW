package eci.arsw.covidanalyzer.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import eci.arsw.covidanalyzer.model.Result;
import eci.arsw.covidanalyzer.model.ResultType;

public class ImplementService implements ICovidAggregateService {

    private List<Result> results = new ArrayList<>();

    @Override
    public void aggregateResult(Result result, ResultType type) {
        
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

    }
    
}
