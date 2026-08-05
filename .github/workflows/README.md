# Notion → GitHub 코드 동기화

Notion 데이터베이스(문제 풀이 아카이브)에 올라온 코드를 매일 자동으로 GitHub 레포에 커밋합니다.

## 설정 순서

### 1. Notion 데이터베이스 ID 확인
데이터베이스 페이지 URL에서 32자리 ID를 복사하세요.
예: `https://app.notion.com/p/3aabc6754ae9801ba7a9c3a3c867d85d?...`
→ ID: `3aabc6754ae9801ba7a9c3a3c867d85d`

### 2. GitHub Secrets 등록
레포 → Settings → Secrets and variables → Actions → New repository secret

| Name | Value |
|---|---|
| `NOTION_TOKEN` | 전달받은 Notion Integration 토큰 (`secret_...` 또는 `ntn_...`) |
| `NOTION_DATABASE_ID` | 위에서 복사한 데이터베이스 ID |

### 3. 파일 배치
이 폴더 구조를 레포 루트에 그대로 복사하세요:
```
.github/workflows/notion-sync.yml
notion_sync.py
```

### 4. 실행 확인
- Actions 탭 → "Notion Sync" → "Run workflow" 버튼으로 수동 실행해서 먼저 테스트
- 정상 동작하면 매일 자동으로 실행됨 (cron 스케줄은 yml 파일에서 조정 가능)

## 동작 방식
- 데이터베이스의 모든 항목(페이지)을 조회
- 각 페이지 안의 **code 블록**을 찾아서 파일로 저장 (일반 텍스트/표는 무시됨)
- 파일명은 페이지 제목(title 속성) 기준
- "작성자/이름/Author/Person" 등의 속성이 있으면 `problems/작성자이름/문제명.py` 형태로 폴더 정리
- 파일 상단에 원본 Notion 속성들이 주석으로 자동 기록됨

---

## (추가) GitHub → Notion: main 브랜치 push 시 자동 등록

main에 push할 때마다, 이번 push로 추가/수정된 코드 파일들을 각각 Notion 데이터베이스에 새 페이지로 등록합니다.

### 배치할 파일
```
.github/workflows/push-to-notion.yml
github_to_notion.py
```
(Secrets는 위 pull 방향과 동일한 `NOTION_TOKEN`, `NOTION_DATABASE_ID`를 그대로 씁니다.)

### 동작 방식
- **전제하는 레포 경로 규칙**: `출처/알고리즘종류/문제번호-문제이름/코드파일`
  예: `프로그래머스/그리디/12938-이중우선순위큐/solution.py`
  이 규칙에 안 맞는 경로(깊이가 다르거나 `-` 구분자가 없는 등)는 자동으로 건너뜁니다.
- 코드 파일 확장자(`.py .js .ts .java .c .cpp .cs .go .rs .kt .swift .rb .php .sql`)만 대상
- 필드 매핑 (데이터베이스에 해당 이름의 속성이 있을 때만 자동으로 채워짐, 없으면 조용히 무시):

  | 경로/커밋 정보 | Notion 속성 이름 예시 | 타입 |
  |---|---|---|
  | 문제이름 | title 속성 (이름 무관, 자동 탐지) | title |
  | 문제번호 | `문제 번호`, `문제번호`, `번호`, `no`, `number` | rich_text/select/number |
  | 커밋 작성자 | `작성자`, `이름`, `author`, `name`, `person` | rich_text/select |
  | 커밋 일시 | `날짜`, `date` | date |
  | 알고리즘 종류 | `알고리즘`, `종류`, `카테고리`, `type`, `category` | rich_text/select |
  | 출처(SWEA/프로그래머스) | `출처`, `source`, `플랫폼`, `platform` | rich_text/select |
  | GitHub 링크 | `link`, `url`, `링크`, `깃허브`, `github` | url |

- **`난이도`는 레포 경로/파일에 정보가 없어 자동으로 채우지 않습니다.** 수동으로 입력하거나, 코드 파일 첫 줄에 난이도를 적는 규칙을 만들면 파싱 로직을 추가해드릴 수 있어요.
- 파일 내용은 code 블록으로 본문에 삽입 (2000자 단위로 자동 분할)

### ⚠️ 양방향 사용 시 주의
pull 스크립트(`notion_sync.py`)와 이 push 스크립트를 동시에 쓰면 **무한 동기화 루프**가 생길 수 있습니다.
- pull → GitHub에 코드 생성 → 그게 다시 push → Notion에 중복 페이지 생성 → 다시 pull...
- 당장은 두 워크플로우를 같은 레포/같은 폴더에 동시 적용하지 말고, 필요에 따라 하나만 골라 쓰는 걸 권장합니다.
- 둘 다 꼭 써야 한다면, pull 스크립트가 이 push-스크립트로 생성된 페이지를 건너뛰도록 Notion 쪽에 "source: github" 같은 태그/속성을 추가하고 필터링하는 로직이 필요합니다. 원하시면 이 부분도 만들어드릴게요.

## 주의사항
- **Notion 쪽에서 Integration이 해당 데이터베이스에 연결(Connect)되어 있어야** API로 읽을 수 있습니다.
  데이터베이스 페이지 우측 상단 `...` 메뉴 → `연결 추가(Add connections)` 에서 확인 가능 (소유자만 가능한 경우가 많음).
- 코드 블록이 없는 페이지는 자동으로 건너뜁니다.
- 언어(language) 인식은 Notion code 블록에 지정된 언어를 기준으로 확장자를 결정합니다. 목록에 없는 언어는 `.txt`로 저장되니, 필요하면 `notion_sync.py`의 `LANG_EXT` 딕셔너리에 추가하세요.
