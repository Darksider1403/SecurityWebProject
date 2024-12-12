package Service;


import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class GHNApiUtil {

    private static final String API_URL = "https://online-gateway.ghn.vn/shiip/public-api";
    private static final String API_KEY = "bbce1485-31c7-11ef-8ba9-b6fbcb92e37e"; //bbce1485-31c7-11ef-8ba9-b6fbcb92e37e

    private static final int SHOP_ID = 5152283; //5152283, 885

    public static String getProvince() throws IOException {
        String url = API_URL + "/master-data/province";
        HttpGet request = new HttpGet(url);
        request.addHeader("Content-Type", "application/json");
        request.addHeader("Token", API_KEY);

        try (CloseableHttpClient httpClient = HttpClients.createDefault();
             CloseableHttpResponse response = httpClient.execute(request)) {
            HttpEntity entity = response.getEntity();
            return entity != null ? EntityUtils.toString(entity) : null;
        }
    }

    public static String getDistrict(int provinceId) throws IOException {
        String url = API_URL + "/master-data/district?province_id=" + provinceId;
        HttpGet request = new HttpGet(url);
        request.addHeader("Content-Type", "application/json");
        request.addHeader("Token", API_KEY);

        try (CloseableHttpClient httpClient = HttpClients.createDefault();
             CloseableHttpResponse response = httpClient.execute(request)) {
            HttpEntity entity = response.getEntity();
            return entity != null ? EntityUtils.toString(entity) : null;
        }
    }

    public static String getWard(int districtId) throws IOException {
        String url = API_URL + "/master-data/ward?district_id=" + districtId;
        HttpGet request = new HttpGet(url);
        request.addHeader("Content-Type", "application/json");
        request.addHeader("Token", API_KEY);

        try (CloseableHttpClient httpClient = HttpClients.createDefault();
             CloseableHttpResponse response = httpClient.execute(request)) {
            HttpEntity entity = response.getEntity();
            return entity != null ? EntityUtils.toString(entity) : null;
        }
    }

    public static String calculateShippingFee(JsonObject feeData) throws IOException {
        String url = API_URL + "/v2/shipping-order/fee";
        HttpPost request = new HttpPost(url);
        request.addHeader("Content-Type", "application/json");
        request.addHeader("Token", API_KEY);

        feeData.addProperty("shop_id", SHOP_ID);

        StringEntity entity = new StringEntity(feeData.toString());
        request.setEntity(entity);

        try (CloseableHttpClient httpClient = HttpClients.createDefault();
             CloseableHttpResponse response = httpClient.execute(request)) {
            HttpEntity responseEntity = response.getEntity();
            return responseEntity != null ? EntityUtils.toString(responseEntity) : null;
        }
    }

    public static String createOrder(JsonObject orderData) throws IOException {
        String url = API_URL + "/v2/shipping-order/create";
        HttpPost request = new HttpPost(url);
        request.addHeader("Content-Type", "application/json");
        request.addHeader("Token", API_KEY);

        StringEntity entity = new StringEntity(orderData.toString());
        request.setEntity(entity);

        try (CloseableHttpClient httpClient = HttpClients.createDefault();
             CloseableHttpResponse response = httpClient.execute(request)) {
            HttpEntity responseEntity = response.getEntity();
            return responseEntity != null ? EntityUtils.toString(responseEntity) : null;
        }
    }

    public static int findProvinceIdByName(String provinceName) throws IOException {
        String provincesJson = getProvince();
        JsonArray provinces = JsonParser.parseString(provincesJson).getAsJsonObject().getAsJsonArray("data");
        for (int i = 0; i < provinces.size(); i++) {
            JsonObject province = provinces.get(i).getAsJsonObject();
            if (province.get("ProvinceName").getAsString().equalsIgnoreCase(provinceName)) {
                return province.get("ProvinceID").getAsInt();
            }
        }
        return -1;
    }

    public static int findDistrictIdByName(int provinceId, String districtName) throws IOException {
        String districtsJson = getDistrict(provinceId);
        JsonArray districts = JsonParser.parseString(districtsJson).getAsJsonObject().getAsJsonArray("data");
        for (int i = 0; i < districts.size(); i++) {
            JsonObject district = districts.get(i).getAsJsonObject();
            if (district.get("DistrictName").getAsString().equalsIgnoreCase(districtName)) {
                return district.get("DistrictID").getAsInt();
            }
        }
        return -1;
    }

    public static int findWardIdByName(int districtId, String wardName) throws IOException {
        String wardsJson = getWard(districtId);
        JsonArray wards = JsonParser.parseString(wardsJson).getAsJsonObject().getAsJsonArray("data");
        for (int i = 0; i < wards.size(); i++) {
            JsonObject ward = wards.get(i).getAsJsonObject();
            if (ward.get("WardName").getAsString().equalsIgnoreCase(wardName)) {
                return ward.get("WardCode").getAsInt();
            }
        }
        return -1;
    }

    public static double totalFee(int provinceId, int districtId, int wardId) {

        try {
            // Lấy mã tỉnh
            if (provinceId == -1) {
                System.out.println("Không tìm thấy tỉnh: " );
                return -1;
            }

            // Lấy mã huyện
            if (districtId == -1) {
                System.out.println("Không tìm thấy huyện: ");
                return -1;
            }

            // Lấy mã xã
            if (wardId == -1) {
                System.out.println("Không tìm thấy xã: ");
                return -1;
            }

            // Tạo JsonObject chứa dữ liệu cần thiết cho việc tính phí vận chuyển
            JsonObject feeData = new JsonObject();
            feeData.addProperty("service_type_id", 2);
            feeData.addProperty("insurance_value", 10000);
            feeData.addProperty("to_ward_code", String.valueOf(wardId));
            feeData.addProperty("to_district_id", districtId);
            feeData.addProperty("weight", 3000);
            feeData.addProperty("height", 20);
            feeData.addProperty("length", 30);
            feeData.addProperty("width", 40);

            // Tính phí vận chuyển
            String shippingFeeJson = GHNApiUtil.calculateShippingFee(feeData);

            // Parse JSON để lấy giá trị phí vận chuyển
            JsonObject shippingFeeObject = JsonParser.parseString(shippingFeeJson).getAsJsonObject();
            if (shippingFeeObject.has("code") && shippingFeeObject.get("code").getAsInt() == 200) {
                JsonObject data = shippingFeeObject.getAsJsonObject("data");
                if (data.has("total")) {
                    double totalFee = data.get("total").getAsDouble();
                    return totalFee;
                } else {
                    System.out.println("Không tìm thấy giá trị phí vận chuyển trong dữ liệu phản hồi từ API.");
                }
            } else {
                System.out.println("Lỗi khi gọi API tính phí vận chuyển.");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return -1;
    }

}
