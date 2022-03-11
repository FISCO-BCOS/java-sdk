package org.fisco.bcos.sdk.v3.contract.auth.contracts;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.codec.abi.FunctionEncoder;
import org.fisco.bcos.sdk.v3.codec.datatypes.Address;
import org.fisco.bcos.sdk.v3.codec.datatypes.Bool;
import org.fisco.bcos.sdk.v3.codec.datatypes.Function;
import org.fisco.bcos.sdk.v3.codec.datatypes.Type;
import org.fisco.bcos.sdk.v3.codec.datatypes.TypeReference;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Bytes4;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint8;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple1;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple2;
import org.fisco.bcos.sdk.v3.contract.Contract;
import org.fisco.bcos.sdk.v3.crypto.CryptoSuite;
import org.fisco.bcos.sdk.v3.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.v3.model.CryptoType;
import org.fisco.bcos.sdk.v3.model.TransactionReceipt;
import org.fisco.bcos.sdk.v3.model.callback.TransactionCallback;
import org.fisco.bcos.sdk.v3.transaction.model.exception.ContractException;
import org.fisco.bcos.sdk.v3.utils.StringUtils;

@SuppressWarnings("unchecked")
public class MethodAuthManager extends Contract {
    public static final String[] BINARY_ARRAY = {
        "608060405234801561001057600080fd5b5060405162001586380380620015868339818101604052606081101561003557600080fd5b81019080805190602001909291908051906020019092919080519060200190929190505050336000806101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff16021790555082600260006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff16021790555081600160006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff16021790555061012b8161013360201b60201c565b50505061029e565b610142336101f760201b60201c565b6101b4576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040180806020018281038252600b8152602001807f4f6e6c79206f776e65722100000000000000000000000000000000000000000081525060200191505060405180910390fd5b806000806101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff16021790555050565b60003073ffffffffffffffffffffffffffffffffffffffff168273ffffffffffffffffffffffffffffffffffffffff1614156102365760019050610299565b6000809054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168273ffffffffffffffffffffffffffffffffffffffff1614156102945760019050610299565b600090505b919050565b6112d880620002ae6000396000f3fe608060405234801561001057600080fd5b506004361061009e5760003560e01c806365143c051161006657806365143c05146102555780639612028e14610299578063a3d21d441461031e578063b2bdfa7b14610378578063cd5d2118146103c25761009e565b806301bc45c9146100a35780631095d170146100ed57806313af40351461015a578063379887671461019e57806359401903146101e8575b600080fd5b6100ab61041e565b604051808273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200191505060405180910390f35b6101586004803603604081101561010357600080fd5b8101908080357bffffffffffffffffffffffffffffffffffffffffffffffffffffffff19169060200190929190803573ffffffffffffffffffffffffffffffffffffffff169060200190929190505050610444565b005b61019c6004803603602081101561017057600080fd5b81019080803573ffffffffffffffffffffffffffffffffffffffff1690602001909291905050506107ef565b005b6101a66108ad565b604051808273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200191505060405180910390f35b610253600480360360408110156101fe57600080fd5b8101908080357bffffffffffffffffffffffffffffffffffffffffffffffffffffffff19169060200190929190803573ffffffffffffffffffffffffffffffffffffffff1690602001909291905050506108d3565b005b6102976004803603602081101561026b57600080fd5b81019080803573ffffffffffffffffffffffffffffffffffffffff169060200190929190505050610c7e565b005b610304600480360360408110156102af57600080fd5b8101908080357bffffffffffffffffffffffffffffffffffffffffffffffffffffffff19169060200190929190803573ffffffffffffffffffffffffffffffffffffffff169060200190929190505050610d3d565b604051808215151515815260200191505060405180910390f35b6103766004803603604081101561033457600080fd5b8101908080357bffffffffffffffffffffffffffffffffffffffffffffffffffffffff19169060200190929190803560ff169060200190929190505050610feb565b005b6103806111a3565b604051808273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200191505060405180910390f35b610404600480360360208110156103d857600080fd5b81019080803573ffffffffffffffffffffffffffffffffffffffff1690602001909291905050506111c8565b604051808215151515815260200191505060405180910390f35b600160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1681565b3373ffffffffffffffffffffffffffffffffffffffff16600160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1614610507576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004018080602001828103825260118152602001807f796f75206d7573742062652061646d696e00000000000000000000000000000081525060200191505060405180910390fd5b600060036000847bffffffffffffffffffffffffffffffffffffffffffffffffffffffff19167bffffffffffffffffffffffffffffffffffffffffffffffffffffffff1916815260200190815260200160002060009054906101000a900460ff1660ff1614156105c2576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004018080602001828103825260338152602001806112706033913960400191505060405180910390fd5b600160036000847bffffffffffffffffffffffffffffffffffffffffffffffffffffffff19167bffffffffffffffffffffffffffffffffffffffffffffffffffffffff1916815260200190815260200160002060009054906101000a900460ff1660ff1614156106d857600060046000847bffffffffffffffffffffffffffffffffffffffffffffffffffffffff19167bffffffffffffffffffffffffffffffffffffffffffffffffffffffff1916815260200190815260200160002060008373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060006101000a81548160ff0219169083151502179055506107eb565b600260036000847bffffffffffffffffffffffffffffffffffffffffffffffffffffffff19167bffffffffffffffffffffffffffffffffffffffffffffffffffffffff1916815260200190815260200160002060009054906101000a900460ff1660ff1614156107ea57600160056000847bffffffffffffffffffffffffffffffffffffffffffffffffffffffff19167bffffffffffffffffffffffffffffffffffffffffffffffffffffffff1916815260200190815260200160002060008373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060006101000a81548160ff0219169083151502179055505b5b5050565b6107f8336111c8565b61086a576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040180806020018281038252600b8152602001807f4f6e6c79206f776e65722100000000000000000000000000000000000000000081525060200191505060405180910390fd5b806000806101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff16021790555050565b600260009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1681565b3373ffffffffffffffffffffffffffffffffffffffff16600160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1614610996576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004018080602001828103825260118152602001807f796f75206d7573742062652061646d696e00000000000000000000000000000081525060200191505060405180910390fd5b600060036000847bffffffffffffffffffffffffffffffffffffffffffffffffffffffff19167bffffffffffffffffffffffffffffffffffffffffffffffffffffffff1916815260200190815260200160002060009054906101000a900460ff1660ff161415610a51576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004018080602001828103825260338152602001806112706033913960400191505060405180910390fd5b600160036000847bffffffffffffffffffffffffffffffffffffffffffffffffffffffff19167bffffffffffffffffffffffffffffffffffffffffffffffffffffffff1916815260200190815260200160002060009054906101000a900460ff1660ff161415610b6757600160046000847bffffffffffffffffffffffffffffffffffffffffffffffffffffffff19167bffffffffffffffffffffffffffffffffffffffffffffffffffffffff1916815260200190815260200160002060008373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060006101000a81548160ff021916908315150217905550610c7a565b600260036000847bffffffffffffffffffffffffffffffffffffffffffffffffffffffff19167bffffffffffffffffffffffffffffffffffffffffffffffffffffffff1916815260200190815260200160002060009054906101000a900460ff1660ff161415610c7957600060056000847bffffffffffffffffffffffffffffffffffffffffffffffffffffffff19167bffffffffffffffffffffffffffffffffffffffffffffffffffffffff1916815260200190815260200160002060008373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060006101000a81548160ff0219169083151502179055505b5b5050565b610c87336111c8565b610cf9576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040180806020018281038252600b8152602001807f4f6e6c79206f776e65722100000000000000000000000000000000000000000081525060200191505060405180910390fd5b80600160006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff16021790555050565b60008060036000857bffffffffffffffffffffff",
        "ffffffffffffffffffffffffffffffffff19167bffffffffffffffffffffffffffffffffffffffffffffffffffffffff1916815260200190815260200160002060009054906101000a900460ff1660ff161415610db15760019050610fe5565b600160036000857bffffffffffffffffffffffffffffffffffffffffffffffffffffffff19167bffffffffffffffffffffffffffffffffffffffffffffffffffffffff1916815260200190815260200160002060009054906101000a900460ff1660ff16148015610eba575060046000847bffffffffffffffffffffffffffffffffffffffffffffffffffffffff19167bffffffffffffffffffffffffffffffffffffffffffffffffffffffff1916815260200190815260200160002060008373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060009054906101000a900460ff165b15610ec85760019050610fe5565b600260036000857bffffffffffffffffffffffffffffffffffffffffffffffffffffffff19167bffffffffffffffffffffffffffffffffffffffffffffffffffffffff1916815260200190815260200160002060009054906101000a900460ff1660ff16148015610fd2575060056000847bffffffffffffffffffffffffffffffffffffffffffffffffffffffff19167bffffffffffffffffffffffffffffffffffffffffffffffffffffffff1916815260200190815260200160002060008373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060009054906101000a900460ff16155b15610fe05760019050610fe5565b600090505b92915050565b3373ffffffffffffffffffffffffffffffffffffffff16600160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16146110ae576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004018080602001828103825260118152602001807f796f75206d7573742062652061646d696e00000000000000000000000000000081525060200191505060405180910390fd5b60018160ff1614806110c3575060028160ff16145b611135576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004018080602001828103825260198152602001807f617574682074797065206d7573742062652031206f7220322e0000000000000081525060200191505060405180910390fd5b8060036000847bffffffffffffffffffffffffffffffffffffffffffffffffffffffff19167bffffffffffffffffffffffffffffffffffffffffffffffffffffffff1916815260200190815260200160002060006101000a81548160ff021916908360ff1602179055505050565b6000809054906101000a900473ffffffffffffffffffffffffffffffffffffffff1681565b60003073ffffffffffffffffffffffffffffffffffffffff168273ffffffffffffffffffffffffffffffffffffffff161415611207576001905061126a565b6000809054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168273ffffffffffffffffffffffffffffffffffffffff161415611265576001905061126a565b600090505b91905056fe796f752073686f756c642073657420746865206d6574686f6420616363657373206175746820747970652066697273746c792ea264697066735822122043b93461257fd278c191001b35424664ea737c72f90f2db7460f1ceb286decac64736f6c634300060a0033"
    };

