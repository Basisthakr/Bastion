package com.Basisttha.Bastion.Service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.Basisttha.Bastion.DTO.SearchRequest;
import com.Basisttha.Bastion.DTO.SearchResponse;
import com.Basisttha.Bastion.Exception.UserNotFoundException;
import com.Basisttha.Bastion.Model.User;
import com.Basisttha.Bastion.Repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepo;

    
    public List<SearchResponse> userSearchResponse(SearchRequest req){
        
        List<User> searchedUsers = userRepo.findByUsernameIgnoreCase(req.getUsername());
        if(searchedUsers.isEmpty()){
            throw new UserNotFoundException("No user with this username exists");
        }
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        searchedUsers = searchedUsers.stream().filter(c -> !c.getId().equals(currentUser.getId())).collect(Collectors.toList());
        if(searchedUsers.isEmpty()){
            throw new UserNotFoundException("Cannot search for yourself");
        }
        return searchedUsers.stream().map(this::toResponse).collect(Collectors.toList());
        
    }
    

    public SearchResponse toResponse(User user){
        return new SearchResponse(user.getId(), user.getUsername());
    }
}
