import com.google.gson.Gson;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.InvalidParameterException;
import java.util.Arrays;

/**
 * You can use this SDK using maven
 * Copy OneApiSdk.jar to ${project.basedir}/src/main/resources/.<br/>
 * Put this code to pom.xml<br/>
 * &lt;dependencies&gt; <br/>
 *   &lt;dependency&gt;<br/>
 *      &lt;groupId&gt;***&lt;/groupId&gt;<br/>
 *      &lt;artifactId&gt;***&lt;/artifactId&gt;<br/>
 *      &lt;scope&gt;system&lt;/scope&gt;<br/>
 *      &lt;systemPath&gt;${project.basedir}/src/main/resources/OneApiSdk.jar&lt;/systemPath&gt;<br/>
 *      &lt;version&gt;0.0.1&lt;/version&gt;<br/>
 *   &lt;/dependency&gt;<br/>
 * &lt;/dependencies&gt;<br/>
 *
 */
public class OneAPI {

  private static Gson gson = new Gson();

  /**
   * baseUri is "https://the-one-api.dev/v2"
   */
  public static final String baseUri = "https://the-one-api.dev/v2";

  /*
  //These lines are for not implemented yet.
  private static final String ENDPOINT_PREFIX_BOOK = "/book";
  private static final String ENDPOINT_PREFIX_MOVIE = "/movie";
  private static final String ENDPOINT_PREFIX_CHARACTER = "/character";
  private static final String ENDPOINT_PREFIX_QUOTE = "/quote";
  private static final String ENDPOINT_PREFIX_CHAPTER = "/chapter";

  private static final String ENDPOINT_SUFFIX_BOOK = "chapter";
  private static final String ENDPOINT_SUFFIX_MOVIE = "quote";
  private static final String ENDPOINT_SUFFIX_CHARACTER = "quote";

  private static final String PAGINATION_PAGE = "page=";
  private static final String PAGINATION_LIMIT = "limit=";
  private static final String PAGINATION_OFFSET = "offset=";

  private static final String SORT = "sort=";

  public enum ENDPOINT_TYPE{BOOK, MOVIE, CHARACTER, QUOTE, CHAPTER}
  public enum SUFFIX{BOOK, MOVIE, CHARACTER, NONE}
  public enum PAGINATION_TYPE{PAGE, LIMIT, OFFSET, NONE}
  public enum FILTER{FILTER, NONE}
  */