    public static final String BINARY = StringUtils.joinAll("", BINARY_ARRAY);

    public static final String[] SM_BINARY_ARRAY = {
        "608060405234801561001057600080fd5b5060405162001586380380620015868339818101604052606081101561003557600080fd5b81019080805190602001909291908051906020019092919080519060200190929190505050336000806101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff16021790555082600260006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff16021790555081600160006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff16021790555061012b8161013360201b60201c565b50505061029e565b610142336101f760201b60201c565b6101b4576040517fc703cb1200000000000000000000000000000000000000000000000000000000815260040180806020018281038252600b8152602001807f4f6e6c79206f776e65722100000000000000000000000000000000000000000081525060200191505060405180910390fd5b806000806101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff16021790555050565b60003073ffffffffffffffffffffffffffffffffffffffff168273ffffffffffffffffffffffffffffffffffffffff1614156102365760019050610299565b6000809054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168273ffffffffffffffffffffffffffffffffffffffff1614156102945760019050610299565b600090505b919050565b6112d880620002ae6000396000f3fe608060405234801561001057600080fd5b506004361061009e5760003560e01c80639008622711610066578063900862271461022b5780639b68af0a14610298578063b2e6ea8a146102e2578063e282c2cf1461032c578063fd57b049146103b15761009e565b806305282c70146100a357806328e91489146100e75780633695cf8814610131578063604d83dc146101755780636e0376d4146101cf575b600080fd5b6100e5600480360360208110156100b957600080fd5b81019080803573ffffffffffffffffffffffffffffffffffffffff16906020019092919050505061041e565b005b6100ef6104dc565b604051808273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200191505060405180910390f35b6101736004803603602081101561014757600080fd5b81019080803573ffffffffffffffffffffffffffffffffffffffff169060200190929190505050610501565b005b6101cd6004803603604081101561018b57600080fd5b8101908080357bffffffffffffffffffffffffffffffffffffffffffffffffffffffff19169060200190929190803560ff1690602001909291905050506105c0565b005b610211600480360360208110156101e557600080fd5b81019080803573ffffffffffffffffffffffffffffffffffffffff169060200190929190505050610778565b604051808215151515815260200191505060405180910390f35b6102966004803603604081101561024157600080fd5b8101908080357bffffffffffffffffffffffffffffffffffffffffffffffffffffffff19169060200190929190803573ffffffffffffffffffffffffffffffffffffffff16906020019092919050505061081f565b005b6102a0610bca565b604051808273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200191505060405180910390f35b6102ea610bf0565b604051808273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200191505060405180910390f35b6103976004803603604081101561034257600080fd5b8101908080357bffffffffffffffffffffffffffffffffffffffffffffffffffffffff19169060200190929190803573ffffffffffffffffffffffffffffffffffffffff169060200190929190505050610c16565b604051808215151515815260200191505060405180910390f35b61041c600480360360408110156103c757600080fd5b8101908080357bffffffffffffffffffffffffffffffffffffffffffffffffffffffff19169060200190929190803573ffffffffffffffffffffffffffffffffffffffff169060200190929190505050610ec4565b005b61042733610778565b610499576040517fc703cb1200000000000000000000000000000000000000000000000000000000815260040180806020018281038252600b8152602001807f4f6e6c79206f776e65722100000000000000000000000000000000000000000081525060200191505060405180910390fd5b806000806101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff16021790555050565b6000809054906101000a900473ffffffffffffffffffffffffffffffffffffffff1681565b61050a33610778565b61057c576040517fc703cb1200000000000000000000000000000000000000000000000000000000815260040180806020018281038252600b8152602001807f4f6e6c79206f776e65722100000000000000000000000000000000000000000081525060200191505060405180910390fd5b80600160006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff16021790555050565b3373ffffffffffffffffffffffffffffffffffffffff16600160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1614610683576040517fc703cb120000000000000000000000000000000000000000000000000000000081526004018080602001828103825260118152602001807f796f75206d7573742062652061646d696e00000000000000000000000000000081525060200191505060405180910390fd5b60018160ff161480610698575060028160ff16145b61070a576040517fc703cb120000000000000000000000000000000000000000000000000000000081526004018080602001828103825260198152602001807f617574682074797065206d7573742062652031206f7220322e0000000000000081525060200191505060405180910390fd5b8060036000847bffffffffffffffffffffffffffffffffffffffffffffffffffffffff19167bffffffffffffffffffffffffffffffffffffffffffffffffffffffff1916815260200190815260200160002060006101000a81548160ff021916908360ff1602179055505050565b60003073ffffffffffffffffffffffffffffffffffffffff168273ffffffffffffffffffffffffffffffffffffffff1614156107b7576001905061081a565b6000809054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168273ffffffffffffffffffffffffffffffffffffffff161415610815576001905061081a565b600090505b919050565b3373ffffffffffffffffffffffffffffffffffffffff16600160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16146108e2576040517fc703cb120000000000000000000000000000000000000000000000000000000081526004018080602001828103825260118152602001807f796f75206d7573742062652061646d696e00000000000000000000000000000081525060200191505060405180910390fd5b600060036000847bffffffffffffffffffffffffffffffffffffffffffffffffffffffff19167bffffffffffffffffffffffffffffffffffffffffffffffffffffffff1916815260200190815260200160002060009054906101000a900460ff1660ff16141561099d576040517fc703cb120000000000000000000000000000000000000000000000000000000081526004018080602001828103825260338152602001806112706033913960400191505060405180910390fd5b600160036000847bffffffffffffffffffffffffffffffffffffffffffffffffffffffff19167bffffffffffffffffffffffffffffffffffffffffffffffffffffffff1916815260200190815260200160002060009054906101000a900460ff1660ff161415610ab357600160046000847bffffffffffffffffffffffffffffffffffffffffffffffffffffffff19167bffffffffffffffffffffffffffffffffffffffffffffffffffffffff1916815260200190815260200160002060008373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060006101000a81548160ff021916908315150217905550610bc6565b600260036000847bffffffffffffffffffffffffffffffffffffffffffffffffffffffff19167bffffffffffffffffffffffffffffffffffffffffffffffffffffffff1916815260200190815260200160002060009054906101000a900460ff1660ff161415610bc557600060056000847bffffffffffffffffffffffffffffffffffffffffffffffffffffffff19167bffffffffffffffffffffffffffffffffffffffffffffffffffffffff1916815260200190815260200160002060008373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060006101000a81548160ff0219169083151502179055505b5b5050565b600160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1681565b600260009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1681565b60008060036000857bffffffffffffffffffffffffffffffffffffffffffffffffffffffff19167bffffffffffffffffffffffffffffffffffffffffffffffffffffffff1916815260200190815260200160002060009054906101000a900460ff1660ff161415610c8a5760019050610ebe565b600160036000857bffffffffffffffffffffffffffffffffffffffffffffffffffffffff19167bffffffffffffffffffffffffffffffffffffffffffffffffffffffff1916815260200190815260200160002060009054906101000a900460ff1660ff16148015610d93575060046000847bffffffffffffffffffffffffffffffffffffffffffffffffffffffff19167bffffffffffffffffffffffffffffffffffffffffffffffffffffffff1916815260200190815260200160002060008373ffffffffffff",
        "ffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060009054906101000a900460ff165b15610da15760019050610ebe565b600260036000857bffffffffffffffffffffffffffffffffffffffffffffffffffffffff19167bffffffffffffffffffffffffffffffffffffffffffffffffffffffff1916815260200190815260200160002060009054906101000a900460ff1660ff16148015610eab575060056000847bffffffffffffffffffffffffffffffffffffffffffffffffffffffff19167bffffffffffffffffffffffffffffffffffffffffffffffffffffffff1916815260200190815260200160002060008373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060009054906101000a900460ff16155b15610eb95760019050610ebe565b600090505b92915050565b3373ffffffffffffffffffffffffffffffffffffffff16600160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1614610f87576040517fc703cb120000000000000000000000000000000000000000000000000000000081526004018080602001828103825260118152602001807f796f75206d7573742062652061646d696e00000000000000000000000000000081525060200191505060405180910390fd5b600060036000847bffffffffffffffffffffffffffffffffffffffffffffffffffffffff19167bffffffffffffffffffffffffffffffffffffffffffffffffffffffff1916815260200190815260200160002060009054906101000a900460ff1660ff161415611042576040517fc703cb120000000000000000000000000000000000000000000000000000000081526004018080602001828103825260338152602001806112706033913960400191505060405180910390fd5b600160036000847bffffffffffffffffffffffffffffffffffffffffffffffffffffffff19167bffffffffffffffffffffffffffffffffffffffffffffffffffffffff1916815260200190815260200160002060009054906101000a900460ff1660ff16141561115857600060046000847bffffffffffffffffffffffffffffffffffffffffffffffffffffffff19167bffffffffffffffffffffffffffffffffffffffffffffffffffffffff1916815260200190815260200160002060008373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060006101000a81548160ff02191690831515021790555061126b565b600260036000847bffffffffffffffffffffffffffffffffffffffffffffffffffffffff19167bffffffffffffffffffffffffffffffffffffffffffffffffffffffff1916815260200190815260200160002060009054906101000a900460ff1660ff16141561126a57600160056000847bffffffffffffffffffffffffffffffffffffffffffffffffffffffff19167bffffffffffffffffffffffffffffffffffffffffffffffffffffffff1916815260200190815260200160002060008373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060006101000a81548160ff0219169083151502179055505b5b505056fe796f752073686f756c642073657420746865206d6574686f6420616363657373206175746820747970652066697273746c792ea26469706673582212200cda2f8a43e9fe7269dbc4720d091c16ef19679e9b99272b55a2e5643805288764736f6c634300060a0033"
    };

