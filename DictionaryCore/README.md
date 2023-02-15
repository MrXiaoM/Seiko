# 零.引言

- 本伪代码大部分和**SQ系**写法类似。Seiko力求低成本迁移你之前的伪代码  
  同时引入新的变量伪代码等来完善写法。
- 本伪代码模块仍处于初级阶段。如有需求请火速issue

# 一.导入

- 将你的伪代码文件移入`/sdcard/Android/data/com.kagg886.seiko/dic`中
- 前往伪代码页面，下拉刷新

# 二.编写工具

推荐MT管理器，当然其他的随便。萝卜白菜，各有所好

# 三.伪代码的基本格式:(以后会改)

一份Seiko伪代码文档由若干个指令构成，其中一份指令领导着若干伪代码。

## 指令

指令由两部分构成:  
`[事件]正则表达式`  
前面的"事件匹配字符串"指的是此指令需要匹配的事件  
而后面的正则表达式则是我们的指令  
你可以使用复合写法，例如按照下面的写法可以同时匹配群和好友命令。  
_不只是只能写两个，后期更新事件时，想追加多少就可以追加多少_  
`[群|好友]早上好`

> 当消息被SeikoDIC接收时，SeikoDIC会先筛选事件类型与其相匹配的词条，然后将消息内容与正则表达式一个一个进行匹配操作。  
> 若以上操作匹配到词条了，则抽出对应的伪代码开始操作  
> 例如:`[群]早上好`可以匹配每一个在群内发送早上好的人  
> **以下是支持的事件列表(以后会补充)**

| 事件匹配字符串 | 详情       | 补充  |
|---------|----------|-----|
| 群       | 代表一个群消息  | 无   |
| 好友      | 代表一个好友消息 | 无   |

> [你可以在这里找到内建事件匹配的所有字符串和他们对应的Mirai事件](src/main/java/com/kagg886/seiko/dic/entity/DictionaryCommandMatcher.java)

## 纯文本

在指令后一定会出现一个纯文本，不然伪代码不会发送任何消息  
直接写入你想发送的消息即可。例如：

```text
[群]早上好
欧尼酱早上好~\n
今天是星期日~
```

如上伪代码被加载后，当群里的一个人发送早上好时，bot将会发送第二行和第三行信息。_(以后详细信息会省略。以此精简文档体积)_
> 我们约定使用\n来作为伪代码的换行符。当此符号出现时，将会换一行再输出。

## 变量

变量使用`%变量名%`来访问。在纯文本中，带有%变量名%的字样会被伪代码引擎替换成别的字符。例如:

```text
[群]我的QQ
您的QQ是:%QQ%
```

如上伪代码被**我**加载后发送的是`你的QQ:485184047`  
伪代码执行过程中内置了一些变量。具体得看是这个指令隶属于哪个事件。

### 下表是所有事件都会内置的变量：

| 变量名 | 类型                              | 介绍          | 备注     |
|-----|---------------------------------|-------------|--------|
| 上下文 | JavaObject(MessageEvent)        | 获得当前的消息包对象  | 暂时不要使用 |
| 缓冲区 | JavaObject(MessageChainBuilder) | 准备发送的消息串    | 暂时不要使用 |
| 时间戳 | 数字                              | 获取伪代码被执行的时间 | 无      |

> [你可以在这里找到所有事件都会内置的内建变量](src/main/java/com/kagg886/seiko/dic/session/AbsRuntime.java)

### 下表是群事件内置的变量

| 变量名  | 类型  | 作用                  |                      备注                       |
|------|-----|---------------------|:---------------------------------------------:|
| 群号   | 数字  | 获得发送此消息的群号          |                       无                       |
| 群名称  | 字符串 | 获得发送此消息的群名称         |                       无                       |
| QQ   | 数字  | 获得发送此消息的QQ号         |                       无                       |
| 昵称   | 字符串 | 获得发送此消息的QQ昵称        |                       无                       |
| 群名片  | 字符串 | 获得发送此消息的QQ在这个群里的群名片 |                       无                       |
| BOT  | 数字  | 接收到此消息的bot的QQ号      |                       无                       |
| 特殊头衔 | 字符串 | 获得发送此消息的QQ的特殊头衔     |               若无特殊头衔则和活跃头衔保持一致                |
| 头衔   | 字符串 | 获得发送此消息的QQ的活跃头衔     |                       无                       |
| 权限   | 字符串 | 获得发送此消息的QQ的权限字符串    | OWNER为群主<br/>ADMINISTRATOR为管理员<br/>MEMBER为群成员 |
| 权限代码 | 数字  | 获得发送此消息的QQ的权限字符串    |                   群主＞管理员＞成员                   |
| 艾特N  | 数字  | 获得发送此消息的QQ本次艾特的人的QQ |    若没有的话则此变量不会被替换<br/>实际使用应写成:`艾特0`,`艾特1`     |
| 艾特数  | 数字  | 获得发送此消息的QQ本次艾特的人的个数 |                       无                       |
| 图片N  | 字符串 | 获得发送此消息的QQ本次发送的图片链接 |                   同:**艾特N**                   |
| 图片数  | 数字  | 获得发送此消息的QQ发送的图片张数   |                       无                       |
| 语音链接 | 字符串 | 获得接收到的语音链接          |            匹配指令应写成`[群][语音链接]`(未测试)            |
| 语音秒数 | 数字  | 获得接受到的语音持续时间        |                       无                       |

