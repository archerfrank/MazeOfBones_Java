import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.EventValues;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.Contract;

import java.io.File;
import java.util.Arrays;

public class Test {
    private static final Logger log = LoggerFactory.getLogger(Test.class);

    public static void main(String[] args) throws Exception {

//        Web3j web3j = Web3j.build(new HttpService(
//                "HTTP://127.0.0.1:7545"));  // FIXME: Enter your Infura token here;
        Web3j web3j = Web3j.build(new HttpService(
                "https://ropsten.infura.io/v3/17c943beed0447a1be5042589b84ca40"));  // FIXME: Enter your Infura token here;
        System.out.println("Connected to Ethereum client version: "
                + web3j.web3ClientVersion().send().getWeb3ClientVersion());
        File f = new File("E:\\Development\\blockchain");

        Credentials credentials =
                WalletUtils.loadCredentials(
                        "Yang!831127",
                        "E:\\Development\\blockchain\\testNet.json");
//        WalletUtils.generateBip39WalletFromMnemonic(
//                        "Yang!831127",
//                        "hamster journey mountain alien easy girl reward ancient step side blur orient",
//                        f);
        System.out.println(credentials.getAddress());
        System.out.println(credentials.getEcKeyPair().getPrivateKey().toString(16));
//        TransactionReceipt transferReceipt = Transfer.sendFunds(
//                web3j, credentials,
//                "0x63c6441Dc51d2a25bd8986f59Fb9fEB940e55458",  // you can put any address here
//                BigDecimal.ONE, Convert.Unit.WEI)  // 1 wei = 10^-18 Ether
//                .send();
//        System.out.println("Transaction complete, view it at https://rinkeby.etherscan.io/tx/"
//                + transferReceipt.getTransactionHash());

//        Event event = new Event("Notify", Arrays.asList(
//                new TypeReference<Utf8String>(true) {
//
//                }));
        final Event event = new Event("AirdropEvent",
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {
                                                }, new TypeReference<Address>(true) {
                                                },
                        new TypeReference<Uint256>() {
                        }, new TypeReference<Utf8String>() {
                        }, new TypeReference<Utf8String>() {
                        }));

        EthFilter filter = new EthFilter(
                DefaultBlockParameterName.EARLIEST,
//                DefaultBlockParameter.valueOf(BigInteger.valueOf(0xc6f241).add(BigInteger.ONE)),
                DefaultBlockParameterName.LATEST,
                "0x46BeD11D5351dc08b7efB44Fdf8Fa85A7aD4b792");
        filter.addSingleTopic(EventEncoder.encode(event));
        web3j.ethLogFlowable(filter).subscribe((x) -> {
            System.out.println(x);
            System.out.println(x.getData());
            System.out.println(x.getTopics());
            EventValues eventValues = Contract.staticExtractEventParameters(event, x);
            System.out.println(eventValues.getIndexedValues());
            System.out.println(eventValues.getNonIndexedValues());
            System.out.println(eventValues.getIndexedValues().get(0).getValue());
            System.out.println(eventValues.getNonIndexedValues().get(0).getValue());
            eventValues.getIndexedValues().forEach(y -> {
                System.out.println(y.getValue());
            });
            eventValues.getNonIndexedValues().forEach(y -> {
                System.out.println(y.getValue());
            });
        });

    }
}
