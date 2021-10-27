package com.gauss.common;

import com.alibaba.fastjson.JSONObject;
import com.gauss.crypto.encode.Bech32;
import com.gauss.msg.MsgSend;
import com.gauss.msg.utils.Message;
import org.apache.commons.codec.binary.Hex;
import org.spongycastle.util.encoders.Base64;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public abstract class EnvBase {
    private String RestServerUrl = "";

    public EnvBase() {
        EnvInstance.setEnv(this);
    }

    private String AddressToValidatorAddress(String address) {
        Bech32.Bech32Data data = Bech32.decode(address);
        return Bech32.encode(this.GetMainPrefix() + "valoper", data.getData());
    }

    public String GetMainPrefix() {
        return null;
    }

    public String GetDenom() {
        return null;
    }

    public String GetChainid() {
        return null;
    }

    public String GetRestServerUrl() {
        return this.RestServerUrl;
    }

    public void SetRestServerUrl(String url) {
        this.RestServerUrl = url;
    }

    public String GetHDPath() {
        return "M/44H/991H/0H/0/0";
    }

    public String GetValidatorAddrPrefix() {
        return String.format("%svaloper", GetMainPrefix());
    }

    public String GetTendermintConsensusPubkeyPrefix() {
        return String.format("%svalconspub", GetMainPrefix());
    }

    public String GetRestPathPrefix() {
        return "gauss";
    }

    public String GetDelegationsUrlPath(String address) {
        return this.GetRestPathPrefix() + "/staking/v1beta1/delegations/" + address;
    }


    /**
     * 验证人佣金URL
     */
    private String GetValidatorCommissionPath(String address) {
        String url;
        if (this.GetRestServerUrl().endsWith("/")) {
            url = this.GetRestServerUrl() + this.GetRestPathPrefix() + "/distribution/v1beta1/validators/" + address + "/commission";
        } else {
            url = this.GetRestServerUrl() + "/" + this.GetRestPathPrefix() + "/distribution/v1beta1/validators/" + address + "/commission";
        }
        return url;
    }

    /**
     * 查询委托人收益
     */
    private String GetDelegatorRewardsPath(String delegator_address) {
        String url;
        if (this.GetRestServerUrl().endsWith("/")) {
            url = this.GetRestServerUrl() + this.GetRestPathPrefix() + "/distribution/v1beta1/delegators/" + delegator_address + "/rewards";
        } else {
            url = this.GetRestServerUrl() + "/" + this.GetRestPathPrefix() + "/distribution/v1beta1/delegators/" + delegator_address + "/rewards";
        }
        return url;
    }

    private String GetLatestBlock() {
        String url;
        if (this.GetRestServerUrl().endsWith("/")) {
            url = this.GetRestServerUrl() + "blocks/latest";
        } else {
            url = this.GetRestServerUrl() + "/blocks/latest";
        }
        return url;
    }

    private String GetBlock(Long height) {
        String url;
        if (this.GetRestServerUrl().endsWith("/")) {
            url = this.GetRestServerUrl() + "blocks/" + height.toString();
        } else {
            url = this.GetRestServerUrl() + "/blocks/" + height.toString();
        }
        return url;
    }

    public boolean HasFee() {
        return true;
    }

    public String GetTxUrlPath() {
        return "/txs";
    }

    public String GetBalanceUrlPath(String address) {
        return "/" + this.GetRestPathPrefix() + "/bank/v1beta1/balances/" + address;
    }

    /**
     * 转账
     *
     * @privateKey 发送者的私钥
     * @toAddress 转入的地址
     * @amount 转入的数量
     * @feeAmount 旷工费
     * @memo 备注
     */
    public Map<String, String> transfer(String privateKey, String toAddress, double amount, String feeAmount, String memo) throws Exception {
        if (memo == null) {
            memo = "";
        }
        if (feeAmount == null) {
            feeAmount = "5000";
        }
        String gas = "200000";
        Map<String, String> map = new HashMap();

        MsgSend msg = new MsgSend();
        msg.setMsgType("cosmos-sdk/MsgSend");
        msg.init(privateKey);
        amount *= 1000000.0D;
        BigInteger bigInteger = BigInteger.valueOf((long) amount);
        Message messages = msg.produceSendMsg(EnvInstance.getEnv().GetDenom(), String.valueOf(bigInteger), toAddress);

        try {
            JSONObject jsonObject = msg.submit(messages, feeAmount, gas, memo);
            String txhash = (String) jsonObject.get("txhash");
            Integer code = (Integer) jsonObject.get("code");
            String raw_log;
            if (code != null) {
                raw_log = (String) jsonObject.get("raw_log");
                map.put("error", raw_log);
            } else if (txhash == null) {
                raw_log = (String) jsonObject.get("raw_log");
                map.put("error", raw_log);
            } else {
                map.put("txhash", txhash);
            }
        } catch (Exception var16) {
            map.put("error", var16.getMessage());
        }

        return map;
    }

    /**
     * 获取余额
     */
    public String getBalance(String address) {
        String url = this.GetRestServerUrl() + this.GetBalanceUrlPath(address);
        try {
            return HttpUtils.httpGet(url);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 查询最新高度
     */
    public String getLatestBlock() {
        String url = this.GetLatestBlock();
        try {
            return HttpUtils.httpGet(url);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 查询特定高度的区块信息
     */
    public String getBlock(Long height) {
        String url = this.GetBlock(height);
        try {
            return HttpUtils.httpGet(url);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取委托数量
     */
    public String getDelegations(String address) {
        String url = this.GetRestServerUrl() + this.GetDelegationsUrlPath(address);
        try {
            return HttpUtils.httpGet(url);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 查询验证人佣金
     */
    public String getValidatorCommission(String address) {
        String url = this.GetValidatorCommissionPath(this.AddressToValidatorAddress(address));
        try {
            return HttpUtils.httpGet(url);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 查询委托人奖励
     */
    public String getDelegatorRewards(String delegatorAddress) {
        String url = this.GetDelegatorRewardsPath(delegatorAddress);
        try {
            return HttpUtils.httpGet(url);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取hash信息
     */
    public String getHashInfo(String hash) {
        String url = this.GetTxsHashInfoURL(hash);
        try {
            return HttpUtils.httpGet(url);
        } catch (Exception e) {
            return null;
        }
    }

    private String GetTxsHashInfoURL(String hash) {
        String txHash = this.hash256(hash);
        return this.GetRestServerUrl() + "/txs/" + txHash;
    }

    private String hash256(String base64str) {
        byte[] bytes = Base64.decode(base64str);
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(bytes);
            return Hex.encodeHexString(messageDigest.digest()).toUpperCase();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
}
