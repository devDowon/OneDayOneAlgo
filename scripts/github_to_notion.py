"""
GitHub push -> Notion 데이터베이스에 코드 페이지 자동 등록 스크립트

전제로 하는 레포 경로 규칙:
    {출처}/{알고리즘종류}/{문제번호}-{문제이름}/{코드파일}
    예: 프로그래머스/그리디/12938-이중우선순위큐/solution.py

동작 방식:
1. 이번 push로 추가/수정된 파일 목록을 받음 (workflow에서 git diff로 계산)
2. 지정된 확장자(코드 파일)만 필터링, 위 경로 규칙에 맞지 않으면 건너뜀
3. 경로에서 문제번호/문제이름/알고리즘종류/출처를 파싱
4. Notion 데이터베이스에 새 페이지 생성:
   - title 속성(예: "code") = 문제 이름
   - "문제 번호" 류 속성 = 파싱한 번호
   - "작성자" 류 속성 = 커밋 작성자 이름
   - "날짜" 류 속성(date 타입) = 커밋 일시
   - "알고리즘/종류/카테고리" 류 속성 = 파싱한 알고리즘 종류 (있는 경우에만)
   - "출처/source/플랫폼" 류 속성 = 파싱한 출처 (있는 경우에만)
   - "링크/url" 류 속성 = GitHub 파일 링크 (있는 경우에만)
   - 본문에 code 블록으로 파일 내용 삽입
   - "난이도"는 레포에 정보가 없어 자동으로 채우지 않음 (수동 입력)

필요한 환경변수:
- NOTION_TOKEN, DATABASE_ID
- COMMIT_AUTHOR   : 커밋 작성자 이름
- COMMIT_DATE     : 커밋 일시 (ISO 8601)
- REPO_URL        : GitHub 파일 링크 생성용 (예: https://github.com/org/repo/blob/main)
- CHANGED_FILES   : 개행으로 구분된 변경 파일 목록
"""

import os
import re
import json
import urllib.request
import urllib.error

NOTION_TOKEN = os.environ["NOTION_TOKEN"]
DATABASE_ID = os.environ["DATABASE_ID"]
COMMIT_AUTHOR = os.environ.get("COMMIT_AUTHOR", "")
COMMIT_DATE = os.environ.get("COMMIT_DATE", "")
REPO_BLOB_URL = os.environ.get("REPO_URL", "")
CHANGED_FILES = [f for f in os.environ.get("CHANGED_FILES", "").splitlines() if f.strip()]
NOTION_VERSION = "2022-06-28"
API_BASE = "https://api.notion.com/v1"

EXT_LANG = {
    "py": "python", "js": "javascript", "ts": "typescript", "java": "java",
    "c": "c", "cpp": "c++", "cs": "c#", "go": "go", "rs": "rust",
    "kt": "kotlin", "swift": "swift", "rb": "ruby", "php": "php", "sql": "sql",
}
# Notion code block content is limited to 2000 chars per rich_text segment,
# so long files are split into multiple segments automatically.
MAX_SEGMENT = 1900

# 경로 규칙: 출처/알고리즘종류/문제번호-문제이름/파일명
PATH_RE = re.compile(r"^(?P<source>[^/]+)/(?P<algo>[^/]+)/(?P<probdir>[^/]+)/(?P<filename>[^/]+)$")


def notion_request(path, method="GET", body=None):
    url = f"{API_BASE}{path}"
    data = json.dumps(body).encode("utf-8") if body is not None else None
    req = urllib.request.Request(url, data=data, method=method)
    req.add_header("Authorization", f"Bearer {NOTION_TOKEN}")
    req.add_header("Notion-Version", NOTION_VERSION)
    req.add_header("Content-Type", "application/json")
    try:
        with urllib.request.urlopen(req) as resp:
            return json.loads(resp.read().decode("utf-8"))
    except urllib.error.HTTPError as e:
        raise RuntimeError(f"Notion API error {e.code}: {e.read().decode('utf-8')}")


def get_database_schema():
    return notion_request(f"/databases/{DATABASE_ID}")["properties"]


def find_property(schema, candidates, ptype=None):
    for name, prop in schema.items():
        if name.lower() in candidates and (ptype is None or prop["type"] == ptype):
            return name, prop["type"]
    return None, None


def find_title_property(schema):
    for name, prop in schema.items():
        if prop["type"] == "title":
            return name
    raise RuntimeError("데이터베이스에 title 속성을 찾을 수 없습니다.")


