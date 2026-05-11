package com.Basisttha.Bastion.Controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Basisttha.Bastion.DTO.SearchRequest;
import com.Basisttha.Bastion.DTO.SearchResponse;
import com.Basisttha.Bastion.Service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/search")
    public ResponseEntity<List<SearchResponse>> search(SearchRequest req){
        return ResponseEntity.ok().body(userService.userSearchResponse(req));
    }
}
