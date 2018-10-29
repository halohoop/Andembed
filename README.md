# Andembed

## 使用方式

- 0. 在library的默认构建脚本build.gradle中，加入插件

     ```groovy
     apply plugin: 'com.halohoop.andembed'
     ```

- 1. 在library的默认构建脚本build.gradle中，加入插件

     ```groovy
     apply plugin: 'com.halohoop.andembed'
     ```

- 1. 加入必要的配置属性DSL

     ```groovy
     xEmbedGroup {
         //必须。配置这个库的唯一id，一般直接将Manifest的包名拷贝过来就好了
         packageName = "com.halohoop.liba"
         //必须。配置这个库的版本号码，和Android DSL相关的配置中的一致即可
         versionName = "1.0.0"
     }
     ```

- 1. 在dependencies DSL 中把需要合入Jar包的库使用 [embed] 配置加入插件的管理

     - 注意：embed不能代替implementation或者api *

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

- 1. 缺陷描述

     该插件目前尚且不能满足以下几种方式引入库的合并

     - 4.1.

     ```groovy
     dependencies {
         //...
         implementation files('libs/classes.jar')
         //...
         embed files('libs/classes.jar')//尚不支持
     }
     ```

     - 4.2.

     ```groovy
     dependencies {
         //...
         implementation files('libs/classes.jar')
         //...
         embed files('libs/classes.jar')//尚不支持
     }
     ```