> [你可以在这里找到群消息的内置变量](src/main/java/com/kagg886/seiko/dic/session/impl/GroupMessageRuntime.java)

### 下表是好友事件内置的变量

| 变量名 | 类型  | 作用             | 备注  |
|-----|-----|----------------|:---:|
| QQ  | 数字  | 获得发送此消息的QQ号    |  无  |
| 昵称  | 字符串 | 获得发送此消息的QQ昵称   |  无  |
| BOT | 数字  | 接收到此消息的bot的QQ号 |  无  |
| 备注  | 字符串 | 获得bot为其设置的备注   |  无  |

> [你可以在这里找到好友消息的内置变量](src/main/java/com/kagg886/seiko/dic/session/impl/FriendMessageRuntime.java)

## 计算

SeikoDIC支持算数，但是你要为它们套上方括号**必须是英文半角！**

```text
[群]计算
5+3=[5+3]\n
114+514=[114+514]
```

如上伪代码被加载后，输出结果是`5+3=8...(后略)`  
如果不套方括号，则只会输出`5+3=5+3...(后略)`

## 条件表达式

SeikoDIC支持条件表达式，以下伪代码展示了如何使用它：

```text
[群]早上好
早上好鸭~
如果:%QQ%==485184047
今天又是美好的一天呢
返回
如果尾
要不要贴贴——
```

`如果`会匹配它冒号后的表达式，若为true则执行返回前的伪代码。  
若为false则跳转到`如果尾`后，若全文都无如果尾则终止此词条的执行。  
**值得一提的是：在遇到`如果`时，会将此行伪代码前存储的所有字符串全部打包发送。因此在上面的运行结果中，你会看到两条信息**

## 函数

Seiko使用`$`包裹字符串的两头来实现函数功能。例如:`$函数 函数参数$`  
但是与Seiko的函数与SQ系伪代码函数略有不同。在学习前请看下面的预备知识：

### 预备知识：

- Seiko的函数分为两类：阻断函数和非阻断函数：  
  所谓阻断函数就是在伪代码引擎调用此函数前会发送在执行此条消息前的所有消息。
  非阻断函数则不会出现此效果。  
  让我们举个例子：在下面的结果中，将会先输出`开始延时了！`，然后等待1秒后输出`延时成功！`和**自己的QQ图片**在一起的图文消息
- 若函数的参数为变量名称(仍需遵守上文提及的变量格式)，则Seiko会将其替换成引用池里的对象而不是字符串(实验性)

```text
[群]延时
开始延时了！
$延时 1000$
延时成功！
$图片 https://q1.qlogo.cn/g?b=qq&nk=485184047&s=640$
```

- Seiko函数不支持嵌套调用

### 函数列表

