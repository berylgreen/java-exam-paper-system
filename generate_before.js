const fs = require('fs');
const path = require('path');

const data = JSON.parse(fs.readFileSync('e:/3陈忱/1协和学院/协和学院教学/考试/java-exam-paper-system/prog_questions.json', 'utf8'));

const entityMap = {
    ecommerce: 'Order',
    bank: 'Account',
    library: 'Book',
    hotel: 'Room',
    hospital: 'Patient',
    restaurant: 'Dish',
    school: 'Student',
    logistics: 'Package', 
    rental: 'Vehicle', 
    smarthome: 'Device'
};

const domainNameMap = {
    ecommerce: '电商系统',
    bank: '银行系统',
    library: '图书馆系统',
    hotel: '酒店管理系统',
    hospital: '医疗系统',
    restaurant: '餐饮系统',
    school: '教务系统',
    logistics: '物流系统',
    rental: '租车系统',
    smarthome: '智能家居系统'
};

data.forEach(q => {
    if (!q.projectPath) return;
    const parts = q.projectPath.split('/');
    const folderName = parts[1];
    const [domain, topic] = folderName.split('-');
    
    let entity = entityMap[domain];
    if (domain === 'logistics') {
        if (topic === 'streams') entity = 'PackageItem';
        else entity = 'Package';
    }
    if (domain === 'rental') {
        if (topic === 'streams') entity = 'Car';
        else entity = 'Vehicle';
    }
    if (domain === 'payment' && topic === 'system') {
        // Handle payment-system-question manually
        return;
    }
    if (domain === 'shape' && topic === 'area') {
        // Handle shape-area-question manually
        return;
    }
    if (domain === 'student' && topic === 'roster') {
        // Handle student-roster-question manually
        return;
    }
    if (domain === 'exception' && topic === 'parser') {
        return;
    }
    if (domain === 'file' && topic === 'io') {
        return;
    }
    if (domain === 'ticket' && topic === 'thread') {
        return;
    }
    if (domain === 'weather' && topic === 'observer') {
        return;
    }
    if (domain === 'vehicle' && topic === 'factory') {
        return;
    }
    if (domain === 'generic' && topic === 'box') {
        return;
    }
    
    const srcDir = path.join('e:/3陈忱/1协和学院/协和学院教学/考试/java-exam-paper-system', q.projectPath, 'src/com/exam', domain);
    if (!fs.existsSync(srcDir)) {
        fs.mkdirSync(srcDir, {recursive: true});
    }

    const domainZh = domainNameMap[domain];
    let files = {};

    if (topic === 'encapsulation') {
        files[`${entity}.java`] = `package com.exam.${domain};

public class ${entity} {
    // 原始设计：属性直接暴露，破坏了封装性，也无法保证数据合法性
    public String id;
    public double value;
    
    // TODO: 1. 将属性改为 private
    // TODO: 2. 提供 Getter 和 Setter 方法
    // TODO: 3. 在 Setter 方法中加入数据有效性校验
}
`;
        files['Main.java'] = `package com.exam.${domain};

public class Main {
    public static void main(String[] args) {
        System.out.println("启动 ${domainZh} 模块测试...");
        ${entity} obj = new ${entity}();
        
        // 外部直接修改属性，可能导致非法数据
        obj.id = ""; 
        obj.value = -100;
        
        System.out.println("ID: " + obj.id + ", Value: " + obj.value);
    }
}
`;
    } else if (topic === 'polymorphism') {
        files[`${entity}.java`] = `package com.exam.${domain};

// 原始设计：仅作为标记类，没有统一的处理方法
public class ${entity} {
}

class Regular${entity} extends ${entity} {
}

class VIP${entity} extends ${entity} {
}
`;
        files['Processor.java'] = `package com.exam.${domain};

public class Processor {
    // 原始设计：使用大量 if-else 和 instanceof 进行类型判断
    public void processAll(${entity}[] items) {
        for (${entity} obj : items) {
            if (obj instanceof Regular${entity}) {
                System.out.println("处理普通业务逻辑");
            } else if (obj instanceof VIP${entity}) {
                System.out.println("处理VIP业务逻辑");
            } else {
                System.out.println("处理未知业务逻辑");
            }
        }
    }
}
`;
        files['Main.java'] = `package com.exam.${domain};

public class Main {
    public static void main(String[] args) {
        System.out.println("启动 ${domainZh} 模块测试...");
        ${entity}[] items = { new Regular${entity}(), new VIP${entity}() };
        Processor processor = new Processor();
        processor.processAll(items);
        
        // TODO: 使用多态重构，消除 Processor 中的 instanceof 判断
    }
}
`;
    } else if (topic === 'strategy') {
        files[`${entity}.java`] = `package com.exam.${domain};

public class ${entity} {
    public String id;
    public String type; // e.g. "TYPE_A", "TYPE_B"
    
    public ${entity}(String id, String type) {
        this.id = id;
        this.type = type;
    }
}
`;
        files['Handler.java'] = `package com.exam.${domain};

public class Handler {
    // 原始设计：使用 switch-case 区分不同处理策略，扩展性差
    public void handle(${entity} item) {
        switch (item.type) {
            case "TYPE_A":
                System.out.println("使用策略A处理: " + item.id);
                break;
            case "TYPE_B":
                System.out.println("使用策略B处理: " + item.id);
                break;
            default:
                System.out.println("未知策略");
        }
    }
}
`;
        files['Main.java'] = `package com.exam.${domain};

public class Main {
    public static void main(String[] args) {
        System.out.println("启动 ${domainZh} 模块测试...");
        Handler handler = new Handler();
        handler.handle(new ${entity}("001", "TYPE_A"));
        handler.handle(new ${entity}("002", "TYPE_B"));
        
        // TODO: 使用策略模式（Strategy Pattern）重构 switch-case 逻辑
    }
}
`;
    } else if (topic === 'exceptions') {
        files['DataParser.java'] = `package com.exam.${domain};

public class DataParser {
    // 原始设计：没有异常处理，遇到非法数据直接导致程序崩溃
    public void parseData(String[] data) {
        for (String s : data) {
            int value = Integer.parseInt(s);
            System.out.println("Parsed: " + value);
        }
    }
}
`;
        files['Main.java'] = `package com.exam.${domain};

public class Main {
    public static void main(String[] args) {
        System.out.println("启动 ${domainZh} 模块测试...");
        DataParser parser = new DataParser();
        String[] data = {"100", "200", "abc", "300"}; // 包含非法数据
        
        // 运行将会抛出 NumberFormatException 并中断程序
        parser.parseData(data);
        
        // TODO: 使用 try-catch 捕获异常，抛出自定义异常，保证后续数据继续处理
    }
}
`;
    } else if (topic === 'collections') {
        files[`${entity}.java`] = `package com.exam.${domain};

public class ${entity} {
    public String id;
    
    public ${entity}(String id) {
        this.id = id;
    }
    
    // TODO: 1. 重写 equals() 和 hashCode() 以实现去重
    // TODO: 2. 实现 Comparable 接口以支持排序
}
`;
        files['Manager.java'] = `package com.exam.${domain};

public class Manager {
    // 原始设计：使用定长数组，容量固定且难以去重
    private ${entity}[] items = new ${entity}[10];
    private int count = 0;
    
    public void add(${entity} item) {
        if (count < items.length) {
            items[count++] = item;
        } else {
            System.out.println("数组已满，无法添加！");
        }
    }
    
    public void printAll() {
        for (int i = 0; i < count; i++) {
            System.out.println("Item ID: " + items[i].id);
        }
    }
}
`;
        files['Main.java'] = `package com.exam.${domain};

public class Main {
    public static void main(String[] args) {
        System.out.println("启动 ${domainZh} 模块测试...");
        Manager manager = new Manager();
        manager.add(new ${entity}("001"));
        manager.add(new ${entity}("002"));
        manager.add(new ${entity}("001")); // 重复数据
        
        manager.printAll();
        
        // TODO: 使用 ArrayList 或 HashSet 替代定长数组，并实现去重和排序
    }
}
`;
    } else if (topic === 'hashmap') {
        files[`${entity}.java`] = `package com.exam.${domain};

public class ${entity} {
    public String id;
    public String info;
    
    public ${entity}(String id, String info) {
        this.id = id;
        this.info = info;
    }
}
`;
        files['Storage.java'] = `package com.exam.${domain};

import java.util.ArrayList;
import java.util.List;

public class Storage {
    // 原始设计：使用两个平行 List 存储 ID 和对象，查询效率低
    private List<String> ids = new ArrayList<>();
    private List<${entity}> items = new ArrayList<>();
    
    public void add(String id, ${entity} item) {
        ids.add(id);
        items.add(item);
    }
    
    public ${entity} get(String id) {
        int index = ids.indexOf(id);
        if (index != -1) {
            return items.get(index);
        }
        return null;
    }
    
    public void remove(String id) {
        int index = ids.indexOf(id);
        if (index != -1) {
            ids.remove(index);
            items.remove(index);
        }
    }
}
`;
        files['Main.java'] = `package com.exam.${domain};

public class Main {
    public static void main(String[] args) {
        System.out.println("启动 ${domainZh} 模块测试...");
        Storage storage = new Storage();
        storage.add("001", new ${entity}("001", "Info 1"));
        
        ${entity} item = storage.get("001");
        System.out.println("获取到: " + (item != null ? item.info : "null"));
        
        // TODO: 使用 HashMap 优化存储结构，提升查询效率
    }
}
`;
    } else if (topic === 'fileio') {
        files[`${entity}.java`] = `package com.exam.${domain};

public class ${entity} {
    // 基础实体类
}
`;
        files['Logger.java'] = `package com.exam.${domain};

public class Logger {
    // 原始设计：这是一个空方法
    public void writeLog(String record) {
        // TODO: 请使用 FileWriter 和 BufferedWriter 将 record 追加写入日志文件
        // TODO: 每次写入后换行，并使用 try-with-resources 保证流关闭
    }
}
`;
        files['Main.java'] = `package com.exam.${domain};

public class Main {
    public static void main(String[] args) {
        System.out.println("启动 ${domainZh} 模块测试...");
        Logger logger = new Logger();
        logger.writeLog("System started");
        logger.writeLog("Operation success");
        System.out.println("日志方法调用完成。");
    }
}
`;
    } else if (topic === 'multithreading') {
        files[`${entity}.java`] = `package com.exam.${domain};

public class ${entity} {
    // 原始设计：共享变量未做同步控制
    public int stock = 100;
}
`;
        files['Worker.java'] = `package com.exam.${domain};

public class Worker implements Runnable {
    private ${entity} target;
    
    public Worker(${entity} target) {
        this.target = target;
    }
    
    @Override
    public void run() {
        for (int i = 0; i < 10; i++) {
            // “读取-判断-修改” 分离，存在线程安全问题
            if (target.stock > 0) {
                try {
                    Thread.sleep(5); // 模拟耗时
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                target.stock--;
                System.out.println(Thread.currentThread().getName() + " 操作后，剩余: " + target.stock);
            }
        }
    }
}
`;
        files['Main.java'] = `package com.exam.${domain};

public class Main {
    public static void main(String[] args) {
        System.out.println("启动 ${domainZh} 模块测试...");
        ${entity} target = new ${entity}();
        
        Thread t1 = new Thread(new Worker(target), "Thread-1");
        Thread t2 = new Thread(new Worker(target), "Thread-2");
        Thread t3 = new Thread(new Worker(target), "Thread-3");
        
        t1.start();
        t2.start();
        t3.start();
        
        // TODO: 使用 synchronized 关键字解决多线程竞态条件，保证共享数据一致性
    }
}
`;
    } else if (topic === 'streams') {
        files[`${entity}.java`] = `package com.exam.${domain};

public class ${entity} {
    public boolean valid;
    public String name;
    
    public ${entity}(boolean valid, String name) {
        this.valid = valid;
        this.name = name;
    }
}
`;
        files['Processor.java'] = `package com.exam.${domain};

import java.util.ArrayList;
import java.util.List;

public class Processor {
    // 原始设计：使用大量 for 循环和嵌套 if 语句进行筛选和提取
    public List<String> processList(List<${entity}> list) {
        List<String> result = new ArrayList<>();
        for (${entity} item : list) {
            if (item.valid == true) {
                result.add(item.name);
            }
        }
        return result;
    }
}
`;
        files['Main.java'] = `package com.exam.${domain};

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("启动 ${domainZh} 模块测试...");
        List<${entity}> list = new ArrayList<>();
        list.add(new ${entity}(true, "Item A"));
        list.add(new ${entity}(false, "Item B"));
        list.add(new ${entity}(true, "Item C"));
        
        Processor processor = new Processor();
        List<String> result = processor.processList(list);
        System.out.println("处理结果: " + result);
        
        // TODO: 使用 Java 8 Stream API 重构 processList 方法
    }
}
`;
    }

    Object.keys(files).forEach(filename => {
        fs.writeFileSync(path.join(srcDir, filename), files[filename]);
    });
});
