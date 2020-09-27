### v2.6.1-rc1

(2020-09-30)

This java sdk is a code refactoring version base on web3sdk 2.6.1. It includes the following new features:

* Support Toml config file, simplify configuration options.
* Support connecting with nodes of different groups.
* Support AMOP subscription and unsubscription at any time instead of just before start service.
* ABI module add support of encode and decode of struct type data.
* Use the common crypto tools of WeBank.
* Add a new module called group management to help applications manage nodes which java SDK connecting with.
* Use modular design, each module can use independently. For example, you can create crypto.jar file if only crypto module is needed.

Please note that:
Java SDK supports FISCO BCOS 2.X, but not compatible with web3sdk. Mean, when tow user want to use AMOP functions to talk with each other, they should either both use java sdk, or both use web3sdk. 