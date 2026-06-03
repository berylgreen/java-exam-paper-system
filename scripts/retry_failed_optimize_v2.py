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
    
    # 1. 获取所有最新的编程题（因为重启导致 ID 变化）
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

    # 2. 读取原来失败的下标 (1-based)
    try:
        # 注意：此处使用 utf-16 因为上个命令导出的是 UTF-16
        with open("failed_indices.txt", "r", encoding="utf-16") as f:
            lines = f.readlines()
        failed_indices = [int(line.strip()) for line in lines if line.strip().isdigit()]
    except Exception as e:
        print(f"读取 failed_indices.txt 失败: {e}")
        return
        
    print(f"共加载 {len(failed_indices)} 个失败的题目下标。")

    # 3. 逐题进行优化
    success_count = 0
    fail_count = 0

    for idx, q_idx in enumerate(failed_indices, 1):
        if q_idx > len(questions) or q_idx < 1:
            print(f"下标 {q_idx} 越界，跳过。")
            continue
            
        q = questions[q_idx - 1]
        q_id = q.get('id')
        print(f"[{idx}/{len(failed_indices)}] 正在重试原第 {q_idx} 题 (新 ID: {q_id}) ... ", end='', flush=True)
        
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
                # 智谱限流或请求体过长会返回 400 
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

    # 4. 导出最新题库并覆盖 backend/src/main/resources/questions.json
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
