package com.ddaaniel.armchair_management.config.filters;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class LogSanitizer {

    private final ObjectMapper objectMapper = new ObjectMapper();

    // Lista de campos sensíveis que queremos mascarar
    private final String[] SENSITIVE_FIELDS = {"password", "token", /* "cpf", */ "email", "creditCard"};

    public String sanitizeJson(String json) {
        try {
            JsonNode rootNode = objectMapper.readTree(json);
            maskSensitiveFields(rootNode);
            return objectMapper.writeValueAsString(rootNode);
        } catch (Exception e) {
            return "[INVALID JSON]"; // Se não for JSON válido, não processa
        }
    }

    private void maskSensitiveFields(JsonNode node) {
        if (node.isObject()) {
            node.fields().forEachRemaining(entry -> {
                String fieldName = entry.getKey();
                JsonNode valueNode = entry.getValue();

                if (isSensitiveField(fieldName) && valueNode.isTextual()) {
                    ((com.fasterxml.jackson.databind.node.ObjectNode) node)
                            .put(fieldName, "****"); // Esconde o valor do campo
                } else {
                    maskSensitiveFields(valueNode);
                }
            });
        } else if (node.isArray()) {
            for (JsonNode element : node) {
                maskSensitiveFields(element);
            }
        }
    }

    private boolean isSensitiveField(String fieldName) {
        for (String sensitive : SENSITIVE_FIELDS) {
            if (sensitive.equalsIgnoreCase(fieldName)) {
                return true;
            }
        }
        return false;
    }
}

