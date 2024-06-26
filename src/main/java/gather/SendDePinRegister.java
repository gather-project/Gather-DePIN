package gather;

import gather.tools.RemoteBroadcast;
import org.nachain.core.base.Amount;
import org.nachain.core.base.Unit;
import org.nachain.core.chain.transaction.*;
import org.nachain.core.chain.transaction.context.TxContext;
import org.nachain.core.config.Constants;
import org.nachain.core.config.KeyStoreHolder;
import org.nachain.core.crypto.Key;
import org.nachain.core.dapp.internal.depin.device.DeviceCompany;
import org.nachain.core.dapp.internal.depin.device.DeviceMining;
import org.nachain.core.dapp.internal.depin.device.DeviceProject;
import org.nachain.core.dapp.internal.depin.device.DeviceProjectService;
import org.nachain.core.dapp.internal.depin.device.events.RegisterDTO;
import org.nachain.core.dapp.internal.depin.device.mining.FullNodeMetadata;
import org.nachain.core.dapp.internal.depin.device.mining.MiningMetadata;
import org.nachain.core.dapp.internal.depin.device.mining.TokenMetadata;
import org.nachain.core.dapp.internal.depin.device.mining.VotesMetadata;
import org.nachain.core.dapp.internal.depin.device.quantity.DestroyQuantity;
import org.nachain.core.dapp.internal.depin.device.quantity.MiningQuantity;
import org.nachain.core.dapp.internal.depin.device.quantity.VotesQuantity;
import org.nachain.core.dapp.internal.fullnode.FNApplyType;
import org.nachain.core.intermediate.tx.AccountTxHeightService;
import org.nachain.core.mailbox.Mail;
import org.nachain.core.mailbox.MailService;
import org.nachain.core.mailbox.MailType;

import java.math.BigDecimal;
import java.math.BigInteger;


public class SendDePinRegister {

    static String owner = "Nc4cPW4aMn1Ak1sdQLMyWDYtYLbgfBa1PJ";

    private static Key sendKey = KeyStoreHolder.getKey(owner);

    // Gather instanceId
    static long instanceId = 20;
    // GAT tokenId
    static long tokenId = 16;
    // GMT tokenId
    static long gmtTokenId = 17;

    static BigInteger totalTokenSupply;

    static {
        totalTokenSupply = Amount.toToken(100000000);
        System.out.println(totalTokenSupply);
    }


