package com.Mongo.EjemploMongo.controller;

import com.Mongo.EjemploMongo.dto.LoginDto;
import com.Mongo.EjemploMongo.dto.TokenDto;
import com.Mongo.EjemploMongo.model.User;
import com.Mongo.EjemploMongo.security.OperationJwt;
import com.Mongo.EjemploMongo.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Calendar;

@RestController
@RequestMapping("/v1/auth")
public class AuthenticationController {

    private final OperationJwt operationJwt;

    private final UserService userService;

    @Autowired
    public AuthenticationController(OperationJwt operationJwt, UserService userService) {
        this.operationJwt = operationJwt;
        this.userService = userService;
    }

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping
    public ResponseEntity<TokenDto> generateJwt(@RequestBody LoginDto loginDto) {
        User userFound = userService.findByEmail(loginDto.getEmail());
        if (userFound != null) {
             authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDto.getEmail()
                     ,loginDto.getPassword()));
            Calendar dateExpiration = Calendar.getInstance();
            dateExpiration.add(Calendar.MINUTE, 5);
            String jwt = operationJwt.generateJwt(userFound, dateExpiration);
            TokenDto tokenDto = new TokenDto(jwt, dateExpiration.getTime());
            return new ResponseEntity<>(tokenDto, HttpStatus.OK);
        } else {
            return new ResponseEntity("email" + loginDto.getEmail()
                    + "not found", HttpStatus.NOT_FOUND);
        }


    }
}