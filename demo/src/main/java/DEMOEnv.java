import com.gauss.common.EnvBase;

public class DEMOEnv extends EnvBase {

    private static String REST_URL = "http://192.168.2.23:9317";
    public DEMOEnv() {
        super();
        this.SetRestServerUrl(REST_URL);
    }
    @Override
    public String GetMainPrefix() {
        return "demo";
    }
    @Override
    public String GetDenom() {
        return "udemo";
    }
    @Override
    public String GetChainid() {
        return "demo";
    }
    @Override
    public String GetHDPath() {
        return "M/44H/991H/0H/0/0";
    }
}
