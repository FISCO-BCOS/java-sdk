![](./docs/FISCO_BCOS_Logo.svg)

[English](./docs/README_EN.md) / 中文

# Java SDK

[![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg?style=flat-square)](http://makeapullrequest.com)
[![Build Status](https://travis-ci.org/FISCO-BCOS/java-sdk.svg?branch=master)](https://travis-ci.org/FISCO-BCOS/java-sdk)
[![CodeFactor](https://www.codefactor.io/repository/github/fisco-bcos/java-sdk/badge)](https://www.codefactor.io/repository/github/fisco-bcos/java-sdk)
[![GitHub All Releases](https://img.shields.io/github/downloads/FISCO-BCOS/java-sdk/total.svg)](https://github.com/FISCO-BCOS/java-sdk)

这是FISCO BCOS客户端的Java SDK，提供了访问FISCO BCOS节点的Java API，支持节点状态查询、部署和调用合约等功能，基于Java SDK可开发区块链应用。

**注意：当前分支为v3.x版本java-sdk，仅适配于FISCO BCOS 3.0+; FISCO BCOS 2.0+ 适配版本请手动切换至[master-2.0](https://github.com/FISCO-BCOS/java-sdk/tree/master-2.0)分支**

## 版本及兼容性说明

**v3.x版本仅适用于FISCO BCOS v3.x，不兼容FISCO BCOS v2.x**。

### **v2.x**

- [文档](https://fisco-bcos-doc.readthedocs.io/zh_CN/latest/docs/develop/sdk/java_sdk/index.html)

- **代码**: [GitHub](https://github.com/FISCO-BCOS/java-sdk/tree/master-2.0), [Gitee](https://gitee.com/FISCO-BCOS/java-sdk/tree/master-2.0/)

- **FISCO BCOS v2.x**: [GitHub](https://github.com/FISCO-BCOS/FISCO-BCOS/tree/master-2.0), [Gitee](https://gitee.com/FISCO-BCOS/FISCO-BCOS/tree/master-2.0/)

### **v3.x**

- [文档](https://fisco-bcos-doc.readthedocs.io/zh_CN/latest/docs/develop/sdk/java_sdk/index.html)

- **代码**: [GitHub](https://github.com/FISCO-BCOS/java-sdk/tree/master), [Gitee](https://gitee.com/FISCO-BCOS/java-sdk/tree/master)

- **FISCO BCOS v3.x**: [GitHub](https://github.com/FISCO-BCOS/FISCO-BCOS/tree/master), [Gitee](https://gitee.com/FISCO-BCOS/FISCO-BCOS/tree/master)


## 功能
* 提供合约编译功能，将Solidity合约文件转换成Java合约文件。
* 支持Liquid合约使用，包含部署Liquid合约，发起Liquid合约交易。
* 提供Java SDK API,提供访问FISCO BCOS JSON-RPC 的功能，并支持预编译（Precompiled）合约调用。
* 提供自定义构造和发送交易功能。
* 提供AMOP功能。
* 支持合约事件推送。
* 支持ABI和Scale的编解码解析。
* 提供账户管理接口。
* 提供权限管理接口。

## 新特性
Java SDK包含如下几个新特性：

* 完全支持 [FISCO BCOS 3.0+](https://fisco-bcos-doc.readthedocs.io/zh_CN/latest/).
* 支持Liquid合约使用，包含部署Liquid合约，发起Liquid合约交易。
* 支持Scale的编解码解析。
* 使用JNI封装后的[FISCO BCOS C SDK](https://github.com/FISCO-BCOS/bcos-c-sdk)接口与区块链交互。

## 文档
* [中文版用户手册](https://fisco-bcos-doc.readthedocs.io/zh_CN/latest/docs/develop/sdk/index.html)
* 英文版用户手册: 施工中...

## 快速开始
* [中文版手册](https://fisco-bcos-doc.readthedocs.io/zh_CN/latest/docs/develop/sdk/java_sdk/quick_start.html)

## 贡献代码
欢迎参与FISCO BCOS的社区建设：
- 点亮我们的小星星(点击项目左上方Star按钮)。
- 提交代码(Pull requests)，参考我们的 [代码贡献流程](https://mp.weixin.qq.com/s/_w_auH8X4SQQWO3lhfNrbQ) 。
- [提问和提交BUG](https://github.com/FISCO-BCOS/java-sdk/issues)。

## 加入我们的社区

FISCO BCOS开源社区是国内活跃的开源社区，社区长期为机构和个人开发者提供各类支持与帮助。已有来自各行业的数千名技术爱好者在研究和使用FISCO BCOS。如您对FISCO BCOS开源技术及应用感兴趣，欢迎加入社区获得更多支持与帮助。

## License

![license](https://img.shields.io/badge/license-Apache%20v2-blue.svg)

Java SDK的开源协议为[Apache License 2.0](http://www.apache.org/licenses/). 详情参考[LICENSE](LICENSE)。
