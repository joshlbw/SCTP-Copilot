package com.example.EmployeeManagementSystem;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.time.LocalDate;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EmployeeController.class)
public class EmployeeControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @SuppressWarnings("removal")
    @MockBean
    private EmployeeRepository employeeRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Employee employee;

    @BeforeEach
    void setUp() {
        employee = new Employee(1L, "John Doe", "john@example.com", "Developer", LocalDate.now());
    }

    @Test
    void addEmployee_success() throws Exception {
        Mockito.when(employeeRepository.save(any(Employee.class))).thenReturn(employee);
        mockMvc.perform(post("/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(employee)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John Doe"));
    }

    @Test
    void addEmployee_invalidInput() throws Exception {
        Employee invalid = new Employee(1L, "", "", "", null);
        mockMvc.perform(post("/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addEmployee_duplicate() throws Exception {
        Mockito.when(employeeRepository.save(any(Employee.class))).thenThrow(new DataIntegrityViolationException("Duplicate"));
        mockMvc.perform(post("/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(employee)))
                .andExpect(status().isConflict());
    }

    @Test
    void updateEmployee_success() throws Exception {
        Mockito.when(employeeRepository.findById(eq(1L))).thenReturn(Optional.of(employee));
        Mockito.when(employeeRepository.save(any(Employee.class))).thenReturn(employee);
        employee.setName("Jane Doe");
        mockMvc.perform(put("/employees/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(employee)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Jane Doe"));
    }

    @Test
    void updateEmployee_notFound() throws Exception {
        Mockito.when(employeeRepository.findById(eq(2L))).thenReturn(Optional.empty());
        mockMvc.perform(put("/employees/2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(employee)))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateEmployee_invalidInput() throws Exception {
        Employee invalid = new Employee(1L, "", "", "", null);
        Mockito.when(employeeRepository.findById(eq(1L))).thenReturn(Optional.of(employee));
        mockMvc.perform(put("/employees/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateEmployee_duplicate() throws Exception {
        Mockito.when(employeeRepository.findById(eq(1L))).thenReturn(Optional.of(employee));
        Mockito.when(employeeRepository.save(any(Employee.class))).thenThrow(new DataIntegrityViolationException("Duplicate"));
        mockMvc.perform(put("/employees/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(employee)))
                .andExpect(status().isConflict());
    }
}
