# algoNote


>  알고리즘 문제 풀이 기록을 위한 Spring 개인 프로젝트 (진행중)


# 목차
- [구현 기능 목록](#구현-기능-목록)
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

    
## 구현 기능 목록

### 회원 가입 
- 소셜로그인(구글)으로 회원가입/로그인한다.
-`Spring Security` 적용, 유저의 `Role` 에따라 접근가능한 URI가 달라진다.
- 중복된 이메일로 회원가입을 시도할 경우 `EmailRedundancyException`을 발생시킨다.
- see also [#1](/../../issues/1) [#2](/../../issues/2)  [#15](/../../issues/15) 

### 회원 목록
- ADMIN 권한을 갖는 유저는 `/admin` 페이지에서 전체 회원 목록을 조회할 수 있다.
- see also [#8](/../../issues/8) 

### 문제 등록
- 한명의 유저는 여러개의 문제를 등록 할 수 있다.
- 문제에는 여러개의 태그를 등록할 수 있다.
- see also [#3](/../../issues/3) 


### 문제 수정
- 문제를 수정한다.
- 작성자가 아닌 유저가 문제를 수정하려고 시도하면 `IllegalArgumentException`이 발생하고, 홈페이지로 리다이렉트 된다.
- 태그 정보가 수정되면 기존 `ProblemTag`리스트를 clear하고, DB에 아래 쿼리를 전송한다. 이후 변경된 태그 정보를 다시 추가한다.

    ```java
    //해당 문제의 problemTag 전부 삭제
    //where절 사용하여 delete 1번만 호출
     String jpql="DELETE FROM ProblemTag where problem.id = :problemid"; 
    ```
- `ProblemTag` 는 복원을 지원 하지 않을 것이므로 `Hard delete` 하였다.
- see also [#3](/../../issues/3) 

### 문제 태그
- 문제, 태그 맵핑 정보는 `PROBLEM_TAG`테이블에 별도로 저장된다. 이  테이블에는 `문제id`, `태그id`가 저장된다.
- 태그는 `TAG` 테이블에 별도로 저장된다.
- `CascadeType.ALL` :  문제에서 `List<PROBLEM_TAG> problemTagList`가 수정되면 상태 변화가 전이된다
- see also [#6](/../../issues/6) 

### 문제 검색
- `queryDsl`을 사용하여 동적쿼리를 생성한다.
- 제목, 내용, 사이트 정보로 문제를 검색할 수 있다.
- see also [#9](/../../issues/9) 

### 리뷰 등록
- 한 문제에 여러개의 리뷰를 등록 할 수 있다.
- 리뷰별로 태그를 등록할 수 있다.
- see also [#7](/../../issues/7) 


​
