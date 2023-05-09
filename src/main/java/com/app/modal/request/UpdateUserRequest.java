package com.app.modal.request;

import javax.persistence.Column;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UpdateUserRequest {
	
	@Size(min=3, max=100)
    @Column(name = "FIRST_NAME", nullable = false, length = 100)
    private String firstName;
    
	@Size(min=3, max=100)
    @Column(name = "LAST_NAME", nullable = false, length = 100)
    private String lastName;
	
	@Size(min=3, max=15)
    @Column(name = "AVATAR_IMAGE", nullable = false, length = 10)
    private String avatarImage;

}
