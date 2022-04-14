package com.careerdevs.gorestsql.controllers;

import com.careerdevs.gorestsql.models.User;
import com.careerdevs.gorestsql.repos.UserRepository;
import com.careerdevs.gorestsql.utils.ApiErrorHandling;
import com.careerdevs.gorestsql.utils.ValidationError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping ("/user")
public class UserController {

    /*
        Required Routes for GoRestSQL MVP:
            GET route that queries one user by ID and saved their data to your local database (returns the SQL user data)
            GET route that returns one user by ID from the SQL database
            GET route that returns all users stored in the SQL database
            DELETE route that deletes one user by ID from SQL database (returns the deleted SQL user data)
            DELETE route that deletes all users from SQL database (returns how many users were deleted)
            POST route that uploads all users from the GoREST API into the SQL database (returns how many users were uploaded)
            POST route that create a user on JUST the SQL database (returns the newly created SQL user data)
            PUT route that updates a user on JUST the SQL database (returns the updated SQL user data)
    * */

    @Autowired
    private UserRepository userRepository;

    @GetMapping ("/{id}")
    public ResponseEntity<?> getById (@PathVariable ("id") String id) {
        try {

            if (ApiErrorHandling.isStrNaN(id))
                throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "- " + id + " is not a valid ID");

            int userId = Integer.parseInt(id);

            Optional<User> foundUser = userRepository.findById(userId);

            if (foundUser.isEmpty())
                throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "- User Not Found With ID: " + id);

            return new ResponseEntity<>(foundUser, HttpStatus.OK);

        } catch (HttpClientErrorException e) {
            return ApiErrorHandling.customApiError(e.getMessage(), e.getStatusCode());
        } catch (Exception e) {
            return ApiErrorHandling.genericApiError(e);
        }
    }

    //    @DeleteMapping ("/delete/{id}") DELETE a new user on the local SQL database
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteById (@PathVariable ("id") String id) {
        try {

            if (ApiErrorHandling.isStrNaN(id))
                throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "- " + id + " is not a valid ID");

            int userId = Integer.parseInt(id);

            Optional<User> foundUser = userRepository.findById(userId);

            if (foundUser.isEmpty())
                throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "- User Not Found With ID: " + id);

            userRepository.deleteById(userId);

            return new ResponseEntity<>(foundUser, HttpStatus.OK);

        } catch (HttpClientErrorException e) {
            return ApiErrorHandling.customApiError(e.getMessage(), e.getStatusCode());
        } catch (Exception e) {
            return ApiErrorHandling.genericApiError(e);
        }
    }

    @PostMapping ("/upload/{id}")
    public ResponseEntity<?> uploadUserById ( @PathVariable ("id") String userId, RestTemplate restTemplate
    ) {

        try {

            if (ApiErrorHandling.isStrNaN(userId))
                throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "- " + userId + " is not a valid ID");
            int uID = Integer.parseInt(userId);

            String url = "https://gorest.co.in/public/v2/users/" + uID;

            User foundUser = restTemplate.getForObject(url, User.class);

            System.out.println(foundUser);

            assert foundUser != null;
            User savedUser = userRepository.save(foundUser);

            return new ResponseEntity<>(savedUser, HttpStatus.OK);

        } catch (HttpClientErrorException e) {
            return ApiErrorHandling.customApiError(e.getMessage(), e.getStatusCode());
        } catch (Exception e) {
            return ApiErrorHandling.genericApiError(e);
        }

    }

//    @GetMapping ("/uploadall") Upload all users from GOREST to your local SQL database
    @PostMapping ("/uploadall")
    public ResponseEntity<?> getAll (
            RestTemplate restTemplate
    ) {

        try {

            String url = "https://gorest.co.in/public/v2/users";

            //MAKE REQUEST FOR FIRST PAGE OF USERS
            ResponseEntity<User[]> response = restTemplate.getForEntity(url, User[].class);

            //EXTRACT JUST THE BODY (user data)
            User[] firstPageUsers = response.getBody();

            assert firstPageUsers != null;
            ArrayList<User> allUsers = new ArrayList<>(Arrays.asList(firstPageUsers));

            HttpHeaders responseHeaders = response.getHeaders();

            //EXTRACT THE TOTAL NUMBER OF PAGES FROM HEADERS
            String totalPages = Objects.requireNonNull(responseHeaders.get("X-Pagination-Pages")).get(0);
            int totalPgNum = Integer.parseInt(totalPages);

            //ITERATE THROUGH TOTAL PAGES TO GET ALL USERS PAGE BY PAGE
            for (int i = 2; i <= totalPgNum; i++) {
                String pageUrl = url + "?page=" + i;
                User[] pageUsers = restTemplate.getForObject(pageUrl, User[].class);

                assert pageUsers != null;
                allUsers.addAll(Arrays.asList(firstPageUsers));

            }

            //upload all users to SQL
            userRepository.saveAll(allUsers);

            return new ResponseEntity<>("Users Saved: " + allUsers.size(), HttpStatus.OK);

        } catch (Exception e) {
            System.out.println(e.getClass());
            System.out.println(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }


    }
    //@PostMapping ("/") Create a new user on the local SQL database
    @PostMapping ("/")
    public ResponseEntity<?> createNewUser ( @RequestBody User newUser) {
        try {

            ValidationError validationErrors = ApiErrorHandling.validateNewUser(newUser);

            if (validationErrors.hasError())
                throw new HttpClientErrorException( HttpStatus.BAD_REQUEST, validationErrors.toJSON());

            User savedUser = userRepository.save(newUser);

            return new ResponseEntity<>(savedUser, HttpStatus.CREATED);

        } catch (HttpClientErrorException e) {
            return ApiErrorHandling.customApiError(e.getMessage(), e.getStatusCode());
        } catch (Exception e) {
            return ApiErrorHandling.genericApiError(e);
        }
    }

//    @PutMapping ("/update") UPDATE a new user on the local SQL database
    @PutMapping ("/")
    public ResponseEntity<?> updateUser ( @RequestBody User updateUser) {
        try {

            ValidationError validationErrors = ApiErrorHandling.validateUpdateUser(updateUser);

            if (validationErrors.hasError())
                throw new HttpClientErrorException( HttpStatus.BAD_REQUEST, validationErrors.toJSON());

            User savedUser = userRepository.save(updateUser);

            return new ResponseEntity<>(savedUser, HttpStatus.CREATED);

        } catch (HttpClientErrorException e) {
            return ApiErrorHandling.customApiError(e.getMessage(), e.getStatusCode());
        } catch (Exception e) {
            return ApiErrorHandling.genericApiError(e);
        }
    }

//    @DeleteMapping ("/deleteall") Delete all users on the local SQL db
    @DeleteMapping("/deleteall")
    public ResponseEntity<?> deleteAllUsers () {
        try {
            long totalUsers = userRepository.count();

            //This resets the user table by clearing the data in the table and resetting the auto increment to 1
            userRepository.deleteAll();
//            userRepository.resetId(); //Method runs with out error, but does not affect new user's ids

            return new ResponseEntity<>(totalUsers + " Users Deleted", HttpStatus.OK);

        }  catch (Exception e) {
            return ApiErrorHandling.genericApiError(e);
        }
    }

    //GET ALL USERS IN SQL DB -- http://localhost:8080/user/all
    @GetMapping("/all")
    public ResponseEntity<?> getAllUser () {
        try {
            Iterable<User> allUsers = userRepository.findAll();
            return new ResponseEntity<>(allUsers, HttpStatus.OK);

        } catch ( Exception e) {
            return ApiErrorHandling.genericApiError(e);
        }
    }

}
