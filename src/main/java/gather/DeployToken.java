package gather;

import gather.tools.RemoteBroadcast;
import org.nachain.core.base.Amount;
import org.nachain.core.base.Unit;
import org.nachain.core.chain.structure.instance.CoreInstanceEnum;
import org.nachain.core.chain.transaction.TxReservedWord;
import org.nachain.core.crypto.Key;
import org.nachain.core.intermediate.tx.AccountTxHeightService;
import org.nachain.core.mailbox.Mail;
import org.nachain.core.mailbox.MailService;
import org.nachain.core.token.CoreTokenEnum;
import org.nachain.core.token.Token;
import org.nachain.core.token.TokenService;
import org.nachain.core.wallet.walletskill.NirvanaWalletSkill;

import java.math.BigInteger;


public class DeployToken {

    public static void run() throws Exception {

        Key sendKey = Key.toKey0x("");
        sendKey.init(new NirvanaWalletSkill());
        String sendWallet = sendKey.toWalletAddress();
        String toWallet = TxReservedWord.INSTANCE.name;
        String initialAddress = sendWallet;

        long instanceId = CoreInstanceEnum.APPCHAIN.id;

        BigInteger gas = RemoteBroadcast.getGas(instanceId);

        BigInteger sendValue = Amount.of(100, Unit.NAC).toBigInteger();

        long txHeight = AccountTxHeightService.nextTxHeight(sendWallet, instanceId, CoreTokenEnum.NAC.id);

        Token token = TokenService.newNormalToken("Gather Token", "GAT", "Gather DePIN Token", Amount.of(100000000L, Unit.NAC), initialAddress);

        Mail newMail = MailService.newInstallTokenMail(token, sendWallet, sendValue, gas, txHeight, sendKey);

        boolean flag = RemoteBroadcast.broadcastMail(instanceId, newMail.toJson());
    }

    public static void main(String[] args) throws Exception {
        DeployToken.run();
    }
}
