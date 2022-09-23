import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.exceptions.TransactionException;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.Transfer;
import org.web3j.utils.Convert;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Base64;

public class Test {
    private static final Logger log = LoggerFactory.getLogger(Test.class);
    public static void main(String[] args) throws Exception {

        Web3j web3j = Web3j.build(new HttpService(
                "HTTP://127.0.0.1:7545"));  // FIXME: Enter your Infura token here;
        System.out.println("Connected to Ethereum client version: "
                + web3j.web3ClientVersion().send().getWeb3ClientVersion());
        File f = new File("E:\\Development\\Blockchain");

        Credentials credentials =
                WalletUtils.loadCredentials(
                        "Yang!831127",
                        "E:\\Development\\Blockchain\\testNet.json");
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

        Event event = new Event("Greet", Arrays.asList(
                new TypeReference<Utf8String>(true) {

                }));

        EthFilter filter = new EthFilter(
                DefaultBlockParameterName.EARLIEST,
                DefaultBlockParameterName.LATEST,
                "0x384Ca431aca4CbfAb257A0c86Ca8A045ec20D8d6");
        filter.addSingleTopic(EventEncoder.encode(event));
        web3j.ethLogFlowable(filter).subscribe((x) -> {System.out.println(x);
            System.out.println(Base64.getDecoder().decode(x.getData()));
            System.out.println(x.getTopics());
        });

    }
}
