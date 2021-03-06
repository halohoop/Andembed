# Andembed

### [English README.md](https://github.com/halohoop/Andembed/blob/master/README_EN.md)

## 背景介绍

在Android中构建组件化的项目的时候，很多时候会遇到一种情况：

​	有ABCD四个组件（或者说库），AB可以组合在一起并且封装一下，变成一个新的组件E，BC可组合在一起并且封装一下，变成一个新的组件F。比如我们需要用到AB两个组件中的功能，但是我们又不想分别让别人同时引入两个库，所以就需要把这两个裤的代码合并到一起后向外提供一个合并之后的新组件（或者说库）。

​    燃鹅，使用内网maven私服的时候（比如：JFrog Artifactory），常常会遇到不能够使用dependencies的transitive功能的情况，也就是使用一个库的时候，它依赖的其他库不能被很好的同步下来。当然也有可能是我不会用，如果有知道怎么用的欢迎交流，WeChat在左边。

​	此插件就是为上文中提到的合并功能而生的。

## Change log

- V1.0.4
    - 新增剔除具体类的配置 excludeClass = [];

## 使用方式

- 0. 在rootProject的build.gradle中，引用插件

     ```groovy
     jcenter()
     ```

     ```groovy
     classpath 'com.halohoop:librarymerger:1.0.4'
     ```

- 2. 在library的默认构建脚本build.gradle中，加入插件

     ```groovy
     apply plugin: 'com.halohoop.andembed'
     ```

- 3. 加入必要的配置属性DSL

     ```groovy
     xEmbedGroup {
         //必须。配置这个库的唯一id，一般直接将Manifest中的包名拷贝过来就好了
         packageName = "com.halohoop.liba"
         //必须。配置这个库的版本号码，和Android DSL相关的配置中的一致即可
         versionName = "1.0.0"
         //可省略。配置最终jar包的输出文件夹的绝对路径
         outputDirPath = ""//自定义输出路径
         //V1.0.4 新增功能 剔除具体类，接受一个数组
         excludeClass = [
             'com/halohoop/liba/BuildConfig.class',
             'c/b/a/BuildConfig.class'
         ]
     }
     ```

- 4. 在dependencies DSL 中把需要合入Jar包的库使用 [embed] 配置加入插件的管理

     - **注意：embed不能代替implementation或者api **

     ```groovy
     dependencies {
         //...
         implementation 'com.android.support:appcompat-v7:28.0.0'
         api 'com.google.guava:guava:27.0-jre'
         //...
         embed 'com.android.support:appcompat-v7:28.0.0'//新增
         embed 'com.google.guava:guava:27.0-jre'//新增
     }
     ```
- 5. 打jar包Task任务make2JarRelease

     ```bash
     ./gradlew clean :module_name:make2JarRelease
     ```
     
     任务执行成功后会在对应project（module）的[build\outputs\jar]目录中生产jar包.

- 6. 缺陷描述

     该插件目前尚且不能满足以下几种方式引入库的合并

     - 6.1.

     ```groovy
     dependencies {
         //...
         implementation files('libs/classes.jar')
         //...
         embed files('libs/classes.jar')//尚不支持，V1.1.0开始会支持，正在开发中
     }
     ```

     - 6.2.

     ```groovy
     dependencies {
         //...
         implementation project(':libb')
         //...
         embed project(':libb')//尚不支持，V1.1.0开始会支持，正在开发中
     }
     ```

## License

    Copyright 2017, Halohoop

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
