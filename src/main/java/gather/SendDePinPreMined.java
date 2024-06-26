package gather;

import gather.tools.RemoteBroadcast;
import org.nachain.core.chain.transaction.*;
import org.nachain.core.chain.transaction.context.TxContext;
import org.nachain.core.config.KeyStoreHolder;
import org.nachain.core.crypto.Key;
import org.nachain.core.dapp.internal.depin.device.DeviceProjectService;
import org.nachain.core.dapp.internal.depin.device.events.PreMinedDTO;
import org.nachain.core.intermediate.tx.AccountTxHeightService;
import org.nachain.core.mailbox.Mail;
import org.nachain.core.mailbox.MailService;
import org.nachain.core.mailbox.MailType;
import org.nachain.core.token.CoreTokenEnum;
import org.nachain.core.util.JsonUtils;

import java.math.BigInteger;

public class SendDePinPreMined {

    static String owner = DePinMock.owner;
    static Key sendKey = KeyStoreHolder.getKey(owner);

    // Gather instanceId
    static long instanceId = 20;

    // NAC tokenId
    static long tokenId = CoreTokenEnum.NAC.id;

    static {

    }


    public static void run() throws Exception {
        PreMinedDTO preMinedDTO = new PreMinedDTO();
        preMinedDTO.setProjectName("Gather");
        preMinedDTO.setInstanceId(instanceId);

        TxContext<PreMinedDTO> txContext = DeviceProjectService.newPreMinedContext(preMinedDTO);

        String sendWallet = owner;
        BigInteger sendValue = BigInteger.ZERO;

        BigInteger gas = RemoteBroadcast.getDPoSGas();

        long txHeight = AccountTxHeightService.nextTxHeight(sendWallet, instanceId, tokenId);

        Tx sendTx = TxService.newTx(TxType.TRANSFER,
                instanceId, tokenId, sendWallet, TxReservedWord.INSTANCE.name, sendValue,
                gas, TxGasType.NAC.value, txHeight, txContext,
                "", 0, sendKey);

        Mail newMail = MailService.newMail(instanceId, MailType.MSG_SEND_TX, sendTx.toJson());

        System.out.println("json:" + JsonUtils.formatJson(newMail));

        boolean flag = RemoteBroadcast.broadcastMail(instanceId, newMail.toJson());
        System.out.println("flag = " + flag + " , hash = " + newMail.getHash());
    }

    public static void main(String[] args) throws Exception {
        SendDePinPreMined.run();
    }


}
