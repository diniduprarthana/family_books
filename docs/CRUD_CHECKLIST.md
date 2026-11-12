# CRUD Checklist — Family Books

Verification of all **16** CRUD operations (4 modules × 4 operations).

## Module 1 — User Management

| Op | Feature | Evidence | Status |
|---|---|---|---|
| C | Register account | `AuthController` POST `/register` → `UserService.register` → `users.txt` | Implemented |
| R | View profile / admin user list | `/profile`, `/admin/users` | Implemented |
| U | Update profile / password / admin edit | `/profile/update`, `/profile/password`, `/admin/users/{id}` | Implemented |
| D | Delete own account / admin delete | `/profile/delete`, `/admin/users/{id}/delete` (+ related reviews removed) | Implemented |

Auth extras (not counted as CRUD): login, logout, roles, session.

## Module 2 — Book Management

| Op | Feature | Evidence | Status |
|---|---|---|---|
| C | Admin add Printed / E-Book | `/admin/books` POST → `BookService.createPrinted/createEBook` | Implemented |
| R | Collection, details, search/filter/sort, admin list | `/books`, `/books/{id}`, `/admin/books` | Implemented |
| U | Admin edit book fields + type-specific data | `/admin/books/{id}` | Implemented |
| D | Admin delete book (+ reviews cascade) | `/admin/books/{id}/delete` | Implemented |

## Module 3 — Category Management

| Op | Feature | Evidence | Status |
|---|---|---|---|
| C | Admin create category | `/admin/categories` POST | Implemented |
| R | Public chips + admin table + books-per-category count | Home/Collection/Admin | Implemented |
| U | Rename / edit description | `/admin/categories/{id}` | Implemented |
| D | Delete with protection if books remain | `/admin/categories/{id}/delete` → `InvalidOperationException` | Implemented |

## Module 4 — Review Management

| Op | Feature | Evidence | Status |
|---|---|---|---|
| C | Authenticated review submit | POST `/reviews` | Implemented |
| R | Book details + average rating + admin list | `/books/{id}`, `/admin/reviews` | Implemented |
| U | Owner (or admin) edit | POST `/reviews/{id}/update` | Implemented |
| D | Owner (or admin) delete | POST `/reviews/{id}/delete`, admin moderation | Implemented |

## Automated test coverage

`CrudServiceIntegrationTest` covers create/read/update/delete for users, books, categories, and reviews, plus ownership, duplicates, persistence reload, and polymorphism.

Run: `mvn test`
