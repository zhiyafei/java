package zxkj.zhixing.utils;

import com.qiniu.api.auth.DigestAuthClient;
import com.qiniu.api.auth.digest.Mac;
import com.qiniu.api.config.Config;
import com.qiniu.api.net.CallRet;
import com.qiniu.api.net.Client;
import com.qiniu.api.net.EncodeUtils;
import com.qiniu.api.rs.PutPolicy;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;


@Service
public class QiNiuUpload {
    private static final Logger logger = LoggerFactory.getLogger(QiNiuUpload.class);

    private static final String QiNiuDoMain = "http://static.jinrongbaguanv.com/";

    private static String qiNiuToken = "";

    private static final com.qiniu.http.Client NEW_QINIU_CLIENT = new com.qiniu.http.Client();


    /**
     * 初始化
     */
    static{

        Config.ACCESS_KEY = "9nIl-BWJVEk7UY75O31EZozxV9APYg23ZmJvyIZW";
        Config.SECRET_KEY = "IyHm7_Sbt9nHZ_10D-pNF8_pgGrQGOa6zdMJ8o5n";
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(new Runnable() {
            public void run() {
                qiNiuToken = getUploadTokenFromQiniu();
            }
        }, 0L, 6000L, TimeUnit.SECONDS);
    }

    /**
     * 上传数据
     *
     * @param key   上传数据保存的文件名
     * @param data  上传的数据
     */
    public static Map<String,Object> upload(String key, byte[] data){
        try {
            Map<String, Object> resultMap = new HashMap<String, Object>();
            //密钥配置
            Auth auth = Auth.create(Config.ACCESS_KEY, Config.SECRET_KEY);
            String uptoken = auth.uploadToken("jinba", key);
            //创建上传对象
            UploadManager uploadManager = new UploadManager();
            //调用put方法上传，这里指定的key和上传策略中的key要一致
            Response res = uploadManager.put(data, key, uptoken);
            logger.debug("upload res:{}", res.bodyString());
            int statusCode = res.statusCode;
            //上传成功
            if(statusCode == 200){
                resultMap.put("statusCode",200);
                resultMap.put("url", QiNiuDoMain + key);
            }else{
                resultMap.put("statusCode",statusCode);
            }
            return resultMap;
        } catch (Exception e) {
            logger.error("upload failed.", e);
            return null;
        }
    }


    /**
     * 从远程url 获取资源 上传到七牛
     * @param sourceUrl
     * @return
     */
    public static String fetch(String sourceUrl){
        try {
            String to = genQiNiuFileName();
            String toQiniu = "jinba:headimg/" + to;
            String encodeFrom = EncodeU.urlsafeEncode(sourceUrl);
            String encodeTo = EncodeUtils.urlsafeEncode(toQiniu);
            String url = "http://iovip.qbox.me/fetch/" + encodeFrom + "/to/" + encodeTo;
            Mac mac = new com.qiniu.api.auth.digest.Mac(Config.ACCESS_KEY, Config.SECRET_KEY);
            Client client = new DigestAuthClient(mac);
            CallRet ret = client.call(url);
            return QiNiuDoMain + "headimg/" + to;
        }catch (Exception e){
            return sourceUrl;
        }
    }


    private static String genQiNiuFileName(){
        return  System.currentTimeMillis()   + ".png";
    }


    public static String getToken(){
        if(StringUtils.isBlank(qiNiuToken)){
            qiNiuToken = getUploadTokenFromQiniu();
        }
        return qiNiuToken;
    }

    public static Map<Object, Object> getTokenMap() {
        Map<Object, Object> tokenMap = new HashMap<Object, Object>();
        tokenMap.put("uptoken", getToken());
        tokenMap.put("status",200);
        return tokenMap;
    }


    /**
     * 获取上传文件uptoken
     * @return
     */
    public static String getUploadTokenFromQiniu() {
        Mac mac = new Mac(Config.ACCESS_KEY, Config.SECRET_KEY);
        PutPolicy putPolicy = new PutPolicy("jinba");
        //两小时,默认一小时
        putPolicy.expires = 7200;
        String uptoken = "";
        try {
            uptoken = putPolicy.token(mac);
        } catch (Exception e) {
            logger.error("token failed.", e);
            return null;
        }
        return uptoken;
    }

    /**
     * 从远程url 获取资源 上传到七牛
     * @param sourceUrl - 原图片URL
     * @param toKey - 要转存成的key名字
     * @return
     */
    public static String uploadByImageUrl(String sourceUrl, String toKey){
        Config.ACCESS_KEY = "9nIl-BWJVEk7UY75O31EZozxV9APYg23ZmJvyIZW";
        Config.SECRET_KEY = "IyHm7_Sbt9nHZ_10D-pNF8_pgGrQGOa6zdMJ8o5n";
        try {
            // 格式: ${bucketName}:${qiniuFileName}
            String toQiniu = String.format("%s:%s", "jinba", toKey);
            String encodeFrom = EncodeUtils.urlsafeEncode(sourceUrl);
            String encodeTo = EncodeUtils.urlsafeEncode(toQiniu);
            String url = "http://iovip.qbox.me/fetch/" + encodeFrom + "/to/" + encodeTo;
            Mac mac = new com.qiniu.api.auth.digest.Mac(Config.ACCESS_KEY, Config.SECRET_KEY);
            Client client = new DigestAuthClient(mac);
            client.call(url);
            return QiNiuDoMain + toKey;
        } catch (Exception e) {
            return sourceUrl;
        }
    }

