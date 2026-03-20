package com.mycompany.motorphapps.service;
 
import com.mycompany.motorphapps.model.Role;
import com.mycompany.motorphapps.dao.Authenticator;
import com.mycompany.motorphapps.model.Person;
import java.util.ArrayList;
import java.util.List;
 

public class AuthService {
 private final Authenticator authenticator = new Authenticator();
 public Person login(String username, String password) {
    return authenticator.login(username, password);
}
    public List<String> getAllowedMenus(String roleStr) {
 
        Role role = Role.fromString(roleStr);
        List<String> menus = new ArrayList<>();
 
        if (role.canViewAttendance())  menus.add("attendance");
        if (role.canViewEmployees())   menus.add("employees");
        if (role.canManageAccounts())  menus.add("accounts");
        if (role.canViewLeave())       menus.add("leave");
 

        if (role.canProcessPayroll())  menus.add("payroll");
 

        if (role.canViewPayroll() && !role.canProcessPayroll()) menus.add("payslip");
 
        return menus;
    }
    
}