| 名字       | 参数个数 | 调用说明                                                                       | 函数类型  | 说明                                   | 调用示例                                                                                                                                                                                                                                                 |
|----------|------|----------------------------------------------------------------------------|-------|--------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| 图片       | 1    | `$图片 图片url$`                                                               | 非阻断函数 | 向消息链里添加一张图片                          | `$图片 https://q1.qlogo.cn/g?b=qq&nk=%QQ%&s=640$`                                                                                                                                                                                                      |
| 读        | 4或3  | `$读 变量名 文件 键 值$`<br/>`$读 变量名 文件 键$`                                        | 非阻断函数 | 读取文件，然后读取键。若键不存在则返回值(值那里可以不用写)       | `$读 A sss.txt key value$`<br/>`$读 A sss.txt key$`                                                                                                                                                                                                    |
| 写        | 3    | `$写 文件 键 值$`                                                               | 非阻断函数 | 写入键值对到文件中                            | `$写 A sss.txt key$`                                                                                                                                                                                                                                  |
| 延时       | 1    | `$延时 毫秒数$`                                                                 | 阻断函数  | 延时后执行后面的代码，单位为毫秒                     | `$延时 1000$`                                                                                                                                                                                                                                          |
| 语音       | 3或2  | `$语音 [群/好友] [群号/好友qq] 语音链接$`<br/>`$语音 %上下文% 语音链接$`                         | 阻断函数  | 发送语音消息，**使用mp3会导致电脑端听不了**            | `$语音 %上下文% https://www.text-to-speech.cn/mp3/1193435796_1674957193_0.mp3$` <br/>` $语音 群 123456789 https://www.text-to-speech.cn/mp3/1193435796_1674957193_0.mp3$` <br/> `$语音 好友 1234 https://www.text-to-speech.cn/mp3/1193435796_1674957193_0.mp3$` |
| 变量赋值     | 2    | `$赋值 变量名 值或表达式$`                                                           | 非阻断函数 | 自定义一个变量并给他对应的值                       | `$赋值 A 字符串$`<br>`$赋值 B 1$` <br/> `$赋值 C [2*%B%+%QQ%$]`                                                                                                                                                                                               |
| 变量删除     | 1    | `$变量删除 变量名$`                                                               | 非阻断函数 | 删除一个变量                               | `$变量删除 A$`                                                                                                                                                                                                                                           |
| 变量检验     | 2    | `$检验变量 存入变量 要检验的变量$`                                                       | 非阻断函数 | 检验要检验的变量是否存在。并将结果存入变量中               | `$检验变量 B A$`(把A的检验结果放进B中)                                                                                                                                                                                                                            |
| 随机数      | 2    | `$随机数 变量名 最小值 最大值$`                                                        | 非阻断函数 | 生成一个随机数并存放到变量名中(左闭右开区间)              | `$随机数 A 1 3$`                                                                                                                                                                                                                                        |
| 集合创建     | 1    | `$集合创建 变量名$`                                                               | 非阻断函数 | 声明一个集合，可以往集合里放各种数据                   | `$集合创建 A$`                                                                                                                                                                                                                                           |
| 集合导入     | 2    | `$集合导入 变量名 要转化的字符串$`                                                       | 非阻断函数 | 声明一个集合并赋值，支持XML JSON HTTP头和Cookie表达式 | `$集合导入 A {"a":"b","c":"d","e":114514,"f":true,"g":{"h":"k","m":"n"}}$`                                                                                                                                                                               |
| 取集合      | 3    | `$取集合 变量名 集合名 键$`                                                          | 非阻断函数 | 获取集合内一个键代表的值                         | `$取集合 A0 A a$`                                                                                                                                                                                                                                       |
| 集合赋值     | 3    | `$集合赋值 集合名 集合变量 要赋予的值$`                                                    | 非阻断函数 | 向一个集合里添加一个键值对。                       | `$集合赋值 A B C$`                                                                                                                                                                                                                                       |
| 集合删除     | 2    | `$集合删除 集合名 集合变量$`                                                          | 非阻断函数 | 删除集合中的一个数据，以键为索引                     | `$集合删除 A B$`                                                                                                                                                                                                                                         |
| 集合检验     | 3    | `$集合检验 集合名 检验变量 存入变量$`                                                     | 非阻断函数 | 检验集合中的键是否存在，并将结果写入存入变量中              | `$集合检验 A B C$`                                                                                                                                                                                                                                       |
| 集合转      | 3    | `$集合转 集合名 转入变量 JSON/JAVA$`                                                 | 非阻断函数 | 将集合名格式化成json结构，以用于网络传输等              | `$集合转 A B JSON$`<br/>`$集合转 A JAVA$`                                                                                                                                                                                                                  |
| JAVA方法运行 | 5或4  | `$JAVA方法运行 存入变量 类名(全限定) 方法名 对象 参数列表$`<br/>`$JAVA方法运行 存入变量 类名(全限定) 方法名 对象$` | 非阻断函数 | 使用反射调用java方法，配合上文的JavaObject使用       | `$JAVA方法运行 A net.mamoe.mirai.event.events.GroupMessageEvent getSenderName %上下文%$`<br/>`$JAVA方法运行 A java.lang.String contains 114514 4$`                                                                                                              |                                      |