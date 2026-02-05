package com.example.demo.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Service;

import java.sql.Types;
import java.util.List;
import java.util.Map;

@Service
public class DynamicDataService {

    private final JdbcTemplate jdbcTemplate;

    public DynamicDataService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Map<String, Object>> executeProcedure(String procedureName, String packageName,
            Map<String, Object> params) {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName(procedureName);

        if (packageName != null && !packageName.isBlank()) {
            jdbcCall.withCatalogName(packageName);
            // Convention: Output cursor named "p_cursor"
            jdbcCall.declareParameters(new SqlOutParameter("p_cursor", Types.REF_CURSOR));
        }

        Map<String, Object> result = jdbcCall.execute(params);

        List<Map<String, Object>> rawList = List.of(result);

        // Extraction intelligente: on cherche la première liste de résultats (le
        // curseur)
        for (Object val : result.values()) {
            if (val instanceof List) {
                rawList = (List<Map<String, Object>>) val;
                break;
            }
        }

        // Normalisation des clés en minuscule (Oracle retourne du majuscule par défaut)
        return rawList.stream().map(row -> {
            Map<String, Object> lowerRow = new java.util.HashMap<>();
            row.forEach((k, v) -> lowerRow.put(k.toLowerCase(), v));
            return lowerRow;
        }).collect(java.util.stream.Collectors.toList());
    }

    // Version simplifiée pour test H2 (H2 ne supporte pas REF CURSOR de la même
    // façon qu'Oracle)
    // Cette méthode sert de fallback ou d'exemple si on appelle une fonction qui
    // retourne une table
    public List<Map<String, Object>> executeQuery(String query, Object... params) {
        return jdbcTemplate.queryForList(query, params);
    }
}
