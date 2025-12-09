# Chapter 01 – Login Default Success URL

This chapter introduces the foundations of Spring Security’s form login using real, working code — with a special focus on **`defaultSuccessUrl`** and how redirect behavior works after a successful login.

Spring Security can feel overwhelming at first because many things happen “behind the scenes”.
In this chapter, we slow everything down and focus on how login and **post-login redirects** actually work, step by step.

This project is intentionally kept simple and focused — **no databases, no JWT, no OAuth, no microservices** — just the core login flow.

---

## 🎯 What You Will Learn

By the end of this chapter, you will understand:

* How Spring Security enables **form-based login**
* How a **custom login page** is wired using `.loginPage("/auth/login")`
* What happens when login **succeeds or fails**
* How Spring Security decides **where to redirect** after login using:
  * `defaultSuccessUrl("/home")`
  * `defaultSuccessUrl("/home", true)` vs `defaultSuccessUrl("/home", false)`
* How Spring Security decides who can access which URL (basic URL authorization)

---

## 🧱 What This Chapter Covers ✅

Included in this project:

* Custom login page (Thymeleaf) at `/auth/login`
* Default Spring Security filter chain (with small customizations)
* Username + password authentication using in-memory user(s)
* Redirect behavior after login:
  * When there is **no saved request**
  * When the user first tries to access a **protected URL**
* Basic protected and public endpoints:
  * Public: `/auth/login`, static assets
  * Protected: `/home`, `/dashboard/**` (as example)

---

## 🚫 Not Covered Yet (Intentionally)

These topics are very important, but to avoid overloading you, they are **not** covered in this chapter:

* Logout handling
* Remember-me
* CSRF deep dive
* OAuth2 / JWT / Keycloak
* Database-backed users and roles

Each of these will be introduced gradually in later chapters, once the core login and redirect mechanics are fully clear.

---

## 🚀 How to Run the Project

### Prerequisites

* Java 17+
* Maven
* (Optional) IntelliJ IDEA or your preferred IDE

### Start the Application

From the project root:

```bash
./mvnw spring-boot:run
