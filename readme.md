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
/api/users
<table>
<tr>
<th>URL</th>
<th>Method</th>
<th>설명</th>
<th>request</th>
<th>response</th>
</tr>
<tr>
<td>/signup</td>
<td>POST</td>
<td>회원가입 후 세션<br>생성하여 자동로그인</td>
<td><code>
{
    userId : String,    <br> &emsp;
    userName : String,  <br> &emsp;
    password : String,  <br>
}
</code></td>
<td>userId</td>
</tr>
<tr>
<td>/login</td>
<td>POST</td>
<td>세션 생성하여 로그인</td>
<td><code>
{
    userId : String,    <br> &emsp;
    password : String,  <br>
}
</code></td>
<td>
id가 존재하지 않을 때 : "0"<br>
id가 존재하지만 pw가 틀렸을 때 : "1"<br>
로그인 성공 : userId<br>
</td>
</tr>
<tr>
<td>/logout</td>
<td>GET</td>
<td>세션 삭제하여 로그아웃</td>
<td>-</td>
<td>-</td>
</tr>
<tr>
<td>/isLogin</td>
<td>GET</td>
<td>세션 값이 있는지 확인</td>
<td>-</td>
<td>userId</td>
</tr>
<tr>
<td>/modify</td>
<td>PUT</td>
<td>회원정보 수정(세션에 저장된 userId와<br>넘긴 데이터의 userId가 같은지 확인하기<br>때문에 로그인이 돼있어야 함)</td>
<td><code>
{
    userId : String,    <br> &emsp;
    userName : String,  <br> &emsp;
    password : String,  <br>
}
</code></td>
<td>-</td>
</tr>
<tr>
<td>/delete</td>
<td>POST</td>
<td>회원정보 삭제</td>
<td><code>
{
    userId : String,    <br> &emsp;
    password : String,  <br>
}
</code></td>
<td>
로그인 돼있는 id와 다를 때 : "0"<br>
id가 맞고 pw가 틀릴때 : "1"<br>
삭제됐을 때 : "2"
</td>
</tr>
<tr>
<td>/isIdDup/{userId}</td>
<td>GET</td>
<td>아이디 중복 체크</td>
<td><code>-</code></td>
<td>
userId가 중복일 때 : true<br>
UserId가 중복이 아닐때 : false<br>
</td>
</tr>
</table>
<hr>
/api/boards
<table>
<tr>
<th>URL</th>
<th>Method</th>
<th>설명</th>
<th>request</th>
<th>response</th>
</tr>
<tr>
<td>/write</td>
<td>POST</td>
<td>게시물 작성</td>
<td><code>
{
    userId : String, <br>
    title : String, <br>
    content : String, <br>
    total : Integer, <br>
    current : Integer, <br>
    tagId : X or [Integer, Integer ...], <br>
    tagName : X or [String, String, ...], <br>
}
</code></td>
<td>게시물 작성 성공 : boardId</td>
</tr>
<tr>
<td>/list(/ASC)</td>
<td>GET</td>
<td>게시물 리스트 조회</td>
<td> 
page={요청할 페이지} size={한 페이지당 보여줄 게시글 수} <br> 
예) /list?page=0&size=15 <br>
/ASC 추가할 시 반대로 정렬 됨
</td>
<td><code>
[<br>
    {<br>
        board : {<br>
            boardId : integeer,<br>
            userId : String,<br>
            title : String,<br>
            content : String,<br>
            partyId : Integer,<br>
            views : Integer,<br>
            writeDate : TimeStamp<br>
        },<br>
        tags : [<br>
            {<br>
                tagId : Integer,<br>
                tagName : String<br>
            },<br>
            {<br>
                tagId : Integer,<br>
                tagName : String<br>
            },<br>
            ...<br>
        ]<br>
    },<br>
    {<br>
        board : {<br>
            boardId : integeer,<br>
            userId : String,<br>
            title : String,<br>
            content : String,<br>
            partyId : Integer,<br>
            views : Integer,<br>
            writeDate : TimeStamp<br>
        },<br>
        tags : [<br>
            {<br>
                tagId : Integer,<br>
                tagName : String<br>
            },<br>
            {<br>
                tagId : Integer,<br>
                tagName : String<br>
            },<br>
            ...<br>
        ]<br>
    },<br>
    ...<br>
]<br>
</code></td>
</tr>
<tr>
<td>/{idx}</td>
<td>GET</td>
<td>게시물 상세 페이지 조회</td>
<td><code>
    {boardId}
