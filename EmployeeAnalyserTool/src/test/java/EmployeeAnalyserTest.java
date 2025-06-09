import com.employee.Employee;
import com.employee.EmployeeAnalyser;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class EmployeeAnalyserTest {

    Map<String, Employee> employeeMap;

    @Before
    public void setUp() {

        employeeMap = new HashMap<>();

        Employee ceo = new Employee("123", "Joe", "Doe", 60000, null);
        Employee e1 = new Employee("124", "Martin", "Chekov", 80000, "123");
        Employee e2 = new Employee("125", "Bob", "Ronstad", 47000, "123");
        Employee e3 = new Employee("300", "Alice", "Hasacat", 50000, "124");
        Employee e4 = new Employee("305", "Brett", "Hardleaf", 36000, "300");
        Employee e5 = new Employee("307", "Tom", "Hardy", 30000, "305");
        Employee e6 = new Employee("309", "Jeff", "Murdock", 24000, "307");

        ceo.getSubordinates().add(e1);
        ceo.getSubordinates().add(e2);
        e1.getSubordinates().add(e3);
        e3.getSubordinates().add(e4);
        e4.getSubordinates().add(e5);
        e5.getSubordinates().add(e6);

        employeeMap.put("123", ceo);
        employeeMap.put("124", e1);
        employeeMap.put("125", e2);
        employeeMap.put("300", e3);
        employeeMap.put("305", e4);
        employeeMap.put("307", e5);
        employeeMap.put("309", e6);
    }

    @Test
    public void testSalaryViolations() {
        List<String> violations = EmployeeAnalyser.checkSalaryRules(employeeMap);
        assertEquals(2, violations.size());
        assertTrue(violations.get(0).contains("Joe Doe earns too little. Short by: 16200.0"));
        assertTrue(violations.get(1).contains("Martin Chekov earns too much. Over by: 5000.0"));
    }

    @Test
    public void testDepthViolations() {
        List<String> violations = EmployeeAnalyser.checkReportingDepths(employeeMap);
        assertEquals(1, violations.size());
        assertTrue(violations.get(0).contains("Jeff Murdock has 5 managers above (limit: 4). Over by: 1"));
    }

}