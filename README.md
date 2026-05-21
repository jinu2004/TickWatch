# TickWatch

This project was created using the [Ktor Project Generator](https://start.ktor.io).

Here are some useful links to get you started:

* [Ktor Documentation](https://ktor.io/docs/home.html)
* [Ktor GitHub page](https://github.com/ktorio/ktor)
* [Ktor Slack chat](https://app.slack.com/client/T09229ZC6/C0A974TJ9). [Request an invite](https://surveys.jetbrains.com/s3/kotlin-slack-sign-up).

## Features

Here's a list of features included in this project:

| Name                                                                                            | Description                                                                                             |
|-------------------------------------------------------------------------------------------------|---------------------------------------------------------------------------------------------------------|
| [AsyncAPI](https://start.ktor.io/p/com.asyncapi/server-asyncapi)                                | Generates and serves AsyncAPI documentation                                                             |
| [Caching Headers](https://start.ktor.io/p/io.ktor/server-caching-headers)                       | Provides options for responding with standard cache-control headers                                     |
| [CORS](https://start.ktor.io/p/io.ktor/server-cors)                                             | Enables Cross-Origin Resource Sharing (CORS)                                                            |
| [Compression](https://start.ktor.io/p/io.ktor/server-compression)                               | Compresses responses using encoding algorithms like GZIP                                                |
| [Default Headers](https://start.ktor.io/p/io.ktor/server-default-headers)                       | Adds a default set of headers to HTTP responses                                                         |
| [HttpsRedirect](https://start.ktor.io/p/io.ktor/server-https-redirect)                          | Redirects insecure HTTP requests to the respective HTTPS endpoint                                       |
| [Simple Cache](https://start.ktor.io/p/com.ucasoft/server-simple-cache)                         | Provides API for cache management                                                                       |
| [Simple Redis Cache](https://start.ktor.io/p/com.ucasoft/server-simple-redis-cache)             | Provides Redis cache for Simple Cache plugin                                                            |
| [Authentication](https://start.ktor.io/p/io.ktor/server-auth)                                   | Provides extension point for handling the Authorization header                                          |
| [Authentication JWT](https://start.ktor.io/p/io.ktor/server-auth-jwt)                           | Handles JSON Web Token (JWT) bearer authentication scheme                                               |
| [Authentication OAuth](https://start.ktor.io/p/io.ktor/server-auth-oauth)                       | Handles OAuth Bearer authentication scheme                                                              |
| [Server-Sent Events (SSE)](https://start.ktor.io/p/io.ktor/server-sse)                          | Support for server push events                                                                          |
| [Call Logging](https://start.ktor.io/p/io.ktor/server-call-logging)                             | Logs client requests                                                                                    |
| [Metrics](https://start.ktor.io/p/io.ktor/server-metrics)                                       | Adds supports for monitoring several metrics                                                            |
| [Content Negotiation](https://start.ktor.io/p/io.ktor/server-content-negotiation)               | Provides automatic content conversion according to Content-Type and Accept headers                      |
| [OpenTelemetry](https://start.ktor.io/p/io.opentelemetry.instrumentation/server-open-telemetry) | Instruments applications with distributed tracing, metrics, and logging for comprehensive observability |
| [kotlinx.serialization](https://start.ktor.io/p/io.ktor/server-kotlinx-serialization)           | Handles JSON serialization using kotlinx.serialization library                                          |
| [Koin](https://start.ktor.io/p/io.insert-koin/server-koin)                                      | Provides dependency injection                                                                           |
| [Exposed](https://start.ktor.io/p/org.jetbrains/server-exposed)                                 | Adds Exposed database to your application                                                               |
| [WebSockets](https://start.ktor.io/p/io.ktor/server-websockets)                                 | Adds WebSocket protocol support for bidirectional client connections                                    |

## Building & Running

To build or run the project, use one of the following tasks:

| Task | Description |
|------|-------------|

If the server starts successfully, you'll see the following output:

```
2024-12-04 14:32:45.584 [main] INFO  Application - Application started in 0.303 seconds.
2024-12-04 14:32:45.682 [main] INFO  Application - Responding at http://0.0.0.0:8080
```