  /**
   * call OneApi without an access token
   * for example, when uri is "https://the-one-api.dev/v2/book/",
   * return value is
   * "{
   *     "docs": [
   *         {
   *             "_id": "5cf5805fb53e011a64671582",
   *             "name": "The Fellowship Of The Ring"
   *         },
   *         {
   *             "_id": "5cf58077b53e011a64671583",
   *             "name": "The Two Towers"
   *         },
   *         {
   *             "_id": "5cf58080b53e011a64671584",
   *             "name": "The Return Of The King"
   *         }
   *     ],
   *     "total": 3,
   *     "limit": 1000,
   *     "offset": 0,
   *     "page": 1,
   *     "pages": 1
   * }"
   * @param parameters uri, access token, and modifiers like pagination, sort, and filter
   *               The first parameter is uri, and the second access token, the rest are modifiers.
   *               possible uris are
   *               "https://the-one-api.dev/v2/book/", or just "/book", or "book" <br/>
   *               "https://the-one-api.dev/v2/book/{id}", or just "/book/{id}", or "book/{id}"<br/>
   *               "https://the-one-api.dev/v2/book/{id}/chapter", or just "/book/{id}/chapter", or "book/{id}/chapter"<br/>
   *               "https://the-one-api.dev/v2/movie/", "movie", "/movie", <br/>
   *               "https://the-one-api.dev/v2/movie/{id}", "movie", "/movie", <br/>
   *               "https://the-one-api.dev/v2/movie/{id}/quote", "movie/{id}/quote", "/movie/{id}/quote", <br/>
   *               "https://the-one-api.dev/v2/character/", "character", "/character", <br/>
   *               "https://the-one-api.dev/v2/character/{id}", "character/{id}", "/character/{id}", <br/>
   *               "https://the-one-api.dev/v2/character/{id}/quote", "character/{id}/quote", "/character/{id}/quote", <br/>
   *               "https://the-one-api.dev/v2/quote/", "quote", "/quote", <br/>
   *               "https://the-one-api.dev/v2/quote/{id}", "quote/{id}", "/quote/{id}", <br/>
   *               "https://the-one-api.dev/v2/chapter/", "chapter", "/chapter", <br/>
   *               "https://the-one-api.dev/v2/chapter/{id}", "chapter/{id}", "/chapter/{id}", <br/>
   *               with <b>pagination, sort, and filter</b><br/>
   *               <b>pagination example</b><br/>
   *               "https://the-one-api.dev/v2/book/5cf5805fb53e011a64671582/chapter", modifier="?page=2&limit=11", <br/>
   *               "https://the-one-api.dev/v2/book/5cf5805fb53e011a64671582/chapter", modifiers="?limit=5", "&offset=3"<br/>
   *               Default values for page, limit, offset are 1, 1000, and 0.<br/>
   *               <b>offset</b> and <b>page</b> do not work at the same time and <b>offset</b> has higher priority.<br/>
   *
   *               <b>sort example uri</b><br/>
   *                You can sort in ascending or descending order. <i>:asc</i> and <i>:desc</i> are used respectively.
   *               "https://the-one-api.dev/v2/book/5cf5805fb53e011a64671582/chapter", modifiers="?sort=chapterName:asc", "&limit=5", "&page=1",<br/>
   *               "https://the-one-api.dev/v2/book/5cf5805fb53e011a64671582/chapter", modifiers="?sort=chapterName:desc&limit=5", "&page=1"<br/>
   *
   *               <b>filter example uri</b><br/>
   *               You can include, exclude by full matching or regex-base matching. You can filter by numeric comparing as well.<br/>
   *               Including: "https://the-one-api.dev/v2/character?name=Legolas", <br/>
   *               Including: "https://the-one-api.dev/v2/character?name=Legolas,Gandalf", <br/>
   *               Excluding: "https://the-one-api.dev/v2/character?name!=Legolas,Gandalf&limit=5&sort=name:desc",<br/>
   *               Regex-based Including: "https://the-one-api.dev/v2/character?name=/^Ga/&limit=3",<br/>
   *               Regex-based Including: "https://the-one-api.dev/v2/character?name=/gan/i&limit=3",<br/>
   *               Regex-based Including: "https://the-one-api.dev/v2/character?race=Human&name=/^B/",<br/>
   *               Regex-based Including: "https://the-one-api.dev/v2/character?name=/n$/i&limit=3",<br/>
   *               Regex-based Excluding: "https://the-one-api.dev/v2/character?name!=/n$/i&limit=3",<br/>
   *               Regex-based Include and Exclude: "https://the-one-api.dev/v2/character?name!=/^A/&name=/n$/&limit=5"
   *               Filter by Numeric Compare: "https://the-one-api.dev/v2//movie?budgetInMillions<100",<br/>
   *               Filter by Numeric Compare: "https://the-one-api.dev/v2/movie?runtimeInMinutes>=160",<br/>
   *
   *               <b>Caution:</b><br/>
   *               -<i>exists</i> filter does not work for now. Examples of not-working uris are "/character?name" and "/character?!name", which means existence <br/>
   *               -<i>filter</i> fields are case-sensitive.<br/>
   *               -<i>filter</i> does not work for all endpoints. For example, filter does not work for "/book".
   *               To be more specific, "https://the-one-api.dev/v2/character?name=/^T/&limit=3" returns characters whose name starts with T,
   *               but "https://the-one-api.dev/v2/book/5cf5805fb53e011a64671582/chapter?chapterName=/^T/" just returns all chapters in the book.<br/>
   *               -Only the "/book" endpoint is available without authentication.</br>
   *               -Access for authenticated users to all endpoints is limited to 100 requests every 10 minutes.<br/>
   *
   * @return stringified json response
   * @throws URISyntaxException
   * @throws IOException
   * @throws InterruptedException
   */
  public static String call(String ...parameters) throws URISyntaxException, IOException, InterruptedException {
    if(parameters == null || parameters.length == 0) throw new InvalidParameterException("You need at least 1 parameter.");
    String strUri = fix(parameters[0]);
    if(parameters.length > 2) strUri += Arrays.stream(parameters).skip(2).reduce((param1, param2) -> (param1 + param2));

    HttpRequest.Builder builder = HttpRequest.newBuilder().uri(new URI(strUri));

    if(parameters.length > 1) builder.header("Authorization", "Bearer " + parameters[1]);
    HttpRequest postRequest = builder.build();

    HttpClient httpClient = HttpClient.newHttpClient();
    HttpResponse<String> httpResponse = httpClient.send(postRequest, HttpResponse.BodyHandlers.ofString());
    return httpResponse.body();
  }

