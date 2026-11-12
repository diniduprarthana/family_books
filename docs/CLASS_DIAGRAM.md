# Class Diagram — Family Books

This diagram matches the implemented source code (models, repositories, services, security adapter).

## Mermaid UML

```mermaid
classDiagram
    direction TB

    class Book {
        <<abstract>>
        -String id
        -String title
        -String author
        -String isbn
        -String categoryId
        -String description
        -BigDecimal price
        -String coverImage
        +getBookType() String*
        +getAvailabilityDescription() String*
        +getTypeSpecificData() String*
        +isAvailable() boolean*
    }

    class PrintedBook {
        -int stockQuantity
        -int pageCount
        +getBookType() String
        +getAvailabilityDescription() String
        +getTypeSpecificData() String
        +isAvailable() boolean
    }

    class EBook {
        -String digitalFormat
        -String downloadAccess
        +getBookType() String
        +getAvailabilityDescription() String
        +getTypeSpecificData() String
        +isAvailable() boolean
    }

    class User {
        -String id
        -String username
        -String email
        -String passwordHash
        -String fullName
        -UserRole role
        -LocalDateTime createdAt
        +isAdmin() boolean
    }

    class UserRole {
        <<enumeration>>
        CUSTOMER
        ADMIN
    }

    class Category {
        -String id
        -String name
        -String description
    }

    class Review {
        -String id
        -String bookId
        -String userId
        -int rating
        -String reviewText
        -LocalDateTime createdAt
        +isOwnedBy(String) boolean
    }

    class UserRepository {
        <<interface>>
        +findAll() List~User~
        +findById(String) Optional~User~
        +save(User) User
        +deleteById(String) void
    }

    class BookRepository {
        <<interface>>
        +findAll() List~Book~
        +findById(String) Optional~Book~
        +save(Book) Book
        +deleteById(String) void
    }

    class CategoryRepository {
        <<interface>>
        +findAll() List~Category~
        +findById(String) Optional~Category~
        +save(Category) Category
        +deleteById(String) void
    }

    class ReviewRepository {
        <<interface>>
        +findAll() List~Review~
        +findByBookId(String) List~Review~
        +save(Review) Review
        +deleteById(String) void
    }

    class FileUserRepository
    class FileBookRepository
    class FileCategoryRepository
    class FileReviewRepository
    class FileStorageSupport

    class UserService
    class BookService
    class CategoryService
    class ReviewService

    class CustomUserDetails {
        -User user
        +getAuthorities() Collection
        +getPassword() String
        +getUsername() String
    }

    Book <|-- PrintedBook : extends
    Book <|-- EBook : extends
    User --> UserRole : uses
    Review --> Book : bookId
    Review --> User : userId
    Book --> Category : categoryId

    UserRepository <|.. FileUserRepository
    BookRepository <|.. FileBookRepository
    CategoryRepository <|.. FileCategoryRepository
    ReviewRepository <|.. FileReviewRepository

    FileUserRepository --> FileStorageSupport
    FileBookRepository --> FileStorageSupport
    FileCategoryRepository --> FileStorageSupport
    FileReviewRepository --> FileStorageSupport

    UserService --> UserRepository
    UserService --> ReviewRepository
    BookService --> BookRepository
    BookService --> CategoryRepository
    BookService --> ReviewRepository
    CategoryService --> CategoryRepository
    CategoryService --> BookRepository
    ReviewService --> ReviewRepository
    ReviewService --> BookRepository

    CustomUserDetails --> User : adapts
```

## Important relationships (viva)

1. **Inheritance:** `PrintedBook` and `EBook` extend abstract `Book`.
2. **Polymorphism:** Controllers/services hold `Book` references; `getBookType()` / `getAvailabilityDescription()` resolve at runtime.
3. **Abstraction:** `Book` abstract class + repository interfaces hide storage details.
4. **Encapsulation / information hiding:** private fields; file parsing lives inside `repository.file` package.
5. **Association:** Reviews reference `bookId` and `userId`; books reference `categoryId`.

## Controllers (not fully expanded above)

- `HomeController`, `AuthController`, `BookController`, `UserController`, `ReviewController`, `AdminController`
- Each depends on service classes only (never on text files directly).
