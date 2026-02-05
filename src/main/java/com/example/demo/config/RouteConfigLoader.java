package com.example.demo.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Data
public class RouteConfigLoader {

    private Map<String, String> routeProcedureMap;
    private Map<String, String> routePackageMap;
    private Map<String, List<String>> routeParamsMap;

    @PostConstruct
    public void loadRoutes() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        ClassPathResource resource = new ClassPathResource("routes.json");
        List<Map<String, Object>> routes = mapper.readValue(resource.getInputStream(), new TypeReference<>() {
        });

        // Convert List to Map: route -> procedure
        routeProcedureMap = routes.stream()
                .collect(Collectors.toMap(m -> (String) m.get("route"), m -> (String) m.get("procedure")));

        // Convert List to Map: route -> package (if exists)
        routePackageMap = routes.stream()
                .filter(m -> m.containsKey("package"))
                .collect(Collectors.toMap(m -> (String) m.get("route"), m -> (String) m.get("package")));

        // Convert List to Map: route -> parameters (List<String>)
        routeParamsMap = routes.stream()
                .filter(m -> m.containsKey("parameters"))
                .collect(Collectors.toMap(
                        m -> (String) m.get("route"),
                        m -> (List<String>) m.get("parameters")));

        System.out.println("Loaded " + routeProcedureMap.size() + " routes from config.");
    }

    public String getProcedureForRoute(String route) {
        return routeProcedureMap.get(route);
    }

    public String getPackageForRoute(String route) {
        return routePackageMap.get(route);
    }

    public List<String> getParamsForRoute(String route) {
        return routeParamsMap.get(route);
    }
}
