# gaia 动态模块项目
<hr>

### 背景介绍
* 通过一个注解来动态完成controller、service、dao等相关类信息的生成与注册到spring容器<br/><br/>
* 消除三层模型冗余代码、统一接口标准、专注于数据模型<br/><br/>
* 开箱即用，提供常用的5种组件（controller、service、serviceImpl、dao、viewObject）与5种持久层方案（spring-boot-starter-data-jpa/hibernate、MyBatis-plus、spring-boot-starter-data-solr、spring-boot-starter-data-mongodb、spring-boot-starter-data-elasticsearch）<br/><br/>
* 基于java8、ASM、spring-boot2.0+、spring-boot-starter-data<br/><br/>
* [示例项目](https://github.com/woodenlock/gaia-example) <br/><br/>
* 许可证使用 [Apache-2.0](LICENSE)

### 使用方式
1. 在目标项目中引入此项目的maven依赖<br><br>
2.  在spring-boot项目的启动类上增加[启用注解](src/main/java/io/github/woodenlock/gaia/annotation/EnableDynamicModules.java)<br><br>
3. 在想要使用动态模块的持久化实体映射类上增加[标记注解](src/main/java/io/github/woodenlock/gaia/annotation/DynamicModule.java)<br><br>

### 默认实现
默认扫描[启用注解]所在包及其后代包（可通过配置覆盖）<br><br>
默认的持久化方案为MyBatis-plus（可通过配置或者注解覆盖）<br><br>
默认使用单属性主键<br><br>
MyBatis-plus、jpa、hibernate默认主键为Long，Solr、Elasticsearch默认主键为String，MongoDB默认主键为ObjectId（可通过自定义代码模板来覆盖）<br><br>
默认生成全部5种开箱即用的组件（controller、service、serviceImpl、dao、viewObject），并将controller、serviceImpl、dao注入spring容器（可通过配置或者注解覆盖）<br><br>

### 项目包结构
一 [src.main.java.io.github.woodenlock.gaia](src/main/java/io/github/woodenlock/gaia)<br>
&emsp;&emsp;一 [annotation](src/main/java/io/github/woodenlock/gaia/annotation)：项目相关注解<br>
&emsp;&emsp;&emsp;&emsp;一 [DynamicModule.java](src/main/java/io/github/woodenlock/gaia/annotation/DynamicModule.java)：用于标注持久化实体映射对象参与动态模块功能的生成<br>
&emsp;&emsp;&emsp;&emsp;一 [EnableDynamicModules.java](src/main/java/io/github/woodenlock/gaia/annotation/EnableDynamicModules.java)：启用gaia动态模块的功能<br>
&emsp;&emsp;&emsp;&emsp;一 [GenerateComponent.java](src/main/java/io/github/woodenlock/gaia/annotation/GenerateComponent.java)：手动覆盖持久化实体映射对象特定组件的执行策略<br>
&emsp;&emsp;一 [base](src/main/java/io/github/woodenlock/gaia/base)：默认使用的实现模板基类<br>
&emsp;&emsp;一 [common](src/main/java/io/github/woodenlock/gaia/common)：一些通用的定义<br>
&emsp;&emsp;&emsp;&emsp;一 [GenerateConst](src/main/java/io/github/woodenlock/gaia/common/GenerateConst.java)：默认持久化方案、组件、配置的名称定义<br>
&emsp;&emsp;一 [function](src/main/java/io/github/woodenlock/gaia/function)：函数式接口定义<br>
&emsp;&emsp;一 [generation](src/main/java/io/github/woodenlock/gaia/generation)：类生成器接口及其默认的实现<br>
&emsp;&emsp;一 [registrar](src/main/java/io/github/woodenlock/gaia/registrar)：spring bean注册器接口及其默认的实现<br>
&emsp;&emsp;一 [util](src/main/java/io/github/woodenlock/gaia/util)：常用简单工具类<br>
&emsp;&emsp;一 [web](src/main/java/io/github/woodenlock/gaia/web)：默认controller模板使用的相关web视图定义<br>
&emsp;&emsp;一 [DynamicModuleProperties.java](src/main/java/io/github/woodenlock/gaia/DynamicModuleProperties.java)：配置属性对象<br>
&emsp;&emsp;一 [DynamicModuleRegistrar.java](src/main/java/io/github/woodenlock/gaia/DynamicModuleRegistrar.java)：整合spring的注册器<br>
&emsp;&emsp;一 [ModuleTypeCache.java](src/main/java/io/github/woodenlock/gaia/ModuleTypeCache.java)：实际动态组件的组装及其缓存<br>
一 [src.main.resources](src/main/resources)<br>
&emsp;&emsp;一 [META-INF/spring-configuration-metadata.json](src/main/resources/META-INF/spring-configuration-metadata.json)：配置提示<br>
&emsp;&emsp;一 [application.properties](src/main/resources/application.properties)：默认开箱即用的配置<br>

### Q&A
1. 生成的部分controller查询接口返回与预期的不一致？<br>
    答：基于Lucene的相关持久化方案（包括Solr、Elasticsearch）的实现效果由对应持久化方案自身决定的，例如索引、是否存储等特定，此项目尽量不侵入持久化框架的实现。
2. 默认提供的代码模板不好用该如何覆盖？<br>
    答：自己实现目标持久化方案与组件类型的[类生成器](src/main/java/io/github/woodenlock/gaia/generation/ClassGenerator.java)，并在配置文件中指定使用该生成器；
    如果想要自定义注入spring容器的方式可以自己实现对应的[bean注册器](src/main/java/io/github/woodenlock/gaia/registrar/BeanRegistrar.java)，并在配置文件中指定使用该注册器；
    具体配置可以参考[示例项目](https://github.com/woodenlock/gaia-example) 。