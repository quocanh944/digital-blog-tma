package com.tma.digital_blog.controller;

import com.tma.digital_blog.dto.UserDTO;
import com.tma.digital_blog.service.UserService;
import com.tma.digital_blog.util.ReferencedException;
import com.tma.digital_blog.util.ReferencedWarning;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {
    private UserService userService;

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Long> createUser(@RequestBody @Valid final UserDTO userDTO) {
        final Long createdId = userService.create(userDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteUser(@PathVariable(name = "id") final Long id) {
        final ReferencedWarning referencedWarning = userService.getReferencedWarning(id);
        if (referencedWarning != null) {
            throw new ReferencedException(referencedWarning);
        }
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("")
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Page<UserDTO>> readUser(
            @RequestParam(
                    value = "page",
                    defaultValue = "0",
                    required = false
            ) Integer page,
            @RequestParam(
                    value = "size",
                    defaultValue = "5",
                    required = false
            ) Integer size,
            @RequestParam(
                    value = "search",
                    defaultValue = "",
                    required = false
            ) String search
    ) {
        Page<UserDTO> res = userService.findAll(page, size, search);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @PostMapping("/{id}/activate")
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Void> activate(
            @PathVariable(name = "id") final Long id
    ) {
        userService.activate(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/deactivate")
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Void> deactivate(
            @PathVariable(name = "id") final Long id
    ) {
        userService.deactivate(id);
        return ResponseEntity.noContent().build();
    }
}
