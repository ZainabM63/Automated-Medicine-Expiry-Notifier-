## 👩‍💻 Developer

**Author:** Zainab Mughal
---

> *“Ensuring medicine safety through automation — one expiry check at a time.”*

```
---
# 💊 Expiry Management System (Expiry Notifier)

A **pharmacy management system** that automatically monitors and removes **expired** and **near-expiry** products.  
It ensures your inventory remains safe and compliant by performing **scheduled checks twice daily** (8 AM and 8 PM).

---

# Directory to run for backend: \Expiry_MED_NOTIFIER\expiry-mang-backend\expiry-mang\expiry-mang>
# Directory to run for frontend: D:\Expiry_MED_NOTIFIER\expiry-management-frontend\expiry-management-frontend>

---

## ⚙️ Backend Setup (Spring Boot + Oracle DB)

### **1. Prerequisites**
- **Java 17**
- **Maven 3.9+**
- **Oracle 12c Database**

---
```
### **2. Database Configuration (⚠️ Important)**

You must configure your **Oracle database username, password, and connection string** in  
`src/main/resources/application.properties`.

```properties
spring.datasource.url=jdbc:oracle:thin:@localhost:1521:xe
spring.datasource.username=system
spring.datasource.password=youpassword
spring.datasource.driver-class-name=oracle.jdbc.OracleDriver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.Oracle12cDialect
---
```
### **3. Database Schema (SQL Setup)**

Run the following SQL commands inside **Oracle SQL Developer** or your Oracle terminal to create the schema and populate sample data.

