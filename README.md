# 순수 JetPack compose, Material3를 사용한 아이이어 노트
(과업 관리 + 일정 관리 + 메모)
## 설명
* 쇼케이스로 만든 기반 업무관리 프로그램 + 메모
* 영어, 한국어, 태국어 지원

## 사용 라이브러리
* Kotlin
* 내부저장소 sqlite(room)
* 데이터 캐싱, 동적 로딩 - paging3
* UI - JetPack Compose, Google Material3
* compose navigation
* 카메라 라이브러리 - CameraX 

## 기능
* __아래의 검색 요건을 모두 혼합하여 조회 가능__
* 우선순위 필터, 최근 사용한 필터가 우선 표출
* 키워드 검색 - 검색 노트 범위 설정 가능
* 날짜 순, 생성일 기준, 업데이트 기준, 단계 설정 기준, 데드라인일 기준
* 선택 노트 / 전체 노트 표출 스위치로 선택 가능
* 검색 날짜 범위 지정 (달력화면에서 지정)
* 즐겨찾기 선택
* 과업의 단계 필터
* 데이터베이스를 json 파일로 추출하여 이메일로 보내기, 추출된 정보로 데이터 복구 가능
* 여러개의 메모장 생성가능, paging3를 이용한 lazyloading, sticky 헤더 구현
* 간편 제스처로 메모 관리 기능 - 목록에서 좌측 스와이프로 수정 화면, 우측 스와이프로 삭제
* 뷰어에서 스와이프로 다음 자료 보기
* 멀티 셀럭트 파일 지정 및 삭제
* 노트 간 메모 이동, 여러 개의 메모를 노특로 한번에 이동하기
* 과업 관리 기능 지원
* 과업의 단계에 따라 검색 필터 기능
* 메모를 업무 단계 지정 가능
* 과업 마감일 설정
* 사진 촬영, 사진 첨부 가능
* 마감일 알림 기능
* 달력 뷰 제공, 일자별 메모(일정) 확인 및 알림 설정

[유튜브 화면녹화](https://www.youtube.com/watch?v=b6Fc3ctLHdg)

![Screenshot 2024-02-01 at 4 25 44 PM](https://github.com/pilseong/todocompose/assets/19240446/325e7a82-446e-41ac-9205-a85b2909bfc1)
