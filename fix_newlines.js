const fs = require('fs');
const file = 'backend/src/main/resources/questions.json';
const data = JSON.parse(fs.readFileSync(file, 'utf8'));

data.forEach(q => {
    ['content', 'answer', 'explanation'].forEach(k => {
        if (typeof q[k] === 'string') {
            q[k] = q[k].split('\\n').join('\n');
        }
    });
});

fs.writeFileSync(file, JSON.stringify(data, null, 2), 'utf8');
console.log('Fixed newlines');
