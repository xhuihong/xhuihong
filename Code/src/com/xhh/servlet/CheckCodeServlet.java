package com.xhh.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.xhh.utils.CodeConfig;

import redis.clients.jedis.Jedis;

@WebServlet("/CheckCodeServlet")
public class CheckCodeServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public CheckCodeServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String phoneNo = request.getParameter("phone_no");
		String inputCode = request.getParameter("verify_code");
		if (phoneNo == null || inputCode == null) {
			return;
		}
		//拼接redis中获取验证码
		String codeKey=CodeConfig.PHONE_PREFIX+phoneNo+CodeConfig.PHONE_SUFFIX;
		//创建jedis对象
		Jedis jedis = new Jedis(CodeConfig.HOST, CodeConfig.PORT);
		String redisCode = jedis.get(codeKey);
		if(inputCode.equals(redisCode)) {
			//将redis中的验证码删除
			jedis.del(codeKey);
			response.getWriter().write("true");
		}
		jedis.close();
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

}
