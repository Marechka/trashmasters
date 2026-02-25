package com.app.trashmasters.employee;

import com.app.trashmasters.employee.dto.EmployeeRequest;
import com.app.trashmasters.employee.model.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
@CrossOrigin(origins = "http://localhost:3000") // Allow React frontend to access this
public class EmployeeController {

    private final EmployeeService employeeService;

    @Autowired
    public EmployeeController(com.app.trashmasters.employee.EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    // GET /api/employees
    @GetMapping
    public ResponseEntity<List<Employee>> getAllEmployees() {
        return ResponseEntity.ok(employeeService.getAllEmployees());
    }

    @GetMapping("/allDrivers")
    public ResponseEntity<List<Employee>> getAllDrivers() {
        return ResponseEntity.ok(employeeService.getAllDriversOnly());
    }

    // GET /api/employees/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Employee> getByEmployeeId(@PathVariable String id) {
        return ResponseEntity.ok(employeeService.getByEmployeeId(id));
    }

    // POST /api/employees
    @PostMapping("/createEmployee")
    public ResponseEntity<Employee> createEmployee(@RequestBody EmployeeRequest employeeRequest) {
        if (employeeRequest.getEmployeeId() == null || employeeRequest.getFirstName() == null || employeeRequest.getLastName() == null) {
            return ResponseEntity.badRequest().build();
        }
        Employee newEmployee = new Employee();
        newEmployee.setEmployeeId(employeeRequest.getEmployeeId());
        newEmployee.setFirstName(employeeRequest.getFirstName());
        newEmployee.setLastName(employeeRequest.getLastName());
        newEmployee.setRole(employeeRequest.getRole());
        newEmployee.setEmail(employeeRequest.getEmail());
        newEmployee.setPhone(employeeRequest.getPhone());

        Employee savedEmployee = employeeService.createEmployee(newEmployee);
        return new ResponseEntity<>(savedEmployee, HttpStatus.CREATED);
    }

    // PUT /api/employees/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Employee> updateEmployee(@PathVariable String id, @RequestBody EmployeeRequest employeeRequest) {
        // Map DTO to Entity
        Employee employeeUpdates = new Employee();
        employeeUpdates.setFirstName(employeeRequest.getFirstName());
        employeeUpdates.setLastName(employeeRequest.getLastName());
        employeeUpdates.setPhone(employeeRequest.getPhone());

        return ResponseEntity.ok(employeeService.updateEmployee(id, employeeUpdates));
    }

    // DELETE /api/employees/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable String id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }


}