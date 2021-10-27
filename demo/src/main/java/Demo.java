import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.ParserConfig;
import com.gauss.crypto.Crypto;
import com.gauss.util.AddressUtil;

import java.util.HashMap;
import java.util.Map;

public class Demo {
    public static void main(String[] args) {
        DEMOEnv env = new DEMOEnv();
        // 生成地址
//        createAddress(env);
//        // 转账
//        String privateKey = "a81784ae0577d50c93821c4c5e1d23e7608dec74c3d750a5024085f4e74194b0";
//        String toAddress = "igpc1ddyr92fhyz9r00un63jraaw90ykfts0rdz5u2a";
//        Double amount = 1.0D;
//        String memo = "test";
//        String feeAmount = "5000";
//        try {
//            Map<String, String> map = env.transfer(privateKey, toAddress, amount, feeAmount, memo);
//            if (map.get("error") == null) {
//                // 此处获取hash值
//                String txhash = map.get("txhash");
//                System.out.println("hash=" + txhash);
//            } else {
//                System.out.println(map.get("error"));
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        // 查询hash
//        String hash = "D8558CA252E8783F4119C561ABDAF7C9D018F3ED7A42A52D1B70D2ED74F9A074";
//        int code = getTxHashStatus(env, hash);
//        if (code == 1) {
//            System.out.println("成功");
//        } else if (code == -1) {
//            System.out.println("失败");
//        } else {
//            System.out.println("处理中，请稍后查询");
//        }
//        // 查询余额
//        System.out.println(getBalance(env, toAddress));
//        // 助记词
//        String mnemonic = generateMnemonic();
//        System.out.println(mnemonic);
//        // 助记词生成地址
//        System.out.println(generatePrivateKeyFromMnemonic(env, mnemonic));
//        // 委托数量
//        String address = "";
//        getDeletations(env, address);
//        // 最新区块高度信息
//        String latestHeightInfo = env.getLatestBlock();
//        System.out.println(latestHeightInfo);
        // 区块高度信息
//        String heightInfo = env.getBlock(10000L);
//        System.out.println(heightInfo);
        // System.out.println(env.getHashInfo("CpcBCpQBChwvY29zbW9zLmJhbmsudjFiZXRhMS5Nc2dTZW5kEnQKLGdhdXNzMXZhOTduZXU0ajBscHZ5cXZlbHZmdjM3YWs5czMyZHVmc3VhMzQ1EixnYXVzczE3MDltcGw1amZncTJhNzhjMmtla2VtNXgzZDducmZoMjNwZzR2ahoWCgZ1Z2F1c3MSDDI2MDExMzA5MjY0OBJpCk4KRgofL2Nvc21vcy5jcnlwdG8uc2VjcDI1NmsxLlB1YktleRIjCiECH/eUMvLZBL6QZFzWd1gU4VUaqgau0SzSIb+V+lzPGnQSBAoCCH8SFwoRCgZ1Z2F1c3MSBzkwMTAwMDAQ4MZbGkCfjQEVXVzXf9JgA5gYcEGck8GzLZ18z93i3V0dd/P+xwivhhLcQZ0vSxKgjZl7knQiTYq95xoh0dnIbm9ku6En"));
    }
    /**
     * 随机生成一个助记词
     * */
    private static String generateMnemonic() {
        return Crypto.generateMnemonic();
    }
    /**
     * 通过助记词产生地址
     * */
    private static String generatePrivateKeyFromMnemonic(DEMOEnv env, String mnemonic) {
        String privateKey = Crypto.generatePrivateKeyFromMnemonic(mnemonic);
        try {
            return AddressUtil.createNewAddressSecp256k1(
                    env.GetMainPrefix(),
                    Crypto.generatePubKeyFromPriv(privateKey));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    /**
     * 获取委托数量
     * */
    private static void getDeletations(DEMOEnv env, String address) {
        String jsonStr = env.getDelegations(address);
        System.out.println(jsonStr);
    }
    /**
     * 创建一个随机地址
     *
     * @return map
     */
    private static void createAddress(DEMOEnv env) {
        String privateKey = Crypto.generatePrivateKey();
        System.out.println(privateKey);
        try {
            String address = AddressUtil.createNewAddressSecp256k1(
                    env.GetMainPrefix(),
                    Crypto.generatePubKeyFromPriv(privateKey));
            System.out.println(address);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 通过hash查询处理结果
     * 0 成功
     * 1 处理中
     * -1 失败
     */
    private static int getTxHashStatus(DEMOEnv env, String txhash) {
        String hashUrl = env.GetRestServerUrl() + env.GetTxUrlPath() + "/" + txhash;
        try {
            String jsonStr = HttpUtil.get(hashUrl);
            ParserConfig.getGlobalInstance().setAutoTypeSupport(true);
            JSONObject jsonObject = JSON.parseObject(jsonStr);
            if (jsonObject.get("error") != null) {
                if (jsonStr.contains("tx (" + txhash + ") not found")) {
                    return 0;
                }
                return -1;
            }
            if (jsonObject.getInteger("code") != null && jsonObject.getInteger("code") > 0) {
                return -1;
            }
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * 获取余额
     * 返回过个币种余额
     * */
    private static Map<String, String> getBalance(DEMOEnv env, String address) {
        Map<String, String> balances = new HashMap<>();
        String jsonStr = env.getBalance(address);
        if (jsonStr != null) {
            ParserConfig.getGlobalInstance().setAutoTypeSupport(true);
            JSONObject jsonObject = JSON.parseObject(jsonStr);
            JSONArray jsonArray =  jsonObject.getJSONArray("balances");
            if (jsonArray == null) {
                return null;
            }
            System.out.println(jsonArray);
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject jsonObject1 = (JSONObject) jsonArray.get(i);
                balances.put(jsonObject1.getString("denom"), jsonObject1.getString("amount"));
            }
            return balances;
        }
        return null;
    }

}
