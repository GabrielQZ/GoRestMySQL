package com.careerdevs.gorestsql.utils;

import com.careerdevs.gorestsql.models.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

public class ApiErrorHandling {


    public static ResponseEntity<?> genericApiError(Exception e) {
        System.out.println(e.getMessage());
        System.out.println(e.getClass());
        return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public static ResponseEntity<?> customApiError(String message, HttpStatus status) {
        return new ResponseEntity<>(message, status);
    }

    public static ValidationError validateNewUser(User user) {
        //WITH HASHMAP
//        HashMap<String, String> error = new HashMap<>();
//
//        if (user.getName() == null) error.put("name", "Name can not be left blank");
//
//        if (user.getEmail() == null) error.put("email", "Email can not be left blank");
//
//        if (user.getGender() == null) error.put("gender", "Gender can not be left blank");
//
//        if (user.getStatus() == null) error.put("status", "status can not be left blank");
//
//        return error;

        //WITH CUSTOM ERROR CLASS
        ValidationError error = new ValidationError();
        if (user.getName() == null) error.addError("name", "Name can not be left blank");

        if (user.getEmail() == null) error.addError("email", "Email can not be left blank");

        if (user.getGender() == null) error.addError("gender", "Gender can not be left blank");

        if (user.getStatus() == null) error.addError("status", "status can not be left blank");

        return error;

    }


    public static ValidationError validateUpdateUser(User user) {
        ValidationError error = new ValidationError();
        if (user.getId() == null) error.addError("id", "ID can not be left blank");

        if (user.getName() == null) error.addError("name", "Name can not be left blank");

        if (user.getEmail() == null) error.addError("email", "Email can not be left blank");

        if (user.getGender() == null) error.addError("gender", "Gender can not be left blank");

        if (user.getStatus() == null) error.addError("status", "status can not be left blank");

        return error;

    }

    public static boolean isStrNaN(String strNum) {
        if (strNum == null) {
            return true;
        }
        try {
            Integer.parseInt(strNum);
        } catch (NumberFormatException nfe) {
            return true;
        }
        return false;
    }

}
