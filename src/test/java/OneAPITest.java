import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;

class OneAPITest {
  private String accessToken = "gkCwiGD_P7dPw3JK_Bhz";
  private class Item{String _id; String name;}
  private class Response{ List<Item> docs = Arrays.asList(new Item()); int total; int limit;}
  private String jsonStr = "{\"docs\":[{}],\"total\":0,\"limit\":0}";

  @BeforeEach
  void setUp() {
  }

  @AfterEach
  void tearDown() {
  }


  @Test
  @DisplayName("OneAPI.fix(strUri)")
  void fix() {
    assertEquals("https://the-one-api.dev/v2", OneAPI.fix("https://the-one-api.dev/v2"));
    assertEquals("https://the-one-api.dev/v2/", OneAPI.fix("https://the-one-api.dev/v2/"));
    assertEquals("https://the-one-api.dev/v2/book", OneAPI.fix("https://the-one-api.dev/v2/book"));
    assertEquals("https://the-one-api.dev/v2/books", OneAPI.fix("https://the-one-api.dev/v2/books"));
    assertEquals("https://the-one-api.dev/v2", OneAPI.fix("\thttps://the-one-api.dev/v2\n"));
    assertEquals("https://the-one-api.dev/v2/ \rbook", OneAPI.fix("\nhttps://the-one-api.dev/v2/ \rbook "));
    assertEquals("https://the-one-api.dev/v2/book", OneAPI.fix("\rhttps://the-one-api.dev/v2/book "));
    assertEquals("https://the-one-api.dev/v2/books", OneAPI.fix(" https://the-one-api.dev/v2/books\r"));
    assertEquals("https://the-one-api.dev/v2/https://the-one-api.dev/v", OneAPI.fix(" https://the-one-api.dev/v"));
    assertEquals("https://the-one-api.dev/v2/https://the-one-api.dev/v/", OneAPI.fix(" https://the-one-api.dev/v/"));
  }

  @Test
  @DisplayName("OneAPI.parse(strJson, type), OneAPI.stringify(object)")
  void parseAndStringify() {
    Response jsonClass = (Response) OneAPI.parse(jsonStr, Response.class);
    String str = OneAPI.stringify(jsonClass);
    assertEquals(jsonStr, str);
  }

  @Test
  @DisplayName("OneAPI.call(...parameters)")
  void call() {
    try {
      String jsonStr = OneAPI.call("book/");
      Response response = (Response) OneAPI.parse(jsonStr, Response.class);
      assertEquals(3, response.total);
      assertEquals(1000, response.limit);
      assertEquals(3, response.docs.size());
      assertEquals("The Fellowship Of The Ring", response.docs.get(0).name);

      jsonStr = OneAPI.call("/character?name!=Legolas,Gandalf&limit=5&sort=name:desc", accessToken);
      response = (Response) OneAPI.parse(jsonStr, Response.class);
      assertEquals(931, response.total);
      assertEquals(5, response.limit);
      assertEquals("Óin (King of Durin's Folk)", response.docs.get(0).name);

      jsonStr = OneAPI.call("/character", accessToken, "?name!=/n$/i&limit=3");
      response = (Response) OneAPI.parse(jsonStr, Response.class);
      assertEquals(773, response.total);
      assertEquals(3, response.limit);
      assertEquals("Adanel", response.docs.get(0).name);
      assertEquals("Adrahil II", response.docs.get(4).name);
    } catch (URISyntaxException e) {
    } catch (IOException e) {
    } catch (InterruptedException e) {
    }
  }
}