    private static DeviceProject createGatherProject() {
        String projectName = "Gather";
        String companyName = "Malaysia Gather";
        String companyAddress = "Malaysia Gather";
        String companyPhone = "";
        String projectOwner = owner;
        String deviceName = "GatherBox";
        String remarks = "DePIN infrastructure network, Top secret chat software.";
        long finishedMiningBlockHeight = 287136000;
        String preMineWalletAddress = owner;

        DeviceCompany deviceCompany = DeviceProjectService.newDeviceCompany(companyName, companyPhone, companyAddress,
                "https://www.gather.top", "https://x.com/GatherGlobal", "support@gather.top");

        DeviceMining deviceMining = DeviceProjectService.newDeviceMining(instanceId, tokenId);

        FullNodeMetadata fullNodeMetadata = deviceMining.getFullNodeMetadata();
        fullNodeMetadata.setApplyType(FNApplyType.TOKEN_NAC);
        fullNodeMetadata.setDestroyFullNodeTokenID(gmtTokenId);
        fullNodeMetadata.addDestroyTokenQuantity(new DestroyQuantity(1, Long.MAX_VALUE, Amount.toToken(1)));
        fullNodeMetadata.addDestroyNacQuantity(new DestroyQuantity(1, Long.MAX_VALUE, Amount.toBigInteger(1, Unit.NANO_NAC)));

        TokenMetadata tokenMetadata = deviceMining.getTokenMetadata();
        tokenMetadata.setTotalTokenSupply(totalTokenSupply);
        tokenMetadata.setDeviceMiningQuantity(Amount.toToken(10000 * 9000));
        tokenMetadata.setPreMineQuantity(Amount.toToken(100000000));
        tokenMetadata.setPreMineWalletAddress(preMineWalletAddress);

        MiningMetadata miningMetadata = deviceMining.getMiningMetadata();
        miningMetadata.setSuperNodeHashRateRatio(BigDecimal.valueOf(0.05));
        miningMetadata.setFullNodeHashRateRatio(BigDecimal.valueOf(0.95));
        miningMetadata.setDeviceHashRateRatio(BigDecimal.valueOf(0.30));
        miningMetadata.setVotingHashRateRatio(BigDecimal.valueOf(0.65));
        miningMetadata.setDailyMiningLimit(Amount.toToken(20000));
        miningMetadata.setReductionLimit(Amount.toToken(2500));
        miningMetadata.setHalvingIntervalYears(4);
        miningMetadata.setStartMiningBlockHeight(1);
        miningMetadata.setEndMiningBlockHeight(finishedMiningBlockHeight);
        long fourYearBlocks = Constants.DPoS_BLOCKS_PER_YEAR * miningMetadata.getHalvingIntervalYears();
        MiningQuantity mq1 = new MiningQuantity(0, fourYearBlocks, Amount.toToken(20000));
        MiningQuantity mq2 = new MiningQuantity(mq1.getEndBlockHeight() + 1, mq1.getEndBlockHeight() + fourYearBlocks, Amount.toToken(10000));
        MiningQuantity mq3 = new MiningQuantity(mq2.getEndBlockHeight() + 1, mq2.getEndBlockHeight() + fourYearBlocks, Amount.toToken(5000));
        MiningQuantity mq4 = new MiningQuantity(mq3.getEndBlockHeight() + 1, mq3.getEndBlockHeight() + fourYearBlocks, Amount.toToken(2500));
        MiningQuantity mq5 = new MiningQuantity(mq4.getEndBlockHeight() + 1, finishedMiningBlockHeight, Amount.toToken(2500));
        miningMetadata.addMiningQuantity(mq1).addMiningQuantity(mq2).addMiningQuantity(mq3).addMiningQuantity(mq4).addMiningQuantity(mq5);

        VotesMetadata votesMetadata = deviceMining.getVotesMetadata();
        votesMetadata.setMinVotesPerDevice(Amount.toToken(1));
        long monthBlocks = Constants.DPoS_BLOCKS_PER_DAY * 30;
        VotesQuantity vq1 = new VotesQuantity(0, monthBlocks, Amount.toToken(200));
        VotesQuantity vq2 = new VotesQuantity(vq1.getEndBlockHeight() + 1, vq1.getEndBlockHeight() + monthBlocks, Amount.toToken(400));
        VotesQuantity vq3 = new VotesQuantity(vq2.getEndBlockHeight() + 1, vq2.getEndBlockHeight() + monthBlocks, Amount.toToken(600));
        VotesQuantity vq4 = new VotesQuantity(vq3.getEndBlockHeight() + 1, vq3.getEndBlockHeight() + monthBlocks, Amount.toToken(800));
        VotesQuantity vq5 = new VotesQuantity(vq4.getEndBlockHeight() + 1, vq4.getEndBlockHeight() + monthBlocks, Amount.toToken(1000));
        VotesQuantity vq6 = new VotesQuantity(vq5.getEndBlockHeight() + 1, vq5.getEndBlockHeight() + monthBlocks, Amount.toToken(1200));
        VotesQuantity vq7 = new VotesQuantity(vq6.getEndBlockHeight() + 1, vq6.getEndBlockHeight() + monthBlocks, Amount.toToken(1400));
        VotesQuantity vq8 = new VotesQuantity(vq7.getEndBlockHeight() + 1, vq7.getEndBlockHeight() + monthBlocks, Amount.toToken(1600));
        VotesQuantity vq9 = new VotesQuantity(vq8.getEndBlockHeight() + 1, vq8.getEndBlockHeight() + monthBlocks, Amount.toToken(1800));
        VotesQuantity vq10 = new VotesQuantity(vq9.getEndBlockHeight() + 1, vq9.getEndBlockHeight() + monthBlocks, Amount.toToken(2000));
        VotesQuantity vq11 = new VotesQuantity(vq10.getEndBlockHeight() + 1, Long.MAX_VALUE, Amount.toToken(2000));
        votesMetadata.addVotesQuantity(vq1).addVotesQuantity(vq2).addVotesQuantity(vq3).addVotesQuantity(vq4).addVotesQuantity(vq5).addVotesQuantity(vq6).addVotesQuantity(vq7).addVotesQuantity(vq8).addVotesQuantity(vq9).addVotesQuantity(vq10).addVotesQuantity(vq11);

        DeviceProject deviceProject = DeviceProjectService.newDeviceProject(projectName, projectOwner, deviceName, deviceCompany, deviceMining, remarks);
        return deviceProject;
    }

    public static void run() throws Exception {
        DeviceProject deviceProject = createGatherProject();
        deviceProject.setFromTx("");
        deviceProject.setBlockHeight(0);

        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setDeviceProject(deviceProject);
        TxContext<RegisterDTO> txContext = DeviceProjectService.newRegisterContext(registerDTO);

        String sendWallet = owner;
        BigInteger sendValue = totalTokenSupply;

        BigInteger gas = RemoteBroadcast.getDPoSGas();

        long txHeight = AccountTxHeightService.nextTxHeight(sendWallet, instanceId, tokenId);

        Tx sendTx = TxService.newTx(TxType.TRANSFER,
                instanceId, tokenId, sendWallet, TxReservedWord.INSTANCE.name, sendValue,
                gas, TxGasType.NAC.value, txHeight, txContext,
                "", 0, sendKey);

        Mail newMail = MailService.newMail(instanceId, MailType.MSG_SEND_TX, sendTx.toJson());

        boolean flag = RemoteBroadcast.broadcastMail(instanceId, newMail.toJson());
        System.out.println("flag = " + flag + " , hash = " + newMail.getHash());
    }

    public static void main(String[] args) throws Exception {
        SendDePinRegister.run();
    }

}
