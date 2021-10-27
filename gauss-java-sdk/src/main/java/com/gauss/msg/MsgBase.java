package com.gauss.msg;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.ParserConfig;
import com.gauss.common.Constants;
import com.gauss.common.EnvInstance;
import com.gauss.common.HttpUtils;
import com.gauss.common.Utils;
import com.gauss.crypto.Crypto;
import com.gauss.msg.utils.BoardcastTx;
import com.gauss.msg.utils.Data2Sign;
import com.gauss.msg.utils.Message;
import com.gauss.msg.utils.TxValue;
import com.gauss.types.Fee;
import com.gauss.types.Pubkey;
import com.gauss.types.Signature;
import com.gauss.types.Token;
import com.gauss.util.EncodeUtils;
import org.bouncycastle.util.Strings;
import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.encoders.Hex;

import java.util.ArrayList;
import java.util.List;

public class MsgBase {

    protected String restServerUrl = EnvInstance.getEnv().GetRestServerUrl();

    protected String sequenceNum;
    protected String accountNum;
    protected String pubKeyString;
    protected String address;
    protected String operAddress;
    protected String priKeyString;

    static protected String msgType;

    public void setMsgType(String type) {
        this.msgType = type;
    }

    static Signature sign(Data2Sign obj, String privateKey) throws Exception {
        String sigResult = null;
//        String sigResult = obj2byteok(obj, privateKey);
        sigResult = obj2byte(obj, privateKey);
        Signature signature = new Signature();
        Pubkey pubkey = new Pubkey();
        pubkey.setType("tendermint/PubKeySecp256k1");
        pubkey.setValue(Strings.fromByteArray(Base64.encode(Hex.decode(Crypto.generatePubKeyHexFromPriv(privateKey)))));
        signature.setPubkey(pubkey);
        signature.setSignature(sigResult);
        return signature;
    }



    static String obj2byte(Data2Sign data, String privateKey) {

        String sigResult = null;
        try {
            String signDataJson = Utils.serializer.toJson(data);
            //序列化
            byte[] byteSignData = signDataJson.getBytes();
            byte[] sig = Crypto.sign(byteSignData, privateKey);
            sigResult = Strings.fromByteArray(Base64.encode(sig));
        } catch (Exception e) {
            System.out.println("serialize msg failed");
        }
        return sigResult;
    }

    static String obj2byteok(Data2Sign data, String privateKey) {
        byte[] byteSignData = null;
        String sigResult = null;
        try {

            System.out.println("===============EncodeUtils=================");
            System.out.println("row data:");
            System.out.println(data);
            System.out.println("json data:");
            System.out.println(EncodeUtils.toJsonStringSortKeys(data));

            byte[] tmp = EncodeUtils.toJsonEncodeBytes(data);
            byteSignData = EncodeUtils.hexStringToByteArray(EncodeUtils.bytesToHex(tmp));

            System.out.println("byte data length:");
            System.out.println(byteSignData.length);

            byte[] sig = Crypto.sign(byteSignData, privateKey);
            sigResult = Strings.fromByteArray(Base64.encode(sig));

            System.out.println("result:");
            System.out.println(sigResult);
            System.out.println("================================");

        } catch (Exception e) {
            System.out.println("serialize msg failed");
        }

        return sigResult;
    }

    public JSONObject submit(Message message,
                       String feeAmount,
                       String gas,
                       String memo) {
        try {
            List<Token> amountList = new ArrayList<>();
            Token amount = new Token();
            amount.setDenom(EnvInstance.getEnv().GetDenom());
            amount.setAmount(feeAmount);
            amountList.add(amount);

            //组装待签名交易结构
            Fee fee = new Fee();
            fee.setAmount(amountList);
            fee.setGas(gas);


            Message[] msgs = new Message[1];
            msgs[0] = message;

            Data2Sign data = new Data2Sign(accountNum, EnvInstance.getEnv().GetChainid(), fee, memo, msgs, sequenceNum);

            Signature signature = MsgBase.sign(data, priKeyString);

            BoardcastTx cosmosTransaction = new BoardcastTx();
            cosmosTransaction.setMode("block");

            TxValue cosmosTx = new TxValue();
            cosmosTx.setType("auth/StdTx");
            cosmosTx.setMsgs(msgs);

            if (EnvInstance.getEnv().HasFee()) {
                cosmosTx.setFee(fee);
            }

            cosmosTx.setMemo(memo);

            List<Signature> signatureList = new ArrayList<>();
            signatureList.add(signature);
            cosmosTx.setSignatures(signatureList);

            cosmosTransaction.setTx(cosmosTx);

            return boardcast(cosmosTransaction.toJson());
        } catch (Exception e) {
            System.out.println("serialize transfer msg failed");
        }
        return null;
    }

    void initMnemonic(String mnemonic) {
        String prikey = Crypto.generatePrivateKeyFromMnemonic(mnemonic);
        init(prikey);
    }


    public void init(String privateKey) {
        pubKeyString = Hex.toHexString(Crypto.generatePubKeyFromPriv(privateKey));
        address = Crypto.generateAddressFromPriv(privateKey);
        ParserConfig.getGlobalInstance().setAutoTypeSupport(true);
        JSONObject accountJson = JSON.parseObject(getAccountPrivate(address));
        sequenceNum = getSequance(accountJson);
        accountNum = getAccountNumber(accountJson);
        priKeyString = privateKey;

        operAddress = Crypto.generateValidatorAddressFromPub(pubKeyString);
    }

    public String getOperAddress() {
        return operAddress;
    }

    private String getAccountPrivate(String userAddress) {
        if (!restServerUrl.endsWith("/")) {
            restServerUrl = restServerUrl + "/";
        }
        String url = restServerUrl + EnvInstance.getEnv().GetRestPathPrefix() + Constants.COSMOS_ACCOUNT_URL_PATH + userAddress;
        System.out.println(url);
        return HttpUtils.httpGet(url);
    }

    private String getSequance(JSONObject account) {
        String res = (String) account
                .getJSONObject("account")
                .get("sequence");
        return res;
    }

    private String getAccountNumber(JSONObject account) {
        String res = (String) account
                .getJSONObject("account")
                .get("account_number");
        return res;
    }

    protected JSONObject boardcast(String tx) {
        String res = HttpUtils.httpPost(restServerUrl + EnvInstance.getEnv().GetTxUrlPath(), tx);
        JSONObject result = JSON.parseObject(res);
        return result;
    }
}
