package gather.tools;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.nachain.core.config.ChainConfig;
import org.nachain.core.util.JsonUtils;
import org.nachain.miner.launcher.webserver.ApiResult;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.math.BigInteger;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;


@Slf4j
public class RemoteBroadcast {

    public static boolean broadcastMail(long instanceId, String mailJson) {
        boolean flag = false;
        String url = ChainConfig.NODE_GATEWAY_DOMAIN + "/broadcast/mail/tx/v2";
        try {
            Connection.Response executeResult = post(url)
                    .data("mail", mailJson)
                    .data("instance", String.valueOf(instanceId))
                    .execute();
            String resultJson = executeResult.body();
            flag = resultJson.contains("true");
        } catch (Exception e) {
            log.error("broadcast error:" + url, e);
        }

        return flag;
    }


    public static BigInteger getDPoSGas() {
        String url = ChainConfig.NODE_GATEWAY_DOMAIN + "/api/v2/mergeApi?module=tx&method=getNacGasAmountByDPoS";

        try {
            Connection.Response executeResult = get(url).execute();
            String resultJson = executeResult.body();
            ApiResult apiResult = JsonUtils.jsonToPojo(resultJson, ApiResult.class);

            return new BigInteger(apiResult.getData().toString());
        } catch (Exception e) {
            log.error("getDPoSGas error:", e);
        }

        return BigInteger.ZERO;
    }

    public static BigInteger getGas(long instanceId) {
        String url = ChainConfig.NODE_GATEWAY_DOMAIN + "/api/v2/mergeApi?module=tx&method=getGas&instance=" + instanceId;

        try {
            Connection.Response executeResult = get(url).execute();
            String resultJson = executeResult.body();
            ApiResult apiResult = JsonUtils.jsonToPojo(resultJson, ApiResult.class);

            return new BigInteger(apiResult.getData().toString());
        } catch (Exception e) {
            log.error("getGas error:", e);
        }

        return BigInteger.ZERO;
    }

    public static Connection post(String url) throws Exception {
        return Jsoup.connect(url).sslSocketFactory(factory)
                .maxBodySize(0)
                .ignoreHttpErrors(true).followRedirects(true)
                .ignoreContentType(true)
                .method(Connection.Method.POST).timeout(30 * 1000);
    }

    public static Connection get(String url) throws Exception {
        return Jsoup.connect(url).sslSocketFactory(factory)
                .maxBodySize(0)
                .ignoreHttpErrors(true).followRedirects(true)
                .ignoreContentType(true)
                .method(Connection.Method.GET).timeout(120 * 1000);
    }


    private static SSLSocketFactory factory;

    static {
        factory = socketFactory();
    }

    static public SSLSocketFactory socketFactory() {
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }

            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }

            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }
        }};

        try {
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new SecureRandom());
            SSLSocketFactory result = sslContext.getSocketFactory();
            return result;
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            throw new RuntimeException("Failed to create a SSL socket factory", e);
        }
    }


}
