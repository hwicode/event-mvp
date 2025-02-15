## 선착순 이벤트 MVP 프로젝트

### 🖐 프로젝트 목표
선착순 쿠폰 발급 이벤트를 최소한의 기능으로 구현합니다.

<br>

### 📌 프로젝트 핵심
+ MVP로 빠르게 구현합니다.
+ 프로덕션에서 사용 가능하게 만듭니다.

<br>

### 📝 요구사항

+ 선착순으로 쿠폰을 발급합니다.
+ 발급할 수 있는 쿠폰 수량이 제한되어 있습니다.
+ 유저 1인당 쿠폰 1매만 발급 가능합니다.
+ 유저는 이벤트 대기 순서를 알 수 있습니다.
+ 유저는 이벤트 대기가 끝나면, 이벤트 당첨 여부를 알 수 있습니다.
+ 개발자는 이벤트 프로젝트가 정상적으로 동작하는지 파악할 수 있습니다.

<br>

### 🧐 상세 구현 과정 & 문제 해결 스토리

+ [선착순 쿠폰 발행 이벤트를 MVP로 구현해보자 - 1](https://velog.io/@hwicode/%EC%84%A0%EC%B0%A9%EC%88%9C-%EC%BF%A0%ED%8F%B0-%EB%B0%9C%ED%96%89-%EC%9D%B4%EB%B2%A4%ED%8A%B8%EB%A5%BC-MVP%EB%A1%9C-%EA%B5%AC%ED%98%84%ED%95%B4%EB%B3%B4%EC%9E%90-1)
+ [선착순 쿠폰 발행 이벤트를 MVP로 구현해보자 - 2](https://velog.io/@hwicode/%EC%84%A0%EC%B0%A9%EC%88%9C-%EC%BF%A0%ED%8F%B0-%EB%B0%9C%ED%96%89-%EC%9D%B4%EB%B2%A4%ED%8A%B8%EB%A5%BC-MVP%EB%A1%9C-%EA%B5%AC%ED%98%84%ED%95%B4%EB%B3%B4%EC%9E%90-2)
+ [Connection refused 에러 해결 과정](https://velog.io/@hwicode/Connection-refused-%EC%97%90%EB%9F%AC-%ED%95%B4%EA%B2%B0-%EA%B3%BC%EC%A0%95)


<br>

### 이벤트 참가 API

![이벤트 참가](https://github.com/user-attachments/assets/e8bc13ea-9ba4-4393-8f95-1b27a072acd7)

<br>

### 이벤트 스케쥴링 과정

![이벤트 스케쥴링](https://github.com/user-attachments/assets/e6442472-cf96-463a-ab71-ea23d405f9a8)

<br>

### 전체 이벤트 진행 과정

![이벤트 진행 과정](https://github.com/user-attachments/assets/e1b2329e-207c-4b8b-9f14-881502fe5411)

<br>


### 참고 사항
+ DB의 쿠폰 테이블의 경우에는 최대한 간단하게 컬럼을 정의했습니다.
+ 개발자는 로그를 통해 프로젝트가 제대로 동작하는지 알 수 있습니다.

