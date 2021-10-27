# gauss-java-sdk

Java SDK for gauss

### 使用说明
#### 安装到maven仓库
```
mvn install:install-file -DgroupId=com.gauss -DartifactId=gauss-java-sdk -Dversion=0.0.6 -Dpackaging=jar -Dfile=gauss-java-sdk-0.0.6.jar
```
#### pom.xml中引用
```
<dependency>
	<groupId>com.gauss</groupId>
	<artifactId>gauss-java-sdk</artifactId>
	<version>0.0.6</version>
</dependency>
```
## 使用（请参考demo）
### 创建文件继承EnvBase类(DEMOEnv.java)
```

public class DEMOEnv extends EnvBase {

    private static String REST_URL = "http://192.168.2.23:9317";
    public DEMOEnv() {
        super();
        this.SetRestServerUrl(REST_URL);
    }
    @Override
    public String GetMainPrefix() {
        return "igpc";
    }
    @Override
    public String GetDenom() {
        return "uigpc";
    }
    @Override
    public String GetChainid() {
        return "igpc";
    }
}
```
### 1.查询余额(参考Demo.java)
```
DEMOEnv env = new DEMOEnv();
// address为钱包地址,返回值为json字符串
String result = env.getBalance(address);
```
### 2.转账(参考Demo.java)
```
// privateKey 为发送地址私钥
// toAddress为接收地址
// amount为数量
// feeAmount 为手续费
// memo为备注
DEMOEnv env = new DEMOEnv();
Map<String, String> map = env.transfer(privateKey, toAddress, amount, feeAmount, memo);
```
### 3.创建地址(参考Demo.java)
```
DEMOEnv env = new DEMOEnv();
createAddress(env)
```
### 4.查询结果(参考Demo.java)
```
// txhash为哈希
getTxHashStatus(txhash)
```
### 5.查询委托人奖励
```
String address;
getDelegatorRewards(address);
```
### 6.查询验证人佣金
```
String address;
getValidatorCommission(address);

```
### 7.查询最新高度
```
getLatestBlock()
```
### 8.查询指定高度的区块信息
```
Long height = 100L;
getBlock(height);
```
### 9.获取交易信息
```
String hash="CpcBCpQBChwvY29zbW9zLmJhbmsudjFiZXRhMS5Nc2dTZW5kEnQKLGdhdXNzMXZhOTduZXU0ajBscHZ5cXZlbHZmdjM3YWs5czMyZHVmc3VhMzQ1EixnYXVzczE3MDltcGw1amZncTJhNzhjMmtla2VtNXgzZDducmZoMjNwZzR2ahoWCgZ1Z2F1c3MSDDI2MDExMzA5MjY0OBJpCk4KRgofL2Nvc21vcy5jcnlwdG8uc2VjcDI1NmsxLlB1YktleRIjCiECH/eUMvLZBL6QZFzWd1gU4VUaqgau0SzSIb+V+lzPGnQSBAoCCH8SFwoRCgZ1Z2F1c3MSBzkwMTAwMDAQ4MZbGkCfjQEVXVzXf9JgA5gYcEGck8GzLZ18z93i3V0dd/P+xwivhhLcQZ0vSxKgjZl7knQiTYq95xoh0dnIbm9ku6En";
getHashInfo(hash);
```
