package com.example.EmployeeManagementSystem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/employees")
public class EmployeeController {

	private final EmployeeRepository employeeRepository;

	@Autowired
	public EmployeeController(EmployeeRepository employeeRepository) {
		this.employeeRepository = employeeRepository;
	}


	// Get all employees
	@GetMapping
	public ResponseEntity<Iterable<Employee>> getAllEmployees() {
		return ResponseEntity.ok(employeeRepository.findAll());
	}

	// Get employee by ID
	@GetMapping("/{id}")
	public ResponseEntity<Employee> getEmployeeById(@PathVariable Long id) {
		return employeeRepository.findById(id)
				.map(ResponseEntity::ok)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee not found"));
	}

	// Add a new employee
	@PostMapping
	public ResponseEntity<Employee> addEmployee(@Valid @RequestBody Employee employee) {
		try {
			Employee savedEmployee = employeeRepository.save(employee);
			return ResponseEntity.ok(savedEmployee);
		} catch (DataIntegrityViolationException ex) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Duplicate employee record or invalid data", ex);
		}
	}

	// Update an existing employee
	@PutMapping("/{id}")
	public ResponseEntity<Employee> updateEmployee(@PathVariable Long id, @Valid @RequestBody Employee employeeDetails) {
		try {
			return employeeRepository.findById(id)
					.map(employee -> {
						employee.setName(employeeDetails.getName());
						employee.setEmail(employeeDetails.getEmail());
						employee.setDesignation(employeeDetails.getDesignation());
						employee.setJoiningDate(employeeDetails.getJoiningDate());
						Employee updatedEmployee = employeeRepository.save(employee);
						return ResponseEntity.ok(updatedEmployee);
					})
					.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee not found"));
		} catch (DataIntegrityViolationException ex) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Duplicate employee record or invalid data", ex);
		}
	}
}
