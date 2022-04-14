package com.careerdevs.gorestsql.utils;

import com.fasterxml.jackson.databind.util.JSONPObject;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ValidationError {
    private final HashMap<String, String> errors = new HashMap<>();

    public void addError (String key, String errMsg) {
        errors.put(key, errMsg);
    }

    public boolean hasError () {
        return errors.size() != 0;
    }

    @Override
    public String toString() {
        //WITH STANDARD STRING (+=)
//            String errorMessage = "ValidationError:\n";
//            for (Map.Entry<String, String> err : errors.entrySet()) {
//                errorMessage += err.getKey() + ": " + err.getValue() + "\n";
//            }
//            return errorMessage;

        //WITH STRING-BUILDER
        StringBuilder errorMessage = new StringBuilder("ValidationError:\n");

        for (Map.Entry<String, String> err : errors.entrySet()) {
            errorMessage.append(err.getKey()).append(": ").append(err.getValue()).append("\n");
        }

        return errorMessage.toString();
    }

    public String toJSON() {
        return new JSONObject(errors).toString();
    }

}
