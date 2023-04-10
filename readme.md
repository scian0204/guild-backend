# 한이음 사이드 프로젝트 - 블로그
## 백엔드(Spring)

프로젝트 생성 - https://start.spring.io/
- spring-version: 3.0.2
- java-version: oracle jdk 17.0.6(https://download.oracle.com/java/17/archive/jdk-17.0.6_windows-x64_bin.exe)
- Dependencies: spring boot devtools, lombok, spring web
- MariaDB : 
```
// 게시글 테이블
CREATE TABLE `Board` (
  `idx` int(11) NOT NULL AUTO_INCREMENT,
  `userId` varchar(100) NOT NULL,
  `title` varchar(100) NOT NULL,
  `content` text NOT NULL,
  `writeDate` timestamp NULL DEFAULT current_timestamp(),
  `imageLoc` text DEFAULT NULL,
  PRIMARY KEY (`idx`)
)

// 유저 테이블
CREATE TABLE `User` (
  `userId` varchar(100) NOT NULL,
  `userName` varchar(100) NOT NULL,
  `password` varchar(100) NOT NULL,
  `regDate` varchar(100) DEFAULT current_timestamp(),
  PRIMARY KEY (`userId`)
)
```

# api요청시 주의사항
> axios.defaults.withCredentials = true; //axios 사용 컴포넌트 마다 한번씩 붙여넣을 것

이거 안쓰면 세션ID가 계속 바뀌기 때문에 로그인 유지가 안됨


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
    "userId" : "",    <br> &emsp;
    "userName" : "",  <br> &emsp;
    "password" : "",  <br> &emsp;
    "regDate" : null  <br>
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
    "userId" : "",    <br> &emsp;
    "password" : "",  <br>
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
    "userId" : "",    <br> &emsp;
    "userName" : "",  <br> &emsp;
    "password" : "",  <br> &emsp;
    "regDate" : null  <br>
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
    "userId" : "",    <br> &emsp;
    "password" : "",  <br>
}
</code></td>
<td>
로그인 돼있는 id와 다를 때 : "0"<br>
id가 맞고 pw가 틀릴때 : "1"<br>
삭제됐을 때 : "2"
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
    "idx" : null, <br>
    "userId" : "", <br>
    "title" : "", <br>
    "content" : "", <br>
    "writeDate" : null, <br>
    "imageLoc" : null <br>
}
</code></td>
<td>title 미작성 시 : "0" <br>
content 미작성 시 : "1" <br>
게시물 작성 성공 : idx</td>
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
        "userId" : "", <br>
	    "title" : "", <br>
    	"writeDate" : null <br>
    }, <br>
    {<br>
        "userId" : "", <br>
	    "title" : "", <br>
    	"writeDate" : null <br>
    }, .....<br>
]
</code></td>
</tr>
<tr>
<td>/{idx}</td>
<td>GET</td>
<td>게시물 상세 페이지 조회</td>
<td><code>
    {idx}
</code></td>
<td><code>
{
    "idx" : idx, <br>
    "userId" : "", <br>
    "title" : "", <br>
    "content" : "", <br>
    "writeDate" : "", <br>
    "imageLoc" : "" <br>
}
</code></td>
</tr>
<tr>
<td>/update</td>
<td>PUT</td>
<td>게시물 수정 (users의 회원 정보 수정과 동일하게 세션에 저장된 userId와 넘긴 데이터의 userId가 같은지 확인)</td>
<td><code>
{
    "idx" : 글의 인덱스,
    "userId" : "글의 유저 아이디",
    "title" : "",
    "content" : "",
    "writeDate" : null,
    "imageLoc" : null
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
    "idx" : 글의 인덱스, <br>
    "userId" : "글의 유저 아이디"<br>
}
</code></td>
<td>userId 상이할 시 : "1"
삭제 시 : "0"</td>
</tr>
</table>