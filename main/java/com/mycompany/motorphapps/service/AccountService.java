package com.mycompany.motorphapps.service;

import com.mycompany.motorphapps.dao.AccountDAO;
import com.mycompany.motorphapps.model.Role;

import java.util.List;


public class AccountService {

    private final AccountDAO dao;

    public AccountService() {
        this.dao = new AccountDAO();
    }

    public List<String[]> getAllAccounts() {
        return dao.getAllAccounts();
    }

    public boolean accountExists(String empId) {
        for (String[] acc : dao.getAllAccounts()) {
            if (acc[0].equals(empId)) return true;
        }
        return false;
    }

    public boolean isValidPassword(String password) {
        return password != null && !password.trim().isEmpty();
    }

    public boolean isValidRole(String role) {
        return role != null && !role.trim().isEmpty();
    }

    public void addAccount(String empId, String password, String role) {
        if (!isValidPassword(password))
            throw new IllegalArgumentException("Password cannot be empty.");
        if (!isValidRole(role))
            throw new IllegalArgumentException("Role cannot be empty.");
        if (accountExists(empId))
            throw new IllegalArgumentException("Account already exists for Employee ID: " + empId);
        dao.addAccount(empId, password, role);
    }

    public void updateAccount(String empId, String newPassword, String newRole) {
        if (!isValidPassword(newPassword))
            throw new IllegalArgumentException("Password cannot be empty.");
        if (!isValidRole(newRole))
            throw new IllegalArgumentException("Role cannot be empty.");
        dao.updateAccount(empId, newPassword, newRole);
    }

    public void deleteAccount(String empId) {
        dao.deleteAccount(empId);
    }

    public boolean canManageAccounts(String role) {
        return Role.fromString(role).canManageAccounts();
    }
}
