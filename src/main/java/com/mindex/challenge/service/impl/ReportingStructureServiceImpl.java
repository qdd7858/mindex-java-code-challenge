package com.mindex.challenge.service.impl;

import com.mindex.challenge.dao.EmployeeRepository;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.ReportingStructureService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ReportingStructureServiceImpl implements ReportingStructureService {
    private static final Logger LOG = LoggerFactory.getLogger(ReportingStructureServiceImpl.class);

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public ReportingStructure read(String id) {
        LOG.debug("Creating employee's reporting structure with id [{}]", id);
        Employee employee = employeeRepository.findByEmployeeId(id);

        if (employee == null) {
            throw new RuntimeException("Invalid employeeId: " + id);
        }

        ReportingStructure report = new ReportingStructure();
        report.setEmployee(employee);
        report.setNumberOfReports(findNumberOfReports(id));
        return report;
    }

    private int findNumberOfReports (String id){
        // Using DFS to find reachable employee from the target employee
        Queue<String> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();
        queue.add(id);
        visited.add(id);
        while (!queue.isEmpty()){
            Employee employee = employeeRepository.findByEmployeeId(queue.poll());
            List<Employee> directReports = employee.getDirectReports();
            if (directReports != null){
                for (Employee e: directReports){
                    if (!visited.contains(e)){
                        queue.add(e.getEmployeeId());
                        visited.add(e.getEmployeeId());
                    }
                }
            }

        }

        return visited.size() - 1;
    }
}
