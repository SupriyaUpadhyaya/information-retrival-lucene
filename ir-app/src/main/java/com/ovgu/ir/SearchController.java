package com.ovgu.ir;

import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.ovgu.ir.Clustering.*;
import static com.ovgu.ir.Indexing.*;

@RestController
public class SearchController{
    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/search")
    public JSONArray search(@RequestParam(value = "x", defaultValue = "5"
    ) int x, @RequestParam(value="q",required = true) String q) throws Exception {
        JSONArray response = getCluster(q,x);
        return response;
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/indexing")
    public String indexer(@RequestParam(value = "ip", defaultValue = "/Users/supriyaupadhyaya/ovgu/Info-Retrival/ir-app/src/main/resources/output"
    ) String inputPath, @RequestParam(value="op",defaultValue = "/Users/supriyaupadhyaya/ovgu/Info-Retrival/ir-app/src/main/resources/corpus") String outputPath) throws Exception {
        indexing(inputPath, outputPath);
        return "SUCCESS";
    }
}
