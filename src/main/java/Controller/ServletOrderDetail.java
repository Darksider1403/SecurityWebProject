package Controller;

import Service.GHNApiUtil;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "ServletOrderDetail", value = "/order")
public class ServletOrderDetail extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JSONArray provinceArray = new JSONObject(GHNApiUtil.getProvince()).getJSONArray("data");
        req.setAttribute("provinces", provinceArray);
        String provinceIdParam = req.getParameter("ProvinceID");
        String districtIdParam = req.getParameter("DistrictID");
        String WardId = req.getParameter("WardId");
        int fee = 0;
        if (provinceIdParam != null && districtIdParam != null && WardId != null) {
            int provinceID = Integer.parseInt(provinceIdParam);
            int districtID = Integer.parseInt(districtIdParam);
            int wardId = Integer.parseInt(WardId);
            fee = (int)GHNApiUtil.totalFee(provinceID, districtID, wardId);
        }

        req.setAttribute("fee", fee);
        req.getRequestDispatcher("order.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html; charset=UTF-8");
        String provinceIdParam = req.getParameter("ProvinceID");
        String districtIdParam = req.getParameter("DistrictID");
        String WardId = req.getParameter("WardId");
        StringBuilder responseHtml = new StringBuilder();

        if (provinceIdParam != null) {
            int provinceID = Integer.parseInt(provinceIdParam);
            JSONArray districtArray = new JSONObject(GHNApiUtil.getDistrict(provinceID)).getJSONArray("data");

            responseHtml.append("<select id='districtSelect' onchange='changeDistrict()' name='district'>");
            for (int i = 0; i < districtArray.length(); i++) {
                JSONObject district = districtArray.getJSONObject(i);
                String districtName = district.getString("DistrictName");
                int districtID = district.getInt("DistrictID");
                responseHtml.append("<option value='").append(districtID).append("'>").append(districtName).append("</option>");
            }
            responseHtml.append("</select>");
        }

        if (districtIdParam != null) {
            int districtID = Integer.parseInt(districtIdParam);
            JSONArray wardArray = new JSONObject(GHNApiUtil.getWard(districtID)).getJSONArray("data");
            responseHtml.append("<select id='wardSelect' onchange='checkSelection()' name='ward'>");
            for (int i = 0; i < wardArray.length(); i++) {
                JSONObject ward = wardArray.getJSONObject(i);
                String wardName = ward.getString("WardName");
                int wardID = ward.getInt("WardCode");
                responseHtml.append("<option value='").append(wardID).append("'>").append(wardName).append("</option>");
            }
            responseHtml.append("</select>");
        }
        resp.getWriter().write(responseHtml.toString());
    }

}
