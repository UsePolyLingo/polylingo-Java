# PolyLingo Java SDK

Official Java SDK for the [PolyLingo](https://usepolylingo.com) translation API.

[![Maven Central](https://img.shields.io/maven-central/v/com.usepolylingo/polylingo)](https://central.sonatype.com/artifact/com.usepolylingo/polylingo)

## Requirements

- Java 11 or later
- A PolyLingo API key ([dashboard](https://usepolylingo.com))

## Installation

### Maven

```xml
<dependency>
  <groupId>com.usepolylingo</groupId>
  <artifactId>polylingo</artifactId>
  <version>0.1.0</version>
</dependency>
```

### Gradle

```gradle
implementation 'com.usepolylingo:polylingo:0.1.0'
```

## Quick start

```java
import com.usepolylingo.polylingo.PolyLingo;
import com.usepolylingo.polylingo.types.*;
import java.util.List;

PolyLingo client = PolyLingo.builder()
    .apiKey(System.getenv("POLYLINGO_API_KEY"))
    .build();

TranslateResult result = client.translate(
    TranslateParams.builder()
        .content("{\"hello\":\"world\"}")
        .targets(List.of("es", "fr"))
        .format("json")
        .build());
System.out.println(result.getTranslations().get("es"));
```

## API

| Method | Description |
| --- | --- |
| `health()` | `GET /health` |
| `languages()` | `GET /languages` |
| `usage()` | `GET /usage` |
| `translate(TranslateParams)` | `POST /translate` |
| `batch(BatchParams)` | `POST /translate/batch` |
| `jobs().create(CreateJobParams)` | `POST /jobs` (returns `202`) |
| `jobs().get(jobId)` | `GET /jobs/:id` |
| `jobs().translate(JobsTranslateParams)` | Create job and poll until complete |

## Builder options

`PolyLingo.builder()`:

| Method | Default |
| --- | --- |
| `apiKey(String)` | required |
| `baseUrl(String)` | `https://api.usepolylingo.com/v1` |
| `timeout(Duration)` | `120s` per request |

## Jobs polling

`JobsTranslateParams.builder()`:

| Method | Default |
| --- | --- |
| `pollInterval(Duration)` | `5s` |
| `timeout(Duration)` | `20m` total poll budget |
| `onProgress(Consumer<Integer>)` | optional queue position callback |

## Errors

Catch `PolyLingoException` or subclasses:

| Class | When |
| --- | --- |
| `AuthException` | HTTP 401 |
| `RateLimitException` | HTTP 429 (`getRetryAfter()` may be set) |
| `JobFailedException` | Job failed or poll timeout (`getJobId()`) |
| `PolyLingoException` | Other API errors |

## Documentation

Full docs: [PolyLingo Java SDK](https://usepolylingo.com/en/docs/sdk/java)

## Building from source

JDK **11+** and Maven **3.9+**:

```bash
mvn verify
```

## Maven Central publishing

Releases are published from GitHub Actions when a `v*` tag is pushed (see `.github/workflows/release.yml`). Configure OSSRH credentials and GPG signing secrets on the repository before cutting the first release.

## License

MIT
