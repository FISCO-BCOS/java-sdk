![](docs/images/FISCO_BCOS_Logo.svg)

English / [中文](docs/README_CN.md)

# Java SDK

[![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg?style=flat-square)](http://makeapullrequest.com)
[![Build Status](https://travis-ci.org/FISCO-BCOS/java-sdk.svg?branch=master)](https://travis-ci.org/FISCO-BCOS/java-sdk)
[![CodeFactor](https://www.codefactor.io/repository/github/fisco-bcos/java-sdk/badge)](https://www.codefactor.io/repository/github/fisco-bcos/java-sdk)
[![GitHub All Releases](https://img.shields.io/github/downloads/FISCO-BCOS/java-sdk/total.svg)](https://github.com/FISCO-BCOS/java-sdk)

This is the FISCO BCOS Client SDK for Java. Developers can use Java SDK to build blockchain applications with FISCO BCOS blockchain.

## Functions
* Contract compiling.
* Interacting with FISCO BCOS JSON-RPC interface.
* constructing and sending transactions.
* Advanced Messages Onchain Protocol(AMOP) functions.
* Contract event subscription.
* Encoding and decoding data with ABI. 
* Account Management.

## New Features
This java sdk is a code refactoring version base on web3sdk (not recommend to use), it includes the following new features:

* Support Toml config file, simplify configuration options.
* Support connecting with nodes of different groups.
* Support AMOP subscription and unsubscription at any time instead of just before start service.
* ABI module add support of encode and decode of struct type data.
* Use the common crypto tools of WeBank.
* Add a new module called group management to help applications manage nodes which java SDK connecting with.
* Use modular design, each module can use independently. For example, you can create crypto.jar file if only crypto module is needed.

## Documentation
* [English User Handbook](https://fisco-bcos-documentation.readthedocs.io/en/latest/docs/sdk/java_sdk/index.html)
* [Chinese User Handbook](https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/sdk/java_sdk/index.html#)
* [Chinese WIKI](https://github.com/FISCO-BCOS/java-sdk/wiki)

## Quick Start
* [English](https://fisco-bcos-documentation.readthedocs.io/en/latest/docs/sdk/java_sdk/quick_start.html)
* [Chinese](https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/sdk/java_sdk/quick_start.html)

## Join Our Community

The FISCO BCOS community is one of the most active open-source blockchain communities in China. It provides long-term technical support for both institutional and individual developers and users of FISCO BCOS. Thousands of technical enthusiasts from numerous industry sectors have joined this community, studying and using FISCO BCOS platform. If you are also interested, you are most welcome to join us for more support and fun.

![](https://media.githubusercontent.com/media/FISCO-BCOS/LargeFiles/master/images/QR_image_en.png)
 

## License
![license](http://img.shields.io/badge/license-Apache%20v2-blue.svg)

All contributions are made under the [Apache License 2.0](http://www.apache.org/licenses/). See [LICENSE](LICENSE).