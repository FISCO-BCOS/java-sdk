## v2.7.1
(2020-12-24)
Please read documentation of Java SDK.

* [English User Handbook](https://fisco-bcos-documentation.readthedocs.io/en/latest/docs/sdk/java_sdk/index.html)
* [Chinese User Handbook](https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/sdk/java_sdk/index.html#)
* [Chinese WIKI](https://github.com/FISCO-BCOS/java-sdk/wiki)

Added:
* Support JDK 15
* AMOP module supports hexadecimal public key and private key as parameters
* Support constructing `AssembleTransactionProcessor` through the contents of `bin` and `abi`
* `AssembleTransactionProcessor` supports the construction of signed transactions

Changed：
* Rename the jar package of Java SDK to `fisco-bcos-java-sdk`
* Support loading certificates and account private keys from the `resources` path
* `ABICodecJsonWrapper` supports the representation of `bytes` type parameters in Base64 and hexadecimal encoding, and distinguishes encoding types with prefixes of `base64://` and `hex://`

Compatibility

* FISCO BCOS v2.0+
* AMOP Not compatible with Web3SDK
----
请参考文档：
* [英文版用户手册](https://fisco-bcos-documentation.readthedocs.io/en/latest/docs/sdk/java_sdk/index.html)
* [中文版用户手册](https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/sdk/java_sdk/index.html#)
* [中文版WIKI](https://github.com/FISCO-BCOS/java-sdk/wiki)

添加:
* 支持JDK 15
* AMOP模块支持传入十六进制的公钥和私钥
* 支持通过`bin`和`abi`的内容构造`AssembleTransactionProcessor`
* `AssembleTransactionProcessor`支持构造带有签名的交易


修改：
 * Java SDK的jar包重命名为`fisco-bcos-java-sdk`
 * 支持从`resources`路径加载证书和账户私钥
 * `ABICodecJsonWrapper`支持以Base64、十六进制编码形式表示`bytes`类型参数，并以`base64://`、`hex://`前缀区分编码类型
 
适配性：
* 适配FISCO BCOS v2.0+
* AMOP功能不兼容Web3SDK

## v2.7.0
(2020-11-20)
Please read documentation of Java SDK.

* [English User Handbook](https://fisco-bcos-documentation.readthedocs.io/en/latest/docs/sdk/java_sdk/index.html)
* [Chinese User Handbook](https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/sdk/java_sdk/index.html#)
* [Chinese WIKI](https://github.com/FISCO-BCOS/java-sdk/wiki)

Added:
 * Added `getBatchReceiptsByBlockNumberAndRange` and `getBatchReceiptsByBlockHashAndRange` interfaces to support batch pull block transaction receipts
 * Added the `getNodeInfo` interface to obtain subscribed topics information
 * Added `revokeManager` interface to revoke contract life cycle management authority
 * `ChainGovernanceService` adds `queryVotesOfMember` and `queryVotesOfThreshold` interfaces to support query voting status

Changed：
 * Separate `sdk-demo` as a `java-sdk-demo` project

Fixed:
 * Fix the exception of contract receipt parsing null pointer exception when deploying constructor with event

Compatibility

* FISCO BCOS v2.0+
* AMOP Not compatible with Web3SDK

----
请参考文档：
* [英文版用户手册](https://fisco-bcos-documentation.readthedocs.io/en/latest/docs/sdk/java_sdk/index.html)
* [中文版用户手册](https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/sdk/java_sdk/index.html#)
* [中文版WIKI](https://github.com/FISCO-BCOS/java-sdk/wiki)

添加:
 * 添加`getBatchReceiptsByBlockNumberAndRange`和`getBatchReceiptsByBlockHashAndRange`接口，支持批量拉取区块交易回执
 * 添加`getNodeInfo`接口，获取订阅的topics信息
 * 添加`revokeManager`接口，撤销合约生命周期管理权限
 * `ChainGovernanceService`添加`queryVotesOfMember`和`queryVotesOfThreshold`接口，支持查询投票情况

修改：
 * 将`sdk-demo`独立为`java-sdk-demo`项目

修复:
 * 修复部署构造函数带有event的合约异常时，合约回执解析空指针异常
 
适配性：
* 适配FISCO BCOS v2.0+
* AMOP功能不兼容Web3SDK

## v2.6.1
(2020-10-29)
Please read documentation of Java SDK.

* [English User Handbook](https://fisco-bcos-documentation.readthedocs.io/en/latest/docs/sdk/java_sdk/index.html)
* [Chinese User Handbook](https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/sdk/java_sdk/index.html#)
* [Chinese WIKI](https://github.com/FISCO-BCOS/java-sdk/wiki)

Changed：
* Hidden the amop private topic suffix in the callback function.
* Added amop demo required resources.
* Upgraded two dependencies that netty-sm-ssl-context to version v1.2.0 and netty to version v4.1.53.Final.
* Improved the documentation of send transaction without bin file.
* Removed unused arguments(abi file) in the functions like encodeMethodByInterface of ABI module.

Fixed:
* Solved the problem of crypto module cannot call by multiple tasks.
* Solved invalid block limit problem, when console delete data and restart.
* Fixed an event decode bug that cannot decode multiple events of one transaction.
* Fixed the AMOP message data truncated bug.

Compatibility

* FISCO BCOS v2.0+
* AMOP Not compatible with Web3SDK

----
请参考文档：
* [英文版用户手册](https://fisco-bcos-documentation.readthedocs.io/en/latest/docs/sdk/java_sdk/index.html)
* [中文版用户手册](https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/sdk/java_sdk/index.html#)
* [中文版WIKI](https://github.com/FISCO-BCOS/java-sdk/wiki)

修改：
 * 隐藏AMOP私有话题的话题前缀。
 * 添加AMOP Demo所需的资源文件。
 * 升级两个依赖版本，netty-sm-ssl-context到v1.2.0，netty 到 v4.1.53.Final.
 * 完善不使用bin文件构造交易的方法文档。
 * 在ABI模块中的encodeMethodByInterface等方法中删除无用的参数（abi文件）。
 
修复:
 * 解决加密模块无法被多进程访问的问题。
 * 解决控制台删除数据后重启会出现block limit不合法异常的问题。
 * 解决同一个交易中如果有多个Event，只能解析一个无法解析多个的问题。
 * 解决AMOP消息数据被截断的问题。
 
适配性：
* 适配FISCO BCOS v2.0+。
* 不适配Web3SDK。意思是，如果两个用户想使用AMOP功能进行通信，他们必须要么同时使用Java SDK，要么同时使用Web3SDK。
 
## v2.6.1-rc1
(2020-09-30)
Please read documentation of Java SDK.

* [English User Handbook](https://fisco-bcos-documentation.readthedocs.io/en/latest/docs/sdk/java_sdk/index.html)
* [Chinese User Handbook](https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/sdk/java_sdk/index.html#)
* [Chinese WIKI](https://github.com/FISCO-BCOS/java-sdk/wiki)

Support functions are:

* Contract compiling.
* Interacting with FISCO BCOS JSON-RPC interface.
* constructing and sending transactions.
* Advanced Messages Onchain Protocol(AMOP) functions.
* Contract event subscription.
* Encoding and decoding data with ABI. 
* Account Management.

This java sdk is a code refactoring version base on Web3SDK 2.6.1. It includes the following new features:

* Support Toml config file, simplify configuration options.
* Support connecting with nodes of different groups.
* Support AMOP subscription and unsubscription at any time instead of just before start service.
* ABI module add support of encode and decode of struct type data.
* Use the common crypto tools of WeBank.
* Add a new module called group management to help applications manage nodes which java SDK connecting with.
* Use modular design, each module can use independently. For example, you can create crypto.jar file if only crypto module is needed.

Compatibility

* FISCO BCOS v2.0+.
* Not compatible with Web3SDK. When two users want to use AMOP functions to talk with each other, they should either both use Java SDK, or both use Web3SDK. 

----

请参考文档：
* [英文版用户手册](https://fisco-bcos-documentation.readthedocs.io/en/latest/docs/sdk/java_sdk/index.html)
* [中文版用户手册](https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/sdk/java_sdk/index.html#)
* [中文版WIKI](https://github.com/FISCO-BCOS/java-sdk/wiki)

支持功能：
* 合约编译。
* FISCO BCOS JSON-RPC 调用。
* 构造和发送交易。
* AMOP功能。
* 合约事件订阅。
* ABI 数据编解码。
* 账户管理。

Java SDK是基于Web3SDK重构的，它包含的新特性有：
* 支持Toml文件配置，简化配置项。
* 支持同时连接不同群组的节点。
* 支持动态订阅和取消订阅AMOP话题。
* ABI模块支持对结构体的编解码。
* 使用WeBank的通用加密工具。
* 新增群组管理模块，帮助用户管理不同群组的节点，简化应用代码。
* 使用模块化设计，便于组装再造。用户可以根据需要下载和使用必要的模块。

适配性：
* 适配FISCO BCOS v2.0+。
* 不适配Web3SDK。意思是，如果两个用户想使用AMOP功能进行通信，他们必须要么同时使用Java SDK，要么同时使用Web3SDK。