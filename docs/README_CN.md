![](images/FISCO_BCOS_Logo.svg)

[English](../README.md) / 中文

# Java SDK

[![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg?style=flat-square)](http://makeapullrequest.com)
[![Build Status](https://travis-ci.org/FISCO-BCOS/java-sdk.svg?branch=master)](https://travis-ci.org/FISCO-BCOS/java-sdk)
[![CodeFactor](https://www.codefactor.io/repository/github/fisco-bcos/java-sdk/badge)](https://www.codefactor.io/repository/github/fisco-bcos/java-sdk)
[![GitHub All Releases](https://img.shields.io/github/downloads/FISCO-BCOS/java-sdk/total.svg)](https://github.com/FISCO-BCOS/java-sdk)

这是FISCO BCOS客户端的Java SDK，提供了访问FISCO BCOS节点的Java API，支持节点状态查询、部署和调用合约等功能，基于Java SDK可开发区块链应用，目前支持FISCO BCOS 2.0+。

## 功能
* 提供合约编译功能，将Solidity合约文件转换成Java合约文件
* 提供Java SDK API,提供访问FISCO BCOS JSON-RPC 的功能，并支持预编译（Precompiled）合约调用
* 提供自定义构造和发送交易功能
* 提供AMOP功能
* 支持合约事件推送
* 支持ABI解析
* 提供账户管理接口

## 新特性
这个Java SDK是web3sdk（不推荐使用）的重构版本，其包含如下几个新特性：

* 支持Toml配置文件的配置，简化配置项。
* 支持连接不同群组的节点。
* 支持AMOP动态订阅和取消订阅。
* 支持解析结构体类型数据的ABI解析。
* 使用Webank通用的加密包。
* 添加群组管理模块，帮助应用管理多个群组中节点连接。
* 使用模块化设计，便于组装再造。

## 贡献代码
欢迎参与FISCO BCOS的社区建设：
- 点亮我们的小星星(点击项目左上方Star按钮)。
- 提交代码(Pull requests)，参考我们的[代码贡献流程](CONTRIBUTING_CN.md)。
- [提问和提交BUG](https://github.com/FISCO-BCOS/java-sdk/issues)。

## 加入我们的社区

FISCO BCOS开源社区是国内活跃的开源社区，社区长期为机构和个人开发者提供各类支持与帮助。已有来自各行业的数千名技术爱好者在研究和使用FISCO BCOS。如您对FISCO BCOS开源技术及应用感兴趣，欢迎加入社区获得更多支持与帮助。


![](https://media.githubusercontent.com/media/FISCO-BCOS/LargeFiles/master/images/QR_image.png)


## License

![license](https://img.shields.io/badge/license-Apache%20v2-blue.svg)

Web3SDK的开源协议为[Apache License 2.0](http://www.apache.org/licenses/). 详情参考[LICENSE](../LICENSE)。