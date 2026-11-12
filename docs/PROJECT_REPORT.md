# Project Report — Family Books

**Title:** Family Books – Online Bookstore Management System  
**Module:** SE1020 – Object Oriented Programming  
**Institution:** SLIIT  
**Tagline:** Every Story Feels Like Home.

---

## 1. Introduction

Family Books is a full-stack Java web application that simulates a premium heritage bookstore. It combines a polished customer-facing website with an administrative console. Persistence is implemented with Java text-file I/O rather than an external database, aligning with SE1020 file-handling expectations while using Spring Boot as a permitted technology.

## 2. Objectives

1. Implement four complete CRUD modules: Users, Books, Categories, Reviews.
2. Demonstrate encapsulation, inheritance, polymorphism, abstraction, and information hiding.
3. Persist data using Java file reading/writing (UTF-8 text files).
4. Provide secure authentication and role-based authorization.
5. Deliver a responsive, premium vintage UI suitable for presentation.
6. Produce viva-ready documentation and an accurate class diagram.

## 3. System Overview

Public users can browse books, register, sign in, manage their profile, and submit reviews. Administrators manage catalogue inventory, categories, users, and review moderation through a dedicated dashboard.

## 4. Requirements Summary

| Area | Requirement | Implementation |
|---|---|---|
| Language | Java | Java 21 |
| Web | Functional website | Spring MVC + Thymeleaf |
| OOP | 5 core concepts | Book hierarchy + repositories/services |
| Storage | File handling | `data/*.txt` via `FileStorageSupport` |
| Security | Auth + roles | Spring Security + BCrypt |
| CRUD | ≥2–3 ops; project target = 4×4 | All 16 operations |

## 5. Technologies

- Spring Boot 3.3, Spring Security, Validation
- Thymeleaf + Tailwind CDN + custom CSS
- GSAP (subtle motion), SweetAlert2 (confirmations)
- Maven, JUnit 5

## 6. Architecture

Layered monolithic design:

- **Controllers** handle HTTP and views
- **Services** enforce business rules
- **Repository interfaces** abstract persistence
- **File repositories** perform synchronized UTF-8 I/O
- **Models** represent domain entities

## 7. CRUD Functionality

See `docs/CRUD_CHECKLIST.md` for the full 16-operation matrix.

Highlights:

- Users cannot self-assign ADMIN on registration
- Category deletion blocked when books still reference the category
- Review edit/delete restricted to owner unless admin
- Book deletion removes associated reviews

## 8. OOP Concepts

| Concept | Where demonstrated |
|---|---|
| Encapsulation | Validated private fields in model classes |
| Inheritance | `PrintedBook` / `EBook` extend `Book` |
| Polymorphism | `getBookType()`, `getAvailabilityDescription()` via `Book` references |
| Abstraction | Abstract `Book`; repository interfaces |
| Information hiding | File I/O internals; hashed passwords; service-layer rules |

## 9. File Handling

Files are created automatically under `data/`. Updates use temporary-file replacement. Concurrent access is guarded with a read/write lock. Corrupted lines are skipped with a warning rather than crashing the application.

## 10. Testing

Automated tests in `src/test/java` cover:

- User/book/category/review CRUD
- Duplicate prevention
- Review ownership
- Invalid inputs
- Persistence after repository reload
- Book polymorphism

Command: `mvn test`  
Results should be recorded from an actual local run (do not fabricate).

## 11. Challenges

1. Balancing premium UI effort with viva-friendly backend simplicity.
2. Modelling Printed vs E-Book differences without over-engineering.
3. Aligning Spring Boot choice with sample docs that mention JSP/Servlets.
4. Safe text-file updates without a database transaction manager.

## 12. Conclusion

Family Books meets the academic scope: four CRUD modules, strong OOP demonstration, real file persistence, secure authentication, and a presentation-ready bookstore interface. The backend remains explainable for an undergraduate viva.

## 13. Git history (actual)

```
6b555e0 Add academic documentation, class diagram, and viva study guide.
4fc2e24 Add CRUD integration tests and seedable text-file data store.
62e3b55 Build premium vintage Thymeleaf UI for public and admin pages.
ac96303 Wire MVC controllers for storefront, auth, reviews, and admin CRUD.
f410e8b Add services, Spring Security, and demo data initialization.
7467fdc Implement file-based repositories with UTF-8 persistence helpers.
852202d Add domain models with Book inheritance and custom exceptions.
bff81b1 Initialize Spring Boot Family Books project foundation.
```

Refresh with `git log --oneline` before submission if more commits are added.
