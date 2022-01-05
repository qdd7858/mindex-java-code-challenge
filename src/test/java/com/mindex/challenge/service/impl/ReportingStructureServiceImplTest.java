package com.mindex.challenge.service.impl;

import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.ReportingStructureService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ReportingStructureServiceImplTest {

    private String reportingStructureUrl;
    private String employeeIdUrl;
    private final String PETE_ID = "62c1084e-6e34-4630-93fd-9153afb65309";
    private final String RINGO_ID = "03aa1462-ffa9-4978-901b-7c001562cf6f";
    private final String JOHN_ID = "16a596ae-edd3-4847-99fe-c4518e82c86f";

    @Autowired
    private ReportingStructureService reportingStructureService;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Before
    public void setup() {
        reportingStructureUrl = "http://localhost:" + port + "/reportingStructure/{id}";
        employeeIdUrl = "http://localhost:" + port + "/employee/{id}";
    }

    @Test
    public void testRead(){
        Employee pete = restTemplate.getForEntity(employeeIdUrl, Employee.class, PETE_ID).getBody();
        ReportingStructure peteReport = restTemplate.getForEntity(reportingStructureUrl, ReportingStructure.class,
                PETE_ID).getBody();
        ReportingStructure zeroReport = new ReportingStructure();
        zeroReport.setEmployee(pete);
        zeroReport.setNumberOfReports(0);
        assertReportingStructureEquivalence(zeroReport, peteReport);
    }

    @Test
    public void testFindNumberOfReports(){
        // Ringo has 2 reports
        ReportingStructure ringoReport = restTemplate.getForEntity(reportingStructureUrl, ReportingStructure.class,
                RINGO_ID).getBody();
        assertEquals(2, ringoReport.getNumberOfReports());

        // John has 4 reports
        ReportingStructure johnReport = restTemplate.getForEntity(reportingStructureUrl, ReportingStructure.class,
                JOHN_ID).getBody();
        assertEquals(4, johnReport.getNumberOfReports());

        // test for self report
        Employee pete = restTemplate.getForEntity(employeeIdUrl, Employee.class, PETE_ID).getBody();
        pete.setDirectReports(new ArrayList<>(Arrays.asList(pete)));
        ReportingStructure peteReport = restTemplate.getForEntity(reportingStructureUrl, ReportingStructure.class,
                PETE_ID).getBody();
        assertEquals(0, peteReport.getNumberOfReports());
    }

    private static void assertReportingStructureEquivalence(ReportingStructure expected, ReportingStructure actual) {
        assertEmployeeEquivalence(expected.getEmployee(), actual.getEmployee());
        assertEquals(expected.getNumberOfReports(), actual.getNumberOfReports());
    }

    private static void assertEmployeeEquivalence(Employee expected, Employee actual) {
        assertEquals(expected.getFirstName(), actual.getFirstName());
        assertEquals(expected.getLastName(), actual.getLastName());
        assertEquals(expected.getDepartment(), actual.getDepartment());
        assertEquals(expected.getPosition(), actual.getPosition());
    }

}