def chunk_text(text, size=MAX_SEGMENT):
    return [text[i:i + size] for i in range(0, len(text), size)] or [""]


def parse_path(filepath):
    m = PATH_RE.match(filepath)
    if not m:
        return None
    source, algo, probdir, filename = m.group("source", "algo", "probdir", "filename")
    if "-" in probdir:
        num, name = probdir.split("-", 1)
    else:
        num, name = probdir, probdir

    # 프로그래머스 문제는 문제 번호를 실제 번호 대신 'P'로 고정
    if source.strip() == "프로그래머스":
        num = "P"

    return {
        "source": source,
        "algo": algo,
        "num": num.strip(),
        "name": name.strip(),
        "filename": filename,
    }


def set_prop(properties, schema, candidates, value):
    """스키마에 해당 속성이 존재할 때만 값을 채움 (없으면 조용히 무시)"""
    if value is None or value == "":
        return
    name, ptype = find_property(schema, candidates)
    if not name:
        return
    if ptype == "rich_text":
        properties[name] = {"rich_text": [{"text": {"content": str(value)[:2000]}}]}
    elif ptype == "select":
        properties[name] = {"select": {"name": str(value)}}
    elif ptype == "multi_select":
        properties[name] = {"multi_select": [{"name": str(value)}]}
    elif ptype == "number":
        digits = re.sub(r"[^0-9.\-]", "", str(value))
        if digits:
            properties[name] = {"number": float(digits)}
    elif ptype == "date":
        properties[name] = {"date": {"start": value}}
    elif ptype == "url":
        properties[name] = {"url": str(value)}


def build_properties(schema, parsed):
    title_name = find_title_property(schema)
    properties = {
        title_name: {"title": [{"text": {"content": parsed["name"][:2000]}}]}
    }

    set_prop(properties, schema, {"문제 번호", "문제번호", "번호", "no", "number"}, parsed["num"])
    set_prop(properties, schema, {"작성자", "이름", "author", "name", "person"}, COMMIT_AUTHOR)
    set_prop(properties, schema, {"날짜", "date"}, COMMIT_DATE)
    set_prop(properties, schema, {"알고리즘", "종류", "카테고리", "algorithm", "type", "category"}, parsed["algo"])
    set_prop(properties, schema, {"출처", "source", "플랫폼", "platform"}, parsed["source"])

    return properties


def create_page(schema, parsed, filepath, code, lang):
    properties = build_properties(schema, parsed)

    url_name, url_type = find_property(schema, {"link", "url", "링크", "깃허브", "github"})
    if url_name and url_type == "url" and REPO_BLOB_URL:
        properties[url_name] = {"url": f"{REPO_BLOB_URL}/{filepath}"}

    children = [{
        "object": "block",
        "type": "code",
        "code": {
            "language": lang,
            "rich_text": [{"type": "text", "text": {"content": seg}} for seg in chunk_text(code)],
        },
    }]

    body = {
        "parent": {"database_id": DATABASE_ID},
        "properties": properties,
        "children": children,
    }
    result = notion_request("/pages", "POST", body)
    return result.get("url", "(URL 없음)")


def main():
    if not CHANGED_FILES:
        print("변경된 코드 파일이 없습니다.")
        return

    schema = get_database_schema()
    print(f"대상 데이터베이스 ID: {DATABASE_ID}")

    for filepath in CHANGED_FILES:
        ext = filepath.rsplit(".", 1)[-1].lower() if "." in filepath else ""
        if ext not in EXT_LANG:
            continue
        if not os.path.exists(filepath):
            continue  # 삭제된 파일은 건너뜀

        parsed = parse_path(filepath)
        if parsed is None:
            print(f"  - 건너뜀 (경로 규칙 불일치): {filepath}")
            continue

        with open(filepath, "r", encoding="utf-8", errors="replace") as f:
            code = f.read()
        if not code.strip():
            continue

        lang = EXT_LANG[ext]
        print(f"Notion 페이지 생성 중: {filepath}  (문제번호={parsed['num']}, 이름={parsed['name']}, 종류={parsed['algo']}, 출처={parsed['source']})")
        page_url = create_page(schema, parsed, filepath, code, lang)
        print(f"  -> 생성 완료: {page_url}")

    print("완료")


if __name__ == "__main__":
    main()