    /**
     * 给图片加上水印
     * @param originalImageUrl - 原图，URL后不能带参数
     * @param watermarkImageUrl - 水印图片URL
     * @param dissolve - 透明度，取值范围1-100，默认值为50
     * @param gravity - 水印位置(http://developer.qiniu.com/code/v6/api/kodo-api/image/watermark.html#watermark-anchor-spec), 默认为South(右下角)
     * @param dx - 横轴边距，默认为20px
     * @param dy - 纵轴边距，默认为20px
     * @return
     */
    public static String putWatermark(String originalImageUrl, String watermarkImageUrl, Integer dissolve, String gravity, Integer dx, Integer dy) {
        String watermarkParams = "?watermark/1/image/%s/dissolve/%d/gravity/%s/dx/%d/dy/%d";
        if (dissolve == null) {
            dissolve = 50;
        }
        if (gravity == null) {
            gravity = "South";
        }
        if (dx == null) {
            dx = 20;
        }
        if (dy == null) {
            dy = 20;
        }
        watermarkParams = String.format(watermarkParams, Base64.encodeBase64String(watermarkImageUrl.getBytes()), dissolve, gravity, dx, dy);
        return originalImageUrl + watermarkParams;
    }

    /**
     * 获取七牛原图的压缩图URL，先进行大小缩放，再进行质量压缩
     * @param originalImageUrl - 七牛原图URL
     * @param doStrip - 是否去除视频元信息
     * @param width - 指定目标图片宽度，高度等比缩放。取值范围0-10000
     * @param quality - 图片质量，取值范围1-100，缺省为85如原图质量小于指定质量，则使用原图质量
     * @return
     */
    public static String getCompressedImageUrl(String originalImageUrl, boolean doStrip, Integer width, Integer quality) {
        if (width != null && (width <= 0 || width > 10000)) {
            throw new RuntimeException("invalid width:" + width);
        }
        if (quality != null && (quality <= 0 || quality > 100)) {
            throw new RuntimeException("invalid quality:" + quality);
        }
        if (width == null) {
            width = 750;
        }
        if (quality == null) {
            quality = 50;
        }
        StringBuilder thumbnailImgParamFormatSb = new StringBuilder("?imageMogr2/thumbnail/%dx");
        if (doStrip) {
            thumbnailImgParamFormatSb.append("/strip");
        }
        thumbnailImgParamFormatSb.append("/quality/%d!");
        ImageInfo imageInfo = getImageInfo(originalImageUrl);
        if (imageInfo == null) {
            logger.error("getCompressedImageUrl failed. originalImageUrl:{}", originalImageUrl);
            return originalImageUrl;
        }
        if (imageInfo.getWidth() < width) {
            width = imageInfo.getWidth();
        }
        String paramStr = String.format(thumbnailImgParamFormatSb.toString(), width, quality);
        return originalImageUrl + paramStr;
    }


    /**
     * 获取图片基本信息
     * @param imageUrl
     * @return
     */
    public static ImageInfo getImageInfo(String imageUrl) {
        String imageInfoUrl = imageUrl + "?imageInfo";
        try {
            Response response = NEW_QINIU_CLIENT.get(imageInfoUrl);
            if (response.statusCode != 200) {
                logger.error("getImageInfo failed. imageUrl:{}, statusCode:{}, response:{}", imageInfoUrl, response.statusCode, response.bodyString());
                return null;
            }
            return response.jsonToObject(ImageInfo.class);
        } catch (QiniuException e) {
            logger.error("getImageInfo failed. imageUrl:{}", imageInfoUrl, e);
        }
        return null;
    }

    /**
     * 七牛图片基本信息
     * @author panpanxu
     */
    public static class ImageInfo {
        private String format;
        private int width;
        private int height;
        private String colorModel;
        public String getFormat() {
            return format;
        }
        public void setFormat(String format) {
            this.format = format;
        }
        public int getWidth() {
            return width;
        }
        public void setWidth(int width) {
            this.width = width;
        }
        public int getHeight() {
            return height;
        }
        public void setHeight(int height) {
            this.height = height;
        }
        public String getColorModel() {
            return colorModel;
        }
        public void setColorModel(String colorModel) {
            this.colorModel = colorModel;
        }
    }

    /**
     * 获取随机七牛key名称
     * @return
     */
    public static String getRandomQiniuKeyName() {
        return System.currentTimeMillis() + RandomStringUtils.randomNumeric(5);
    }
}