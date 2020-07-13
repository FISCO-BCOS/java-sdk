package org.fisco.bcos.sdk.client.response;

/**
 * Transaction object returned by:
 *
 * <ul>
 *   <li>eth_getTransactionByHash
 *   <li>eth_getTransactionByBlockHashAndIndex
 *   <li>eth_getTransactionByBlockNumberAndIndex
 * </ul>
 *
 * <p>This differs slightly from the request {@link SendTransaction} Transaction object.
 *
 * <p>See <a href="https://github.com/ethereum/wiki/wiki/JSON-RPC#eth_gettransactionbyhash">docs</a>
 * for further details.
 */
public class BcosTransaction {}
