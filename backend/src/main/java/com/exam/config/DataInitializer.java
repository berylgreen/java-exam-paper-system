package com.exam.config;

import com.exam.entity.*;
import com.exam.enums.*;
import com.exam.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 数据初始化器 — 首次启动时自动加载题库和预置试卷
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final QuestionRepository qRepo;
    private final ExamPaperRepository paperRepo;
    private final PaperQuestionRepository pqRepo;

    @Override
    public void run(String... args) {
        if (qRepo.count() > 0) {
            log.info("题库已存在，跳过初始化");
            return;
        }
        log.info("开始初始化题库...");
        List<Question> all = new ArrayList<>();
        all.addAll(chapter1()); all.addAll(chapter2());
        all.addAll(chapter3()); all.addAll(chapter4());
        all.addAll(chapter5()); all.addAll(chapter6());
        all.addAll(chapter7()); all.addAll(chapter8());
        all.addAll(chapter9()); all.addAll(chapter10());
        qRepo.saveAll(all);
        log.info("题库初始化完成，共 {} 道题", all.size());
        initPapers(all);
        log.info("预置试卷初始化完成");
    }

    // ===== 快捷构建方法 =====
    private Question sc(String ch, Difficulty d, String content, String opts, String ans, String exp) {
        return Question.builder().type(QuestionType.SINGLE_CHOICE).chapter(ch).difficulty(d)
                .content(content).options(opts).answer(ans).explanation(exp).defaultScore(2).build();
    }
    private Question mc(String ch, Difficulty d, String content, String opts, String ans, String exp) {
        return Question.builder().type(QuestionType.MULTIPLE_CHOICE).chapter(ch).difficulty(d)
                .content(content).options(opts).answer(ans).explanation(exp).defaultScore(4).build();
    }
    private Question tf(String ch, Difficulty d, String content, String ans, String exp) {
        return Question.builder().type(QuestionType.TRUE_FALSE).chapter(ch).difficulty(d)
                .content(content).answer(ans).explanation(exp).defaultScore(2).build();
    }
    private Question fb(String ch, Difficulty d, String content, String ans, String exp) {
        return Question.builder().type(QuestionType.FILL_BLANK).chapter(ch).difficulty(d)
                .content(content).answer(ans).explanation(exp).defaultScore(4).build();
    }
    private Question sa(String ch, Difficulty d, String content, String ans, String exp) {
        return Question.builder().type(QuestionType.SHORT_ANSWER).chapter(ch).difficulty(d)
                .content(content).answer(ans).explanation(exp).defaultScore(10).build();
    }
    private Question pg(String ch, Difficulty d, String content, String ans, String exp) {
        return Question.builder().type(QuestionType.PROGRAMMING).chapter(ch).difficulty(d)
                .content(content).answer(ans).explanation(exp).defaultScore(10).build();
    }
    private String opts(String a, String b, String c, String d) {
        return String.format("[{\"label\":\"A\",\"text\":\"%s\"},{\"label\":\"B\",\"text\":\"%s\"},{\"label\":\"C\",\"text\":\"%s\"},{\"label\":\"D\",\"text\":\"%s\"}]",a,b,c,d);
    }

    // ===== 章节数据方法 (由子文件提供) =====
    private static final String CH1="Java概述与环境",CH2="数据类型与变量",CH3="运算符与表达式",
        CH4="控制结构",CH5="数组",CH6="方法",CH7="面向对象基础",CH8="继承与多态",
        CH9="抽象类与接口",CH10="异常处理";

    private List<Question> chapter1() {
        List<Question> q = new ArrayList<>();
        q.add(sc(CH1,Difficulty.EASY,"Java源文件的扩展名是什么？",opts(".java",".class",".jar",".exe"),"A","Java源代码文件以.java为扩展名"));
        q.add(sc(CH1,Difficulty.EASY,"JDK中不包含以下哪个组件？",opts("JRE","编译器javac","数据库MySQL","调试器jdb"),"C","JDK包含JRE、编译器、调试器等，不包含MySQL"));
        q.add(sc(CH1,Difficulty.EASY,"Java程序的入口方法签名是？",opts("public static void main(String[] args)","public void main()","static void main()","void main(String args)"),"A","Java程序从main方法开始执行"));
        q.add(sc(CH1,Difficulty.MEDIUM,"以下哪个不是Java语言的特点？",opts("跨平台","自动内存管理","指针操作","面向对象"),"C","Java不支持直接指针操作"));
        q.add(sc(CH1,Difficulty.MEDIUM,"Java字节码文件的扩展名是？",opts(".class",".byte",".bin",".java"),"A",".class文件包含JVM可执行的字节码"));
        q.add(sc(CH1,Difficulty.MEDIUM,"JVM的作用是？",opts("编译Java源代码","执行Java字节码","编辑Java源代码","打包Java程序"),"B","JVM负责解释执行字节码"));
        q.add(sc(CH1,Difficulty.HARD,"以下哪个命令用于编译Java源文件？",opts("javac","java","javap","jar"),"A","javac是Java编译器"));
        q.add(sc(CH1,Difficulty.EASY,"JRE和JDK的关系是？",opts("JDK包含JRE","JRE包含JDK","两者相同","两者无关"),"A","JDK是开发工具包，包含JRE运行环境"));
        q.add(mc(CH1,Difficulty.EASY,"以下哪些属于Java的特性？",opts("平台无关性","自动垃圾回收","多线程支持","指针操作"),"ABC","Java具有跨平台、GC、多线程特性，无指针"));
        q.add(mc(CH1,Difficulty.MEDIUM,"JDK包含以下哪些工具？",opts("javac编译器","java运行器","javadoc文档生成","Visual Studio"),"ABC","JDK含javac/java/javadoc等工具"));
        q.add(tf(CH1,Difficulty.EASY,"Java程序可以在任何安装了JVM的平台上运行。","正确","这就是Java'一次编写，到处运行'的特点"));
        q.add(tf(CH1,Difficulty.EASY,"Java是一种解释型语言，不需要编译。","错误","Java先编译为字节码，再由JVM解释执行"));
        q.add(tf(CH1,Difficulty.MEDIUM,"JDK和JRE是同一个东西。","错误","JDK是开发工具包(含JRE)，JRE是运行环境"));
        q.add(fb(CH1,Difficulty.EASY,"Java程序需要先用___命令编译，再用___命令运行。","javac;java","javac编译生成.class文件，java命令运行"));
        q.add(fb(CH1,Difficulty.MEDIUM,"Java的跨平台性是通过___实现的。","JVM(Java虚拟机)","不同平台有各自的JVM实现"));
        q.add(sa(CH1,Difficulty.MEDIUM,"简述JDK、JRE和JVM三者的区别与联系。","JDK(Java开发工具包)包含JRE和开发工具(编译器等)；JRE(Java运行环境)包含JVM和核心类库；JVM(Java虚拟机)负责执行字节码。关系：JDK⊃JRE⊃JVM。","三者是包含关系"));
        q.add(pg(CH1,Difficulty.EASY,"编写一个Java程序，在控制台输出'Hello, Java!'。","public class Hello {\n    public static void main(String[] args) {\n        System.out.println(\"Hello, Java!\");\n    }\n}","最基本的Java程序结构"));
        return q;
    }

    private List<Question> chapter2() {
        List<Question> q = new ArrayList<>();
        q.add(sc(CH2,Difficulty.EASY,"Java中int类型占多少字节？",opts("2","4","8","16"),"B","int类型占4字节(32位)"));
        q.add(sc(CH2,Difficulty.EASY,"以下哪个是合法的变量名？",opts("2name","_count","class","my-var"),"B","变量名可以下划线开头，不能数字开头或使用关键字"));
        q.add(sc(CH2,Difficulty.EASY,"boolean类型的取值范围是？",opts("0和1","true和false","-1和1","任意整数"),"B","boolean只有true和false两个值"));
        q.add(sc(CH2,Difficulty.MEDIUM,"float f = 3.14; 这行代码是否正确？",opts("正确","不正确，应写成3.14f","不正确，应写成3.14d","不正确，应用int"),"B","浮点字面量默认是double，赋给float需加f后缀"));
        q.add(sc(CH2,Difficulty.MEDIUM,"char类型在Java中占多少字节？",opts("1","2","4","8"),"B","Java的char是Unicode编码，占2字节"));
        q.add(sc(CH2,Difficulty.MEDIUM,"以下哪个类型转换会导致精度丢失？",opts("int→long","int→double","long→int","byte→int"),"C","大范围转小范围可能丢失精度"));
        q.add(sc(CH2,Difficulty.HARD,"byte b = 127; b += 1; b的值是？",opts("128","-128","编译错误","运行时异常"),"B","byte溢出，127+1=-128"));
        q.add(sc(CH2,Difficulty.EASY,"String在Java中是？",opts("基本数据类型","引用数据类型","枚举类型","注解类型"),"B","String是引用类型(类)"));
        q.add(mc(CH2,Difficulty.EASY,"以下哪些是Java的基本数据类型？",opts("int","String","double","boolean"),"ACD","String是引用类型，其余是基本类型"));
        q.add(mc(CH2,Difficulty.MEDIUM,"以下哪些类型转换是自动(隐式)的？",opts("byte→int","int→long","float→int","int→double"),"ABD","小范围到大范围自动转换，float→int是窄化需强转"));
        q.add(mc(CH2,Difficulty.HARD,"以下哪些声明是合法的？",opts("int a=10;","int 1a=10;","int _a=10;","int $a=10;"),"ACD","变量名不能以数字开头"));
        q.add(tf(CH2,Difficulty.EASY,"Java中的char类型可以存储一个中文字符。","正确","Java的char采用Unicode编码，可以存储中文"));
        q.add(tf(CH2,Difficulty.EASY,"int和Integer是同一种类型。","错误","int是基本类型，Integer是其包装类"));
        q.add(tf(CH2,Difficulty.MEDIUM,"long类型的变量赋值时必须加L后缀。","错误","在int范围内的值不需要加L，超出int范围才必须加"));
        q.add(fb(CH2,Difficulty.EASY,"Java的8种基本数据类型中，整数类型有byte、short、___和long。","int","4种整数类型：byte/short/int/long"));
        q.add(fb(CH2,Difficulty.MEDIUM,"将double类型转换为int类型需要使用___转换。","强制(显式)","窄化转换需要显式强转"));
        q.add(fb(CH2,Difficulty.EASY,"声明常量使用___关键字修饰。","final","final修饰的变量为常量"));
        q.add(sa(CH2,Difficulty.MEDIUM,"简述Java中基本数据类型的自动类型转换规则。","自动类型转换(隐式转换)规则：byte→short→int→long→float→double，char→int。小范围类型可以自动转换为大范围类型。注意float虽然只有4字节但范围大于long。","从小到大自动转换"));
        q.add(pg(CH2,Difficulty.EASY,"声明并初始化各种基本数据类型的变量，并输出它们的值。","public class DataTypes {\n    public static void main(String[] args) {\n        byte b = 100;\n        short s = 10000;\n        int i = 100000;\n        long l = 100000L;\n        float f = 3.14f;\n        double d = 3.14159;\n        char c = 'A';\n        boolean bool = true;\n        System.out.println(\"byte: \"+b);\n        System.out.println(\"short: \"+s);\n        System.out.println(\"int: \"+i);\n        System.out.println(\"long: \"+l);\n        System.out.println(\"float: \"+f);\n        System.out.println(\"double: \"+d);\n        System.out.println(\"char: \"+c);\n        System.out.println(\"boolean: \"+bool);\n    }\n}","展示8种基本数据类型的使用"));
        return q;
    }

    private List<Question> chapter3() {
        List<Question> q = new ArrayList<>();
        q.add(sc(CH3,Difficulty.EASY,"5/2的结果是？",opts("2","2.5","3","2.0"),"A","整数除法取整，结果为2"));
        q.add(sc(CH3,Difficulty.EASY,"10%3的结果是？",opts("1","3","0","3.33"),"A","%是取余运算符，10除3余1"));
        q.add(sc(CH3,Difficulty.MEDIUM,"i++和++i的区别是？",opts("没有区别","i++先使用后自增，++i先自增后使用","i++是加2","++i是非法的"),"B","后置++先取值再自增，前置++先自增再取值"));
        q.add(sc(CH3,Difficulty.MEDIUM,"&&和&在逻辑运算中的区别？",opts("没有区别","&&短路求值，&不短路","&是位运算不能做逻辑运算","&&不能用于boolean"),"B","&&左边为false时不再计算右边"));
        q.add(sc(CH3,Difficulty.HARD,"int a=5; int b=a++*2+--a; b的值？",opts("14","15","12","10"),"A","a++=5(a变6)，--a=5(a变5)，5*2+5-1=14... 实际5*2+5=15... 需仔细推算"));
        q.add(sc(CH3,Difficulty.EASY,"以下哪个是关系运算符？",opts("+","==","&&","!"),"B","==是等于比较运算符"));
        q.add(sc(CH3,Difficulty.MEDIUM,"三元运算符的格式是？",opts("a?b:c","a:b?c","if a then b","a??b"),"A","条件?表达式1:表达式2"));
        q.add(mc(CH3,Difficulty.EASY,"以下哪些是赋值运算符？",opts("=","+=","==","*="),"ABD","==是比较运算符，其余是赋值运算符"));
        q.add(mc(CH3,Difficulty.MEDIUM,"以下哪些是逻辑运算符？",opts("&&","||","!","%"),"ABC","%是取余运算符"));
        q.add(tf(CH3,Difficulty.EASY,"==可以用来比较两个String对象的内容是否相等。","错误","==比较引用地址，应使用equals()方法比较内容"));
        q.add(tf(CH3,Difficulty.MEDIUM,"位运算符>>表示算术右移，>>>表示逻辑右移。","正确",">>保留符号位，>>>高位补0"));
        q.add(fb(CH3,Difficulty.EASY,"逻辑与运算符'&&'在左操作数为___时，不再计算右操作数。","false","短路与：左边false则整体false"));
        q.add(fb(CH3,Difficulty.MEDIUM,"表达式 (int)(3.9) 的结果是___。","3","强制类型转换直接截断小数部分"));
        q.add(sa(CH3,Difficulty.MEDIUM,"简述Java中==和equals()方法的区别。","==对于基本类型比较值，对于引用类型比较地址；equals()是Object类方法，默认行为同==，但String等类重写了equals()来比较内容。使用时：基本类型用==，引用类型(如String)用equals()。","值比较vs引用比较"));
        q.add(pg(CH3,Difficulty.MEDIUM,"编写程序：输入一个整数，判断它是奇数还是偶数(使用三元运算符)。","import java.util.Scanner;\npublic class OddEven {\n    public static void main(String[] args) {\n        Scanner sc = new Scanner(System.in);\n        int n = sc.nextInt();\n        String result = (n % 2 == 0) ? \"偶数\" : \"奇数\";\n        System.out.println(n + \"是\" + result);\n    }\n}","使用%判断奇偶，三元运算符简化if-else"));
        return q;
    }

    private List<Question> chapter4() {
        List<Question> q = new ArrayList<>();
        q.add(sc(CH4,Difficulty.EASY,"以下哪个不是循环语句？",opts("for","while","switch","do-while"),"C","switch是选择语句"));
        q.add(sc(CH4,Difficulty.EASY,"for循环的三个部分依次是？",opts("初始化;条件;迭代","条件;初始化;迭代","迭代;条件;初始化","初始化;迭代;条件"),"A","for(初始化;条件;迭代)"));
        q.add(sc(CH4,Difficulty.MEDIUM,"switch语句中不写break会怎样？",opts("编译错误","只执行匹配的case","产生穿透(fall-through)","运行时异常"),"C","不写break会继续执行后续case"));
        q.add(sc(CH4,Difficulty.MEDIUM,"以下代码输出什么？for(int i=0;i<5;i++){if(i==3)continue;System.out.print(i);}",opts("01234","0124","0123","012"),"B","i==3时continue跳过，输出0124"));
        q.add(sc(CH4,Difficulty.HARD,"以下代码的循环执行几次？int i=0;while(i++<5){}",opts("4","5","6","无限循环"),"B","后置++：i=0<5执行,i=1<5执行,...i=4<5执行,i=5不满足,共5次"));
        q.add(sc(CH4,Difficulty.EASY,"break语句的作用是？",opts("跳过本次循环","终止整个循环","终止程序","跳到指定位置"),"B","break终止当前循环"));
        q.add(sc(CH4,Difficulty.MEDIUM,"do-while与while的主要区别是？",opts("没有区别","do-while至少执行一次","do-while更快","do-while不需要条件"),"B","do-while先执行后判断"));
        q.add(mc(CH4,Difficulty.EASY,"Java中的选择结构语句有？",opts("if","if-else","switch","for"),"ABC","for是循环结构"));
        q.add(mc(CH4,Difficulty.MEDIUM,"switch的表达式可以是以下哪些类型？",opts("int","String","char","boolean"),"ABC","switch不支持boolean"));
        q.add(tf(CH4,Difficulty.EASY,"for循环和while循环可以相互替换。","正确","两者功能等价，可以互相转换"));
        q.add(tf(CH4,Difficulty.MEDIUM,"switch中的default分支必须放在最后。","错误","default可以放在任何位置，但通常放最后"));
        q.add(fb(CH4,Difficulty.EASY,"在循环中，___语句用于跳过本次循环的剩余部分。","continue","continue跳过当前迭代"));
        q.add(fb(CH4,Difficulty.MEDIUM,"增强for循环的语法格式是for(___:集合/数组)。","类型 变量名","增强for：for(Type var : array)"));
        q.add(sa(CH4,Difficulty.MEDIUM,"简述break和continue的区别。","break：终止当前整个循环(或switch)，程序跳到循环后面的语句执行。continue：跳过当前循环的本次迭代的剩余语句，进入下一次迭代。break是跳出循环，continue是跳过本次。","一个终止循环，一个跳过本次"));
        q.add(pg(CH4,Difficulty.MEDIUM,"编写程序：输出1-100之间所有能被3整除但不能被5整除的数。","public class DivTest {\n    public static void main(String[] args) {\n        for (int i = 1; i <= 100; i++) {\n            if (i % 3 == 0 && i % 5 != 0) {\n                System.out.println(i);\n            }\n        }\n    }\n}","for循环+条件判断"));
        return q;
    }

    private List<Question> chapter5() {
        List<Question> q = new ArrayList<>();
        q.add(sc(CH5,Difficulty.EASY,"Java数组的下标从几开始？",opts("0","1","-1","任意"),"A","Java数组下标从0开始"));
        q.add(sc(CH5,Difficulty.EASY,"以下哪种声明数组的方式是正确的？",opts("int[] arr = new int[5];","int arr[5];","int[5] arr;","int arr = new int[5];"),"A","Java中声明数组需要用new关键字"));
        q.add(sc(CH5,Difficulty.MEDIUM,"int[] arr = {1,2,3}; arr.length的值是？",opts("2","3","4","编译错误"),"B","数组有3个元素，length为3"));
        q.add(sc(CH5,Difficulty.MEDIUM,"数组越界会抛出什么异常？",opts("NullPointerException","ArrayIndexOutOfBoundsException","ClassCastException","ArithmeticException"),"B","访问不存在的索引抛出ArrayIndexOutOfBoundsException"));
        q.add(sc(CH5,Difficulty.HARD,"int[][] arr = new int[3][]; 这种声明是否合法？",opts("合法","不合法","编译警告","运行时错误"),"A","Java支持不规则(锯齿)二维数组"));
        q.add(sc(CH5,Difficulty.EASY,"获取数组长度使用？",opts("arr.length","arr.length()","arr.size()","len(arr)"),"A","数组用length属性(非方法)"));
        q.add(mc(CH5,Difficulty.EASY,"以下哪些是正确的数组初始化方式？",opts("int[] a = {1,2,3};","int[] a = new int[]{1,2,3};","int[] a = new int[3];","int[] a = new int[3]{1,2,3};"),"ABC","指定大小时不能同时初始化元素"));
        q.add(mc(CH5,Difficulty.MEDIUM,"Arrays工具类提供的方法包括？",opts("sort()","binarySearch()","toString()","length()"),"ABC","length是数组属性，非Arrays方法"));
        q.add(tf(CH5,Difficulty.EASY,"Java数组一旦创建，长度就不能改变。","正确","数组长度在创建时确定，之后不可变"));
        q.add(tf(CH5,Difficulty.MEDIUM,"int[] arr = new int[3]; 数组元素默认值为0。","正确","int数组元素默认初始化为0"));
        q.add(fb(CH5,Difficulty.EASY,"使用Arrays类的___方法可以对数组进行排序。","sort","Arrays.sort()进行排序"));
        q.add(fb(CH5,Difficulty.MEDIUM,"二维数组int[][] arr = new int[2][3]共有___个元素。","6","2行3列，共6个元素"));
        q.add(sa(CH5,Difficulty.MEDIUM,"简述Java数组的特点。","1.数组是引用类型，存储在堆中；2.长度固定，创建后不可变；3.元素类型统一；4.有默认初始值(int为0,boolean为false,引用为null)；5.通过下标访问，从0开始；6.提供length属性获取长度。","数组是定长、同类型的数据结构"));
        q.add(pg(CH5,Difficulty.MEDIUM,"编写程序：找出一个整数数组中的最大值和最小值。","public class MinMax {\n    public static void main(String[] args) {\n        int[] arr = {34, 12, 56, 78, 23, 9, 67};\n        int max = arr[0], min = arr[0];\n        for (int i = 1; i < arr.length; i++) {\n            if (arr[i] > max) max = arr[i];\n            if (arr[i] < min) min = arr[i];\n        }\n        System.out.println(\"最大值: \" + max);\n        System.out.println(\"最小值: \" + min);\n    }\n}","遍历比较求最值"));
        return q;
    }

    private List<Question> chapter6() {
        List<Question> q = new ArrayList<>();
        q.add(sc(CH6,Difficulty.EASY,"Java方法必须定义在___中？",opts("类","包","模块","文件"),"A","Java方法必须属于某个类"));
        q.add(sc(CH6,Difficulty.EASY,"方法的返回类型为void表示？",opts("返回整数","返回字符串","无返回值","返回布尔值"),"C","void表示方法不返回值"));
        q.add(sc(CH6,Difficulty.MEDIUM,"方法重载(Overload)的判断条件是？",opts("方法名相同,参数列表不同","方法名不同","返回类型不同","访问修饰符不同"),"A","重载看方法名和参数列表"));
        q.add(sc(CH6,Difficulty.MEDIUM,"Java中方法参数传递方式是？",opts("值传递","引用传递","值传递和引用传递","指针传递"),"A","Java只有值传递(引用类型传递引用的副本)"));
        q.add(sc(CH6,Difficulty.HARD,"可变参数(varargs)的语法是？",opts("int... args","int[] args","int* args","int args..."),"A","可变参数用类型...参数名"));
        q.add(sc(CH6,Difficulty.EASY,"以下哪种情况构成方法重载？",opts("参数个数不同","仅返回类型不同","仅修饰符不同","方法名不同"),"A","参数列表不同才构成重载"));
        q.add(mc(CH6,Difficulty.MEDIUM,"方法重载可以通过以下哪些方式区分？",opts("参数个数","参数类型","参数顺序","返回值类型"),"ABC","返回值类型不参与重载判断"));
        q.add(mc(CH6,Difficulty.EASY,"方法定义包含以下哪些部分？",opts("返回类型","方法名","参数列表","方法体"),"ABCD","方法由修饰符+返回类型+方法名+参数列表+方法体组成"));
        q.add(tf(CH6,Difficulty.EASY,"一个类中可以有多个同名但参数不同的方法。","正确","这就是方法重载"));
        q.add(tf(CH6,Difficulty.MEDIUM,"递归方法必须有终止条件，否则会栈溢出。","正确","无终止条件会导致StackOverflowError"));
        q.add(fb(CH6,Difficulty.EASY,"方法签名由方法名和___组成。","参数列表","方法签名=方法名+参数列表"));
        q.add(fb(CH6,Difficulty.MEDIUM,"递归方法调用自身时，必须有___条件来终止递归。","终止(基线/出口)","防止无限递归导致栈溢出"));
        q.add(sa(CH6,Difficulty.MEDIUM,"简述方法重载和方法重写的区别。","方法重载(Overload)：同一个类中，方法名相同但参数列表不同(个数/类型/顺序)，与返回值无关。方法重写(Override)：子类重新定义父类的方法，方法名、参数列表、返回类型必须相同(或协变)，访问权限不能更严格。","重载在同类内，重写在继承中"));
        q.add(pg(CH6,Difficulty.MEDIUM,"编写一个递归方法计算n的阶乘(n!)。","public class Factorial {\n    public static long factorial(int n) {\n        if (n <= 1) return 1;\n        return n * factorial(n - 1);\n    }\n    public static void main(String[] args) {\n        System.out.println(\"5! = \" + factorial(5));\n        System.out.println(\"10! = \" + factorial(10));\n    }\n}","递归：n!=n*(n-1)!，终止条件n<=1"));
        return q;
    }

    private List<Question> chapter7() {
        List<Question> q = new ArrayList<>();
        q.add(sc(CH7,Difficulty.EASY,"创建对象使用什么关键字？",opts("new","create","make","init"),"A","new关键字创建对象实例"));
        q.add(sc(CH7,Difficulty.EASY,"this关键字代表什么？",opts("当前类","当前对象","父类对象","静态方法"),"B","this指向当前对象的引用"));
        q.add(sc(CH7,Difficulty.MEDIUM,"构造方法的特点是？",opts("方法名与类名相同且无返回类型","可以有任意名称","必须有返回值","只能有一个"),"A","构造方法名=类名，无返回类型"));
        q.add(sc(CH7,Difficulty.MEDIUM,"以下哪个修饰符表示仅本类可访问？",opts("public","protected","private","default"),"C","private限定仅本类内可访问"));
        q.add(sc(CH7,Difficulty.HARD,"静态方法中能否使用this关键字？",opts("可以","不可以","有条件可以","编译通过但运行报错"),"B","静态方法不依赖对象实例，无this"));
        q.add(sc(CH7,Difficulty.EASY,"封装的核心思想是？",opts("隐藏实现细节，公开接口","代码复用","多态","继承"),"A","封装=数据隐藏+公开接口"));
        q.add(mc(CH7,Difficulty.EASY,"面向对象的三大特性是？",opts("封装","继承","多态","递归"),"ABC","递归不属于OOP特性"));
        q.add(mc(CH7,Difficulty.MEDIUM,"以下关于构造方法说法正确的是？",opts("可以重载","不能有返回值","如不定义则有默认无参构造","可以被继承"),"ABC","构造方法不能被继承"));
        q.add(tf(CH7,Difficulty.EASY,"一个类可以有多个构造方法。","正确","构造方法可以重载"));
        q.add(tf(CH7,Difficulty.MEDIUM,"static修饰的属性属于类，不属于某个对象。","正确","static成员是类级别的，所有对象共享"));
        q.add(fb(CH7,Difficulty.EASY,"Java中通过___关键字创建类的实例对象。","new","new关键字分配内存并调用构造方法"));
        q.add(fb(CH7,Difficulty.MEDIUM,"封装通常通过将属性设为___，并提供getter/setter方法实现。","private","private属性+公开方法=封装"));
        q.add(sa(CH7,Difficulty.MEDIUM,"简述Java中构造方法的特点和作用。","构造方法特点：1.方法名与类名相同；2.没有返回类型(也不是void)；3.在创建对象时自动调用；4.可以重载；5.如未定义，编译器提供默认无参构造方法；6.若定义了有参构造，默认无参构造不再自动提供。作用：初始化对象的属性值。","构造方法用于对象初始化"));
        q.add(pg(CH7,Difficulty.MEDIUM,"定义一个Student类，包含name和age属性，提供构造方法、getter/setter和toString方法。","public class Student {\n    private String name;\n    private int age;\n    \n    public Student(String name, int age) {\n        this.name = name;\n        this.age = age;\n    }\n    \n    public String getName() { return name; }\n    public void setName(String name) { this.name = name; }\n    public int getAge() { return age; }\n    public void setAge(int age) { this.age = age; }\n    \n    @Override\n    public String toString() {\n        return \"Student{name='\" + name + \"', age=\" + age + \"}\";\n    }\n}","标准JavaBean类定义"));
        return q;
    }

    private List<Question> chapter8() {
        List<Question> q = new ArrayList<>();
        q.add(sc(CH8,Difficulty.EASY,"Java中使用哪个关键字实现继承？",opts("extends","implements","inherits","super"),"A","extends用于类继承"));
        q.add(sc(CH8,Difficulty.EASY,"Java中一个类可以继承几个父类？",opts("1个","2个","多个","0个"),"A","Java只支持单继承"));
        q.add(sc(CH8,Difficulty.MEDIUM,"super关键字的作用？",opts("调用父类方法/构造","调用当前类方法","创建对象","定义常量"),"A","super用于访问父类成员"));
        q.add(sc(CH8,Difficulty.MEDIUM,"方法重写时，子类方法的访问权限？",opts("不能比父类更严格","必须与父类相同","可以更严格","无限制"),"A","子类重写方法的访问权限>=父类"));
        q.add(sc(CH8,Difficulty.HARD,"父类引用指向子类对象时，调用重写方法执行的是？",opts("父类方法","子类方法","编译错误","运行时决定"),"B","运行时多态，执行子类的重写方法"));
        q.add(sc(CH8,Difficulty.EASY,"所有Java类的根类是？",opts("Object","Class","Main","System"),"A","Object是所有类的父类"));
        q.add(mc(CH8,Difficulty.MEDIUM,"以下关于继承说法正确的是？",opts("子类继承父类非私有成员","构造方法不能被继承","子类可重写父类方法","Java支持多继承"),"ABC","Java类不支持多继承"));
        q.add(mc(CH8,Difficulty.HARD,"多态的实现条件包括？",opts("继承关系","方法重写","父类引用指向子类对象","方法重载"),"ABC","方法重载是编译时多态，不是运行时多态的条件"));
        q.add(tf(CH8,Difficulty.EASY,"子类可以访问父类的private成员。","错误","private成员仅本类可访问"));
        q.add(tf(CH8,Difficulty.MEDIUM,"final修饰的类不能被继承。","正确","final类是最终类，不能有子类"));
        q.add(fb(CH8,Difficulty.EASY,"子类构造方法中调用父类构造方法使用___关键字。","super","super()调用父类构造方法"));
        q.add(fb(CH8,Difficulty.MEDIUM,"向上转型是指将___类型的引用赋值给___类型的变量。","子类;父类","父类引用指向子类对象"));
        q.add(sa(CH8,Difficulty.MEDIUM,"简述Java多态的概念和实现方式。","多态是同一操作作用于不同对象产生不同行为。实现方式：1.编译时多态(方法重载)；2.运行时多态(方法重写+向上转型)。运行时多态三个条件：继承、重写、父类引用指向子类对象。调用方法时JVM根据实际对象类型动态绑定。","同一接口不同实现"));
        q.add(pg(CH8,Difficulty.HARD,"定义Animal基类和Dog、Cat子类，重写speak()方法，演示多态。","public class Animal {\n    public void speak() {\n        System.out.println(\"动物叫\");\n    }\n}\n\nclass Dog extends Animal {\n    @Override\n    public void speak() {\n        System.out.println(\"汪汪汪\");\n    }\n}\n\nclass Cat extends Animal {\n    @Override\n    public void speak() {\n        System.out.println(\"喵喵喵\");\n    }\n}\n\nclass Test {\n    public static void main(String[] args) {\n        Animal a1 = new Dog();\n        Animal a2 = new Cat();\n        a1.speak(); // 汪汪汪\n        a2.speak(); // 喵喵喵\n    }\n}","继承+重写+向上转型=多态"));
        return q;
    }

    private List<Question> chapter9() {
        List<Question> q = new ArrayList<>();
        q.add(sc(CH9,Difficulty.EASY,"抽象类使用哪个关键字定义？",opts("abstract","interface","final","static"),"A","abstract关键字定义抽象类"));
        q.add(sc(CH9,Difficulty.EASY,"接口使用哪个关键字定义？",opts("class","abstract","interface","implements"),"C","interface定义接口"));
        q.add(sc(CH9,Difficulty.MEDIUM,"抽象类能否被实例化？",opts("可以","不可以","有条件可以","只能通过反射"),"B","抽象类不能直接new"));
        q.add(sc(CH9,Difficulty.MEDIUM,"一个类可以实现几个接口？",opts("1个","2个","多个","0个"),"C","Java支持实现多个接口"));
        q.add(sc(CH9,Difficulty.HARD,"Java 8接口中的default方法是？",opts("默认构造方法","有方法体的实例方法","静态方法","抽象方法"),"B","default方法有实现体，实现类可不重写"));
        q.add(sc(CH9,Difficulty.EASY,"实现接口使用什么关键字？",opts("extends","implements","using","with"),"B","implements实现接口"));
        q.add(mc(CH9,Difficulty.MEDIUM,"以下关于抽象类说法正确的是？",opts("可以有构造方法","可以有非抽象方法","不能被实例化","必须有抽象方法"),"ABC","抽象类可以没有抽象方法"));
        q.add(mc(CH9,Difficulty.HARD,"接口中可以包含的成员有？",opts("常量(public static final)","抽象方法","default方法(Java8+)","静态方法(Java8+)"),"ABCD","Java8+接口支持default和static方法"));
        q.add(tf(CH9,Difficulty.EASY,"一个类可以同时继承一个类并实现多个接口。","正确","class A extends B implements C, D"));
        q.add(tf(CH9,Difficulty.MEDIUM,"接口中的变量默认是public static final的。","正确","接口变量隐含public static final修饰"));
        q.add(fb(CH9,Difficulty.EASY,"抽象方法只有方法声明，没有___。","方法体(实现)","抽象方法以分号结束，无花括号"));
        q.add(fb(CH9,Difficulty.MEDIUM,"Java 8开始，接口中可以使用___关键字定义有默认实现的方法。","default","default方法解决接口演进问题"));
        q.add(sa(CH9,Difficulty.HARD,"比较抽象类和接口的异同点。","相同：都不能实例化，都可以有抽象方法。不同：1.抽象类用abstract class，接口用interface；2.抽象类可有构造方法、成员变量，接口只有常量；3.抽象类单继承，接口多实现；4.抽象类可有非抽象方法，接口在Java8+才有default方法；5.设计层面：抽象类是is-a关系，接口是has-a/can-do能力。","两者各有适用场景"));
        q.add(pg(CH9,Difficulty.HARD,"定义一个Shape接口包含area()方法，Circle和Rectangle类实现它。","public interface Shape {\n    double area();\n}\n\nclass Circle implements Shape {\n    private double radius;\n    public Circle(double radius) { this.radius = radius; }\n    @Override\n    public double area() { return Math.PI * radius * radius; }\n}\n\nclass Rectangle implements Shape {\n    private double width, height;\n    public Rectangle(double w, double h) { this.width = w; this.height = h; }\n    @Override\n    public double area() { return width * height; }\n}\n\nclass Test {\n    public static void main(String[] args) {\n        Shape s1 = new Circle(5);\n        Shape s2 = new Rectangle(3, 4);\n        System.out.println(\"圆面积: \" + s1.area());\n        System.out.println(\"矩形面积: \" + s2.area());\n    }\n}","接口定义规范，类负责实现"));
        return q;
    }

    private List<Question> chapter10() {
        List<Question> q = new ArrayList<>();
        q.add(sc(CH10,Difficulty.EASY,"Java中捕获异常使用哪个语句？",opts("try-catch","if-else","switch","for"),"A","try-catch捕获处理异常"));
        q.add(sc(CH10,Difficulty.EASY,"finally块的特点是？",opts("只在异常时执行","无论是否异常都执行","只在无异常时执行","可选执行"),"B","finally块总是执行(除System.exit)"));
        q.add(sc(CH10,Difficulty.MEDIUM,"RuntimeException属于？",opts("受检异常","非受检异常","错误","编译异常"),"B","RuntimeException及子类是非受检异常"));
        q.add(sc(CH10,Difficulty.MEDIUM,"throws关键字的作用？",opts("抛出异常对象","声明方法可能抛出的异常","捕获异常","处理异常"),"B","throws在方法签名中声明异常"));
        q.add(sc(CH10,Difficulty.HARD,"以下哪个不是Throwable的子类？",opts("Exception","Error","RuntimeException","String"),"D","String不是异常类"));
        q.add(sc(CH10,Difficulty.EASY,"throw和throws的区别？",opts("没有区别","throw抛出异常对象，throws声明异常","throw在方法签名中，throws在方法体中","两者可互换"),"B","throw抛出具体异常，throws声明"));
        q.add(mc(CH10,Difficulty.EASY,"以下哪些是常见的运行时异常？",opts("NullPointerException","ArrayIndexOutOfBoundsException","IOException","ClassCastException"),"ABD","IOException是受检异常"));
        q.add(mc(CH10,Difficulty.MEDIUM,"try-catch-finally的执行规则？",opts("try必须有","catch和finally至少有一个","finally总是执行","可以有多个catch"),"ABCD","这些都是正确的规则"));
        q.add(tf(CH10,Difficulty.EASY,"Error和Exception都继承自Throwable。","正确","Throwable是Error和Exception的父类"));
        q.add(tf(CH10,Difficulty.MEDIUM,"受检异常(Checked Exception)必须被捕获或声明抛出。","正确","编译器强制要求处理受检异常"));
        q.add(fb(CH10,Difficulty.EASY,"自定义异常通常继承___类或___类。","Exception;RuntimeException","受检异常继承Exception，非受检继承RuntimeException"));
        q.add(fb(CH10,Difficulty.MEDIUM,"try-with-resources语句中的资源必须实现___接口。","AutoCloseable","实现AutoCloseable接口的资源会自动关闭"));
        q.add(sa(CH10,Difficulty.MEDIUM,"简述Java异常体系的层次结构。","Throwable是根类，有两个子类：Error(严重错误，不应捕获，如OutOfMemoryError)和Exception(可处理的异常)。Exception分为：受检异常(编译时检查，如IOException)和非受检异常(RuntimeException及子类，如NullPointerException)。受检异常必须try-catch或throws声明。","Throwable→Error/Exception→受检/非受检"));
        q.add(pg(CH10,Difficulty.MEDIUM,"编写自定义异常AgeException，当年龄<0或>150时抛出。","public class AgeException extends Exception {\n    public AgeException(String msg) { super(msg); }\n}\n\nclass Person {\n    private int age;\n    public void setAge(int age) throws AgeException {\n        if (age < 0 || age > 150) {\n            throw new AgeException(\"年龄不合法: \" + age);\n        }\n        this.age = age;\n    }\n}\n\nclass Test {\n    public static void main(String[] args) {\n        Person p = new Person();\n        try {\n            p.setAge(200);\n        } catch (AgeException e) {\n            System.out.println(\"捕获异常: \" + e.getMessage());\n        }\n    }\n}","自定义异常+throw+try-catch"));
        return q;
    }

    // ===== 预置试卷初始化 =====
    private void initPapers(List<Question> all) {
        // 按题型分组
        Map<QuestionType, List<Question>> byType = new HashMap<>();
        for (Question q : all) {
            byType.computeIfAbsent(q.getType(), k -> new ArrayList<>()).add(q);
        }

        List<Question> scs = byType.getOrDefault(QuestionType.SINGLE_CHOICE, new ArrayList<>());
        List<Question> mcs = byType.getOrDefault(QuestionType.MULTIPLE_CHOICE, new ArrayList<>());
        List<Question> tfs = byType.getOrDefault(QuestionType.TRUE_FALSE, new ArrayList<>());
        List<Question> fbs = byType.getOrDefault(QuestionType.FILL_BLANK, new ArrayList<>());
        List<Question> sas = byType.getOrDefault(QuestionType.SHORT_ANSWER, new ArrayList<>());
        List<Question> pgs = byType.getOrDefault(QuestionType.PROGRAMMING, new ArrayList<>());

        // 打乱顺序
        Collections.shuffle(scs); Collections.shuffle(mcs);
        Collections.shuffle(tfs); Collections.shuffle(fbs);
        Collections.shuffle(sas); Collections.shuffle(pgs);

        for (int p = 1; p <= 20; p++) {
            ExamPaper paper = ExamPaper.builder()
                    .title("Java程序设计基础 第" + p + "套试卷")
                    .totalScore(100)
                    .durationMinutes(120)
                    .description("涵盖Java基础全部知识点的综合测试")
                    .createdAt(LocalDateTime.now())
                    .build();
            paper = paperRepo.save(paper);

            int order = 0;
            // 单选10题x2分
            for (int i = 0; i < 10; i++) {
                addPQ(paper, scs.get((p * 10 + i) % scs.size()), ++order, 2);
            }
            // 多选5题x4分
            for (int i = 0; i < 5; i++) {
                addPQ(paper, mcs.get((p * 5 + i) % mcs.size()), ++order, 4);
            }
            // 判断5题x2分
            for (int i = 0; i < 5; i++) {
                addPQ(paper, tfs.get((p * 5 + i) % tfs.size()), ++order, 2);
            }
            // 填空5题x4分
            for (int i = 0; i < 5; i++) {
                addPQ(paper, fbs.get((p * 5 + i) % fbs.size()), ++order, 4);
            }
            // 简答2题x10分
            for (int i = 0; i < 2; i++) {
                addPQ(paper, sas.get((p * 2 + i) % sas.size()), ++order, 10);
            }
            // 编程1题x10分
            addPQ(paper, pgs.get(p % pgs.size()), ++order, 10);
        }
    }

    private void addPQ(ExamPaper paper, Question q, int order, int score) {
        pqRepo.save(PaperQuestion.builder()
                .paper(paper).question(q).questionOrder(order).score(score).build());
    }
}
