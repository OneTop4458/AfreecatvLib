## AfreecatvLib
아프리카tv(SOOP) 연동을 할 수 있는 Java API입니다.<br>
해당 라이브러리는 [**taromati**](https://github.com/taromati/done-connector)님의 **done-connector**를 개선한 API입니다.<br><br>
**개선된 사항**
- 일반 채팅 연동 가능
- 사용하기 쉽게 수정
- 방송 정보 가져오기 (방송인 아이디, 방송인 닉네임, 방송 제목, 방송 태그)

## 라이선스
해당 라이브러리의 라이선스는 [**taromati**](https://github.com/taromati/done-connector)님의 **done-connector**의 라이선스와 같습니다.

## 예시
```groovy
repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {
    implementation("com.github.Choi-JinHwan:AfreecatvLib:1.0.0")
}
```

```java
        /*---| 플랫폼 연동 및 소켓 연결 |---*/
final AfreecatvAPI api = new AfreecatvAPI.AfreecatvBuilder().withData("비제이 아이디").build()
        .addListeners(new AfreecatvListener() {
            @Override
            public void onMessageChat(MessageChatEvent e) {
                /*---| 일반 채팅 불러오기 |---*/
            }

            @Override
            public void onDonationChat(DonationChatEvent e) {
                /*---| 후원 채팅 불러오기 |---*/
            }
        }).connect();

/*---| 소켓 연결 해제 |---*/
        api.disconnect();

/*---| 방송 정보 가져오기 |---*/
final AfreecatvLiveInfo liveInfo = AfreecatvAPI.getLiveInfo("비제이 아이디");
```
