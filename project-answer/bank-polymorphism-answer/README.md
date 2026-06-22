# 多态与继承 (银行系统)

某银行系统中有一个职责过重的统计类，它需要处理多种不同类型的账户对象。原有实现中，统计类通过大量的 `if (obj instanceof X) ... else if (obj instanceof Y) ...` 语句来区分账户类型并执行对应逻辑，导致代码可读性差、扩展性弱，也不利于后续维护。

**请使用面向对象中的多态思想对该设计进行重构，要求如下：**
1. 定义一个通用的 `Account` 抽象类或接口，用于描述账户的共同行为。
2. 为不同类型的账户创建具体子类，并在子类中实现各自的处理逻辑。
3. 重构统计类，使其只面向 `Account` 编程，通过多态调用统一处理账户对象，不再出现任何 `instanceof` 类型判断或分支处理。

**要求说明：**
- 代码应体现“统一抽象 + 子类实现 + 多态调用”的设计思想。
- 统计类只负责遍历和调用账户对象的行为，不负责判断具体类型。

## 测试数据示例
请在 `Main` 类中使用以下测试数据进行演示：
- 创建基础账户对象：`张三的账户`
- 创建高级账户对象：`李四的账户`
- 使用统一方法遍历调用处理逻辑

### 预期输出示例
```text
统一处理基础账户：张三的账户
统一处理高级账户：李四的账户
```

---

## 解决方案

本题的核心是使用**多态**替代原先基于 `instanceof` 的类型判断。

### 1. 原实现的问题
如果统计类中写成如下形式：

```java
if (obj instanceof RegularAccount) {
    // 处理普通账户
} else if (obj instanceof VIPAccount) {
    // 处理 VIP 账户
} else if (obj instanceof EnterpriseAccount) {
    // 处理企业账户
}
```

会带来以下问题：
- **可维护性差**：账户类型一多，分支会越来越长。
- **扩展性差**：每新增一种账户类型，都必须修改统计类代码。
- **违背开闭原则**：对扩展不够开放，对修改不够封闭。

### 2. 重构思路
先抽取所有账户共有的抽象行为，例如 `process()`，然后让不同账户子类分别实现自己的处理逻辑：
- `RegularAccount` 实现普通账户的处理；
- `VIPAccount` 实现 VIP 账户的处理；
- `EnterpriseAccount` 实现企业账户的处理。

这样，统计类只需要面向父类 `Account` 编程：

```java
for (Account account : accounts) {
    account.process();
}
```

程序在运行时会根据对象的实际类型自动调用对应子类的方法，这就是**多态**。

### 3. 重构后的优点
- **消除了类型判断**：不再需要 `instanceof`。
- **提高扩展性**：新增账户类型时，只需新增一个子类并实现 `process()` 方法，无需修改统计类。
- **符合面向对象设计原则**：体现了抽象、封装和多态，也更符合开闭原则。

### 4. 总结
本题重构的关键在于：
- 把“不同账户的不同处理逻辑”下放到各自子类中；
- 把“遍历并调用处理方法”保留在统计类中；
- 用多态机制替代分支判断，使系统结构更清晰、可维护性更强。

### 参考代码

```java
package com.exam.bank;
interface AccountProcessor { void process(); }
class NormalAccount implements AccountProcessor {
    private String name;
    public NormalAccount(String name) { this.name = name; }
    @Override public void process() { System.out.println("统一处理基础账户：" + name); }
}
class VIPAccount implements AccountProcessor {
    private String name;
    public VIPAccount(String name) { this.name = name; }
    @Override public void process() { System.out.println("统一处理高级账户：" + name); }
}
public class Main {
    public static void main(String[] args) {
        System.out.println("--- 执行测试用例 ---");
        AccountProcessor[] processors = { new NormalAccount("张三的账户"), new VIPAccount("李四的账户") };
        for (AccountProcessor p : processors) { p.process(); }
    }
}

```