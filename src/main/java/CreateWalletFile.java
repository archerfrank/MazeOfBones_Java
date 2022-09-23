import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.Transfer;
import org.web3j.utils.Convert;

import java.io.File;
import java.math.BigDecimal;

public class CreateWalletFile {
    private static final Logger log = LoggerFactory.getLogger(CreateWalletFile.class);
    public static void main(String[] args) throws Exception {

        Web3j web3j = Web3j.build(new HttpService(
                "HTTP://127.0.0.1:7545"));  // FIXME: Enter your Infura token here;
        System.out.println("Connected to Ethereum client version: "
                + web3j.web3ClientVersion().send().getWeb3ClientVersion());
        File f = new File("E:\\Development\\Blockchain");


        WalletUtils.generateBip39WalletFromMnemonic(
                        "Yang!831127",
                        "hamster journey mountain alien easy girl reward ancient step side blur orient",
                        f);


    }
}
