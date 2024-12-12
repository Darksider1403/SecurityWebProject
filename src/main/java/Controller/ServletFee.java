package Controller;

import Service.GHNApiUtil;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "ServletFee", value = "/fee")
public class ServletFee extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        String provinceIdParam = req.getParameter("ProvinceID");
        String districtIdParam = req.getParameter("DistrictID");
        String WardId = req.getParameter("WardId");
        int fee = 0;
        if (provinceIdParam != null && districtIdParam != null && WardId != null) {
            int provinceID = Integer.parseInt(provinceIdParam);
            int districtID = Integer.parseInt(districtIdParam);
            int wardId = Integer.parseInt(WardId);
            fee = (int) GHNApiUtil.totalFee(provinceID, districtID, wardId);
        }
        JSONObject jsonResponse = new JSONObject();
        jsonResponse.put("fee", fee);
        resp.getWriter().write(jsonResponse.toString());

    }
}
