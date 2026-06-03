import urllib.request
import urllib.parse
import json
import time
import os

BASE_URL = "http://127.0.0.1:8080/api/questions"
PROMPT = "请优化这道编程题：题干表述更清晰，补充更严谨的答案与解析（如果涉及代码，请在 answer 和 explanation 字段中使用 Markdown 代码块排版）。"

def main():
    print("========== 开始批量 AI 优化编程题 ==========")
    
    # 1. 获取所有编程题
    print("正在获取所有编程题...")
    try:
        url = f"{BASE_URL}?size=2000&type=PROGRAMMING"
        req = urllib.request.Request(url)
        with urllib.request.urlopen(req) as response:
            res_data = json.loads(response.read().decode('utf-8'))
            questions = res_data.get('content', [])
    except Exception as e:
        print(f"获取题目失败: {e}")
        return

    if not questions:
        print("未找到任何编程题。")
        return

    print(f"共找到 {len(questions)} 道编程题。")

    # 2. 逐题进行优化
    success_count = 0
    fail_count = 0

    for idx, q in enumerate(questions, 1):
        q_id = q.get('id')
        print(f"[{idx}/{len(questions)}] 正在优化题目 ID: {q_id} ... ", end='', flush=True)
        
        optimize_url = f"{BASE_URL}/{q_id}/optimize"
        payload = json.dumps({"prompt": PROMPT}).encode('utf-8')
        
        req = urllib.request.Request(optimize_url, data=payload, method='POST')
        req.add_header('Content-Type', 'application/json')
        
        try:
            with urllib.request.urlopen(req) as response:
                if response.status == 200:
                    print("成功")
                    success_count += 1
                else:
                    print(f"失败 (状态码: {response.status})")
                    fail_count += 1
        except Exception as e:
            print(f"失败: {e}")
            fail_count += 1
            
        # 暂停一小会儿，防止请求过快触发 AI 服务的限流
        time.sleep(1)

    print(f"\n优化完成！成功: {success_count}，失败: {fail_count}")

    # 3. 导出最新题库并覆盖 backend/src/main/resources/questions.json
    print("\n正在导出最新题库数据...")
    try:
        export_url = f"{BASE_URL}/export"
        export_req = urllib.request.Request(export_url)
        with urllib.request.urlopen(export_req) as response:
            exported_json_bytes = response.read()
            
        target_path = os.path.join("backend", "src", "main", "resources", "questions.json")
        with open(target_path, "wb") as f:
            f.write(exported_json_bytes)
        print(f"已成功将最新题库保存至: {target_path}")
    except Exception as e:
        print(f"导出题库失败: {e}")

if __name__ == "__main__":
    main()
