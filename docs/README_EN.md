![](docs/images/FISCO_BCOS_Logo.svg)

English / [中文](docs/README_CN.md)

# Java SDK

[![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg?style=flat-square)](http://makeapullrequest.com)
[![Build Status](https://travis-ci.org/FISCO-BCOS/java-sdk.svg?branch=master)](https://travis-ci.org/FISCO-BCOS/java-sdk)
[![CodeFactor](https://www.codefactor.io/repository/github/fisco-bcos/java-sdk/badge)](https://www.codefactor.io/repository/github/fisco-bcos/java-sdk)
[![GitHub All Releases](https://img.shields.io/github/downloads/FISCO-BCOS/java-sdk/total.svg)](https://github.com/FISCO-BCOS/java-sdk)

This is the FISCO BCOS Client SDK for Java. Developers can use Java SDK to build blockchain applications with FISCO BCOS blockchain.

**The current branch is v3.x version java-sdk, which is only suitable for FISCO BCOS 3.0+; please manually switch to [master-2.0](https://github.com/FISCO-BCOS/java-sdk/tree/master-2.0) branch for FISCO BCOS 2.0+ adaptation sdk).**

## Version and Compatibility Notes

**Version v3.x is only for FISCO BCOS v3.x, not compatible with FISCO BCOS v2.x**.

### **v2.x**

- [Documentation](https://fisco-bcos-doc.readthedocs.io/zh_CN/latest/docs/develop/sdk/java_sdk/index.html)

- **Code**: [GitHub](https://github.com/FISCO-BCOS/java-sdk/tree/master-2.0), [Gitee](https://gitee.com/FISCO-BCOS/java-sdk/tree/master-2.0/)

- **FISCO BCOS v2.x**: [GitHub](https://github.com/FISCO-BCOS/FISCO-BCOS/tree/master-2.0), [Gitee](https://gitee.com/FISCO-BCOS/FISCO-BCOS/tree/master-2.0/)

### **v3.x**

- [Documentation](https://fisco-bcos-doc.readthedocs.io/zh_CN/latest/docs/develop/sdk/java_sdk/index.html)

- **Code**: [GitHub](https://github.com/FISCO-BCOS/java-sdk/tree/master), [Gitee](https://gitee.com/FISCO-BCOS/java-sdk/tree/master)

- **FISCO BCOS v3.x**: [GitHub](https://github.com/FISCO-BCOS/FISCO-BCOS/tree/master), [Gitee](https://gitee.com/FISCO-BCOS/FISCO-BCOS/tree/master)


## Functions
* Solidity contract compiling.
* Liquid contract support, including deploy contracts, sending transactions.
* Interacting with FISCO BCOS JSON-RPC interface.
* Constructing and sending transactions.
* Advanced Messages Onchain Protocol(AMOP) functions.
* Contract event subscription.
* Encoding and decoding data with ABI and Scale.
* Account Management.
* Authority Management.

## New Features
Java SDK includes the following new features:

* Support [FISCO BCOS 3.0+](https://fisco-bcos-doc.readthedocs.io/zh_CN/latest/).
* Support Liquid usage, including deploy contracts, sending transactions.
* Support Scale encoding and decoding data.
* Use JNI wrapper of [FISCO BCOS C SDK](https://github.com/FISCO-BCOS/bcos-c-sdk) interface, which connecting blockchain nodes.

## Documentation
* English User Manual: in progress...
* [Chinese User Manual](https://fisco-bcos-doc.readthedocs.io/zh_CN/latest/docs/develop/sdk/index.html)

## Quick Start
* [Chinese](https://fisco-bcos-doc.readthedocs.io/zh_CN/latest/docs/develop/sdk/java_sdk/quick_start.html)

## Join Our Community

The FISCO BCOS community is one of the most active open-source blockchain communities in China. It provides long-term technical support for both institutional and individual developers and users of FISCO BCOS. Thousands of technical enthusiasts from numerous industry sectors have joined this community, studying and using FISCO BCOS platform. If you are also interested, you are most welcome to join us for more support and fun.

## License
![license](http://img.shields.io/badge/license-Apache%20v2-blue.svg)

All contributions are made under the [Apache License 2.0](http://www.apache.org/licenses/). See [LICENSE](LICENSE).