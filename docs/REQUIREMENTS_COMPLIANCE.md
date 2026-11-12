# Requirements Compliance Map — Family Books

Honest mapping of academic requirements to implementation evidence.

Legend: **FULL** / **PARTIAL** / **CONFIRM** (needs lecturer confirmation) / **LIMIT**

---

## Marking allocation coverage

| Assessment area | Marks | Status | Evidence |
|---|---:|---|---|
| CRUD functionality | 10 | FULL | 16 operations across 4 modules; see `CRUD_CHECKLIST.md` |
| OOP concepts | 40 | FULL | Encapsulation, inheritance, polymorphism, abstraction, information hiding in models/repos/services |
| File handling / database | 10 | FULL | Java NIO text files in `data/` (no SQL/NoSQL DB) |
| Completeness | 10 | FULL | Public site + auth + admin + docs + tests |
| Individual contribution / GitHub | 5 | PARTIAL | Repo can be initialized with genuine commits; contribution depends on real team work |
| Presentation / communication | 10 | PARTIAL | App + `VIVA_GUIDE.md` support presentation; live delivery is student-owned |
| Class diagrams | 5 | FULL | `docs/CLASS_DIAGRAM.md` matches source |
| Teamwork / peer review | 5 | CONFIRM | Not software-deliverable; depends on team process |
| Coding standards | 5 | FULL | Layered packages, naming, validation, comments where useful |

---

## Mandatory objectives

| # | Objective | Status | Evidence |
|---|---|---|---|
| 1 | Java backend | FULL | `src/main/java/com/familybooks/**` |
| 2 | Spring Boot permitted stack | FULL / CONFIRM | Implemented with Spring Boot; confirm if marker expects JSP/Servlets sample path |
| 3 | OOP five concepts | FULL | Book hierarchy + repositories/services |
| 4 | Java file read/write | FULL | `FileStorageSupport`, `File*Repository` |
| 5 | Functional web UI | FULL | Thymeleaf templates + static assets |
| 6 | Coding standards | FULL | Clear packages and responsibilities |
| 7 | UML class diagram | FULL | `docs/CLASS_DIAGRAM.md` |
| 8 | Academic report | FULL | `docs/PROJECT_REPORT.md` |
| 9 | Genuine GitHub history | PARTIAL | Requires student GitHub publish + real commits over time |
| 10 | Viva-explainable Java | FULL | Simple layered design + `VIVA_GUIDE.md` |

---

## Items requiring lecturer confirmation

1. **JSP/Servlets vs Spring Boot + Thymeleaf**  
   Sample individual-contribution text in course materials refers to JSP/Servlets, while the project description lists Spring Boot among permitted technologies. This project deliberately uses Spring Boot. Ask the lecturer whether this is acceptable for your marking sheet.

2. **Peer review / teamwork marks**  
   Cannot be satisfied by code alone.

3. **Individual contribution marks**  
   Depend on authentic personal commits and report sections written by each member.

---

## Known limitations (do not over-claim)

- No external relational database (intentional academic constraint).
- Cover images are labeled sample SVG assets (not licensed commercial photography).
- GSAP scroll animations are intentionally restrained; reduced-motion is respected.
- Tailwind is loaded via CDN for academic simplicity (not a production build pipeline).
- Concurrent multi-node deployment is out of scope; local single-process locking only.

---

## Security compliance

| Control | Status |
|---|---|
| Password hashing (BCrypt) | FULL |
| Role-based route protection | FULL |
| CSRF (Spring Security default) | FULL |
| Review ownership checks | FULL |
| No self-elevation to ADMIN on register | FULL |
| Password hashes not shown in views | FULL |

---

## Final compliance statement

Core SE1020 technical requirements implemented for this repository are satisfied with evidence in code, tests, and docs. Social/process marks (teamwork, presentation delivery, personal GitHub contribution quality) remain student responsibilities. The Spring Boot vs JSP wording mismatch is explicitly flagged for lecturer confirmation.