</code></td>
<td><code>
{
    Board : { <br>
        baordId : Integer, <br>
        userId : String, <br>
        title : String, <br>
        content : String, <br>
        partyId : Integer, <br>
        view : Integer, <br>
        writeDate : Timestamp <br>
    }, <br>
    Party : { <br>
        partyId : Integer, <br>
        total : Integer, <br>
        current : Integer, <br>
        isActive : boolean, <br>
    }, <br>
    Tags : [ <br>
        { <br>
            tagId : Integer <br>
            tagName : String <br>
        }, <br>
        { <br>
            tagId : Integer <br>
            tagName : String <br>
        }, <br>
        ... <br>
    ] <br>
}
</code></td>
</tr>
<tr>
<td>/update</td>
<td>PUT</td>
<td>게시물 수정 (users의 회원 정보 수정과 동일하게 세션에 저장된 userId와 넘긴 데이터의 userId가 같은지 확인)</td>
<td><code>
{<br>
    boardId : Integer, <br>
    userId : String, <br>
    title : String, <br>
    content : String, <br>
    partyId : Integer, <br>
    tagId : X or [Integer, Integer, ...], <br>
    tagName : X or [String, String, ...], <br>
    total : X or Integer, <br>
    current : X or Integer, <br>
}
</code></td>
<td>-</td>
</tr>
<tr>
<td>/delete</td>
<td>POST</td>
<td>게시물 삭제 (세션에 저장된 userId와 넘긴 데이터의 userId가 같은지 확인)</td>
<td><code>
{
    boardId : Integer, <br>
    userId : String<br>
}
</code></td>
<td>userId 상이할 시 : "1"
삭제 시 : "0"</td>
</tr>
<tr>
<td>/search/{text}</td>
<td>GET</td>
<td>제목이나 내용에 text가 포함된 게시글과 태그들을 리스트로 반환</td>
<td><code>-</code></td>
<td><code>
[<br>
    {<br>
        board : {<br>
            boardId : integeer,<br>
            userId : String,<br>
            title : String,<br>
            content : String,<br>
            partyId : Integer,<br>
            views : Integer,<br>
            writeDate : TimeStamp<br>
        },<br>
        tags : [<br>
            {<br>
                tagId : Integer,<br>
                tagName : String<br>
            },<br>
            {<br>
                tagId : Integer,<br>
                tagName : String<br>
            },<br>
            ...<br>
        ]<br>
    },<br>
    {<br>
        board : {<br>
            boardId : integeer,<br>
            userId : String,<br>
            title : String,<br>
            content : String,<br>
            partyId : Integer,<br>
            views : Integer,<br>
            writeDate : TimeStamp<br>
        },<br>
        tags : [<br>
            {<br>
                tagId : Integer,<br>
                tagName : String<br>
            },<br>
            {<br>
                tagId : Integer,<br>
                tagName : String<br>
            },<br>
            ...<br>
        ]<br>
    },<br>
    ...<br>
]<br>
</code></td>
</tr>
<tr>
<td>/searchByTagid/{tagId}</td>
<td>GET</td>
<td>태그id의 태그가 달린 게시글과 태그 목록 반환</td>
<td><code>-</code></td>
<td><code>
[<br>
    {<br>
        board : {<br>
            boardId : integeer,<br>
            userId : String,<br>
            title : String,<br>
            content : String,<br>
            partyId : Integer,<br>
            views : Integer,<br>
            writeDate : TimeStamp<br>
        },<br>
        tags : [<br>
            {<br>
                tagId : Integer,<br>
                tagName : String<br>
            },<br>
            {<br>
                tagId : Integer,<br>
                tagName : String<br>
            },<br>
            ...<br>
        ]<br>
    },<br>
    {<br>
        board : {<br>
            boardId : integeer,<br>
            userId : String,<br>
            title : String,<br>
            content : String,<br>
            partyId : Integer,<br>
            views : Integer,<br>
            writeDate : TimeStamp<br>
        },<br>
        tags : [<br>
            {<br>
                tagId : Integer,<br>
                tagName : String<br>
            },<br>
            {<br>
                tagId : Integer,<br>
                tagName : String<br>
            },<br>
            ...<br>
        ]<br>
    },<br>
    ...<br>
]<br>
</code></td>
</tr>
</table>
<hr/>
/api/tag
<table>
<tr>
<th>URL</th>
<th>Method</th>
<th>설명</th>
<th>request</th>
<th>response</th>
</tr>
<tr>
<td>/{tagName}</td>
<td>GET</td>
<td>해당 이름이 포함된 Tag 검색</td>
<td><code>-</code></td>
<td><code>
[<br>
    {<br>
        tagId : Integer,<br>
        tagName : String<br>
    },<br>
    {<br>
        tagId : Integer,<br>
        tagName : String<br>
    },<br>
    ...<br>
]<br>
</code></td>
</tr>
</table>

<hr/>
/api/party
<table>
<tr>
<th>URL</th>
<th>Method</th>
<th>설명</th>
<th>request</th>
<th>response</th>
</tr>
<tr>
<td>/join</td>
<td>POST</td>
<td>파티 참가</td>
<td><code>
{ <br>
    partyId : Integer, <br>
    userId : String <br>
} <br>
</code></td>
<td><code>partyId</code></td>
</tr>
<tr>
<td>/leave</td>
<td>POST</td>
<td>파티 탈퇴</td>
<td><code>
{ <br>
    partyId : Integer, <br>
    userId : String <br>
} <br>
</code></td>
<td><code>-</code></td>
</tr>
</table>