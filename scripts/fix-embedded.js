const fs = require('fs');
const path = require('path');
const failed = JSON.parse(fs.readFileSync('failed_answers_6.json', 'utf8'));

for (let f of failed) {
    const dir = path.resolve(f.path, 'src');
    if (!fs.existsSync(dir)) continue;

    function findJavaFiles(d) {
        let res = [];
        const entries = fs.readdirSync(d, { withFileTypes: true });
        for (let e of entries) {
            const p = path.join(d, e.name);
            if (e.isDirectory()) res = res.concat(findJavaFiles(p));
            else if (p.endsWith('.java')) res.push(p);
        }
        return res;
    }
    const javaFiles = findJavaFiles(dir);

    for (let file of javaFiles) {
        let content = fs.readFileSync(file, 'utf8');
        let modified = false;
        let base = path.basename(file, '.java');

        // 1. Revert bad item.set... in embedded classes
        if (content.includes('item.setId(id)')) {
            content = content.replace(/item\.setId\(id\);/g, 'this.id = id;');
            modified = true;
        }
        if (content.includes('item.setName(name)')) {
            content = content.replace(/item\.setName\(name\);/g, 'this.name = name;');
            modified = true;
        }
        if (content.includes('item.setValid(valid)')) {
            content = content.replace(/item\.setValid\(valid\);/g, 'this.valid = valid;');
            modified = true;
        }

        // 2. Inject properties and constructors into embedded classes
        const entities = ['Order', 'Account', 'Book', 'Room', 'Patient', 'Dish', 'Student', 'PackageItem', 'Car', 'Device', 'Package', 'Vehicle'];
        for (const ent of entities) {
            // Looking for 'class Order {' or 'class Order implements/extends'
            let regexStr = 'class\\s+' + ent + '\\s*(?:implements\\s+[^{]+|extends\\s+[^{]+)?\\s*\\{';
            let regex = new RegExp(regexStr, 'g');
            if (content.match(regex)) {
                // If it doesn't have the parameterized constructor, inject it
                if (!content.includes('public ' + ent + '(boolean valid, String name)')) {
                    let inject = `
    private boolean valid;
    private String name = "";
    private int value;
    private String id;
    
    public ${ent}() {}
    public ${ent}(boolean valid, String name) { this.valid = valid; this.name = name; }
    
    public boolean isValid() { return valid; }
    public void setValid(boolean valid) { this.valid = valid; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getValue() { return value; }
    public void setValue(int value) { this.value = value; }
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
`;
                    content = content.replace(regex, '$&' + inject);
                    modified = true;
                }
            }
        }

        // 3. Fix WechatPay.java constructor syntax
        if (f.path.includes('payment-system-answer') && base === 'WechatPay') {
            content = content.replace(/public WechatPay\(\) \{\}/g, '');
            content = content.replace(/public WechatPay\(boolean valid, String name\) \{\}/g, '');
            modified = true;
        }

        // 4. Resolve duplicate RegularPackage in logistics-polymorphism-answer
        if (f.path.includes('logistics-polymorphism-answer') && base === 'Test') {
            content = content.replace(/class RegularPackage extends Package \{ public void process\(\) \{\} \}/g, '');
            modified = true;
        }

        if (modified) {
            fs.writeFileSync(file, content, 'utf8');
            console.log('Fixed embedded issues in: ' + file);
        }
    }
}
