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

    private static final String SRC_NET = "网络2026年1月";
    private static final String SRC_TB = "课后习题原题";

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
        log.info("题库初始化完成(网络来源)，共 {} 道题", all.size());
        initPapers(all);
        log.info("预置试卷初始化完成");
        List<Question> tbAll = initTextbookQuestions();
        qRepo.saveAll(tbAll);
        log.info("课后习题原题初始化完成，共 {} 道题", tbAll.size());
    }

    // ===== 快捷构建方法 =====
    private Question sc(String ch, Difficulty d, String content, String opts, String ans, String exp) {
        return sc(ch, d, content, opts, ans, exp, SRC_NET);
    }
    private Question sc(String ch, Difficulty d, String content, String opts, String ans, String exp, String src) {
        return Question.builder().type(QuestionType.SINGLE_CHOICE).chapter(ch).difficulty(d)
                .content(content).options(opts).answer(ans).explanation(exp).defaultScore(2).source(src).build();
    }
    private Question mc(String ch, Difficulty d, String content, String opts, String ans, String exp) {
        return mc(ch, d, content, opts, ans, exp, SRC_NET);
    }
    private Question mc(String ch, Difficulty d, String content, String opts, String ans, String exp, String src) {
        return Question.builder().type(QuestionType.MULTIPLE_CHOICE).chapter(ch).difficulty(d)
                .content(content).options(opts).answer(ans).explanation(exp).defaultScore(4).source(src).build();
    }
    private Question tf(String ch, Difficulty d, String content, String ans, String exp) {
        return tf(ch, d, content, ans, exp, SRC_NET);
    }
    private Question tf(String ch, Difficulty d, String content, String ans, String exp, String src) {
        return Question.builder().type(QuestionType.TRUE_FALSE).chapter(ch).difficulty(d)
                .content(content).answer(ans).explanation(exp).defaultScore(2).source(src).build();
    }
    private Question fb(String ch, Difficulty d, String content, String ans, String exp) {
        return fb(ch, d, content, ans, exp, SRC_NET);
    }
    private Question fb(String ch, Difficulty d, String content, String ans, String exp, String src) {
        return Question.builder().type(QuestionType.FILL_BLANK).chapter(ch).difficulty(d)
                .content(content).answer(ans).explanation(exp).defaultScore(4).source(src).build();
    }
    private Question sa(String ch, Difficulty d, String content, String ans, String exp) {
        return sa(ch, d, content, ans, exp, SRC_NET);
    }
    private Question sa(String ch, Difficulty d, String content, String ans, String exp, String src) {
        return Question.builder().type(QuestionType.SHORT_ANSWER).chapter(ch).difficulty(d)
                .content(content).answer(ans).explanation(exp).defaultScore(10).source(src).build();
    }
    private Question pg(String ch, Difficulty d, String content, String ans, String exp) {
        return pg(ch, d, content, ans, exp, SRC_NET);
    }
    private Question pg(String ch, Difficulty d, String content, String ans, String exp, String src) {
        return Question.builder().type(QuestionType.PROGRAMMING).chapter(ch).difficulty(d)
                .content(content).answer(ans).explanation(exp).defaultScore(10).source(src).build();
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

    // ===== 课后习题原题 =====
    private static final String TB1="Java概述",TB2="Java基础语法",TB3="面向对象基础",TB4="继承与多态",
        TB5="抽象类与接口",TB6="异常处理",TB7="集合框架",TB8="I/O流",TB9="GUI编程",TB10="多线程";

    private List<Question> initTextbookQuestions() {
        List<Question> q = new ArrayList<>();
        // ===== 第1章 Java概述 =====
        q.add(fb(TB1,Difficulty.EASY,"Java语言最早诞生于1991年，起初被称为______语言。","Oak","Java前身为Oak语言",SRC_TB));
        q.add(fb(TB1,Difficulty.EASY,"Java的跨平台特性是通过在______中运行Java程序实现的。","Java虚拟机（JVM）","JVM实现跨平台",SRC_TB));
        q.add(fb(TB1,Difficulty.EASY,"Java运行环境简称为______。","JRE","JRE=Java Runtime Environment",SRC_TB));
        q.add(fb(TB1,Difficulty.EASY,"Java的源文件和字节码文件的扩展名分别是______和______。",".java;.class","源文件.java，编译后.class",SRC_TB));
        q.add(fb(TB1,Difficulty.EASY,"javac和java两个命令存放在JDK安装目录的______目录下。","bin","JDK的bin目录存放工具",SRC_TB));
        q.add(sc(TB1,Difficulty.EASY,"Java属于以下哪种语言？",opts("机器语言","汇编语言","高级语言","以上都不是"),"C","Java是高级编程语言",SRC_TB));
        q.add(sc(TB1,Difficulty.EASY,"简单来说，Java程序的运行机制分为编写、（ ）和运行3个过程。",opts("编辑","汇编","编码","编译"),"D","编写→编译→运行",SRC_TB));
        q.add(sc(TB1,Difficulty.EASY,"下面哪种类型的文件可以在Java虚拟机中运行？",opts(".java",".jre",".exe",".class"),"D","JVM执行.class字节码文件",SRC_TB));
        q.add(sc(TB1,Difficulty.MEDIUM,"安装好JDK后，java命令的作用相当于（ ）",opts("Java文档制作工具","Java解释器","Java编译器","Java启动器"),"B","java命令解释执行字节码",SRC_TB));
        q.add(sc(TB1,Difficulty.EASY,"用Java虚拟机执行类名为Hello的应用程序的正确命令是（ ）",opts("java Hello.class","java Hello.java","Hello.class","java Hello"),"D","java命令后跟类名不加扩展名",SRC_TB));
        q.add(sa(TB1,Difficulty.MEDIUM,"简述Java语言的特点。","Java语言特点：简单性、面向对象、分布式、健壮性、安全性、体系结构中立（跨平台）、可移植性、解释执行、高性能、多线程、动态性。","Java语言核心特性",SRC_TB));
        q.add(sa(TB1,Difficulty.MEDIUM,"简述Java的3个技术平台及它们分别适合开发的应用。","Java SE（标准版，桌面应用）、Java EE（企业版，企业级网络应用）、Java ME（微型版，移动终端应用）。","三大平台定位不同",SRC_TB));
        q.add(sa(TB1,Difficulty.EASY,"简述什么是JRE和JDK。","JRE是Java运行环境，包含JVM和核心类库。JDK是Java开发工具包，包含JRE和开发工具（编译器、调试器等）。","JDK⊃JRE⊃JVM",SRC_TB));
        q.add(sa(TB1,Difficulty.MEDIUM,"简述Java的编译运行机制。","编译阶段：使用javac命令将.java源文件编译成.class字节码文件。运行阶段：使用java命令启动JVM，将字节码解释/即时编译并执行。","先编译后解释执行",SRC_TB));
        q.add(pg(TB1,Difficulty.EASY,"编写程序，实现在控制台显示\"路漫漫其修远兮，吾将上下而求索\"。","public class Poem {\n    public static void main(String[] args) {\n        System.out.println(\"路漫漫其修远兮，吾将上下而求索\");\n    }\n}","基本输出语句",SRC_TB));

        // ===== 第2章 Java基础语法 =====
        q.add(fb(TB2,Difficulty.EASY,"将两个数相加，生成一个值的语句称为______。","表达式","表达式产生值",SRC_TB));
        q.add(fb(TB2,Difficulty.EASY,"数据类型转换方式分为自动类型转换和______两种。","强制类型转换","显式转换=强制类型转换",SRC_TB));
        q.add(fb(TB2,Difficulty.EASY,"选择结构也称______，其根据条件的成立与否来决定要执行哪些语句。","分支结构","分支=选择",SRC_TB));
        q.add(fb(TB2,Difficulty.EASY,"通常称给定条件为循环条件，称反复执行的程序段为______。","循环体","循环体是重复执行的代码",SRC_TB));
        q.add(fb(TB2,Difficulty.EASY,"结构化程序中最简单的结构是______。","顺序结构","顺序→分支→循环",SRC_TB));
        q.add(sc(TB2,Difficulty.EASY,"do-while循环结构中的循环体执行的最少次数为（ ）",opts("1","0","3","2"),"A","do-while至少执行一次",SRC_TB));
        q.add(sc(TB2,Difficulty.MEDIUM,"已知y=2, z=3, n=4，则经过n=n-y*z/n运算后，n的值为（ ）",opts("-12","-1","3","-3"),"C","整数除法：n=4-2*3/4=4-1=3",SRC_TB));
        q.add(sc(TB2,Difficulty.MEDIUM,"已知a=2, b=3，则表达式a%b*4%b的值为（ ）",opts("2","1","-1","-2"),"A","2%3=2, 2*4=8, 8%3=2",SRC_TB));
        q.add(sc(TB2,Difficulty.MEDIUM,"语句\"while(!e)\"中的条件\"!e\"等价于（ ）",opts("e==0","e!=1","e!=0","~e"),"A","!e等价于e为假",SRC_TB));
        q.add(sc(TB2,Difficulty.EASY,"while循环，条件为（ ）时执行循环体。",opts("false","true","0","假或真"),"B","条件为true时循环",SRC_TB));
        q.add(sa(TB2,Difficulty.MEDIUM,"请简述Java的8种基本数据类型及其所占内存大小。","byte(1字节), short(2字节), int(4字节), long(8字节), float(4字节), double(8字节), char(2字节), boolean(1位/通常1字节)。","8种基本类型",SRC_TB));
        q.add(sa(TB2,Difficulty.MEDIUM,"请简述数据类型转换的原理。","自动类型转换：容量小的自动转为容量大的。强制类型转换：大转小，可能精度损失，需显式括号。","宽化vs窄化",SRC_TB));
        q.add(sa(TB2,Difficulty.MEDIUM,"请简述&&和&的区别。","&&是短路与，左侧为false不再计算右侧；&是逻辑与，都会计算。在位运算中&也是按位与。","短路与vs非短路与",SRC_TB));
        q.add(sa(TB2,Difficulty.EASY,"请简述break和continue语句的区别。","break跳出当前整个循环或switch结构；continue跳过当前循环剩余语句，进入下一次迭代。","break终止循环,continue跳过本次",SRC_TB));
        q.add(pg(TB2,Difficulty.EASY,"编写程序，使用\"*\"输出直角三角形。","public class Triangle {\n    public static void main(String[] args) {\n        for (int i = 1; i <= 5; i++) {\n            for (int j = 1; j <= i; j++) {\n                System.out.print(\"*\");\n            }\n            System.out.println();\n        }\n    }\n}","嵌套循环打印图形",SRC_TB));
        q.add(pg(TB2,Difficulty.MEDIUM,"编写程序，计算字符数组中每个字符出现的次数。","import java.util.HashMap;\npublic class CharCount {\n    public static void main(String[] args) {\n        char[] chars = {'a','b','c','a','b','a'};\n        HashMap<Character,Integer> map = new HashMap<>();\n        for (char c : chars) map.put(c, map.getOrDefault(c,0)+1);\n        for (Character key : map.keySet()) System.out.println(key+\": \"+map.get(key));\n    }\n}","HashMap统计字符频次",SRC_TB));

        // ===== 第3章 面向对象基础 =====
        q.add(fb(TB3,Difficulty.EASY,"对象是对事物的抽象，而______是对对象的抽象和归纳。","类","类是对象的模板",SRC_TB));
        q.add(fb(TB3,Difficulty.EASY,"在类体中，变量定义部分所定义的变量称为类的______。","成员变量","成员变量=实例变量",SRC_TB));
        q.add(fb(TB3,Difficulty.EASY,"在Java中，可以使用关键字______来创建类的实例对象。","new","new分配内存并调用构造方法",SRC_TB));
        q.add(fb(TB3,Difficulty.EASY,"在关键字中能代表当前类或对象本身的是______。","this","this引用当前对象",SRC_TB));
        q.add(fb(TB3,Difficulty.MEDIUM,"______指那些类定义代码被置于其他类定义中的类。","内部类","内部类定义在其他类内部",SRC_TB));
        q.add(sc(TB3,Difficulty.EASY,"类的定义必须包含在以下哪种符号之间？",opts("小括号()","双引号\"\"","大括号{}","中括号[]"),"C","类体用大括号包围",SRC_TB));
        q.add(sc(TB3,Difficulty.EASY,"在以下哪种情况下，构造方法被调用？",opts("类定义时","创建对象时","使用对象的属性时","使用对象的方法时"),"B","new时自动调用构造方法",SRC_TB));
        q.add(sc(TB3,Difficulty.MEDIUM,"有一个类B，下面为其构造方法的声明，正确的是（ ）",opts("b(int x) {}","void B(int x) {}","void b(int x) {}","B(int x) {}"),"D","构造方法名=类名，无返回类型",SRC_TB));
        q.add(sc(TB3,Difficulty.EASY,"下面哪一种是正确的类声明？",opts("public class Qf{}","public void QF{}","public class void max{}","public class min(){}"),"A","正确的类声明格式",SRC_TB));
        q.add(sc(TB3,Difficulty.MEDIUM,"定义外部类时不能用到的关键字是（ ）",opts("final","public","protected","abstract"),"C","外部类不能用protected修饰",SRC_TB));
        q.add(sa(TB3,Difficulty.EASY,"什么是面向对象？","面向对象是一种编程思想，将现实世界的事物抽象为对象，通过对象之间的交互来设计软件。具有封装、继承、多态三大特征。","OOP核心思想",SRC_TB));
        q.add(sa(TB3,Difficulty.MEDIUM,"构造方法与普通成员方法的区别是什么？","1.构造方法名必须与类名相同；2.没有返回类型(连void都没有)；3.随对象实例化自动调用，普通方法需主动调用。","构造方法的特殊性",SRC_TB));
        q.add(sa(TB3,Difficulty.MEDIUM,"什么是垃圾回收机制？","是JVM自动回收不可达对象所占内存空间的机制，减轻了程序员手动管理内存的负担，防止内存泄漏。","GC自动回收内存",SRC_TB));
        q.add(sa(TB3,Difficulty.EASY,"类与对象之间的关系是什么样的？","类是对象的抽象模板，对象是类的具体实例。类定义了对象的属性和行为。","类是模板，对象是实例",SRC_TB));
        q.add(pg(TB3,Difficulty.EASY,"书架上有30本书，箱子中有40本书，把书架上的书全部放进箱子，使用带参数的方法计算箱子里书的总数。","public class BookBox {\n    public int addBooks(int currentBoxBooks, int shelfBooks) {\n        return currentBoxBooks + shelfBooks;\n    }\n    public static void main(String[] args) {\n        BookBox box = new BookBox();\n        int total = box.addBooks(40, 30);\n        System.out.println(\"箱子里书的总数: \" + total);\n    }\n}","带参数方法的使用",SRC_TB));
        q.add(pg(TB3,Difficulty.MEDIUM,"创建信用卡类，有两个成员变量卡号和密码。设计两个不同的构造方法，分别用于设置密码和未设置密码(默认\"123321\")两种情况。","public class CreditCard {\n    String cardNo;\n    String password;\n    public CreditCard(String cardNo, String password) {\n        this.cardNo = cardNo;\n        this.password = password;\n    }\n    public CreditCard(String cardNo) {\n        this.cardNo = cardNo;\n        this.password = \"123321\";\n    }\n}","构造方法重载",SRC_TB));
        q.add(pg(TB3,Difficulty.EASY,"设计手机类，手机有一个拨打电话的静态方法，此方法与手机的品牌和型号无关。","public class Phone {\n    public static void call() {\n        System.out.println(\"拨打电话\");\n    }\n}","静态方法不依赖对象",SRC_TB));

        // ===== 第4章 继承与多态 =====
        q.add(fb(TB4,Difficulty.EASY,"如果在子类中需要访问父类的被重写方法，可以通过______关键字来实现。","super","super访问父类成员",SRC_TB));
        q.add(fb(TB4,Difficulty.EASY,"Java中使用______关键字来实现类的继承。","extends","extends实现继承",SRC_TB));
        q.add(fb(TB4,Difficulty.EASY,"Java语言中______是所有类的根。","Object","Object是根类",SRC_TB));
        q.add(fb(TB4,Difficulty.MEDIUM,"类成员的访问控制符有______、______、______和默认4种。","public;protected;private","四种访问控制级别",SRC_TB));
        q.add(fb(TB4,Difficulty.EASY,"当一个类的修饰符为______时，说明该类不能被继承。","final","final类不能有子类",SRC_TB));
        q.add(sc(TB4,Difficulty.EASY,"下列选项中，表示数据或方法只能被本类访问的修饰符是（ ）",opts("public","protected","private","final"),"C","private限本类访问",SRC_TB));
        q.add(sc(TB4,Difficulty.EASY,"关键字（ ）表明一个对象或变量在初始化后不能修改。",opts("extends","final","this","finalize"),"B","final表示不可修改",SRC_TB));
        q.add(sc(TB4,Difficulty.MEDIUM,"已知Employee是父类，Manager和Director是子类，则正确的语句是（ ）",opts("Employee e = new Manager();","Director d=new Manager();","Director d=new Employee();","Manager m=new Director();"),"A","父类引用可指向子类对象",SRC_TB));
        q.add(sc(TB4,Difficulty.MEDIUM,"关于Java中的继承，下列说法错误的是（ ）",opts("继承是面向对象编程的核心特征之一","继承使得程序员可以在原有类的基础上设计新类","子类会自动拥有父类的属性和方法","Java中的类采用多重继承"),"D","Java类只支持单继承",SRC_TB));
        q.add(sc(TB4,Difficulty.EASY,"能作为类的修饰符，也能作为类成员的修饰符的是（ ）",opts("public","extends","Float","static"),"A","public可修饰类和成员",SRC_TB));
        q.add(sa(TB4,Difficulty.EASY,"什么是继承？","继承是子类自动获得父类非私有属性和方法的机制，是代码复用的重要手段，体现is-a关系。","继承实现代码复用",SRC_TB));
        q.add(sa(TB4,Difficulty.MEDIUM,"什么是多态？","多态是指同一行为具有多个不同表现形式的能力。通常体现为：父类引用指向子类对象，调用被重写的方法时表现出子类的行为。","同一接口不同实现",SRC_TB));
        q.add(sa(TB4,Difficulty.MEDIUM,"请简述方法的重载与重写的区别。","重载(Overload)在同一个类中，方法名相同参数列表不同。重写(Override)发生在子父类中，方法签名相同，返回值和异常范围不大于父类，访问权限不小于父类。","重载vs重写",SRC_TB));
        q.add(pg(TB4,Difficulty.EASY,"创建银行卡类，并设计两个子类：储蓄卡和信用卡。","class BankCard { }\nclass DebitCard extends BankCard { }\nclass CreditCardEx extends BankCard { }","简单继承关系",SRC_TB));
        q.add(pg(TB4,Difficulty.MEDIUM,"定义交通工具类，设计火车和汽车子类，重写移动方法。","class Vehicle {\n    public void move() { System.out.println(\"交通工具可以移动\"); }\n}\nclass Train extends Vehicle {\n    @Override\n    public void move() { System.out.println(\"火车在铁轨上行驶\"); }\n}\nclass Car extends Vehicle {\n    @Override\n    public void move() { System.out.println(\"汽车在公路上行驶\"); }\n}","方法重写演示多态",SRC_TB));

        // ===== 第5章 抽象类与接口 =====
        q.add(fb(TB5,Difficulty.EASY,"Java中使用______关键字，来表示抽象的意思。","abstract","abstract定义抽象",SRC_TB));
        q.add(fb(TB5,Difficulty.EASY,"Java中使用______关键字，来实现接口的继承。","implements","implements实现接口",SRC_TB));
        q.add(fb(TB5,Difficulty.MEDIUM,"Java 8中引入了一个新的操作符\"->\"，可以称之为箭头操作符或者______操作符。","lambda","Lambda表达式简化代码",SRC_TB));
        q.add(fb(TB5,Difficulty.EASY,"______类不能创建对象，必须产生其子类，由子类创建对象。","抽象","抽象类不能实例化",SRC_TB));
        q.add(fb(TB5,Difficulty.MEDIUM,"定义接口时，接口体中只进行方法的声明，不允许提供方法的______。","实现（方法体）","接口方法默认抽象",SRC_TB));
        q.add(sc(TB5,Difficulty.MEDIUM,"以下关于Java语言继承的说法，正确的是（ ）",opts("Java中的类可以有多个直接父类","抽象类不能有子类","Java中的接口支持多继承","最终类可以作为其他类的父类"),"C","接口支持多继承",SRC_TB));
        q.add(sc(TB5,Difficulty.EASY,"现有两个类A、B，表示B继承A的是（ ）",opts("class A extends B","class B implements A","class A implements B","class B extends A"),"D","B extends A表示B继承A",SRC_TB));
        q.add(sc(TB5,Difficulty.EASY,"下列选项中，用于定义接口的关键字是（ ）",opts("interface","implements","abstract","class"),"A","interface定义接口",SRC_TB));
        q.add(sc(TB5,Difficulty.EASY,"下列选项中，表示数据或方法只能被本类访问的修饰符是（ ）",opts("public","protected","private","final"),"C","private限本类访问",SRC_TB));
        q.add(sc(TB5,Difficulty.HARD,"关于\"@FunctionalInterface\"注解代表的含义，下列说法正确的是（ ）",opts("主要用于编译级错误检查","主要用于简化Java开发","检查是否含有多个非抽象方法","当接口包含默认方法时编译报错"),"A","FunctionalInterface用于编译检查",SRC_TB));
        q.add(sa(TB5,Difficulty.HARD,"请简述抽象类和接口的区别。","1.抽象类用abstract修饰，接口用interface。2.抽象类可有普通方法和变量，接口只有抽象方法(Java8+有default)和常量。3.类只支持单继承抽象类，但可多实现接口。","抽象类vs接口",SRC_TB));
        q.add(sa(TB5,Difficulty.MEDIUM,"接口中方法的修饰符都有哪些？属性的修饰符有哪些？","方法默认修饰符为public abstract；属性默认修饰符为public static final。","接口成员默认修饰符",SRC_TB));
        q.add(sa(TB5,Difficulty.MEDIUM,"接口的作用是什么？简述接口与类的关系。","作用：定义规范、实现多重继承效果、解耦。类实现接口(implements)，必须实现所有抽象方法，一个类可实现多个接口。","接口定义规范",SRC_TB));
        q.add(pg(TB5,Difficulty.MEDIUM,"设计一个名为Geometric的几何图形抽象类，包含color和filled属性，无参构造和有参构造。","abstract class Geometric {\n    String color;\n    boolean filled;\n    public Geometric() {}\n    public Geometric(String color, boolean filled) {\n        this.color = color;\n        this.filled = filled;\n    }\n}","抽象类设计",SRC_TB));

        // ===== 第6章 异常处理 =====
        q.add(fb(TB6,Difficulty.EASY,"Java语言的异常捕获结构由try、______和finally 3个部分组成。","catch","try-catch-finally",SRC_TB));
        q.add(fb(TB6,Difficulty.EASY,"抛出异常、生成异常对象都可以通过______语句实现。","throw","throw抛出异常对象",SRC_TB));
        q.add(fb(TB6,Difficulty.EASY,"捕获异常的统一出口通过______语句实现。","finally","finally总是执行",SRC_TB));
        q.add(fb(TB6,Difficulty.MEDIUM,"______异常是由于环境造成的，是捕获处理的重点，即表示是可以恢复的。","检查（Checked）","受检异常需强制处理",SRC_TB));
        q.add(fb(TB6,Difficulty.EASY,"Throwable类有两个子类，分别是______类和Exception类。","Error","Throwable→Error+Exception",SRC_TB));
        q.add(sc(TB6,Difficulty.EASY,"在异常处理中，释放资源、关闭文件等，由（ ）来完成。",opts("try子句","catch子句","finally子句","throw子句"),"C","finally用于资源清理",SRC_TB));
        q.add(sc(TB6,Difficulty.EASY,"当方法遇到异常又不知如何处理时，应该（ ）",opts("捕获异常","抛出异常","声明异常","嵌套异常"),"B","不知如何处理就抛出",SRC_TB));
        q.add(sc(TB6,Difficulty.MEDIUM,"下列关于异常的说法，正确的是（ ）",opts("异常是编译时出现的错误","异常是运行时出现的错误","程序错误就是异常","以上说法都不正确"),"B","异常是运行时错误",SRC_TB));
        q.add(sc(TB6,Difficulty.MEDIUM,"下列有关throw和throws的说法，不正确的是（ ）",opts("throw后面加异常类的对象","throws后面加异常类的类名","throws后面只能加自定义异常类","以上说法都不正确"),"C","throws可加任何异常类",SRC_TB));
        q.add(sc(TB6,Difficulty.EASY,"关于异常，下列说法正确的是（ ）",opts("异常是一种对象","一旦程序运行异常将被创建","为保证运行速度应避免异常控制","以上说法都不正确"),"A","异常是对象",SRC_TB));
        q.add(sa(TB6,Difficulty.EASY,"请简述什么是异常。","异常是程序在运行期发生的不正常情况，它中断了正常的指令流。","异常中断正常执行流",SRC_TB));
        q.add(sa(TB6,Difficulty.MEDIUM,"请简述什么是必检异常，什么是免检异常。","必检异常(Checked)：Exception的直接子类，编译期必须捕获或声明。免检异常(Unchecked)：RuntimeException及子类、Error，编译期不强制。","受检vs非受检异常",SRC_TB));
        q.add(sa(TB6,Difficulty.MEDIUM,"请简述Error和Exception的区别。","Error指JVM无法解决的严重问题(如OOM)，程序无法处理；Exception指一般性问题，可通过代码捕获处理。","Error严重,Exception可处理",SRC_TB));
        q.add(pg(TB6,Difficulty.MEDIUM,"编写Circle类，提供InvalidRadiusException异常类，当半径为负数时抛出异常。","class InvalidRadiusException extends Exception { }\nclass Circle {\n    double radius;\n    public Circle() {}\n    public Circle(double radius) throws InvalidRadiusException {\n        setRadius(radius);\n    }\n    public void setRadius(double radius) throws InvalidRadiusException {\n        if (radius < 0) throw new InvalidRadiusException();\n        this.radius = radius;\n    }\n}","自定义异常类",SRC_TB));
        q.add(pg(TB6,Difficulty.HARD,"编写Triangle类，创建IllegalTriangleException异常类，构造方法中验证三角形边长规则。","class IllegalTriangleException extends Exception { }\nclass Triangle {\n    public Triangle(double a, double b, double c) throws IllegalTriangleException {\n        if (a+b<=c || a+c<=b || b+c<=a) throw new IllegalTriangleException();\n    }\n}","三角形边长校验异常",SRC_TB));

        // ===== 第7章 集合框架 =====
        q.add(fb(TB7,Difficulty.EASY,"______接口是List, Set和Queue等接口的父接口。","Collection","Collection是集合顶级接口",SRC_TB));
        q.add(fb(TB7,Difficulty.EASY,"______集合中元素是有序且可重复的，相当于数学里面的数列。","List","List有序可重复",SRC_TB));
        q.add(fb(TB7,Difficulty.EASY,"______集合中元素是无序、不可重复的。","Set","Set无序不重复",SRC_TB));
        q.add(fb(TB7,Difficulty.MEDIUM,"______接口未继承Collection接口，用于存储键值对形式的元素。","Map","Map存储key-value",SRC_TB));
        q.add(fb(TB7,Difficulty.HARD,"Java 8提供了______方法进行遍历，遍历时输出剩余元素且只能用一次。","forEachRemaining","Iterator的forEachRemaining",SRC_TB));
        q.add(sc(TB7,Difficulty.EASY,"在Java中，（ ）类对象可以使用值对的形式保存数据。",opts("ArrayList","HashSet","LinkedList","HashMap"),"D","HashMap存储键值对",SRC_TB));
        q.add(sc(TB7,Difficulty.MEDIUM,"下列属于线程安全的类是（ ）",opts("ArrayList","Vector","HashMap","HashSet"),"B","Vector线程安全",SRC_TB));
        q.add(sc(TB7,Difficulty.MEDIUM,"\"ArrayList list=new ArrayList(20);\"，list扩容了几次？",opts("0次","1次","2次","3次"),"A","指定初始容量不扩容",SRC_TB));
        q.add(sc(TB7,Difficulty.EASY,"下面哪个Map是排序的？",opts("TreeMap","HashMap","WeakHashMap","LinkedHashMap"),"A","TreeMap自然排序",SRC_TB));
        q.add(sc(TB7,Difficulty.MEDIUM,"下列关于Stream的描述中，不正确的是（ ）",opts("Stream用于处理集合及数组","Stream不会存储元素","Stream可以改变源对象","Stream操作是延迟执行的"),"C","Stream不改变源对象",SRC_TB));
        q.add(sa(TB7,Difficulty.EASY,"简述Set和List有哪些区别。","Set是不重复、无序的集合；List是可重复、有序的集合(有索引)。","Set无序不重复,List有序可重复",SRC_TB));
        q.add(sa(TB7,Difficulty.MEDIUM,"简述Collection与Collections的区别。","Collection是集合框架的顶级接口；Collections是集合操作的工具类，提供排序、查找等静态方法。","接口vs工具类",SRC_TB));
        q.add(sa(TB7,Difficulty.MEDIUM,"简述Iterator和ListIterator的区别。","Iterator可遍历List和Set，单向遍历；ListIterator是Iterator子接口，专供List，支持双向遍历、修改和添加。","单向vs双向迭代器",SRC_TB));
        q.add(sa(TB7,Difficulty.MEDIUM,"简述使用泛型的好处。","1.编译时强类型检查；2.避免强制类型转换；3.提高代码重用性和可读性。","泛型=类型安全",SRC_TB));
        q.add(pg(TB7,Difficulty.EASY,"将26个英文字母正序和逆序输出。","public class Alphabet {\n    public static void main(String[] args) {\n        for (char c = 'A'; c <= 'Z'; c++) System.out.print(c + \" \");\n        System.out.println();\n        for (char c = 'Z'; c >= 'A'; c--) System.out.print(c + \" \");\n    }\n}","字符循环遍历",SRC_TB));
        q.add(pg(TB7,Difficulty.MEDIUM,"使用Map接口实现类，输出西北省份及其主要城市。","import java.util.*;\npublic class ProvinceCity {\n    public static void main(String[] args) {\n        Map<String,String> map = new HashMap<>();\n        map.put(\"陕西省\",\"西安市\"); map.put(\"甘肃省\",\"兰州市\");\n        map.put(\"青海省\",\"西宁市\"); map.put(\"宁夏回族自治区\",\"银川市\");\n        map.put(\"新疆维吾尔自治区\",\"乌鲁木齐市\");\n        for (Map.Entry<String,String> e : map.entrySet()) System.out.println(e.getKey()+\": \"+e.getValue());\n    }\n}","HashMap使用示例",SRC_TB));

        // ===== 第8章 I/O流 =====
        q.add(fb(TB8,Difficulty.EASY,"I/O流按操作数据单位可分为______和______，按流向可分为______和______。","字节流;字符流;输入流;输出流","四种分类",SRC_TB));
        q.add(fb(TB8,Difficulty.EASY,"Java的I/O中针对字节传输操作提供的流，统称为______。","字节流","字节流处理二进制数据",SRC_TB));
        q.add(fb(TB8,Difficulty.MEDIUM,"字符流中带缓冲区的流分别是______类和______类。","BufferedReader;BufferedWriter","带缓冲的字符流",SRC_TB));
        q.add(fb(TB8,Difficulty.MEDIUM,"File类中提供的______方法用来遍历目录下所有文件。","listFiles()","listFiles返回文件数组",SRC_TB));
        q.add(fb(TB8,Difficulty.HARD,"Java提供了______类，支持\"随机访问\"方式读写文件。","RandomAccessFile","随机访问文件类",SRC_TB));
        q.add(sc(TB8,Difficulty.EASY,"下面哪个流类属于面向字符的输入流？",opts("BufferedWriter","FileInputStream","ObjectInputStream","InputStreamReader"),"D","InputStreamReader是字符输入流",SRC_TB));
        q.add(sc(TB8,Difficulty.MEDIUM,"新建流对象，下面哪个选项的代码是错误的？",opts("new BufferedWriter(new FileWriter(\"a.txt\"))","new BufferedReader(new FileInputStream(\"a.dat\"))","new GZIPOutputStream(new FileOutputStream(\"a.zip\"))","new ObjectInputStream(new FileInputStream(\"a.dat\"))"),"B","BufferedReader参数应为Reader",SRC_TB));
        q.add(sc(TB8,Difficulty.EASY,"Java I/O程序设计中，下列描述正确的是（ ）",opts("OutputStream用于写操作","InputStream用于写操作","只有字节流可以进行读操作","I/O库不支持文件读写API"),"A","OutputStream写,InputStream读",SRC_TB));
        q.add(sc(TB8,Difficulty.EASY,"下列哪个不是合法的字符编码？",opts("UTF-8","ISO8858-1","GBL","ASCII"),"C","GBL不是标准字符编码",SRC_TB));
        q.add(sc(TB8,Difficulty.MEDIUM,"File file1=new File(\"e:\\\\xxx\\\\yyy\"); file1.mkdir(); 的功能是（ ）",opts("在当前目录下生成子目录","生成目录e:\\xxx\\yyy","在当前目录下生成文件","以上说法都不对"),"B","mkdir创建最后一级目录",SRC_TB));
        q.add(sa(TB8,Difficulty.EASY,"Java中有几种类型的流？","按流向分：输入流和输出流；按数据单位分：字节流和字符流；按功能分：节点流和处理流。","三种维度分类",SRC_TB));
        q.add(sa(TB8,Difficulty.MEDIUM,"什么是Java序列化？","Java序列化是将Java对象转换为字节序列的过程，以便于存储或网络传输。","对象→字节序列",SRC_TB));
        q.add(sa(TB8,Difficulty.MEDIUM,"如何实现Java序列化？","让对象所属的类实现java.io.Serializable接口，然后使用ObjectOutputStream写入对象。","实现Serializable接口",SRC_TB));
        q.add(sa(TB8,Difficulty.EASY,"什么是标准的I/O流？","标准I/O流包括System.in（标准输入）、System.out（标准输出）、System.err（标准错误输出）。","三个标准流",SRC_TB));
        q.add(pg(TB8,Difficulty.EASY,"利用程序在D盘新建文件\"test.txt\"，写入\"我写的代码没bug\"。","import java.io.FileWriter;\npublic class WriteTest {\n    public static void main(String[] args) throws Exception {\n        FileWriter fw = new FileWriter(\"D:\\\\test.txt\");\n        fw.write(\"我写的代码没bug\");\n        fw.close();\n    }\n}","FileWriter写文件",SRC_TB));
        q.add(pg(TB8,Difficulty.MEDIUM,"利用转换流复制\"test.txt\"为\"my.txt\"。","import java.io.*;\npublic class CopyTest {\n    public static void main(String[] args) throws Exception {\n        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(\"D:\\\\test.txt\")));\n        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(\"D:\\\\my.txt\")));\n        String line;\n        while ((line = br.readLine()) != null) bw.write(line);\n        br.close(); bw.close();\n    }\n}","转换流+缓冲复制文件",SRC_TB));

        // ===== 第9章 GUI编程 =====
        q.add(fb(TB9,Difficulty.EASY,"AWT有两个抽象基类，分别为______、______。","Component;Container","AWT基础类",SRC_TB));
        q.add(fb(TB9,Difficulty.MEDIUM,"Java提供的______是专门处理窗体事件的监听接口。","WindowListener","窗体事件监听",SRC_TB));
        q.add(fb(TB9,Difficulty.EASY,"______类属于流式布局管理器。","FlowLayout","流式布局",SRC_TB));
        q.add(fb(TB9,Difficulty.HARD,"______和Swing都可以处理GUI，前者需要在Eclipse中安装______插件。","SWT;WindowBuilder","SWT和Eclipse插件",SRC_TB));
        q.add(fb(TB9,Difficulty.MEDIUM,"AWT事件处理中，主要涉及3个对象：______、______、______。","事件源;事件;事件处理者","事件三要素",SRC_TB));
        q.add(sc(TB9,Difficulty.EASY,"在Java中，要使用布局管理器，必须导入（ ）包。",opts("java.awt.*","java.awt.layout.*","javax.swing.layout.*","javax.swing.*"),"A","布局管理器在java.awt",SRC_TB));
        q.add(sc(TB9,Difficulty.MEDIUM,"Swing与AWT的区别不包括（ ）",opts("Swing是纯Java实现的轻量级构件","Swing没有本地代码","Swing不依赖操作系统","Swing支持图形用户界面"),"D","两者都支持GUI",SRC_TB));
        q.add(sc(TB9,Difficulty.MEDIUM,"编写Java applet程序时，事件响应需导入（ ）",opts("java.awt.*","java.applet.*","java.io.*","java.awt.event.*"),"D","事件处理在awt.event包",SRC_TB));
        q.add(sc(TB9,Difficulty.EASY,"下列不属于容器的是（ ）",opts("Window","TextBox","JPanel","JScrollPane"),"B","TextBox是文本框非容器",SRC_TB));
        q.add(sc(TB9,Difficulty.MEDIUM,"当Frame改变大小时，要使按钮大小不变，应使用（ ）",opts("FlowLayout","CardLayout","BorderLayout","GridLayout"),"A","FlowLayout不改变组件大小",SRC_TB));
        q.add(sa(TB9,Difficulty.MEDIUM,"简述AWT和Swing的区别。","AWT依赖操作系统本地组件(重量级)；Swing用纯Java编写(轻量级)，跨平台性更好，组件更丰富。","重量级vs轻量级",SRC_TB));
        q.add(sa(TB9,Difficulty.EASY,"java.awt包中提供的布局管理器有哪些？","FlowLayout, BorderLayout, GridLayout, CardLayout, GridBagLayout。","五种布局管理器",SRC_TB));
        q.add(sa(TB9,Difficulty.MEDIUM,"简述事件处理机制中所涉及的概念。","事件源(产生事件的组件)、事件(用户操作封装对象)、事件监听器(处理事件的方法)。","事件三要素",SRC_TB));

        // ===== 第10章 多线程 =====
        q.add(fb(TB10,Difficulty.EASY,"______是Java程序的并发机制，它能同步共享数据、处理不同事件。","多线程","多线程并发机制",SRC_TB));
        q.add(fb(TB10,Difficulty.EASY,"线程有新建、就绪、运行、______和死亡5种状态。","阻塞","线程五种状态",SRC_TB));
        q.add(fb(TB10,Difficulty.EASY,"JDK 5.0以前，线程的创建有两种方法：实现______接口和继承Thread类。","Runnable","两种创建线程方式",SRC_TB));
        q.add(fb(TB10,Difficulty.MEDIUM,"多线程程序设计的含义是可以将程序任务分成几个______的子任务。","并行","并行执行子任务",SRC_TB));
        q.add(fb(TB10,Difficulty.MEDIUM,"在多线程系统中，多个线程之间有______和互斥两种关系。","同步","同步与互斥",SRC_TB));
        q.add(sc(TB10,Difficulty.EASY,"线程调用了sleep()方法后，该线程将进入（ ）状态。",opts("可运行","运行","阻塞","终止"),"C","sleep使线程阻塞",SRC_TB));
        q.add(sc(TB10,Difficulty.MEDIUM,"关于Java线程，下面说法错误的是（ ）",opts("线程是以CPU为主体的行为","Java利用线程使系统异步","继承Thread类可创建线程","新线程创建后自动开始运行"),"D","需调用start()才运行",SRC_TB));
        q.add(sc(TB10,Difficulty.MEDIUM,"线程控制方法中，yield()的作用是（ ）",opts("返回当前线程的引用","使优先级比其他的线程执行","强行终止线程","只让给同优先级线程执行"),"D","yield让出CPU给同优先级",SRC_TB));
        q.add(sc(TB10,Difficulty.EASY,"当（ ）方法终止时，线程进入死亡状态。",opts("run()","setPriority()","yield()","sleep()"),"A","run结束则线程死亡",SRC_TB));
        q.add(sc(TB10,Difficulty.EASY,"线程通过（ ）方法可以改变优先级。",opts("run()","setPriority()","yield()","sleep()"),"B","setPriority改变优先级",SRC_TB));
        q.add(sa(TB10,Difficulty.EASY,"什么是线程？什么是进程？","进程是操作系统分配资源的最小单位；线程是CPU调度的最小单位，是进程内的一条执行路径。","进程vs线程",SRC_TB));
        q.add(sa(TB10,Difficulty.MEDIUM,"Java有哪几种创建线程的方式？","1.继承Thread类；2.实现Runnable接口；3.实现Callable接口配合FutureTask；4.使用线程池。","四种创建线程方式",SRC_TB));
        q.add(sa(TB10,Difficulty.MEDIUM,"什么是线程的生命周期？","线程从创建到消亡的过程：新建(New)→就绪(Runnable)→运行(Running)→阻塞(Blocked)→死亡(Dead)。","五种生命周期状态",SRC_TB));
        q.add(pg(TB10,Difficulty.MEDIUM,"利用多线程同时输出10以内的奇数和偶数及线程名称，输出完毕后输出\"end\"。","public class ThreadTest {\n    public static void main(String[] args) {\n        Thread odd = new Thread(() -> {\n            for (int i=1;i<=10;i+=2) System.out.println(Thread.currentThread().getName()+\": \"+i);\n        }, \"奇数线程\");\n        Thread even = new Thread(() -> {\n            for (int i=2;i<=10;i+=2) System.out.println(Thread.currentThread().getName()+\": \"+i);\n        }, \"偶数线程\");\n        odd.start(); even.start();\n        try { odd.join(); even.join(); } catch (Exception e) {}\n        System.out.println(\"end\");\n    }\n}","多线程基本用法",SRC_TB));

        return q;
    }
}
