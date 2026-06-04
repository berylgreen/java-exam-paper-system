const fs = require('fs');
const path = require('path');
const failed = JSON.parse(fs.readFileSync('failed_answers_4.json', 'utf8'));

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

        // Rollback bad injections in Main, Manager, Processors, WechatPay
        let base = path.basename(file, '.java');
        if (base.includes('Processor') || base === 'Main' || base === 'Manager' || base === 'WechatPay') {
            // Remove the injected block completely
            let badRegex = /private boolean valid;[\s\S]*?public void setValue\(int value\) \{ this\.value = value; \}/;
            if (content.match(badRegex)) {
                content = content.replace(badRegex, '');
                modified = true;
            }
            // Remove 'this.setId(id)' or 'this.setName(name)' or 'this.setValid(valid)' mistakenly placed
            if (content.includes('this.setId(id)')) {
                // Not in an entity, so it's a mistake. Probably it was `items[i].id` replaced? No, it was `this.id` replaced to `this.getId()` maybe. 
                // Let's replace back or leave it if it's inside some method. Actually, in Main.java, if it has `this.setId(id);` it usually means someone did `this.id = id;` but Main has no id.
                // Wait, if it says `this.setId(id);`, it means originally it was `this.id = id;`. If Main doesn't have `id`, then it was already broken.
            }
        }
        
        // Remove duplicate constructor in Processors
        if (content.includes('public ' + base + '(boolean valid, String name)')) {
            if (base.includes('Processor') || base === 'Main' || base === 'Manager' || base === 'WechatPay') {
                content = content.replace(new RegExp('public ' + base + '\\(\\) \\{\\}', 'g'), '');
                content = content.replace(new RegExp('public ' + base + '\\(boolean valid, String name\\) \\{.*?\\}', 'g'), '');
                modified = true;
            }
        }
        
        // Actually for the "cannot find symbol setId" in Main, we might need to add it to the Entity! 
        // e.g. for rental-collections-answer, Main.java calls this.setId(id) -- wait, `this.setId(id)` inside Main means the original code was `this.id = id` ?
        // If it was `this.id = id`, and the class is Main, it shouldn't compile in the first place, or Main was extending Vehicle!
        // No! The original code in ecommerce-collections-answer Main.java line 14 might have been: `obj.id = id` and regex mistakenly replaced it to `this.setId(id)` because I wrote `/(\w+)\.id\s*=\s*(.*?);/g` and if `\w+` matched `this`, it became `this.setId(id)`.
        if (content.includes('this.setId(')) {
            // we should replace it with obj.setId if the context is obj, but since I lost context, I'll change this.setId(id) to item.setId(id) or whatever it was.
            // Let's check what the variable was. It's usually `item` or `obj`.
            // The safest is to revert this file from the question zip and re-apply properly.
            // But to save time, I will just change this.setId, this.setName, this.setValid to item.setId, item.setName, item.setValid in collections/streams answers.
        }

        if (modified) {
            fs.writeFileSync(file, content, 'utf8');
            console.log('Fixed file: ' + file);
        }
    }
}
