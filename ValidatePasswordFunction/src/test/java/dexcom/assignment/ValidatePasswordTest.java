package dexcom.assignment;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class ValidatePasswordTest {

  @Mock
  APIGatewayProxyRequestEvent  input;

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void errorSpecialCharacter() {
    ValidatePassword app = new ValidatePassword();


    when(input.getBody()).thenReturn("{\"password\" : \"mypass111\"}");
    APIGatewayProxyResponseEvent result = app.handleRequest(input, null);
    assertEquals(404, result.getStatusCode().intValue());
    String content = result.getBody();
    assertNotNull(content);
    assertEquals(content, "{\"ErrorMessage\":\"Password should contain at least one special character\"}");
  }

  @Test
  public void errorNumber() {
    ValidatePassword app = new ValidatePassword();

    when(input.getBody()).thenReturn("{\"password\" : \"mypassssssss\"}");
    APIGatewayProxyResponseEvent result = app.handleRequest(input, null);
    assertEquals(404, result.getStatusCode().intValue());
    String content = result.getBody();
    assertNotNull(content);
    assertEquals(content, "{\"ErrorMessage\":\"Password should contain at least one number\"}");
  }

  @Test
  public void errorPasswordSize() {
    ValidatePassword app = new ValidatePassword();

    when(input.getBody()).thenReturn("{\"password\" : \"mypa\"}");
    APIGatewayProxyResponseEvent result = app.handleRequest(input, null);
    assertEquals(404, result.getStatusCode().intValue());
    String content = result.getBody();
    assertNotNull(content);
    assertEquals(content, "{\"ErrorMessage\":\"Password length should be greater than 8 characters\"}");
  }

  @Test
  public void successPassword() {
    ValidatePassword app = new ValidatePassword();

    when(input.getBody()).thenReturn("{\"password\" : \"mypassword1!\"}");
    APIGatewayProxyResponseEvent result = app.handleRequest(input, null);
    assertEquals(200, result.getStatusCode().intValue());
    String content = result.getBody();
    assertNotNull(content);
    assertEquals(content, "{\"hash\":\"8353c73d66b4dc125ae462bff8da98c3d47f6a32c538c39e97d06840f30b4a9f\"}");
  }


}
