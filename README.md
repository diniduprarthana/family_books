# Family Books – Online Bookstore Management System

**Every Story Feels Like Home.**

Academic full-stack Java project for **SE1020 – Object Oriented Programming** (SLIIT).

A premium vintage bookstore website with four complete CRUD modules, Spring Security authentication, Java text-file persistence, and demonstrable OOP concepts.

---

## Features

- Public storefront: Home, Collection, Book Details, About
- Secure registration, login, logout (CUSTOMER / ADMIN)
- User profile management (view, update, password change, delete)
- Admin dashboard with live statistics
- Book management (PrintedBook + EBook polymorphism)
- Category management with delete protection
- Review management with ownership checks
- Search, category filter, and sorting
- UTF-8 text-file storage (no external database)

---

## Technology Stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3.3, Spring MVC, Spring Security |
| Views | Thymeleaf |
| Frontend | HTML5, Tailwind CSS (CDN), custom CSS, vanilla JS, GSAP, SweetAlert2 |
| Storage | Local text files via Java NIO (`Buffered`/`Files` API) |
| Build | Maven |
| IDE | IntelliJ IDEA |

---

## Requirements

- JDK 21+
- Maven 3.9+ (or IntelliJ bundled Maven)
- Windows / macOS / Linux

---

## How to Run

```bash
# From the project root
mvn spring-boot:run
```

Or in IntelliJ IDEA:

1. Open the `Family_Books` folder as a Maven project
2. Wait for dependencies to resolve
3. Run `com.familybooks.FamilyBooksApplication`

Then open: [http://localhost:8080](http://localhost:8080)

---

## Demo Accounts

| Role | Username | Password |
|---|---|---|
| Admin | `admin` | `Admin@123` |
| Customer | `amaya` | `Customer1` |
| Customer | `nimal` | `Customer1` |

Passwords are stored as **BCrypt hashes** only (never plaintext).

Demo data is seeded automatically on first start if files are empty / missing seed records.

---

## Text File Storage

Data directory: `data/` (project working directory)

| File | Contents |
|---|---|
| `users.txt` | id\|username\|email\|passwordHash\|fullName\|role\|createdAt |
| `books.txt` | id\|title\|author\|isbn\|categoryId\|description\|price\|coverImage\|bookType\|typeData |
| `categories.txt` | id\|name\|description |
| `reviews.txt` | id\|bookId\|userId\|rating\|reviewText\|createdAt |

Architecture flow:

`Browser → Controller → Service → Repository → Text File`

---

## Project Structure

```
src/main/java/com/familybooks/
  FamilyBooksApplication.java
  config/          Security + data seeding
  controller/      MVC controllers
  model/           Domain entities + Book hierarchy
  repository/      Interfaces
  repository/file/ Text-file implementations
  service/         Business logic
  security/        UserDetails integration
  exception/       Custom exceptions
  util/            ID generation
```

---

## Documentation

See the `docs/` folder:

- `PROJECT_REPORT.md` – academic report
- `CLASS_DIAGRAM.md` – UML (Mermaid)
- `VIVA_GUIDE.md` – Sinhala + English viva prep
- `CRUD_CHECKLIST.md` – all 16 CRUD operations
- `REQUIREMENTS_COMPLIANCE.md` – marking map + lecturer confirmation items

---

## Tests

```bash
mvn test
```

---

## GitHub

```bash
git init
git add .
git commit -m "Initial Family Books implementation"
# Create a GitHub repo, then:
git remote add origin https://github.com/<your-username>/Family_Books.git
git push -u origin main
```

Do not fabricate contribution history. Commit real milestones as you work.

---

## Academic Note

The SE1020 sample materials mention JSP/Servlets in places while also listing Spring Boot as permitted. This project uses **Spring Boot + Thymeleaf** as the chosen permitted approach. Confirm with your lecturer if your marking sheet expects JSP/Servlets specifically.
