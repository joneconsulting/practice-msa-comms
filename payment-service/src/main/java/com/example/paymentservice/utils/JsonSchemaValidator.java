package com.example.paymentservice.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.everit.json.schema.Schema;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.InputStream;

public class JsonSchemaValidator {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static boolean validate(String json, String schemaPath) {
        try (InputStream schemaStream = JsonSchemaValidator.class.getResourceAsStream(schemaPath)) {
            JSONObject rawSchema = new JSONObject(new JSONTokener(schemaStream));
            Schema schema = SchemaLoader.load(rawSchema);

            JSONObject payload = new JSONObject(json);
            schema.validate(payload); // 유효하지 않음 시 예외 발생
            return true;
        } catch (Exception e) {
            System.err.println("❌ 스키마 검증 실패: " + e.getMessage());
            return false;
        }
    }
}
