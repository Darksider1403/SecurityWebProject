package Controller;

import Model.Account;
import Service.ContractService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "ServletContract", value = "/contract")
public class ServletContract extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String fullName = request.getParameter("fullName");
        String phone = request.getParameter("phone");
        String email = request.getParameter("email");
        String message = request.getParameter("message");
        int accountType ;
        HttpSession session = request.getSession();
        Account account = (Account) session.getAttribute("account");
        if(account!=null){
            accountType=1;
        }
        else{
            accountType=0;
        }
        ContractService cs = ContractService.getInstance();
        cs.createContract(fullName,phone,email,message,accountType);
        response.sendRedirect("Home.jsp");
    }
}
