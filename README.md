## 👋 네트워크프로그래밍 프로젝트 👋
<div align="center">
  <img src="https://capsule-render.vercel.app/api?type=waving&color=auto&height=100&section=header&text=BubblePOP&fontSize=40&animation=fadeIn&fontColor=random" alt="BubblePOP Header"/>
</div>
BubblePOP은 한성대학교 2024-2학기 네트워크 프로그래밍 팀 프로젝트로, 소켓통신을 활용한 멀티플레이어 게임 기능을 구현한 버블 팝 게임입니다.<br>
같은 색의 공을 맞추어 제거하는 버블 슈터 고전 아케이드 게임('Puzzle Bubble')에서 착안한 프로그램입니다.<br>

## ✨ 주요 기능 ✨
1. 로그인 시스템

  - 아이디와 비밀번호 입력을 통한 로그인 처리
  - 로그인 성공 시 로비 화면으로 이동
  
2. 로비

  - 게임 대기방 목록 확인 및 생성
  - 아이템 교환방 목록 조회
  - 게임 전체 채팅 기능
  - 플레이어 아이템 목록 조회
3. 상점 (파일 전송)
  - 아이템 구매 및 판매 기능
    
4. 아이템 교환방
  - 아이템 교환
  - 교환방 내 실시간 채팅 지원
    
5. 대기방
  - 대기방 내 채팅
  - 준비 확인 및 게임 시작 대기
6. 게임 실행
  - 구슬 발사 및 제거 로직
  - 점수 시스템과 승리 조건 구현

## 🎮 게임 기능 및 로직 🎮
- 게임 로직

  - 구슬 이동: 일정 시간 간격으로 구슬 배열이 아래로 이동
  - 조작 키:
    
    방향키 (→, ←): 발사 포인트 이동
- 스페이스바: 구슬 발사
충돌 및 제거:
발사된 구슬이 같은 색상의 구슬 그룹에 닿으면 3개 이상 연결 시 제거
연결되지 않은 구슬은 아래로 떨어짐

- 승리 조건 및 점수 시스템: 

  제한 시간 내 점수가 높은 플레이어가 승리.
  
  승리한 플레이어는 점수를 바탕으로 코인 획득
  
- 아이템 사용

  - Q 버튼: 구슬 색상 랜덤 변경
  - W 버튼: 가장 아래 라인 구슬 모두 제거
  - E 버튼: 폭탄을 발사해 연결된 구슬 2개씩 폭발

## 👥 팀원 소개 (Team Members)

| **임차민** | **정예빈** |
|:---:|:---:|
| <img src="https://github.com/ckals413.png" width="100"> | <img src="https://github.com/benniejung.png" width="100"> |
| [@ckals413](https://github.com/ckals413) | [@benniejung](https://github.com/benniejung) |

## 📚 기술 스택 📚
<p align="center"> <img src="https://img.shields.io/badge/Java-007396?style=for-the-badge&logo=Java&logoColor=white"> <img src="https://img.shields.io/badge/JavaFX-007396?style=for-the-badge&logo=Java&logoColor=white"> <img src="https://img.shields.io/badge/Socket.IO-010101?style=for-the-badge&logo=Socket.IO&logoColor=white"> </p>
💻 Java
객체 지향 프로그래밍 언어로, 안정적이고 플랫폼에 독립적인 애플리케이션 개발에 사용되었습니다.

🎨 JavaFX
Java를 위한 최신 GUI 툴킷으로, 풍부한 사용자 인터페이스를 구현하는 데 활용되었습니다.

🌐 Socket.IO
실시간 양방향 통신을 가능하게 하는 라이브러리로, 서버와 클라이언트 간의 원활한 데이터 전송을 지원합니다.

## 🎥 데모 영상

https://github.com/user-attachments/assets/a841e314-358e-4f9a-82e4-a87097a9baff