    public static final String SM_BINARY = StringUtils.joinAll("", SM_BINARY_ARRAY);

    public static final String[] ABI_ARRAY = {
        "[{\"inputs\":[{\"internalType\":\"address\",\"name\":\"contractAddress\",\"type\":\"address\"},{\"internalType\":\"address\",\"name\":\"admin\",\"type\":\"address\"},{\"internalType\":\"address\",\"name\":\"owner\",\"type\":\"address\"}],\"stateMutability\":\"nonpayable\",\"type\":\"constructor\"},{\"inputs\":[],\"name\":\"_admin\",\"outputs\":[{\"internalType\":\"address\",\"name\":\"\",\"type\":\"address\"}],\"stateMutability\":\"view\",\"type\":\"function\"},{\"inputs\":[],\"name\":\"_contractAddress\",\"outputs\":[{\"internalType\":\"address\",\"name\":\"\",\"type\":\"address\"}],\"stateMutability\":\"view\",\"type\":\"function\"},{\"inputs\":[],\"name\":\"_owner\",\"outputs\":[{\"internalType\":\"address\",\"name\":\"\",\"type\":\"address\"}],\"stateMutability\":\"view\",\"type\":\"function\"},{\"inputs\":[{\"internalType\":\"address\",\"name\":\"src\",\"type\":\"address\"}],\"name\":\"auth\",\"outputs\":[{\"internalType\":\"bool\",\"name\":\"\",\"type\":\"bool\"}],\"stateMutability\":\"view\",\"type\":\"function\"},{\"inputs\":[{\"internalType\":\"bytes4\",\"name\":\"methodId\",\"type\":\"bytes4\"},{\"internalType\":\"address\",\"name\":\"account\",\"type\":\"address\"}],\"name\":\"closeMehtodAccessAuth\",\"outputs\":[],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"inputs\":[{\"internalType\":\"bytes4\",\"name\":\"methodId\",\"type\":\"bytes4\"},{\"internalType\":\"address\",\"name\":\"account\",\"type\":\"address\"}],\"name\":\"hasMethodAccessAuth\",\"outputs\":[{\"internalType\":\"bool\",\"name\":\"\",\"type\":\"bool\"}],\"stateMutability\":\"view\",\"type\":\"function\"},{\"inputs\":[{\"internalType\":\"bytes4\",\"name\":\"methodId\",\"type\":\"bytes4\"},{\"internalType\":\"address\",\"name\":\"account\",\"type\":\"address\"}],\"name\":\"openMethodAccessAuth\",\"outputs\":[],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"inputs\":[{\"internalType\":\"address\",\"name\":\"admin\",\"type\":\"address\"}],\"name\":\"resetAdmin\",\"outputs\":[],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"inputs\":[{\"internalType\":\"bytes4\",\"name\":\"methodId\",\"type\":\"bytes4\"},{\"internalType\":\"uint8\",\"name\":\"authType\",\"type\":\"uint8\"}],\"name\":\"setMethodAccessAuthType\",\"outputs\":[],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"inputs\":[{\"internalType\":\"address\",\"name\":\"owner\",\"type\":\"address\"}],\"name\":\"setOwner\",\"outputs\":[],\"stateMutability\":\"nonpayable\",\"type\":\"function\"}]"
    };

