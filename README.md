# exception-demo

> Spring MVC exception handling with @ResponseStatus, @ExceptionHandler, and @RestControllerAdvice

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.org/)
[![Spring MVC](https://img.shields.io/badge/Spring%20MVC-6.2.5-blue.svg)](https://spring.io/projects/spring-framework)
[![Jakarta Validation](https://img.shields.io/badge/Jakarta%20Validation-3.1.0-red.svg)](https://jakarta.ee/specifications/bean-validation/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

A comprehensive demonstration of **Spring MVC exception handling mechanisms** featuring `@ResponseStatus`, `@ExceptionHandler`, `@RestControllerAdvice`, and unified error response design.

## Features

- Two exception handling strategies demonstrated
- `@ResponseStatus` for simple exception handling
- `@ExceptionHandler` for controller-level exception handling
- `@RestControllerAdvice` for global exception handling
- Custom exception classes (`FormValidationException`)
- Jakarta Bean Validation integration
- Form data validation (application/x-www-form-urlencoded)
- JSON request validation (application/json)
- Unified error response format
- Multi-part file upload with batch coffee creation
- Complete CRUD operations for coffee and orders
- Spring Cache integration

## Tech Stack

- Spring Boot 3.4.5
- Spring MVC 6.2.5
- Spring Data JPA
- Jakarta Validation (Bean Validation 3.0)
- Java 21
- H2 Database 2.3.232
- Joda Money 2.0.2
- Jackson (Hibernate6 module, XML format)
- Apache Commons Lang3
- Lombok
- Maven 3.8+

## Getting Started

### Prerequisites

- JDK 21 or higher
- Maven 3.8+ (or use included Maven Wrapper)

### Quick Start

**Run the application:**

```bash
./mvnw spring-boot:run
```

**Test exception handling:**

```bash
# Test 1: Form validation error (price is empty)
curl -X POST http://localhost:8080/coffee/ \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "name=Americano&price="

# Test 2: JSON validation error (name is empty)
curl -X POST http://localhost:8080/coffee/ \
  -H "Content-Type: application/json" \
  -d '{"name": "", "price": 150.00}'
```

## Exception Handling Mechanisms

### Two Exception Handling Strategies

This project demonstrates **two different exception handling approaches** in Spring MVC:

| Strategy | Exception Type | Handler | Response Format | Use Case |
|----------|---------------|---------|-----------------|----------|
| **@ResponseStatus** | FormValidationException | Spring auto-handles | Spring MVC default | Simple cases |
| **@RestControllerAdvice** | ValidationException | GlobalControllerAdvice | Custom JSON | Unified error format |

### Strategy 1: @ResponseStatus

**Custom Exception Class:**

```java
@ResponseStatus(HttpStatus.BAD_REQUEST)
@Getter
@AllArgsConstructor
public class FormValidationException extends RuntimeException {
    private final BindingResult result;
}
```

**Controller Usage:**

```java
@PostMapping(consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
@ResponseBody
@ResponseStatus(HttpStatus.CREATED)
public Coffee addCoffee(@Valid NewCoffeeRequest newCoffee, BindingResult result) {
    if (result.hasErrors()) {
        log.warn("Binding Errors: {}", result);
        throw new FormValidationException(result);  // Auto-handled by @ResponseStatus
    }
    return coffeeService.saveCoffee(newCoffee.getName(), newCoffee.getPrice());
}
```

**Test:**

```bash
curl -X POST http://localhost:8080/coffee/ \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "name=Americano&price="
```

**Response:**

```json
{
    "timestamp": "2025-10-17T05:42:54.993+00:00",
    "status": 400,
    "error": "Bad Request",
    "message": "No message available",
    "path": "/coffee/"
}
```

**Characteristics:**
- ✅ Simple: No additional code needed
- ✅ Automatic: Spring handles it automatically
- ❌ Fixed format: Cannot customize error response
- ❌ Less information: Default Spring error format

### Strategy 2: @RestControllerAdvice

**Global Exception Handler:**

```java
@RestControllerAdvice
public class GlobalControllerAdvice {
    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> validationExceptionHandler(ValidationException exception) {
        Map<String, String> map = new HashMap<>();
        map.put("message", exception.getMessage());
        return map;
    }
}
```

**Controller Usage:**

```java
@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
@ResponseBody
@ResponseStatus(HttpStatus.CREATED)
public Coffee addJsonCoffee(@Valid @RequestBody NewCoffeeRequest newCoffee, 
                            BindingResult result) {
    if (result.hasErrors()) {
        log.warn("Binding Errors: {}", result);
        throw new ValidationException(result.toString());  // Handled by GlobalControllerAdvice
    }
    return coffeeService.saveCoffee(newCoffee.getName(), newCoffee.getPrice());
}
```

**Test:**

```bash
curl -X POST http://localhost:8080/coffee/ \
  -H "Content-Type: application/json" \
  -d '{"name": "", "price": 150.00}'
```

**Response:**

```json
{
    "message": "org.springframework.validation.BeanPropertyBindingResult: 1 errors\nField error in object 'newCoffeeRequest' on field 'name': rejected value [null]; codes [NotEmpty.newCoffeeRequest.name,NotEmpty.name,NotEmpty.java.lang.String,NotEmpty]; arguments [org.springframework.context.support.DefaultMessageSourceResolvable: codes [newCoffeeRequest.name,name]; arguments []; default message [name]]; default message [不得是空的]"
}
```

**Characteristics:**
- ✅ Flexible: Can customize error response format
- ✅ Unified: All controllers share the same handler
- ✅ Detailed: Can include detailed error information
- ✅ Global: Handles exceptions from all controllers
- ⚠️ More code: Needs additional Advice class

## Configuration

### Application Properties

```properties
# JPA/Hibernate configuration
spring.jpa.hibernate.ddl-auto=none
spring.jpa.properties.hibernate.show_sql=true
spring.jpa.properties.hibernate.format_sql=true

# Error response configuration (for development only)
server.error.include-message=always
server.error.include-binding-errors=always
```

**Important:**
- `show_sql=true`: Show SQL statements (development only)
- `include-message=always`: Include error messages in response (development only)
- **Production**: Set to `never` to avoid information leakage

### Database Schema

**schema.sql:**

```sql
drop table t_coffee if exists;
drop table t_order if exists;
drop table t_order_coffee if exists;

create table t_coffee (
    id bigint auto_increment,
    create_time timestamp,
    update_time timestamp,
    name varchar(255),
    price bigint,
    primary key (id)
);

create table t_order (
    id bigint auto_increment,
    create_time timestamp,
    update_time timestamp,
    customer varchar(255),
    state integer not null,
    primary key (id)
);

create table t_order_coffee (
    coffee_order_id bigint not null,
    items_id bigint not null
);
```

**data.sql:**

```sql
insert into t_coffee (name, price, create_time, update_time) 
    values ('espresso', 10000, now(), now());
insert into t_coffee (name, price, create_time, update_time) 
    values ('latte', 12500, now(), now());
insert into t_coffee (name, price, create_time, update_time) 
    values ('capuccino', 12500, now(), now());
insert into t_coffee (name, price, create_time, update_time) 
    values ('mocha', 15000, now(), now());
insert into t_coffee (name, price, create_time, update_time) 
    values ('macchiato', 15000, now(), now());
```

## API Documentation

### Coffee API

#### 1. Get All Coffees

```bash
curl -X GET http://localhost:8080/coffee/
```

**Sample Response:**

```json
[
    {
        "id": 1,
        "createTime": "2025-10-17T05:41:43.785+00:00",
        "updateTime": "2025-10-17T05:41:43.785+00:00",
        "name": "espresso",
        "price": 100.00
    },
    {
        "id": 2,
        "createTime": "2025-10-17T05:41:43.786+00:00",
        "updateTime": "2025-10-17T05:41:43.786+00:00",
        "name": "latte",
        "price": 125.00
    }
]
```

#### 2. Add Coffee (Form Data - Success)

```bash
curl -X POST http://localhost:8080/coffee/ \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "name=Americano&price=125.00"
```

**Response:** 201 CREATED

#### 3. Add Coffee (Form Data - Validation Error)

```bash
curl -X POST http://localhost:8080/coffee/ \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "name=Americano&price="
```

**Response:** 400 BAD REQUEST (handled by @ResponseStatus)

```json
{
    "timestamp": "2025-10-17T05:42:54.993+00:00",
    "status": 400,
    "error": "Bad Request",
    "message": "No message available",
    "path": "/coffee/"
}
```

#### 4. Add Coffee (JSON - Success)

```bash
curl -X POST http://localhost:8080/coffee/ \
  -H "Content-Type: application/json" \
  -d '{
    "name": "latte",
    "price": 125.00
  }'
```

**Response:** 201 CREATED

#### 5. Add Coffee (JSON - Validation Error)

```bash
curl -X POST http://localhost:8080/coffee/ \
  -H "Content-Type: application/json" \
  -d '{
    "name": "",
    "price": 150.00
  }'
```

**Response:** 400 BAD REQUEST (handled by GlobalControllerAdvice)

```json
{
    "message": "org.springframework.validation.BeanPropertyBindingResult: 1 errors\nField error in object 'newCoffeeRequest' on field 'name': rejected value [null]; codes [NotEmpty.newCoffeeRequest.name,NotEmpty.name,NotEmpty.java.lang.String,NotEmpty]; arguments [org.springframework.context.support.DefaultMessageSourceResolvable: codes [newCoffeeRequest.name,name]; arguments []; default message [name]]; default message [不得是空的]"
}
```

#### 6. Batch Add Coffees (File Upload)

**Create a test file (coffee.txt):**

```
Americano 125.0
Italian 150.0
```

**Upload:**

```bash
curl -X POST http://localhost:8080/coffee/ \
  -H "Content-Type: multipart/form-data" \
  -F "file=@coffee.txt"
```

**Response:** 201 CREATED (list of created coffees)

## Key Components

### FormValidationException

```java
/**
 * Custom exception for form validation errors
 * Automatically handled by Spring via @ResponseStatus
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
@Getter
@AllArgsConstructor
public class FormValidationException extends RuntimeException {
    private final BindingResult result;
}
```

**Flow:**

```
1. Form validation fails in addCoffee()
2. Throw FormValidationException
3. Spring detects @ResponseStatus(BAD_REQUEST)
4. Auto-return 400 with Spring MVC default error format
```

### GlobalControllerAdvice

```java
/**
 * Global exception handler for all controllers
 * Handles ValidationException with custom JSON format
 */
@RestControllerAdvice
public class GlobalControllerAdvice {
    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> validationExceptionHandler(ValidationException exception) {
        Map<String, String> map = new HashMap<>();
        map.put("message", exception.getMessage());
        return map;
    }
}
```

**Flow:**

```
1. JSON validation fails in addJsonCoffee()
2. Throw ValidationException
3. GlobalControllerAdvice.validationExceptionHandler() catches it
4. Return custom JSON: {"message": "..."}
5. HTTP status: 400 Bad Request
```

### CoffeeController

```java
@Controller
@RequestMapping("/coffee")
@Slf4j
public class CoffeeController {
    
    /**
     * Form data validation - uses @ResponseStatus
     * Content-Type: application/x-www-form-urlencoded
     */
    @PostMapping(consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public Coffee addCoffee(@Valid NewCoffeeRequest newCoffee, BindingResult result) {
        if (result.hasErrors()) {
            throw new FormValidationException(result);
        }
        return coffeeService.saveCoffee(newCoffee.getName(), newCoffee.getPrice());
    }
    
    /**
     * JSON request validation - uses GlobalControllerAdvice
     * Content-Type: application/json
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public Coffee addJsonCoffee(@Valid @RequestBody NewCoffeeRequest newCoffee, 
                                BindingResult result) {
        if (result.hasErrors()) {
            throw new ValidationException(result.toString());
        }
        return coffeeService.saveCoffee(newCoffee.getName(), newCoffee.getPrice());
    }
    
    /**
     * Batch file upload
     * Content-Type: multipart/form-data
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public List<Coffee> batchAddCoffee(@RequestParam("file") MultipartFile file) {
        // Read file and create coffees
    }
}
```

### NewCoffeeRequest

```java
@Getter
@Setter
@ToString
public class NewCoffeeRequest {
    @NotEmpty
    private String name;
    
    @NotNull
    private Money price;
}
```

**Validation:**
- `@NotEmpty`: Name cannot be null or empty
- `@NotNull`: Price cannot be null

## Exception Handling Priority

### Priority Order

```
1. Controller-level @ExceptionHandler (Highest)
2. @RestControllerAdvice @ExceptionHandler
3. @ResponseStatus on exception class
4. DefaultHandlerExceptionResolver (Lowest)
```

### Example

**Scenario:** ValidationException is thrown in CoffeeController

```java
// Priority 1: Controller-level handler (if exists)
@Controller
public class CoffeeController {
    @ExceptionHandler(ValidationException.class)
    public Map<String, String> handleValidation(ValidationException ex) {
        // This takes precedence
    }
}

// Priority 2: Global handler (used in this project)
@RestControllerAdvice
public class GlobalControllerAdvice {
    @ExceptionHandler(ValidationException.class)
    public Map<String, String> validationExceptionHandler(ValidationException ex) {
        // This is used if no controller-level handler
    }
}
```

## Testing

### Form Validation Tests

**1. Success Case:**

```bash
curl -v -X POST http://localhost:8080/coffee/ \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "name=Americano&price=125.00"

# Response: 201 CREATED
```

**2. Validation Error (Empty Price):**

```bash
curl -v -X POST http://localhost:8080/coffee/ \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "name=Americano&price="

# Response: 400 BAD REQUEST
# Handled by: @ResponseStatus on FormValidationException
```

### JSON Validation Tests

**1. Success Case:**

```bash
curl -v -X POST http://localhost:8080/coffee/ \
  -H "Content-Type: application/json" \
  -d '{
    "name": "latte",
    "price": 125.00
  }'

# Response: 201 CREATED
```

**2. Validation Error (Empty Name):**

```bash
curl -v -X POST http://localhost:8080/coffee/ \
  -H "Content-Type: application/json" \
  -d '{
    "name": "",
    "price": 150.00
  }'

# Response: 400 BAD REQUEST
# Handled by: GlobalControllerAdvice
```

### File Upload Test

**1. Create test file (coffee.txt):**

```
Americano 125.0
Italian 150.0
```

**2. Upload:**

```bash
curl -v -X POST http://localhost:8080/coffee/ \
  -H "Content-Type: multipart/form-data" \
  -F "file=@coffee.txt"

# Response: 201 CREATED (list of coffees)
```

## Exception Handling Flow

### Form Validation Flow

```
┌─────────────────────────────────────────────────────────────┐
│ 1. Client sends form data                                   │
│    POST /coffee/                                            │
│    Content-Type: application/x-www-form-urlencoded          │
│    Data: name=Americano&price=                              │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│ 2. CoffeeController.addCoffee()                             │
│    - @Valid triggers Bean Validation                        │
│    - BindingResult captures errors                          │
│    - result.hasErrors() = true                              │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│ 3. throw new FormValidationException(result)                │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│ 4. Spring detects @ResponseStatus(BAD_REQUEST)              │
│    on FormValidationException class                         │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│ 5. ResponseStatusExceptionResolver handles it               │
│    Returns Spring MVC default error format                  │
│    HTTP Status: 400 Bad Request                             │
└─────────────────────────────────────────────────────────────┘
```

### JSON Validation Flow

```
┌─────────────────────────────────────────────────────────────┐
│ 1. Client sends JSON data                                   │
│    POST /coffee/                                            │
│    Content-Type: application/json                           │
│    Data: {"name": "", "price": 150.00}                      │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│ 2. CoffeeController.addJsonCoffee()                         │
│    - @Valid triggers Bean Validation                        │
│    - BindingResult captures errors                          │
│    - result.hasErrors() = true                              │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│ 3. throw new ValidationException(result.toString())         │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│ 4. GlobalControllerAdvice catches it                        │
│    @ExceptionHandler(ValidationException.class)             │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│ 5. validationExceptionHandler() processes it                │
│    Returns custom JSON: {"message": "..."}                  │
│    HTTP Status: 400 Bad Request                             │
└─────────────────────────────────────────────────────────────┘
```

## HandlerExceptionResolver Chain

### Spring MVC Exception Resolution

```java
// Spring MVC has multiple exception resolvers (ordered by priority):

1. ExceptionHandlerExceptionResolver
   - Handles @ExceptionHandler annotations
   - Used by GlobalControllerAdvice

2. ResponseStatusExceptionResolver
   - Handles @ResponseStatus annotations
   - Used by FormValidationException

3. DefaultHandlerExceptionResolver
   - Handles Spring MVC built-in exceptions
   - e.g., MethodArgumentNotValidException, HttpMediaTypeNotSupportedException

4. HandlerExceptionResolverComposite
   - Composite of multiple resolvers
```

### How It Works

```
Exception thrown
     ↓
DispatcherServlet catches it
     ↓
Iterate through HandlerExceptionResolver chain
     ↓
First matching resolver handles it
     ↓
Return ModelAndView or direct response
     ↓
Send HTTP response to client
```

## Best Practices

### 1. Choose the Right Strategy

**Use @ResponseStatus when:**
- Simple exception handling
- No need to customize error format
- Exception class is specific to one use case

**Use @RestControllerAdvice when:**
- Need unified error response format
- Multiple controllers need same handling
- Want to customize error details
- Need to handle multiple exception types globally

### 2. Custom Exception Design

```java
// ✅ Recommended: Specific exception with @ResponseStatus
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class CoffeeNotFoundException extends RuntimeException {
    public CoffeeNotFoundException(String name) {
        super("Coffee not found: " + name);
    }
}

// ✅ Recommended: Business exception with error code
public class BusinessException extends RuntimeException {
    private final String errorCode;
    private final String message;
    
    public BusinessException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}

// ❌ Not recommended: Generic RuntimeException
throw new RuntimeException("Error");  // Too generic
```

### 3. Global Exception Handler Design

```java
// ✅ Recommended: Comprehensive global handler
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    // Handle validation exceptions
    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidation(ValidationException ex) {
        return ErrorResponse.builder()
            .code("VALIDATION_ERROR")
            .message(ex.getMessage())
            .timestamp(LocalDateTime.now())
            .build();
    }
    
    // Handle business exceptions
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusiness(BusinessException ex) {
        return ResponseEntity
            .status(ex.getHttpStatus())
            .body(ErrorResponse.from(ex));
    }
    
    // Handle unexpected exceptions
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleUnexpected(Exception ex) {
        log.error("Unexpected error", ex);
        return ErrorResponse.builder()
            .code("INTERNAL_ERROR")
            .message("系統內部錯誤")
            .timestamp(LocalDateTime.now())
            .build();
    }
}
```

### 4. Unified Error Response

```java
// ✅ Recommended: Structured error response
@Data
@Builder
public class ErrorResponse {
    private String code;
    private String message;
    private LocalDateTime timestamp;
    private String path;
    private List<FieldError> fieldErrors;
}

// ❌ Not recommended: Simple string or map
return "Error";  // Too simple
return Map.of("error", "bad request");  // Inconsistent
```

### 5. Production Configuration

```properties
# ✅ Production: Hide sensitive information
server.error.include-message=never
server.error.include-binding-errors=never
server.error.include-stacktrace=never
server.error.include-exception=false
server.error.whitelabel.enabled=false

# ❌ Development only: Show detailed errors
server.error.include-message=always
server.error.include-binding-errors=always
```

### 6. Logging

```java
// ✅ Recommended: Log exceptions appropriately
@ExceptionHandler(BusinessException.class)
public ResponseEntity<ErrorResponse> handleBusiness(BusinessException ex) {
    log.warn("Business error: {}", ex.getMessage());  // WARN for business errors
    return ResponseEntity.badRequest().body(ErrorResponse.from(ex));
}

@ExceptionHandler(Exception.class)
public ResponseEntity<ErrorResponse> handleUnexpected(Exception ex) {
    log.error("Unexpected error", ex);  // ERROR with stack trace
    return ResponseEntity.status(500).body(ErrorResponse.unexpected());
}
```

## Common Issues

### Issue 1: Validation Error Not Caught

**Problem:** Validation exception not handled

**Cause:** Missing `BindingResult` parameter

**Solution:**

```java
// ❌ Wrong: No BindingResult
@PostMapping("/")
public Coffee addCoffee(@Valid @RequestBody NewCoffeeRequest request) {
    // Spring throws MethodArgumentNotValidException automatically
    // Cannot manually handle validation errors
}

// ✅ Correct: With BindingResult
@PostMapping("/")
public Coffee addCoffee(@Valid @RequestBody NewCoffeeRequest request, 
                        BindingResult result) {
    if (result.hasErrors()) {
        // Manual handling
        throw new ValidationException(result.toString());
    }
}
```

### Issue 2: @ResponseStatus Not Working

**Problem:** @ResponseStatus annotation has no effect

**Cause:** Exception caught by higher-priority handler

**Solution:**

```java
// Check exception handling priority:
// 1. Controller @ExceptionHandler (highest)
// 2. @RestControllerAdvice @ExceptionHandler
// 3. @ResponseStatus (this one)
// 4. DefaultHandlerExceptionResolver (lowest)

// If you have @ExceptionHandler for the same exception type,
// it will override @ResponseStatus
```

### Issue 3: Different Error Format Between Form and JSON

**Problem:** Inconsistent error responses

**Explanation:** This is by design in this demo

**Form validation:** Uses @ResponseStatus → Spring MVC default format

```json
{
    "timestamp": "...",
    "status": 400,
    "error": "Bad Request",
    "path": "/coffee/"
}
```

**JSON validation:** Uses GlobalControllerAdvice → Custom format

```json
{
    "message": "..."
}
```

**To unify:** Use GlobalControllerAdvice for both

```java
@RestControllerAdvice
public class GlobalControllerAdvice {
    @ExceptionHandler({ValidationException.class, FormValidationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidation(Exception ex) {
        return ErrorResponse.builder()
            .code("VALIDATION_ERROR")
            .message(ex.getMessage())
            .timestamp(LocalDateTime.now())
            .build();
    }
}
```

### Issue 4: Production Information Leakage

**Problem:** Detailed error messages exposed in production

**Cause:** `server.error.include-message=always`

**Solution:**

```properties
# application-prod.properties
server.error.include-message=never
server.error.include-binding-errors=never
server.error.include-stacktrace=never
server.error.include-exception=false
```

**Global handler:**

```java
@ExceptionHandler(Exception.class)
public ErrorResponse handleUnexpected(Exception ex) {
    log.error("Unexpected error", ex);  // Log detailed error
    
    // Return generic message to client
    return ErrorResponse.builder()
        .code("INTERNAL_ERROR")
        .message("系統內部錯誤，請稍後再試")  // Generic message
        .timestamp(LocalDateTime.now())
        .build();
}
```

## @ResponseStatus vs @ExceptionHandler Comparison

| Aspect | @ResponseStatus | @ExceptionHandler + @RestControllerAdvice |
|--------|-----------------|------------------------------------------|
| **Location** | On exception class | On handler method |
| **Scope** | Global (any controller) | Controller or Global (via @RestControllerAdvice) |
| **Response Format** | Spring MVC default | Fully customizable |
| **Flexibility** | Low | High |
| **Code** | Minimal | Needs handler method |
| **Priority** | Lower | Higher |
| **Use Case** | Simple cases | Complex cases, unified format |
| **This Project** | FormValidationException | ValidationException |

## Best Practices Demonstrated

1. **Custom Exception Classes**: FormValidationException with clear purpose
2. **@ResponseStatus Annotation**: Simple exception handling without extra code
3. **@RestControllerAdvice**: Global exception handling for all controllers
4. **@ExceptionHandler**: Specific exception type handling
5. **BindingResult**: Capture and process validation errors
6. **Validation Annotations**: @NotEmpty, @NotNull for data validation
7. **Content-Type Routing**: Different handling for form vs JSON
8. **HTTP Status Codes**: Appropriate status codes (201, 400)
9. **Logging**: Log validation errors for debugging
10. **Separation of Concerns**: Exception handling separated from business logic

## References

- [Spring MVC Exception Handling](https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-controller/ann-exceptionhandler.html)
- [Spring Boot Error Handling](https://docs.spring.io/spring-boot/docs/current/reference/html/web.html#web.servlet.spring-mvc.error-handling)
- [Jakarta Bean Validation](https://jakarta.ee/specifications/bean-validation/3.0/)
- [HTTP Status Codes](https://developer.mozilla.org/en-US/docs/Web/HTTP/Status)

## License

MIT License - see [LICENSE](LICENSE) file for details.

## About Us

我們主要專注在敏捷專案管理、物聯網（IoT）應用開發和領域驅動設計（DDD）。喜歡把先進技術和實務經驗結合，打造好用又靈活的軟體解決方案。近來也積極結合 AI 技術，推動自動化工作流，讓開發與運維更有效率、更智慧。持續學習與分享，希望能一起推動軟體開發的創新和進步。

## Contact

**風清雲談** - 專注於敏捷專案管理、物聯網（IoT）應用開發和領域驅動設計（DDD）。

- 🌐 官方網站：[風清雲談部落格](https://blog.fengqing.tw/)
- 📘 Facebook：[風清雲談粉絲頁](https://www.facebook.com/profile.php?id=61576838896062)
- 💼 LinkedIn：[Chu Kuo-Lung](https://www.linkedin.com/in/chu-kuo-lung)
- 📺 YouTube：[雲談風清頻道](https://www.youtube.com/channel/UCXDqLTdCMiCJ1j8xGRfwEig)
- 📧 Email：[fengqing.tw@gmail.com](mailto:fengqing.tw@gmail.com)

---

**⭐ 如果這個專案對您有幫助，歡迎給個 Star！**
