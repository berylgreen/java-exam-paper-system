# 集合框架 (List/Set) (银行系统)

银行系统原先使用定长对象数组保存账户列表。随着账户数量增加，这种方式容易出现容量不足、扩容不便，以及重复账户难以有效控制等问题。

请基于 Java 集合框架完成改造，要求如下：
1. 使用 `ArrayList` 或 `HashSet` 替代原有数组来管理账户对象。
2. 设计 `Account` 类，并根据“账户编号唯一”这一业务规则重写 `equals()` 和 `hashCode()`，以实现集合中的去重。
3. 为账户对象提供排序功能，可通过实现 `Comparable<Account>`接口或创建 `Comparator`，按照账户编号升序排序。
4. 编写示例代码，演示以下过程：
   - 向集合中添加多个账户对象；
   - 验证重复账户能够被正确去重；
   - 将数据按账户编号升序输出。


---

## 解决方案

```java
// 运行结果：
A001
A002
A003
```

解析如下：

1. `HashSet` 用于存储账户对象：
   - 与普通数组相比，集合容量可动态扩展；
   - `HashSet` 不允许存放重复元素，适合完成“账户去重”需求。

2. 在 `Account` 类中重写 `equals()` 和 `hashCode()`：
   - 题目要求按业务规则去重，这里规定“账户编号 `id` 唯一”；
   - 因此两个 `Account` 对象只要 `id` 相同，就应视为同一个账户；
   - `HashSet` 判断重复元素时会结合 `hashCode()` 和 `equals()`，所以这两个方法必须同时重写。

3. 实现 `Comparable<Account>`接口或创建 `Comparator`：
   - 通过重写 `compareTo()`，定义账户对象的自然排序规则；
   - 本题中按照账户编号的字典序升序排序。

4. 排序处理过程：
   - `HashSet` 本身不保证元素顺序；
   - 因此先将 `HashSet` 中的数据转换为 `ArrayList`；
   - 再通过 `Collections.sort(accountList)` 完成排序。

5. 原答案中的主要问题：
   - 将自定义类命名为 `ArrayList`，会与 Java 集合类 `ArrayList` 重名，容易引发混淆；
   - `Set<ArrayList>`、`List<ArrayList>` 的写法语义不正确，本题应以 `Account` 作为业务对象；
   - 改为 `Account` 类后，更符合银行账户管理场景，也更体现集合框架在去重与排序中的实际应用。

综上，使用 `HashSet` 负责去重，使用 `ArrayList` 配合排序完成有序输出，是本题较为合理的实现方式。

### 参考代码

```java
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

class Account implements Comparable<Account> {
    private String id;

    public Account(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Account account = (Account) o;
        return Objects.equals(id, account.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public int compareTo(Account other) {
        return this.id.compareTo(other.id);
    }
}

public class Main {
    public static void main(String[] args) {
        Set<Account> accountSet = new HashSet<>();

        accountSet.add(new Account("A002"));
        accountSet.add(new Account("A001"));
        accountSet.add(new Account("A003"));
        accountSet.add(new Account("A002")); // 重复账户，无法重复加入

        List<Account> accountList = new ArrayList<>(accountSet);
        Collections.sort(accountList);

        for (Account account : accountList) {
            System.out.println(account.getId());
        }
    }
}
```
