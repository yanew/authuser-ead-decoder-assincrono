package com.ead.authuser.controllers;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ead.authuser.dtos.UserDto;
import com.ead.authuser.models.UserModel;
import com.ead.authuser.services.UserService;
import com.ead.authuser.specifications.SpecificationTemplate;
import com.fasterxml.jackson.annotation.JsonView;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/users")
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping
    public ResponseEntity<Page<UserModel>> getAllUSers(SpecificationTemplate.UserSpec spec,
    		@PageableDefault(page = 0, size = 1, sort = "userId", direction = Sort.Direction.ASC) Pageable pageable,
    		@RequestParam(required=false)UUID courseId){
    	Page<UserModel> userModelPage = null;
    	
    	if(courseId!=null) {
    		userModelPage = userService.findAll(SpecificationTemplate.userCourseId(courseId).and(spec), pageable);
    	}else {
    		userModelPage = userService.findAll(spec, pageable);
    	}
    			
    	if(!userModelPage.isEmpty()) {
    		for(UserModel user : userModelPage.toList()) {
    			user.add(linkTo(methodOn(UserController.class).getOneUser(user.getUserId())).withSelfRel());
    		}
    	}
    	
    	
        return ResponseEntity.status(HttpStatus.OK).body(userModelPage);
    }

	@GetMapping("/{userId}")
    public ResponseEntity<Object> getOneUser(@PathVariable(value = "userId") UUID userId){
        Optional<UserModel> userModelOptional = userService.findById(userId);
        if(!userModelOptional.isPresent()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("UsuÃ¡rio nÃ£o encontrado!");
        }else{
            return ResponseEntity.status(HttpStatus.OK).body(userModelOptional.get());
        }
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUser(@PathVariable(value = "userId") UUID userId){
        Optional<UserModel> userModelOptional = userService.findById(userId);
        if(!userModelOptional.isPresent()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("UsuÃ¡rio nÃ£o encontrado!");
        }else{
            userService.delete(userModelOptional.get());
            return ResponseEntity.status(HttpStatus.OK).body("UsuÃ¡rio deletado com sucesso!");
        }
    }
    
    @PutMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@PathVariable(value = "userId") UUID userId,
    		@RequestBody @Validated(UserDto.UserView.UserPut.class) 
    		@JsonView(UserDto.UserView.UserPut.class) UserDto userDto){
        Optional<UserModel> userModelOptional = userService.findById(userId);
        if(!userModelOptional.isPresent()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("UsuÃ¡rio nÃ£o encontrado!");
        }else{
        	var userModel = userModelOptional.get();
        	userModel.setFullName(userDto.getFullName());
        	userModel.setPhoneNumber(userDto.getPhoneNumber());
        	userModel.setCpf(userDto.getCpf());
        	userModel.setLastUpdateDate(LocalDateTime.now(ZoneId.of("UTC")));
        	
            userService.save(userModel);
            return ResponseEntity.status(HttpStatus.OK).body("UsuÃ¡rio atualizado com sucesso!");
        }
    }

    @PutMapping("/{userId}/password")
    public ResponseEntity<Object> updatePassword(@PathVariable(value = "userId") UUID userId,
    		@RequestBody @Validated(UserDto.UserView.PasswordPut.class) 
    		@JsonView(UserDto.UserView.PasswordPut.class) UserDto userDto){
        Optional<UserModel> userModelOptional = userService.findById(userId);
        if(!userModelOptional.isPresent()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("UsuÃ¡rio nÃ£o encontrado!");
        }else if(!userModelOptional.get().getPassword().equals(userDto.getOldPassword())) {
        	return ResponseEntity.status(HttpStatus.NOT_FOUND).body("O campo 'Senha anterior' informa senha diferente da atual!");
        } else{
        	var userModel = userModelOptional.get();
        	
        	if(userModel.getPassword().equals(userDto.getPassword())) {
        		return ResponseEntity.status(HttpStatus.NOT_FOUND).body("VocÃª informou a mesma senha!");	
        	}else {
        		userModel.setPassword(userDto.getPassword());
            	userModel.setLastUpdateDate(LocalDateTime.now(ZoneId.of("UTC")));
            	
                userService.save(userModel);
                return ResponseEntity.status(HttpStatus.OK).body("UsuÃ¡rio atualizado com sucesso!");	
        	}
        }
    }
    
    @PutMapping("/{userId}/image")
    public ResponseEntity<Object> updateImage(@PathVariable(value = "userId") UUID userId,
    		@RequestBody @Validated(UserDto.UserView.ImagePut.class) 
    		@JsonView(UserDto.UserView.ImagePut.class) UserDto userDto){
        Optional<UserModel> userModelOptional = userService.findById(userId);
        if(!userModelOptional.isPresent()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("UsuÃ¡rio nÃ£o encontrado!");
        }else{
        	var userModel = userModelOptional.get();
        	userModel.setImageUrl(userDto.getImageUrl());
        	userModel.setLastUpdateDate(LocalDateTime.now(ZoneId.of("UTC")));
        	
            userService.save(userModel);
            return ResponseEntity.status(HttpStatus.OK).body(userModel);
        }
    }

}