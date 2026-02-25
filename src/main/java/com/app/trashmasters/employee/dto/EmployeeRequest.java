package com.app.trashmasters.employee.dto;

import com.app.trashmasters.employee.model.UserRole;
import lombok.*;

@Data
public class EmployeeRequest {
    private String employeeId;
    private String firstName;
    private String lastName;
    private UserRole role;
    private String email;
    private String phone;

}