package com.example.demo.controller;

import com.example.demo.config.RouteConfigLoader;
import com.example.demo.service.DynamicDataService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/rpc")
public class DynamicController {

    private final RouteConfigLoader routeConfigLoader;
    private final DynamicDataService dynamicDataService;

    public DynamicController(RouteConfigLoader routeConfigLoader, DynamicDataService dynamicDataService) {
        this.routeConfigLoader = routeConfigLoader;
        this.dynamicDataService = dynamicDataService;
    }

    @GetMapping("/{routeKey}")
    public ResponseEntity<?> handleDynamicRequest(
            @PathVariable String routeKey,
            @RequestParam Map<String, Object> allParams) {

        String procedureName = routeConfigLoader.getProcedureForRoute(routeKey);

        if (procedureName == null) {
            return ResponseEntity.notFound().build();
        }

        String packageName = routeConfigLoader.getPackageForRoute(routeKey);

        // Ensure expected parameters are present (even if null) for proper mapping
        List<String> expectedParams = routeConfigLoader.getParamsForRoute(routeKey);
        if (expectedParams != null) {
            for (String param : expectedParams) {
                if (!allParams.containsKey(param)) {
                    allParams.put(param, null);
                }
            }
        }

        System.out.println("Routing " + routeKey + " -> Package: " + packageName + ", Procedure: " + procedureName
                + " with params: " + allParams);

        try {
            // Appel au service générique via JDBC pour Oracle
            List<Map<String, Object>> result = dynamicDataService.executeProcedure(procedureName, packageName,
                    allParams);
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error executing procedure: " + e.getMessage());
        }
    }
}
