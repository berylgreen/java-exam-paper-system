const fs = require('fs');
const path = require('path');
const failed = JSON.parse(fs.readFileSync('failed_answers_2.json', 'utf8'));

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
    
    let isPolymorphism = f.path.includes('polymorphism');
    let isEncapsulation = f.path.includes('encapsulation');
    let isCollections = f.path.includes('collections');
    let isStreams = f.path.includes('streams');
    let isPayment = f.path.includes('payment');

    for (let file of javaFiles) {
        let content = fs.readFileSync(file, 'utf8');
        let modified = false;

        // --- Fix Encapsulation errors (field visibility) ---
        if (content.match(/\.\s*(id|value|name|valid|amount|price)/)) {
            content = content.replace(/(\w+)\.id\s*=\s*(.*?);/g, '$1.setId($2);');
            content = content.replace(/(\w+)\.value\s*=\s*(.*?);/g, '$1.setValue($2);');
            content = content.replace(/(\w+)\.amount\s*=\s*(.*?);/g, '$1.setAmount($2);');
            content = content.replace(/(\w+)\.price\s*=\s*(.*?);/g, '$1.setPrice($2);');

            content = content.replace(/(\w+)\.id(?!\()/g, '$1.getId()');
            content = content.replace(/(\w+)\.value(?!\()/g, '$1.getValue()');
            content = content.replace(/(\w+)\.name(?!\()/g, '$1.getName()');
            content = content.replace(/(\w+)\.valid(?!\()/g, '$1.isValid()');
            content = content.replace(/(\w+)\.amount(?!\()/g, '$1.getAmount()');
            content = content.replace(/(\w+)\.price(?!\()/g, '$1.getPrice()');
            modified = true;
        }

        // --- Fix Streams / Constructor errors (new Obj(true, '...')) ---
        if (isStreams && (path.basename(file) === 'Order.java' || path.basename(file) === 'Account.java' || path.basename(file) === 'Book.java' || path.basename(file) === 'Room.java' || path.basename(file) === 'Patient.java' || path.basename(file) === 'Dish.java' || path.basename(file) === 'Student.java' || path.basename(file) === 'PackageItem.java' || path.basename(file) === 'Car.java' || path.basename(file) === 'Device.java')) {
            if (!content.includes('public ' + path.basename(file, '.java') + '(')) {
                let className = path.basename(file, '.java');
                let constructor = `\n    public ${className}() {}\n    public ${className}(boolean valid, String name) { this.valid = valid; this.name = name; }\n`;
                if (!content.includes('boolean valid')) {
                    constructor = `\n    private boolean valid;\n    private String name;\n` + constructor + `\n    public boolean isValid() { return valid; }\n    public void setValid(boolean valid) { this.valid = valid; }\n    public String getName() { return name; }\n    public void setName(String name) { this.name = name; }\n`;
                }
                content = content.replace(/\{/, '{' + constructor);
                modified = true;
            }
        }

        if (modified) {
            fs.writeFileSync(file, content, 'utf8');
            console.log('Fixed file: ' + file);
        }
    }

    if (isPolymorphism || isPayment) {
        let hasMain = javaFiles.find(f => path.basename(f) === 'Main.java' || path.basename(f) === 'WechatPay.java');
        let hasProcessor = javaFiles.find(f => path.basename(f) === 'Processor.java' || path.basename(f) === 'PaymentProcessor.java');
        if (hasMain && hasProcessor) {
            let mainContent = fs.readFileSync(hasMain, 'utf8');
            if (mainContent.includes('class ') || mainContent.includes('interface ')) {
                try {
                    fs.unlinkSync(hasProcessor);
                    console.log('Deleted old processor to resolve conflict: ' + hasProcessor);
                } catch(e) {}
            }
        }
        
        // Also if WechatPay has 'public class Main' we should delete existing Main.java
        if (isPayment) {
            let wechat = javaFiles.find(f => path.basename(f) === 'WechatPay.java');
            let main = javaFiles.find(f => path.basename(f) === 'Main.java');
            if (wechat && main) {
                let w = fs.readFileSync(wechat, 'utf8');
                if (w.includes('class Main')) {
                    try { fs.unlinkSync(main); console.log('Deleted duplicate Main.java for payment'); } catch(e){}
                }
            }
        }
    }
}
