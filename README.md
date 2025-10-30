# Restaurant Dish API - Postman Guide

REST API quản lý **Danh mục (Category)** và **Món ăn (Dish)** sử dụng Spring Boot + MySQL.

---

## 📋 Table of Contents
- [Cấu hình](#cấu-hình)
- [Setup Database](#setup-database)
- [API Endpoints](#api-endpoints)
    - [Categories](#1-categories)
    - [Dishes](#2-dishes)

---

## ⚙️ Cấu hình

**Base URL:** `http://localhost:7891/api/v1`

**application.properties:**
```properties
server.port=7891

spring.datasource.url=jdbc:mysql://localhost:3306/dishdb2?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=quan
spring.datasource.password=
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

spring.jpa.database-platform=org.hibernate.dialect.MySQL8Dialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

---

## 🗄️ Setup Database

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

## 🔌 API Endpoints

### 1. Categories

#### 📌 Lấy tất cả danh mục

**Method:** `GET`  
**URL:** `http://localhost:7891/api/v1/categories`

**Postman Setup:**
1. Method: `GET`
2. URL: `http://localhost:7891/api/v1/categories`
3. Headers: `Accept: application/json`
4. Click **Send**

**Response Example:**
```json
[
  {
    "id": 1,
    "name": "Món nướng"
  },
  {
    "id": 2,
    "name": "Món luộc"
  }
]
```

---

### 2. Dishes

#### 📌 1. Lấy danh sách món ăn (có phân trang & filter)

**Method:** `GET`  
**URL:** `http://localhost:7891/api/v1/dishes`

**Query Parameters:**

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `page` | int | 1 | Trang hiện tại (>= 1) |
| `limit` | int | 5 | Số bản ghi/trang (>= 1) |
| `status` | string | ON_SALE | Trạng thái: `ON_SALE`, `STOPPED` |
| `keyword` | string | - | Tìm theo tên hoặc mô tả |
| `categoryId` | long | - | Lọc theo danh mục |
| `minPrice` | decimal | - | Giá tối thiểu |
| `maxPrice` | decimal | - | Giá tối đa |
| `sortBy` | string | startDate | Sắp xếp: `name`, `price`, `startDate` |
| `sortDir` | string | desc | Chiều: `asc` hoặc `desc` |

**Postman Setup:**
1. Method: `GET`
2. URL: `http://localhost:7891/api/v1/dishes`
3. Tab **Params**, thêm:
    - `page` = `1`
    - `limit` = `10`
    - `status` = `ON_SALE`
    - `sortBy` = `price`
    - `sortDir` = `asc`

**Examples:**

**Ví dụ 1:** Lấy tất cả món đang bán, sắp xếp theo giá tăng dần
```
GET http://localhost:7891/api/v1/dishes?page=1&limit=10&status=ON_SALE&sortBy=price&sortDir=asc
```

**Ví dụ 2:** Tìm kiếm món có từ "bò", giá từ 100k-300k
```
GET http://localhost:7891/api/v1/dishes?keyword=bò&minPrice=100000&maxPrice=300000
```

**Ví dụ 3:** Lọc theo danh mục "Món nướng" (categoryId = 1)
```
GET http://localhost:7891/api/v1/dishes?categoryId=1
```

**Response Example:**
```json
{
  "content": [
    {
      "id": "MN001",
      "name": "Bò bít tết",
      "description": "Bò Mỹ nhập khẩu",
      "imageUrl": "https://example.com/bo-bit-tet.jpg",
      "price": 250000.00,
      "startDate": "2025-01-15T10:00:00",
      "lastModifiedDate": null,
      "status": "ON_SALE",
      "category": {
        "id": 1,
        "name": "Món nướng"
      }
    }
  ],
  "page": 1,
  "size": 10,
  "totalElements": 1,
  "totalPages": 1
}
```

---

#### 📌 2. Xem chi tiết món ăn

**Method:** `GET`  
**URL:** `http://localhost:7891/api/v1/dishes/{id}`

**Postman Setup:**
1. Method: `GET`
2. URL: `http://localhost:7891/api/v1/dishes/MN001`
3. Click **Send**

**Response Example:**
```json
{
  "id": "MN001",
  "name": "Bò bít tết sốt tiêu xanh",
  "description": "Bò Mỹ nhập khẩu",
  "imageUrl": "https://example.com/bo-bit-tet.jpg",
  "price": 250000.00,
  "startDate": "2025-01-15T10:00:00",
  "lastModifiedDate": null,
  "status": "ON_SALE",
  "category": {
    "id": 1,
    "name": "Món nướng"
  }
}
```

---

#### 📌 3. Thêm món ăn mới

**Method:** `POST`  
**URL:** `http://localhost:7891/api/v1/dishes`

**Postman Setup:**
1. Method: `POST`
2. URL: `http://localhost:7891/api/v1/dishes`
3. Tab **Headers**:
    - `Content-Type` = `application/json`
4. Tab **Body** → chọn **raw** → chọn **JSON**
5. Paste JSON dưới đây:

**Request Body:**
```json
{
  "name": "Bò bít tết sốt tiêu xanh",
  "description": "Bò Mỹ nhập khẩu, thịt thăn mềm, ướp gia vị đặc biệt",
  "imageUrl": "https://example.com/images/bo-bit-tet.jpg",
  "price": 250000.0,
  "categoryId": 1
}
```

**Response Example:**
```json
{
  "id": "MN001",
  "name": "Bò bít tết sốt tiêu xanh",
  "description": "Bò Mỹ nhập khẩu, thịt thăn mềm, ướp gia vị đặc biệt",
  "imageUrl": "https://example.com/images/bo-bit-tet.jpg",
  "price": 250000.00,
  "startDate": "2025-10-29T08:30:00",
  "lastModifiedDate": null,
  "status": "ON_SALE",
  "category": {
    "id": 1,
    "name": "Món nướng"
  }
}
```

---

#### 📌 4. Cập nhật món ăn

**Method:** `PUT`  
**URL:** `http://localhost:7891/api/v1/dishes/{id}`

**Postman Setup:**
1. Method: `PUT`
2. URL: `http://localhost:7891/api/v1/dishes/MN001`
3. Tab **Headers**:
    - `Content-Type` = `application/json`
4. Tab **Body** → **raw** → **JSON**

**Request Body:**
```json
{
  "name": "Bò bít tết sốt tiêu đen",
  "description": "Bò Mỹ nhập khẩu, nâng cấp với sốt tiêu đen đặc biệt",
  "imageUrl": "https://example.com/images/bo-bit-tet-v2.jpg",
  "price": 260000.0,
  "categoryId": 1,
  "status": "ON_SALE"
}
```

**Response:** Trả về món ăn đã được cập nhật

---

#### 📌 5. Xóa món ăn (Soft Delete)

**Method:** `DELETE`  
**URL:** `http://localhost:7891/api/v1/dishes/{id}`

**Postman Setup:**
1. Method: `DELETE`
2. URL: `http://localhost:7891/api/v1/dishes/MN001`
3. Click **Send**

**Response:**
```json
{
  "message": "Dish deleted successfully"
}
```

**Lưu ý:** Đây là soft delete, món ăn sẽ được đánh dấu `status = DELETED` thay vì xóa hẳn khỏi database.

---

## 🔍 Status Values

| Status | Description |
|--------|-------------|
| `ON_SALE` | Món đang bán |
| `STOPPED` | Ngừng bán tạm thời |
| `DELETED` | Đã xóa (soft delete) |

---

## 🐛 Troubleshooting

### Lỗi ByteBuddyInterceptor
Thêm annotation vào Entity:
```java
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Category { ... }

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Dish { ... }
```

### Lỗi kết nối MySQL
- Kiểm tra MySQL đang chạy: `sudo systemctl status mysql`
- Kiểm tra username/password trong `application.properties`
- Kiểm tra database đã tạo: `SHOW DATABASES;`

---

## 📦 Import Postman Collection

Tạo file `Restaurant_API.postman_collection.json` với nội dung:

```json
{
  "info": {
    "name": "Restaurant Dish API",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
  },
  "item": [
    {
      "name": "Categories",
      "item": [
        {
          "name": "Get All Categories",
          "request": {
            "method": "GET",
            "url": "http://localhost:7891/api/v1/categories"
          }
        }
      ]
    },
    {
      "name": "Dishes",
      "item": [
        {
          "name": "Get All Dishes",
          "request": {
            "method": "GET",
            "url": {
              "raw": "http://localhost:7891/api/v1/dishes?page=1&limit=10&status=ON_SALE",
              "query": [
                {"key": "page", "value": "1"},
                {"key": "limit", "value": "10"},
                {"key": "status", "value": "ON_SALE"}
              ]
            }
          }
        },
        {
          "name": "Get Dish By ID",
          "request": {
            "method": "GET",
            "url": "http://localhost:7891/api/v1/dishes/MN001"
          }
        },
        {
          "name": "Create Dish",
          "request": {
            "method": "POST",
            "header": [{"key": "Content-Type", "value": "application/json"}],
            "url": "http://localhost:7891/api/v1/dishes",
            "body": {
              "mode": "raw",
              "raw": "{\n  \"name\": \"Bò bít tết sốt tiêu xanh\",\n  \"description\": \"Bò Mỹ nhập khẩu\",\n  \"imageUrl\": \"https://example.com/bo-bit-tet.jpg\",\n  \"price\": 250000.0,\n  \"categoryId\": 1\n}"
            }
          }
        },
        {
          "name": "Update Dish",
          "request": {
            "method": "PUT",
            "header": [{"key": "Content-Type", "value": "application/json"}],
            "url": "http://localhost:7891/api/v1/dishes/MN001",
            "body": {
              "mode": "raw",
              "raw": "{\n  \"name\": \"Bò bít tết sốt tiêu đen\",\n  \"description\": \"Bò Mỹ nhập khẩu\",\n  \"imageUrl\": \"https://example.com/bo-bit-tet-v2.jpg\",\n  \"price\": 260000.0,\n  \"categoryId\": 1,\n  \"status\": \"ON_SALE\"\n}"
            }
          }
        },
        {
          "name": "Delete Dish",
          "request": {
            "method": "DELETE",
            "url": "http://localhost:7891/api/v1/dishes/MN001"
          }
        }
      ]
    }
  ]
}
```

**Import vào Postman:**
1. Mở Postman
2. Click **Import** (góc trên bên trái)
3. Chọn file JSON trên
4. Bắt đầu test API!

---