    public static final String ABI = StringUtils.joinAll("", ABI_ARRAY);

    public static final String FUNC__ADMIN = "_admin";

    public static final String FUNC__CONTRACTADDRESS = "_contractAddress";

    public static final String FUNC__OWNER = "_owner";

    public static final String FUNC_AUTH = "auth";

    public static final String FUNC_CLOSEMEHTODACCESSAUTH = "closeMehtodAccessAuth";

    public static final String FUNC_HASMETHODACCESSAUTH = "hasMethodAccessAuth";

    public static final String FUNC_OPENMETHODACCESSAUTH = "openMethodAccessAuth";

    public static final String FUNC_RESETADMIN = "resetAdmin";

    public static final String FUNC_SETMETHODACCESSAUTHTYPE = "setMethodAccessAuthType";

    public static final String FUNC_SETOWNER = "setOwner";

    protected MethodAuthManager(String contractAddress, Client client, CryptoKeyPair credential) {
        super(getBinary(client.getCryptoSuite()), contractAddress, client, credential);
    }

    public static String getBinary(CryptoSuite cryptoSuite) {
        return (cryptoSuite.getCryptoTypeConfig() == CryptoType.ECDSA_TYPE ? BINARY : SM_BINARY);
    }

    public static String getABI() {
        return ABI;
    }

