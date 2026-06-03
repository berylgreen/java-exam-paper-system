import urllib.request
import urllib.error
import urllib.parse
import json
import time
import os

BASE_URL = "http://127.0.0.1:8080/api/questions"
PROMPT = "请优化这道编程题：题干表述更清晰，补充更严谨的答案与解析（如果涉及代码，请在 answer 和 explanation 字段中使用 Markdown 代码块排版）。"

def main():
    print("========== 开始重试失败的 AI 优化 ==========")
    
    # 1. 获取失败的 IDs
    try:
        with open("failed_ids.txt", "r", encoding="utf-16") as f:
            lines = f.readlines()
        failed_ids = [line.strip() for line in lines if line.strip()]
    except Exception as e:
        print(f"读取 failed_ids.txt 失败: {e}")
        return

    if not failed_ids:
        print("未找到任何失败的题目 ID。")
        return

    print(f"共加载 {len(failed_ids)} 个失败的题目 ID。")

    # 2. 逐题进行优化
    success_count = 0
    fail_count = 0

    for idx, q_id in enumerate(failed_ids, 1):
        print(f"[{idx}/{len(failed_ids)}] 正在重试题目 ID: {q_id} ... ", end='', flush=True)
        
        optimize_url = f"{BASE_URL}/{q_id}/optimize"
        payload = json.dumps({"prompt": PROMPT}).encode('utf-8')
        
        req = urllib.request.Request(optimize_url, data=payload, method='POST')
        req.add_header('Content-Type', 'application/json')
        
        max_retries = 3
        success = False
        
        for attempt in range(1, max_retries + 1):
            try:
                with urllib.request.urlopen(req, timeout=120) as response:
                    if response.status == 200:
                        print("成功")
                        success_count += 1
                        success = True
                        break
                    else:
                        print(f"失败 (状态码: {response.status})", end=' ')
            except urllib.error.HTTPError as e:
                # Spring Boot / 智谱可能返回 400
                print(f"(HTTP {e.code})", end=' ')
            except Exception as e:
                print(f"({e})", end=' ')
                
            if attempt < max_retries:
                print("重试中...", end=' ', flush=True)
                time.sleep(5)  # 遇到错误，退避更长时间

        if not success:
            print("最终失败")
            fail_count += 1
            
        # 成功后暂停 3 秒，防止连续请求过快触发 AI 服务的限流
        time.sleep(3)

    print(f"\n重试完成！成功: {success_count}，最终失败: {fail_count}")

    # 3. 导出最新题库并覆盖 backend/src/main/resources/questions.json
    print("\n正在导出最新题库数据...")
    try:
        export_url = f"{BASE_URL}/export"
        export_req = urllib.request.Request(export_url)
        with urllib.request.urlopen(export_req, timeout=60) as response:
            exported_json_bytes = response.read()
            
        target_path = os.path.join("backend", "src", "main", "resources", "questions.json")
        with open(target_path, "wb") as f:
            f.write(exported_json_bytes)
        print(f"已成功将最新题库保存至: {target_path}")
    except Exception as e:
        print(f"导出题库失败: {e}")

if __name__ == "__main__":
    main()
