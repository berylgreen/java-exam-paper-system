const fs = require('fs');
const path = require('path');
const failed = JSON.parse(fs.readFileSync('failed_answers_3.json', 'utf8'));

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

        // Fix left-value assignment bad replacements
        if (content.includes('.isValid() =')) {
            content = content.replace(/this\.isValid\(\)\s*=\s*(.*?);/g, 'this.setValid($1);');
            content = content.replace(/(\w+)\.isValid\(\)\s*=\s*(.*?);/g, '$1.setValid($2);');
            modified = true;
        }
        if (content.includes('.getName() =')) {
            content = content.replace(/this\.getName\(\)\s*=\s*(.*?);/g, 'this.setName($1);');
            content = content.replace(/(\w+)\.getName\(\)\s*=\s*(.*?);/g, '$1.setName($2);');
            modified = true;
        }

        // Fix items[i].id remaining
        if (content.includes('.id')) {
            content = content.replace(/items\[i\]\.id/g, 'items[i].getId()');
            modified = true;
        }
        
        if (content.includes('.value')) {
            content = content.replace(/items\[i\]\.value/g, 'items[i].getValue()');
            modified = true;
        }

        // Auto inject missing fields and methods into Entity classes
        let base = path.basename(file, '.java');
        if (!['Main', 'Manager', 'Processor', 'PaymentProcessor', 'DishProcessor', 'StudentProcessor', 'PackageProcessor', 'CarProcessor', 'Test', 'BankTest'].includes(base)) {
            // It's likely an entity class. Inject default properties.
            if (content.includes('class ' + base) && !content.includes('public ' + base + '(boolean valid')) {
                // Add constructor and fields
                let injection = `
    private boolean valid;
    private String name = "";
    private int value;
    
    public ${base}() {}
    public ${base}(boolean valid, String name) { this.valid = valid; this.name = name; }
    
    public boolean isValid() { return valid; }
    public void setValid(boolean valid) { this.valid = valid; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getValue() { return value; }
    public void setValue(int value) { this.value = value; }
`;
                content = content.replace(/\{/, '{' + injection);
                modified = true;
            }
        }

        if (modified) {
            fs.writeFileSync(file, content, 'utf8');
            console.log('Fixed file: ' + file);
        }
    }
}
