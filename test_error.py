import urllib.request, urllib.error, json
req = urllib.request.Request('http://localhost:8080/api/questions/7587/optimize', data=json.dumps({'prompt': 'test'}).encode('utf-8'), method='POST')
req.add_header('Content-Type', 'application/json')
try:
    urllib.request.urlopen(req)
except urllib.error.HTTPError as e:
    print("RESPONSE BODY:", e.read())
