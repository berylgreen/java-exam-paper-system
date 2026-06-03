const fs = require('fs');
const path = require('path');

const questionsFile = 'backend/src/main/resources/questions.json';
const data = JSON.parse(fs.readFileSync(questionsFile, 'utf8'));

// 工具方法：复制目录
function copyDir(src, dest) {
    if (!fs.existsSync(dest)) {
        fs.mkdirSync(dest, { recursive: true });
    }
    const entries = fs.readdirSync(src, { withFileTypes: true });
    for (let entry of entries) {
        const srcPath = path.join(src, entry.name);
        const destPath = path.join(dest, entry.name);
        if (entry.isDirectory()) {
            copyDir(srcPath, destPath);
        } else {
            fs.copyFileSync(srcPath, destPath);
        }
    }
}

// 递归查找目录下的某文件，返回文件的绝对路径
function findFile(dir, fileName) {
    const entries = fs.readdirSync(dir, { withFileTypes: true });
    for (let entry of entries) {
        const fullPath = path.join(dir, entry.name);
        if (entry.isDirectory()) {
            const result = findFile(fullPath, fileName);
            if (result) return result;
        } else if (entry.name === fileName) {
            return fullPath;
        }
    }
    return null;
}

// 提取代码块
function extractJavaBlocks(text) {
    const blocks = [];
    const regex = /```java\s*([\s\S]*?)\s*```/g;
    let match;
    while ((match = regex.exec(text)) !== null) {
        blocks.push(match[1].trim());
    }
    return blocks;
}

// 寻找第一个类名 (public class 或 class)
function extractClassName(code) {
    let match = code.match(/public\s+(?:abstract\s+)?class\s+([a-zA-Z0-9_]+)/);
    if (match) return match[1];
    match = code.match(/class\s+([a-zA-Z0-9_]+)/);
    if (match) return match[1];
    return null;
}

// 获取 package 的路径
function extractPackagePath(code) {
    let match = code.match(/package\s+([a-zA-Z0-9_\.]+)\s*;/);
    if (match) {
        return match[1].replace(/\./g, '/');
    }
    return null;
}

let updatedCount = 0;

for (let q of data) {
    if (q.type === 'PROGRAMMING' && q.projectPath) {
        // 创建答案工程的路径
        const projectName = path.basename(q.projectPath);
        // 如果是以 -question 结尾的，替换为 -answer，否则加 -answer
        const newProjectName = projectName.endsWith('-question') ? projectName.replace('-question', '-answer') : projectName + '-answer';
        const answerProjectPath = path.join('answer-projects', newProjectName).replace(/\\/g, '/');

        // 如果该题尚未生成，或者我们需要覆盖它
        console.log(`Processing: ${q.projectPath} -> ${answerProjectPath}`);
        
        const absSrc = path.resolve(q.projectPath);
        const absDest = path.resolve(answerProjectPath);

        if (!fs.existsSync(absSrc)) {
            console.warn(`Source project not found: ${absSrc}`);
            continue;
        }

        // 1. 复制原工程
        if (fs.existsSync(absDest)) {
            fs.rmSync(absDest, { recursive: true, force: true });
        }
        copyDir(absSrc, absDest);

        // 2. 提取 Java 代码并写入
        const javaBlocks = extractJavaBlocks(q.answer);
        if (javaBlocks.length === 0) {
            console.warn(`No java code blocks found in answer for question ID/content: ${q.content.substring(0, 30)}...`);
        }

        for (let block of javaBlocks) {
            const className = extractClassName(block);
            if (className) {
                const fileName = className + '.java';
                const pkgPath = extractPackagePath(block);

                let targetFilePath = findFile(absDest, fileName);
                
                if (targetFilePath) {
                    // 如果源工程已经有同名文件，覆盖它
                    fs.writeFileSync(targetFilePath, block, 'utf8');
                    console.log(`  Updated existing file: ${targetFilePath}`);
                } else {
                    // 源工程没有这个文件，我们需要在适当的位置新建
                    // 如果有包声明，创建包目录
                    let baseDir = path.join(absDest, 'src');
                    // 有的工程如果是maven的，在 src/main/java
                    if (fs.existsSync(path.join(absDest, 'src', 'main', 'java'))) {
                        baseDir = path.join(absDest, 'src', 'main', 'java');
                    }
                    
                    if (pkgPath) {
                        baseDir = path.join(baseDir, pkgPath);
                        if (!fs.existsSync(baseDir)) {
                            fs.mkdirSync(baseDir, { recursive: true });
                        }
                    } else {
                        // 如果没有包路径，默认找原工程里的 .java，看看它们通常在哪里
                        const someJava = findFile(absDest, 'Main.java');
                        if (someJava) {
                            baseDir = path.dirname(someJava);
                        }
                    }

                    const newFile = path.join(baseDir, fileName);
                    fs.writeFileSync(newFile, block, 'utf8');
                    console.log(`  Created new file: ${newFile}`);
                }
            } else {
                console.warn(`  Could not parse class name from block:\n${block.substring(0, 50)}...`);
            }
        }

        // 3. 更新 questions.json 记录
        q.answerProjectPath = answerProjectPath;
        updatedCount++;
    }
}

fs.writeFileSync(questionsFile, JSON.stringify(data, null, 2), 'utf8');
console.log(`\nSuccessfully updated ${updatedCount} programming questions.`);
