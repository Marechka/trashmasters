package com.app.trashmasters.employee;

import com.app.trashmasters.employee.model.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeServiceImpl implements com.app.trashmasters.employee.EmployeeService {

    private final EmployeeRepository employeeRepository;

    @Autowired
    public EmployeeServiceImpl(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    @Override
    public Employee getByEmployeeId(String id) {
        return employeeRepository.findByEmployeeId(id)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));
    }

    @Override
    public Employee createEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }

    @Override
    public Employee updateEmployee(String id, Employee employeeDetails) {
        Employee existingEmployee = getByEmployeeId(id);

        // Update fields
        existingEmployee.setFirstName(employeeDetails.getFirstName());
        existingEmployee.setLastName(employeeDetails.getLastName());
        existingEmployee.setPhone(employeeDetails.getPhone());
        // Do not update ID or Email if you don't want to allow it

        return employeeRepository.save(existingEmployee);
    }

    @Override
    public void deleteEmployee(String id) {
        Employee employee = getByEmployeeId(id); // Check if exists first
        employeeRepository.delete(employee);
    }

    @Override
    public List<Employee> getAllDriversOnly() {
        return employeeRepository.findAll().stream()
                .filter(user -> user.getRole().name().equals("DRIVER"))
                .toList();
    }
}