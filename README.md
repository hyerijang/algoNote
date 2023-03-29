# algoNote

> 알고리즘 문제 풀이 기록을 위한 Spring 개인 프로젝트입니다. 
> 
> 사용자는 태그를 활용하여 알고리즘 문제를 기록 및 분류 하고, 문제 별로 리뷰를 작성할 수 있습니다. 
> 
> 문제, 리뷰 검색 기능을 제공합니다.  
---

## 프로젝트 구현 목표
> 개인 알고리즘 문제 저장소 구현
> 
> Spring, JPA, 테스트 코드에 대한 학습

- [x] 라인/분기 커버리지 70% 이상 달성하기
- [x] 빌더패턴 적용하기
- [x] API 구현
- [x] Thymeleaf: fragment 적용


<img src="https://user-images.githubusercontent.com/46921979/228466964-66f8751f-81e2-425c-a0fa-8d4a4be34d5b.JPG" width= "90%">

---

## ERD
<img src="https://user-images.githubusercontent.com/46921979/228477294-4166fdce-1309-4a4b-a981-538b52f872e2.png" width= "90%">

---

## Environment

`Tool` : intelliJ

`OS` : window 10 / Ubuntu 22.04.1 LTS

---

## Dependencies

### Language

`Frontend`: html, css , javascript, thymeleaf, jquery

`Backend`: java openjdk 11.0.11

### Spring Boot 2.6.0

`Data access`: jpa 2.6.0, hibernate 5.1.2, querydsl 5.0.0

`Data processing`: Jackson 2.13.0

`Test Coverage` : Jacoco 0.8.7

### DB

H2 2.1.214

### Library



## 기능 설명


  - [회원 가입](#회원-가입)
  - [회원 목록](#회원-목록)

  - [문제 등록](#문제-등록)
  - [문제 수정](#문제-수정)
  - [문제 태그](#문제-태그)
  - [문제 검색](#문제-검색)
  - [리뷰 등록](#리뷰-등록)

  - [태그 검색](#태그-검색)
  - [리뷰 검색](#리뷰-검색)

  - [댓글 대댓글](#댓글-대댓글)

---

### 회원 가입

- 소셜로그인(구글)으로 회원가입/로그인한다. -`Spring Security` 적용, 유저의 `Role` 에따라 접근가능한 URI가 달라진다.
- 중복된 이메일로 회원가입을 시도할 경우 `EmailRedundancyException`을 발생시킨다.
- see also [#1](/../../issues/1) [#2](/../../issues/2) [#15](/../../issues/15)
<img src="https://user-images.githubusercontent.com/46921979/228471582-649aa9f1-a9c1-4e52-8c59-ed42e91f0837.png" width= "90%">


### 회원 목록

- ADMIN 권한을 갖는 유저는 `/admin` 페이지에서 전체 회원 목록을 조회할 수 있다.
- see also [#8](/../../issues/8)


### 문제 등록

- 한명의 유저는 여러개의 문제를 등록 할 수 있다.
- 문제에는 여러개의 태그를 등록할 수 있다.
- see also [#3](/../../issues/3)
  <img src="https://user-images.githubusercontent.com/46921979/228471793-a42c5d2c-2ad3-4b30-b61a-d55195c582b6.png" width= "90%">

### 문제 상세

- 문제 상세페이지에서 문제를 수정하고 관련된 리뷰를 등록, 조회할 수 있다.
- 
  <img src="https://user-images.githubusercontent.com/46921979/228484467-9bb9a4e3-2d24-4a53-8c3f-a80e0610c18a.png" width = "90%">


### 문제 수정

- 문제를 수정한다.
- 작성자가 아닌 유저가 문제를 수정하려고 시도하면 `IllegalArgumentException`이 발생하고, 이전 페이지로 리다이렉트 된다.
- 태그 정보가 수정되면 기존 `ProblemTag`리스트를 clear하고, DB에 아래 쿼리를 전송한다. 이후 변경된 태그 정보를 다시 추가한다.

  ```java
  //해당 문제의 problemTag 전부 삭제
  //where절 사용하여 delete 1번만 호출
   String jpql="DELETE FROM ProblemTag where problem.id = :problemid";
  ```

- `ProblemTag` 는 복원을 지원 하지 않을 것이므로 `Hard delete` 하였다.
- see also [#3](/../../issues/3)
<img src="https://user-images.githubusercontent.com/46921979/228482986-7b3ba322-00ed-4ca2-b64b-c5741233dab5.png" width = "90%">

### 문제 태그

- 문제, 태그 맵핑 정보는 `PROBLEM_TAG`테이블에 별도로 저장된다. 이 테이블에는 `문제id`, `태그id`가 저장된다.
- 태그는 `TAG` 테이블에 별도로 저장된다.
- `CascadeType.ALL` : 문제에서 `List<PROBLEM_TAG> problemTagList`가 수정되면 상태 변화가 전이된다
- see also [#6](/../../issues/6)

### 문제 검색

- `queryDsl`을 사용하여 동적쿼리를 생성한다.
- 키워드로 문제를 검색하며, 키워드와 일치하는 제목, 태그, 내용이 있는 경우 검색 결과에 포함된다.
- see also [#9](/../../issues/9)
<img src="https://user-images.githubusercontent.com/46921979/228482475-9f97453c-e952-4195-8bcc-342493cbb004.png" width = "90%">

### 리뷰 등록

- 한 문제에 여러개의 리뷰를 등록 할 수 있다.
- 리뷰별로 태그를 등록할 수 있다.
- see also [#7](/../../issues/7)
  <img src="https://user-images.githubusercontent.com/46921979/228483777-13ee9f93-5312-4f69-b468-3c5b8c4997ab.png" width = "90%">

