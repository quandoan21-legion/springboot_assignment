# Restaurant Dish API (Spring Boot + Spring Data JPA)

REST API quản lý **Danh mục (Category)** và **Món ăn (Dish)**.

## 1. Cấu hình ứng dụng

```properties
server.port=7891

spring.datasource.url=jdbc:mysql://localhost:3306/dishdb2?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=quan
spring.datasource.password=
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

spring.jpa.database-platform=org.hibernate.dialect.MySQL8Dialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

---

## 2. SQL: Tạo database, bảng, và dữ liệu mẫu

```sql
CREATE DATABASE IF NOT EXISTS dishdb2 CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE dishdb2;

CREATE TABLE IF NOT EXISTS categories (
  id   BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(100) NOT NULL UNIQUE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS dishes (
  id                 VARCHAR(64) PRIMARY KEY,
  name               VARCHAR(200) NOT NULL,
  description        TEXT,
  image_url          VARCHAR(500),
  price              DECIMAL(12,2) NOT NULL,
  start_date         DATETIME(6) NOT NULL,
  last_modified_date DATETIME(6) NULL,
  status             ENUM('ON_SALE','STOPPED','DELETED') NOT NULL,
  category_id        BIGINT NOT NULL,
  CONSTRAINT fk_dish_category FOREIGN KEY (category_id) REFERENCES categories(id)
) ENGINE=InnoDB;

INSERT INTO categories (name) VALUES
('Món nướng'),
('Món luộc'),
('Món chay'),
('Đồ uống')
ON DUPLICATE KEY UPDATE name = VALUES(name);
```

---

## 4. API Endpoints

### 4.1 Category

#### Lấy tất cả danh mục
**GET** `/api/v1/categories`

```bash
curl -X GET "http://localhost:7891/api/v1/categories" -H "Accept: application/json"
```

---

### 4.2 Dish

#### 4.2.1 Lấy danh sách món ăn (filter & paging)

**GET** `/api/v1/dishes`

Query params:
- `page`, `limit`, `status`, `sortBy`, `sortDir`, `keyword`, `categoryId`, `minPrice`, `maxPrice`

```bash
curl -G "http://localhost:7891/api/v1/dishes" --data-urlencode "page=1" --data-urlencode "limit=5" --data-urlencode "status=ON_SALE"
```

#### 4.2.2 Xem chi tiết món ăn
**GET** `/api/v1/dishes/{id}`

```bash
curl -X GET "http://localhost:7891/api/v1/dishes/MN001"
```

#### 4.2.3 Thêm món ăn mới
**POST** `/api/v1/dishes`

```json
{
  "name": "Bò bít tết sốt tiêu xanh",
  "description": "Bò Mỹ nhập khẩu...",
  "imageUrl": "https://example.com/images/bo-bit-tet.jpg",
  "price": 250000.0,
  "categoryId": 1
}
```

```bash
curl -X POST "http://localhost:7891/api/v1/dishes" -H "Content-Type: application/json" -d @dish.json
```

#### 4.2.4 Cập nhật món ăn
**PUT** `/api/v1/dishes/{id}`

```json
{
  "name": "Bò bít tết sốt tiêu đen",
  "description": "Bò Mỹ nhập khẩu...",
  "imageUrl": "https://example.com/images/bo-bit-tet-v2.jpg",
  "price": 260000.0,
  "categoryId": 1,
  "status": "STOPPED"
}
```

```bash
curl -X PUT "http://localhost:7891/api/v1/dishes/MN001" -H "Content-Type: application/json" -d @update.json
```

#### 4.2.5 Xoá (soft delete)
**DELETE** `/api/v1/dishes/{id}`

```bash
curl -X DELETE "http://localhost:7891/api/v1/dishes/MN001"
```

---

## 5. Lưu ý

- Bảng `dishes` phải có `image_url`, `start_date`, `last_modified_date`.
- Nếu lỗi `ByteBuddyInterceptor`: thêm `@JsonIgnoreProperties({"hibernateLazyInitializer","handler"})` cho entity Category & Dish.