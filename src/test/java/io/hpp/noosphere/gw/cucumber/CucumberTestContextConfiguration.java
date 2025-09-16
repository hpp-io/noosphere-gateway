package io.hpp.noosphere.gw.cucumber;

import io.cucumber.spring.CucumberContextConfiguration;
import io.hpp.noosphere.gw.IntegrationTest;
import org.springframework.test.context.web.WebAppConfiguration;

@CucumberContextConfiguration
@IntegrationTest
@WebAppConfiguration
public class CucumberTestContextConfiguration {}
