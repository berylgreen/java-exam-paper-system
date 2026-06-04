const fs = require('fs');
const path = require('path');
const failed = JSON.parse(fs.readFileSync('failed_answers_7.json', 'utf8'));

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
        let originalContent = content;
        let base = path.basename(file, '.java');

        // Fix WechatPay specifically
        if (f.path.includes('payment-system-answer') && base === 'WechatPay') {
            content = content.replace(/public interface PaymentStrategy \{\s*\}/g, '');
            // remove redundant getters and setters injected into interface by previous bad runs
            content = content.replace(/public void setName\(String name\) \{ \}/g, '');
            content = content.replace(/public String getName\(\) \{ return ""; \}/g, '');
            content = content.replace(/public void setValid\(boolean valid\) \{ \}/g, '');
            content = content.replace(/public boolean isValid\(\) \{ return false; \}/g, '');
            content = content.replace(/public void setId\(String id\) \{ \}/g, '');
            content = content.replace(/public String getId\(\) \{ return ""; \}/g, '');
            content = content.replace(/public class WechatPay implements PaymentStrategy/g, 'public class WechatPay');
            content = content.replace(/class Alipay implements PaymentStrategy/g, 'class Alipay');
        }
        
        if (f.path.includes('payment-system-answer') && base === 'Order') {
            content = content.replace(/this\.setAmount\(amount\);/g, 'this.amount = amount;');
        }

        // We want to extract the class definition and remove ONLY the duplicates inside that specific class
        const entities = ['Order', 'Account', 'Book', 'Room', 'Patient', 'Dish', 'Student', 'PackageItem', 'Car', 'Device', 'Package', 'Vehicle'];
        for (const ent of entities) {
            // Because our injection block was literally exactly this block:
            let injectedRegexStr = '    private boolean valid;\\s*    private String name = "";\\s*    private int value;\\s*    private String id;\\s*    public ' + ent + '\\(\\) \\{\\}\\s*    public ' + ent + '\\(boolean valid, String name\\) \\{ this\\.valid = valid; this\\.name = name; \\}\\s*    public boolean isValid\\(\\) \\{ return valid; \\}\\s*    public void setValid\\(boolean valid\\) \\{ this\\.valid = valid; \\}\\s*    public String getName\\(\\) \\{ return name; \\}\\s*    public void setName\\(String name\\) \\{ this\\.name = name; \\}\\s*    public int getValue\\(\\) \\{ return value; \\}\\s*    public void setValue\\(int value\\) \\{ this\\.value = value; \\}\\s*    public String getId\\(\\) \\{ return id; \\}\\s*    public void setId\\(String id\\) \\{ this\\.id = id; \\}';
            
            let injectedRegex = new RegExp(injectedRegexStr, 'g');
            
            if (content.match(injectedRegex)) {
                // remove the entire block first
                content = content.replace(injectedRegex, '');
                
                // Now intelligently reinject missing parts into the class body
                let classRegexStr = 'class\\s+' + ent + '\\s*(?:implements\\s+[^{]+|extends\\s+[^{]+)?\\s*\\{';
                let classRegex = new RegExp(classRegexStr, 'g');
                
                content = content.replace(classRegex, (match) => {
                    let inject = '';
                    if (!content.includes('private boolean valid;')) inject += '\\n    private boolean valid;';
                    if (!content.includes('private String name;')) inject += '\\n    private String name = "";';
                    if (!content.includes('private int value;')) inject += '\\n    private int value;';
                    if (!content.includes('private String id;')) inject += '\\n    private String id;';
                    
                    if (!content.includes('public ' + ent + '()')) inject += '\\n    public ' + ent + '() {}';
                    if (!content.includes('public ' + ent + '(boolean valid, String name)')) inject += '\\n    public ' + ent + '(boolean valid, String name) { this.valid = valid; this.name = name; }';
                    
                    if (!content.includes('isValid()')) inject += '\\n    public boolean isValid() { return valid; }';
                    if (!content.includes('setValid(')) inject += '\\n    public void setValid(boolean valid) { this.valid = valid; }';
                    if (!content.includes('getName()')) inject += '\\n    public String getName() { return name; }';
                    if (!content.includes('setName(')) inject += '\\n    public void setName(String name) { this.name = name; }';
                    if (!content.includes('getValue()')) inject += '\\n    public int getValue() { return value; }';
                    if (!content.includes('setValue(')) inject += '\\n    public void setValue(int value) { this.value = value; }';
                    if (!content.includes('getId()')) inject += '\\n    public String getId() { return id; }';
                    if (!content.includes('setId(')) inject += '\\n    public void setId(String id) { this.id = id; }';
                    
                    return match + inject;
                });
            }
            
            // Clean up if there are double private String name; etc. due to other stuff
            content = content.replace(/\\n    private String name = "";\\n\\s*private String name;/g, '\\n    private String name;');
            content = content.replace(/\\n    private String id;\\n\\s*private String id;/g, '\\n    private String id;');
        }
        
        // Remove duplicate DishStatistics in restaurant-polymorphism-answer
        if (f.path.includes('restaurant-polymorphism-answer') && base === 'Dish') {
            content = content.replace(/public class DishStatistics \{/g, 'class DishStatistics {');
        }

        if (content !== originalContent) {
            // Replace literal \n with actual newlines in case my manual injection above caused literal \n text
            content = content.replace(/\\n/g, '\n');
            fs.writeFileSync(file, content, 'utf8');
            console.log('Cleaned up embedded variables in: ' + file);
        }
    }
}
