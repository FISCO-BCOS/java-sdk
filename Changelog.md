## v3.0.0-rc3
(2022-03-31)

请阅读Java SDK v3.x+文档：

- [中文用户手册](https://fisco-bcos-doc.readthedocs.io/zh_CN/latest/docs/develop/sdk/java_sdk/index.html)

### 新增

* 支持Solidity合约并行冲突字段解析
* 添加getABI接口
### 更新

* 调用`bcos-c-sdk`的接口实现交易编解码功能
* 调用`bcos-c-sdk`的接口实现密码算法
* 升级`bcos-sdk-jni`到`3.0.0-rc3`
* `org.fisco.bcos.sdk`包路径修改为`org.fisco.bcos.sdk.v3`
* sdk-abi代码重构
* BFS link取代CNS

### 修复

* 修复AMOP `getTopics`返回null导致的异常
* 修复[#issue 510](https://github.com/FISCO-BCOS/java-sdk/issues/510)，解决windows环境无法找不到`libbcos-sdk-jni.dll`动态库的问题
* 修复ABI/Scale编解码相关的bug


### 兼容性说明

- 不兼容 FISCO BCOS 2.0+ 版本
- 兼容java-sdk v3.x的历史版本
- 支持[3.0.0-rc3](https://github.com/FISCO-BCOS/FISCO-BCOS/releases/tag/v3.0.0-rc3)版本和[3.0.0-rc2](https://github.com/FISCO-BCOS/FISCO-BCOS/releases/tag/v3.0.0-rc2)版本的FISCO BCOS区块链
- 不支持[3.0.0-rc1](https://github.com/FISCO-BCOS/FISCO-BCOS/releases/tag/v3.0.0-rc1)版本的FISCO BCOS区块链

----

Please read documentation of Java SDK v3.x.

* English User Handbook：Working in progress...
* [Chinese User Handbook](https://fisco-bcos-doc.readthedocs.io/zh_CN/latest/docs/develop/sdk/java_sdk/index.html)

### Added

* Support Solidity contract parallel conflict field analyze
* support getABI interface
### Changed

* Call the interface of `bcos-c-sdk` to implement transaction encoding and decoding functions
* Call the interface of `bcos-c-sdk` to implement the cryptographic algorithm
* Upgrade `bcos-sdk-jni` to `3.0.0-rc3`
* `org.fisco.bcos.sdk` package path is modified to `org.fisco.bcos.sdk.v3`
* sdk-abi code refactor
* use BFS link to replace CNS

### Fixed

* Fix exception caused by AMOP `getTopics` returning null
* Fix [#issue 510](https://github.com/FISCO-BCOS/java-sdk/issues/510) to solve the problem that `libbcos-sdk-jni.dll` dynamic library cannot be found in windows environment
* Fix bugs related to ABI/Scale codec
### Compatibility Notes

- Not compatible with FISCO BCOS version 2.0+
- Incompatible with the historical version of java-sdk v3.x
- Support [3.0.0-rc3](https://github.com/FISCO-BCOS/FISCO-BCOS/releases/tag/v3.0.0-rc3) version and [3.0.0-rc2](https://github .com/FISCO-BCOS/FISCO-BCOS/releases/tag/v3.0.0-rc2) FISCO BCOS
- [3.0.0-rc1](https://github.com/FISCO-BCOS/FISCO-BCOS/releases/tag/v3.0.0-rc1) FISCO BCOS blockchain is not supported

## v3.0.0-rc2
(2022-02-23)

请阅读Java SDK v3.x+文档：

- [中文用户手册](https://fisco-bcos-doc.readthedocs.io/zh_CN/latest/docs/develop/sdk/java_sdk/index.html)

### 更新

* 升级`bcos-sdk-jni`到`3.0.0-rc2`
* 修改SDK的默认群组为`group0`
* 交易由`Base64`编码修改为十六进制编码
* 对齐WBC-Liquid，修改`bytesN`类型数据的Scale编码

### 修复

* 修复Scale编码 struct数组编码丢失泛型类型数据的问题

### 兼容性说明

- 不兼容 FISCO BCOS 2.0+ 版本
- 兼容java-sdk v3.x的历史版本
- 支持[3.0.0-rc2](https://github.com/FISCO-BCOS/FISCO-BCOS/releases/tag/v3.0.0-rc2)版本的FISCO BCOS区块链
- 不支持[3.0.0-rc1](https://github.com/FISCO-BCOS/FISCO-BCOS/releases/tag/v3.0.0-rc1)版本的FISCO BCOS区块链

----

Please read documentation of Java SDK v3.x.

* English User Handbook：Working in progress...
* [Chinese User Handbook](https://fisco-bcos-doc.readthedocs.io/zh_CN/latest/docs/develop/sdk/java_sdk/index.html)

### Changed

* Upgrade `bcos-sdk-jni` to `3.0.0-rc2`
* Modify the default group of the SDK to `group0`
* Transaction modified from `Base64` encoding to hexadecimal encoding
* Align with WBC-Liquid, modify the Scale encoding of `bytesN`

### Fixed

* Fix the problem that Scale encoding struct array loses generic type data

### Compatibility Notes

- Not compatible with FISCO BCOS version 2.0+
- Compatible with historical versions of `java-sdk v3.x`
- Support [3.0.0-rc2](https://github.com/FISCO-BCOS/FISCO-BCOS/releases/tag/v3.0.0-rc2) version of FISCO BCOS blockchain
- [3.0.0-rc1](https://github.com/FISCO-BCOS/FISCO-BCOS/releases/tag/v3.0.0-rc1) version of FISCO BCOS blockchain is not supported


## v3.0.0-rc1
(2021-12-10)

请阅读Java SDK文档：

- [中文用户手册](https://fisco-bcos-doc.readthedocs.io/zh_CN/latest/docs/develop/sdk/java_sdk/index.html)

### 新特性
Java SDK包含如下几个新特性：

* 完全支持 [FISCO BCOS 3.0+](https://fisco-bcos-doc.readthedocs.io/zh_CN/latest/)，不兼容 FISCO BCOS 2.0+ 版本。FISCO BCOS 2.0+ 版本请使用对应版本的Java SDK。
* 支持Liquid合约使用，包含部署Liquid合约，发起Liquid合约交易。
* 支持Scale的编解码解析。
* 使用JNI封装后的[FISCO BCOS C SDK](https://github.com/FISCO-BCOS/bcos-c-sdk)接口与区块链交互。

### 功能
* 提供合约编译功能，将Solidity合约文件转换成Java合约文件。
* 支持Liquid合约使用，包含部署Liquid合约，发起Liquid合约交易。
* 提供Java SDK API,提供访问FISCO BCOS JSON-RPC 的功能，并支持预编译（Precompiled）合约调用。
* 提供自定义构造和发送交易功能。
* 提供AMOP功能。
* 支持合约事件推送。
* 支持ABI和Scale的编解码解析。
* 提供账户和权限管理接口。

### 兼容性说明

- 不兼容 FISCO BCOS 2.0+ 版本
- 支持[3.0.0-rc1](https://github.com/FISCO-BCOS/FISCO-BCOS/releases/tag/v3.0.0-rc1)版本的FISCO BCOS区块链

----

Please read documentation of Java SDK.

* English User Handbook：Working in progress...
* [Chinese User Handbook](https://fisco-bcos-doc.readthedocs.io/zh_CN/latest/docs/develop/sdk/java_sdk/index.html)

### New Features

Java SDK includes the following new features:

* Support [FISCO BCOS 3.0+](https://fisco-bcos-doc.readthedocs.io/zh_CN/latest/). No longer support for FISCO BCOS 2.0+, if you want to use Java SDK to connect FISCO BCOS 2.0+, please use Java SDK in 2.0+ versions.
* Support Liquid usage, including deploy contracts, sending transactions.
* Support Scale encoding and decoding data.
* Use JNI wrapper of [FISCO BCOS C SDK](https://github.com/FISCO-BCOS/bcos-c-sdk) interface, which connecting blockchain nodes.

### Functions
* Solidity contract compiling.
* Liquid contract support, including deploy contracts, sending transactions.
* Interacting with FISCO BCOS JSON-RPC interface.
* Constructing and sending transactions.
* Advanced Messages Onchain Protocol(AMOP) functions.
* Contract event subscription.
* Encoding and decoding data with ABI and Scale.
* Account Management.
* Authority Management.

### compatibility

- Not compatible with FISCO BCOS version 2.0+.
- Support [3.0.0-rc1](https://github.com/FISCO-BCOS/FISCO-BCOS/releases/tag/v3.0.0-rc1) version of FISCO BCOS blockchain.

## v2.8.0
(2021-07-27)
Added:
* Add Hardware Secure Module(HSM), use hardware protect your private key, speed up crypto procedure.
* Support use PCI crypto card or crypto machine to make SM2 SM3 calculation.
* Support use HSM internal key to make TLS connection with FISCO BCOS nodes.
* Support use HSM internal key to sign transaction.

Update:
* Update crypto dependency version of sdk-crypto module.
* Support to read the certificate from the jar package.
* The interface that sent the transaction returns the transaction hash.
* Add VRF random number generation and random number verification interface.

----
添加:
* 新增硬件加密模块，使用硬件保护您的私钥，提升密码运算速度。
* 支持使用PCI加密卡/加密机进行SM2，SM3运算。
* 支持使用密码卡/密码机内部密钥与FISCO BCOS节点建立连接。
* 支持使用密码卡/密码机内部密钥进行交易签名。

更新：
* 更新sdk-crypto模块所使用的密码算法库版本。
* 支持从jar包中读取证书。
* 发送交易的接口返回交易哈希。
* 添加VRF随机数生成和随机数验证接口。

## v2.7.2
(2021-03-24)
Please read documentation of Java SDK.

* [English User Handbook](https://fisco-bcos-documentation.readthedocs.io/en/latest/docs/sdk/java_sdk/index.html)
* [Chinese User Handbook](https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/sdk/java_sdk/index.html#)
* [Chinese WIKI](https://github.com/FISCO-BCOS/java-sdk/wiki)

Added:
* Add an interface that supports remotely calling signature services to assemble and send transactions(AssembleTransactionWithRemoteSignProcessor, RemoteSignProviderInterface)

Fixed:
 * Fix the problem that the ContractLoader of the java sdk is loaded incorrectly in some scenarios, which causes the deployment and call of the contract to fail [#264](https://github.com/FISCO-BCOS/java-sdk/issues/264)
 * Fix the problem of errors when loading certificates and private keys from the classpath path in the windows environment [#260](https://github.com/FISCO-BCOS/java-sdk/issues/260)
 * Fix the problem that the FromBlock of EventLogParams does not take effect [#253](https://github.com/FISCO-BCOS/java-sdk/issues/253)
 * Fix the problem that the Java SDK can only read the private key generated by openssl with the -key name parameter added [#249](https://github.com/FISCO-BCOS/java-sdk/issues/249)


Compatibility

* FISCO BCOS v2.0+
* AMOP Not compatible with Web3SDK
----
请参考文档：
* [英文版用户手册](https://fisco-bcos-documentation.readthedocs.io/en/latest/docs/sdk/java_sdk/index.html)
* [中文版用户手册](https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/sdk/java_sdk/index.html#)
* [中文版WIKI](https://github.com/FISCO-BCOS/java-sdk/wiki)

添加:
* 增加支持远程调用签名服务来组装并发送交易的接口(AssembleTransactionWithRemoteSignProcessor, RemoteSignProviderInterface)


修复：

 * 修复某些场景下Java SDK的ContractLoader加载出错，导致部署和调用合约失败的问题 [#264](https://github.com/FISCO-BCOS/java-sdk/issues/264)
 * 修复windows环境下从classpath路径下加载证书和私钥出错的问题 [#260](https://github.com/FISCO-BCOS/java-sdk/issues/260)
 * 修复EventLogParams的FromBlock不生效的问题 [#253](https://github.com/FISCO-BCOS/java-sdk/issues/253)
 * 修复Java SDK只能读取openssl添加-key name参数的生成的私钥的问题 [#249](https://github.com/FISCO-BCOS/java-sdk/issues/249)

 
适配性：
* 适配FISCO BCOS v2.0+
* AMOP功能不兼容Web3SDK

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
