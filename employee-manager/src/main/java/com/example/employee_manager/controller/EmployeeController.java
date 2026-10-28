package com.example.employee_manager.controller;

import com.example.employee_manager.model.Employee;
import com.example.employee_manager.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/employees")
public class EmployeeController {

    private final EmployeeService service;

    public EmployeeController(EmployeeService service) {
        this.service = service;
    }

    /* ---------- READ (Show list and form) ---------- */
    @GetMapping
    public String list(@RequestParam(value = "keyword", required = false) String keyword,
            Model model) {
        model.addAttribute("employees", service.search(keyword));
        model.addAttribute("keyword", keyword);
        if (!model.containsAttribute("employee")) {
            model.addAttribute("employee", new Employee());
        }
        return "employees"; // This looks for employees.html
    }

    /* ---------- CREATE (Save new employee) ---------- */
    @PostMapping
    public String create(@Valid @ModelAttribute("employee") Employee employee,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        // Check if email is already taken
        if (employee.getEmail() != null && service.emailExists(employee.getEmail())) {
            result.rejectValue("email", "duplicate", "This email is already registered");
        }

        // If there are validation errors, reload the page with error messages
        if (result.hasErrors()) {
            model.addAttribute("employees", service.findAll());
            return "employees";
        }

        service.save(employee);
        redirectAttributes.addFlashAttribute("success", "Employee saved successfully!");
        return "redirect:/employees";
    }

    /* ---------- EDIT FORM (Show edit page) ---------- */
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Employee employee = service.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid employee id: " + id));
        model.addAttribute("employee", employee);
        return "edit-employee"; // This looks for edit-employee.html
    }

    /* ---------- UPDATE (Save changes) ---------- */
    @PostMapping("/{id}/update")
    public String update(@PathVariable Long id,
            @Valid @ModelAttribute("employee") Employee employee,
            BindingResult result,
            RedirectAttributes redirectAttributes) {

        if (employee.getEmail() != null && service.emailExistsForOther(id, employee.getEmail())) {
            result.rejectValue("email", "duplicate", "This email is already registered");
        }

        if (result.hasErrors()) {
            return "edit-employee";
        }

        employee.setId(id); // Important: ensure the ID stays the same
        service.save(employee);
        redirectAttributes.addFlashAttribute("success", "Employee updated successfully!");
        return "redirect:/employees";
    }

    /* ---------- DELETE (Remove employee) ---------- */
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        service.deleteById(id);
        redirectAttributes.addFlashAttribute("success", "Employee deleted successfully!");
        return "redirect:/employees";
    }
}