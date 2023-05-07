# 대림대학교 3학년 1학기 캡스톤디자인 프로젝트 - 길드
## 백엔드(Spring boot)

프로젝트 생성 - https://start.spring.io/
- spring-version: 3.0.2
- java-version: oracle jdk 17.0.6(https://download.oracle.com/java/17/archive/jdk-17.0.6_windows-x64_bin.exe)
- Dependencies: spring boot devtools, lombok, spring web
- MariaDB DDL: - db서버 터질때를 대비
``` SQL
-- 게시글 테이블
CREATE TABLE `Board` (
  `boardId` int(11) NOT NULL AUTO_INCREMENT,
  `userId` varchar(100) NOT NULL,
  `title` varchar(100) NOT NULL,
  `content` text NOT NULL,
  `partyId` int(11) NOT NULL,
  `views` int(11) NOT NULL DEFAULT 0,
  `writeDate` timestamp NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`boardId`),
  KEY `Board_FK` (`userId`),
  KEY `Board_FK_2` (`partyId`),
  CONSTRAINT `Board_FK` FOREIGN KEY (`userId`) REFERENCES `User` (`userId`) ON DELETE CASCADE,
  CONSTRAINT `Board_FK_2` FOREIGN KEY (`partyId`) REFERENCES `Party` (`partyId`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 유저 테이블
CREATE TABLE `User` (
  `userId` varchar(100) NOT NULL,
  `userName` varchar(100) NOT NULL,
  `password` varchar(100) NOT NULL,
  `regDate` timestamp NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 댓글 테이블
CREATE TABLE `Comment` (
  `cmtId` int(11) NOT NULL AUTO_INCREMENT,
  `boardId` int(11) NOT NULL,
  `userId` varchar(100) NOT NULL,
  `target` int(11) DEFAULT NULL,
  `comment` text NOT NULL,
  `isPublic` tinyint(1) NOT NULL DEFAULT 1,
  `writeDate` timestamp NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`cmtId`),
  KEY `Comment_FK` (`target`),
  KEY `Comment_FK_1` (`userId`),
  CONSTRAINT `Comment_FK` FOREIGN KEY (`target`) REFERENCES `Comment` (`cmtId`) ON DELETE CASCADE,
  CONSTRAINT `Comment_FK_1` FOREIGN KEY (`userId`) REFERENCES `User` (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 파티 테이블
CREATE TABLE `Party` (
  `partyId` int(11) NOT NULL AUTO_INCREMENT,
  `total` int(11) NOT NULL,
  `current` int(11) NOT NULL,
  `isActive` tinyint(1) NOT NULL DEFAULT 1,
  PRIMARY KEY (`partyId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 유저의 파티참가 현황 테이블
CREATE TABLE `PartyUser` (
  `idx` int(11) NOT NULL AUTO_INCREMENT,
  `partyId` int(11) NOT NULL,
  `userId` varchar(100) NOT NULL,
  PRIMARY KEY (`idx`),
  KEY `PartyUser_FK` (`userId`),
  KEY `PartyUser_FK_1` (`partyId`),
  CONSTRAINT `PartyUser_FK` FOREIGN KEY (`userId`) REFERENCES `User` (`userId`) ON DELETE CASCADE,
  CONSTRAINT `PartyUser_FK_1` FOREIGN KEY (`partyId`) REFERENCES `Party` (`partyId`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 태그 테이블
CREATE TABLE `Tag` (
  `tagId` int(11) NOT NULL AUTO_INCREMENT,
  `tagName` varchar(100) NOT NULL,
  PRIMARY KEY (`tagId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 글 별 태그 목록 테이블
CREATE TABLE `TagBoard` (
  `idx` int(11) NOT NULL AUTO_INCREMENT,
  `tagId` int(11) NOT NULL,
  `boardId` int(11) NOT NULL,
  PRIMARY KEY (`idx`),
  KEY `NewTable_FK` (`boardId`),
  KEY `NewTable_FK_1` (`tagId`),
  CONSTRAINT `NewTable_FK` FOREIGN KEY (`boardId`) REFERENCES `Board` (`boardId`),
  CONSTRAINT `NewTable_FK_1` FOREIGN KEY (`tagId`) REFERENCES `Tag` (`tagId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
```

## api요청시 주의사항
> axios.defaults.withCredentials = true; //axios 사용 컴포넌트 마다 한번씩 붙여넣을 것

이거 안쓰면 세션ID가 계속 바뀌기 때문에 로그인 유지가 안됨


## 웹소켓을 이용한 채팅기능 가능 확인
[출처](https://ssjeong.tistory.com/entry/JAVA-Spring-Boot%EC%97%90%EC%84%9C-WebSocket-%EA%B5%AC%EC%B6%95-%EB%B0%8F-%EC%82%AC%EC%9A%A9%ED%95%98%EA%B8%B0)
### 웹소켓 테스트 클라이언트 파일 위치
> src/main/resources/static

따로 Visual Studio Code로 실행할 것


## API 표
### Swagger로 대체
- 서버실행 후 http://localhost:8080/swagger-ui/index.html#/ 접속 
- http://guild-api.kro.kr:8088/docs/ 접속