```sql
-- Create PRODUCT table
CREATE TABLE PRODUCT ( 
   name VARCHAR2(255),
   batch_number VARCHAR2(50) PRIMARY KEY,
   expiry_date DATE
);

-- View all products
SELECT * FROM PRODUCT;
SELECT * FROM v$database;

-- Insert sample data
INSERT INTO PRODUCT(name, batch_number, expiry_date) VALUES ('bjjhf', 'B030', TO_DATE('2024-12-15', 'YYYY-MM-DD'));
INSERT INTO PRODUCT(name, batch_number, expiry_date) VALUES ('erug', 'B010', TO_DATE('12-12-2024', 'DD-MM-YYYY'));
INSERT INTO PRODUCT(name, batch_number, expiry_date) VALUES ('fjjawg', 'B001', TO_DATE('2024-12-15', 'YYYY-MM-DD'));

DELETE FROM PRODUCT WHERE Batch_Number='B390' OR Batch_Number='B015';
COMMIT;

-- Example medicines
INSERT INTO PRODUCT(name, batch_number, expiry_date) VALUES ('Advant® (Candesartan cilexetil)', 'B089', TO_DATE('2024-12-14', 'YYYY-MM-DD'));
INSERT INTO PRODUCT(name, batch_number, expiry_date) VALUES ('Advantec® (Candesartan cilexetil + Hydrochlorothiazide)', 'B002', TO_DATE('2024-12-13', 'YYYY-MM-DD'));
INSERT INTO PRODUCT(name, batch_number, expiry_date) VALUES ('Agnar® (Calcium + Vitamin D + Vitamin K2)', 'B003', TO_DATE('2024-12-14', 'YYYY-MM-DD'));
INSERT INTO PRODUCT(name, batch_number, expiry_date) VALUES ('Amclav® (Amoxicillin trihydrate + Clavulanate potassium)', 'B004', TO_DATE('2024-12-13', 'YYYY-MM-DD'));
INSERT INTO PRODUCT(name, batch_number, expiry_date) VALUES ('Apixaget® (Apixaban)', 'B005', TO_DATE('2024-12-14', 'YYYY-MM-DD'));

INSERT INTO PRODUCT(name, batch_number, expiry_date) VALUES ('Artheget-DS® (Artemether + Lumefantrine)', 'B009', TO_DATE('2024-11-15', 'YYYY-MM-DD'));
INSERT INTO PRODUCT(name, batch_number, expiry_date) VALUES ('Artheget-EZ® (Artemether + Lumefantrine)', 'B010', TO_DATE('2024-10-20', 'YYYY-MM-DD'));
INSERT INTO PRODUCT(name, batch_number, expiry_date) VALUES ('Basagine™ Pen (Insulin Glargine)', 'B011', TO_DATE('2024-09-30', 'YYYY-MM-DD'));
INSERT INTO PRODUCT(name, batch_number, expiry_date) VALUES ('Bekson Forte HFA® (Beclomethasone dipropionate)', 'B012', TO_DATE('2024-08-10', 'YYYY-MM-DD'));
INSERT INTO PRODUCT(name, batch_number, expiry_date) VALUES ('jghc (Beclomethasone)', 'B000', TO_DATE('2024-09-15', 'YYYY-MM-DD'));

-- Near expiry and fresh products
INSERT INTO PRODUCT(name, batch_number, expiry_date) VALUES ('Cefaloget™ (Cefaclor)', 'B015', TO_DATE('2025-01-01', 'YYYY-MM-DD'));
INSERT INTO PRODUCT(name, batch_number, expiry_date) VALUES ('Cefiget® (Cefixime trihydrate)', 'B016', TO_DATE('2025-01-15', 'YYYY-MM-DD'));
INSERT INTO PRODUCT(name, batch_number, expiry_date) VALUES ('Cefoperz™ (Cefoperazone + Sulbactam)', 'B017', TO_DATE('2025-02-01', 'YYYY-MM-DD'));
COMMIT;

-- Stored Procedure for inserting new products
CREATE OR REPLACE PROCEDURE INSERT_PRODUCT(
  p_name          IN VARCHAR2,
  p_batch_number  IN VARCHAR2,
  p_expiry_date   IN DATE
) AS
BEGIN
  INSERT INTO PRODUCT (name, batch_number, expiry_date)
  VALUES (p_name, p_batch_number, p_expiry_date);
  COMMIT;
END;
/

-- Example usage of the procedure
BEGIN
  INSERT_PRODUCT(
    p_name => 'Product A',
    p_batch_number => 'B12345',
    p_expiry_date => TO_DATE('2025-12-31', 'YYYY-MM-DD')
  );
END;
/

-- Additional data (near expiry and expired)
INSERT INTO PRODUCT (NAME, BATCH_NUMBER, EXPIRY_DATE)
VALUES ('Paracetamol 500mg', 'B091', TO_DATE('2026-03-15', 'YYYY-MM-DD'));

INSERT INTO PRODUCT (NAME, BATCH_NUMBER, EXPIRY_DATE)
VALUES ('Amoxicillin 250mg', 'B002', TO_DATE('2026-05-10', 'YYYY-MM-DD'));

-- Near-expiry products (within 2 months)
INSERT INTO PRODUCT (NAME, BATCH_NUMBER, EXPIRY_DATE)
VALUES ('Ibuprofen 200mg', 'B07654', TO_DATE('2025-11-25', 'YYYY-MM-DD'));
INSERT INTO PRODUCT (NAME, BATCH_NUMBER, EXPIRY_DATE)
VALUES ('Vitamin C 500mg', 'B004', TO_DATE('2025-12-10', 'YYYY-MM-DD'));
INSERT INTO PRODUCT (NAME, BATCH_NUMBER, EXPIRY_DATE)
VALUES ('Cetirizine 10mg', 'B005', TO_DATE('2025-11-30', 'YYYY-MM-DD'));

-- Expired product
INSERT INTO PRODUCT (NAME, BATCH_NUMBER, EXPIRY_DATE)
VALUES ('Cough Syrup 100ml', 'B006', TO_DATE('2025-08-20', 'YYYY-MM-DD'));

COMMIT;
```

---

### **4. Run the Backend**

```bash
cd expiry-mang
mvn spring-boot:run
```

> The backend server starts at: **[http://localhost:8080](http://localhost:8080)**

---

## 💻 Frontend Setup (React)

### **1. Prerequisites**

* **Node.js** and **npm**

### **2. Install Dependencies**

```bash
cd expiry-management-frontend
npm install
```

### **3. Run the Frontend**

```bash
npm start
```

> React app runs on **[http://localhost:3000](http://localhost:3000)**

---

## 🔁 System Features

* ✅ Automatically deletes **expired** and **near-expiry** products
* ⏰ Scheduled checks **twice daily** (8 AM & 8 PM)
* 💾 Integrated with **Oracle 12c**
* 🌐 RESTful API + WebSocket-based updates
* ⚡ Smooth React frontend with real-time updates

---

## 🧩 Technologies Used

| Layer             | Technology           |
| ----------------- | -------------------- |
| **Backend**       | Spring Boot 3.3.5    |
| **Database**      | Oracle 12c           |
| **Frontend**      | React (Node.js, npm) |
| **Language**      | Java 17              |
| **Build Tool**    | Maven                |
| **Communication** | REST API, WebSockets |

---

## ⚠️ Notes

* Always **configure Oracle credentials carefully** — avoid committing them to GitHub.
* Use the **provided SQL script** to initialize your database.
* The scheduler automatically handles product expiry without manual deletion.

---

