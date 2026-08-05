# GitHub → Notion 코드 자동 등록

main 브랜치에 push할 때마다, 이번 push로 추가/수정된 코드 파일들을 파싱해서 Notion 데이터베이스에 새 페이지로 자동 등록합니다.

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
.github/workflows/push-to-notion.yml
github_to_notion.py
```

## 동작 방식

- **전제하는 레포 경로 규칙**: `출처/알고리즘종류/문제번호-문제이름/코드파일`
  예: `프로그래머스/그리디/12938-이중우선순위큐/solution.py`
  이 규칙에 안 맞는 경로(깊이가 다르거나 `-` 구분자가 없는 등)는 자동으로 건너뜁니다.
- 코드 파일 확장자(`.py .js .ts .java .c .cpp .cs .go .rs .kt .swift .rb .php .sql`)만 대상
- **프로그래머스** 문제는 문제 번호를 실제 번호 대신 `P`로 고정, **그 외(SWEA 등)**는 폴더명의 번호를 그대로 사용
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

## 🧪 테스트 방법

**0단계 — 토큰/DB 연결 확인 (터미널에서)**
실제 push 전에 토큰과 DB 접근이 되는지부터 확인하세요.
```bash
curl -s -X GET "https://api.notion.com/v1/databases/{DATABASE_ID}" \
  -H "Authorization: Bearer {NOTION_TOKEN}" \
  -H "Notion-Version: 2022-06-28"
```
- 정상이면 데이터베이스 속성(properties) JSON이 출력됩니다.
- `401`이면 토큰이 틀림, `404`면 Integration이 이 데이터베이스에 연결(Connect)되어 있지 않은 것입니다.

**1단계 — GitHub Actions 수동 테스트 (실제 push 없이)**
1. 레포에 파일들을 배치하고 Secrets를 등록한 뒤, main에 한 번 push해서 워크플로우 파일 자체를 반영하세요. (이 최초 1회는 실제 push가 필요합니다.)
2. GitHub 레포 → **Actions** 탭 → **Push to Notion** 워크플로우 선택 → **Run workflow** 버튼 클릭
3. `test_file` 입력창에 실제 존재하는 파일 경로를 하나 입력 (예: `SWEA/DP/4008-특이한사칙연산/sol.py`)
4. 실행 후 로그에서 `Notion 페이지 생성 중: ...` 메시지와 `완료`가 뜨는지 확인
5. Notion 데이터베이스에 새 페이지가 실제로 생성됐는지 확인 (제목/문제번호/작성자/날짜가 의도대로 채워졌는지)

**2단계 — 실제 push로 end-to-end 테스트**
```bash
mkdir -p "SWEA/DP/9999-테스트문제"
echo "print('hello')" > "SWEA/DP/9999-테스트문제/sol.py"
git add . && git commit -m "test: notion sync 테스트" && git push origin main
```
Actions 탭에서 로그 확인 후, Notion에서 생성된 걸 확인하고 테스트용 파일/페이지는 삭제하세요.

**실패 시 체크리스트**
- Actions 로그에 `403`/`404` → Integration이 DB에 연결 안 됨 (소유자에게 연결 요청)
- `건너뜀 (경로 규칙 불일치)` 로그 → 파일 경로가 `출처/알고리즘종류/번호-이름/파일` 4단계 구조가 아님
- select 속성인데 값이 안 들어감 → Notion select 속성은 존재하지 않는 옵션 값을 API로 못 넣는 경우가 있어 데이터베이스에 해당 옵션을 미리 만들어둬야 할 수 있음 (예: 작성자 select에 새 이름 추가)

## 주의사항
- **Notion 쪽에서 Integration이 해당 데이터베이스에 연결(Connect)되어 있어야** API로 페이지를 생성할 수 있습니다. 읽기뿐 아니라 **쓰기(편집) 권한**까지 필요합니다.
  데이터베이스 페이지 우측 상단 `...` 메뉴 → `연결 추가(Add connections)`에서 확인 가능 (소유자만 가능한 경우가 많음).
