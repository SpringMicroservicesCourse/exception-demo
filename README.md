# SpringBucks Waiter Service ⚡

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

## 專案介紹

本專案為「SpringBucks 咖啡訂單服務」範例，展示如何以 Spring Boot 3.x、JPA、驗證、快取等技術，實作一個 RESTful 微服務。適合學習 Spring 生態系統、DDD 分層設計、資料驗證與例外處理。

- 提供咖啡品項查詢、訂單建立等 API
- 展示資料庫操作、物件映射、驗證與例外處理
- 適合台灣軟體團隊、Java/Spring 初學者與進階工程師

> 💡 **為什麼選擇此專案？**
> - 完整範例，涵蓋 Controller、Service、Repository 分層
> - 採用台灣常用專業術語與註解，團隊溝通無障礙
> - 具備實務常見的驗證、例外處理、快取等設計

### 🎯 專案特色

- 清楚分層架構，易於維護與擴充
- 重要程式碼區塊皆有詳細註解，方便團隊理解
- 完整示範 Spring Boot 3.x 與 Jakarta EE 整合

## 技術棧

### 核心框架
- **Spring Boot 3.4.5** － 微服務應用主體框架
- **Spring Data JPA** － 資料庫 ORM 映射
- **Hibernate Validator (Jakarta Validation)** － 參數驗證
- **Spring Cache** － 快取機制

### 開發工具與輔助
- **Lombok** － 精簡 Java POJO 程式碼
- **Joda-Money** － 處理金額型別
- **H2 Database** － 內嵌測試資料庫
- **Maven** － 專案建置與依賴管理

## 專案結構

```
exception-demo/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── tw/fengqing/spring/springbucks/waiter/
│   │   │       ├── controller/   # 控制器層，處理 API 請求
│   │   │       ├── model/        # 實體與 Enum 定義
│   │   │       ├── repository/   # JPA 資料存取
│   │   │       ├── service/      # 商業邏輯
│   │   │       └── support/      # 格式化、序列化等輔助
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── schema.sql
│   │       └── data.sql
│   └── test/
│       └── java/
│           └── tw/fengqing/spring/springbucks/waiter/
├── pom.xml
└── README.md
```

## 快速開始

### 前置需求
- Java 21 (建議使用 OpenJDK 21)
- Maven 3.8+

### 安裝與執行

1. **克隆此倉庫：**
```bash
git clone https://github.com/SpringMicroservicesCourse/exception-demo.git
```

2. **進入專案目錄：**
```bash
cd exception-demo
```

3. **編譯專案：**
```bash
mvn clean package
```

4. **執行應用程式：**
```bash
mvn spring-boot:run
```

## 進階說明

### 環境變數
```properties
# 必要設定（如需連接外部資料庫）
DB_URL=jdbc:postgresql://localhost:5432/dbname
API_KEY=your-api-key

# 選用設定
LOG_LEVEL=INFO
```

### 設定檔說明
```properties
# application.properties 主要設定
spring.datasource.url=${DB_URL}
spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.show_sql=true
spring.jpa.properties.hibernate.format_sql=true
```

## 參考資源

- [Spring Boot 官方文件](https://spring.io/projects/spring-boot)
- [Spring Data JPA 官方文件](https://spring.io/projects/spring-data-jpa)
- [Hibernate Validator 官方文件](https://hibernate.org/validator/)
- [Joda-Money 官方文件](https://www.joda.org/joda-money/)

## 注意事項與最佳實踐

### ⚠️ 重要提醒

| 項目   | 說明           | 建議做法         |
|--------|----------------|------------------|
| 安全性 | 密碼與金鑰處理 | 使用環境變數     |
| 效能   | 資料庫連線     | 使用連線池       |
| 註解   | 重要程式區塊   | 請務必加上中文註解 |
| 專業用語 | 文件與註解     | 採用台灣常用術語 |

### 🔒 最佳實踐指南

- 重要邏輯、複雜流詳細註解，方便團隊維護
- 參數驗證與例外處理請統一於 Controller 層處理
- 配置檔與敏感資訊請勿硬編於程式，建議用環境變數

## 授權說明

本專案採用 MIT 授權條款，詳見 LICENSE 檔案。

## 關於我們

我們專注於敏捷專案管理、物聯網（IoT）應用開發與領域驅動設計（DDD），致力於將先進技術與實務經驗結合，打造好用又靈活的軟體解決方案。

## 聯繫我們

- **FB 粉絲頁**：[風清雲談 | Facebook](https://www.facebook.com/profile.php?id=61576838896062)
- **LinkedIn**：[linkedin.com/in/chu-kuo-lung](https://www.linkedin.com/in/chu-kuo-lung)
- **YouTube 頻道**：[雲談風清 - YouTube](https://www.youtube.com/channel/UCXDqLTdCMiCJ1j8xGRfwEig)
- **風清雲談 部落格**：[風清雲談](https://blog.fengqing.tw/)
- **電子郵件**：[fengqing.tw@gmail.com](mailto:fengqing.tw@gmail.com)

---

**📅 最後更新：[2025-07-16]**  
**👨‍💻 維護者：[風清雲談團隊]** 

## Spring MVC 異常處理說明

在 Spring MVC 中，異常處理主要依賴 `HandlerExceptionResolver` 這個核心介面及其多個實作類別。常見的有：
- `ExceptionHandlerExceptionResolver`
- `ResponseStatusExceptionResolver`
- `DefaultHandlerExceptionResolver`
- `HandlerExceptionResolverComposite`

### ResponseStatus 註解
你可以在自訂的 Exception 類別上加上 `@ResponseStatus`，例如：
```java
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class FormValidationException extends RuntimeException { ... }
```
這樣當拋出此例外時，Spring 會自動回傳對應的 HTTP 狀態碼（如 400 Bad Request）。

### ExceptionHandler 註解
你可以在 Controller 或 `@RestControllerAdvice` 類別中，撰寫帶有 `@ExceptionHandler` 的方法，專門攔截並處理特定例外。例如：
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
這樣所有 Controller 拋出的 `ValidationException` 都會被這個 Advice 處理，並以 JSON 格式回應。

### 實務範例說明
- 在 `CoffeeController` 的表單提交（`addCoffee`）時，若驗證失敗，會拋出自訂的 `FormValidationException`，此例外類別有 `@ResponseStatus`，Spring 會自動回傳 400。
- 在 JSON 請求（`addJsonCoffee`）時，若驗證失敗，則直接拋出 `ValidationException`，由全域的 `GlobalControllerAdvice` 統一處理，回傳 400 及錯誤訊息。
- 這種設計讓表單與 JSON 請求都能有一致且明確的錯誤回應，方便前端與團隊成員理解。

### 設計原則與建議
- 建議將複雜或全域的異常處理統一寫在 `@RestControllerAdvice`，方便維護與擴充。
- 重要的自訂 Exception 請加上 `@ResponseStatus`，明確標示 HTTP 狀態。
- `@ExceptionHandler` 方法可回傳多種型別（如 Map、物件、字串），彈性高。
- `@RestControllerAdvice` 的優先級低於 Controller 內部的 `@ExceptionHandler`，可根據需求選擇設計層級。

### 實測說明
- 表單請求驗證失敗時，會直接回傳 400，並由 Spring MVC 處理。
- JSON 請求驗證失敗時，會由全域 Advice 處理，回傳 400 及自訂訊息。

這些設計讓專案異常處理更有彈性、可讀性與團隊協作性。 