    public String _admin() throws ContractException {
        final Function function =
                new Function(
                        FUNC__ADMIN,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeCallWithSingleValueReturn(function, String.class);
    }

    public String _contractAddress() throws ContractException {
        final Function function =
                new Function(
                        FUNC__CONTRACTADDRESS,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeCallWithSingleValueReturn(function, String.class);
    }

    public String _owner() throws ContractException {
        final Function function =
                new Function(
                        FUNC__OWNER,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeCallWithSingleValueReturn(function, String.class);
    }

    public Boolean auth(String src) throws ContractException {
        final Function function =
                new Function(
                        FUNC_AUTH,
                        Arrays.<Type>asList(new Address(src)),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeCallWithSingleValueReturn(function, Boolean.class);
    }

    public TransactionReceipt closeMehtodAccessAuth(byte[] methodId, String account) {
        final Function function =
                new Function(
                        FUNC_CLOSEMEHTODACCESSAUTH,
                        Arrays.<Type>asList(new Bytes4(methodId), new Address(account)),
                        Collections.<TypeReference<?>>emptyList());
        return executeTransaction(function);
    }

    public void closeMehtodAccessAuth(
            byte[] methodId, String account, TransactionCallback callback) {
        final Function function =
                new Function(
                        FUNC_CLOSEMEHTODACCESSAUTH,
                        Arrays.<Type>asList(new Bytes4(methodId), new Address(account)),
                        Collections.<TypeReference<?>>emptyList());
        asyncExecuteTransaction(function, callback);
    }

    public String getSignedTransactionForCloseMehtodAccessAuth(byte[] methodId, String account) {
        final Function function =
                new Function(
                        FUNC_CLOSEMEHTODACCESSAUTH,
                        Arrays.<Type>asList(new Bytes4(methodId), new Address(account)),
                        Collections.<TypeReference<?>>emptyList());
        return createSignedTransaction(function);
    }

    public Tuple2<byte[], String> getCloseMehtodAccessAuthInput(
            TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function =
                new Function(
                        FUNC_CLOSEMEHTODACCESSAUTH,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(
                                new TypeReference<Bytes4>() {}, new TypeReference<Address>() {}));
        List<Type> results =
                this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple2<byte[], String>(
                (byte[]) results.get(0).getValue(), (String) results.get(1).getValue());
    }

    public Boolean hasMethodAccessAuth(byte[] methodId, String account) throws ContractException {
        final Function function =
                new Function(
                        FUNC_HASMETHODACCESSAUTH,
                        Arrays.<Type>asList(new Bytes4(methodId), new Address(account)),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeCallWithSingleValueReturn(function, Boolean.class);
    }

    public TransactionReceipt openMethodAccessAuth(byte[] methodId, String account) {
        final Function function =
                new Function(
                        FUNC_OPENMETHODACCESSAUTH,
                        Arrays.<Type>asList(new Bytes4(methodId), new Address(account)),
                        Collections.<TypeReference<?>>emptyList());
        return executeTransaction(function);
    }

    public void openMethodAccessAuth(
            byte[] methodId, String account, TransactionCallback callback) {
        final Function function =
                new Function(
                        FUNC_OPENMETHODACCESSAUTH,
                        Arrays.<Type>asList(new Bytes4(methodId), new Address(account)),
                        Collections.<TypeReference<?>>emptyList());
        asyncExecuteTransaction(function, callback);
    }

    public String getSignedTransactionForOpenMethodAccessAuth(byte[] methodId, String account) {
        final Function function =
                new Function(
                        FUNC_OPENMETHODACCESSAUTH,
                        Arrays.<Type>asList(new Bytes4(methodId), new Address(account)),
                        Collections.<TypeReference<?>>emptyList());
        return createSignedTransaction(function);
    }

    public Tuple2<byte[], String> getOpenMethodAccessAuthInput(
            TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function =
                new Function(
                        FUNC_OPENMETHODACCESSAUTH,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(
                                new TypeReference<Bytes4>() {}, new TypeReference<Address>() {}));
        List<Type> results =
                this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple2<byte[], String>(
                (byte[]) results.get(0).getValue(), (String) results.get(1).getValue());
    }

    public TransactionReceipt resetAdmin(String admin) {
        final Function function =
                new Function(
                        FUNC_RESETADMIN,
                        Arrays.<Type>asList(new Address(admin)),
                        Collections.<TypeReference<?>>emptyList());
        return executeTransaction(function);
    }

    public void resetAdmin(String admin, TransactionCallback callback) {
        final Function function =
                new Function(
                        FUNC_RESETADMIN,
                        Arrays.<Type>asList(new Address(admin)),
                        Collections.<TypeReference<?>>emptyList());
        asyncExecuteTransaction(function, callback);
    }

    public String getSignedTransactionForResetAdmin(String admin) {
        final Function function =
                new Function(
                        FUNC_RESETADMIN,
                        Arrays.<Type>asList(new Address(admin)),
                        Collections.<TypeReference<?>>emptyList());
        return createSignedTransaction(function);
    }

    public Tuple1<String> getResetAdminInput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function =
                new Function(
                        FUNC_RESETADMIN,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        List<Type> results =
                this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple1<String>((String) results.get(0).getValue());
    }

    public TransactionReceipt setMethodAccessAuthType(byte[] methodId, BigInteger authType) {
        final Function function =
                new Function(
                        FUNC_SETMETHODACCESSAUTHTYPE,
                        Arrays.<Type>asList(new Bytes4(methodId), new Uint8(authType)),
                        Collections.<TypeReference<?>>emptyList());
        return executeTransaction(function);
    }

    public void setMethodAccessAuthType(
            byte[] methodId, BigInteger authType, TransactionCallback callback) {
        final Function function =
                new Function(
                        FUNC_SETMETHODACCESSAUTHTYPE,
                        Arrays.<Type>asList(new Bytes4(methodId), new Uint8(authType)),
                        Collections.<TypeReference<?>>emptyList());
        asyncExecuteTransaction(function, callback);
    }

    public String getSignedTransactionForSetMethodAccessAuthType(
            byte[] methodId, BigInteger authType) {
        final Function function =
                new Function(
                        FUNC_SETMETHODACCESSAUTHTYPE,
                        Arrays.<Type>asList(new Bytes4(methodId), new Uint8(authType)),
                        Collections.<TypeReference<?>>emptyList());
        return createSignedTransaction(function);
    }

    public Tuple2<byte[], BigInteger> getSetMethodAccessAuthTypeInput(
            TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function =
                new Function(
                        FUNC_SETMETHODACCESSAUTHTYPE,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(
                                new TypeReference<Bytes4>() {}, new TypeReference<Uint8>() {}));
        List<Type> results =
                this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple2<byte[], BigInteger>(
                (byte[]) results.get(0).getValue(), (BigInteger) results.get(1).getValue());
    }

    public TransactionReceipt setOwner(String owner) {
        final Function function =
                new Function(
                        FUNC_SETOWNER,
                        Arrays.<Type>asList(new Address(owner)),
                        Collections.<TypeReference<?>>emptyList());
        return executeTransaction(function);
    }

    public void setOwner(String owner, TransactionCallback callback) {
        final Function function =
                new Function(
                        FUNC_SETOWNER,
                        Arrays.<Type>asList(new Address(owner)),
                        Collections.<TypeReference<?>>emptyList());
        asyncExecuteTransaction(function, callback);
    }

    public String getSignedTransactionForSetOwner(String owner) {
        final Function function =
                new Function(
                        FUNC_SETOWNER,
                        Arrays.<Type>asList(new Address(owner)),
                        Collections.<TypeReference<?>>emptyList());
        return createSignedTransaction(function);
    }

    public Tuple1<String> getSetOwnerInput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function =
                new Function(
                        FUNC_SETOWNER,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        List<Type> results =
                this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple1<String>((String) results.get(0).getValue());
    }

    public static MethodAuthManager load(
            String contractAddress, Client client, CryptoKeyPair credential) {
        return new MethodAuthManager(contractAddress, client, credential);
    }

    public static MethodAuthManager deploy(
            Client client,
            CryptoKeyPair credential,
            String contractAddress,
            String admin,
            String owner)
            throws ContractException {
        byte[] encodedConstructor =
                FunctionEncoder.encodeConstructor(
                        Arrays.<Type>asList(
                                new Address(contractAddress),
                                new Address(admin),
                                new Address(owner)));
        return deploy(
                MethodAuthManager.class,
                client,
                credential,
                getBinary(client.getCryptoSuite()),
                null,
                encodedConstructor,
                null);
    }
}
