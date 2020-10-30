package net.snowflake.client.jdbc.telemetryOOB;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TelemetryServiceTest {
  private boolean defaultState;

  @Before
  public void setUp() {
    TelemetryService service = TelemetryService.getInstance();
    defaultState = service.isEnabled();
    service.enable();
  }

  @After
  public void tearDown() throws InterruptedException {
    // wait 5 seconds while the service is flushing
    TimeUnit.SECONDS.sleep(5);
    TelemetryService service = TelemetryService.getInstance();
    if (defaultState) {
      service.enable();
    } else {
      service.disable();
    }
  }

  @Test
  public void testTelemetryInitErrors() {
    TelemetryService service = TelemetryService.getInstance();
    assertThat(
        "Telemetry server failure count should be zero at first.",
        service.getServerFailureCount(),
        equalTo(0));
    assertThat(
        "Telemetry client failure count should be zero at first.",
        service.getClientFailureCount(),
        equalTo(0));
  }

  @Test
  public void testTelemetryEnableDisable() {
    TelemetryService service = TelemetryService.getInstance();
    // We already enabled telemetry in setup phase.
    assertThat("Telemetry should be already enabled here.", service.isEnabled(), equalTo(true));
    service.disable();
    assertThat("Telemetry should be already disabled here.", service.isEnabled(), equalTo(false));
    service.enable();
    assertThat("Telemetry should be enabled back", service.isEnabled(), equalTo(true));
  }

  @Test
  public void testTelemetryConnectionString() {
    String expectedStr =
        "https://snowflake.reg.local:8082?ROLE=fakerole&ACCOUNT=fakeaccount&PASSWORD=******&DATABASE=fakedatabase&PORT=8082&SCHEMA=fakeschema&HOST=snowflake.reg.local&USER=fakeuser&URI=jdbc%3Asnowflake%3A%2F%2Fsnowflake.reg.local%3A8082";
    Map<String, String> param = new HashMap<>();
    param.put("uri", "jdbc:snowflake://snowflake.reg.local:8082");
    param.put("host", "snowflake.reg.local");
    param.put("port", "8082");
    param.put("account", "fakeaccount");
    param.put("user", "fakeuser");
    param.put("password", "fakepassword");
    param.put("database", "fakedatabase");
    param.put("schema", "fakeschema");
    param.put("role", "fakerole");
    TelemetryService service = TelemetryService.getInstance();
    service.updateContextForIT(param);

    assertThat(
        "sfConnectionStr generated by telemetry is incorrect",
        service.getSnowflakeConnectionString().toString(),
        equalTo(expectedStr));
  }
}
