# Viva Guide — Family Books (Sinhala + English)

SE1020 Object Oriented Programming · Beginner-friendly study sheet based on **this project's actual code**.

---

## 1. Project one-liner

**EN:** Family Books is a Spring Boot online bookstore with four CRUD modules, text-file storage, and Spring Security login.  
**SI:** Family Books කියන්නේ Spring Boot එකකින් හදපු online bookstore එකක්. CRUD modules හතරක් තියෙනවා, data ටික text files වල save වෙනවා, login එක Spring Security එකෙන්.

---

## 2. Architecture (explain with confidence)

```
Browser → Controller → Service → Repository → data/*.txt
```

| Layer | Example class | Job |
|---|---|---|
| Controller | `BookController` | HTTP request/response + Thymeleaf pages |
| Service | `BookService` | Business rules / validation |
| Repository interface | `BookRepository` | Abstraction for storage |
| File repository | `FileBookRepository` | Actual read/write of `books.txt` |
| Model | `Book`, `User`, `Category`, `Review` | Domain data |

**Why controllers don't touch files:** Information hiding + clean OOP. Easy viva line: “Controller calls Service; Service calls Repository; only File*Repository knows about text files.”

---

## 3. OOP Concepts (40 marks focus)

### Encapsulation
Private fields + getters/setters with validation.  
Example: `User.setEmail(...)` rejects blank/invalid email.

### Inheritance
`PrintedBook extends Book`, `EBook extends Book`.  
Shared fields live in `Book`; type-specific fields live in subclasses.

### Polymorphism
Method called on a `Book` reference, but behaviour depends on real object type:

```java
Book book = bookRepository.findById(id).get();
book.getBookType();                 // PRINTED or EBOOK
book.getAvailabilityDescription();  // stock text OR digital download text
```

Also used in admin dashboard and `BookService.describeAvailabilityForAll()`.

### Abstraction
1. Abstract class `Book` with abstract methods.  
2. Interfaces like `UserRepository`, `BookRepository`.

### Information Hiding
- Password stored only as hash (`passwordHash`)
- File locking/parsing helpers are private/internal in `FileStorageSupport`
- Controllers never open `users.txt` directly

---

## 4. Key classes (short answers)

### `Book` (abstract)
Base catalogue item. Forces subclasses to implement type + availability methods.

### `PrintedBook`
Has `stockQuantity` and `pageCount`. Availability depends on stock.

### `EBook`
Has `digitalFormat` and `downloadAccess`. Always digitally available if format is set.

### `User`
Account entity with `UserRole` (`CUSTOMER` / `ADMIN`). Password is BCrypt hash.

### `Category`
Stable `id` (e.g. `CAT-FICTION`) — not identified only by name.

### `Review`
Links `bookId` + `userId`. `isOwnedBy()` supports permission checks.

### `FileStorageSupport`
UTF-8 read/write, skip comments, escape `|`, write via temp file + replace, read/write lock.

### `SecurityConfig`
Form login, logout, CSRF on, `/admin/**` needs `ROLE_ADMIN`.

### `CustomUserDetails` / `CustomUserDetailsService`
Bridge between our `User` model and Spring Security.

### `DataInitializer`
Seeds demo admin/customers/books/categories/reviews without duplicating IDs.

---

## 5. File handling (explain step by step)

**Read**
1. `Files.readAllLines(..., UTF_8)`
2. Skip empty lines and `#` comments
3. Split by `|`
4. Build model objects (for books: choose `PrintedBook` or `EBook` from `bookType`)

**Write / Update**
1. Load all records into a `List`
2. Replace or add the target object
3. Write to `*.tmp`
4. Atomically move temp file over the real file

**Delete**
Filter the list, then rewrite the file.

---

## 6. CRUD workflows (say these exactly)

### User CREATE
Register form → `AuthController` → `UserService.register` → hash password → `FileUserRepository.save` → `users.txt`

### Book READ
`/books` → `BookService.searchFilterSort` → Thymeleaf collection page

### Category DELETE
Admin clicks delete → service checks if any book uses category → if yes, throw `InvalidOperationException` with message

### Review UPDATE
Only owner or admin → `ReviewService.update` → ownership assert → save

---

## 7. Likely viva Q&A

**Q: Why abstract Book?**  
A: Printed and e-books share core data but differ in availability behaviour. Abstract methods force each subclass to define that behaviour.

**Q: Where is polymorphism used in the running app?**  
A: Anywhere a `Book` reference calls `getAvailabilityDescription()` / `getBookType()` — book cards, details page, admin tables, and `describeAvailabilityForAll()`.

**Q: How are passwords stored?**  
A: BCrypt hash via `PasswordEncoder`. Never plaintext in `users.txt`.

**Q: Can a customer become admin from the register form?**  
A: No. `register()` always sets `UserRole.CUSTOMER`. Only admin update can change roles.

**Q: What happens to reviews if a book is deleted?**  
A: `BookService.delete` calls `reviewRepository.deleteByBookId`.

**Q: Why Spring Boot instead of JSP/Servlets sample?**  
A: Project description lists Spring Boot as permitted. We documented this and should confirm with the lecturer if the marking sheet expects JSP specifically.

**Q: How do you prevent file corruption when two writes happen?**  
A: `ReentrantReadWriteLock` in `FileStorageSupport` + temp-file replacement.

**Q: Show encapsulation.**  
A: Open `User.java` / `Book.java` — private fields, validated setters.

---

## 8. Sinhala quick lines (memorise)

- **Encapsulation:** data private තියලා getters/setters එක්ක protect කරන එක.
- **Inheritance:** common code `Book` එකේ, special code subclass වල.
- **Polymorphism:** same method name, different behaviour according to object type.
- **Abstraction:** unnecessary details hide කරලා important idea එක විතරක් පෙන්නන එක (abstract class / interface).
- **File handling:** Java NIO එකෙන් UTF-8 text files read/write කරනවා.

---

## 9. Demo script (5 minutes)

1. Open homepage — show design + featured books  
2. Collection — search/filter  
3. Register/login as customer — add review  
4. Login as `admin` / `Admin@123`  
5. Admin dashboard stats  
6. Add/edit/delete a book  
7. Try deleting a category that still has books — show error message  
8. Open `data/users.txt` — show hashed password  
9. Open class diagram + point to inheritance

---

## 10. Classes to open during viva

1. `Book.java` + `PrintedBook.java` + `EBook.java`  
2. `FileBookRepository.java` (polymorphic parse)  
3. `UserService.java` (hash + validation)  
4. `SecurityConfig.java`  
5. `docs/CLASS_DIAGRAM.md`