  /**
   * This function fixes user-input strUri. For example, if user-input strUri is "/book", this function puts baseUri as prefix to it.
   * If user-input strUri is just "book", this function puts "/" between baseUri and user-input strUri.
   * If user-input strUri is valid, i.e., starts with baseUri, it remains unchanged.
   * Unnecessary whitespaces are trimmed as well.
   * This function does not check user-input strUri contains a valid endpoint.
   * example: <br/>
   * when strUri is "https://the-one-api.dev/v2", return value is "https://the-one-api.dev/v2" <br/>
   * when strUri is "https://the-one-api.dev/v2/", return value is "https://the-one-api.dev/v2/" <br/>
   * when strUri is "https://the-one-api.dev/v2/book", return value is "https://the-one-api.dev/v2/book" <br/>
   * when strUri is "https://the-one-api.dev/v2/books", return value is "https://the-one-api.dev/v2/books" <br/>
   * when strUri is "https://the-one-api.dev/v", return value is "https://the-one-api.dev/v2/https://the-one-api.dev/v" <br/>
   * when strUri is "https://the-one-api.dev/v/", return value is "https://the-one-api.dev/v2/https://the-one-api.dev/v/" <br/>
   * @param strUri
   * @return fixed uri
   */
  public static String fix(String strUri) {
    String checkedUri = strUri.trim();
    if(!checkedUri.startsWith(baseUri)) {
      if(checkedUri.charAt(0) != '/') checkedUri = "/" + checkedUri;
      checkedUri = baseUri + checkedUri;
    }
    return checkedUri;
  }

  /**
   * This function serialize an object into Json string using Gson.
   * This is a utility function.
   * for example, let's define <i>BookResponse</i> class and <i>Book<i/> class as follows:<br/>
   * class Book{<br/>
   *  String id;<br/>
   *  String name;<br/>
   *  }<br/>
   * class BookResponse{<br/>
   *  List<Book> books;<br/>
   *  int total;<br/>
   *  int limit;<br/>
   * }<br/>
   * We can simply stringify a BookResponse instance using stringify()
   * @param object
   * @return
   */
  public static String stringify(Object object) {
    return gson.toJson(object);
  }

  /**
   * This function parse a Json string into an Object using Gson.
   * This is a utility function for analyzing an api call response.
   * for example, let's define <i>BookResponse</i> class and <i>Book<i/> class as follows:<br/>
   * class Book{<br/>
   *  String id;<br/>
   *  String name;<br/>
   *  }<br/>
   * class BookResponse{<br/>
   *  List<Book> books;<br/>
   *  int total;<br/>
   *  int limit;<br/>
   * }<br/>
   *
   * String jsonStr = "{
   *     "docs": [
   *         {
   *             "_id": "5cf5805fb53e011a64671582",
   *             "name": "The Fellowship Of The Ring"
   *         },
   *         {
   *             "_id": "5cf58077b53e011a64671583",
   *             "name": "The Two Towers"
   *         },
   *         {
   *             "_id": "5cf58080b53e011a64671584",
   *             "name": "The Return Of The King"
   *         }
   *     ],
   *     "total": 3,
   *     "limit": 1000,
   *     "offset": 0,
   *     "page": 1,
   *     "pages": 1
   * }"
   * BookResponse bookResponse = (bookResponse)parse(jsonStr, BookResponse.class);
   * System.out.println(bookResonse.total); //result is 3
   * System.out.println(bookResonse.limit); //result is 1000
   * System.out.println(bookResponse.books.get[0].name); //result is "The Fellowship Of The Ring"
   * @param strJson
   * @param type
   * @return
   */
  public static Object parse(String strJson, Type type) {
    return gson.fromJson(strJson, type);
  }
}